import {
    useEffect,
    useState
} from "react";

import {
    useNavigate
} from "react-router-dom";


function ProductFileUpload() {

    const navigate =
        useNavigate();


    // ==========================================
    // 目前登入會員
    // ==========================================

    const userText =
        sessionStorage.getItem(
            "user"
        );


    let currentUser = null;


    try {

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
    // ★ Admin
    // ==========================================

    const isAdmin =
        currentUser?.email ===
            "admin@example.com";


    // ==========================================
    // State
    // ==========================================

    const [
        products,
        setProducts
    ] = useState([]);


    const [
        productId,
        setProductId
    ] = useState("");


    const [
        selectedFiles,
        setSelectedFiles
    ] = useState([]);


    const [
        uploadedFiles,
        setUploadedFiles
    ] = useState([]);


    const [
        previewUrl,
        setPreviewUrl
    ] = useState("");


    const [
        message,
        setMessage
    ] = useState("");


    const [
        loading,
        setLoading
    ] = useState(false);


    const PRODUCT_API =
        "http://localhost:8080/api/products";


    const FILE_API =
        "http://localhost:8080/api/files";


    // ==========================================
    // 初始化
    // ==========================================

    useEffect(() => {

        // 沒登入
        if (!userText) {

            alert(
                "請先登入"
            );

            navigate(
                "/login"
            );

            return;
        }


        // 非管理員
        if (!isAdmin) {

            alert(
                "此功能僅限管理員使用"
            );

            navigate(
                "/"
            );

            return;
        }


        loadProducts();

        loadUploadedFiles();

    }, []);


    // ==========================================
    // 查詢商品
    // ==========================================

    async function loadProducts() {

        try {

            const response =
                await fetch(
                    PRODUCT_API
                );


            if (!response.ok) {

                throw new Error(
                    "商品資料載入失敗"
                );
            }


            const data =
                await response.json();


            setProducts(
                data
            );


            if (
                data.length > 0
            ) {

                setProductId(
                    String(
                        data[0].id
                    )
                );
            }

        } catch (error) {

            console.error(
                error
            );


            setMessage(
                "商品資料載入失敗"
            );
        }
    }


    // ==========================================
    // 查詢圖片
    // ==========================================

    async function loadUploadedFiles() {

        try {

            const response =
                await fetch(
                    FILE_API
                );


            if (!response.ok) {

                throw new Error(
                    "圖片資料載入失敗"
                );
            }


            const data =
                await response.json();


            setUploadedFiles(
                data
            );

        } catch (error) {

            console.error(
                error
            );
        }
    }


    // ==========================================
    // 選擇檔案
    // ==========================================

    function handleFileChange(
        event
    ) {

        const files =
            Array.from(
                event.target.files
            );


        const validFiles =
            files.filter(
                file => {

                    const validType =
                        [
                            "image/jpeg",
                            "image/png",
                            "image/webp"
                        ].includes(
                            file.type
                        );


                    const validSize =
                        file.size
                        <=
                        10 * 1024 * 1024;


                    return (
                        validType
                        &&
                        validSize
                    );
                }
            );


        if (
            validFiles.length
            !==
            files.length
        ) {

            setMessage(
                "部分圖片格式不符或超過 10MB"
            );

        } else {

            setMessage("");
        }


        setSelectedFiles(
            validFiles
        );


        if (
            validFiles.length > 0
        ) {

            setPreviewUrl(
                URL.createObjectURL(
                    validFiles[0]
                )
            );

        } else {

            setPreviewUrl("");
        }
    }


    // ==========================================
    // 移除尚未上傳圖片
    // ==========================================

    function removeSelectedFile(
        index
    ) {

        const newFiles =
            selectedFiles.filter(
                (_, i) =>
                    i !== index
            );


        setSelectedFiles(
            newFiles
        );


        if (
            newFiles.length > 0
        ) {

            setPreviewUrl(
                URL.createObjectURL(
                    newFiles[0]
                )
            );

        } else {

            setPreviewUrl("");
        }
    }


    // ==========================================
    // 清除
    // ==========================================

    function clearSelectedFiles() {

        setSelectedFiles([]);

        setPreviewUrl("");

        setMessage("");
    }


    // ==========================================
    // Upload
    // ==========================================

    async function handleUpload() {

        if (!isAdmin) {

            setMessage(
                "只有管理員可以上傳圖片"
            );

            return;
        }


        if (!productId) {

            setMessage(
                "請選擇商品"
            );

            return;
        }


        if (
            selectedFiles.length
            === 0
        ) {

            setMessage(
                "請選擇圖片"
            );

            return;
        }


        const token =
            sessionStorage
                .getItem(
                    "token"
                );


        if (!token) {

            navigate(
                "/login"
            );

            return;
        }


        try {

            setLoading(true);

            setMessage("");


            for (
                const file
                of selectedFiles
            ) {

                const formData =
                    new FormData();


                formData.append(
                    "productId",
                    productId
                );


                formData.append(
                    "file",
                    file
                );


                const response =
                    await fetch(

                        `${FILE_API}/upload`,

                        {

                            method:
                                "POST",

                            headers: {

                                Authorization:
                                    `Bearer ${token}`
                            },

                            body:
                                formData
                        }
                    );


                if (
                    response.status
                    === 401
                ) {

                    throw new Error(
                        "登入已失效"
                    );
                }


                if (
                    response.status
                    === 403
                ) {

                    throw new Error(
                        "你沒有管理員權限"
                    );
                }


                if (!response.ok) {

                    const text =
                        await response.text();


                    throw new Error(
                        text
                        ||
                        "圖片上傳失敗"
                    );
                }
            }


            setMessage(
                `✅ 成功上傳 ${selectedFiles.length} 張圖片`
            );


            setSelectedFiles([]);

            setPreviewUrl("");


            await loadUploadedFiles();


        } catch (error) {

            console.error(
                error
            );


            setMessage(
                error.message
            );

        } finally {

            setLoading(false);
        }
    }


    // ==========================================
    // Delete
    // ==========================================

    async function handleDelete(
        id
    ) {

        if (!isAdmin) {

            alert(
                "只有管理員可以刪除圖片"
            );

            return;
        }


        const confirmed =
            window.confirm(
                "確定刪除此圖片？"
            );


        if (!confirmed) {
            return;
        }


        const token =
            sessionStorage
                .getItem(
                    "token"
                );


        try {

            const response =
                await fetch(

                    `${FILE_API}/${id}`,

                    {

                        method:
                            "DELETE",

                        headers: {

                            Authorization:
                                `Bearer ${token}`
                        }
                    }
                );


            if (!response.ok) {

                const text =
                    await response.text();


                throw new Error(
                    text
                    ||
                    "刪除失敗"
                );
            }


            await loadUploadedFiles();


        } catch (error) {

            alert(
                error.message
            );
        }
    }


    // ==========================================
    // Product name
    // ==========================================

    function getProductName(
        id
    ) {

        const product =
            products.find(
                item =>
                    Number(
                        item.id
                    )
                    ===
                    Number(id)
            );


        return product
            ? product.name
            : "-";
    }


    // ==========================================
    // File size
    // ==========================================

    function formatSize(
        size
    ) {

        if (!size) {
            return "0 B";
        }


        if (
            size < 1024
        ) {

            return `${size} B`;
        }


        if (
            size
            <
            1024 * 1024
        ) {

            return (
                size / 1024
            ).toFixed(1)
            + " KB";
        }


        return (
            size /
            1024 /
            1024
        ).toFixed(2)
        + " MB";
    }


    // ==========================================
    // Date
    // ==========================================

    function formatDate(
        value
    ) {

        if (!value) {
            return "-";
        }


        return new Date(
            value
        ).toLocaleString(
            "zh-TW"
        );
    }


    // ==========================================
    // 非管理員先不顯示內容
    // ==========================================

    if (!isAdmin) {

        return null;
    }


    // ==========================================
    // JSX
    // ==========================================

    return (

        <div
            className=
                "page-container"
        >

            <h1
                className=
                    "page-title"
            >

                ⚙️ 商品圖片管理

            </h1>


            <p
                className=
                    "page-subtitle"
            >

                管理員專用：
                上傳、預覽與刪除商品圖片

            </p>


            {/* 商品 */}

            <div
                style={{
                    background:
                        "white",
                    padding:
                        "24px",
                    borderRadius:
                        "18px",
                    marginBottom:
                        "20px"
                }}
            >

                <h3>
                    🛒 選擇商品
                </h3>


                <select
                    value={
                        productId
                    }
                    onChange={
                        event =>
                            setProductId(
                                event
                                    .target
                                    .value
                            )
                    }
                    style={{
                        width:
                            "100%",
                        padding:
                            "12px",
                        borderRadius:
                            "10px"
                    }}
                >

                    {
                        products.map(
                            product => (

                            <option
                                key={
                                    product.id
                                }
                                value={
                                    product.id
                                }
                            >

                                {
                                    product.id
                                }

                                {" - "}

                                {
                                    product.name
                                }

                                {" - NT$ "}

                                {
                                    Number(
                                        product.price
                                    )
                                    .toLocaleString()
                                }

                            </option>

                            )
                        )
                    }

                </select>


                <h3
                    style={{
                        marginTop:
                            "20px"
                    }}
                >

                    ☁️ 選擇圖片

                </h3>


                <input
                    type="file"
                    multiple
                    accept=
                        "image/jpeg,image/png,image/webp"
                    onChange={
                        handleFileChange
                    }
                />


                {
                    selectedFiles.length
                    > 0
                    &&
                    (

                        <div
                            style={{
                                marginTop:
                                    "20px"
                            }}
                        >

                            {
                                selectedFiles.map(
                                    (
                                        file,
                                        index
                                    ) => (

                                    <div
                                        key={
                                            index
                                        }
                                    >

                                        {
                                            file.name
                                        }

                                        {" "}

                                        {
                                            formatSize(
                                                file.size
                                            )
                                        }

                                        <button
                                            type="button"
                                            onClick={
                                                () =>
                                                    removeSelectedFile(
                                                        index
                                                    )
                                            }
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
                    previewUrl
                    &&
                    (

                        <img
                            src={
                                previewUrl
                            }
                            alt="預覽"
                            style={{
                                maxWidth:
                                    "350px",
                                marginTop:
                                    "20px",
                                display:
                                    "block"
                            }}
                        />
                    )
                }


                <div
                    style={{
                        marginTop:
                            "20px"
                    }}
                >

                    <button
                        type="button"
                        className=
                            "primary-button"
                        onClick={
                            handleUpload
                        }
                        disabled={
                            loading
                        }
                    >

                        {
                            loading
                            ?
                            "上傳中..."
                            :
                            "⬆️ 上傳圖片"
                        }

                    </button>


                    <button
                        type="button"
                        onClick={
                            clearSelectedFiles
                        }
                        style={{
                            marginLeft:
                                "10px"
                        }}
                    >

                        清除

                    </button>

                </div>


                {
                    message
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

                            {message}

                        </p>
                    )
                }

            </div>


            {/* ==================================
                已上傳
            ================================== */}

            <div
                style={{
                    background:
                        "white",
                    padding:
                        "24px",
                    borderRadius:
                        "18px"
                }}
            >

                <h2>

                    🖼️ 已上傳圖片
                    {" ("}
                    {
                        uploadedFiles.length
                    }
                    {")"}

                </h2>


                <div
                    style={{
                        overflowX:
                            "auto"
                    }}
                >

                    <table
                        style={{
                            width:
                                "100%",
                            borderCollapse:
                                "collapse"
                        }}
                    >

                        <thead>

                            <tr>

                                <th>#</th>
                                <th>預覽</th>
                                <th>商品</th>
                                <th>檔名</th>
                                <th>大小</th>
                                <th>時間</th>
                                <th>操作</th>

                            </tr>

                        </thead>


                        <tbody>

                            {
                                uploadedFiles.map(
                                    (
                                        file,
                                        index
                                    ) => (

                                    <tr
                                        key={
                                            file.id
                                        }
                                    >

                                        <td>
                                            {
                                                index + 1
                                            }
                                        </td>


                                        <td>

                                            <img
                                                src={
                                                    `${FILE_API}/${file.id}/view`
                                                }
                                                alt={
                                                    file.originalFileName
                                                }
                                                style={{
                                                    width:
                                                        "70px",
                                                    height:
                                                        "70px",
                                                    objectFit:
                                                        "cover"
                                                }}
                                            />

                                        </td>


                                        <td>

                                            {
                                                file.productId
                                            }

                                            {" - "}

                                            {
                                                getProductName(
                                                    file.productId
                                                )
                                            }

                                        </td>


                                        <td>
                                            {
                                                file.originalFileName
                                            }
                                        </td>


                                        <td>
                                            {
                                                formatSize(
                                                    file.fileSize
                                                )
                                            }
                                        </td>


                                        <td>
                                            {
                                                formatDate(
                                                    file.uploadTime
                                                )
                                            }
                                        </td>


                                        <td>

                                            <button
                                                type="button"
                                                onClick={
                                                    () =>
                                                        handleDelete(
                                                            file.id
                                                        )
                                                }
                                                style={{
                                                    background:
                                                        "#ef4444",
                                                    color:
                                                        "white",
                                                    border:
                                                        "none",
                                                    padding:
                                                        "8px 12px",
                                                    borderRadius:
                                                        "8px"
                                                }}
                                            >

                                                🗑️ 刪除

                                            </button>

                                        </td>

                                    </tr>

                                    )
                                )
                            }

                        </tbody>

                    </table>

                </div>

            </div>

        </div>
    );
}


export default ProductFileUpload;