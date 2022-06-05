package com.Application.enums;

public enum Categories {
    FRUITS{
        @Override
        public Long getCategory() {
            return 1L;
        }

        @Override
        public String getName(String lang) {
            switch (lang){
                case "ru":
                    return "Фрукты";
                case "uz":
                    return "Mevalar";
                default:
                    return "Fruits";
            }
        }
    },
    VEGETABLES {
        @Override
        public Long getCategory() {
            return 2L;
        }

        @Override
        public String getName(String lang) {
            switch (lang){
                case "ru":
                    return "Овощи";
                case "uz":
                    return "Sabzavotlar";
                default:
                    return "Vegetables";
            }
        }
    },
    DAIRIES {
        @Override
        public Long getCategory() {
            return 3L;
        }

        public String getName(String lang) {
            switch (lang){
                case "ru":
                    return "Молочные продукты";
                case "uz":
                    return "Sutli mahsulotlar";
                default:
                    return "Dairies";
            }
        }
    },
    DRINKS {
        @Override
        public Long getCategory() {
            return 4L;
        }

        public String getName(String lang) {
            switch (lang){
                case "ru":
                    return "Напитки";
                case "uz":
                    return "Ichilimliklar";
                default:
                    return "Drinks";
            }
        }
    },
    MEATS {
        @Override
        public Long getCategory() {
            return 5L;
        }

        public String getName(String lang) {
            switch (lang){
                case "ru":
                    return "Мясные изделия";
                case "uz":
                    return "Go'sht mahsulotlari";
                default:
                    return "Meats";
            }
        }
    },
    SWEETS{
        @Override
        public Long getCategory(){
            return 6L;
        }

        public String getName(String lang) {
            switch (lang){
                case "ru":
                    return "Сладости";
                case "uz":
                    return "Shirinliklar";
                default:
                    return "Candies";
            }
        }
    },
    BAKERIES{
        @Override
        public Long getCategory(){
            return 7L;
        }

        public String getName(String lang) {
            switch (lang){
                case "ru":
                    return "Выпечка";
                case "uz":
                    return "Non mahsulotlari";
                default:
                    return "Bakeries";
            }
        }
    };

    public abstract Long getCategory();
    public abstract String getName(String lang);
}
