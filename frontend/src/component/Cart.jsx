import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Cart({
    cart,
    updateQuantity,
    removeFromCart,
    clearCart
}) {

    const navigate = useNavigate();

    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);


    // ==========================================
    // 計算總金額
    // ==========================================

    const total = cart.reduce(
        (sum, item) =>
            sum +
            Number(item.price) *
            Number(item.quantity),
        0
    );


    // ==========================================
    // JWT 失效
    // ==========================================

    const handleUnauthorized = () => {

        // ★ 正式 Access Token
        sessionStorage.removeItem("accessToken");

        // ★ Refresh Token
        sessionStorage.removeItem("refreshToken");

        // 舊版本相容
        sessionStorage.removeItem("token");

        // User
        sessionStorage.removeItem("user");

        // Login 狀態
        sessionStorage.removeItem("isLoggedIn");


        // 通知 Navbar
        window.dispatchEvent(
            new Event("loginStatusChanged")
        );


        alert(
            "登入已失效，請重新登入"
        );


        navigate("/login");
    };


    // ==========================================
    // Checkout
    // ==========================================

    const checkout = async () => {

        // ======================================
        // 1. 取得 User
        // ======================================

        const userText =
            sessionStorage.getItem("user");


        // ======================================
        // 2. ★ 取得 Access Token
        //
        // Login.jsx 存的是 accessToken
        // 所以這裡一定也要讀 accessToken
        // ======================================

        const accessToken =
            sessionStorage.getItem(
                "accessToken"
            );


        // ======================================
        // 3. 檢查登入
        // ======================================

        if (!userText || !accessToken) {

            alert(
                "請先登入會員"
            );

            navigate("/login");

            return;
        }


        // ======================================
        // 4. 購物車不能為空
        // ======================================

        if (cart.length === 0) {

            setMessage(
                "購物車目前沒有商品"
            );

            return;
        }


        // ======================================
        // 5. 前端庫存檢查
        // ======================================

        for (const item of cart) {

            if (
                item.stock !== null &&
                item.stock !== undefined &&
                item.quantity > item.stock
            ) {

                setMessage(
                    `商品「${item.name}」庫存不足，目前只剩 ${item.stock} 件`
                );

                return;
            }
        }


        // ======================================
        // 6. 解析 User
        // ======================================

        let user;

        try {

            user =
                JSON.parse(userText);

        } catch (error) {

            console.error(
                "User 資料解析失敗：",
                error
            );

            handleUnauthorized();

            return;
        }


        // ======================================
        // 7. 建立 Order Request
        // ======================================

        const requestBody = {

            userId:
                user.id,

            items:
                cart.map(
                    item => ({

                        productId:
                            item.id,

                        quantity:
                            Number(
                                item.quantity
                            )
                    })
                )
        };


        try {

            setLoading(true);

            setMessage("");


            // ==================================
            // 8. 建立訂單
            // ==================================

            const response =
                await fetch(
                    "http://localhost:8080/api/orders",
                    {
                        method: "POST",

                        headers: {

                            "Content-Type":
                                "application/json",

                            // ★ 使用 accessToken
                            "Authorization":
                                `Bearer ${accessToken}`
                        },

                        body:
                            JSON.stringify(
                                requestBody
                            )
                    }
                );


            // ==================================
            // 9. JWT 失效
            // ==================================

            if (
                response.status === 401 ||
                response.status === 403
            ) {

                handleUnauthorized();

                return;
            }


            // ==================================
            // 10. 建立訂單失敗
            // ==================================

            if (!response.ok) {

                const text =
                    await response.text();

                throw new Error(
                    text ||
                    "建立訂單失敗"
                );
            }


            // ==================================
            // 11. 建立訂單成功
            // ==================================

            const order =
                await response.json();


            console.log(
                "Order：",
                order
            );


            // ==================================
            // 12. 清空購物車
            // ==================================

            clearCart();


            alert(
                "🎉 訂單建立成功！\n" +
                "訂單編號：" +
                order.id
            );


            // ==================================
            // 13. 前往付款
            // ==================================

            navigate(
                `/payment/${order.id}`
            );

        } catch (error) {

            console.error(
                "建立訂單錯誤：",
                error
            );

            setMessage(
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

            <h1 className="page-title">
                🛒 我的購物車
            </h1>


            <p className="page-subtitle">
                確認商品、數量與庫存後進行結帳
            </p>


            {cart.length === 0 ? (

                <div className="empty-box">

                    <div
                        style={{
                            fontSize: "60px"
                        }}
                    >
                        🛒
                    </div>


                    <h2>
                        購物車目前沒有商品
                    </h2>


                    <button
                        type="button"
                        className="primary-button"
                        onClick={() =>
                            navigate("/products")
                        }
                    >
                        💻 前往商品專區
                    </button>

                </div>

            ) : (

                <>

                    {cart.map(
                        item => (

                            <div
                                className="cart-item"
                                key={item.id}
                            >

                                <img
                                    className="cart-image"
                                    src={item.image}
                                    alt={item.name}
                                />


                                <div className="cart-info">

                                    <h2>
                                        {item.name}
                                    </h2>


                                    <span className="category-badge">
                                        {item.category}
                                    </span>


                                    <p className="product-price">

                                        NT$ {
                                            Number(
                                                item.price
                                            ).toLocaleString()
                                        }

                                    </p>


                                    <p
                                        style={{
                                            color:
                                                item.stock <= 5
                                                    ? "#ea580c"
                                                    : "#16a34a",

                                            fontWeight:
                                                "bold"
                                        }}
                                    >

                                        📦 庫存：
                                        {item.stock}
                                        件

                                    </p>


                                    <div>

                                        <label>
                                            數量：
                                        </label>


                                        <input
                                            type="number"
                                            min="1"
                                            max={item.stock}
                                            value={item.quantity}
                                            className="quantity-input"

                                            onChange={
                                                event => {

                                                    let quantity =
                                                        Number(
                                                            event.target.value
                                                        );


                                                    if (
                                                        quantity < 1
                                                    ) {
                                                        quantity = 1;
                                                    }


                                                    if (
                                                        item.stock !== null &&
                                                        item.stock !== undefined &&
                                                        quantity > item.stock
                                                    ) {

                                                        alert(
                                                            `商品「${item.name}」目前最多只能購買 ${item.stock} 件`
                                                        );

                                                        quantity =
                                                            item.stock;
                                                    }


                                                    updateQuantity(
                                                        item.id,
                                                        quantity
                                                    );
                                                }
                                            }
                                        />


                                        <span
                                            style={{
                                                marginLeft: "10px",
                                                color: "#64748b"
                                            }}
                                        >

                                            最多 {item.stock} 件

                                        </span>

                                    </div>


                                    <p>

                                        小計：

                                        <strong>

                                            NT$ {
                                                Number(
                                                    item.price *
                                                    item.quantity
                                                ).toLocaleString()
                                            }

                                        </strong>

                                    </p>


                                    <button
                                        type="button"
                                        className="danger-button"

                                        onClick={() =>
                                            removeFromCart(
                                                item.id
                                            )
                                        }
                                    >
                                        🗑️ 移除商品
                                    </button>

                                </div>

                            </div>
                        )
                    )}


                    <div className="total-box">

                        <h2>

                            💰 總金額：

                            NT$ {
                                Number(
                                    total
                                ).toLocaleString()
                            }

                        </h2>


                        <button
                            type="button"
                            className="orange-button"
                            disabled={loading}
                            onClick={checkout}
                        >

                            {
                                loading
                                    ? "庫存確認中..."
                                    : "💳 前往結帳"
                            }

                        </button>

                    </div>

                </>

            )}


            {message && (

                <p
                    style={{
                        color: "#dc2626",
                        fontWeight: "bold",
                        marginTop: "20px"
                    }}
                >
                    {message}
                </p>

            )}

        </div>
    );
}

export default Cart;