package com.Application.errors;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ErrorRepo extends CrudRepository<Error, Long> {
    @Query("SELECT e FROM Error e WHERE e.errorNum = ?1 AND e.lang = ?2")
    Error getByErrorNumAndLanguage(int num, String lang);
}
