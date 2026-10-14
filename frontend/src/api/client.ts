import type { ApiError } from './types';

// All requests go through the Vite dev proxy (see vite.config.ts),
// so no absolute backend URL is needed in the browser.
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(path, {
    headers: { 'Content-Type': 'application/json' },
    ...init,
  });
  if (!res.ok) {
    let message = `${res.status} ${res.statusText}`;
    try {
      const body = (await res.json()) as ApiError;
      message =
        body.errors?.length > 0
          ? `${body.error}: ${body.errors.map((e) => `${e.field} ${e.message}`).join('; ')}`
          : body.error;
    } catch {
      /* keep status text */
    }
    throw new Error(message);
  }
  return res.json() as Promise<T>;
}

export const api = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'POST', body: JSON.stringify(body) }),
};
