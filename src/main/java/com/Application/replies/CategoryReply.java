package com.Application.replies;

import com.Application.dto.Product;

import java.util.List;

public class CategoryReply {
    private String title;
    private List<Product> data;

    public CategoryReply(String title, List<Product> data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Product> getData() {
        return data;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CategoryReply{" +
                "title='" + title + '\'' +
                ", data=" + data +
                '}';
    }
}
