package com.Application;

import com.Application.dto.*;
import com.Application.enums.Categories;
import com.Application.replies.BuyListReply;
import com.Application.replies.CategoryReply;
import com.Application.replies.LogInReply;
import com.Application.repo.BasketRepo;
import com.Application.repo.CardRepo;
import com.Application.repo.ProductRepo;
import com.Application.repo.UserRepo;
import com.Application.settings.RestError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.Application.settings.Utils.*;


@RestController
public class BasketController {

    private static final Logger log = LoggerFactory.getLogger(BasketController.class);
    private static final Integer START_POINT_SUM = 1_000_000;
    private static final Long ALCOHOL_ITEM = 1013L;

    @Value("${images.folder}")
    private String IMAGES_FOLDER;

    @Value("${api.url.base}")
    private String API_URL;

    private final ProductRepo productRepo;
    private final BasketRepo basketRepo;
    private final UserRepo userRepo;
    private final CardRepo cardRepo;
    private final BasketService basketService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    @Autowired
    public BasketController(ProductRepo productRepo, BasketRepo basketRepo, UserRepo userRepo, CardRepo cardRepo, BasketService basketService) {
        this.productRepo = productRepo;
        this.basketRepo = basketRepo;
        this.userRepo = userRepo;
        this.cardRepo = cardRepo;
        this.basketService = basketService;
    }

    //Метод авторизации пользователя

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    private RestError logIn(
            @RequestBody String json
    ){
        log.info("> login");
        String logIn = null;
        String password = null;

        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            logIn = getSafeString(jo,"logIn",null);
            password = getSafeString(jo,"password",null);
        } catch (Exception e){
            log.info("Couldn't create a json");
        }
        User user = userRepo.findByLogin(logIn).orElse(null);
        boolean addInfo = false;
        if (user == null){
            log.info("Пользователь с таким логином " + logIn + " не найден");
            user = new User();
            user.setLogin(logIn);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            userRepo.save(user);
            basketService.login(user);
            addInfo = true;
        } else {
            boolean passwordOK = true;
            user.setPasswordCheck(user.getPasswordCheck() + 1);
            userRepo.save(user);
            if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
                passwordOK = false;
                log.error("Неверный пароль");
            }
            if(!passwordOK){
                if (user.getPasswordCheck() >= 3){
                    log.info("Аккаунт заблокирован");
                    user.setBlocked(true);
                    userRepo.save(user);
                    log.info("< login");
                    return new RestError(11, "Account is deleted",HttpStatus.BAD_REQUEST);
                }
                if (user.getPasswordCheck() == 2){
                    log.info("Последняя попытка");
                    log.info("< login");
                    return new RestError(12, "Last chance",HttpStatus.BAD_REQUEST);
                }
                return new RestError(10,"Password is incorrect",HttpStatus.BAD_REQUEST);
            } else {
                user.setPasswordCheck(0);
                userRepo.save(user);
            }
        }
        log.info("Пользователь " + user.getUserId() + " успешно вошел в приложение");
        log.info("< login");
        RestError re = new RestError();
        re.setData(new LogInReply(user.getUserId(),addInfo));
        return re;
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
            userId = getSafeLong(jo,"userId",null);
            productId = getSafeLong(jo,"productId",null);
            weight = getSafeInt(jo,"weight",null);
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
        if(Objects.equals(product.getProductId(), ALCOHOL_ITEM)){
            if(!basketService.checkAge(basket)){
                log.info("Товар не разрешен!");
                return new RestError(5,"Товар запрещен!", "Товар запрещен", HttpStatus.BAD_REQUEST);
            }
        }
        RestError re = basketService.adding(product,weight,basket);
        log.info("> addToBasket");
        return re;
    }

    //Метод вывода чека

    @RequestMapping(value = "/buyList",method = RequestMethod.GET)
    private RestError buyList(
            @RequestParam(name = "userId") Long userId
    ){
        log.info("> buyList");
        List <ProductItem> productList = basketRepo.getProductListById(userId);
        if (productList.isEmpty()){
            log.info("< buyList");
            return new RestError(13,"Shopping list is empty",HttpStatus.BAD_REQUEST);
        }
        Integer fullPrice = basketService.findFullPrice(productList);
        log.info("< buyList");
        RestError re = new RestError();
        re.setData(new BuyListReply(productList,fullPrice));
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
        List<Product> productList = productRepo.findAllAvailable();
        log.info("> allProducts");
        return new RestError(productList,HttpStatus.OK);
    }

    //Метод вывода категорий

    @RequestMapping(value = "/getCategories", method = RequestMethod.GET)
    private RestError getCategories(){
        log.info("> getCategories");
        List<Product> fruits = productRepo.findByCategory(Categories.FRUITS.getCategory());
        List<Product> vegetables = productRepo.findByCategory(Categories.VEGETABLES.getCategory());
        List<Product> dairies = productRepo.findByCategory(Categories.DAIRIES.getCategory());
        List<Product> drinks = productRepo.findByCategory(Categories.DRINKS.getCategory());
        List<Product> meats = productRepo.findByCategory(Categories.MEATS.getCategory());
        List<Product> sweets = productRepo.findByCategory(Categories.SWEETS.getCategory());
        List<Product> bakeries = productRepo.findByCategory(Categories.BAKERIES.getCategory());

        CategoryReply fruitReply = new CategoryReply(Categories.FRUITS.getName(), fruits);
        CategoryReply vegetableReply = new CategoryReply(Categories.VEGETABLES.getName(), vegetables);
        CategoryReply dairiesReply = new CategoryReply(Categories.DAIRIES.getName(), dairies);
        CategoryReply drinksReply = new CategoryReply(Categories.DRINKS.getName(), drinks);
        CategoryReply meatsReply = new CategoryReply(Categories.MEATS.getName(), meats);
        CategoryReply sweetsReply = new CategoryReply(Categories.SWEETS.getName(), sweets);
        CategoryReply bakeriesReply = new CategoryReply(Categories.BAKERIES.getName(), bakeries);
        log.info("< getCategories");
        Object[] data = new Object[]{fruitReply,vegetableReply,dairiesReply,drinksReply,meatsReply,sweetsReply,bakeriesReply};
        RestError re = new RestError();
        re.setData(data);
        return re;
    }



    @RequestMapping(value = "/getCategory", method = RequestMethod.GET)
    private RestError getCategory(
            @RequestParam Long categoryId
    ){
        log.info("> getCategory");
        List<Product> items = productRepo.findByCategory(categoryId);
        Categories category = Arrays.stream(Categories.values()).filter(categories -> categories.getCategory().equals(categoryId)).findFirst().orElse(null);
        log.info("< getCategory");
        RestError re = new RestError();
        re.setData(new CategoryReply(category.getName(), items));
        return re;
    }

    //Метод заполнения аккаунта

    @RequestMapping(value = "/addInfo", method = RequestMethod.POST)
    private RestError addInfo(
            @RequestBody String json
    ) {
        log.info("> addInfo");
        Long userId = null;
        String name = null;
        String lastName = null;
        Integer age = null;
        String userInfo = null;
        String sex = null;

        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            userId = getSafeLong(jo,"userId",null);
            name = getSafeString(jo,"name",null);
            lastName = getSafeString(jo,"lastName",null);
            age = getSafeInt(jo,"age",null);
            userInfo = getSafeString(jo,"userInfo",null);
            sex = getSafeString(jo,"sex",null);
        } catch (Exception e){
            log.error("Couldn't create json");
        }

        User user = userRepo.findById(userId).orElse(null);
        if (user == null){
            log.error("Пользователь с таким id " + userId + "не найден");
            log.info("< addInfo");
            return new RestError(1,"User not found in Base",HttpStatus.BAD_REQUEST);
        }
        user.setName(name);
        user.setLastName(lastName);
        user.setAge(age);
        user.setUserInfo(userInfo);
        user.setSex(sex);
        userRepo.save(user);
        return new RestError("OK",HttpStatus.OK);
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
            userId = getSafeLong(jo,"userId",null);
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
        cardRepo.save(new Card(START_POINT_SUM, userId));
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
            userId = getSafeLong(jo,"userId",null);
        } catch (Exception e){
            log.info("Couldn't create a json");
        }
        Basket basket = basketRepo.findByUserId(userId).orElse(null);
        Card card = cardRepo.findByUserId(userId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);
        if (user == null){
            log.error("Пользователь с таким id " + userId + "не найден");
            log.info("< payment");
            return new RestError(1,"User not found in Base",HttpStatus.BAD_REQUEST);
        }
        if (card == null){
            log.error("Пользователь с таким id" + userId + "не найден / card not found");
            log.info("< payment");
            return new RestError(8,"Card not found in Base / user not found",HttpStatus.BAD_REQUEST);
        }
        if(basket == null){
            log.error("Пользователь с таким id " + userId + "не найден / basket not found");
            return new RestError(2, "Basket not found in Base / user not found",HttpStatus.BAD_REQUEST);
        }

        return basketService.checking(card,basket);
    }

    // Метод вывода картинки продукта

    @RequestMapping(value = "/get-image/{imgFileName}", method = RequestMethod.GET)
    public RestError getImage(
            HttpServletResponse response,

            @PathVariable("imgFileName") String imgFileName
    ) {

        log.info("> get-image [ " + imgFileName +" ]");

        byte[] content = null;
        try {
            String path = IMAGES_FOLDER + imgFileName;
            log.info(path);
            File file = new File(path);
            content = IOUtils.toByteArray(new FileInputStream(file));
        } catch (Exception e) {
            log.info("Couldn't load img: " + e.getMessage());
        }
        log.info("bytes.size = " + content.length);
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            os.write(content);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("< get-image");
        return new RestError("OK", HttpStatus.OK);
    }
}

