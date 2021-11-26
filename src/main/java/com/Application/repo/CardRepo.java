package com.Application.repo;

import com.Application.dto.Card;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepo extends CrudRepository<Card,Long> {

    @Query("SELECT o FROM Card o where o.user.userId = ?1")
    Optional <Card> findByUserId(Long userId);
}
