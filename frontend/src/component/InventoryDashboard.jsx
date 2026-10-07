import { useEffect, useState } from "react";

function InventoryDashboard() {

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // ==============================
    // 讀取商品資料
    // ==============================
    useEffect(() => {

        const loadProducts = async () => {

            try {

                setLoading(true);
                setError("");

                const response = await fetch(
                    "http://localhost:8080/api/products"
                );

                if (!response.ok) {
                    throw new Error("商品資料讀取失敗");
                }

                const data = await response.json();

                setProducts(data);

            } catch (error) {

                console.error("庫存資料讀取失敗：", error);

                setError(
                    "無法取得庫存資料，請確認 Spring Boot 是否已啟動"
                );

            } finally {

                setLoading(false);
            }
        };

        loadProducts();

    }, []);


    // ==============================
    // 判斷庫存狀態
    // ==============================
    const getStockStatus = (stock) => {

        if (stock <= 20) {

            return {
                text: "庫存不足",
                icon: "🔴",
                className: "inventory-danger"
            };
        }

        if (stock <= 50) {

            return {
                text: "庫存偏低",
                icon: "🟡",
                className: "inventory-warning"
            };
        }

        return {
            text: "庫存正常",
            icon: "🟢",
            className: "inventory-normal"
        };
    };


    // ==============================
    // Dashboard 統計
    // ==============================

    const totalProducts = products.length;

    const dangerCount = products.filter(
        product => product.stock <= 20
    ).length;

    const warningCount = products.filter(
        product =>
            product.stock > 20 &&
            product.stock <= 50
    ).length;

    const normalCount = products.filter(
        product => product.stock > 50
    ).length;


    // ==============================
    // Loading
    // ==============================

    if (loading) {

        return (
            <div className="inventory-dashboard-page">

                <div className="inventory-dashboard-container">

                    <h2>📊 庫存管理 Dashboard</h2>

                    <p>庫存資料載入中...</p>

                </div>

            </div>
        );
    }


    return (

        <div className="inventory-dashboard-page">

            <div className="inventory-dashboard-container">

                <h2>
                    📊 庫存管理 Dashboard
                </h2>

                <p className="inventory-dashboard-subtitle">
                    MyPC 商城商品庫存即時管理
                </p>


                {/* ==========================
                    錯誤訊息
                   ========================== */}

                {error && (

                    <div className="inventory-error-message">
                        ⚠️ {error}
                    </div>

                )}


                {/* ==========================
                    Dashboard 統計卡片
                   ========================== */}

                <div className="inventory-summary">

                    <div className="inventory-summary-card">

                        <div className="inventory-summary-icon">
                            📦
                        </div>

                        <div className="inventory-summary-number">
                            {totalProducts}
                        </div>

                        <div className="inventory-summary-title">
                            商品總數
                        </div>

                    </div>


                    <div className="inventory-summary-card">

                        <div className="inventory-summary-icon">
                            🔴
                        </div>

                        <div className="inventory-summary-number">
                            {dangerCount}
                        </div>

                        <div className="inventory-summary-title">
                            庫存不足
                        </div>

                    </div>


                    <div className="inventory-summary-card">

                        <div className="inventory-summary-icon">
                            🟡
                        </div>

                        <div className="inventory-summary-number">
                            {warningCount}
                        </div>

                        <div className="inventory-summary-title">
                            庫存偏低
                        </div>

                    </div>


                    <div className="inventory-summary-card">

                        <div className="inventory-summary-icon">
                            🟢
                        </div>

                        <div className="inventory-summary-number">
                            {normalCount}
                        </div>

                        <div className="inventory-summary-title">
                            庫存正常
                        </div>

                    </div>

                </div>


                {/* ==========================
                    庫存標準
                   ========================== */}

                <div className="inventory-rule">

                    <strong>庫存判斷標準：</strong>

                    <span>🔴 20 件以下：庫存不足</span>

                    <span>🟡 21～50 件：庫存偏低</span>

                    <span>🟢 51 件以上：庫存正常</span>

                </div>


                {/* ==========================
                    商品庫存 Table
                   ========================== */}

                <div className="inventory-table-wrapper">

                    <table className="inventory-table">

                        <thead>

                            <tr>

                                <th>ID</th>

                                <th>商品名稱</th>

                                <th>商品分類</th>

                                <th>售價</th>

                                <th>目前庫存</th>

                                <th>庫存狀態</th>

                            </tr>

                        </thead>


                        <tbody>

                            {products.map(product => {

                                const status =
                                    getStockStatus(product.stock);

                                return (

                                    <tr key={product.id}>

                                        <td>
                                            {product.id}
                                        </td>

                                        <td>
                                            <strong>
                                                {product.name}
                                            </strong>
                                        </td>

                                        <td>
                                            {product.category}
                                        </td>

                                        <td>
                                            NT$ {Number(
                                                product.price
                                            ).toLocaleString()}
                                        </td>

                                        <td>
                                            {product.stock}
                                        </td>

                                        <td>

                                            <span
                                                className={
                                                    `inventory-status ${status.className}`
                                                }
                                            >

                                                {status.icon}

                                                {" "}

                                                {status.text}

                                            </span>

                                        </td>

                                    </tr>

                                );

                            })}

                        </tbody>

                    </table>

                </div>

            </div>

        </div>
    );
}

export default InventoryDashboard;