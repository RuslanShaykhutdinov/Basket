package com.Application.object;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @SequenceGenerator(name="productIdGenerator", sequenceName = "product_id_seq", schema = "public", initialValue = 1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "productIdGenerator")
    @Column(name = "product_id")
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

    @Basic
    @Column(name = "image_url")
    private String imageUrl;

    public Product() {
        this.availability = true;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId.equals(product.productId) && name.equals(product.name) && price.equals(product.price) && weight.equals(product.weight) && Objects.equals(info, product.info) && availability.equals(product.availability) && imageUrl.equals(product.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name, price, weight, info, availability, imageUrl);
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
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
