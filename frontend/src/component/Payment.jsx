import { useState } from "react";
import {
    useNavigate,
    useParams
} from "react-router-dom";


function Payment() {

    // ==========================================
    // Order ID
    // ==========================================

    const { orderId } =
        useParams();


    const navigate =
        useNavigate();


    // ==========================================
    // Payment Method
    // ==========================================

    const [
        paymentMethod,
        setPaymentMethod
    ] = useState(
        "CREDIT_CARD"
    );


    // ==========================================
    // Message
    // ==========================================

    const [
        message,
        setMessage
    ] = useState("");


    // ==========================================
    // Loading
    // ==========================================

    const [
        loading,
        setLoading
    ] = useState(false);


    // ==========================================
    // JWT 失效
    // ==========================================

    const handleUnauthorized = () => {

        sessionStorage.removeItem(
            "accessToken"
        );

        sessionStorage.removeItem(
            "refreshToken"
        );

        // 舊版本相容
        sessionStorage.removeItem(
            "token"
        );

        sessionStorage.removeItem(
            "user"
        );

        sessionStorage.removeItem(
            "isLoggedIn"
        );


        window.dispatchEvent(
            new Event(
                "loginStatusChanged"
            )
        );


        alert(
            "登入已失效，請重新登入"
        );


        navigate(
            "/login"
        );
    };


    // ==========================================
    // Payment
    // ==========================================

    const handlePayment =
        async () => {


            // ==================================
            // 1. ★ 取得 Access Token
            // ==================================

            const accessToken =
                sessionStorage.getItem(
                    "accessToken"
                );


            // ==================================
            // 2. 確認登入
            // ==================================

            if (!accessToken) {

                alert(
                    "請先登入會員"
                );


                navigate(
                    "/login"
                );


                return;
            }


            // ==================================
            // 3. 確認 Order ID
            // ==================================

            if (!orderId) {

                setMessage(
                    "找不到訂單編號"
                );


                return;
            }


            try {

                setLoading(true);

                setMessage("");


                // ==================================
                // 4. 呼叫付款 API
                // ==================================

                const response =
                    await fetch(

                        "http://localhost:8080/api/payments",

                        {

                            method:
                                "POST",


                            headers: {

                                "Content-Type":
                                    "application/json",


                                // ★ 改成 accessToken
                                "Authorization":
                                    `Bearer ${accessToken}`
                            },


                            body:
                                JSON.stringify({

                                    orderId:
                                        Number(
                                            orderId
                                        ),


                                    paymentMethod:
                                        paymentMethod
                                })
                        }
                    );


                // ==================================
                // 5. JWT 無效
                // ==================================

                if (
                    response.status === 401 ||
                    response.status === 403
                ) {

                    handleUnauthorized();

                    return;
                }


                // ==================================
                // 6. Payment Error
                // ==================================

                if (!response.ok) {

                    const text =
                        await response.text();


                    throw new Error(
                        text ||
                        "付款失敗"
                    );
                }


                // ==================================
                // 7. Payment Response
                // ==================================

                const payment =
                    await response.json();


                console.log(
                    "付款結果：",
                    payment
                );


                // ==================================
                // 8. PAID
                // ==================================

                if (
                    payment.paymentStatus
                    === "PAID"
                ) {

                    alert(
                        "✅ 付款完成！"
                    );


                    navigate(

                        "/order-complete",

                        {

                            state: {

                                orderId:
                                    Number(
                                        orderId
                                    )
                            }
                        }
                    );


                    return;
                }


                // ==================================
                // 9. PENDING
                // ==================================

                if (
                    payment.paymentStatus
                    === "PENDING"
                ) {

                    alert(
                        "⏳ 付款資料建立完成，等待付款確認"
                    );


                    navigate(
                        "/orders"
                    );


                    return;
                }


                // ==================================
                // 10. 其他狀態
                // ==================================

                alert(
                    "付款資料已建立"
                );


                navigate(
                    "/orders"
                );


            } catch (error) {

                console.error(
                    "付款錯誤：",
                    error
                );


                setMessage(
                    "付款失敗：" +
                    error.message
                );


            } finally {

                setLoading(false);
            }
        };


    // ==========================================
    // JSX
    // ==========================================

    return (

        <div className="page-container">

            <div className="payment-card">


                <h1 className="page-title">
                    💳 選擇付款方式
                </h1>


                <p
                    className=
                        "payment-order-number"
                >

                    訂單編號：

                    <strong>
                        #{orderId}
                    </strong>

                </p>


                {/* 信用卡 */}

                <label
                    className=
                        "payment-option"
                >

                    <input
                        type="radio"
                        value="CREDIT_CARD"

                        checked={
                            paymentMethod
                            === "CREDIT_CARD"
                        }

                        onChange={
                            event =>
                                setPaymentMethod(
                                    event.target.value
                                )
                        }
                    />

                    &nbsp;

                    💳 信用卡

                </label>


                {/* 銀行轉帳 */}

                <label
                    className=
                        "payment-option"
                >

                    <input
                        type="radio"
                        value="BANK_TRANSFER"

                        checked={
                            paymentMethod
                            === "BANK_TRANSFER"
                        }

                        onChange={
                            event =>
                                setPaymentMethod(
                                    event.target.value
                                )
                        }
                    />

                    &nbsp;

                    🏦 銀行轉帳

                </label>


                {/* 貨到付款 */}

                <label
                    className=
                        "payment-option"
                >

                    <input
                        type="radio"
                        value="CASH_ON_DELIVERY"

                        checked={
                            paymentMethod
                            === "CASH_ON_DELIVERY"
                        }

                        onChange={
                            event =>
                                setPaymentMethod(
                                    event.target.value
                                )
                        }
                    />

                    &nbsp;

                    🚚 貨到付款

                </label>


                {/* 確認付款 */}

                <button
                    className=
                        "primary-button"

                    disabled={
                        loading
                    }

                    onClick={
                        handlePayment
                    }

                    style={{
                        width:
                            "100%",

                        marginTop:
                            "18px"
                    }}
                >

                    {
                        loading
                            ? "付款處理中..."
                            : "✅ 確認付款方式"
                    }

                </button>


                {/* 回訂單 */}

                <button
                    className=
                        "orange-button"

                    disabled={
                        loading
                    }

                    onClick={() =>
                        navigate(
                            "/orders"
                        )
                    }

                    style={{
                        width:
                            "100%",

                        marginTop:
                            "10px"
                    }}
                >

                    📋 回我的訂單

                </button>


                {/* Error */}

                {message && (

                    <p
                        style={{
                            color:
                                "red",

                            textAlign:
                                "center",

                            marginTop:
                                "15px",

                            fontWeight:
                                "bold"
                        }}
                    >

                        {message}

                    </p>

                )}


            </div>

        </div>
    );
}


export default Payment;