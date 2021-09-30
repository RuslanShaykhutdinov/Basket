CREATE TABLE  IF NOT EXISTS products(

    id BIGSERIAL PRIMARY KEY ,
    name VARCHAR(200),
    price INTEGER ,
    weight INTEGER NOT NULL ,
    info VARCHAR(200),
    availability BOOLEAN
);

CREATE TABLE  IF NOT EXISTS users(

    user_id BIGSERIAL PRIMARY KEY ,
    name VARCHAR(200) NOT NULL,
    last_name VARCHAR(200),
    age INTEGER NOT NULL ,
    user_info VARCHAR(200)
);

CREATE TABLE  IF NOT EXISTS cards(

    card_number BIGSERIAL PRIMARY KEY ,
    amount_of_money INTEGER NOT NULL,
    holder BIGINT NOT NULL

);

CREATE TABLE IF NOT EXISTS baskets(
    id BIGSERIAL PRIMARY KEY ,
    user_basket BIGINT NOT NULL ,
    list_of_products VARCHAR
);