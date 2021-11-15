package com.basket.Basket;

import com.basket.Basket.object.Basket;
import com.basket.Basket.object.Card;
import com.basket.Basket.object.Product;
import com.basket.Basket.object.User;
import com.basket.Basket.repo.BasketRepo;
import com.basket.Basket.repo.CardRepo;
import com.basket.Basket.repo.ProductRepo;
import com.basket.Basket.repo.UserRepo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;




@RestController
public class BasketController {

    private static final Logger log = LoggerFactory.getLogger(BasketController.class);
    public static final Integer START_POINT_SUM = 1_000_000;

    private final ProductRepo productRepo;
    private final BasketRepo basketRepo;
    private final UserRepo userRepo;
    private final CardRepo cardRepo;
    private final BasketService basketService;


    @Autowired
    public BasketController(ProductRepo productRepo, BasketRepo basketRepo, UserRepo userRepo, CardRepo cardRepo, BasketService basketService) {
        this.productRepo = productRepo;
        this.basketRepo = basketRepo;
        this.userRepo = userRepo;
        this.cardRepo = cardRepo;
        this.basketService = basketService;
    }

    //Метод авторизации пользователя

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    private RestError logIn(
            @RequestParam(name = "userId") Long userId
    ){
        log.info("> login");
        User user = userRepo.findById(userId).orElse(null);
        if (user == null){
            log.error("Пользователь с таким id " + userId + "не найден");
            log.info("< login");
            return new RestError(1,"User not found in Base",HttpStatus.BAD_REQUEST);
        }
        basketService.login(user);
        log.info("Пользователь " + user.getName() + " успешно вошел в приложение");
        log.info("< login");
        return new RestError("OK",HttpStatus.OK);
    }

    //Метод добавление товара пользователя по ID

    @RequestMapping(value = "/addToBasket", method = RequestMethod.POST)
    private RestError addItemToBasket(
            @RequestBody String json
    ){
        log.info("> addToBasket");

        Long userId = null;
        Long productId = null;
        Integer weight = null;

        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            userId = jo.get("userId").getAsLong();
            productId = jo.get("productId").getAsLong();
            weight = jo.get("weight").getAsInt();
        } catch (Exception e){
            log.info("Couldn't create a json");
        }

        Basket basket = basketRepo.findByUserId(userId).orElse(null);
        Product product = productRepo.findProduct(productId).orElse(null);
        if (basket == null){
            log.error("Пользователь с таким id " + userId + "не найден");
            log.info("< addToBasket");
            return new RestError(2, "Basket not found in Base / user not found",HttpStatus.BAD_REQUEST);
        }
        if (product == null){
            log.info("Продукт не найден в базе! Введенный id " + productId);
            log.info("< addToBasket");
            return  new RestError(3,"Product not found / wrong id",HttpStatus.BAD_REQUEST);
        }
        RestError re = basketService.adding(productId,weight,basket);
        log.info("> addToBasket");
        return re;
    }

    //Метод вывода чека
    @RequestMapping(value = "/buyList",method = RequestMethod.GET)
    private RestError buyList(
            @RequestParam(name = "userId") Long userId
    ){
        log.info("> buyList");
        Basket basket = basketRepo.findByUserId(userId).orElse(null);
        if(basket == null){
            log.error("Пользователь с таким id " + userId + "не найден / basket not found");
            log.info("< buyList");
            return new RestError(2, "Basket not found in Base / user not found",HttpStatus.BAD_REQUEST);
        }
        RestError re = basketService.creatingList(basket);
        log.info("< buyList");
        return re;
    }

    //Метод удаления списка покупок

    @RequestMapping(value = "/deleteList",method = RequestMethod.DELETE)
    private RestError deleteList(
            @RequestParam(name = "userId") Long userId
    ){
        log.info("> deleteList");
        Basket basket = basketRepo.findByUserId(userId).orElse(null);
        if(basket == null){
            log.error("Пользователь с таким id " + userId + "не найден / basket not found");
            log.info("< deleteList");
            return new RestError(2, "Basket not found in Base / user not found",HttpStatus.BAD_REQUEST);
        }
        basketService.cleanBasket(basket);
        log.info("< deleteList");
        return new RestError("Корзина очищена!",HttpStatus.OK);
    }

    //Метод удаления товара пользователя по ID

    @RequestMapping(value = "/removeFromBasket",method = RequestMethod.DELETE)
    private RestError removeFromBasket(
            @RequestParam(name = "productId") Long productId,
            @RequestParam(name = "userId") Long userId
    ){
        log.info("> removeFromBasket");
        Basket basket = basketRepo.findByUserId(userId).orElse(null);
        Product product = productRepo.findById(productId).orElse(null);
        if(basket == null){
            log.error("Пользователь с таким id " + userId + "не найден / basket not found");
            log.info("< removeFromBasket");
            return new RestError(2, "Basket not found in Base / user not found",HttpStatus.BAD_REQUEST);
        }
        if (product == null){
            log.info("Продукт не найден в базе! Введенный id " + productId);
            log.info("< removeFromBasket");
            return  new RestError(3,"Product not found / wrong id",HttpStatus.BAD_REQUEST);
        }
        RestError re = basketService.removing(product,basket);
        log.info("< removeFromBasket");
        return re;
    }

    //Метод вывода всего доступного товара

    @RequestMapping(value = "/allProducts",method = RequestMethod.GET)
    private RestError allProducts(){
        log.info(" < allProducts");
        List<Product> productList = productRepo.findAll();
        StringBuilder stringBuilder = new StringBuilder();
        for (Product product : productList) {
            if(product.getWeight() <= 0){
                continue; //не показывает если закончилось
            }
            stringBuilder.append(product.getName()).append(" ").append(product.getPrice())
                    .append(" ").append(product.getWeight()).append("\n");
        }
        log.info("> allProducts");
        return new RestError(stringBuilder,HttpStatus.OK);
    }

    //Метод создания карты пользователю

    @RequestMapping(value = "/createCard",method = RequestMethod.POST)
    private RestError createCard(
            @RequestBody String json
    ){
        log.info("> createCard");
        Long userId = null;

        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            userId = jo.get("userId").getAsLong();
        } catch (Exception e){
            log.info("Couldn't create a json");
        }
        Card card = cardRepo.findByUserId(userId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);
        if (user == null){
            log.error("Пользователь с таким id " + userId + "не найден");
            log.info("< createCard");
            return new RestError(1,"User not found in Base",HttpStatus.BAD_REQUEST);
        }
        if (card != null){
            log.info("У пользователя c id " + userId + " уже есть карта");
            log.info("< createCard");
            return new RestError(7,null, "У пользователя уже есть карта!", HttpStatus.BAD_REQUEST );
        }
        cardRepo.save(new Card(START_POINT_SUM, user));
        log.info("< createCard");
        return new RestError("OK",HttpStatus.OK);
    }

    //Метод списания с карты

    @RequestMapping(value = "/payment",method = RequestMethod.PUT)
    private RestError payment(
            @RequestBody String json
    ){
        log.info("> payment");
        Long userId = null;

        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            userId = jo.get("userId").getAsLong();
        } catch (Exception e){
            log.info("Couldn't create a json");
        }
        Basket basket = basketRepo.findByUserId(userId).orElse(null);
        Card card = cardRepo.findByUserId(userId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);
        if (user == null){
            log.error("Пользователь с таким id " + userId + "не найден");
            log.info("< createCard");
            return new RestError(1,"User not found in Base",HttpStatus.BAD_REQUEST);
        }
        if (card == null){
            log.error("Пользователь с таким id" + userId + "не найден / card not found");
            log.info("< login");
            return new RestError(8,"Card not found in Base / user not found",HttpStatus.BAD_REQUEST);
        }
        if(basket == null){
            log.error("Пользователь с таким id " + userId + "не найден / basket not found");
            return new RestError(2, "Basket not found in Base / user not found",HttpStatus.BAD_REQUEST);
        }
        return basketService.checking(userId,card,basket);
    }
}

