import {
    useEffect,
    useState
} from "react";

import {
    useNavigate
} from "react-router-dom";


function OrderList() {

    const [
        orders,
        setOrders
    ] = useState([]);


    const [
        loading,
        setLoading
    ] = useState(true);


    const [
        error,
        setError
    ] = useState("");


    const navigate =
        useNavigate();


    // ==========================================
    // JWT 過期 / 登入失效
    // ==========================================

    const handleUnauthorized = () => {

        // 新版 Access Token
        sessionStorage.removeItem(
            "accessToken"
        );

        // 新版 Refresh Token
        sessionStorage.removeItem(
            "refreshToken"
        );

        // 舊版 token
        // 保留清除，避免以前資料殘留
        sessionStorage.removeItem(
            "token"
        );

        sessionStorage.removeItem(
            "user"
        );

        sessionStorage.removeItem(
            "isLoggedIn"
        );


        // 通知 Navbar 登入狀態改變
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
    // 讀取我的訂單
    // ==========================================

    useEffect(() => {

        const loadOrders =
            async () => {

                // ==================================
                // 取得登入會員資料
                // ==================================

                const userText =
                    sessionStorage.getItem(
                        "user"
                    );


                // ==================================
                // ★ 修改：
                // 使用新版 accessToken
                // 不再使用舊版 token
                // ==================================

                const accessToken =
                    sessionStorage.getItem(
                        "accessToken"
                    );


                // ==================================
                // 尚未登入
                // ==================================

                if (
                    !userText
                    ||
                    !accessToken
                ) {

                    navigate(
                        "/login"
                    );

                    return;
                }


                let user;


                // ==================================
                // 解析會員資料
                // ==================================

                try {

                    user =
                        JSON.parse(
                            userText
                        );

                } catch (error) {

                    console.error(
                        "會員資料解析失敗：",
                        error
                    );


                    sessionStorage.removeItem(
                        "accessToken"
                    );

                    sessionStorage.removeItem(
                        "refreshToken"
                    );

                    sessionStorage.removeItem(
                        "token"
                    );

                    sessionStorage.removeItem(
                        "user"
                    );

                    sessionStorage.removeItem(
                        "isLoggedIn"
                    );


                    navigate(
                        "/login"
                    );

                    return;
                }


                // ==================================
                // user id 不存在
                // ==================================

                if (
                    !user
                    ||
                    user.id === null
                    ||
                    user.id === undefined
                ) {

                    console.error(
                        "登入會員沒有 user.id：",
                        user
                    );

                    setError(
                        "登入會員資料不完整，請重新登入"
                    );

                    return;
                }


                try {

                    setLoading(true);

                    setError("");


                    // ==================================
                    // 查詢目前會員的所有訂單
                    //
                    // GET
                    // /api/orders/user/{userId}
                    // ==================================

                    const response =
                        await fetch(

                            `http://localhost:8080/api/orders/user/${user.id}`,

                            {
                                method:
                                    "GET",

                                headers: {

                                    // ==================
                                    // ★ 新版 JWT
                                    // Access Token
                                    // ==================

                                    "Authorization":
                                        `Bearer ${accessToken}`
                                }
                            }
                        );


                    // ==================================
                    // JWT 無效 / 過期 / 權限不足
                    // ==================================

                    if (
                        response.status === 401
                        ||
                        response.status === 403
                    ) {

                        handleUnauthorized();

                        return;
                    }


                    // ==================================
                    // 其他錯誤
                    // ==================================

                    if (!response.ok) {

                        const text =
                            await response.text();


                        throw new Error(

                            text
                            ||
                            "訂單資料取得失敗"
                        );
                    }


                    // ==================================
                    // JSON
                    // ==================================

                    const data =
                        await response.json();


                    console.log(
                        "我的訂單：",
                        data
                    );


                    // ==================================
                    // 確保回傳為陣列
                    // ==================================

                    if (
                        Array.isArray(data)
                    ) {

                        setOrders(
                            data
                        );

                    } else {

                        console.warn(
                            "訂單 API 回傳不是陣列：",
                            data
                        );

                        setOrders([]);
                    }


                } catch (error) {

                    console.error(
                        "訂單取得錯誤：",
                        error
                    );


                    setError(
                        error.message
                    );


                } finally {

                    setLoading(false);
                }
            };


        loadOrders();

    }, [navigate]);


    // ==========================================
    // Date
    // ==========================================

    const formatDate =
        (date) => {

            if (!date) {

                return "無日期";
            }


            return new Date(
                date
            ).toLocaleString();
        };


    // ==========================================
    // Order status
    // ==========================================

    const getOrderStatus =
        (status) => {

            switch (status) {

                case "NEW":

                    return "新訂單";


                case "PAID":

                    return "已付款";


                case "CANCELLED":

                    return "已取消";


                default:

                    return status
                        || "未知狀態";
            }
        };


    // ==========================================
    // Payment Method
    // ==========================================

    const getPaymentMethod =
        (method) => {

            switch (method) {

                case "CREDIT_CARD":

                    return "信用卡";


                case "BANK_TRANSFER":

                    return "銀行轉帳";


                case "CASH_ON_DELIVERY":

                    return "貨到付款";


                default:

                    return "尚未選擇";
            }
        };


    // ==========================================
    // Payment Status
    // ==========================================

    const getPaymentStatus =
        (status) => {

            switch (status) {

                case "UNPAID":

                    return "尚未付款";


                case "PENDING":

                    return "等待付款確認";


                case "PAID":

                    return "付款完成";


                case "FAILED":

                    return "付款失敗";


                default:

                    return "尚未付款";
            }
        };


    // ==========================================
    // Loading
    // ==========================================

    if (loading) {

        return (

            <div className="page-container">

                <h2 className="page-title">

                    📦 訂單載入中...

                </h2>

            </div>
        );
    }


    // ==========================================
    // JSX
    // ==========================================

    return (

        <div className="page-container">

            <h1 className="page-title">

                📋 我的訂單

            </h1>


            <p className="page-subtitle">

                查看您的購買紀錄與付款狀態

            </p>


            {/* ==================================
                Error
               ================================== */}

            {error && (

                <p
                    style={{
                        color: "red",
                        textAlign: "center"
                    }}
                >

                    {error}

                </p>
            )}


            {/* ==================================
                沒有訂單
               ================================== */}

            {
                !error
                &&
                orders.length === 0
                &&
                (

                    <div className="empty-box">

                        <div
                            style={{
                                fontSize: "55px"
                            }}
                        >

                            📦

                        </div>


                        <h2>

                            目前沒有訂單

                        </h2>


                        <button
                            className=
                                "primary-button"

                            onClick={() =>
                                navigate(
                                    "/products"
                                )
                            }
                        >

                            💻 前往購物

                        </button>

                    </div>
                )
            }


            {/* ==================================
                Orders
               ================================== */}

            {
                orders.map(
                    order => (

                        <div
                            className=
                                "order-card"

                            key={
                                order.id
                            }
                        >


                            {/* ======================
                                Header
                               ====================== */}

                            <div
                                className=
                                    "order-header"
                            >

                                <h2>

                                    🧾 訂單
                                    #{order.id}

                                </h2>


                                <p>

                                    📅 訂單日期：

                                    {
                                        formatDate(
                                            order.orderDate
                                        )
                                    }

                                </p>


                                <p>

                                    📦 訂單狀態：

                                    <strong>

                                        {
                                            getOrderStatus(
                                                order.status
                                            )
                                        }

                                    </strong>

                                </p>


                                <p>

                                    💰 訂單總金額：

                                    <strong
                                        style={{
                                            color: "#ea580c",
                                            fontSize: "19px"
                                        }}
                                    >

                                        NT$ {
                                            Number(
                                                order.totalAmount
                                                || 0
                                            ).toLocaleString()
                                        }

                                    </strong>

                                </p>


                                <p>

                                    💳 付款方式：

                                    <strong>

                                        {
                                            getPaymentMethod(
                                                order.paymentMethod
                                            )
                                        }

                                    </strong>

                                </p>


                                <p>

                                    付款狀態：

                                    <strong>

                                        {
                                            getPaymentStatus(
                                                order.paymentStatus
                                            )
                                        }

                                    </strong>

                                </p>

                            </div>


                            {/* ==================================
                                商品快照
                               ================================== */}

                            <h3>

                                🛍️ 商品明細

                            </h3>


                            {
                                order.items
                                &&
                                order.items.length > 0

                                    ? order.items.map(
                                        (
                                            item,
                                            index
                                        ) => (

                                            <div
                                                className=
                                                    "order-item"

                                                key={
                                                    item.productId
                                                    ?? index
                                                }
                                            >


                                                {/* ==================
                                                    Snapshot image
                                                   ================== */}

                                                {
                                                    item.productImage

                                                        ? (

                                                            <img
                                                                className=
                                                                    "order-image"

                                                                src={
                                                                    item.productImage
                                                                }

                                                                alt={
                                                                    item.productName
                                                                    || "商品"
                                                                }
                                                            />
                                                        )

                                                        : (

                                                            <div
                                                                className=
                                                                    "order-image"

                                                                style={{
                                                                    display: "flex",
                                                                    alignItems: "center",
                                                                    justifyContent: "center",
                                                                    background: "#f3f4f6",
                                                                    borderRadius: "10px"
                                                                }}
                                                            >

                                                                無圖片

                                                            </div>
                                                        )
                                                }


                                                {/* ==================
                                                    Snapshot info
                                                   ================== */}

                                                <div
                                                    className=
                                                        "order-item-info"
                                                >

                                                    <h3>

                                                        {
                                                            item.productName
                                                            ||
                                                            "商品名稱無資料"
                                                        }

                                                    </h3>


                                                    <span
                                                        className=
                                                            "category-badge"
                                                    >

                                                        {
                                                            item.productCategory
                                                            ||
                                                            "未分類"
                                                        }

                                                    </span>


                                                    <p>

                                                        下單單價：

                                                        <strong>

                                                            NT$ {
                                                                Number(
                                                                    item.price
                                                                    || 0
                                                                ).toLocaleString()
                                                            }

                                                        </strong>

                                                    </p>


                                                    <p>

                                                        數量：

                                                        {
                                                            item.quantity
                                                        }

                                                    </p>


                                                    <p>

                                                        小計：

                                                        <strong>

                                                            NT$ {
                                                                Number(
                                                                    item.subtotal
                                                                    || 0
                                                                ).toLocaleString()
                                                            }

                                                        </strong>

                                                    </p>

                                                </div>

                                            </div>
                                        )
                                    )

                                    : (

                                        <p>

                                            此訂單沒有商品明細

                                        </p>
                                    )
                            }


                            {/* ==================================
                                UNPAID
                               ================================== */}

                            {
                                order.paymentStatus
                                === "UNPAID"
                                &&
                                (

                                    <div
                                        style={{
                                            marginTop: "20px"
                                        }}
                                    >

                                        <p
                                            className=
                                                "status-unpaid"
                                        >

                                            ⚠️ 尚未付款

                                        </p>


                                        <button
                                            className=
                                                "orange-button"

                                            onClick={() =>
                                                navigate(
                                                    `/payment/${order.id}`
                                                )
                                            }
                                        >

                                            💳 前往付款

                                        </button>

                                    </div>
                                )
                            }


                            {/* ==================================
                                PAID
                               ================================== */}

                            {
                                order.paymentStatus
                                === "PAID"
                                &&
                                (

                                    <h3
                                        className=
                                            "status-paid"

                                        style={{
                                            marginTop: "20px"
                                        }}
                                    >

                                        ✅ 付款完成

                                    </h3>
                                )
                            }


                            {/* ==================================
                                PENDING
                               ================================== */}

                            {
                                order.paymentStatus
                                === "PENDING"
                                &&
                                (

                                    <div
                                        style={{
                                            marginTop: "20px"
                                        }}
                                    >

                                        <h3
                                            className=
                                                "status-pending"
                                        >

                                            ⏳ 等待付款確認

                                        </h3>


                                        {
                                            order.paymentMethod
                                            === "BANK_TRANSFER"
                                            &&
                                            (

                                                <p>

                                                    您選擇銀行轉帳，
                                                    請完成轉帳後等待確認。

                                                </p>
                                            )
                                        }


                                        {
                                            order.paymentMethod
                                            === "CASH_ON_DELIVERY"
                                            &&
                                            (

                                                <p>

                                                    您選擇貨到付款，
                                                    商品送達時再進行付款。

                                                </p>
                                            )
                                        }

                                    </div>
                                )
                            }


                            {/* ==================================
                                FAILED
                               ================================== */}

                            {
                                order.paymentStatus
                                === "FAILED"
                                &&
                                (

                                    <div
                                        style={{
                                            marginTop: "20px"
                                        }}
                                    >

                                        <p
                                            className=
                                                "status-unpaid"
                                        >

                                            ❌ 付款失敗

                                        </p>


                                        <button
                                            className=
                                                "orange-button"

                                            onClick={() =>
                                                navigate(
                                                    `/payment/${order.id}`
                                                )
                                            }
                                        >

                                            🔄 重新付款

                                        </button>

                                    </div>
                                )
                            }

                        </div>
                    )
                )
            }

        </div>
    );
}


export default OrderList;