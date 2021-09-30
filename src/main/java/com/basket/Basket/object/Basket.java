package com.basket.Basket.object;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Baskets")
public class Basket {
    @Id
    @SequenceGenerator(name="basketIdGenerator", sequenceName = "basket_id_seq", schema = "public", initialValue = 1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "basketIdGenerator")
    @Column(name = "id")
    private Long baskedId;

    @ManyToOne
    @JoinColumn(name = "user_basket", nullable = false)
    private User user;

    @ManyToMany
    @Column(name = "list_of_products")
    private List<Product> productList;

    public Basket() {
    }

    public Basket( User user, List<Product> productList) {
        this.user = user;
        this.productList = productList;
    }

    public Basket(Long baskedId, User user, List<Product> productList) {
        this.baskedId = baskedId;
        this.user = user;
        this.productList = productList;
    }

    public Long getBaskedId() {
        return baskedId;
    }

    public void setBaskedId(Long baskedId) {
        this.baskedId = baskedId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public String toString() {
        return "Basket{" +
                "baskedId=" + baskedId +
                ", user=" + user +
                ", productList=" + productList +
                '}';
    }
}
