CREATE TABLE if NOT EXISTS Products(

    product_id BIGSERIAL PRIMARY KEY ,
    name VARCHAR(25) NOT NULL,
    price INTEGER NOT NULL,
    weight INTEGER NOT NULL ,
    info VARCHAR(200),
    availability BOOLEAN NOT NULL
);

CREATE TABLE if NOT EXISTS Users(

    user_id BIGSERIAL PRIMARY KEY ,
    login VARCHAR(25) NOT NULL,
    password VARCHAR(200) NOT NULL,
    name VARCHAR(25),
    last_name VARCHAR(25),
    age INTEGER,
    user_info VARCHAR(200),
    passwordCheck INTEGER,
    blocked BOOLEAN NOT NULL
);

CREATE TABLE if NOT EXISTS Cards(

    card_id BIGSERIAL PRIMARY KEY ,
    amount_of_money INTEGER NOT NULL,
    user_id BIGSERIAL NOT NULL

);

CREATE TABLE if NOT EXISTS Baskets(
    basket_id BIGSERIAL PRIMARY KEY ,
    user_id BIGSERIAL NOT NULL ,
    list_of_products VARCHAR
);