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
            User user = currentUser.get();
            String userName = user.getName();
            log.info("Пользователь " + userName + " найден в базе данных!");
            basketRepo.save(new Basket(user,new LinkedList<>()));
            return new ResponseEntity<>("Пользователь " + userName + " успешно вошел в приложение", HttpStatus.OK);
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
            @PathVariable Integer weight){

        Product chosenProduct = basketService.checkWeight(productId,weight);
        Optional<Basket> optionalBasket = basketRepo.findByUserId(userId);
        if(optionalBasket.isPresent()){
            Basket basket = optionalBasket.get();
            if(chosenProduct.getName().equals("Alcohol") && basket.getUser().getAge() <= 21){
                log.info("Товар не разрешен!");
                return new ResponseEntity<>("Товар не разрешен!",HttpStatus.BAD_GATEWAY);
            }
            if(chosenProduct.getAvailability()){
                HashMap<List<Long>, Integer> map = basketRepo.map;
                List<Long> key = Arrays.asList(userId,productId);
                if(map.containsKey(key)){
                    map.put(key,map.get(key) + weight);
                    log.info(map.entrySet().toString());
                    return new ResponseEntity<>(chosenProduct.getName() + " успешно приумножен!",HttpStatus.OK);
                } else {
                    map.put(new ArrayList<>(Arrays.asList(userId,productId)),weight);
                    basket.getProductList().add(chosenProduct);
                    basketRepo.save(basket);
                    log.info(map.entrySet().toString());
                    return new ResponseEntity<>(chosenProduct.getName() + " успешно добавлен в вашу корзину!",HttpStatus.OK);
                }
            } else {
                log.error("Товар закончился!");
                return new ResponseEntity<>("Товар закончился!",HttpStatus.BAD_REQUEST);
            }
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
            if(!basket.getProductList().isEmpty()){
                StringBuilder s = new StringBuilder();
                int sum = 0;
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
                log.error("Список покупок пользователя пуст! Сумма к оплате: 0");
                return new ResponseEntity<>("Список покупок пуст! Сумма к оплате: 0" ,HttpStatus.BAD_REQUEST);
            }
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
            for (Product product: basket.getProductList()) {
                basketRepo.map.remove(Arrays.asList(userId, product.getProductId()));
            }
            basket.setProductList(new LinkedList<>());
            basketRepo.save(basket);
            log.info("Корзина очищена!");
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
        Optional<Product> optionalProduct = productRepo.findById(productId);
        Optional<Basket> optionalBasket = basketRepo.findByUserId(userId);
        if(optionalBasket.isPresent()){
            Basket basket = optionalBasket.get();
            if(optionalProduct.isPresent()){
                Product chosenProduct = optionalProduct.get();
                basketRepo.map.remove(Arrays.asList(userId, chosenProduct.getProductId()));
                basket.getProductList().remove(chosenProduct);
                return new ResponseEntity<>("Продукт " + chosenProduct.getName() + " успешно удален из вашего списка покупок!",HttpStatus.OK);
            } else {
                log.error("Вы ввели неправильный user_id " + userId);
                return new ResponseEntity<>("Вы ввели неправильный productId " + userId,HttpStatus.BAD_REQUEST);
            }
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
        Optional<Card> optionalCard = cardRepo.findByUserId(userId);
        if(user.isPresent()){
            if(optionalBasket.isPresent()){
                Basket basket = optionalBasket.get();
                if(optionalCard.isPresent()){
                    List<Product> productList = basket.getProductList();
                    Card card = optionalCard.get();
                    int sum = 0;
                    int remainder = card.getAmountOfMoney();
                    for (Product product: productList) {
                        Integer weight = basketRepo.map.get(Arrays.asList(userId, product.getProductId()));
                        System.out.println(weight);
                        int objSum = product.getPrice() * weight;
                        sum += objSum;
                        remainder -= sum;
                        if(remainder > 0){
                            basketService.changeProducts(product.getProductId(),weight);
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
                    basketRepo.delete(basket);
                    log.info("Оплата прошла успешно");
                    return new ResponseEntity<>("С вашей картой списано " + sum + ". Ваш остаток по счету равен :" + remainder +
                                " Спасибо за покупку! Ждем вас снова!",HttpStatus.OK);
                    } else {
                    log.error("Карта пользователя не найдена!");
                    return new ResponseEntity<>("Пожалуйста, создайте карту",HttpStatus.BAD_GATEWAY);
                }
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

