package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        System.out.println("========================================");
        System.out.println("開始產生產品銷售報表");
        System.out.println("========================================");


        /*
         * 1. 讀取 JRXML
         *
         * 實際檔案位置：
         * src/test/resources/reports/product_sales_report.jrxml
         */
        InputStream reportStream =
                getClass().getResourceAsStream(
                        "/reports/product_sales_report.jrxml"
                );

        assertNotNull(
                reportStream,
                "找不到 product_sales_report.jrxml"
        );

        System.out.println("JRXML 讀取成功");
        


        /*
         * 2. 編譯 JRXML
         */
        JasperReport jasperReport;

        try {

            jasperReport =
                    JasperCompileManager.compileReport(
                            reportStream
                    );

            System.out.println("JRXML 編譯成功");
            
            System.out.println("========================================");
            System.out.println("檢查 JasperReports Font Extension");
            System.out.println("========================================");

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
            
            
            

        } catch (Exception e) {

            System.err.println();
            System.err.println("========================================");
            System.err.println("JRXML 編譯失敗");
            System.err.println("========================================");

            e.printStackTrace();

            throw e;
        }


        assertNotNull(
                jasperReport,
                "JasperReport 編譯結果為 null"
        );


        /*
         * 3. 報表參數
         */
        Map<String, Object> parameters =
                new HashMap<>();


        /*
         * 4. 建立 PDF 輸出資料夾
         *
         * target/reports
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
         * 5. PDF 檔名
         */
        Path outputFile =
                outputDirectory.resolve(
                        "product-sales-report.pdf"
                );


        /*
         * 6. 取得 MySQL Connection
         */
        try (Connection connection =
                     dataSource.getConnection()) {

            System.out.println(
                    "MySQL 連線成功："
                    + connection
                            .getMetaData()
                            .getURL()
            );


            /*
             * 7. JasperReport 填入 MySQL 資料
             *
             * 如果中文字型、SQL、Field、
             * fonts.xml 等設定有問題，
             * 通常會在這裡發生錯誤。
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

                /*
                 * 印出完整 Exception
                 */
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
             * 8. 建立 PDF Exporter
             */
            JRPdfExporter exporter =
                    new JRPdfExporter();


            /*
             * 9. 指定報表內容
             */
            exporter.setExporterInput(
                    new SimpleExporterInput(
                            jasperPrint
                    )
            );


            /*
             * 10. 指定輸出的 PDF
             */
            exporter.setExporterOutput(
                    new SimpleOutputStreamExporterOutput(
                            outputFile.toFile()
                    )
            );


            /*
             * 11. 正式輸出 PDF
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
         * 12. 確認 PDF 是否存在
         */
        assertTrue(
                Files.exists(outputFile),
                "PDF 沒有成功建立"
        );


        /*
         * 13. 取得完整路徑
         */
        Path absolutePath =
                outputFile
                        .toAbsolutePath()
                        .normalize();


        /*
         * 14. 顯示成功資訊
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
                + Files.size(outputFile)
                + " bytes"
        );

        System.out.println(
                "========================================"
        );
    }
}