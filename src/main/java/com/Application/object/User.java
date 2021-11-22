package com.Application.object;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name="user_id_Generator", sequenceName = "user_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "user_id_Generator")
    @Column(name = "user_id")
    private Long userId;

    @Basic
    @Column(name = "login")
    private String login;

    @Basic
    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "user_info")
    private String userInfo;

    @Basic
    @Column(name = "passwordCheck")
    private int passwordCheck;

    @Basic
    @Column(name = "blocked")
    private boolean blocked;

    public User() {
        this.passwordCheck = 0;
        this.blocked = false;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userID) {
        this.userId = userID;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPasswordCheck() {
        return passwordCheck;
    }

    public void setPasswordCheck(int passwordCheck) {
        this.passwordCheck = passwordCheck;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return passwordCheck == user.passwordCheck && blocked == user.blocked && Objects.equals(userId, user.userId) && login.equals(user.login) && password.equals(user.password) && name.equals(user.name) && Objects.equals(lastName, user.lastName) && age.equals(user.age) && Objects.equals(userInfo, user.userInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, login, password, name, lastName, age, userInfo, passwordCheck, blocked);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", userInfo='" + userInfo + '\'' +
                ", passwordCheck=" + passwordCheck +
                ", blocked=" + blocked +
                '}';
    }
}
