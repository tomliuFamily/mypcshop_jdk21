import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";


function Login() {

    // ==========================================
    // State
    // ==========================================

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");

    const [message, setMessage] =
        useState("");

    const [loading, setLoading] =
        useState(false);


    const navigate =
        useNavigate();


    // ==========================================
    // 登入
    // ==========================================

    const handleLogin = async (e) => {

        e.preventDefault();

        setMessage("");
        setLoading(true);


        try {

            // ==================================
            // 1. 呼叫 Spring Boot Login API
            // ==================================

            const response =
                await fetch(
                    "http://localhost:8080/api/user/login",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            email: email,
                            password: password
                        })
                    }
                );


            // ==================================
            // 2. 登入失敗
            // ==================================

            if (!response.ok) {

                let errorMessage =
                    "帳號或密碼錯誤";

                try {

                    const errorData =
                        await response.json();

                    if (errorData.message) {

                        errorMessage =
                            errorData.message;
                    }

                } catch {

                    // 後端如果不是回 JSON
                    // 就使用預設錯誤訊息
                }


                setMessage(
                    errorMessage
                );

                return;
            }


            // ==================================
            // 3. 登入成功
            //
            // 後端回傳：
            //
            // id
            // email
            // name
            // address
            // accessToken
            // refreshToken
            // ==================================

            const data =
                await response.json();


            console.log(
                "登入成功：",
                data
            );


            // ==================================
            // 4. 檢查 Token
            // ==================================

            if (
                !data.accessToken
                ||
                !data.refreshToken
            ) {

                setMessage(
                    "登入成功，但後端沒有回傳完整 Token"
                );

                return;
            }


            // ==================================
            // 5. 儲存短效 Access Token
            // ==================================

            sessionStorage.setItem(
                "accessToken",
                data.accessToken
            );


            // ==================================
            // 6. 儲存長效 Refresh Token
            // ==================================

            sessionStorage.setItem(
                "refreshToken",
                data.refreshToken
            );


            // ==================================
            // 7. 儲存 User 資料
            //
            // 不儲存 password
            // ==================================

            const user = {

                id: data.id,

                email: data.email,

                name: data.name,

                address: data.address
            };


            sessionStorage.setItem(
                "user",
                JSON.stringify(user)
            );


            // ==================================
            // 8. 保存登入狀態
            // ==================================

            sessionStorage.setItem(
                "isLoggedIn",
                "true"
            );


            // ==================================
            // 9. Debug
            // ==================================

            console.log(
                "目前登入 user：",
                user
            );

            console.log(
                "Access Token：",
                data.accessToken
            );

            console.log(
                "Refresh Token：",
                data.refreshToken
            );


            // ==================================
            // 10. ★ 登入成功後
            //     瀏覽 / 登入統計 +1
            //
            // POST /api/stats/visit
            //
            // 這支 API 有 Spring Security 保護
            // 所以一定要帶 Access Token
            // ==================================

            try {

                const statsResponse =
                    await fetch(
                        "http://localhost:8080/api/stats/visit",
                        {
                            method: "POST",

                            headers: {

                                "Authorization":
                                    `Bearer ${data.accessToken}`
                            }
                        }
                    );


                // ==============================
                // 統計成功
                // ==============================

                if (statsResponse.ok) {

                    const statsData =
                        await statsResponse.json();


                    console.log(
                        "登入統計成功，目前累計：",
                        statsData.totalVisits
                    );

                } else {

                    // ==========================
                    // 統計失敗
                    //
                    // 不影響會員登入
                    // ==========================

                    console.warn(
                        "登入成功，但瀏覽人數統計失敗：",
                        statsResponse.status
                    );
                }

            } catch (statsError) {

                // ==============================
                // 統計 API 出問題
                //
                // 不應該讓會員登入失敗
                // ==============================

                console.warn(
                    "登入成功，但無法更新瀏覽人數：",
                    statsError
                );
            }


            // ==================================
            // 11. 顯示登入成功
            // ==================================

            setMessage(
                "登入成功！"
            );


            // ==================================
            // 12. 通知 Navbar
            // ==================================

            window.dispatchEvent(
                new Event(
                    "loginStatusChanged"
                )
            );


            // ==================================
            // 13. 登入成功後回首頁
            // ==================================

            navigate("/");
        }

        catch (error) {

            console.error(
                "登入發生錯誤：",
                error
            );

            setMessage(
                "無法連線到伺服器，請確認 Spring Boot 是否已啟動"
            );
        }

        finally {

            setLoading(false);
        }
    };


    // ==========================================
    // JSX
    //
    // ★ 以下版面完全維持原本設計
    // ==========================================

    return (

        <div className="login-page">

            <div className="login-card">

                <h2>
                    會員登入
                </h2>


                <p className="login-subtitle">
                    歡迎回到 MyPC 商城
                </p>


                <form
                    onSubmit={handleLogin}
                >

                    {/* Email */}

                    <div className="form-group">

                        <label>
                            Email
                        </label>

                        <input
                            type="email"
                            value={email}
                            onChange={
                                (e) =>
                                    setEmail(
                                        e.target.value
                                    )
                            }
                            placeholder="請輸入 Email"
                            required
                            autoComplete="email"
                        />

                    </div>


                    {/* Password */}

                    <div className="form-group">

                        <label>
                            密碼
                        </label>

                        <input
                            type="password"
                            value={password}
                            onChange={
                                (e) =>
                                    setPassword(
                                        e.target.value
                                    )
                            }
                            placeholder="請輸入密碼"
                            required
                            autoComplete="current-password"
                        />

                    </div>


                    {/* Login Button */}

                    <button
                        type="submit"
                        disabled={loading}
                        className="login-button"
                    >

                        {
                            loading
                                ? "登入中..."
                                : "登入"
                        }

                    </button>

                </form>


                {/* Message */}

                {
                    message
                    &&
                    (
                        <p className="login-message">
                            {message}
                        </p>
                    )
                }


                {/* Register */}

                <div className="login-register">

                    還沒有帳號？

                    {" "}

                    <Link to="/create">
                        立即註冊
                    </Link>

                </div>

            </div>

        </div>
    );
}


export default Login;