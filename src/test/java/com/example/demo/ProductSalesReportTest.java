package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Font;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;


@SpringBootTest
public class ProductSalesReportTest {


    @Autowired
    private DataSource dataSource;


    @Test
    void generateProductSalesReportPdf() throws Exception {


        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "開始產生產品銷售報表"
        );
        System.out.println(
                "========================================"
        );


        /*
         * ==========================================
         * 1. 讀取 JRXML
         * ==========================================
         *
         * 實際位置：
         *
         * src/test/resources/reports/
         * product_sales_report.jrxml
         *
         */
        InputStream reportStream =
                getClass().getResourceAsStream(
                        "/reports/product_sales_report.jrxml"
                );


        assertNotNull(
                reportStream,
                "找不到 product_sales_report.jrxml"
        );


        System.out.println(
                "JRXML 讀取成功"
        );


        /*
         * ==========================================
         * 2. 編譯 JRXML
         * ==========================================
         */
        JasperReport jasperReport;


        try {


            jasperReport =
                    JasperCompileManager.compileReport(
                            reportStream
                    );


            System.out.println(
                    "JRXML 編譯成功"
            );


        } catch (Exception e) {


            System.err.println();

            System.err.println(
                    "========================================"
            );

            System.err.println(
                    "JRXML 編譯失敗"
            );

            System.err.println(
                    "========================================"
            );


            e.printStackTrace();


            throw e;
        }


        assertNotNull(
                jasperReport,
                "JasperReport 編譯結果為 null"
        );


        /*
         * ==========================================
         * 3. 檢查 JasperReports Font Extension
         * ==========================================
         *
         * 確認 Jenkins / Maven classpath
         * 是否真的可以找到：
         *
         * 1. jasperreports_extension.properties
         * 2. fonts.xml
         * 3. NotoSansTC-Regular.ttf
         *
         */
        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "檢查 JasperReports Font Extension"
        );

        System.out.println(
                "========================================"
        );


        System.out.println(
                "jasperreports_extension.properties = "
                + getClass().getResource(
                        "/jasperreports_extension.properties"
                )
        );


        System.out.println(
                "fonts.xml = "
                + getClass().getResource(
                        "/fonts/fonts.xml"
                )
        );


        System.out.println(
                "NotoSansTC-Regular.ttf = "
                + getClass().getResource(
                        "/fonts/NotoSansTC-Regular.ttf"
                )
        );


        /*
         * ==========================================
         * 4. 直接測試 Java 21 能不能讀取 TTF
         * ==========================================
         *
         * 這一步非常重要。
         *
         * 如果這裡成功：
         *
         * Java 21
         *      ↓
         * NotoSansTC-Regular.ttf
         *      ↓
         * 可以正常建立 Font
         *
         * 表示 TTF 本身正常，
         * 問題比較可能在 JasperReports Font Extension。
         *
         *
         * 如果這裡失敗：
         *
         * 表示 Java 本身就無法解析這個 TTF。
         *
         */
        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "直接測試 Java 是否能讀取 TTF"
        );

        System.out.println(
                "========================================"
        );


        try (
                InputStream fontStream =
                        getClass().getResourceAsStream(
                                "/fonts/NotoSansTC-Regular.ttf"
                        )
        ) {


            assertNotNull(
                    fontStream,
                    "Java 找不到 NotoSansTC-Regular.ttf"
            );


            /*
             * 使用 Java AWT Font
             * 直接解析 TTF。
             */
            Font font =
                    Font.createFont(
                            Font.TRUETYPE_FONT,
                            fontStream
                    );


            System.out.println(
                    "Java TTF 載入成功"
            );


            System.out.println(
                    "Font Name："
                    + font.getFontName()
            );


            System.out.println(
                    "Font Family："
                    + font.getFamily()
            );


        } catch (Exception e) {


            System.err.println();

            System.err.println(
                    "========================================"
            );

            System.err.println(
                    "Java TTF 載入失敗"
            );

            System.err.println(
                    "========================================"
            );


            System.err.println(
                    "Exception 類型："
                    + e.getClass().getName()
            );


            System.err.println(
                    "錯誤訊息："
                    + e.getMessage()
            );


            e.printStackTrace();


            throw e;
        }


        /*
         * ==========================================
         * 5. 報表參數
         * ==========================================
         */
        Map<String, Object> parameters =
                new HashMap<>();


        /*
         * ==========================================
         * 6. 建立 PDF 輸出資料夾
         * ==========================================
         *
         * target/reports
         *
         */
        Path outputDirectory =
                Paths.get(
                        "target",
                        "reports"
                );


        Files.createDirectories(
                outputDirectory
        );


        /*
         * ==========================================
         * 7. PDF 輸出檔案
         * ==========================================
         */
        Path outputFile =
                outputDirectory.resolve(
                        "product-sales-report.pdf"
                );


        /*
         * ==========================================
         * 8. 取得 MySQL Connection
         * ==========================================
         */
        try (
                Connection connection =
                        dataSource.getConnection()
        ) {


            System.out.println();

            System.out.println(
                    "MySQL 連線成功："
                    + connection
                            .getMetaData()
                            .getURL()
            );


            /*
             * ======================================
             * 9. JasperReports 填入 MySQL 資料
             * ======================================
             *
             * JRXML
             *    ↓
             * SQL
             *    ↓
             * MySQL
             *    ↓
             * JasperPrint
             *
             */
            JasperPrint jasperPrint;


            try {


                System.out.println();

                System.out.println(
                        "開始執行 JasperFillManager.fillReport..."
                );


                jasperPrint =
                        JasperFillManager.fillReport(
                                jasperReport,
                                parameters,
                                connection
                        );


                System.out.println(
                        "報表資料填入成功"
                );


            } catch (Exception e) {


                System.err.println();

                System.err.println(
                        "========================================"
                );

                System.err.println(
                        "JasperReports 填入資料時發生錯誤"
                );

                System.err.println(
                        "========================================"
                );


                e.printStackTrace();


                System.err.println(
                        "========================================"
                );


                throw e;
            }


            assertNotNull(
                    jasperPrint,
                    "JasperPrint 為 null"
            );


            /*
             * ======================================
             * 10. 建立 PDF Exporter
             * ======================================
             */
            JRPdfExporter exporter =
                    new JRPdfExporter();


            /*
             * ======================================
             * 11. 指定 JasperPrint
             * ======================================
             */
            exporter.setExporterInput(
                    new SimpleExporterInput(
                            jasperPrint
                    )
            );


            /*
             * ======================================
             * 12. 指定 PDF 輸出位置
             * ======================================
             */
            exporter.setExporterOutput(
                    new SimpleOutputStreamExporterOutput(
                            outputFile.toFile()
                    )
            );


            /*
             * ======================================
             * 13. 正式輸出 PDF
             * ======================================
             */
            try {


                System.out.println();

                System.out.println(
                        "開始輸出 PDF..."
                );


                exporter.exportReport();


                System.out.println(
                        "PDF Export 完成"
                );


            } catch (Exception e) {


                System.err.println();

                System.err.println(
                        "========================================"
                );

                System.err.println(
                        "PDF 輸出時發生錯誤"
                );

                System.err.println(
                        "========================================"
                );


                e.printStackTrace();


                System.err.println(
                        "========================================"
                );


                throw e;
            }
        }


        /*
         * ==========================================
         * 14. 確認 PDF 是否存在
         * ==========================================
         */
        assertTrue(
                Files.exists(
                        outputFile
                ),
                "PDF 沒有成功建立"
        );


        /*
         * ==========================================
         * 15. 取得完整路徑
         * ==========================================
         */
        Path absolutePath =
                outputFile
                        .toAbsolutePath()
                        .normalize();


        /*
         * ==========================================
         * 16. 顯示成功資訊
         * ==========================================
         */
        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "產品銷售報表 PDF 產生成功"
        );

        System.out.println(
                "========================================"
        );


        System.out.println();

        System.out.println(
                "PDF Windows 路徑："
        );

        System.out.println(
                absolutePath
        );


        System.out.println();

        System.out.println(
                "PDF File URL："
        );

        System.out.println(
                absolutePath.toUri()
        );


        System.out.println();

        System.out.println(
                "PDF 檔案大小："
                + Files.size(
                        outputFile
                )
                + " bytes"
        );


        System.out.println(
                "========================================"
        );
    }
}