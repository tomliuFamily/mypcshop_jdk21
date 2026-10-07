import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";

export default function Navbar({ cartCount = 0 }) {
    const navigate = useNavigate();
    const location = useLocation();

    const [searchKeyword, setSearchKeyword] = useState("");

    const isProductPage = location.pathname === "/products";

    const isLoggedIn =
        sessionStorage.getItem("isLoggedIn") === "true";

    let user = null;

    try {
        user = JSON.parse(
            sessionStorage.getItem("user") || "null"
        );
    } catch (error) {
        console.error("會員資料解析失敗：", error);
    }

    const userEmail = String(user?.email || "")
        .trim()
        .toLowerCase();

    const userName = String(user?.name || "").trim();

    const isAdmin =
        isLoggedIn &&
        (
            userEmail === "admin@example.com" ||
            userName === "管理員"
        );

    // 其他頁面的搜尋：先進入商品頁，
    // 商品頁可以繼續設定價格並一起搜尋。
    function handleSearch(event) {
        event.preventDefault();

        const params = new URLSearchParams();
        const keyword = searchKeyword.trim();

        if (keyword) {
            params.set("keyword", keyword);
        }

        navigate(
            params.size
                ? `/products?${params}`
                : "/products"
        );
    }

    // 商品頁使用同一組關鍵字與價格搜尋表單。
    function focusProductSearch() {
        const input =
            document.getElementById("product-keyword");

        input?.scrollIntoView({
            behavior: "smooth",
            block: "center"
        });

        input?.focus({
            preventScroll: true
        });
    }

    function handleLogout() {
        [
            "accessToken",
            "refreshToken",
            "token",
            "user",
            "isLoggedIn"
        ].forEach(key => {
            sessionStorage.removeItem(key);
        });

        window.dispatchEvent(
            new Event("loginStatusChanged")
        );

        alert("已成功登出");
        navigate("/login");
    }

    return (
        <nav className="navbar">
            <div className="navbar-inner">
                <Link to="/" className="nav-logo">
                    🛍️ MyPC 商城
                </Link>

                <Link to="/" className="nav-link">
                    🏠 首頁
                </Link>

                <Link to="/products" className="nav-link">
                    🖥️ 商品專區
                </Link>

                <Link to="/messages" className="nav-link">
                    💬 留言板
                </Link>

                <Link to="/cart" className="nav-link">
                    🛒 購物車 ({cartCount})
                </Link>

                {isProductPage ? (
                    <button
                        type="button"
                        className="search-button"
                        onClick={focusProductSearch}
                    >
                        🔍 搜尋商品／價格
                    </button>
                ) : (
                    <form
                        className="nav-search"
                        onSubmit={handleSearch}
                    >
                        <span
                            className="nav-search-icon"
                            aria-hidden="true"
                        >
                            🔍
                        </span>

                        <input
                            type="search"
                            aria-label="搜尋商品"
                            placeholder="搜尋商品..."
                            value={searchKeyword}
                            onChange={event =>
                                setSearchKeyword(
                                    event.target.value
                                )
                            }
                        />

                        <button
                            type="submit"
                            className="search-button"
                        >
                            搜尋
                        </button>
                    </form>
                )}

                {isLoggedIn ? (
                    <>
                        <Link
                            to="/orders"
                            className="nav-link"
                        >
                            📋 我的訂單
                        </Link>

                        <Link
                            to="/member/preferences"
                            className="nav-link"
                        >
                            ⚙️ 我的設定
                        </Link>

                        {isAdmin && (
                            <>
                                <Link
                                    to="/upload-file"
                                    className="nav-link"
                                >
                                    ⚙️ 產品管理
                                </Link>

                                <Link
                                    to="/admin/user-import"
                                    className="nav-link"
                                >
                                    📥 會員匯入
                                </Link>

                                <Link
                                    to="/admin/inventory"
                                    className="nav-link"
                                >
                                    📊 庫存管理
                                </Link>
                            </>
                        )}

                        <span className="nav-user">
                            👤 {
                                user?.name ||
                                user?.email ||
                                "會員"
                            }
                        </span>

                        <button
                            type="button"
                            className="logout-button"
                            onClick={handleLogout}
                        >
                            登出
                        </button>
                    </>
                ) : (
                    <>
                        <Link
                            to="/login"
                            className="nav-link"
                        >
                            👤 登入
                        </Link>

                        <button
                            type="button"
                            className="nav-register-button"
                            onClick={() =>
                                navigate("/create")
                            }
                        >
                            註冊
                        </button>
                    </>
                )}
            </div>
        </nav>
    );
}