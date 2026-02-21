import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { LoadingOverlay } from '@mantine/core';

interface LoadingContextType {
  isLoadingGlobal: boolean;
  showLoadingGlobal: () => void;
  hideLoadingGlobal: () => void;
}

const LoadingContext = createContext<LoadingContextType | undefined>(undefined);

export function LoadingProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [isLoadingGlobal, setIsLoadingGlobal] = useState(false);

  const showLoadingGlobal = useCallback(() => setIsLoadingGlobal(true), []);
  const hideLoadingGlobal = useCallback(() => setIsLoadingGlobal(false), []);

  const value = useMemo(
    () => ({ isLoadingGlobal, showLoadingGlobal, hideLoadingGlobal }),
    [isLoadingGlobal, showLoadingGlobal, hideLoadingGlobal]
  );

  return (
    <LoadingContext.Provider value={value}>
      <div style={{ position: 'relative', minHeight: '100vh' }}>
        <LoadingOverlay
          visible={isLoadingGlobal}
          zIndex={9999}
          overlayProps={{ radius: 'sm', blur: 2 }}
        />
        {children}
      </div>
    </LoadingContext.Provider>
  );
}

export function useLoadingGlobal() {
  const context = useContext(LoadingContext);
  if (context === undefined) {
    throw new Error('useLoading must be used within a LoadingProvider');
  }
  return context;
}
