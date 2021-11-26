package com.Application;

public enum Categories {
    FRUITS{
        @Override
        public Long getCategory() {
            return 1L;
        }
    },
    VEGETABLES {
        @Override
        public Long getCategory() {
            return 2L;
        }
    },
    DAIRIES {
        @Override
        public Long getCategory() {
            return 3L;
        }
    },
    DRINKS {
        @Override
        public Long getCategory() {
            return 4L;
        }
    },
    MEATS {
        @Override
        public Long getCategory() {
            return 5L;
        }
    },
    SWEETS{
        @Override
        public Long getCategory(){
            return 6L;
        }
    },
    BAKERIES{
        @Override
        public Long getCategory(){
            return 7L;
        }
    };

    public abstract Long getCategory();
}
