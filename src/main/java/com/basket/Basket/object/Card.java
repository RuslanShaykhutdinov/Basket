package com.basket.Basket.object;

import com.basket.Basket.object.User;

import javax.persistence.*;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @SequenceGenerator(name="cardIdGenerator", sequenceName = "card_id_seq", schema = "public", initialValue = 8600, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cardIdGenerator")
    @Column(name = "card_number")
    private Long cardNumber;

    @Basic
    @Column(name = "amount_of_money")
    private Integer amountOfMoney;

    @OneToOne
    @JoinColumn(name = "holder",nullable = false)
    private User user;

    public Card() {
    }

    public Card(Integer amountOfMoney, User user) {
        this.amountOfMoney = amountOfMoney;
        this.user = user;
    }

    public Long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Integer amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber=" + cardNumber +
                ", amountOfMoney=" + amountOfMoney +
                ", user=" + user +
                '}';
    }
}

