package com.Application.object;

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
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "price")
    private Integer price;

    @Basic
    @Column(name = "weight")
    private Integer weight;

    public ProductItem() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "ProductItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", weight=" + weight +
                '}';
    }
}
