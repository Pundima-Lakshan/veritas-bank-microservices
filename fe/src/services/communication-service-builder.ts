import { useAuthStore } from "@/stores/auth-store";

type ResponseType = "json" | "blob" | "text" | "none";

const handleResponse = async <O>(
  url: string,
  requestOption: RequestInit,
  responseType: ResponseType = "json"
) => {
  const response = await fetch(url, requestOption);

  if (response.status === 401) {
    // Optionally handle token refresh here if needed
    // const IsTokenAvailable = await refreshToken();
    // if (IsTokenAvailable) {
    //   if (requestOption.headers != null) {
    //     Object.assign(requestOption.headers, {
    //       Authorization: getBearerToken(),
    //     });
    //   }
    //   response = await fetch(url, requestOption);
    // }
  }

  if (!response.ok) {
    let jsonRes;
    try {
      jsonRes = await response.json();
    } catch {
      jsonRes = {};
    }
    throw {
      status: jsonRes.statusCode ?? jsonRes.status ?? response.status,
      statusText: jsonRes.statusCode ?? response.statusText,
      content:
        jsonRes.message ??
        jsonRes.error ??
        jsonRes.detail ??
        jsonRes.status ??
        "Error",
    };
  }
  if (responseType === "none") {
    return null as O;
  }
  if (responseType === "text") {
    return (await response.text()) as O;
  }
  if (responseType === "blob") {
    return (await response.blob()) as O;
  }
  return (await response.json()) as O;
};

// const getBearerToken = () => `Bearer ${sessionStorage.getItem("access_token")}`;
const getBearerToken = () => {
  return `Bearer ${useAuthStore.getState().accessToken}`;
};

const makeRequest = async <O>(
  url: string,
  requestOption: RequestInit,
  responseType: ResponseType = "json"
) => {
  const options: RequestInit = {
    ...requestOption,
    headers: {
      Authorization: getBearerToken(),
      "Content-Type": "application/json",
      ...(requestOption.headers || {}),
    },
  };
  return await handleResponse<O>(url, options, responseType);
};

const makeFormRequest = async <O>(
  url: string,
  requestOption: RequestInit,
  responseType: ResponseType = "json"
) => {
  const options: RequestInit = {
    ...requestOption,
  };
  return await handleResponse<O>(url, options, responseType);
};

export interface SearchParams {
  [key: string]: string | number | boolean | undefined;
}

export const get = async <O = unknown>(
  url: string,
  searchParams?: SearchParams,
  responseType?: ResponseType
) => {
  let fullUrl = url;
  if (searchParams) {
    const params = new URLSearchParams();
    Object.entries(searchParams).forEach(([key, value]) => {
      if (value !== undefined) params.append(key, String(value));
    });
    fullUrl += (url.includes("?") ? "&" : "?") + params.toString();
  }
  return await makeRequest<O>(fullUrl, { method: "GET" }, responseType);
};

export const post = async <O = unknown, R = unknown>(
  url: string,
  data: R,
  responseType?: ResponseType
) => {
  return await makeRequest<O>(
    url,
    {
      method: "POST",
      body: JSON.stringify(data),
    },
    responseType
  );
};

export const postFormData = async <O = unknown>(
  url: string,
  data: FormData,
  responseType?: ResponseType
) => {
  return await makeFormRequest<O>(
    url,
    {
      method: "POST",
      body: data,
    },
    responseType
  );
};

export const put = async <O = unknown, R = unknown>(
  url: string,
  data: R,
  responseType?: ResponseType
) => {
  return await makeRequest<O>(
    url,
    {
      method: "PUT",
      body: JSON.stringify(data),
    },
    responseType
  );
};

export const remove = async <O = unknown>(
  url: string,
  searchParams?: SearchParams,
  responseType?: ResponseType
) => {
  let fullUrl = url;
  if (searchParams) {
    const params = new URLSearchParams();
    Object.entries(searchParams).forEach(([key, value]) => {
      if (value !== undefined) params.append(key, String(value));
    });
    fullUrl += (url.includes("?") ? "&" : "?") + params.toString();
  }
  return await makeRequest<O>(fullUrl, { method: "DELETE" }, responseType);
};

export const getBlob = async <T>(url: string, options: RequestInit = {}) => {
  const response = await fetch(url, {
    method: "GET",
    credentials: "include",
    ...options,
  });

  if (!response.ok) {
    throw new Error("Network response was not ok");
  }

  return (await response.blob()) as T;
};

export const downloadJson = async <T>(url: string) => {
  const response = await fetch(url, {
    method: "GET",
  });

  if (!response.ok) {
    throw new Error("Network response was not ok");
  }

  return (await response.json()) as T;
};
