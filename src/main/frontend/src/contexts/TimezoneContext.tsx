import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';

const TIMEZONE_STORAGE_KEY = 'user-timezone';

interface TimezoneContextType {
  timezone: string;
  setTimezone: (tz: string) => void;
}

const TimezoneContext = createContext<TimezoneContextType | undefined>(undefined);

function getSystemTimezone(): string {
  return Intl.DateTimeFormat().resolvedOptions().timeZone;
}

function getInitialTimezone(): string {
  const stored = localStorage.getItem(TIMEZONE_STORAGE_KEY);
  if (stored) {
    return stored;
  }
  const systemTz = getSystemTimezone();
  localStorage.setItem(TIMEZONE_STORAGE_KEY, systemTz);
  return systemTz;
}

export function TimezoneProvider({ children }: { children: ReactNode }) {
  const [timezone, setTimezoneState] = useState<string>(getInitialTimezone);

  const setTimezone = (tz: string) => {
    localStorage.setItem(TIMEZONE_STORAGE_KEY, tz);
    setTimezoneState(tz);
  };

  useEffect(() => {
    const stored = localStorage.getItem(TIMEZONE_STORAGE_KEY);
    if (!stored) {
      const systemTz = getSystemTimezone();
      localStorage.setItem(TIMEZONE_STORAGE_KEY, systemTz);
      setTimezoneState(systemTz);
    }
  }, []);

  return (
    <TimezoneContext.Provider value={{ timezone, setTimezone }}>
      {children}
    </TimezoneContext.Provider>
  );
}

export function useTimezone() {
  const context = useContext(TimezoneContext);
  if (context === undefined) {
    throw new Error('useTimezone must be used within a TimezoneProvider');
  }
  return context;
}
