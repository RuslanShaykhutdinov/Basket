package com.Application.dto;

import javax.persistence.*;

@Entity
@Table(name = "product_item")
public class ProductItem {

    @Id
    @SequenceGenerator(name="productItemIdGenerator", sequenceName = "product_item_id_seq", schema = "public", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productItemIdGenerator")
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "productId")
    private Long productId;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "price")
    private Integer price;

    @Basic
    @Column(name = "weight")
    private Integer weight;

    @Basic
    @Column(name = "image_url")
    private String imageUrl;

    public ProductItem() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ProductItem{" +
                "id=" + id +
                ", productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", weight=" + weight +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
