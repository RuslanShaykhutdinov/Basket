package com.Application.enums;

public enum Categories {
    FRUITS{
        @Override
        public Long getCategory() {
            return 1L;
        }

        @Override
        public String getName() {
            return "Fruits";
        }
    },
    VEGETABLES {
        @Override
        public Long getCategory() {
            return 2L;
        }

        @Override
        public String getName() {
            return "Vegetables";
        }
    },
    DAIRIES {
        @Override
        public Long getCategory() {
            return 3L;
        }

        @Override
        public String getName() {
            return "Dairies";
        }
    },
    DRINKS {
        @Override
        public Long getCategory() {
            return 4L;
        }

        @Override
        public String getName() {
            return "Drinks";
        }
    },
    MEATS {
        @Override
        public Long getCategory() {
            return 5L;
        }

        @Override
        public String getName() {
            return "Meats";
        }
    },
    SWEETS{
        @Override
        public Long getCategory(){
            return 6L;
        }

        @Override
        public String getName() {
            return "Sweets";
        }
    },
    BAKERIES{
        @Override
        public Long getCategory(){
            return 7L;
        }

        @Override
        public String getName() {
            return "Bakeries";
        }
    };

    public abstract Long getCategory();
    public abstract String getName();
}
