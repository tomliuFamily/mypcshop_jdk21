package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "site_stats")
public class SiteStats {


    @Id
    private Integer id;


    @Column(
        name = "total_visits",
        nullable = false
    )
    private Long totalVisits;


    public SiteStats() {
    }


    public SiteStats(
            Integer id,
            Long totalVisits) {

        this.id =
                id;

        this.totalVisits =
                totalVisits;
    }


    public Integer getId() {
        return id;
    }


    public void setId(
            Integer id) {

        this.id =
                id;
    }


    public Long getTotalVisits() {
        return totalVisits;
    }


    public void setTotalVisits(
            Long totalVisits) {

        this.totalVisits =
                totalVisits;
    }
}