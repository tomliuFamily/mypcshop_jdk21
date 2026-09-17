package com.example.demo.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.OrderReceiptItemDto;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;

@Service
public class OrderReportServiceImpl
        implements OrderReportService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;


    // ==========================================
    // Constructor
    // ==========================================

    public OrderReportServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserRepository userRepository) {

        this.orderRepository =
                orderRepository;

        this.orderItemRepository =
                orderItemRepository;

        this.userRepository =
                userRepository;
    }


    // ==========================================
    // Generate Order Receipt PDF
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public byte[] generateOrderReceipt(
            Integer orderId
    ) throws Exception {


        // ==========================================
        // 1. 檢查 orderId
        // ==========================================

        if (orderId == null) {

            throw new RuntimeException(
                    "訂單編號不可為空"
            );
        }


        // ==========================================
        // 2. 查詢訂單
        // ==========================================

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "找不到訂單："
                                                + orderId
                                        )
                        );


        // ==========================================
        // 3. 檢查訂單狀態
        // ==========================================

        if (order.getStatus() == null) {

            throw new RuntimeException(
                    "訂單狀態不存在"
            );
        }


        if (!order.getStatus()
                .equalsIgnoreCase("PAID")) {

            throw new RuntimeException(
                    "訂單尚未付款完成，無法列印。"
                    + "目前狀態："
                    + order.getStatus()
            );
        }


        // ==========================================
        // 4. 查詢會員
        // ==========================================

        Integer userId =
                order.getUserId();


        if (userId == null) {

            throw new RuntimeException(
                    "此訂單沒有會員編號"
            );
        }


        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "找不到會員："
                                                + userId
                                        )
                        );


        // ==========================================
        // 5. 查詢訂單明細
        // ==========================================

        List<OrderItem> orderItems =
                orderItemRepository
                        .findByOrderId(orderId);


        if (orderItems == null
                || orderItems.isEmpty()) {

            throw new RuntimeException(
                    "此訂單沒有商品明細："
                    + orderId
            );
        }


        // ==========================================
        // 6. OrderItem → Report DTO
        // ==========================================

        List<OrderReceiptItemDto> reportItems =
                new ArrayList<>();


        for (OrderItem item : orderItems) {


            Integer quantity =
                    item.getQuantity();


            Double price =
                    item.getPrice();


            if (quantity == null) {

                quantity = 0;
            }


            if (price == null) {

                price = 0.0;
            }


            Double subtotal =
                    price * quantity;


            OrderReceiptItemDto dto =
                    new OrderReceiptItemDto();


            dto.setProductId(
                    item.getProductId()
            );


            dto.setProductName(
                    safeString(
                            item.getProductName()
                    )
            );


            dto.setQuantity(
                    quantity
            );


            dto.setPrice(
                    price
            );


            dto.setSubtotal(
                    subtotal
            );


            reportItems.add(
                    dto
            );
        }


        // ==========================================
        // 7. Jasper DataSource
        // ==========================================

        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(
                        reportItems
                );


        // ==========================================
        // 8. Jasper Parameters
        // ==========================================

        Map<String, Object> parameters =
                new HashMap<>();


        parameters.put(
                "ORDER_ID",
                String.valueOf(
                        order.getId()
                )
        );


        parameters.put(
                "CUSTOMER_NAME",
                safeString(
                        user.getName()
                )
        );


        parameters.put(
                "CUSTOMER_EMAIL",
                safeString(
                        user.getEmail()
                )
        );


        parameters.put(
                "CUSTOMER_ADDRESS",
                safeString(
                        user.getAddress()
                )
        );


        // ==========================================
        // 訂單狀態中文顯示
        // ==========================================

        String statusText;


        if ("PAID".equalsIgnoreCase(
                order.getStatus()
        )) {

            statusText =
                    "已付款";

        } else {

            statusText =
                    safeString(
                            order.getStatus()
                    );
        }


        parameters.put(
                "ORDER_STATUS",
                statusText
        );


        // ==========================================
        // 訂單日期
        // ==========================================

        String orderDate =
                "";


        if (order.getOrderDate() != null) {


            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd HH:mm:ss"
                    );


            orderDate =
                    order.getOrderDate()
                            .format(formatter);
        }


        parameters.put(
                "ORDER_DATE",
                orderDate
        );


        // ==========================================
        // 訂單總金額
        // ==========================================

        Double totalAmount =
                order.getTotalAmount();


        if (totalAmount == null) {

            totalAmount =
                    0.0;
        }


        parameters.put(
                "TOTAL_AMOUNT",
                totalAmount
        );


        // ==========================================
        // 9. Jasper Resource Check
        // ==========================================

        System.out.println();
        System.out.println(
                "========== Jasper Resource Check =========="
        );


        System.out.println(
                "JRXML = "
                + getClass().getResource(
                        "/reports/order_receipt.jrxml"
                )
        );


        System.out.println(
                "fonts.xml = "
                + getClass().getResource(
                        "/fonts/fonts.xml"
                )
        );


        System.out.println(
                "TTF = "
                + getClass().getResource(
                        "/fonts/NotoSansTC-Regular.ttf"
                )
        );


        System.out.println(
                "==========================================="
        );


        // ==========================================
        // 10. 顯示 Runtime fonts.xml
        // ==========================================

        System.out.println();
        System.out.println(
                "========== Runtime fonts.xml =========="
        );


        try (
                InputStream fontXmlStream =
                        getClass()
                                .getResourceAsStream(
                                        "/fonts/fonts.xml"
                                )
        ) {


            if (fontXmlStream == null) {

                System.out.println(
                        "fonts.xml 找不到"
                );

            } else {


                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        fontXmlStream,
                                        StandardCharsets.UTF_8
                                )
                        );


                String line;


                while (
                        (line = reader.readLine())
                        != null
                ) {

                    System.out.println(
                            line
                    );
                }
            }
        }


        System.out.println(
                "======================================="
        );


        // ==========================================
        // 11. Java TTF Test
        // ==========================================

        System.out.println();
        System.out.println(
                "========== Java TTF Test =========="
        );


        try (
                InputStream fontStream =
                        getClass()
                                .getResourceAsStream(
                                        "/fonts/NotoSansTC-Regular.ttf"
                                )
        ) {


            if (fontStream == null) {

                throw new RuntimeException(
                        "TTF 不在 classpath"
                );
            }


            java.awt.Font testFont =
                    java.awt.Font.createFont(
                            java.awt.Font.TRUETYPE_FONT,
                            fontStream
                    );


            System.out.println(
                    "Font Name = "
                    + testFont.getFontName()
            );


            System.out.println(
                    "Font Family = "
                    + testFont.getFamily()
            );


            System.out.println(
                    "Font PS Name = "
                    + testFont.getPSName()
            );
        }


        System.out.println(
                "==================================="
        );


        // ==========================================
        // 12. 取得 JRXML
        // ==========================================

        InputStream reportStream =
                getClass()
                        .getResourceAsStream(
                                "/reports/order_receipt.jrxml"
                        );


        if (reportStream == null) {

            throw new RuntimeException(
                    "找不到報表檔案："
                    + "/reports/order_receipt.jrxml"
            );
        }


        // ==========================================
        // 13. Compile JRXML
        // ==========================================

        JasperReport jasperReport =
                JasperCompileManager
                        .compileReport(
                                reportStream
                        );


        // ==========================================
        // 14. Fill Report
        // ==========================================

        JasperPrint jasperPrint =
                JasperFillManager
                        .fillReport(
                                jasperReport,
                                parameters,
                                dataSource
                        );


        // ==========================================
        // 15. Export PDF
        // ==========================================

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();


        JRPdfExporter exporter =
                new JRPdfExporter();


        exporter.setExporterInput(
                new SimpleExporterInput(
                        jasperPrint
                )
        );


        exporter.setExporterOutput(
                new SimpleOutputStreamExporterOutput(
                        outputStream
                )
        );


        exporter.exportReport();


        // ==========================================
        // 16. 回傳 PDF byte[]
        // ==========================================

        return outputStream
                .toByteArray();
    }


    // ==========================================
    // safeString
    // ==========================================

    private String safeString(
            String value
    ) {


        if (value == null) {

            return "";
        }


        return value;
    }
}