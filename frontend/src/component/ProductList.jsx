import { useEffect, useState } from "react";
import {
    useLocation,
    useNavigate
} from "react-router-dom";

const API_URL =
    "http://localhost:8080/api/products";

const NOTIFICATION_API =
    "http://localhost:8080/api/stock-notifications";

const inputStyle = {
    width: "160px",
    maxWidth: "100%",
    padding: "10px 12px",
    borderRadius: "10px",
    border: "1px solid #ddd6fe"
};

// 空白代表不限價格；0 是有效價格。
function validatePrices(minPrice, maxPrice) {
    const min =
        minPrice === "" ? null : Number(minPrice);

    const max =
        maxPrice === "" ? null : Number(maxPrice);

    if (
        min !== null &&
        (!Number.isFinite(min) || min < 0)
    ) {
        return "最低價格必須是大於或等於 0 的有效數字";
    }

    if (
        max !== null &&
        (!Number.isFinite(max) || max < 0)
    ) {
        return "最高價格必須是大於或等於 0 的有效數字";
    }

    if (
        min !== null &&
        max !== null &&
        min > max
    ) {
        return "最低價格不可大於最高價格";
    }

    return "";
}

export default function ProductList({ addToCart }) {
    const location = useLocation();

    // 每次搜尋、清除或返回歷史頁面，
    // 都從網址重新還原表單與商品結果。
    return (
        <ProductSearch
            key={location.key}
            search={location.search}
            addToCart={addToCart}
        />
    );
}

function ProductSearch({ search, addToCart }) {
    const navigate = useNavigate();

    const applied = new URLSearchParams(search);

    const appliedKeyword =
        (applied.get("keyword") || "").trim();

    const appliedMin =
        applied.get("minPrice") || "";

    const appliedMax =
        applied.get("maxPrice") || "";

    // 表單輸入與已送出的條件分開，
    // 修改欄位後按搜尋才會更新商品。
    const [keyword, setKeyword] =
        useState(appliedKeyword);

    const [minPrice, setMinPrice] =
        useState(appliedMin);

    const [maxPrice, setMaxPrice] =
        useState(appliedMax);

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState("");
    const [formError, setFormError] = useState("");
    const [currentPage, setCurrentPage] = useState(1);

    const [
        notificationLoadingId,
        setNotificationLoadingId
    ] = useState(null);

    const productsPerPage = 4;

    useEffect(() => {
        const controller = new AbortController();

        async function loadProducts() {
            const error = validatePrices(
                appliedMin,
                appliedMax
            );

            if (error) {
                setMessage(error);
                setLoading(false);
                return;
            }

            const params = new URLSearchParams();

            if (appliedKeyword) {
                params.set("keyword", appliedKeyword);
            }

            if (appliedMin !== "") {
                params.set("minPrice", appliedMin);
            }

            if (appliedMax !== "") {
                params.set("maxPrice", appliedMax);
            }

            const url = params.size
                ? `${API_URL}/search?${params}`
                : API_URL;

            try {
                const response = await fetch(url, {
                    signal: controller.signal
                });

                if (!response.ok) {
                    throw new Error(
                        "商品資料載入失敗，請稍後再試"
                    );
                }

                const data = await response.json();

                if (!Array.isArray(data)) {
                    throw new Error("商品資料格式錯誤");
                }

                setProducts(data);

                setMessage(
                    data.length === 0
                        ? "查無符合條件的商品"
                        : ""
                );
            } catch (error) {
                if (!controller.signal.aborted) {
                    setMessage(error.message);
                }
            } finally {
                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            }
        }

        loadProducts();

        // 切換搜尋條件時取消舊請求，
        // 避免舊結果覆蓋新結果。
        return () => controller.abort();
    }, [appliedKeyword, appliedMin, appliedMax]);

    function handleSearch(event) {
        event.preventDefault();

        const error = validatePrices(
            minPrice,
            maxPrice
        );

        setFormError(error);

        if (error) {
            return;
        }

        const params = new URLSearchParams();

        if (keyword.trim()) {
            params.set("keyword", keyword.trim());
        }

        if (minPrice !== "") {
            params.set("minPrice", minPrice);
        }

        if (maxPrice !== "") {
            params.set("maxPrice", maxPrice);
        }

        navigate(
            params.size
                ? `/products?${params}`
                : "/products"
        );
    }

    function handleViewProduct(product) {
        try {
            const saved = JSON.parse(
                localStorage.getItem("recentProducts") ||
                "[]"
            );

            const recent =
                Array.isArray(saved) ? saved : [];

            const updated = [
                product,
                ...recent.filter(
                    item =>
                        Number(item.id) !==
                        Number(product.id)
                )
            ];

            localStorage.setItem(
                "recentProducts",
                JSON.stringify(updated.slice(0, 10))
            );

            alert(
                `🕘 已將「${product.name}」加入最近瀏覽商品`
            );
        } catch (error) {
            console.error(
                "最近瀏覽商品紀錄失敗：",
                error
            );

            alert("最近瀏覽商品紀錄失敗");
        }
    }

    async function handleStockNotification(product) {
        const userText =
            sessionStorage.getItem("user");

        const accessToken =
            sessionStorage.getItem("accessToken");

        if (!userText || !accessToken) {
            alert("請先登入會員");
            navigate("/login");
            return;
        }

        let user;

        try {
            user = JSON.parse(userText);
        } catch {
            alert("會員資料錯誤，請重新登入");
            navigate("/login");
            return;
        }

        if (!user?.id || !product?.id) {
            alert("找不到會員或商品資料");
            return;
        }

        try {
            setNotificationLoadingId(product.id);

            const response = await fetch(
                NOTIFICATION_API,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization:
                            `Bearer ${accessToken}`
                    },
                    body: JSON.stringify({
                        userId: user.id,
                        productId: product.id,
                        targetPrice: null
                    })
                }
            );

            if (response.status === 401) {
                alert("登入已失效，請重新登入");
                navigate("/login");
                return;
            }

            if (response.status === 403) {
                alert("沒有權限設定到貨通知");
                return;
            }

            const data = await response
                .json()
                .catch(() => ({}));

            if (!response.ok) {
                throw new Error(
                    data.message ||
                    "到貨通知設定失敗"
                );
            }

            alert(
                `🔔「${product.name}」到貨通知設定成功`
            );
        } catch (error) {
            alert(
                error.message ||
                "到貨通知設定失敗"
            );
        } finally {
            setNotificationLoadingId(null);
        }
    }

    const totalPages = Math.ceil(
        products.length / productsPerPage
    );

    const currentProducts = products.slice(
        (currentPage - 1) * productsPerPage,
        currentPage * productsPerPage
    );

    function goToPage(page) {
        if (page < 1 || page > totalPages) {
            return;
        }

        setCurrentPage(page);

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    }

    return (
        <div className="page-container">
            <h1 className="page-title">
                🖥️ 商品專區
            </h1>

            <p className="page-subtitle">
                精選電腦周邊商品
            </p>

            <form
                id="product-search"
                onSubmit={handleSearch}
                style={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "flex-end",
                    gap: "12px",
                    flexWrap: "wrap",
                    margin: "20px 0",
                    padding: "16px",
                    borderRadius: "16px",
                    background: "#f5f3ff"
                }}
            >
                <label
                    style={{
                        display: "grid",
                        gap: "6px"
                    }}
                >
                    商品關鍵字

                    <input
                        id="product-keyword"
                        name="keyword"
                        type="search"
                        placeholder="例如：滑鼠"
                        value={keyword}
                        onChange={event =>
                            setKeyword(event.target.value)
                        }
                        style={inputStyle}
                    />
                </label>

                <label
                    style={{
                        display: "grid",
                        gap: "6px"
                    }}
                >
                    最低價格

                    <input
                        name="minPrice"
                        type="number"
                        min="0"
                        step="any"
                        placeholder="不限"
                        value={minPrice}
                        onChange={event =>
                            setMinPrice(event.target.value)
                        }
                        style={inputStyle}
                    />
                </label>

                <label
                    style={{
                        display: "grid",
                        gap: "6px"
                    }}
                >
                    最高價格

                    <input
                        name="maxPrice"
                        type="number"
                        min="0"
                        step="any"
                        placeholder="不限"
                        value={maxPrice}
                        onChange={event =>
                            setMaxPrice(event.target.value)
                        }
                        style={inputStyle}
                    />
                </label>

                <button
                    type="submit"
                    className="primary-button"
                >
                    🔍 搜尋商品
                </button>

                <button
                    type="button"
                    onClick={() => navigate("/products")}
                    style={{
                        padding: "10px 16px",
                        borderRadius: "10px",
                        border: "1px solid #ddd6fe",
                        background: "white",
                        color: "#5b21b6",
                        cursor: "pointer"
                    }}
                >
                    清除全部
                </button>
            </form>

            {(
                appliedKeyword ||
                appliedMin !== "" ||
                appliedMax !== ""
            ) && (
                <div className="filter-info">
                    目前搜尋：
                    {appliedKeyword || "全部商品"}

                    {appliedMin !== "" &&
                        ` ／ 最低 NT$ ${appliedMin}`}

                    {appliedMax !== "" &&
                        ` ／ 最高 NT$ ${appliedMax}`}
                </div>
            )}

            {formError && (
                <p
                    role="alert"
                    style={{
                        textAlign: "center",
                        color: "#dc2626"
                    }}
                >
                    {formError}
                </p>
            )}

            {message && (
                <p
                    role="status"
                    style={{
                        textAlign: "center",
                        color: "#dc2626"
                    }}
                >
                    {message}
                </p>
            )}

            {loading && (
                <p
                    role="status"
                    style={{ textAlign: "center" }}
                >
                    商品資料載入中...
                </p>
            )}

            {!loading && (
                <div className="product-grid">
                    {currentProducts.map(product => (
                        <div
                            className="product-card"
                            key={product.id}
                        >
                            <div className="product-image-area">
                                <img
                                    className="product-image"
                                    src={
                                        product.image ||
                                        "/no-image.png"
                                    }
                                    alt={product.name}
                                />
                            </div>

                            <div className="category-badge">
                                {product.category}
                            </div>

                            <h2 className="product-name">
                                {product.name}
                            </h2>

                            <p className="product-description">
                                {product.description}
                            </p>

                            <div className="product-price">
                                NT$ {
                                    Number(product.price)
                                        .toLocaleString("zh-TW")
                                }
                            </div>

                            <div
                                className={
                                    product.stock <= 0
                                        ? "stock-out"
                                        : product.stock <= 10
                                            ? "stock-low"
                                            : "stock-normal"
                                }
                            >
                                {product.stock > 0
                                    ? `庫存：${product.stock}`
                                    : "目前缺貨"}
                            </div>

                            <div
                                style={{
                                    marginTop: "8px",
                                    color: "#6b7280",
                                    fontSize: "14px"
                                }}
                            >
                                🔥 累積銷售：
                                {product.salesCount ?? 0}
                            </div>

                            <button
                                type="button"
                                className="recent-view-button"
                                onClick={() =>
                                    handleViewProduct(product)
                                }
                            >
                                🕘 查看商品
                            </button>

                            <div className="product-action-area">
                                {Number(product.stock) > 0 && (
                                    <button
                                        type="button"
                                        className="primary-button"
                                        onClick={() =>
                                            addToCart(product)
                                        }
                                    >
                                        🛒 加入購物車
                                    </button>
                                )}

                                <button
                                    type="button"
                                    className="stock-notification-button"
                                    disabled={
                                        notificationLoadingId !== null
                                    }
                                    onClick={() =>
                                        handleStockNotification(product)
                                    }
                                >
                                    {notificationLoadingId === product.id
                                        ? "設定中..."
                                        : "🔔 到貨通知"}
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {!loading && totalPages > 1 && (
                <div
                    style={{
                        display: "flex",
                        justifyContent: "center",
                        gap: "8px",
                        margin: "30px 0",
                        flexWrap: "wrap"
                    }}
                >
                    <button
                        type="button"
                        className="primary-button"
                        disabled={currentPage === 1}
                        onClick={() =>
                            goToPage(currentPage - 1)
                        }
                    >
                        上一頁
                    </button>

                    {Array.from(
                        { length: totalPages },
                        (_, index) => index + 1
                    ).map(page => (
                        <button
                            type="button"
                            key={page}
                            onClick={() => goToPage(page)}
                            aria-current={
                                currentPage === page
                                    ? "page"
                                    : undefined
                            }
                            style={{
                                padding: "10px 14px",
                                borderRadius: "10px",
                                border: "1px solid #ddd6fe",
                                cursor: "pointer",
                                background:
                                    currentPage === page
                                        ? "#7c3aed"
                                        : "white",
                                color:
                                    currentPage === page
                                        ? "white"
                                        : "#5b21b6"
                            }}
                        >
                            {page}
                        </button>
                    ))}

                    <button
                        type="button"
                        className="primary-button"
                        disabled={currentPage === totalPages}
                        onClick={() =>
                            goToPage(currentPage + 1)
                        }
                    >
                        下一頁
                    </button>
                </div>
            )}
        </div>
    );
}