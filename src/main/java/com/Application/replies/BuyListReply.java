package com.Application.replies;

import com.Application.object.ProductItem;

import java.util.List;

public class BuyListReply {
    private List<ProductItem> productList;
    private Integer fullPrice;

    public BuyListReply(List<ProductItem> productList, Integer fullPrice) {
        this.productList = productList;
        this.fullPrice = fullPrice;
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

    @Override
    public String toString() {
        return "BuyListReply{" +
                "productList=" + productList +
                ", fullPrice=" + fullPrice +
                '}';
    }
}
