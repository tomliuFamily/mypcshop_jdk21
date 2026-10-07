import { useState } from "react";
import { useNavigate } from "react-router-dom";


function AdminUserImport() {

    const navigate = useNavigate();


    // ==========================================
    // State
    // ==========================================

    const [file, setFile] =
        useState(null);

    const [message, setMessage] =
        useState("");

    const [success, setSuccess] =
        useState(null);

    const [loading, setLoading] =
        useState(false);

    const [importedCount, setImportedCount] =
        useState(null);


    // ==========================================
    // 檔案大小
    // ==========================================

    const formatFileSize = (bytes) => {

        if (!bytes) {
            return "0 KB";
        }

        const kb =
            bytes / 1024;

        if (kb < 1024) {

            return `${kb.toFixed(2)} KB`;
        }

        const mb =
            kb / 1024;

        return `${mb.toFixed(2)} MB`;
    };


    // ==========================================
    // 選擇 TXT
    // ==========================================

    const handleFileChange = (e) => {

        const selectedFile =
            e.target.files[0];


        // 選新檔案時
        // 清除上一次結果
        setMessage("");

        setSuccess(null);

        setImportedCount(null);


        if (!selectedFile) {

            setFile(null);

            return;
        }


        // ======================================
        // 副檔名檢查
        // ======================================

        if (
            !selectedFile.name
                .toLowerCase()
                .endsWith(".txt")
        ) {

            setFile(null);

            setSuccess(false);

            setMessage(
                "請選擇 .txt 文字檔"
            );


            e.target.value = "";

            return;
        }


        // ======================================
        // 儲存選擇的檔案
        // ======================================

        setFile(
            selectedFile
        );
    };


    // ==========================================
    // 上傳
    // ==========================================

    const handleUpload = async () => {

        // ======================================
        // 尚未選擇檔案
        // ======================================

        if (!file) {

            setSuccess(false);

            setImportedCount(null);

            setMessage(
                "請先選擇 TXT 會員資料檔案"
            );

            return;
        }


        // ======================================
        // 取得 Access Token
        // ======================================

        const accessToken =
            sessionStorage.getItem(
                "accessToken"
            );


        // ======================================
        // 沒登入
        // ======================================

        if (!accessToken) {

            alert(
                "請先登入"
            );

            navigate(
                "/login"
            );

            return;
        }


        // ======================================
        // FormData
        //
        // 對應 Spring Boot：
        //
        // @RequestParam("file")
        // MultipartFile file
        // ======================================

        const formData =
            new FormData();


        formData.append(
            "file",
            file
        );


        try {

            // ==================================
            // 開始上傳
            // ==================================

            setLoading(true);

            setSuccess(null);

            setImportedCount(null);

            setMessage(
                `正在匯入 ${file.name}，請稍候...`
            );


            // ==================================
            // 呼叫 Spring Boot
            // ==================================

            const response =
                await fetch(

                    "http://localhost:8080/api/admin/users/import",

                    {
                        method:
                            "POST",

                        headers: {

                            Authorization:
                                `Bearer ${accessToken}`
                        },

                        body:
                            formData
                    }
                );


            // ==================================
            // 取得後端資料
            // ==================================

            let data = null;


            try {

                data =
                    await response.json();

            } catch {

                // 如果後端不是 JSON
                const text =
                    await response.text()
                        .catch(
                            () => ""
                        );

                data = {
                    message:
                        text
                };
            }


            console.log(
                "會員匯入 API 回傳：",
                data
            );


            // ==================================
            // 200
            // 匯入成功
            // ==================================

            if (response.ok) {

                const count =
                    data?.importedCount
                    ?? 0;


                setSuccess(
                    true
                );


                setImportedCount(
                    count
                );


                setMessage(
                    data?.message
                    ||
                    "會員資料匯入成功"
                );


                // ==============================
                // 成功後清除檔案
                // 但是成功訊息保留
                // ==============================

                setFile(
                    null
                );


                const fileInput =
                    document.getElementById(
                        "admin-user-import-file"
                    );


                if (fileInput) {

                    fileInput.value =
                        "";
                }


                return;
            }


            // ==================================
            // 401
            // Token 過期 / 無效
            // ==================================

            if (
                response.status === 401
            ) {

                setSuccess(
                    false
                );


                setImportedCount(
                    null
                );


                setMessage(
                    "登入已失效，請重新登入"
                );


                return;
            }


            // ==================================
            // 403
            // 不是管理員
            // ==================================

            if (
                response.status === 403
            ) {

                setSuccess(
                    false
                );


                setImportedCount(
                    null
                );


                setMessage(
                    "權限不足，只有管理員可以執行會員資料匯入"
                );


                return;
            }


            // ==================================
            // 400
            //
            // 重複 Email
            // TXT 格式錯誤
            // 資料錯誤
            //
            // 後端 @Transactional
            // 應執行 Rollback
            // ==================================

            if (
                response.status === 400
            ) {

                setSuccess(
                    false
                );


                setImportedCount(
                    null
                );


                setMessage(

                    `${
                        data?.message
                        ||
                        "會員資料匯入失敗"
                    }。整批資料已取消匯入。`

                );


                return;
            }


            // ==================================
            // 其他 HTTP Error
            // ==================================

            setSuccess(
                false
            );


            setImportedCount(
                null
            );


            setMessage(

                data?.message
                ||
                `會員資料匯入失敗，HTTP ${response.status}`

            );


        } catch (error) {

            console.error(
                "會員匯入錯誤：",
                error
            );


            setSuccess(
                false
            );


            setImportedCount(
                null
            );


            setMessage(
                "無法連線到 Spring Boot 伺服器，請確認後端是否已啟動"
            );


        } finally {

            setLoading(
                false
            );
        }
    };


    // ==========================================
    // JSX
    // ==========================================

    return (

        <div className="admin-import-page">

            <div className="admin-import-card">


                {/* ==================================
                    標題
                   ================================== */}

                <h2>

                    📥 管理員－會員資料匯入

                </h2>


                <p className="admin-import-subtitle">

                    使用 TXT 文字檔批次新增會員資料

                </p>


                {/* ==================================
                    TXT 格式說明
                   ================================== */}

                <div className="admin-import-format">

                    <strong>

                        📄 TXT 檔案格式

                    </strong>


                    <p>

                        email|password|name|address

                    </p>


                    <p>

                        mary@demo.com|123456|Mary|台北市

                    </p>


                    <p>

                        peter@demo.com|123456|Peter|新北市

                    </p>

                </div>


                {/* ==================================
                    Step 1
                    選擇檔案
                   ================================== */}

                <div className="admin-import-file">

                    <label>

                        ① 選擇會員 TXT 檔案

                    </label>


                    <input
                        id="admin-user-import-file"

                        type="file"

                        accept=".txt,text/plain"

                        onChange={
                            handleFileChange
                        }

                        disabled={
                            loading
                        }
                    />

                </div>


                {/* ==================================
                    已選擇檔案
                   ================================== */}

                {
                    file
                    &&
                    (

                        <div className="admin-selected-file">

                            <div>

                                📄 已選擇檔案：

                                <strong>

                                    {file.name}

                                </strong>

                            </div>


                            <div
                                style={{
                                    marginTop: "6px"
                                }}
                            >

                                檔案大小：

                                <strong>

                                    {
                                        formatFileSize(
                                            file.size
                                        )
                                    }

                                </strong>

                            </div>

                        </div>
                    )
                }


                {/* ==================================
                    尚未選檔提示
                   ================================== */}

                {
                    !file
                    &&
                    success === null
                    &&
                    (

                        <div
                            style={{
                                marginBottom: "18px",
                                color: "#64748b",
                                fontSize: "14px"
                            }}
                        >

                            ⬆️ 請先按上方「選擇檔案」，
                            選擇要匯入的 .txt 檔案

                        </div>
                    )
                }


                {/* ==================================
                    Step 2
                    上傳
                   ================================== */}

                <button
                    type="button"

                    className="admin-import-button"

                    onClick={
                        handleUpload
                    }

                    disabled={
                        loading
                        ||
                        !file
                    }
                >

                    {
                        loading

                            ? "⏳ 正在匯入會員資料..."

                            : "② 📤 上傳並匯入會員資料"
                    }

                </button>


                {/* ==================================
                    上傳中
                   ================================== */}

                {
                    loading
                    &&
                    (

                        <div
                            className="admin-import-message"
                            style={{
                                background: "#eff6ff",
                                color: "#1d4ed8"
                            }}
                        >

                            ⏳ {message}

                        </div>
                    )
                }


                {/* ==================================
                    成功
                   ================================== */}

                {
                    !loading
                    &&
                    success === true
                    &&
                    (

                        <div
                            className=
                                "admin-import-message success"
                        >

                            <div
                                style={{
                                    fontSize: "20px",
                                    marginBottom: "8px"
                                }}
                            >

                                ✅ 匯入成功

                            </div>


                            <div>

                                {message}

                            </div>


                            {
                                importedCount !== null
                                &&
                                (

                                    <div
                                        style={{
                                            marginTop: "8px"
                                        }}
                                    >

                                        本次成功新增：

                                        <strong
                                            style={{
                                                fontSize: "20px",
                                                margin: "0 5px"
                                            }}
                                        >

                                            {importedCount}

                                        </strong>

                                        筆會員資料

                                    </div>
                                )
                            }


                            <div
                                style={{
                                    marginTop: "8px"
                                }}
                            >

                                💾 MySQL 資料庫更新完成

                            </div>

                        </div>
                    )
                }


                {/* ==================================
                    失敗
                   ================================== */}

                {
                    !loading
                    &&
                    success === false
                    &&
                    (

                        <div
                            className=
                                "admin-import-message error"
                        >

                            <div
                                style={{
                                    fontSize: "20px",
                                    marginBottom: "8px"
                                }}
                            >

                                ❌ 匯入失敗

                            </div>


                            <div>

                                {message}

                            </div>


                            <div
                                style={{
                                    marginTop: "8px"
                                }}
                            >

                                ↩️ 如果是重複會員資料，
                                Transaction 會 Rollback，
                                不會只新增部分資料。

                            </div>

                        </div>
                    )
                }


                {/* ==================================
                    匯入規則
                   ================================== */}

                <div className="admin-import-note">

                    <strong>

                        ⚠️ 匯入規則

                    </strong>


                    <p>

                        如果 TXT 中有任何會員 Email
                        已存在，整批資料將取消匯入。

                    </p>


                    <p>

                        系統使用 Transaction。
                        發生錯誤時執行 Rollback，
                        避免只匯入部分會員資料。

                    </p>

                </div>

            </div>

        </div>
    );
}


export default AdminUserImport;