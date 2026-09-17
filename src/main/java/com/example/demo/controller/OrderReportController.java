package com.example.demo.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.OrderReportService;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderReportController {


    private final OrderReportService orderReportService;


    public OrderReportController(
            OrderReportService orderReportService
    ) {

        this.orderReportService =
                orderReportService;
    }


    @GetMapping(
            value = "/{orderId}/receipt",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> getOrderReceipt(
            @PathVariable Integer orderId
    ) {


        try {


            byte[] pdf =
                    orderReportService
                            .generateOrderReceipt(
                                    orderId
                            );


            HttpHeaders headers =
                    new HttpHeaders();


            headers.setContentType(
                    MediaType.APPLICATION_PDF
            );


            headers.setContentDisposition(
                    ContentDisposition
                            .inline()
                            .filename(
                                    "order-"
                                    + orderId
                                    + ".pdf"
                            )
                            .build()
            );


            headers.setContentLength(
                    pdf.length
            );


            return new ResponseEntity<>(
                    pdf,
                    headers,
                    HttpStatus.OK
            );


        } catch (RuntimeException e) {


            e.printStackTrace();


            return ResponseEntity
                    .badRequest()
                    .build();


        } catch (Exception e) {


            e.printStackTrace();


            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}