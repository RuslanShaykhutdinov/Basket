package com.basket.Basket;

import com.basket.Basket.object.Basket;
import com.basket.Basket.object.Product;
import com.basket.Basket.repo.ProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.abs;

@Service
public class BasketService {

    private static final Logger log = LoggerFactory.getLogger(BasketService.class);

    @Autowired
    ProductRepo productRepo;

    public Product checkWeight(Long productId, Integer weight) {
        Optional<Product> optionalProduct = productRepo.findProduct(productId);
        if(optionalProduct.isPresent()){
            Product chosenProduct = optionalProduct.get();
            int difWeight = chosenProduct.getWeight() - weight;
            if (difWeight < 0) {
                log.error("Вес товара превышает запас на " + abs(difWeight));
            }
            return chosenProduct;
        } else {
            log.error("Введен неправильный ид адрес!");
            return null;
        }
    }

    public void changeProducts(Long productId, Integer weight) {
        Optional <Product> optionalProduct = productRepo.findById(productId);
        if(optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            int newWeight = product.getWeight() - weight;
            product.setWeight(newWeight);
            if(newWeight < 0){
                product.setAvailability(false);
            }
        }
    }
}
