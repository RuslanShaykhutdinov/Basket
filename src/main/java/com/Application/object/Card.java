package com.Application.object;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @SequenceGenerator(name="cardIdGenerator", sequenceName = "card_id_seq", schema = "public", initialValue = 8600, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cardIdGenerator")
    @Column(name = "card_id")
    private Long cardId;

    @Basic
    @Column(name = "amount_of_money")
    private Integer amountOfMoney;

    @Basic
    @Column(name = "user_id")
    private Long userId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, updatable = false, insertable = false)
    private User user;

    public Card() {
    }

    public Card(Integer amountOfMoney, Long userId) {
        this.amountOfMoney = amountOfMoney;
        this.userId = userId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardNumber) {
        this.cardId = cardNumber;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardId.equals(card.cardId) && amountOfMoney.equals(card.amountOfMoney) && userId.equals(card.userId) && user.equals(card.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, amountOfMoney, userId, user);
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", amountOfMoney=" + amountOfMoney +
                ", userId=" + userId +
                ", user=" + user +
                '}';
    }
}

