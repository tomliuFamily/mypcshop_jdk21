package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.SiteStatsService;


@RestController
@RequestMapping("/api/stats")
@CrossOrigin(
    origins = "http://localhost:5173"
)
public class SiteStatsController {


    @Autowired
    private SiteStatsService siteStatsService;


    // ==========================================
    // 查瀏覽人次
    // ==========================================

    @GetMapping("/visits")
    public ResponseEntity<?> getVisits() {


        try {


            Long totalVisits =
                    siteStatsService
                        .getTotalVisits();


            Map<String, Long> result =
                    new HashMap<>();


            result.put(
                "totalVisits",
                totalVisits
            );


            return ResponseEntity.ok(
                    result
            );


        } catch (Exception e) {


            return ResponseEntity
                    .internalServerError()
                    .body(
                        "取得瀏覽人次失敗："
                        + e.getMessage()
                    );
        }
    }


    // ==========================================
    // 新增一次瀏覽
    // ==========================================

    @PostMapping("/visit")
    public ResponseEntity<?> addVisit() {


        try {


            Long totalVisits =
                    siteStatsService
                        .addVisit();


            Map<String, Long> result =
                    new HashMap<>();


            result.put(
                "totalVisits",
                totalVisits
            );


            return ResponseEntity.ok(
                    result
            );


        } catch (Exception e) {


            return ResponseEntity
                    .internalServerError()
                    .body(
                        "更新瀏覽人次失敗："
                        + e.getMessage()
                    );
        }
    }
}