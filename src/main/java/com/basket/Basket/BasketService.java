package com.basket.Basket;

import com.basket.Basket.object.Basket;
import com.basket.Basket.object.Card;
import com.basket.Basket.object.Product;
import com.basket.Basket.object.User;
import com.basket.Basket.repo.BasketRepo;
import com.basket.Basket.repo.CardRepo;
import com.basket.Basket.repo.ProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.Math.abs;

@Service
public class BasketService {

    private static final Logger log = LoggerFactory.getLogger(BasketService.class);


    private  final ProductRepo productRepo;
    private final BasketRepo basketRepo;
    private final CardRepo cardRepo;

    private static final int INITIAL_SUM = 0;

    @Autowired
    public BasketService(ProductRepo productRepo, BasketRepo basketRepo, CardRepo cardRepo) {
        this.productRepo = productRepo;
        this.basketRepo = basketRepo;
        this.cardRepo = cardRepo;
    }

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

    public void signing(Optional<User> currentUser) {
        User user = currentUser.get();
        String userName = user.getName();
        log.info("Пользователь " + userName + " найден в базе данных!");
        basketRepo.save(new Basket(user,new LinkedList<>()));
    }

    public ResponseEntity<String> adding(Long productId,Integer weight, Basket basket) {
        Product chosenProduct = checkWeight(productId,weight);
        Long userId = basket.getUser().getUserID();
        if(chosenProduct.getName().equals("Alcohol") && basket.getUser().getAge() <= 21){
            log.info("Товар не разрешен!");
            return new ResponseEntity<>("Товар не разрешен!", HttpStatus.BAD_GATEWAY);
        }
        if(chosenProduct.getAvailability()){
            HashMap<List<Long>, Integer> map = basketRepo.map;
            List<Long> key = Arrays.asList(userId,productId);
            if(map.containsKey(key)){
                map.put(key,map.get(key) + weight);
                return new ResponseEntity<>(chosenProduct.getName() + " успешно приумножен!",HttpStatus.OK);
            } else {
                map.put(new ArrayList<>(Arrays.asList(userId,productId)),weight);
                basket.getProductList().add(chosenProduct);
                basketRepo.save(basket);
                return new ResponseEntity<>(chosenProduct.getName() + " успешно добавлен в вашу корзину!",HttpStatus.OK);
            }
        } else {
            log.error("Товар закончился!");
            return new ResponseEntity<>("Товар закончился!",HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> creatingList(Basket basket) {
        if(!basket.getProductList().isEmpty()){
            Long userId = basket.getUser().getUserID();
            StringBuilder s = new StringBuilder();
            int sum = INITIAL_SUM;
            for (Product product: basket.getProductList()) {
                Integer weight = basketRepo.map.get(Arrays.asList(userId, product.getProductId()));
                Integer objSum = product.getPrice() * weight;
                s.append("\n").append(product.getName()).append(" ").append(objSum);
                sum += objSum;
            }
            s.append("\t").append(sum);
            String s1 = "Список покупок пользователя " + basket.getUser().getName() + ": " + s;
            return new ResponseEntity<>(s1,HttpStatus.OK);
        } else {
            log.error("Список покупок пользователя пуст! Сумма к оплате: " + INITIAL_SUM);
            return new ResponseEntity<>("Список покупок пуст! Сумма к оплате: " + INITIAL_SUM,HttpStatus.BAD_REQUEST);
        }
    }

    public void cleanBasket(Basket basket) {
        for (Product product: basket.getProductList()) {
            basketRepo.map.remove(Arrays.asList(basket.getUser().getUserID(), product.getProductId()));
        }
        basket.setProductList(new LinkedList<>());
        basketRepo.save(basket);
        log.info("Корзина очищена!");
    }

    public ResponseEntity<String> removing(Long productId, Basket basket) {
        Optional<Product> optionalProduct = productRepo.findById(productId);
        if(optionalProduct.isPresent()){
            Product chosenProduct = optionalProduct.get();
            basketRepo.map.remove(Arrays.asList(basket.getUser().getUserID(), productId));
            basket.getProductList().remove(chosenProduct);
            return new ResponseEntity<>("Продукт " + chosenProduct.getName() + " успешно удален из вашего списка покупок!",HttpStatus.OK);
        } else {
            log.error("Вы ввели неправильный productId " + productId);
            return new ResponseEntity<>("Вы ввели неправильный productId " + productId,HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> deleting(Basket basket) {
        Long userId = basket.getUser().getUserID();
        Optional<Card> optionalCard = cardRepo.findByUserId(userId);
        if(optionalCard.isPresent()){
            List<Product> productList = basket.getProductList();
            Card card = optionalCard.get();
            return checking(card,productList);
        } else {
            log.error("Карта пользователя не найдена!");
            return new ResponseEntity<>("Пожалуйста, создайте карту",HttpStatus.BAD_GATEWAY);
        }
    }

    private ResponseEntity<String> checking(Card card, List<Product> productList) {
        Long userId = card.getUser().getUserID();
        Optional<Basket> optionalBasket = basketRepo.findByUserId(userId);
        int sum = INITIAL_SUM;
        int remainder = card.getAmountOfMoney();
        for (Product product: productList) {
            Integer weight = basketRepo.map.get(Arrays.asList(userId, product.getProductId()));
            System.out.println(weight);
            int objSum = product.getPrice() * weight;
            sum += objSum;
            remainder -= sum;
            if(remainder > 0){
                changeProducts(product.getProductId(),weight);
                basketRepo.map.remove(Arrays.asList(userId, product.getProductId()));
                productRepo.save(product);
                card.setAmountOfMoney(remainder);
            } else {
                log.error("Недостаточно средств на карте!");
                return new ResponseEntity<>("Пожалуйста пополните карту! ",HttpStatus.BAD_REQUEST);
            }
        }
        cardRepo.save(card);
        log.info("Продукты изменены в базе!");
        basketRepo.delete(optionalBasket.get());
        log.info("Оплата прошла успешно");
        return new ResponseEntity<>("С вашей картой списано " + sum + ". Ваш остаток по счету равен :" + remainder +
                " Спасибо за покупку! Ждем вас снова!",HttpStatus.OK);
    }
}
