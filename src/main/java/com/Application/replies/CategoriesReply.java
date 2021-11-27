package com.Application.replies;

public class CategoriesReply {
    private CategoryReply fruits;
    private CategoryReply vegetables;
    private CategoryReply dairies;
    private CategoryReply drinks;
    private CategoryReply meats;
    private CategoryReply sweets;
    private CategoryReply bakeries;

    public CategoriesReply(CategoryReply fruits, CategoryReply vegetables, CategoryReply dairies, CategoryReply drinks, CategoryReply meats, CategoryReply sweets, CategoryReply bakeries) {
        this.fruits = fruits;
        this.vegetables = vegetables;
        this.dairies = dairies;
        this.drinks = drinks;
        this.meats = meats;
        this.sweets = sweets;
        this.bakeries = bakeries;
    }

    public CategoryReply getFruits() {
        return fruits;
    }

    public void setFruits(CategoryReply fruits) {
        this.fruits = fruits;
    }

    public CategoryReply getVegetables() {
        return vegetables;
    }

    public void setVegetables(CategoryReply vegetables) {
        this.vegetables = vegetables;
    }

    public CategoryReply getDairies() {
        return dairies;
    }

    public void setDairies(CategoryReply dairies) {
        this.dairies = dairies;
    }

    public CategoryReply getDrinks() {
        return drinks;
    }

    public void setDrinks(CategoryReply drinks) {
        this.drinks = drinks;
    }

    public CategoryReply getMeats() {
        return meats;
    }

    public void setMeats(CategoryReply meats) {
        this.meats = meats;
    }

    public CategoryReply getSweets() {
        return sweets;
    }

    public void setSweets(CategoryReply sweets) {
        this.sweets = sweets;
    }

    public CategoryReply getBakeries() {
        return bakeries;
    }

    public void setBakeries(CategoryReply bakeries) {
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
