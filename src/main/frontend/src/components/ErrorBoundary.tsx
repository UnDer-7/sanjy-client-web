import { Component, type ErrorInfo, type ReactNode } from 'react';
import { logReactError, logJsError, logUnhandledRejection } from '../services/ErrorLogService';

interface ErrorBoundaryProps {
  children: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(): ErrorBoundaryState {
    return { hasError: true };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    logReactError(error, errorInfo.componentStack || '');
  }

  componentDidMount(): void {
    globalThis.addEventListener('error', this.handleGlobalError);
    globalThis.addEventListener('unhandledrejection', this.handleUnhandledRejection);
  }

  componentWillUnmount(): void {
    globalThis.removeEventListener('error', this.handleGlobalError);
    globalThis.removeEventListener('unhandledrejection', this.handleUnhandledRejection);
  }

  handleGlobalError = (event: ErrorEvent): void => {
    if (event.error instanceof Error) {
      logJsError(event.error);
    } else {
      logJsError(new Error(event.message));
    }
  };

  handleUnhandledRejection = (event: PromiseRejectionEvent): void => {
    logUnhandledRejection(event.reason);
  };

  render(): ReactNode {
    if (this.state.hasError) {
      return (
        <div
          style={{
            padding: '20px',
            textAlign: 'center',
            fontFamily: 'system-ui, sans-serif',
          }}
        >
          <h1>Something went wrong</h1>
          <p>An unexpected error occurred. Please refresh the page.</p>
          <button
            onClick={() => globalThis.location.reload()}
            style={{
              padding: '10px 20px',
              fontSize: '16px',
              cursor: 'pointer',
            }}
          >
            Refresh Page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
