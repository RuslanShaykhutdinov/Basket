package com.basket.Basket.object;

import javax.persistence.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @SequenceGenerator(name="userIdGenerator", sequenceName = "user_id_seq", schema = "public", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "userIdGenerator")
    @Column(name = "user_id")
    private Long userID;

    @Basic
    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Basic
    @Column(name = "age")
    private Integer age;

    @Column(name = "user_info")
    private String userInfo;

    public User() {
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public User(String name, String lastName, Integer age, String userInfo) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.userInfo = userInfo;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", userInfo='" + userInfo + '\'' +
                '}';
    }
}
