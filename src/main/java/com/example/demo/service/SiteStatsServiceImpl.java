package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.SiteStats;
import com.example.demo.repository.SiteStatsRepository;


@Service
public class SiteStatsServiceImpl
        implements SiteStatsService {


    private static final Integer SITE_ID = 1;


    private static final Long DEFAULT_VISITS =
            10000L;


    @Autowired
    private SiteStatsRepository siteStatsRepository;


    // ==========================================
    // 取得瀏覽人次
    // ==========================================

    @Override
    public Long getTotalVisits() {


        SiteStats stats =
                getOrCreateStats();


        return stats.getTotalVisits();
    }


    // ==========================================
    // 增加一次瀏覽
    // ==========================================

    @Override
    @Transactional
    public Long addVisit() {


        SiteStats stats =
                getOrCreateStats();


        Long currentVisits =
                stats.getTotalVisits();


        if (currentVisits == null) {

            currentVisits =
                    DEFAULT_VISITS;
        }


        stats.setTotalVisits(
                currentVisits + 1
        );


        SiteStats saved =
                siteStatsRepository
                    .save(stats);


        return saved.getTotalVisits();
    }


    // ==========================================
    // 找不到資料時建立 Default 10000
    // ==========================================

    private SiteStats getOrCreateStats() {


        return siteStatsRepository
                .findById(
                    SITE_ID
                )
                .orElseGet(
                    () -> {

                        SiteStats stats =
                                new SiteStats();


                        stats.setId(
                                SITE_ID
                        );


                        stats.setTotalVisits(
                                DEFAULT_VISITS
                        );


                        return siteStatsRepository
                                .save(stats);
                    }
                );
    }
}