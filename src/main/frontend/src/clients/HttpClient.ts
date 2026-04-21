import Env from '../Env';
import type { ApiError } from '../models/ApiError.ts';
import { ErrorLogService } from '../services/ErrorLogService.ts';
import { NotificationsSanjy } from '../services/NotificationsSanjy.ts';
import { UuidService } from '../services/UuidService.ts';

export interface RequestOptions {
  headers?: Record<string, string>;
  params?: Record<string, unknown>;
  timeout?: number;
  signal?: AbortSignal;
}

export class HttpError extends Error {
  status: number;
  statusText: string;
  url: string;
  method: string;
  body: unknown;

  constructor(
    message: string,
    status: number,
    statusText: string,
    url: string,
    method: string,
    body: unknown
  ) {
    super(message);
    this.name = 'HttpError';
    this.status = status;
    this.statusText = statusText;
    this.url = url;
    this.method = method;
    this.body = body;
  }
}

const DEFAULT_TIMEOUT_MS = 30_000;

function isApiError(v: unknown): v is ApiError {
  return typeof v === 'object' && v !== null && 'userCode' in v && 'userMessage' in v;
}

function buildUrl(path: string, params?: Record<string, unknown>): string {
  const url = `${Env.API_BASE_URL}${path}`;
  if (!params) {
    return url;
  }
  const search = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== null) {
      search.set(key, String(value));
    }
  }
  const query = search.toString();
  return query ? `${url}?${query}` : url;
}

function buildHeaders(correlationId: string, options?: RequestOptions): Record<string, string> {
  return {
    'X-Correlation-ID': correlationId,
    ...options?.headers,
  };
}

function buildBody(body?: unknown): { body?: BodyInit; contentType?: string } {
  if (body === undefined || body === null) {
    return {};
  }
  if (body instanceof FormData)
    return {
      body,
    };
  return {
    body: JSON.stringify(body),
    contentType: 'application/json',
  };
}

async function parseErrorResponseBody(response: Response): Promise<unknown> {
  try {
    return await response.json();
  } catch {
    return response.text().catch(() => null);
  }
}

function handleError(error: HttpError): never {
  const message = isApiError(error.body) ? error.body.userMessage : error.message;

  ErrorLogService.logApiError(message, {
    url: error.url,
    method: error.method,
    status: error.status,
    statusText: error.statusText,
    responseData: error.body,
  });

  if (isApiError(error.body)) {
    NotificationsSanjy.error(
      'Backend Communication Error',
      `Error code: ${error.body.userCode} | ${error.body.userMessage}`
    );
  } else if (error.message) {
    NotificationsSanjy.error('Request Error', error.message);
  } else {
    NotificationsSanjy.error('Unexpected Error', 'An unexpected error occurred. Please try again.');
  }

  throw error;
}

async function request<T>(
  method: string,
  path: string,
  body?: unknown,
  options?: RequestOptions
): Promise<T> {
  const url = buildUrl(path, options?.params);
  const correlationId = UuidService.randomUUID();
  const { body: serializedBody, contentType } = buildBody(body);
  const headers = buildHeaders(correlationId, options);

  if (contentType) {
    headers['Content-Type'] = contentType;
  }

  const timeout = options?.timeout ?? DEFAULT_TIMEOUT_MS;
  const timeoutController = new AbortController();
  const timeoutId = setTimeout(() => timeoutController.abort(), timeout);

  const signal = options?.signal
    ? AbortSignal.any([timeoutController.signal, options.signal])
    : timeoutController.signal;

  try {
    const response = await fetch(url, { method, headers, body: serializedBody, signal });
    clearTimeout(timeoutId);

    if (response.status === 204) return undefined as T;

    if (!response.ok) {
      const responseBody = await parseErrorResponseBody(response);
      handleError(
        new HttpError(
          `HTTP ${response.status} ${response.statusText}`,
          response.status,
          response.statusText,
          url,
          method,
          responseBody
        )
      );
    }

    return (await response.json()) as T;
  } catch (error) {
    clearTimeout(timeoutId);
    if (error instanceof HttpError) {
      throw error;
    }

    if (error instanceof DOMException && error.name === 'AbortError') {
      if (options?.signal?.aborted) {
        throw error;
      }
      handleError(new HttpError('Request timed out', 408, 'Request Timeout', url, method, null));
    }

    handleError(
      new HttpError(
        error instanceof Error ? error.message : 'Network error',
        0,
        'Network Error',
        url,
        method,
        null
      )
    );
  }
}

function get<T>(path: string, options?: RequestOptions): Promise<T> {
  return request<T>('GET', path, undefined, options);
}

function post<T>(path: string, body?: unknown, options?: RequestOptions): Promise<T> {
  return request<T>('POST', path, body, options);
}

function put<T>(path: string, body?: unknown, options?: RequestOptions): Promise<T> {
  return request<T>('PUT', path, body, options);
}

function del<T>(path: string, options?: RequestOptions): Promise<T> {
  return request<T>('DELETE', path, undefined, options);
}

export const HttpClient = { get, post, put, delete: del };
