package com.Application;

import com.Application.dto.*;
import com.Application.replies.BuyListReply;
import com.Application.repo.BasketRepo;
import com.Application.repo.CardRepo;
import com.Application.repo.ProductItemRepo;
import com.Application.repo.ProductRepo;
import com.Application.settings.RestError;
import com.Application.translations.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
        log.info("> Service login with userId={}", user.getUserId());
        Basket basket = new Basket();
        basket.setUserId(user.getUserId());
        basket.setProductList(new ArrayList<>());
        basketRepo.save(basket);
        log.info("< Service login");
    }
    public RestError adding(Product product, Integer weight, Basket basket, String lang){
        Long productId = product.getProductId();
        log.info("> Service adding with productId={}, weight={}, basketId={}, lang={}", productId, weight, basket.getBaskedId(), lang);
        int difWeight = product.getWeight() - weight;
        if (difWeight < 0) {
            log.error("Вес товара превышает запас на " + abs(difWeight));
            log.info("< Service adding");
            return new RestError(4," Вес товара превышает запас на " , abs(difWeight),HttpStatus.BAD_REQUEST);
        }
        if(product.getAvailability()){
            List<ProductItem> productList = basket.getProductList();
            ProductItem sameProduct = productList.stream().filter(p -> p.getProductId().equals(productId)).findFirst().orElse(null);
            if (sameProduct == null){
                ProductItem item = new ProductItem();
                item.setProductId(productId);
                if (!"en".equals(lang))
                    item.setName(Translation.productNames.get(productId + lang));
                else
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
            log.error("Товар закончился! productId={}", productId);
            log.info("< Service adding");
            return new RestError(6,"Товар закончился!", HttpStatus.BAD_REQUEST);
        }
    }

    public boolean checkAge(Basket basket) {
        log.info("> Service checkAge with userId={}", basket.getUserId());
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

    public RestError checking(Card card, Basket basket, String lang) {
        log.info("> Service checking cardId={}, basketId={}", card.getCardId(), basket.getBaskedId());
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
            return new RestError(9,"Not enough money",HttpStatus.BAD_REQUEST);
        }
        if (!"en".equals(lang))
            productList.forEach(p -> p.setName(Translation.productNames.get(p.getProductId()+ lang)));
        log.info("Продукты изменены в базе!");
        RestError re = new RestError();
        re.setData(new BuyListReply(productList,sum, 0));
        cleanBasket(basket);
        log.info("Оплата прошла успешно");
        log.info("< Service checking");
        return re;
    }

    public void changeProducts(ProductItem item) {
        log.info("> Service changeProducts productId={}", item.getProductId());
        Product product = productRepo.findProduct(item.getProductId()).get();
        int newWeight = product.getWeight() - item.getWeight();
        product.setWeight(newWeight);
        if(newWeight <= 0){
            product.setAvailability(false);
            Iterable<Basket> baskets = basketRepo.findAll();
            // удаляем закончившийся продукт из всех корзин
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
            fullPrice += product.getPrice();
        }
        log.info("< Service findFullPrice");
        return fullPrice;
    }

    public void cleanBasket(Basket basket) {
        log.info("> Service cleanBasket with userId {}", basket.getUserId());
        basket.setProductList(new ArrayList<>());
        log.info("Корзина очищена!");
        basketRepo.save(basket);
        log.info("< Service cleanBasket");
    }

    public RestError removingByNum(Product product, Basket basket, Integer weight) {
        log.info("> Service removingByNum productId={}, basketId={}, weight={}", product.getProductId(), basket.getBaskedId(), weight);
        List<ProductItem> productList = basket.getProductList();
        ProductItem item = productList.stream().filter(productItem -> productItem.getProductId().equals(product.getProductId())).findFirst().orElse(null);

        int newWeight = item.getWeight() - weight;
        if (newWeight > 0){
            item.setWeight(newWeight);
            item.setPrice(product.getPrice() * newWeight);
            productItemRepo.save(item);
        } else {
            // если пользователь ввел меньше чем у него было в корзине, то продукт удаляется
            productList.remove(item);
            basket.setProductList(productList);
        }
        basketRepo.save(basket);

        Integer count = productList.size();
        log.info("< removingByNum");
        return new RestError(count,HttpStatus.OK);
    }
}
