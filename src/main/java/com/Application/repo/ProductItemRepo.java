package com.Application.repo;

import com.Application.dto.ProductItem;
import org.springframework.data.repository.CrudRepository;

public interface ProductItemRepo extends CrudRepository<ProductItem,Long> {
}
