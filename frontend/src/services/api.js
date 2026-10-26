const API_BASE_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8081";

let csrfToken;

async function getCsrfToken() {
  const response = await fetch(`${API_BASE_URL}/api/v1/auth/csrf`, {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error("No se pudo preparar la solicitud segura.");
  }

  const body = await response.json();
  csrfToken = body.token;
  return csrfToken;
}

function resolveApiUrl(input) {
  if (input instanceof Request) return input;

  const requestUrl = new URL(input, API_BASE_URL);
  if (requestUrl.pathname.startsWith("/api/v1/")) {
    const apiUrl = new URL(API_BASE_URL);
    requestUrl.protocol = apiUrl.protocol;
    requestUrl.host = apiUrl.host;
  }
  return requestUrl;
}

export async function apiFetch(input, init = {}) {
  const requestMethod = input instanceof Request ? input.method : "GET";
  const method = (init.method ?? requestMethod).toUpperCase();
  const requestUrl = resolveApiUrl(input);
  const headers = new Headers(input instanceof Request ? input.headers : undefined);

  new Headers(init.headers).forEach((value, name) => headers.set(name, value));

  if (!["GET", "HEAD", "OPTIONS", "TRACE"].includes(method)) {
    const token = csrfToken ?? await getCsrfToken();
    headers.set("X-XSRF-TOKEN", token);
  }

  return fetch(requestUrl, {
    ...init,
    credentials: "include",
    headers,
  });
}

export { API_BASE_URL };
