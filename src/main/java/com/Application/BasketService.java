package com.Application;

import com.Application.object.*;
import com.Application.repo.CardRepo;
import com.Application.repo.ProductItemRepo;
import com.Application.repo.ProductRepo;
import com.Application.repo.BasketRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.Math.abs;

@Service
public class BasketService {

    private static final Logger log = LoggerFactory.getLogger(BasketService.class);


    private  final ProductRepo productRepo;
    private final BasketRepo basketRepo;
    private final CardRepo cardRepo;
    private final ProductItemRepo productItemRepo;

    private static final int INITIAL_SUM = 0;

    @Autowired
    public BasketService(ProductRepo productRepo, BasketRepo basketRepo, CardRepo cardRepo, ProductItemRepo productItemRepo) {
        this.productRepo = productRepo;
        this.basketRepo = basketRepo;
        this.cardRepo = cardRepo;
        this.productItemRepo = productItemRepo;
    }

    public void changeProducts(Long productId, Integer weight) {
        Product product = productRepo.findById(productId).orElse(null);
        int newWeight = product.getWeight() - weight;
        product.setWeight(newWeight);
        if(newWeight <= 0){
            product.setAvailability(false);
        }
    }

    public void login(User user) {
        log.info("> Service login");
        Basket basket = new Basket();
        basket.setUserId(user.getUserId());
        basket.setProductList(new LinkedList<>());
        basketRepo.save(basket);
        log.info("< Service login");
    }
    public RestError addingV2(Product product,Integer weight, Basket basket){
        int difWeight = product.getWeight() - weight;
        if (difWeight < 0) {
            log.error("Вес товара превышает запас на " + abs(difWeight));
            return new RestError(4," Перевес", "Вес товара превышает запас на " + abs(difWeight),HttpStatus.BAD_REQUEST);
        }
        if(product.getAvailability()){
            ProductItem item = new ProductItem();
            item.setName(product.getName());
            item.setPrice(product.getPrice());
            item.setWeight(weight);
            productItemRepo.save(item);
            basket.getProductList().add(item);
            basketRepo.save(basket);
            return new RestError(product.getName() + " успешно добавлен в вашу корзину!", HttpStatus.OK);
        } else {
            log.error("Товар закончился!");
            return new RestError(6,"Товар закончился!","Товар закончился!", HttpStatus.BAD_REQUEST);
        }
    }

    public RestError adding(Long productId,Integer weight, Basket basket) {
        Product chosenProduct = productRepo.findById(productId).orElse(null);
        int difWeight = chosenProduct.getWeight() - weight;
        if (difWeight < 0) {
            log.error("Вес товара превышает запас на " + abs(difWeight));
            return new RestError(4," Перевес", "Вес товара превышает запас на " + abs(difWeight),HttpStatus.BAD_REQUEST);
        }
        Long userId = basket.getUser().getUserId();
        if(chosenProduct.getName().equals("Alcohol") && basket.getUser().getAge() <= 21){
            log.info("Товар не разрешен!");
            return new RestError(5,"Товар запрещен!", "Товар запрещен", HttpStatus.BAD_REQUEST);
        }
        if(chosenProduct.getAvailability()){
            HashMap<List<Long>, Integer> map = basketRepo.map;
            List<Long> key = Arrays.asList(userId,productId);
            if(map.containsKey(key)){
                map.put(key,map.get(key) + weight);
                return new RestError(chosenProduct.getName() + " успешно приумножен!", HttpStatus.OK);
            } else {
                map.put(new ArrayList<>(Arrays.asList(userId,productId)),weight);
                //basket.getProductList().add(chosenProduct);
                basketRepo.save(basket);
                return new RestError(chosenProduct.getName() + " успешно добавлен в вашу корзину!", HttpStatus.OK);
            }
        } else {
            log.error("Товар закончился!");
            return new RestError(6,"Товар закончился!","Товар закончился!", HttpStatus.BAD_REQUEST);
        }
    }

    public RestError creatingList(Basket basket) {
        if(!basket.getProductList().isEmpty()){
            Long userId = basket.getUser().getUserId();
            StringBuilder s = new StringBuilder();
            int sum = INITIAL_SUM;
//            for (Product product: basket.getProductList()) {
//                Integer weight = basketRepo.map.get(Arrays.asList(userId, product.getProductId()));
//                Integer objSum = product.getPrice() * weight;
//                s.append("\n").append(product.getName()).append(" ").append(objSum);
//                sum += objSum;
//            }
            s.append("\t").append(sum);
            String s1 = "Список покупок пользователя " + basket.getUser().getName() + ": " + s;
            return new RestError(s1,HttpStatus.OK);
        } else {
            log.error("Список покупок пользователя пуст! Сумма к оплате: " + INITIAL_SUM);
            return new RestError("Список покупок пуст! Сумма к оплате: " + INITIAL_SUM,HttpStatus.BAD_REQUEST);
        }
    }

    public void cleanBasket(Basket basket) {
//        for (Product product: basket.getProductList()) {
//            basketRepo.map.remove(Arrays.asList(basket.getUser().getUserId(), product.getProductId()));
//        }
        basket.setProductList(new LinkedList<>());
        log.info("Корзина очищена!");
        basketRepo.save(basket);
    }

    public RestError removing(Product product, Basket basket) {
        basketRepo.map.remove(Arrays.asList(basket.getUser().getUserId(), product.getProductId()));
        basket.getProductList().remove(product);
        return new RestError("Продукт " + product.getName() + " успешно удален из вашего списка покупок!",HttpStatus.OK);
    }

//    public RestError checking(Long userId, Card card, Basket basket) {
//        List<Product> productList = basket.getProductList();
//        int sum = INITIAL_SUM;
//        int remainder = card.getAmountOfMoney();
//        for (Product product: productList) {
//            Integer weight = basketRepo.map.get(Arrays.asList(userId, product.getProductId()));
//            System.out.println(weight);
//            int objSum = product.getPrice() * weight;
//            sum += objSum;
//            remainder -= sum;
//            if(remainder > 0){
//                changeProducts(product.getProductId(),weight);
//                basketRepo.map.remove(Arrays.asList(userId, product.getProductId()));
//                productRepo.save(product);
//                card.setAmountOfMoney(remainder);
//            } else {
//                log.error("Недостаточно средств на карте!");
//                return new RestError(9,"Not enough money","Not enough money ",HttpStatus.BAD_REQUEST);
//            }
//        }
//        cardRepo.save(card);
//        log.info("Продукты изменены в базе!");
//        basketRepo.delete(basket);
//        log.info("Оплата прошла успешно");
//        return new RestError(0,null,"С карты списано " + sum + ". Ваш остаток по счету равен :" + remainder +
//                " Спасибо за покупку! Ждем вас снова!",HttpStatus.OK);
//    }

    public boolean checkAge(Basket basket) {
        boolean allowed = true;
        Integer age = basket.getUser().getAge();
        if(age == null && age < 21){
            allowed = false;
        }
        return  allowed;
    }

    public Integer findFullPrice(List<ProductItem> productList) {
        Integer fullPrice = null;
        for (ProductItem product: productList) {
            fullPrice += product.getPrice() * product.getWeight();
        }
        return fullPrice;
    }
}
