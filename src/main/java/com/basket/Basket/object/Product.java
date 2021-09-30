package com.basket.Basket.object;

import javax.persistence.*;

@Entity
@Table(name = "Products")
public class Product {
    @Id
    @SequenceGenerator(name="productIdGenerator", sequenceName = "product_id_seq", schema = "public", initialValue = 1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productIdGenerator")
    @Column(name = "id")
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

    @Column(name = "info")
    private  String info;

    @Basic
    @Column(name = "availability")
    private Boolean availability;

    public Product() {
        this.availability = true;
    }

    public Product(String name, Integer price, Integer weight) {
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.availability = true;
    }

    public Product(String name, Integer price, Integer weight, String info, Boolean availability) {
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.info = info;
        this.availability = availability;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", weight=" + weight +
                ", info='" + info + '\'' +
                ", availability=" + availability +
                '}';
    }
}
