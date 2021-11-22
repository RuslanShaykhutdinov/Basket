package com.Application.repo;

import com.Application.object.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends CrudRepository<Product,Long> {
    @Query("SELECT o FROM Product o where o.productId = ?1")
    Optional <Product> findProduct(Object getProductId);

    @Query("SELECT p FROM Product p WHERE p.weight > 0")
    List<Product> findAllAvailable();
}
