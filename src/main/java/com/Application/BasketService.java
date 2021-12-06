package com.Application;

import com.Application.dto.*;
import com.Application.replies.BuyListReply;
import com.Application.repo.BasketRepo;
import com.Application.repo.CardRepo;
import com.Application.repo.ProductItemRepo;
import com.Application.repo.ProductRepo;
import com.Application.settings.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.abs;

@Service
public class BasketService {

    private static final Logger log = LoggerFactory.getLogger(BasketService.class);


    private  final ProductRepo productRepo;
    private final BasketRepo basketRepo;
    private final CardRepo cardRepo;
    private final ProductItemRepo productItemRepo;

    private static final int INITIAL_SUM = 0;
    private static final long ADULTHOOD = (16 * 365 + 5 * 366) * 24 * 60 * 60 * 1000L;

    @Autowired
    public BasketService(ProductRepo productRepo, BasketRepo basketRepo, CardRepo cardRepo, ProductItemRepo productItemRepo) {
        this.productRepo = productRepo;
        this.basketRepo = basketRepo;
        this.cardRepo = cardRepo;
        this.productItemRepo = productItemRepo;
    }

    public void login(User user) {
        log.info("> Service login");
        Basket basket = new Basket();
        basket.setUserId(user.getUserId());
        basket.setProductList(new LinkedList<>());
        basketRepo.save(basket);
        log.info("< Service login");
    }
    public RestError adding(Product product, Integer weight, Basket basket){
        log.info("> Service adding");
        int difWeight = product.getWeight() - weight;
        if (difWeight < 0) {
            log.error("Вес товара превышает запас на " + abs(difWeight));
            log.info("< Service adding");
            return new RestError(4," Перевес", "Вес товара превышает запас на " + abs(difWeight),HttpStatus.BAD_REQUEST);
        }
        if(product.getAvailability()){
            List<ProductItem> productList = basket.getProductList();
            ProductItem sameProduct = productList.stream().filter(p -> p.getProductId().equals(product.getProductId())).findFirst().orElse(null);
            if (sameProduct == null){
                ProductItem item = new ProductItem();
                item.setProductId(product.getProductId());
                item.setName(product.getName());
                item.setPrice(product.getPrice() * weight);
                item.setWeight(weight);
                item.setImageUrl(product.getImageUrl());
                productItemRepo.save(item);
                productList.add(item);
                basketRepo.save(basket);
            } else {
                int newWeight = sameProduct.getWeight() + weight;
                sameProduct.setWeight(newWeight);
                sameProduct.setPrice(product.getPrice() * newWeight);
                productItemRepo.save(sameProduct);
            }
            Integer count = productList.size();
            log.info("< Service adding");
            return new RestError(count, HttpStatus.OK);
        } else {
            log.error("Товар закончился!");
            log.info("< Service adding");
            return new RestError(6,"Товар закончился!","Товар закончился!", HttpStatus.BAD_REQUEST);
        }
    }

    public boolean checkAge(Basket basket) {
        log.info("> Service checkAge");
        boolean allowed = true;
        Date date = basket.getUser().getBirthday();
        if(date == null){
            allowed = false;
        } else {
            if (Math.abs(new Date().getTime() - date.getTime()) < ADULTHOOD) {
                allowed = false;
            }
        }
        log.info("< Service checkAge");
        return  allowed;
    }

//    public RestError removing(Product product, Basket basket) {
//        log.info("> Service removing");
//        basket.setProductList(basket.getProductList().stream().filter(productItem -> !productItem.getName().equals(product.getName())).collect(Collectors.toList()));
//        basketRepo.save(basket);
//        log.info("< Service removing");
//        return new RestError("Продукт " + product.getName() + " успешно удален из вашего списка покупок!",HttpStatus.OK);
//    }

    public RestError checking(Card card, Basket basket) {
        log.info("> Service checking");
        List<ProductItem> productList = basket.getProductList();
        int sum = findFullPrice(productList);
        int remainder = card.getAmountOfMoney();
        if(sum < remainder){
            productList.forEach(this::changeProducts);
            card.setAmountOfMoney(remainder - sum);
            cardRepo.save(card);
        } else {
            log.error("Недостаточно средств на карте!");
            log.info("< Service checking");
            return new RestError(9,"Not enough money","Not enough money ",HttpStatus.BAD_REQUEST);
        }
        log.info("Продукты изменены в базе!");
        RestError re = new RestError();
        re.setData(new BuyListReply(productList,sum));
        cleanBasket(basket);
        log.info("Оплата прошла успешно");
        log.info("< Service checking");
        return re;
    }

    public void changeProducts(ProductItem item) {
        log.info("> Service changeProducts");
        Product product = productRepo.findByName(item.getName());
        int newWeight = product.getWeight() - item.getWeight();
        product.setWeight(newWeight);
        if(newWeight <= 0){
            product.setAvailability(false);
            Iterable<Basket> baskets = basketRepo.findAll();
            baskets.forEach(basket -> basket.getProductList().remove(item));
            basketRepo.saveAll(baskets);
        }
        productRepo.save(product);
        log.info("< Service changeProducts");
    }

    public Integer findFullPrice(List<ProductItem> productList) {
        log.info("> Service findFullPrice");
        int fullPrice = INITIAL_SUM;
        for (ProductItem product: productList) {
            fullPrice += product.getPrice() * product.getWeight();
        }
        log.info("< Service findFullPrice");
        return fullPrice;
    }

    public void cleanBasket(Basket basket) {
        log.info("> Service cleanBasket");
        basket.setProductList(new LinkedList<>());
        log.info("Корзина очищена!");
        basketRepo.save(basket);
        log.info("< Service cleanBasket");
    }

    public RestError removingByNum(Product product, Basket basket, Integer weight) {
        log.info("> Service removingByNum");
        List<ProductItem> productList = basket.getProductList();
        ProductItem item = productList.stream().filter(productItem -> productItem.getProductId().equals(product.getProductId())).findFirst().orElse(null);
        int newWeight = 0;
        if (item != null) {
            newWeight = item.getWeight() - weight;
            if (newWeight > 0){
                item.setWeight(newWeight);
                productItemRepo.save(item);
            } else {
                // если пользователь ввел меньше чем у него было в корзине то продукт удаляется
                productList.remove(item);
                basket.setProductList(productList);
            }
            basketRepo.save(basket);
        } else {
            return  new RestError(15,"UnexpectedError",HttpStatus.BAD_REQUEST);
        }
        log.info("< removingByNum");
        return new RestError("OK",HttpStatus.OK);
    }
}
