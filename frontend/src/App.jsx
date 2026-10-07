import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";

import {
    useEffect,
    useState
} from "react";

import Navbar
    from "./component/Navbar";

import Home
    from "./component/Home";

import Login
    from "./component/Login";

import CreateUser
    from "./component/CreateUser";

import ProductList
    from "./component/ProductList";

import Cart
    from "./component/Cart";

import Payment
    from "./component/Payment";

import OrderList
    from "./component/OrderList";

import OrderComplete
    from "./component/OrderComplete";

import MessageBoard
    from "./component/MessageBoard";

import ProductFileUpload
    from "./component/ProductFileUpload";

// ★ 管理員會員 TXT 匯入
import AdminUserImport
    from "./component/AdminUserImport";

// ★ 庫存管理 Dashboard
import InventoryDashboard
    from "./component/InventoryDashboard";

// ★ 會員設定：最近瀏覽 + 到貨通知
import MemberPreferences
    from "./component/MemberPreferences";

import "./App.css";


function App() {

    // ==========================================
    // 購物車 state
    // ==========================================

    const [cart, setCart] =
        useState(() => {

            try {

                const savedCart =
                    localStorage.getItem(
                        "cart"
                    );


                if (!savedCart) {

                    return [];
                }


                const parsedCart =
                    JSON.parse(
                        savedCart
                    );


                if (
                    Array.isArray(
                        parsedCart
                    )
                ) {

                    return parsedCart;
                }


                return [];


            } catch (error) {

                console.error(
                    "購物車讀取失敗：",
                    error
                );


                return [];
            }
        });


    // ==========================================
    // cart 改變時
    // 存回 localStorage
    // ==========================================

    useEffect(
        () => {

            localStorage.setItem(
                "cart",
                JSON.stringify(
                    cart
                )
            );

        },
        [cart]
    );


    // ==========================================
    // 加入購物車
    // ==========================================

    const addToCart =
        (product) => {

            setCart(
                currentCart => {

                    const existingItem =
                        currentCart.find(
                            item =>
                                item.id ===
                                product.id
                        );


                    // ==================================
                    // 商品已經存在購物車
                    // ==================================

                    if (existingItem) {

                        return currentCart.map(
                            item => {

                                if (
                                    item.id ===
                                    product.id
                                ) {

                                    let newQuantity =
                                        Number(
                                            item.quantity
                                        ) + 1;


                                    // ==================================
                                    // 不可以超過商品庫存
                                    // ==================================

                                    if (
                                        product.stock !== null
                                        &&
                                        product.stock !== undefined
                                        &&
                                        newQuantity >
                                        product.stock
                                    ) {

                                        alert(
                                            `商品「${product.name}」庫存不足，目前最多只能購買 ${product.stock} 件`
                                        );


                                        newQuantity =
                                            product.stock;
                                    }


                                    return {

                                        ...item,

                                        quantity:
                                            newQuantity
                                    };
                                }


                                return item;
                            }
                        );
                    }


                    // ==================================
                    // 新商品加入購物車
                    // ==================================

                    return [

                        ...currentCart,

                        {
                            ...product,
                            quantity: 1
                        }
                    ];
                }
            );
        };


    // ==========================================
    // 修改商品數量
    // ==========================================

    const updateQuantity =
        (
            productId,
            quantity
        ) => {

            setCart(
                currentCart =>
                    currentCart.map(
                        item => {

                            if (
                                item.id ===
                                productId
                            ) {

                                return {

                                    ...item,

                                    quantity:
                                        Number(
                                            quantity
                                        )
                                };
                            }


                            return item;
                        }
                    )
            );
        };


    // ==========================================
    // 移除商品
    // ==========================================

    const removeFromCart =
        (productId) => {

            setCart(
                currentCart =>
                    currentCart.filter(
                        item =>
                            item.id !==
                            productId
                    )
            );
        };


    // ==========================================
    // 清空購物車
    // ==========================================

    const clearCart =
        () => {

            setCart([]);
        };


    // ==========================================
    // Navbar 購物車數量
    // ==========================================

    const cartCount =
        cart.reduce(
            (
                total,
                item
            ) => {

                return total
                    +
                    Number(
                        item.quantity || 0
                    );
            },
            0
        );


    // ==========================================
    // JSX
    // ==========================================

    return (

        <BrowserRouter>


            {/* ==================================
                Navbar
            ================================== */}

            <Navbar
                cartCount={
                    cartCount
                }
            />


            {/* ==================================
                所有 Route
            ================================== */}

            <Routes>


                {/* ==================================
                    首頁
                ================================== */}

                <Route
                    path="/"
                    element={
                        <Home />
                    }
                />


                {/* ==================================
                    登入
                ================================== */}

                <Route
                    path="/login"
                    element={
                        <Login />
                    }
                />


                {/* ==================================
                    註冊
                ================================== */}

                <Route
                    path="/create"
                    element={
                        <CreateUser />
                    }
                />


                {/* ==================================
                    商品專區
                ================================== */}

                <Route
                    path="/products"
                    element={
                        <ProductList
                            addToCart={
                                addToCart
                            }
                        />
                    }
                />


                {/* ==================================
                    購物車
                ================================== */}

                <Route
                    path="/cart"
                    element={
                        <Cart
                            cart={
                                cart
                            }

                            updateQuantity={
                                updateQuantity
                            }

                            removeFromCart={
                                removeFromCart
                            }

                            clearCart={
                                clearCart
                            }
                        />
                    }
                />


                {/* ==================================
                    付款
                ================================== */}

                <Route
                    path="/payment/:orderId"
                    element={
                        <Payment />
                    }
                />


                {/* ==================================
                    我的訂單
                ================================== */}

                <Route
                    path="/orders"
                    element={
                        <OrderList />
                    }
                />


                {/* ==================================
                    訂單完成
                ================================== */}

                <Route
                    path="/order-complete"
                    element={
                        <OrderComplete />
                    }
                />


                {/* ==================================
                    留言板
                ================================== */}

                <Route
                    path="/messages"
                    element={
                        <MessageBoard />
                    }
                />


                {/* ==================================
                    商品檔案上傳
                ================================== */}

                <Route
                    path="/upload-file"
                    element={
                        <ProductFileUpload />
                    }
                />


                {/* ==================================
                    管理員會員 TXT 匯入
                ================================== */}

                <Route
                    path="/admin/user-import"
                    element={
                        <AdminUserImport />
                    }
                />


                {/* ==================================
                    管理員庫存 Dashboard
                ================================== */}

                <Route
                    path="/admin/inventory"
                    element={
                        <InventoryDashboard />
                    }
                />


                {/* ==================================
                    ★ 會員設定

                    功能：
                    1. 最近瀏覽商品
                    2. 到貨通知

                    ★ 注意：
                    一定要放在 Routes 裡面
                ================================== */}

                <Route
                    path="/member/preferences"
                    element={
                        <MemberPreferences
                            addToCart={
                                addToCart
                            }
                        />
                    }
                />


            </Routes>


        </BrowserRouter>
    );
}


export default App;