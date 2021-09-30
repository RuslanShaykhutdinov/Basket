package com.basket.Basket.repo;

import com.basket.Basket.object.Basket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public interface BasketRepo extends CrudRepository<Basket,Long>  {

    @Query("SELECT o FROM Basket o where o.user.userID = ?1")
    Optional<Basket> findByUserId(Long userId);

    HashMap<List<Long>, Integer> map = new HashMap<>();
}

