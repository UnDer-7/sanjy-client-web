import { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';
import { LoadingOverlay } from '@mantine/core';

interface LoadingContextType {
  isLoading: boolean;
  showLoading: () => void;
  hideLoading: () => void;
}

const LoadingContext = createContext<LoadingContextType | undefined>(undefined);

export function LoadingProvider({ children }: { children: ReactNode }) {
  const [isLoading, setIsLoading] = useState(false);

  const showLoading = () => setIsLoading(true);
  const hideLoading = () => setIsLoading(false);

  return (
    <LoadingContext.Provider value={{ isLoading, showLoading, hideLoading }}>
      <div style={{ position: 'relative', minHeight: '100vh' }}>
        <LoadingOverlay
          visible={isLoading}
          zIndex={9999}
          overlayProps={{ radius: 'sm', blur: 2 }}
        />
        {children}
      </div>
    </LoadingContext.Provider>
  );
}

export function useLoading() {
  const context = useContext(LoadingContext);
  if (context === undefined) {
    throw new Error('useLoading must be used within a LoadingProvider');
  }
  return context;
}
