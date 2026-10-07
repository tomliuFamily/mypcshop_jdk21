import {
    useState
} from "react";

import {
    useNavigate
} from "react-router-dom";


function CreateUser() {

    const navigate =
        useNavigate();


    const [name, setName] =
        useState("");

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");

    const [address, setAddress] =
        useState("");

    const [message, setMessage] =
        useState("");

    const [success, setSuccess] =
        useState(false);

    const [loading, setLoading] =
        useState(false);


    // ==========================================
    // 註冊會員
    // ==========================================

    const handleSubmit =
        async (event) => {

            event.preventDefault();


            try {

                setLoading(true);

                setMessage("");

                setSuccess(false);


                const requestBody = {

                    name:
                        name,

                    email:
                        email,

                    password:
                        password,

                    address:
                        address
                };


                const response =
                    await fetch(

                        "http://localhost:8080/api/user/create",

                        {
                            method: "POST",

                            headers: {

                                "Content-Type":
                                    "application/json"
                            },

                            body:
                                JSON.stringify(
                                    requestBody
                                )
                        }
                    );


                // ==============================
                // 註冊失敗
                // ==============================

                if (!response.ok) {

                    const text =
                        await response.text();


                    throw new Error(

                        text
                        || "會員註冊失敗"
                    );
                }


                const data =
                    await response.json();


                console.log(
                    "註冊成功：",
                    data
                );


                setSuccess(true);

                setMessage(
                    "🎉 註冊成功！即將前往登入頁。"
                );


                // 清除輸入
                setName("");
                setEmail("");
                setPassword("");
                setAddress("");


                setTimeout(() => {

                    navigate(
                        "/login"
                    );

                }, 1200);


            } catch (error) {

                console.error(
                    "註冊錯誤：",
                    error
                );


                setSuccess(false);


                setMessage(
                    error.message
                );


            } finally {

                setLoading(false);
            }
        };


    return (

        <div className="auth-page">

            <div className="auth-card">

                <div className="auth-icon">
                    ✨
                </div>


                <h2>
                    建立會員帳號
                </h2>


                <form
                    onSubmit={
                        handleSubmit
                    }
                >

                    {/* Name */}

                    <div className="form-group">

                        <label>
                            👤 姓名
                        </label>


                        <input
                            className="form-input"

                            type="text"

                            value={
                                name
                            }

                            onChange={
                                event =>
                                    setName(
                                        event.target.value
                                    )
                            }

                            placeholder=
                                "請輸入姓名"

                            required
                        />

                    </div>


                    {/* Email */}

                    <div className="form-group">

                        <label>
                            📧 Email
                        </label>


                        <input
                            className="form-input"

                            type="email"

                            value={
                                email
                            }

                            onChange={
                                event =>
                                    setEmail(
                                        event.target.value
                                    )
                            }

                            placeholder=
                                "請輸入 Email"

                            required
                        />

                    </div>


                    {/* Password */}

                    <div className="form-group">

                        <label>
                            🔑 密碼
                        </label>


                        <input
                            className="form-input"

                            type="password"

                            value={
                                password
                            }

                            onChange={
                                event =>
                                    setPassword(
                                        event.target.value
                                    )
                            }

                            placeholder=
                                "請輸入密碼"

                            required
                        />

                    </div>


                    {/* Address */}

                    <div className="form-group">

                        <label>
                            🏠 地址
                        </label>


                        <input
                            className="form-input"

                            type="text"

                            value={
                                address
                            }

                            onChange={
                                event =>
                                    setAddress(
                                        event.target.value
                                    )
                            }

                            placeholder=
                                "請輸入地址"

                            required
                        />

                    </div>


                    <button
                        type="submit"

                        className=
                            "primary-button"

                        disabled={
                            loading
                        }

                        style={{
                            width: "100%"
                        }}
                    >

                        {
                            loading

                                ? "註冊中..."

                                : "✨ 建立帳號"
                        }

                    </button>

                </form>


                {message && (

                    <p
                        className={
                            success

                                ? "auth-success"

                                : "auth-message"
                        }
                    >

                        {message}

                    </p>

                )}


                <p
                    style={{
                        marginTop: "22px",
                        textAlign: "center"
                    }}
                >

                    已經有帳號？


                    <button
                        type="button"

                        onClick={() =>
                            navigate(
                                "/login"
                            )
                        }

                        style={{
                            border: "none",
                            background: "none",
                            color: "#7c3aed",
                            fontWeight: "bold",
                            cursor: "pointer"
                        }}
                    >

                        前往登入

                    </button>

                </p>

            </div>

        </div>
    );
}


export default CreateUser;