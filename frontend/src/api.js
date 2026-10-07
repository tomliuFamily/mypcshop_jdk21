const API_BASE =
    "http://localhost:8080";


async function refreshAccessToken() {

    const refreshToken =
        sessionStorage.getItem(
            "refreshToken"
        );

    if (!refreshToken) {

        return null;
    }

    const response =
        await fetch(
            `${API_BASE}/api/user/refresh`,
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify({
                    refreshToken:
                        refreshToken
                })
            }
        );

    if (!response.ok) {

        sessionStorage.removeItem(
            "accessToken"
        );

        sessionStorage.removeItem(
            "refreshToken"
        );

        sessionStorage.removeItem(
            "user"
        );

        sessionStorage.removeItem(
            "isLoggedIn"
        );

        return null;
    }

    const data =
        await response.json();

    sessionStorage.setItem(
        "accessToken",
        data.accessToken
    );

    return data.accessToken;
}


export async function authFetch(
        url,
        options = {}) {

    let accessToken =
        sessionStorage.getItem(
            "accessToken"
        );

    const headers = {
        ...(options.headers || {})
    };

    if (accessToken) {

        headers.Authorization =
            `Bearer ${accessToken}`;
    }

    let response =
        await fetch(
            url,
            {
                ...options,
                headers
            }
        );


    // Access Token 過期
    if (response.status === 401) {

        const newAccessToken =
            await refreshAccessToken();

        if (!newAccessToken) {

            window.location.href =
                "/login";

            return response;
        }

        const retryHeaders = {
            ...(options.headers || {}),
            Authorization:
                `Bearer ${newAccessToken}`
        };

        // 用新的 Access Token
        // 再呼叫一次原本 API
        response =
            await fetch(
                url,
                {
                    ...options,
                    headers:
                        retryHeaders
                }
            );
    }

    return response;
}