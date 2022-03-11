package com.Application.replies;

import java.util.Date;

public class GetInfoReply {
    private String name;
    private String lastName;
    private Date birthDay;
    private Boolean smileImage;

    public GetInfoReply() {
    }

    public GetInfoReply(String name, String lastName, Date birthDay) {
        this.name = name;
        this.lastName = lastName;
        this.birthDay = birthDay;
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

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public Boolean getSmileImage() {
        return smileImage;
    }

    public void setSmileImage(Boolean smileImage) {
        this.smileImage = smileImage;
    }

    @Override
    public String toString() {
        return "GetInfoReply{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDay='" + birthDay + '\'' +
                ", smileImage=" + smileImage +
                '}';
    }
}
