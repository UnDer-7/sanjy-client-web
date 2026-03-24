import axios, { AxiosError } from 'axios';
import Env from '../Env';
import { UuidService } from '../services/UuidService.ts';
import { NotificationsSanjy } from '../services/NotificationsSanjy.ts';
import type { ApiError } from '../models/ApiError.ts';
import { ErrorLogService } from '../services/ErrorLogService.ts';

export const HttpClient = axios.create({
  baseURL: `${Env.API_BASE_URL}`,
  timeout: 30000,
  headers: {
    'X-Correlation-ID': UuidService.randomUUID(),
  },
});

HttpClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    const apiError = error.response?.data;

    // Log error to localStorage
    const errorMessage = apiError?.userMessage || error.message || 'Unknown error';
    const errorDetail = {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      statusText: error.response?.statusText,
      responseData: apiError,
    };
    ErrorLogService.logApiError(errorMessage, errorDetail);

    if (apiError?.userCode && apiError?.userMessage) {
      NotificationsSanjy.error(
        'Backend Communication Error',
        `Error code: ${apiError.userCode} | ${apiError.userMessage}`
      );
    } else if (error.message) {
      NotificationsSanjy.error('Request Error', error.message);
    } else {
      NotificationsSanjy.error(
        'Unexpected Error',
        'An unexpected error occurred. Please try again.'
      );
    }

    return Promise.reject(error);
  }
);
