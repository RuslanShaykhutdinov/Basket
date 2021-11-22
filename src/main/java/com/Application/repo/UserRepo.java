package com.Application.repo;

import com.Application.object.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.login = ?1 AND u.blocked = false ")
    Optional<User> findByLogin(String logIn);
}
