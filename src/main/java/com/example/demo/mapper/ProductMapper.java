package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Product;


@Mapper
public interface ProductMapper {


    // =====================================================
    // MyBatis 自訂商品查詢
    //
    // 可同時處理：
    //
    // 1. keyword 商品名稱搜尋
    // 2. minPrice 最低價格
    // 3. maxPrice 最高價格
    // 4. keyword + 價格一起搜尋
    //
    // =====================================================

    List<Product> searchProducts(

            @Param("keyword")
            String keyword,

            @Param("minPrice")
            Double minPrice,

            @Param("maxPrice")
            Double maxPrice

    );

}