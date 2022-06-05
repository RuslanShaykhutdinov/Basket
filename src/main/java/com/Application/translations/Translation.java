package com.Application.translations;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Service
public class Translation {

    public static final Map<String, String> productNames = new HashMap<>();

    @PostConstruct
    public void start() {


        productNames.put(1000 + "en","Tomatoes");
        productNames.put(1000+ "uz", "Pomidorlar");
        productNames.put(1000 + "ru", "Помидоры");
        productNames.put(1001 + "en","Cucunbers");
        productNames.put(1001 + "uz", "Bodringlar");
        productNames.put(1001 + "ru", "Огурцы");
        productNames.put(1002 + "en","Milk");
        productNames.put(1002 + "uz", "Sut");
        productNames.put(1002 + "ru", "Молоко");
        productNames.put(1003 + "en","Meat");
        productNames.put(1003 + "uz", "Go'sht");
        productNames.put(1003 + "ru", "Мясо");
        productNames.put(1004 + "en","Bread");
        productNames.put(1004 + "uz", "Non");
        productNames.put(1004 + "ru", "Хлеб");
        productNames.put(1005 + "en","Butter");
        productNames.put(1005+ "uz", "Yog'i");
        productNames.put(1005 + "ru", "Масло");
        productNames.put(1006 + "en","Cheese");
        productNames.put(1006 + "uz", "Pishloq");
        productNames.put(1006 + "ru", "Сыр");
        productNames.put(1007 + "en","Orange Juice");
        productNames.put(1007 + "uz", "Apelsin sharbati");
        productNames.put(1007 + "ru", "Апельсиновый сок");
        productNames.put(1008 + "en","Water");
        productNames.put(1008 + "uz", "Suv");
        productNames.put(1008 + "ru", "Вода");
        productNames.put(1009 + "en","Fish");
        productNames.put(1009 + "uz", "Baliq'");
        productNames.put(1009 + "ru", "Рыба");
        productNames.put(1010 + "en","Watermelon");
        productNames.put(1010 + "uz", "Tarvuz");
        productNames.put(1010 + "ru", "Арбуз");
        productNames.put(1011 + "en","Garlic");
        productNames.put(1011 + "uz", "Sarimsoq");
        productNames.put(1011 + "ru", "Чеснок");
        productNames.put(1012 + "en","Chocolate");
        productNames.put(1012 + "uz", "Shokolad");
        productNames.put(1012 + "ru", "Шоколад");
        productNames.put(1013 + "en","Alcohol");
        productNames.put(1013 + "uz", "Spirtli ichimliklar'");
        productNames.put(1013 + "ru", "Алкоголь");

    }

}
