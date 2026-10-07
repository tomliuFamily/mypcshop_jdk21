import {
    useEffect,
    useState
} from "react";

import {
    useNavigate
} from "react-router-dom";


function MemberPreferences({
    addToCart
}) {

    const navigate =
        useNavigate();


    const [
        recentProducts,
        setRecentProducts
    ] = useState([]);


    const [
        notifications,
        setNotifications
    ] = useState([]);


    const [
        loading,
        setLoading
    ] = useState(true);


    const [
        message,
        setMessage
    ] = useState("");


    // ==========================================
    // 登入會員
    // ==========================================

    const userText =
        sessionStorage.getItem(
            "user"
        );


    const accessToken =
        sessionStorage.getItem(
            "accessToken"
        );


    let user = null;


    try {

        if (userText) {

            user =
                JSON.parse(
                    userText
                );
        }

    } catch (error) {

        console.error(
            "會員資料解析失敗",
            error
        );
    }


    // ==========================================
    // 最近瀏覽
    // ==========================================

    const loadRecentProducts =
        () => {

            try {

                const text =
                    localStorage.getItem(
                        "recentProducts"
                    );


                if (!text) {

                    setRecentProducts(
                        []
                    );

                    return;
                }


                const data =
                    JSON.parse(
                        text
                    );


                if (
                    Array.isArray(
                        data
                    )
                ) {

                    setRecentProducts(
                        data.slice(
                            0,
                            10
                        )
                    );

                } else {

                    setRecentProducts(
                        []
                    );
                }


            } catch (error) {

                console.error(
                    "最近瀏覽讀取失敗",
                    error
                );


                setRecentProducts(
                    []
                );
            }
        };


    // ==========================================
    // 到貨通知
    // ==========================================

    const loadNotifications =
        async () => {

            if (
                !user?.id
                ||
                !accessToken
            ) {

                return;
            }


            try {

                const response =
                    await fetch(

                        `http://localhost:8080/api/stock-notifications/user/${user.id}`,

                        {

                            headers: {

                                Authorization:
                                    `Bearer ${accessToken}`
                            }
                        }
                    );


                if (
                    response.status
                    ===
                    401
                ) {

                    setMessage(
                        "登入已失效，請重新登入"
                    );

                    return;
                }


                if (!response.ok) {

                    throw new Error(
                        "到貨通知讀取失敗"
                    );
                }


                const data =
                    await response.json();


                setNotifications(
                    data
                );


            } catch (error) {

                console.error(
                    error
                );


                setMessage(
                    "到貨通知資料讀取失敗"
                );
            }
        };


    // ==========================================
    // 初次載入
    // ==========================================

    useEffect(
        () => {

            if (
                !user
                ||
                !accessToken
            ) {

                alert(
                    "請先登入會員"
                );


                navigate(
                    "/login"
                );


                return;
            }


            loadRecentProducts();


            loadNotifications()
                .finally(
                    () =>
                        setLoading(
                            false
                        )
                );

        },
        []
    );


    // ==========================================
    // 清除瀏覽紀錄
    // ==========================================

    const clearRecent =
        () => {

            localStorage.removeItem(
                "recentProducts"
            );


            setRecentProducts(
                []
            );


            setMessage(
                "最近瀏覽紀錄已清除"
            );
        };


    // ==========================================
    // ★ 加入購物車
    // ==========================================
    //
    // MemberPreferences 裡面的商品來自
    // localStorage recentProducts
    //
    // 所以先把資料整理成購物車使用的
    // 標準 Product 格式，再交給 App.jsx
    // 的 addToCart()
    // ==========================================

    const handleAddToCart =
        (product) => {

            // ==================================
            // 1. 確認 addToCart 有傳進來
            // ==================================

            if (
                typeof addToCart
                !==
                "function"
            ) {

                console.error(
                    "MemberPreferences 沒有收到 addToCart"
                );


                setMessage(
                    "購物車功能目前無法使用"
                );


                return;
            }


            // ==================================
            // 2. 商品 ID
            // ==================================

            const productId =
                Number(
                    product?.id
                );


            if (
                !productId
                ||
                Number.isNaN(
                    productId
                )
            ) {

                console.error(
                    "商品 ID 錯誤：",
                    product
                );


                setMessage(
                    "商品資料不完整，無法加入購物車"
                );


                return;
            }


            // ==================================
            // 3. 庫存
            // ==================================

            const stock =
                product.stock === null
                ||
                product.stock === undefined
                    ?
                    null
                    :
                    Number(
                        product.stock
                    );


            // ==================================
            // 4. 如果確定是 0 庫存
            // ==================================

            if (
                stock !== null
                &&
                stock <= 0
            ) {

                setMessage(
                    `商品「${product.name}」目前沒有庫存`
                );


                return;
            }


            // ==================================
            // 5. 建立標準購物車商品
            // ==================================

            const cartProduct = {

                ...product,

                id:
                    productId,

                name:
                    product.name
                    ||
                    product.title
                    ||
                    "未命名商品",

                price:
                    Number(
                        product.price
                        ||
                        0
                    ),

                image:
                    product.image
                    ||
                    "",

                category:
                    product.category
                    ||
                    "",

                stock:
                    stock
            };


            // ==================================
            // 6. 呼叫 App.jsx 的 addToCart
            // ==================================

            addToCart(
                cartProduct
            );


            // ==================================
            // 7. 顯示成功訊息
            // ==================================

            setMessage(
                `✅ 「${cartProduct.name}」已加入購物車`
            );


            console.log(
                "從會員設定加入購物車：",
                cartProduct
            );
        };


    // ==========================================
    // 取消到貨通知
    // ==========================================

    const cancelNotification =
        async (
            notificationId
        ) => {

            if (
                !window.confirm(
                    "確定取消這個到貨通知嗎？"
                )
            ) {

                return;
            }


            try {

                const response =
                    await fetch(

                        `http://localhost:8080/api/stock-notifications/${notificationId}/user/${user.id}`,

                        {

                            method:
                                "DELETE",

                            headers: {

                                Authorization:
                                    `Bearer ${accessToken}`
                            }
                        }
                    );


                const data =
                    await response.json();


                if (!response.ok) {

                    throw new Error(

                        data.message
                        ||
                        "取消失敗"
                    );
                }


                setMessage(
                    "已取消到貨通知"
                );


                await loadNotifications();


            } catch (error) {

                setMessage(
                    error.message
                );
            }
        };


    // ==========================================
    // 日期格式
    // ==========================================

    const formatDate =
        (dateText) => {

            if (!dateText) {

                return "-";
            }


            return new Date(
                dateText
            )
                .toLocaleString(
                    "zh-TW"
                );
        };


    // ==========================================
    // Loading
    // ==========================================

    if (loading) {

        return (

            <div
                className=
                    "member-preferences-page"
            >

                <div
                    className=
                        "member-preferences-container"
                >

                    資料載入中...

                </div>

            </div>
        );
    }


    // ==========================================
    // JSX
    // ==========================================

    return (

        <div
            className=
                "member-preferences-page"
        >

            <div
                className=
                    "member-preferences-container"
            >


                {/* ==========================
                    標題
                ========================== */}

                <div
                    className=
                        "member-preferences-title"
                >

                    <div
                        className=
                            "member-preferences-title-icon"
                    >
                        ⚙️
                    </div>


                    <div>

                        <h2>
                            會員設定
                        </h2>


                        <p>
                            管理您的瀏覽紀錄與到貨通知
                        </p>

                    </div>

                </div>


                {/* ==========================
                    訊息
                ========================== */}

                {
                    message
                    &&
                    (

                        <div
                            className=
                                "member-preferences-message"
                        >

                            {message}

                        </div>
                    )
                }


                {/* ==========================
                    最近瀏覽商品
                ========================== */}

                <section
                    className=
                        "member-preferences-card"
                >

                    <div
                        className=
                            "member-section-header"
                    >

                        <div>

                            <h3>
                                🕘 最近瀏覽商品
                            </h3>


                            <p>
                                系統保留最近 10 筆瀏覽紀錄
                            </p>

                        </div>


                        <button

                            type="button"

                            className=
                                "recent-clear-button"

                            onClick={
                                clearRecent
                            }
                        >

                            🗑 清除紀錄

                        </button>

                    </div>


                    {
                        recentProducts.length
                        ===
                        0
                            ? (

                                <div
                                    className=
                                        "member-empty"
                                >

                                    目前沒有最近瀏覽商品

                                </div>

                            )
                            : (

                                <div
                                    className=
                                        "recent-product-grid"
                                >

                                    {
                                        recentProducts.map(

                                            product => (

                                                <div

                                                    className=
                                                        "recent-product-card"

                                                    key={
                                                        product.id
                                                    }
                                                >

                                                    <img

                                                        src={
                                                            product.image
                                                        }

                                                        alt={
                                                            product.name
                                                        }
                                                    />


                                                    <h4>

                                                        {
                                                            product.name
                                                        }

                                                    </h4>


                                                    <div
                                                        className=
                                                            "recent-product-price"
                                                    >

                                                        NT$ {

                                                            Number(
                                                                product.price
                                                            )
                                                                .toLocaleString()
                                                        }

                                                    </div>


                                                    <div
                                                        className=
                                                            "recent-product-category"
                                                    >

                                                        {
                                                            product.category
                                                        }

                                                    </div>


                                                    {/* ======================
                                                        ★ 加入購物車
                                                    ====================== */}

                                                    <button

                                                        type="button"

                                                        className=
                                                            "recent-cart-button"

                                                        onClick={
                                                            () =>
                                                                handleAddToCart(
                                                                    product
                                                                )
                                                        }
                                                    >

                                                        🛒 加入購物車

                                                    </button>

                                                </div>
                                            )
                                        )
                                    }

                                </div>
                            )
                    }

                </section>


                {/* ==========================
                    到貨通知
                ========================== */}

                <section
                    className=
                        "member-preferences-card"
                >

                    <div
                        className=
                            "member-section-header"
                    >

                        <div>

                            <h3>
                                🔔 到貨通知
                            </h3>


                            <p>
                                您關注的商品補貨狀態
                            </p>

                        </div>

                    </div>


                    {
                        notifications.length
                        ===
                        0
                            ? (

                                <div
                                    className=
                                        "member-empty"
                                >

                                    目前沒有設定到貨通知

                                </div>

                            )
                            : (

                                <div
                                    className=
                                        "notification-table-wrapper"
                                >

                                    <table
                                        className=
                                            "notification-table"
                                    >

                                        <thead>

                                            <tr>

                                                <th>
                                                    商品名稱
                                                </th>

                                                <th>
                                                    商品分類
                                                </th>

                                                <th>
                                                    期待價格
                                                </th>

                                                <th>
                                                    通知狀態
                                                </th>

                                                <th>
                                                    設定時間
                                                </th>

                                                <th>
                                                    操作
                                                </th>

                                            </tr>

                                        </thead>


                                        <tbody>

                                            {
                                                notifications.map(

                                                    item => (

                                                        <tr
                                                            key={
                                                                item.id
                                                            }
                                                        >

                                                            <td
                                                                className=
                                                                    "notification-product"
                                                            >

                                                                {
                                                                    item.image
                                                                    &&
                                                                    (

                                                                        <img

                                                                            src={
                                                                                item.image
                                                                            }

                                                                            alt={
                                                                                item.productName
                                                                            }
                                                                        />
                                                                    )
                                                                }


                                                                <strong>

                                                                    {
                                                                        item.productName
                                                                    }

                                                                </strong>

                                                            </td>


                                                            <td>

                                                                {
                                                                    item.category
                                                                }

                                                            </td>


                                                            <td>

                                                                {
                                                                    item.targetPrice
                                                                        ?
                                                                        `NT$ ${Number(
                                                                            item.targetPrice
                                                                        ).toLocaleString()}`
                                                                        :
                                                                        "—"
                                                                }

                                                            </td>


                                                            <td>

                                                                {
                                                                    item.stock > 0
                                                                        ?
                                                                        (

                                                                            <span
                                                                                className=
                                                                                    "notification-available"
                                                                            >

                                                                                🟢 有貨通知

                                                                            </span>

                                                                        )
                                                                        :
                                                                        (

                                                                            <span
                                                                                className=
                                                                                    "notification-waiting"
                                                                            >

                                                                                🔴 缺貨中

                                                                            </span>
                                                                        )
                                                                }

                                                            </td>


                                                            <td>

                                                                {
                                                                    formatDate(
                                                                        item.createdAt
                                                                    )
                                                                }

                                                            </td>


                                                            <td>

                                                                <button

                                                                    type="button"

                                                                    className=
                                                                        "notification-delete-button"

                                                                    onClick={
                                                                        () =>
                                                                            cancelNotification(
                                                                                item.id
                                                                            )
                                                                    }
                                                                >

                                                                    🗑 取消通知

                                                                </button>

                                                            </td>

                                                        </tr>
                                                    )
                                                )
                                            }

                                        </tbody>

                                    </table>

                                </div>
                            )
                    }

                </section>

            </div>

        </div>
    );
}


export default MemberPreferences;