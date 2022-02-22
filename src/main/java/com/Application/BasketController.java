package com.Application;

import com.Application.dto.*;
import com.Application.enums.Categories;
import com.Application.errors.Error;
import com.Application.errors.ErrorRepo;
import com.Application.replies.BuyListReply;
import com.Application.replies.CategoryReply;
import com.Application.replies.GetInfoReply;
import com.Application.replies.LogInReply;
import com.Application.repo.BasketRepo;
import com.Application.repo.CardRepo;
import com.Application.repo.ProductRepo;
import com.Application.repo.UserRepo;
import com.Application.settings.RestError;
import com.Application.settings.ThreadLanguage;
import com.Application.settings.Utils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import static com.Application.settings.Utils.*;


@RestController
public class BasketController {

    private static final Logger log = LoggerFactory.getLogger(BasketController.class);
    private static final Integer START_POINT_SUM = 1_000_000;
    private static final Long ALCOHOL_ITEM = 1013L;
    private static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

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
    private final ErrorRepo errorRepo;


    @Autowired
    public BasketController(ProductRepo productRepo, BasketRepo basketRepo, UserRepo userRepo, CardRepo cardRepo, BasketService basketService, ErrorRepo errorRepo) {
        this.productRepo = productRepo;
        this.basketRepo = basketRepo;
        this.userRepo = userRepo;
        this.cardRepo = cardRepo;
        this.basketService = basketService;
        this.errorRepo = errorRepo;
    }

    //Метод авторизации пользователя

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    private RestError logIn(
            HttpServletRequest request,
            @RequestBody String json
    ){
        log.info("> login");

        String lang = request.getHeader(HEADER_ACCEPT_LANGUAGE);

        if (lang != null) {
            ThreadLanguage.setLang(lang);
        } else {
            lang = "en";
        }
        Error error = null;
        String logIn = null;
        String password = null;

        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            logIn = getSafeString(jo,"logIn",null);
            password = getSafeString(jo,"password",null);
        } catch (Exception e){
            log.error("Couldn't create a json");
        }

        User user = userRepo.findByLogin(logIn).orElse(null);
        // Флаг, что пользователю надо показать заполнение инфо
        boolean addInfo = false;

        if (user == null){
            log.warn("Пользователь с таким логином {} не найден!", logIn);
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
                log.warn("Неверный пароль");
            }

            if(!passwordOK){
                if (user.getPasswordCheck() >= 3){
                    log.error("Аккаунт заблокирован");
                    user.setBlocked(true);
                    userRepo.save(user);
                    log.info("< login");
                    error = errorRepo.getByErrorNumAndLanguage(11, lang);
                    return new RestError(11, error.getMessage(), HttpStatus.BAD_REQUEST);
                }
                if (user.getPasswordCheck() == 2){
                    log.warn("Последняя попытка");
                    log.info("< login");
                    error = errorRepo.getByErrorNumAndLanguage(12, lang);
                    return new RestError(12, error.getMessage(),HttpStatus.BAD_REQUEST);
                }
                error = errorRepo.getByErrorNumAndLanguage(10, lang);
                return new RestError(10,error.getMessage(),HttpStatus.BAD_REQUEST);
            } else {
                user.setPasswordCheck(0);
                userRepo.save(user);
            }
        }

        GetInfoReply getInfoReply = new GetInfoReply();
        if (user.getName() != null  && user.getLastName() != null && user.getBirthday() != null){
            getInfoReply.setName(user.getName());
            getInfoReply.setLastName(user.getLastName());
            getInfoReply.setBirthDay(user.getBirthday());
            getInfoReply.setSmileImage(true);
        }

        log.info("< login");
        return new RestError(new LogInReply(getInfoReply,addInfo), HttpStatus.OK);
    }

    //Метод добавление товара пользователя по ID

    @RequestMapping(value = "/addToBasket", method = RequestMethod.POST)
    private RestError addItemToBasket(
            HttpServletRequest request,
            @RequestBody String json
    ){
        log.info("> addToBasket");

        String lang = request.getHeader(HEADER_ACCEPT_LANGUAGE);

        if (lang != null) {
            ThreadLanguage.setLang(lang);
        } else {
            lang = "en";
        }

        Long userId = null;
        Long productId = null;
        Integer weight = null;
        Error error = null;

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
            log.error("Корзина с таким userId {} не найдена!", userId);
            log.info("< addToBasket");
            error = errorRepo.getByErrorNumAndLanguage(2, lang);
            return new RestError(2, error.getMessage(),HttpStatus.BAD_REQUEST);
        }

        if (product == null){
            log.error("Продукт с таким productId {} не найден! ", productId);
            log.info("< addToBasket");
            error = errorRepo.getByErrorNumAndLanguage(3, lang);
            return  new RestError(3,error.getMessage(),HttpStatus.BAD_REQUEST);
        }

        if (weight > product.getWeight()){
            log.error("Недостаточно товара на складе! productId {}", productId);
            log.info("< addToBasket");
            error = errorRepo.getByErrorNumAndLanguage(14, lang);
            return  new RestError(14,error.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if(Objects.equals(product.getProductId(), ALCOHOL_ITEM)){
            if(!basketService.checkAge(basket)){
                log.error("Товар не разрешен! userId {}", userId);
                log.info("< addToBasket");
                error = errorRepo.getByErrorNumAndLanguage(5, lang);
                return new RestError(5,error.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        RestError serviceRe = basketService.adding(product,weight,basket);
        RestError re = null;
        if (serviceRe.getError() != 0){
            error = errorRepo.getByErrorNumAndLanguage(serviceRe.getError(), lang);
            String message = error.getMessage();
            if (serviceRe.getData() != null)
                message += serviceRe.getData();
            re = new RestError(error.getErrorNum(), message, serviceRe.getStatus());
        } else {
            re = new RestError(serviceRe.getData(), serviceRe.getStatus());
        }

        log.info("< addToBasket");
        return re;
    }

    //Метод вывода чека

    @RequestMapping(value = "/buyList",method = RequestMethod.GET)
    private RestError buyList(
            HttpServletRequest request,
            @RequestParam(name = "userId") Long userId
    ){
        log.info("> buyList userId {}", userId);

        String lang = request.getHeader(HEADER_ACCEPT_LANGUAGE);

        if (lang != null) {
            ThreadLanguage.setLang(lang);
        } else {
            lang = "en";
        }

        List <ProductItem> productList = basketRepo.getProductListById(userId);

        if (productList.isEmpty()){
            log.error("Список продуктов пуст!");
            log.info("< buyList");
            Error error = errorRepo.getByErrorNumAndLanguage(13, lang);
            return new RestError(13,error.getMessage(),HttpStatus.BAD_REQUEST);
        }

        Integer fullPrice = basketService.findFullPrice(productList);
        log.info("< buyList");
        return new RestError(new BuyListReply(productList,fullPrice), HttpStatus.OK);
    }

    //Метод удаления списка покупок

    @RequestMapping(value = "/deleteList",method = RequestMethod.DELETE)
    private RestError deleteList(
            HttpServletRequest request,
            @RequestParam(name = "userId") Long userId
    ){
        log.info("> deleteList userId {}", userId );

        String lang = request.getHeader(HEADER_ACCEPT_LANGUAGE);

        if (lang != null) {
            ThreadLanguage.setLang(lang);
        } else {
            lang = "en";
        }

        Basket basket = basketRepo.findByUserId(userId).orElse(null);

        if(basket == null){
            log.error("Корзина с таким userId {} не найдена!", userId);
            log.info("< deleteList");
            Error error = errorRepo.getByErrorNumAndLanguage(2, lang);
            return new RestError(2, error.getMessage(), HttpStatus.BAD_REQUEST);
        }

        basketService.cleanBasket(basket);
        log.info("< deleteList");
        return new RestError("Корзина очищена!", HttpStatus.OK); //TODO уточнить У Нурлана почему такой ответ
    }

    //Метод удаления товара пользователя по ID

//    @RequestMapping(value = "/removeFromBasket",method = RequestMethod.DELETE)
//    private RestError removeFromBasket(
//            @RequestParam(name = "productId") Long productId,
//            @RequestParam(name = "userId") Long userId
//    ){
//        log.info("> removeFromBasket");
//        Basket basket = basketRepo.findByUserId(userId).orElse(null);
//        Product product = productRepo.findById(productId).orElse(null);
//        if(basket == null){
//            log.error("Пользователь с таким id " + userId + "не найден / basket not found");
//            log.info("< removeFromBasket");
//            return new RestError(2, "Basket not found in Base / user not found",HttpStatus.BAD_REQUEST);
//        }
//        if (product == null){
//            log.info("Продукт не найден в базе! Введенный id " + productId);
//            log.info("< removeFromBasket");
//            return  new RestError(3,"Product not found / wrong id",HttpStatus.BAD_REQUEST);
//        }
//        RestError re = basketService.removing(product,basket);
//        log.info("< removeFromBasket");
//        return re;
//    }

    @RequestMapping(value = "/removeFromBasketByNumber", method = RequestMethod.DELETE)
    private RestError removeFromBasketByNumber(
            HttpServletRequest request,
            @RequestParam(name = "productId") Long productId,
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "weight") Integer weight
    ){
        log.info("> removeFromBasketByNumber userId {}, productId {}, weight {}", userId, productId, weight);

        String lang = request.getHeader(HEADER_ACCEPT_LANGUAGE);

        if (lang != null) {
            ThreadLanguage.setLang(lang);
        } else {
            lang = "en";
        }

        Product product = productRepo.findById(productId).orElse(null);
        Basket basket = basketRepo.findByUserId(userId).orElse(null);
        Error error = null;

        if (product == null){
            log.error("Продукт с таким productId {} не найден! ", productId);
            log.info("< removeFromBasketByNumber");
            error = errorRepo.getByErrorNumAndLanguage(3, lang);
            return  new RestError(3,error.getMessage(),HttpStatus.BAD_REQUEST);
        }

        if(basket == null){
            log.error("Корзина с таким userId {} не найдена!", userId);
            log.info("< removeFromBasketByNumber");
            error = errorRepo.getByErrorNumAndLanguage(2, lang);
            return new RestError(2, error.getMessage(),HttpStatus.BAD_REQUEST);
        }

        RestError re = basketService.removingByNum(product,basket,weight);
        re.setStatus(HttpStatus.OK);
        log.info("< removeFromBasketByNumber");
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
        Object[] data = new Object[]{fruitReply,vegetableReply,dairiesReply,drinksReply,meatsReply,sweetsReply,bakeriesReply};
        log.info("< getCategories");
        return new RestError(data, HttpStatus.OK);
    }


    //Метод заполнения аккаунта

    @RequestMapping(value = "/addInfo", method = RequestMethod.POST)
    private RestError addInfo(
            HttpServletRequest request,
            @RequestBody String json
    ) {
        log.info("> addInfo json{}", json);

        String lang = request.getHeader(HEADER_ACCEPT_LANGUAGE);

        if (lang != null) {
            ThreadLanguage.setLang(lang);
        } else {
            lang = "en";
        }

        Long userId = null;
        String name = null;
        String lastName = null;
        String date = null;
        String userInfo = null;
        String sex = null;

        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            userId = getSafeLong(jo,"userId",null);
            name = getSafeString(jo,"name",null);
            lastName = getSafeString(jo,"lastName",null);
            date = getSafeString(jo,"date",null);
            userInfo = getSafeString(jo,"userInfo",null);
            sex = getSafeString(jo,"sex",null);
        } catch (Exception e){
            log.error("Couldn't create json");
        }

        User user = userRepo.findById(userId).orElse(null);

        if (user == null){
            log.error("Пользователь с таким userId {} не найден! ", userId);
            log.info("< addInfo");
            Error error = errorRepo.getByErrorNumAndLanguage(1, lang);
            return new RestError(1,error.getMessage(),HttpStatus.BAD_REQUEST);
        }

        user.setName(name);
        user.setLastName(lastName);
        try {
            user.setBirthday(Utils.format.parse(date));
        } catch (ParseException e) {
            log.error("error {}", e.getMessage());
            e.printStackTrace();
        }
        user.setUserInfo(userInfo);
        user.setSex(sex);
        userRepo.save(user);

        GetInfoReply gir = new GetInfoReply(name,lastName);
        try {
            gir.setBirthDay(Utils.format.parse(date));
        } catch (ParseException e) {
            log.error("error {}", e.getMessage());
            e.printStackTrace();
        }
        gir.setSmileImage(true);

        log.info("< addInfo");
        return new RestError(gir,HttpStatus.OK);
    }

    //Метод создания карты пользователю

    @RequestMapping(value = "/createCard",method = RequestMethod.POST)
    private RestError createCard(
            HttpServletRequest request,
            @RequestBody String json
    ){
        log.info("> createCard");

        String lang = request.getHeader(HEADER_ACCEPT_LANGUAGE);

        if (lang != null) {
            ThreadLanguage.setLang(lang);
        } else {
            lang = "en";
        }

        Long userId = null;
        Error error = null;

        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            userId = getSafeLong(jo,"userId",null);
        } catch (Exception e){
            log.info("Couldn't create a json");
        }

        Card card = cardRepo.findByUserId(userId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);

        if (user == null){
            log.error("Пользователь с таким userId {} не найден! ", userId);
            log.info("< createCard");
            error = errorRepo.getByErrorNumAndLanguage(1, lang);
            return new RestError(1,error.getMessage(),HttpStatus.BAD_REQUEST);
        }

        if (card != null){
            log.error("У пользователя c userId {} уже есть карта!", userId);
            log.info("< createCard");
            error = errorRepo.getByErrorNumAndLanguage(7, lang);
            return new RestError(7,error.getMessage(), HttpStatus.BAD_REQUEST );
        }

        cardRepo.save(new Card(START_POINT_SUM, userId));
        log.info("< createCard");
        return new RestError("OK",HttpStatus.OK);
    }

    //Метод списания с карты

    @RequestMapping(value = "/payment",method = RequestMethod.PUT)
    private RestError payment(
            HttpServletRequest request,
            @RequestBody String json
    ){
        log.info("> payment");

        String lang = request.getHeader(HEADER_ACCEPT_LANGUAGE);

        if (lang != null) {
            ThreadLanguage.setLang(lang);
        } else {
            lang = "en";
        }

        Error error = null;
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
            log.error("Пользователь с таким userId {} не найден!", userId);
            log.info("< payment");
            error = errorRepo.getByErrorNumAndLanguage(1, lang);
            return new RestError(1,error.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if (card == null){
            log.error("У пользователя с таким userId {} карта не найдена!" , userId );
            log.info("< payment");
            error = errorRepo.getByErrorNumAndLanguage(8, lang);
            return new RestError(8,error.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if(basket == null){
            log.error("Корзина с таким userId {} не найдена!", userId);
            log.info("< payment");
            error = errorRepo.getByErrorNumAndLanguage(2, lang);
            return new RestError(2, error.getMessage(), HttpStatus.BAD_REQUEST);
        }

        log.info("< payment");
        RestError serverRe = basketService.checking(card,basket);
        RestError re = null;
        if (serverRe.getError() != 0){
            error = errorRepo.getByErrorNumAndLanguage(serverRe.getError(), lang);
            re = new RestError(error.getErrorNum(), error.getMessage(), HttpStatus.BAD_REQUEST);
        } else {
            re = new RestError(serverRe.getData(), HttpStatus.OK);
        }
        return re;
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

