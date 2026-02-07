const { VITE_API_BASE_URL } = import.meta.env;

export default {
  API_BASE_URL: VITE_API_BASE_URL,
} as {
  readonly API_BASE_URL: string;
};
