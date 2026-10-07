import {
    useEffect,
    useState
} from "react";

import {
    useNavigate
} from "react-router-dom";


function Home() {

    const navigate =
        useNavigate();


    // ==========================================
    // 商品
    // ==========================================

    const [
        products,
        setProducts
    ] = useState([]);


    // ==========================================
    // 瀏覽 / 登入累計人次
    //
    // 預設先顯示 10000
    // 等後端 API 回來後更新
    // ==========================================

    const [
        visitCount,
        setVisitCount
    ] = useState(10000);


    // ==========================================
    // 讀取商品
    // ==========================================

    useEffect(() => {

        fetch(
            "http://localhost:8080/api/products"
        )

            .then(response => {

                if (!response.ok) {

                    throw new Error(
                        "商品讀取失敗"
                    );
                }


                return response.json();
            })

            .then(data => {

                if (
                    Array.isArray(data)
                ) {

                    setProducts(
                        data
                    );
                }
            })

            .catch(error => {

                console.error(
                    "首頁商品讀取失敗：",
                    error
                );
            });

    }, []);


    // ==========================================
    // ★ 查詢目前累計人次
    //
    // 注意：
    //
    // 現在首頁只使用 GET
    //
    // GET
    // /api/stats/visits
    //
    // 首頁本身不再 +1
    //
    // 真正的 +1 已經移到 Login.jsx
    //
    // 只有登入成功後：
    //
    // POST
    // /api/stats/visit
    //
    // 才會 +1
    // ==========================================

    useEffect(() => {

        fetch(
            "http://localhost:8080/api/stats/visits",
            {
                method:
                    "GET"
            }
        )

            .then(response => {

                if (!response.ok) {

                    throw new Error(
                        "瀏覽人數讀取失敗"
                    );
                }


                return response.json();
            })

            .then(data => {

                console.log(
                    "目前累計瀏覽人數：",
                    data.totalVisits
                );


                if (
                    data.totalVisits !== null
                    &&
                    data.totalVisits !== undefined
                ) {

                    setVisitCount(
                        data.totalVisits
                    );
                }
            })

            .catch(error => {

                console.error(
                    "瀏覽人數 API 錯誤：",
                    error
                );
            });

    }, []);


    // ==========================================
    // 首頁上方商品
    //
    // 取前 3 個商品顯示
    // ==========================================

    const heroProducts =
        products.slice(
            0,
            3
        );


    // ==========================================
    // 商品分類
    // ==========================================

    const categories = [

        {
            icon: "🖱️",
            name: "滑鼠",
            keyword: "滑鼠"
        },

        {
            icon: "⌨️",
            name: "鍵盤",
            keyword: "鍵盤"
        },

        {
            icon: "🔌",
            name: "USB HUB",
            keyword: "集線器"
        },

        {
            icon: "🎧",
            name: "耳機",
            keyword: "耳機"
        },

        {
            icon: "🔋",
            name: "旅充",
            keyword: "充電器"
        },

        {
            icon: "🖥️",
            name: "更多周邊",
            keyword: ""
        }
    ];


    // ==========================================
    // 點商品分類
    // ==========================================

    const openCategory =
        keyword => {

            if (!keyword) {

                navigate(
                    "/products"
                );

                return;
            }


            navigate(
                `/products?keyword=${encodeURIComponent(keyword)}`
            );
        };


    // ==========================================
    // JSX
    //
    // ★ 以下畫面完全維持原本版面
    // ==========================================

    return (

        <div className="home-page">


            {/* ==================================
                HERO
               ================================== */}

            <section className="hero-section">


                {/* ==================================
                    背景裝飾圓圈
                   ================================== */}

                <div
                    className=
                        "hero-circle hero-circle-one"
                />


                <div
                    className=
                        "hero-circle hero-circle-two"
                />


                <div
                    className=
                        "hero-circle hero-circle-three"
                />


                {/* ==================================
                    上方商品圖片
                   ================================== */}

                <div className="hero-products">


                    {
                        heroProducts.map(
                            (
                                product,
                                index
                            ) => (

                                <div
                                    className=
                                        "hero-product-box"

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

                                        className={

                                            index === 0

                                                ?

                                                "hero-product-image hero-product-laptop"

                                                :

                                                index === 1

                                                    ?

                                                    "hero-product-image hero-product-mouse"

                                                    :

                                                    "hero-product-image hero-product-keyboard"
                                        }

                                    />


                                </div>
                            )
                        )
                    }


                </div>


                {/* ==================================
                    Hero 文字
                   ================================== */}

                <div className="hero-content">


                    <h1 className="hero-title">

                        歡迎來到{" "}


                        <span
                            className=
                                "hero-title-highlight"
                        >

                            MyPC 商城

                        </span>

                    </h1>


                    <p className="hero-description">

                        精選電腦周邊商品，
                        從滑鼠、鍵盤、USB HUB、
                        耳機到旅充，

                        <br />

                        輕鬆找到適合您的 3C 好物。

                    </p>


                    {/* ==================================
                        累計瀏覽人數
                       ================================== */}

                    <div className="visit-counter">

                        👀 本站累計瀏覽人數


                        <strong>

                            {
                                Number(
                                    visitCount
                                ).toLocaleString(
                                    "zh-TW"
                                )
                            }

                        </strong>


                        人次

                    </div>


                    {/* ==================================
                        開始逛商品
                       ================================== */}

                    <button
                        type="button"

                        className=
                            "hero-main-button"

                        onClick={
                            () =>
                                navigate(
                                    "/products"
                                )
                        }
                    >

                        🛒 開始逛商品 ❯

                    </button>


                    <div className="hero-slogan">

                        ─── 品質・價格・服務 ───

                    </div>


                </div>


            </section>


            {/* ==================================
                四大特色
               ================================== */}

            <section className="feature-container">


                {/* ==================================
                    快速出貨
                   ================================== */}

                <div className="feature-card">


                    <div
                        className=
                            "feature-icon feature-red"
                    >

                        🚚

                    </div>


                    <div>


                        <h3 className="feature-title">

                            快速出貨

                        </h3>


                        <p className="feature-text">

                            下單後盡快為您出貨

                        </p>


                    </div>


                </div>


                {/* ==================================
                    安全付款
                   ================================== */}

                <div className="feature-card">


                    <div
                        className=
                            "feature-icon feature-green"
                    >

                        🛡️

                    </div>


                    <div>


                        <h3 className="feature-title">

                            安全付款

                        </h3>


                        <p className="feature-text">

                            多元付款方式更安心

                        </p>


                    </div>


                </div>


                {/* ==================================
                    專業客服
                   ================================== */}

                <div className="feature-card">


                    <div
                        className=
                            "feature-icon feature-blue"
                    >

                        🎧

                    </div>


                    <div>


                        <h3 className="feature-title">

                            專業客服

                        </h3>


                        <p className="feature-text">

                            有問題隨時為您服務

                        </p>


                    </div>


                </div>


                {/* ==================================
                    精選好物
                   ================================== */}

                <div className="feature-card">


                    <div
                        className=
                            "feature-icon feature-yellow"
                    >

                        ⭐

                    </div>


                    <div>


                        <h3 className="feature-title">

                            精選好物

                        </h3>


                        <p className="feature-text">

                            嚴選高品質 3C 周邊

                        </p>


                    </div>


                </div>


            </section>


            {/* ==================================
                熱門商品分類
               ================================== */}

            <section className="category-section">


                {/* ==================================
                    分類標題
                   ================================== */}

                <div className="category-header">


                    <div className="category-heading">


                        <h2>

                            🖥️ 熱門商品分類

                        </h2>


                        <p>

                            選擇分類，直接查看對應商品

                        </p>


                    </div>


                    <button
                        type="button"

                        className=
                            "view-all-button"

                        onClick={
                            () =>
                                navigate(
                                    "/products"
                                )
                        }
                    >

                        瀏覽全部商品 ❯

                    </button>


                </div>


                {/* ==================================
                    六個分類
                   ================================== */}

                <div className="category-grid">


                    {
                        categories.map(
                            category => (

                                <div
                                    className=
                                        "category-card"

                                    key={
                                        category.name
                                    }

                                    onClick={
                                        () =>
                                            openCategory(
                                                category.keyword
                                            )
                                    }
                                >


                                    <div
                                        className=
                                            "category-icon"
                                    >

                                        {
                                            category.icon
                                        }

                                    </div>


                                    <div
                                        className=
                                            "category-name"
                                    >

                                        {
                                            category.name
                                        }

                                    </div>


                                    <div
                                        className=
                                            "category-subtitle"
                                    >

                                        查看商品

                                    </div>


                                </div>
                            )
                        )
                    }


                </div>


            </section>


        </div>
    );
}


export default Home;