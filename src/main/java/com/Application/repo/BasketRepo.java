package com.Application.repo;

import com.Application.dto.Basket;
import com.Application.dto.ProductItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasketRepo extends CrudRepository<Basket,Long>  {

    @Query("SELECT o FROM Basket o WHERE o.userId = ?1")
    Optional<Basket> findByUserId(Long userId);

    @Query("SELECT b.productList FROM Basket b WHERE b.userId = ?1")
    List<ProductItem> getProductListById(Long userId);

}

