package com.basket.Basket;

import com.basket.Basket.object.Basket;
import com.basket.Basket.object.Card;
import com.basket.Basket.object.Product;
import com.basket.Basket.object.User;
import com.basket.Basket.repo.BasketRepo;
import com.basket.Basket.repo.CardRepo;
import com.basket.Basket.repo.ProductRepo;
import com.basket.Basket.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

@RestController
public class BasketController {

    private static final Logger log = LoggerFactory.getLogger(BasketController.class);

    @Autowired
    ProductRepo productRepo;

    @Autowired
    BasketRepo basketRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    CardRepo cardRepo;

    @Autowired
    BasketService basketService;


    //Метод авторизации пользователя

    @RequestMapping(value = "/signing/{id}", method = RequestMethod.GET)
    private ResponseEntity<String> signIn(
            @PathVariable(name = "id") Long userId
    ){
        Optional<User> currentUser = userRepo.findById(userId);
        if(currentUser.isPresent()){
            basketService.signing(currentUser);
            return new ResponseEntity<>("Пользователь " + currentUser.get().getName() + " успешно вошел в приложение", HttpStatus.OK);
        } else {
            log.error("Пользователь с таким " + userId + "не найден");
            return new ResponseEntity<>("Не удалось войти в аккаунт",HttpStatus.BAD_REQUEST);
        }
    }

    //Метод добавление товара пользователя по ID

    @RequestMapping(value = "/{user_id}/addToBasket/{id}/{weight}", method = RequestMethod.POST)
    private ResponseEntity<String> addItemToBasket(
            @PathVariable(name = "id") Long productId ,
            @PathVariable(name = "user_id") Long userId,
            @PathVariable Integer weight
    ){
        Optional<Basket> optionalBasket = basketRepo.findByUserId(userId);
        if(optionalBasket.isPresent()){
            Basket basket = optionalBasket.get();
            return basketService.adding(productId,weight,basket);
        } else {
            log.error("Вы ввели неправильный user_id " + userId);
            return new ResponseEntity<>("Вы ввели неправильный user_id " + userId,HttpStatus.BAD_REQUEST);
        }
    }

    //Метод вывода чека
    @RequestMapping(value = "/{user_id}/buyList",method = RequestMethod.GET)
    private ResponseEntity<String> buyList(
            @PathVariable(name = "user_id") Long userId
    ){
        Optional<Basket> optionalBasket = basketRepo.findByUserId(userId);
        if(optionalBasket.isPresent()){
            Basket basket = optionalBasket.get();
            return basketService.creatingList(basket);
        } else {
            log.error("Неверный id " + userId);
            return new ResponseEntity<>("Вы ввели неправильный id " + userId,HttpStatus.BAD_REQUEST);
        }
    }

    //Метод удаления списка покупок

    @RequestMapping(value = "/{user_id}/deleteList",method = RequestMethod.DELETE)
    private ResponseEntity<String> deleteList(
            @PathVariable(name = "user_id") Long userId
    ){
        Optional<Basket> optionalBasket = basketRepo.findByUserId(userId);
        if(optionalBasket.isPresent()){
            Basket basket = optionalBasket.get();
            basketService.cleanBasket(basket);
            return new ResponseEntity<>("Корзина очищена!",HttpStatus.OK);
        }else {
            log.error("Неверный id " + userId);
            return new ResponseEntity<>("Вы ввели неправильный id " + userId,HttpStatus.BAD_REQUEST);
        }

    }

    //Метод удаления товара пользователя по ID

    @RequestMapping(value = "/{user_id}/removeFromBasket/{id}",method = RequestMethod.DELETE)
    private ResponseEntity<String> removeFromBasket(
            @PathVariable(name = "id") Long productId,
            @PathVariable(name = "user_id") Long userId
    ){
        Optional<Basket> optionalBasket = basketRepo.findByUserId(userId);
        if(optionalBasket.isPresent()){
            Basket basket = optionalBasket.get();
            return basketService.removing(productId,basket);
        } else {
            log.error("Вы ввели неправильный user_id " + userId);
            return new ResponseEntity<>("Вы ввели неправильный user_id " + userId,HttpStatus.BAD_REQUEST);
        }
    }

    //Метод вывода всего доступного товара

    @RequestMapping(value = "/availableProducts",method = RequestMethod.GET)
    private ResponseEntity<StringBuilder> allProducts(){
        List<Product> product = productRepo.findAll();
        StringBuilder stringBuilder = new StringBuilder();
        for (Product value : product) {
            if(value.getWeight() < 0){
                continue;
            }
            stringBuilder.append(value.getName()).append(" ").append(value.getPrice())
                    .append(" ").append(value.getWeight()).append("\n");
        }
        log.info("Вывод списка всех доступных товаров!");
        return new ResponseEntity<>(stringBuilder,HttpStatus.OK);
    }

    //Метод создания карты пользователю

    @RequestMapping(value = "/{user_id}/card",method = RequestMethod.POST)
    private ResponseEntity<String> createCard(
            @PathVariable(name = "user_id") Long userId
    ){
        Optional<Card> card = cardRepo.findByUserId(userId);
        Optional<User> optionalUser = userRepo.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(!card.isPresent()){
                cardRepo.save(new Card(1_000_000, user));
                log.info("Карта пользователя создана!");
                return new ResponseEntity<>("Карта пользователя создана",HttpStatus.OK);
            } else {
                log.info("У пользователя " + user.getName() + " уже есть карта");
                return new ResponseEntity<>("У вас уже есть карта!",HttpStatus.BAD_GATEWAY);
            }
        } else {
            log.error("Вы ввели неправильный user_id " + userId);
            return new ResponseEntity<>("Вы ввели неправильный user_id " + userId,HttpStatus.BAD_REQUEST);
        }
    }

    //Метод списания с карты

    @RequestMapping(value = "/{user_id}/payment",method = RequestMethod.PUT)
    private ResponseEntity<String> payment(
            @PathVariable(name = "user_id") Long userId
    ){
        Optional<User> user = userRepo.findById(userId);
        Optional<Basket> optionalBasket = basketRepo.findByUserId(userId);
        if(user.isPresent()){
            if(optionalBasket.isPresent()){
                Basket basket = optionalBasket.get();
                return basketService.deleting(basket);
            } else {
                log.info("Корзина пользователя " + userId + "пуста!");
                return  new ResponseEntity<>("Ваша корзина пуста!",HttpStatus.BAD_REQUEST);
                }
        } else {
            log.info("Пользователь с таким id " + userId + "не найден!");
            return  new ResponseEntity<>("Вы ввели неправильный id" + userId,HttpStatus.BAD_REQUEST);
        }
    }
}

