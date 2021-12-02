package com.Application.replies;

import com.Application.dto.ProductItem;

import java.util.List;

public class BuyListReply {
    private List<ProductItem> productList;
    private Integer fullPrice;
    private Integer productsNum;

    public BuyListReply(List<ProductItem> productList, Integer fullPrice) {
        this.productList = productList;
        this.fullPrice = fullPrice;
    }

    public BuyListReply(List<ProductItem> productList, Integer fullPrice, Integer productsNum) {
        this.productList = productList;
        this.fullPrice = fullPrice;
        this.productsNum = productsNum;
    }

    public List<ProductItem> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductItem> productList) {
        this.productList = productList;
    }

    public Integer getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(Integer fullPrice) {
        this.fullPrice = fullPrice;
    }

    public Integer getProductsNum() {
        return productsNum;
    }

    public void setProductsNum(Integer productsNum) {
        this.productsNum = productsNum;
    }

    @Override
    public String toString() {
        return "BuyListReply{" +
                "productList=" + productList +
                ", fullPrice=" + fullPrice +
                ", productsNum=" + productsNum +
                '}';
    }
}
