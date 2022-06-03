package com.Application.replies;

import com.Application.dto.ProductItem;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class BuyListReply {
    private List<ProductItem> productList;
    private Integer fullPrice;
    private Integer count;

    public BuyListReply(List<ProductItem> productList, Integer fullPrice) {
        this.productList = productList;
        this.fullPrice = fullPrice;
    }

    public BuyListReply(List<ProductItem> productList, Integer fullPrice, int count) {
        this.productList = productList;
        this.fullPrice = fullPrice;
        this.count = count;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "BuyListReply{" +
                "productList=" + productList +
                ", fullPrice=" + fullPrice +
                '}';
    }
}
