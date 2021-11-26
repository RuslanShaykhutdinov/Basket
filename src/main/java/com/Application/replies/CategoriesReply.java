package com.Application.replies;

import com.Application.dto.Product;

import java.util.List;

public class CategoriesReply {
    private List<Product> fruits;
    private List<Product> vegetables;
    private List<Product> dairies;
    private List<Product> drinks;
    private List<Product> meats;
    private List<Product> sweets;
    private List<Product> bakeries;


    public CategoriesReply(List<Product> fruits, List<Product> vegetables, List<Product> dairies, List<Product> drinks, List<Product> meats, List<Product> sweets, List<Product> bakeries) {
        this.fruits = fruits;
        this.vegetables = vegetables;
        this.dairies = dairies;
        this.drinks = drinks;
        this.meats = meats;
        this.sweets = sweets;
        this.bakeries = bakeries;
    }

    public List<Product> getFruits() {
        return fruits;
    }

    public void setFruits(List<Product> fruits) {
        this.fruits = fruits;
    }

    public List<Product> getVegetables() {
        return vegetables;
    }

    public void setVegetables(List<Product> vegetables) {
        this.vegetables = vegetables;
    }

    public List<Product> getDairies() {
        return dairies;
    }

    public void setDairies(List<Product> dairies) {
        this.dairies = dairies;
    }

    public List<Product> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Product> drinks) {
        this.drinks = drinks;
    }

    public List<Product> getMeats() {
        return meats;
    }

    public void setMeats(List<Product> meats) {
        this.meats = meats;
    }

    public List<Product> getSweets() {
        return sweets;
    }

    public void setSweets(List<Product> sweets) {
        this.sweets = sweets;
    }

    public List<Product> getBakeries() {
        return bakeries;
    }

    public void setBakeries(List<Product> bakeries) {
        this.bakeries = bakeries;
    }

    @Override
    public String toString() {
        return "CategoriesReply{" +
                "fruits=" + fruits +
                ", vegetables=" + vegetables +
                ", dairies=" + dairies +
                ", drinks=" + drinks +
                ", meats=" + meats +
                ", sweets=" + sweets +
                ", bakeries=" + bakeries +
                '}';
    }
}
