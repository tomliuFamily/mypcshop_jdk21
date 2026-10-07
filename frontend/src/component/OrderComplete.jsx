import {
    useEffect,
    useState
} from "react";

import {
    useLocation,
    useNavigate
} from "react-router-dom";


function OrderComplete() {

    const location = useLocation();
    const navigate = useNavigate();

    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);

    // ==========================================
    // 取得訂單編號
    // ==========================================

    const orderId = location.state?.orderId;


    // ==========================================
    // 取得 Token
    // 同時支援 localStorage 與 sessionStorage
    // ==========================================

    const getStoredToken = () => {

        let storedToken =
            localStorage.getItem("token")
            ||
            sessionStorage.getItem("token")
            ||
            localStorage.getItem("jwtToken")
            ||
            sessionStorage.getItem("jwtToken")
            ||
            localStorage.getItem("accessToken")
            ||
            sessionStorage.getItem("accessToken")
            ||
            localStorage.getItem("authToken")
            ||
            sessionStorage.getItem("authToken");


        if (!storedToken) {
            return null;
        }


        // 如果 Token 被 JSON.stringify() 儲存，
        // 去除外層雙引號
        try {

            if (
                storedToken.startsWith('"')
                &&
                storedToken.endsWith('"')
            ) {

                storedToken = JSON.parse(storedToken);
            }

        } catch (error) {

            console.warn(
                "Token JSON 解析失敗：",
                error
            );
        }


        // 如果儲存內容已經包含 Bearer，
        // 避免送出 Bearer Bearer xxxxx
        if (storedToken.startsWith("Bearer ")) {

            storedToken = storedToken.substring(7);
        }


        return storedToken;
    };


    // ==========================================
    // 清除登入資料
    // ==========================================

    const clearLoginData = () => {

        const loginKeys = [
            "token",
            "jwtToken",
            "accessToken",
            "authToken",
            "user",
            "isLoggedIn"
        ];


        loginKeys.forEach((key) => {

            localStorage.removeItem(key);
            sessionStorage.removeItem(key);
        });
    };


    // ==========================================
    // 沒有 orderId
    // ==========================================

    useEffect(() => {

        if (!orderId) {

            setMessage("找不到訂單編號");
        }

    }, [orderId]);


    // ==========================================
    // 處理登入失效
    // ==========================================

    const handleLoginExpired = () => {

        clearLoginData();

        setMessage(
            "登入資料已失效，請重新登入"
        );


        setTimeout(() => {

            navigate(
                "/login",
                {
                    replace: true
                }
            );

        }, 1200);
    };


    // ==========================================
    // 取得訂單 PDF Blob
    // ==========================================

    const getOrderPdf = async () => {

        if (!orderId) {

            throw new Error(
                "找不到訂單編號"
            );
        }


        // 每次呼叫 API 時重新讀取 Token
        const token = getStoredToken();


        console.log(
            "訂單 PDF 是否取得 Token：",
            Boolean(token)
        );


        if (!token) {

            const error =
                new Error(
                    "登入資料已失效，請重新登入"
                );

            error.isLoginExpired = true;

            throw error;
        }


        const response = await fetch(

            `http://localhost:8080/api/orders/${orderId}/receipt`,

            {
                method: "GET",

                headers: {
                    Accept: "application/pdf",

                    Authorization:
                        `Bearer ${token}`
                }
            }
        );


        console.log(
            "訂單 PDF HTTP 狀態：",
            response.status
        );


        // ======================================
        // JWT 無效或沒有權限
        // ======================================

        if (
            response.status === 401
            ||
            response.status === 403
        ) {

            const error =
                new Error(
                    `登入驗證失敗，HTTP ${response.status}`
                );

            error.isLoginExpired = true;

            throw error;
        }


        // ======================================
        // 其他後端錯誤
        // ======================================

        if (!response.ok) {

            const errorText =
                await response.text();


            throw new Error(
                errorText
                ||
                `產生 PDF 失敗，HTTP ${response.status}`
            );
        }


        // ======================================
        // 確認後端回傳 PDF
        // ======================================

        const contentType =
            response.headers.get(
                "content-type"
            )
            ||
            "";


        console.log(
            "PDF Content-Type：",
            contentType
        );


        if (
            !contentType
                .toLowerCase()
                .includes("application/pdf")
        ) {

            const responseText =
                await response.text();


            throw new Error(
                responseText
                ||
                `後端沒有回傳 PDF，Content-Type：${contentType}`
            );
        }


        const blob =
            await response.blob();


        console.log(
            "PDF Blob size：",
            blob.size
        );


        if (blob.size === 0) {

            throw new Error(
                "後端回傳的 PDF 是空檔案"
            );
        }


        return blob;
    };


    // ==========================================
    // 列印／開啟 PDF
    // ==========================================

    const handlePrintOrder = async () => {

        if (loading) {
            return;
        }


        // 必須在使用者按按鈕時先開視窗，
        // 避免 Chrome 阻擋彈出式視窗
        const pdfWindow =
            window.open(
                "",
                "_blank"
            );


        if (!pdfWindow) {

            alert(
                "瀏覽器阻擋了 PDF 視窗，請允許 localhost 的彈出式視窗。"
            );

            return;
        }


        try {

            setLoading(true);

            setMessage(
                "正在產生訂單 PDF..."
            );


            const pdfBlob =
                await getOrderPdf();


            const pdfUrl =
                URL.createObjectURL(
                    pdfBlob
                );


            pdfWindow.location.href =
                pdfUrl;


            setMessage(
                "訂單 PDF 已開啟，可以使用瀏覽器的列印功能"
            );


            // 不要立刻釋放，避免 PDF 尚未載入
            setTimeout(() => {

                URL.revokeObjectURL(
                    pdfUrl
                );

            }, 60000);


        } catch (error) {

            console.error(
                "開啟 PDF 失敗：",
                error
            );


            pdfWindow.close();


            if (error.isLoginExpired) {

                handleLoginExpired();

            } else {

                setMessage(
                    error.message
                    ||
                    "開啟訂單 PDF 失敗"
                );
            }


        } finally {

            setLoading(false);
        }
    };


    // ==========================================
    // 下載 PDF
    // ==========================================

    const handleDownloadPdf = async () => {

        if (loading) {
            return;
        }


        try {

            setLoading(true);

            setMessage(
                "正在下載訂單 PDF..."
            );


            const pdfBlob =
                await getOrderPdf();


            const pdfUrl =
                URL.createObjectURL(
                    pdfBlob
                );


            const link =
                document.createElement(
                    "a"
                );


            link.href = pdfUrl;

            link.download =
                `order_${orderId}.pdf`;


            document.body.appendChild(
                link
            );


            link.click();


            document.body.removeChild(
                link
            );


            // 稍後再釋放下載網址
            setTimeout(() => {

                URL.revokeObjectURL(
                    pdfUrl
                );

            }, 1000);


            setMessage(
                `訂單 PDF 已下載：order_${orderId}.pdf`
            );


        } catch (error) {

            console.error(
                "下載 PDF 失敗：",
                error
            );


            if (error.isLoginExpired) {

                handleLoginExpired();

            } else {

                setMessage(
                    error.message
                    ||
                    "下載訂單 PDF 失敗"
                );
            }


        } finally {

            setLoading(false);
        }
    };


    // ==========================================
    // 繼續購物
    // ==========================================

    const handleContinueShopping = () => {

        navigate("/products");
    };


    // ==========================================
    // 共用按鈕樣式
    // ==========================================

    const buttonStyle = {

        minWidth: "175px",
        padding: "17px 24px",
        border: "1px solid #777",
        borderRadius: "3px",
        background: "#f7f7f7",
        color: "#111",
        fontSize: "21px",

        cursor:
            loading
                ?
                "not-allowed"
                :
                "pointer"
    };


    // ==========================================
    // JSX
    // ==========================================

    return (

        <div className="page-container">

            <div
                style={{
                    maxWidth: "650px",
                    margin: "0 auto",
                    padding: "55px 45px",
                    border: "1px solid #d8d8d8",
                    borderRadius: "20px",
                    background: "white",
                    textAlign: "center"
                }}
            >

                <h1
                    style={{
                        margin: "0 0 25px",
                        color: "#050505",
                        fontSize: "72px",
                        fontWeight: "400"
                    }}
                >
                    訂單完成
                </h1>


                <p
                    style={{
                        margin: "0 0 5px",
                        color: "#123578",
                        fontSize: "29px"
                    }}
                >
                    感謝您的訂購！
                </p>


                <p
                    style={{
                        margin: "0 0 45px",
                        color: "#123578",
                        fontSize: "28px"
                    }}
                >
                    訂單編號：

                    <strong
                        style={{
                            marginLeft: "8px",
                            fontSize: "31px"
                        }}
                    >
                        {
                            orderId
                            ||
                            "—"
                        }
                    </strong>
                </p>


                <div
                    style={{
                        display: "flex",
                        justifyContent: "center",
                        flexWrap: "wrap",
                        gap: "22px"
                    }}
                >

                    <button
                        type="button"
                        onClick={handlePrintOrder}

                        disabled={
                            loading
                            ||
                            !orderId
                        }

                        style={buttonStyle}
                    >
                        {
                            loading
                                ?
                                "處理中..."
                                :
                                "列印訂單"
                        }
                    </button>


                    <button
                        type="button"
                        onClick={handleDownloadPdf}

                        disabled={
                            loading
                            ||
                            !orderId
                        }

                        style={buttonStyle}
                    >
                        {
                            loading
                                ?
                                "處理中..."
                                :
                                "下載 PDF"
                        }
                    </button>


                    <button
                        type="button"
                        onClick={handleContinueShopping}

                        style={{
                            ...buttonStyle,
                            cursor: "pointer"
                        }}
                    >
                        繼續購物
                    </button>

                </div>


                {
                    message
                    && (

                        <p
                            style={{
                                marginTop: "35px",
                                marginBottom: "0",

                                color:
                                    message.includes("失敗")
                                    ||
                                    message.includes("找不到")
                                    ||
                                    message.includes("失效")
                                    ||
                                    message.includes("錯誤")
                                        ?
                                        "#dc2626"
                                        :
                                        "#123578",

                                fontSize: "22px"
                            }}
                        >
                            {message}
                        </p>
                    )
                }

            </div>

        </div>
    );
}


export default OrderComplete;