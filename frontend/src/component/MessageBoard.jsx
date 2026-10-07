import {
    useEffect,
    useRef,
    useState
} from "react";

import {
    Client
} from "@stomp/stompjs";

import {
    useNavigate
} from "react-router-dom";


function MessageBoard() {

    const navigate =
        useNavigate();

    const API_URL =
        "http://localhost:8080/api/messages";


    // ==========================================
    // 留言資料
    // ==========================================

    const [
        messages,
        setMessages
    ] = useState([]);


    // ==========================================
    // 新留言
    // ==========================================

    const [
        content,
        setContent
    ] = useState("");


    // ==========================================
    // 圖片
    // ==========================================

    const [
        selectedFiles,
        setSelectedFiles
    ] = useState([]);

    const [
        previewUrls,
        setPreviewUrls
    ] = useState([]);

    const previewUrlsRef =
        useRef([]);


    // ==========================================
    // 分頁
    // ==========================================

    const [
        currentPage,
        setCurrentPage
    ] = useState(0);

    const currentPageRef =
        useRef(0);

    const [
        totalPages,
        setTotalPages
    ] = useState(0);

    const [
        totalElements,
        setTotalElements
    ] = useState(0);


    // ==========================================
    // 狀態
    // ==========================================

    const [
        loading,
        setLoading
    ] = useState(false);

    const [
        statusMessage,
        setStatusMessage
    ] = useState("");


    // ==========================================
    // 每一則留言自己的回覆文字
    // ==========================================

    const [
        replyContents,
        setReplyContents
    ] = useState({});

    const [
        replyingId,
        setReplyingId
    ] = useState(null);


    // ==========================================
    // Login User
    // ==========================================

    let currentUser = null;

    try {

        const userText =
            sessionStorage.getItem(
                "user"
            );

        if (userText) {

            currentUser =
                JSON.parse(
                    userText
                );
        }

    } catch (error) {

        console.error(
            "會員資料解析失敗：",
            error
        );
    }


    // ==========================================
    // ★ 取得 JWT Access Token
    //
    // Login.jsx 登入成功後存的是：
    // accessToken
    //
    // 舊版錯誤：
    // sessionStorage.getItem("token")
    // ==========================================

    const token =
        sessionStorage.getItem(
            "accessToken"
        );


    const isLoggedIn =
        sessionStorage.getItem(
            "isLoggedIn"
        ) === "true";


    // ==========================================
    // Admin 判斷
    // ==========================================

    const userEmail =
        String(
            currentUser?.email || ""
        )
            .trim()
            .toLowerCase();

    const userName =
        String(
            currentUser?.name || ""
        )
            .trim();

    const isAdmin =
        isLoggedIn
        &&
        (
            userEmail ===
            "admin@example.com"
            ||
            userName ===
            "管理員"
        );


    // ==========================================
    // ★ 清除登入資料
    // ==========================================

    function clearLoginData() {

        sessionStorage.removeItem(
            "accessToken"
        );

        sessionStorage.removeItem(
            "refreshToken"
        );

        // 相容以前版本
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
    }


    // ==========================================
    // ★ JWT 過期處理
    // ==========================================

    function handleUnauthorized() {

        clearLoginData();

        alert(
            "登入已失效，請重新登入"
        );

        navigate(
            "/login"
        );
    }


    // ==========================================
    // 初始化
    // ==========================================

    useEffect(() => {

        loadMessagePage(
            0
        );


        // ======================================
        // WebSocket
        // ======================================

        const client =
            new Client({

                brokerURL:
                    "ws://localhost:8080/ws",

                reconnectDelay:
                    5000
            });


        client.onConnect =
            () => {

                client.subscribe(

                    "/topic/messages",

                    () => {

                        // 新留言或新回覆出現
                        // 重新讀取目前所在頁

                        loadMessagePage(
                            currentPageRef.current
                        );
                    }
                );
            };


        client.activate();


        return () => {

            client.deactivate();

            previewUrlsRef
                .current
                .forEach(

                    url =>

                        URL.revokeObjectURL(
                            url
                        )
                );
        };

    }, []);


    // ==========================================
    // 後端分頁
    // ==========================================

    async function loadMessagePage(
        page
    ) {

        try {

            const response =
                await fetch(

                    `${API_URL}/page?page=${page}`

                );


            if (!response.ok) {

                throw new Error(
                    "留言載入失敗"
                );
            }


            const data =
                await response.json();


            setMessages(
                data.content || []
            );


            const backendPage =
                data.number ?? 0;


            setCurrentPage(
                backendPage
            );


            currentPageRef.current =
                backendPage;


            setTotalPages(
                data.totalPages ?? 0
            );


            setTotalElements(
                data.totalElements ?? 0
            );


        } catch (error) {

            console.error(
                error
            );
        }
    }


    // ==========================================
    // 選圖片
    // ==========================================

    function handleFileChange(
        event
    ) {

        const files =
            Array.from(
                event.target.files
            );


        if (
            files.length > 5
        ) {

            setStatusMessage(
                "每則留言最多 5 張圖片"
            );

            return;
        }


        const validFiles =
            [];


        for (
            const file
            of files
        ) {

            const validType =
                [
                    "image/jpeg",
                    "image/png",
                    "image/webp"
                ]
                    .includes(
                        file.type
                    );


            if (!validType) {

                setStatusMessage(
                    "只允許 JPG、PNG、WEBP"
                );

                return;
            }


            if (
                file.size
                >
                10 * 1024 * 1024
            ) {

                setStatusMessage(
                    "單張圖片不可超過 10MB"
                );

                return;
            }


            validFiles.push(
                file
            );
        }


        // 清除舊 Preview URL

        previewUrlsRef
            .current
            .forEach(

                url =>

                    URL.revokeObjectURL(
                        url
                    )
            );


        const urls =
            validFiles.map(

                file =>

                    URL.createObjectURL(
                        file
                    )
            );


        previewUrlsRef.current =
            urls;


        setSelectedFiles(
            validFiles
        );


        setPreviewUrls(
            urls
        );


        setStatusMessage(
            ""
        );
    }


    // ==========================================
    // 移除其中一張圖片
    // ==========================================

    function removeFile(
        index
    ) {

        URL.revokeObjectURL(
            previewUrls[index]
        );


        const newFiles =
            selectedFiles.filter(

                (_, i) =>
                    i !== index
            );


        const newUrls =
            previewUrls.filter(

                (_, i) =>
                    i !== index
            );


        previewUrlsRef.current =
            newUrls;


        setSelectedFiles(
            newFiles
        );


        setPreviewUrls(
            newUrls
        );
    }


    // ==========================================
    // 清空圖片
    // ==========================================

    function clearFiles() {

        previewUrlsRef
            .current
            .forEach(

                url =>

                    URL.revokeObjectURL(
                        url
                    )
            );


        previewUrlsRef.current =
            [];


        setSelectedFiles(
            []
        );


        setPreviewUrls(
            []
        );
    }


    // ==========================================
    // ★ 發表新的主留言
    // ==========================================

    async function handleSubmit(
        event
    ) {

        event.preventDefault();


        // ======================================
        // ★ 登入檢查
        // ======================================

        if (
            !token
            ||
            !isLoggedIn
        ) {

            alert(
                "請先登入後再留言"
            );

            navigate(
                "/login"
            );

            return;
        }


        // ======================================
        // 留言內容
        // ======================================

        if (
            content.trim()
            ===
            ""
        ) {

            setStatusMessage(
                "請輸入留言內容"
            );

            return;
        }


        try {

            setLoading(
                true
            );


            setStatusMessage(
                ""
            );


            // ==================================
            // FormData
            // ==================================

            const formData =
                new FormData();


            // ==================================
            // 不再傳 userId / userName
            //
            // 後端直接從 JWT Principal
            // 判斷真正登入者
            // ==================================

            formData.append(
                "content",
                content.trim()
            );


            selectedFiles
                .forEach(

                    file => {

                        formData.append(
                            "files",
                            file
                        );
                    }
                );


            // ==================================
            // ★ 發表留言 API
            // ==================================

            const response =
                await fetch(

                    `${API_URL}/upload`,

                    {

                        method:
                            "POST",

                        headers: {

                            // ★ 使用 accessToken
                            Authorization:
                                `Bearer ${token}`
                        },

                        body:
                            formData
                    }
                );


            // ==================================
            // JWT 過期
            // ==================================

            if (
                response.status === 401
            ) {

                handleUnauthorized();

                return;
            }


            if (
                response.status === 403
            ) {

                const text =
                    await response.text();


                throw new Error(
                    text
                    ||
                    "沒有留言權限"
                );
            }


            if (!response.ok) {

                const text =
                    await response.text();


                throw new Error(
                    text
                    ||
                    "留言送出失敗"
                );
            }


            // ==================================
            // 留言成功
            // ==================================

            setContent(
                ""
            );


            clearFiles();


            setStatusMessage(
                "✅ 留言發表成功"
            );


            currentPageRef.current =
                0;


            await loadMessagePage(
                0
            );


        } catch (error) {

            console.error(
                error
            );


            setStatusMessage(
                error.message
            );


        } finally {

            setLoading(
                false
            );
        }
    }


    // ==========================================
    // 修改回覆輸入框
    // ==========================================

    function handleReplyChange(
        messageId,
        value
    ) {

        setReplyContents(

            previous => ({

                ...previous,

                [messageId]:
                    value
            })
        );
    }


    // ==========================================
    // ★ 判斷目前會員是否可以回覆
    // ==========================================

    function canReply(
        item
    ) {

        if (
            !isLoggedIn
            ||
            !currentUser
        ) {

            return false;
        }


        // 管理員全部可以

        if (isAdmin) {

            return true;
        }


        // 一般會員只能回自己的主留言

        return Number(
            item.userId
        )
            ===
            Number(
                currentUser.id
            );
    }


    // ==========================================
    // ★ 雙向送出回覆
    // ==========================================

    async function handleReply(
        messageId
    ) {

        // ======================================
        // 登入檢查
        // ======================================

        if (
            !token
            ||
            !isLoggedIn
        ) {

            alert(
                "請先登入"
            );

            navigate(
                "/login"
            );

            return;
        }


        const replyContent =
            String(

                replyContents[
                    messageId
                ]
                ||
                ""

            )
                .trim();


        if (
            replyContent === ""
        ) {

            alert(
                "請輸入回覆內容"
            );

            return;
        }


        try {

            setReplyingId(
                messageId
            );


            // ==================================
            // Reply API
            // ==================================

            const response =
                await fetch(

                    `${API_URL}/${messageId}/reply`,

                    {

                        method:
                            "POST",

                        headers: {

                            "Content-Type":
                                "application/json",

                            // ★ 使用 accessToken
                            Authorization:
                                `Bearer ${token}`
                        },

                        body:
                            JSON.stringify({

                                content:
                                    replyContent
                            })
                    }
                );


            // ==================================
            // JWT 過期
            // ==================================

            if (
                response.status
                ===
                401
            ) {

                handleUnauthorized();

                return;
            }


            if (
                response.status
                ===
                403
            ) {

                const text =
                    await response.text();


                throw new Error(
                    text
                    ||
                    "你沒有權限回覆這則留言"
                );
            }


            if (!response.ok) {

                const text =
                    await response.text();


                throw new Error(
                    text
                    ||
                    "回覆失敗"
                );
            }


            // ==================================
            // 清空該留言 Reply
            // ==================================

            setReplyContents(

                previous => ({

                    ...previous,

                    [messageId]:
                        ""
                })
            );


            // ==================================
            // 重新讀目前頁面
            // ==================================

            await loadMessagePage(
                currentPageRef.current
            );


        } catch (error) {

            console.error(
                error
            );


            alert(
                error.message
            );


        } finally {

            setReplyingId(
                null
            );
        }
    }


    // ==========================================
    // 換頁
    // ==========================================

    function goToPage(
        page
    ) {

        if (
            page < 0
            ||
            page >= totalPages
        ) {

            return;
        }


        currentPageRef.current =
            page;


        setCurrentPage(
            page
        );


        loadMessagePage(
            page
        );
    }


    // ==========================================
    // Date
    // ==========================================

    function formatDate(
        value
    ) {

        if (!value) {

            return "";
        }


        return new Date(
            value
        )
            .toLocaleString(
                "zh-TW"
            );
    }


    // ==========================================
    // JSX
    // ==========================================

    return (

        <div
            className=
                "message-board-container"
        >


            {/* ==================================
                標題
            ================================== */}

            <div
                className=
                    "message-board-header"
            >

                <h1>
                    💬 客戶留言板
                </h1>


                <p>
                    商品問題、使用心得、
                    售後服務都歡迎留言
                </p>

            </div>


            {/* ==================================
                新留言
            ================================== */}

            <div
                className=
                    "message-form-card"
            >

                <h2>
                    ✏️ 發表留言
                </h2>


                <form
                    onSubmit={
                        handleSubmit
                    }
                >

                    <textarea

                        value={
                            content
                        }

                        onChange={
                            event =>
                                setContent(
                                    event.target.value
                                )
                        }

                        maxLength={
                            1000
                        }

                        rows={
                            5
                        }

                        placeholder=
                            "例如：收到商品後發現外殼破損，我附上照片供客服確認..."

                        style={{
                            width:
                                "100%",

                            boxSizing:
                                "border-box",

                            padding:
                                "14px",

                            borderRadius:
                                "12px",

                            border:
                                "1px solid #dbe2ea",

                            resize:
                                "vertical"
                        }}
                    />


                    <div
                        style={{
                            textAlign:
                                "right",

                            color:
                                "#64748b",

                            fontSize:
                                "14px",

                            marginTop:
                                "5px"
                        }}
                    >

                        {content.length}
                        /1000

                    </div>


                    {/* ==========================
                        圖片
                    ========================== */}

                    <div
                        style={{
                            marginTop:
                                "20px"
                        }}
                    >

                        <h3>
                            📷 附上問題照片
                            （選填）
                        </h3>


                        <p
                            style={{
                                color:
                                    "#64748b"
                            }}
                        >

                            最多 5 張，
                            JPG / PNG / WEBP，
                            單張最大 10MB

                        </p>


                        <input

                            type="file"

                            multiple

                            accept=
                                "image/jpeg,image/png,image/webp"

                            onChange={
                                handleFileChange
                            }
                        />

                    </div>


                    {/* ==========================
                        Preview
                    ========================== */}

                    {
                        previewUrls.length
                        >
                        0
                        &&
                        (

                            <div
                                style={{
                                    display:
                                        "grid",

                                    gridTemplateColumns:
                                        "repeat(auto-fill,minmax(140px,1fr))",

                                    gap:
                                        "12px",

                                    marginTop:
                                        "18px"
                                }}
                            >

                                {
                                    previewUrls.map(

                                        (
                                            url,
                                            index
                                        ) => (

                                            <div

                                                key={
                                                    url
                                                }

                                                style={{
                                                    position:
                                                        "relative",

                                                    border:
                                                        "1px solid #e2e8f0",

                                                    borderRadius:
                                                        "12px",

                                                    overflow:
                                                        "hidden"
                                                }}
                                            >

                                                <img

                                                    src={
                                                        url
                                                    }

                                                    alt={
                                                        selectedFiles[
                                                            index
                                                        ]?.name
                                                    }

                                                    style={{
                                                        width:
                                                            "100%",

                                                        height:
                                                            "130px",

                                                        objectFit:
                                                            "cover"
                                                    }}
                                                />


                                                <button

                                                    type="button"

                                                    onClick={
                                                        () =>
                                                            removeFile(
                                                                index
                                                            )
                                                    }

                                                    style={{
                                                        position:
                                                            "absolute",

                                                        top:
                                                            "6px",

                                                        right:
                                                            "6px",

                                                        border:
                                                            "none",

                                                        borderRadius:
                                                            "50%",

                                                        width:
                                                            "30px",

                                                        height:
                                                            "30px",

                                                        cursor:
                                                            "pointer"
                                                    }}
                                                >

                                                    ✕

                                                </button>

                                            </div>
                                        )
                                    )
                                }

                            </div>
                        )
                    }


                    {
                        statusMessage
                        &&
                        (

                            <p
                                style={{
                                    marginTop:
                                        "15px",

                                    fontWeight:
                                        "bold"
                                }}
                            >

                                {statusMessage}

                            </p>
                        )
                    }


                    <button

                        type="submit"

                        className=
                            "primary-button"

                        disabled={
                            loading
                        }

                        style={{
                            marginTop:
                                "18px"
                        }}
                    >

                        {
                            loading
                                ?
                                "送出中..."
                                :
                                "📨 發表留言"
                        }

                    </button>

                </form>

            </div>


            {/* ==================================
                留言列表
            ================================== */}

            <div
                style={{
                    marginTop:
                        "30px"
                }}
            >

                <h2>
                    📋 客戶留言
                </h2>


                <p>
                    共 {totalElements} 筆留言
                </p>


                {
                    messages.length
                    ===
                    0
                        ? (

                            <p>
                                尚無留言
                            </p>

                        )
                        : (

                            messages.map(

                                item => (

                                    <div

                                        key={
                                            item.id
                                        }

                                        className=
                                            "message-card"

                                        style={{
                                            background:
                                                "white",

                                            borderRadius:
                                                "16px",

                                            padding:
                                                "20px",

                                            marginBottom:
                                                "18px",

                                            boxShadow:
                                                "0 3px 15px rgba(0,0,0,0.06)"
                                        }}
                                    >


                                        {/* ==================
                                            主留言會員
                                        ================== */}

                                        <div
                                            style={{
                                                display:
                                                    "flex",

                                                justifyContent:
                                                    "space-between",

                                                gap:
                                                    "15px"
                                            }}
                                        >

                                            <strong>

                                                👤 {
                                                    item.userName
                                                }

                                            </strong>


                                            <span
                                                style={{
                                                    color:
                                                        "#64748b",

                                                    fontSize:
                                                        "14px"
                                                }}
                                            >

                                                {
                                                    formatDate(
                                                        item.createdAt
                                                    )
                                                }

                                            </span>

                                        </div>


                                        {/* 主留言 */}

                                        <p
                                            style={{
                                                whiteSpace:
                                                    "pre-wrap",

                                                lineHeight:
                                                    "1.8"
                                            }}
                                        >

                                            {
                                                item.content
                                            }

                                        </p>


                                        {/* ==================
                                            客戶附圖
                                        ================== */}

                                        {
                                            item.attachments
                                            &&
                                            item.attachments
                                                .length > 0
                                            &&
                                            (

                                                <div>

                                                    <div
                                                        style={{
                                                            marginBottom:
                                                                "8px",

                                                            fontWeight:
                                                                "bold"
                                                        }}
                                                    >

                                                        📷 客戶附圖

                                                    </div>


                                                    <div
                                                        style={{
                                                            display:
                                                                "grid",

                                                            gridTemplateColumns:
                                                                "repeat(auto-fill,minmax(150px,1fr))",

                                                            gap:
                                                                "12px"
                                                        }}
                                                    >

                                                        {
                                                            item.attachments
                                                                .map(

                                                                    attachment => (

                                                                        <a

                                                                            key={
                                                                                attachment.id
                                                                            }

                                                                            href={
                                                                                `${API_URL}/attachments/${attachment.id}/view`
                                                                            }

                                                                            target="_blank"

                                                                            rel="noreferrer"
                                                                        >

                                                                            <img

                                                                                src={
                                                                                    `${API_URL}/attachments/${attachment.id}/view`
                                                                                }

                                                                                alt={
                                                                                    attachment.originalFileName
                                                                                }

                                                                                style={{
                                                                                    width:
                                                                                        "100%",

                                                                                    height:
                                                                                        "150px",

                                                                                    objectFit:
                                                                                        "cover",

                                                                                    borderRadius:
                                                                                        "10px",

                                                                                    border:
                                                                                        "1px solid #e2e8f0"
                                                                                }}
                                                                            />

                                                                        </a>
                                                                    )
                                                                )
                                                        }

                                                    </div>

                                                </div>
                                            )
                                        }


                                        {/* ==================
                                            ★ 雙向對話紀錄
                                        ================== */}

                                        {
                                            item.replies
                                            &&
                                            item.replies.length
                                            >
                                            0
                                            &&
                                            (

                                                <div
                                                    style={{
                                                        marginTop:
                                                            "24px"
                                                    }}
                                                >

                                                    <strong>
                                                        💬 對話紀錄
                                                    </strong>


                                                    {
                                                        item.replies.map(

                                                            reply => {

                                                                const adminReply =
                                                                    reply.senderRole
                                                                    ===
                                                                    "ADMIN";


                                                                return (

                                                                    <div

                                                                        key={
                                                                            reply.id
                                                                        }

                                                                        style={{
                                                                            marginTop:
                                                                                "12px",

                                                                            padding:
                                                                                "14px 16px",

                                                                            borderRadius:
                                                                                "12px",

                                                                            background:
                                                                                adminReply
                                                                                    ?
                                                                                    "#f5f3ff"
                                                                                    :
                                                                                    "#f8fafc",

                                                                            borderLeft:
                                                                                adminReply
                                                                                    ?
                                                                                    "5px solid #7c3aed"
                                                                                    :
                                                                                    "5px solid #3b82f6"
                                                                        }}
                                                                    >

                                                                        <div
                                                                            style={{
                                                                                display:
                                                                                    "flex",

                                                                                justifyContent:
                                                                                    "space-between",

                                                                                gap:
                                                                                    "10px"
                                                                            }}
                                                                        >

                                                                            <strong>

                                                                                {
                                                                                    adminReply
                                                                                        ?
                                                                                        "🛠️ 管理員"
                                                                                        :
                                                                                        `👤 ${reply.senderName}`
                                                                                }

                                                                            </strong>


                                                                            <span
                                                                                style={{
                                                                                    color:
                                                                                        "#64748b",

                                                                                    fontSize:
                                                                                        "13px"
                                                                                }}
                                                                            >

                                                                                {
                                                                                    formatDate(
                                                                                        reply.createdAt
                                                                                    )
                                                                                }

                                                                            </span>

                                                                        </div>


                                                                        <div
                                                                            style={{
                                                                                marginTop:
                                                                                    "8px",

                                                                                whiteSpace:
                                                                                    "pre-wrap",

                                                                                lineHeight:
                                                                                    "1.7"
                                                                            }}
                                                                        >

                                                                            {
                                                                                reply.content
                                                                            }

                                                                        </div>

                                                                    </div>
                                                                );
                                                            }
                                                        )
                                                    }

                                                </div>
                                            )
                                        }


                                        {/* ==================
                                            ★ 雙向 Reply Form
                                        ================== */}

                                        {
                                            canReply(
                                                item
                                            )
                                            &&
                                            (

                                                <div
                                                    style={{
                                                        marginTop:
                                                            "22px",

                                                        paddingTop:
                                                            "18px",

                                                        borderTop:
                                                            "1px solid #e2e8f0"
                                                    }}
                                                >

                                                    <strong>

                                                        {
                                                            isAdmin
                                                                ?
                                                                "🛠️ 管理員回覆"
                                                                :
                                                                "💬 繼續回覆"
                                                        }

                                                    </strong>


                                                    <textarea

                                                        value={
                                                            replyContents[
                                                                item.id
                                                            ]
                                                            ||
                                                            ""
                                                        }

                                                        onChange={
                                                            event =>

                                                                handleReplyChange(

                                                                    item.id,

                                                                    event.target.value
                                                                )
                                                        }

                                                        maxLength={
                                                            1000
                                                        }

                                                        rows={
                                                            3
                                                        }

                                                        placeholder={
                                                            isAdmin
                                                                ?
                                                                "輸入客服回覆內容..."
                                                                :
                                                                "輸入您的回覆..."
                                                        }

                                                        style={{
                                                            display:
                                                                "block",

                                                            width:
                                                                "100%",

                                                            boxSizing:
                                                                "border-box",

                                                            marginTop:
                                                                "10px",

                                                            padding:
                                                                "12px",

                                                            border:
                                                                "1px solid #dbe2ea",

                                                            borderRadius:
                                                                "10px",

                                                            resize:
                                                                "vertical"
                                                        }}
                                                    />


                                                    <div
                                                        style={{
                                                            textAlign:
                                                                "right",

                                                            color:
                                                                "#64748b",

                                                            fontSize:
                                                                "13px",

                                                            marginTop:
                                                                "4px"
                                                        }}
                                                    >

                                                        {
                                                            (
                                                                replyContents[
                                                                    item.id
                                                                ]
                                                                ||
                                                                ""
                                                            ).length
                                                        }

                                                        /1000

                                                    </div>


                                                    <button

                                                        type="button"

                                                        className=
                                                            "primary-button"

                                                        disabled={
                                                            replyingId
                                                            ===
                                                            item.id
                                                        }

                                                        onClick={
                                                            () =>
                                                                handleReply(
                                                                    item.id
                                                                )
                                                        }

                                                        style={{
                                                            marginTop:
                                                                "10px"
                                                        }}
                                                    >

                                                        {
                                                            replyingId
                                                            ===
                                                            item.id
                                                                ?
                                                                "回覆中..."
                                                                :
                                                                "💬 送出回覆"
                                                        }

                                                    </button>

                                                </div>
                                            )
                                        }

                                    </div>
                                )
                            )
                        )
                }

            </div>


            {/* ==================================
                後端分頁
            ================================== */}

            {
                totalPages
                >
                1
                &&
                (

                    <div

                        className=
                            "pagination"

                        style={{
                            display:
                                "flex",

                            justifyContent:
                                "center",

                            alignItems:
                                "center",

                            gap:
                                "8px",

                            margin:
                                "25px 0"
                        }}
                    >


                        <button

                            type="button"

                            disabled={
                                currentPage
                                ===
                                0
                            }

                            onClick={
                                () =>
                                    goToPage(
                                        currentPage - 1
                                    )
                            }
                        >

                            ‹ 上一頁

                        </button>


                        {
                            Array.from(

                                {
                                    length:
                                        totalPages
                                },

                                (
                                    _,
                                    index
                                ) =>
                                    index
                            )
                                .map(

                                    page => (

                                        <button

                                            key={
                                                page
                                            }

                                            type="button"

                                            onClick={
                                                () =>
                                                    goToPage(
                                                        page
                                                    )
                                            }

                                            style={{
                                                fontWeight:
                                                    page
                                                    ===
                                                    currentPage
                                                        ?
                                                        "bold"
                                                        :
                                                        "normal"
                                            }}
                                        >

                                            {
                                                page + 1
                                            }

                                        </button>
                                    )
                                )
                        }


                        <button

                            type="button"

                            disabled={
                                currentPage
                                >=
                                totalPages - 1
                            }

                            onClick={
                                () =>
                                    goToPage(
                                        currentPage + 1
                                    )
                            }
                        >

                            下一頁 ›

                        </button>

                    </div>
                )
            }

        </div>
    );
}


export default MessageBoard;