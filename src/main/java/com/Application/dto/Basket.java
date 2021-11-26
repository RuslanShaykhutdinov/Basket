package com.Application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "baskets")
public class Basket {
    @Id
    @SequenceGenerator(name="basketIdGenerator", sequenceName = "basket_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "basketIdGenerator")
    @Column(name = "basket_id")
    private Long baskedId;

    @Basic
    @Column(name = "user_id")
    private Long userId;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, updatable = false, insertable = false)
    private User user;

    @OneToMany
    @Column(name = "list_of_products")
    private List<ProductItem> productList;

    public Basket() {
    }

    public Long getBaskedId() {
        return baskedId;
    }

    public void setBaskedId(Long baskedId) {
        this.baskedId = baskedId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public List<ProductItem> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductItem> productList) {
        this.productList = productList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Basket basket = (Basket) o;
        return baskedId.equals(basket.baskedId) && userId.equals(basket.userId) && user.equals(basket.user) && Objects.equals(productList, basket.productList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baskedId, userId, user, productList);
    }

    @Override
    public String toString() {
        return "Basket{" +
                "baskedId=" + baskedId +
                ", userId=" + userId +
                ", user=" + user +
                ", productList=" + productList +
                '}';
    }
}
