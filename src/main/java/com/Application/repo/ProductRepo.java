package com.Application.repo;

import com.Application.dto.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends CrudRepository<Product,Long> {
    @Query("SELECT o FROM Product o where o.productId = ?1")
    Optional <Product> findProduct(Long getProductId);

    @Query("SELECT p FROM Product p WHERE p.weight > 0")
    List<Product> findAllAvailable();

    @Query("SELECT p FROM Product p WHERE p.name = ?1")
    Product findByName(String name);

    @Query("SELECT p FROM Product p WHERE p.categoryId = ?1")
    List<Product> findByCategory(Long categoryId);
}
