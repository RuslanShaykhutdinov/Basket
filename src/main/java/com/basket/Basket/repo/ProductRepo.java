package com.basket.Basket.repo;

import com.basket.Basket.object.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends CrudRepository<Product,Long> {
    @Query("SELECT o FROM Product o where o.productId = ?1")
    Optional <Product> findProduct(Object getProductId);

    List<Product> findAll();
}
