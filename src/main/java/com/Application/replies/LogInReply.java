package com.Application.replies;

import org.springframework.beans.factory.annotation.Value;

public class LogInReply {

    private GetInfoReply getInfoReply;
    private Boolean addInfo;

    @Value("0")
    private Integer count;

    public LogInReply(GetInfoReply getInfoReply, Boolean addInfo) {
        this.getInfoReply = getInfoReply;
        this.addInfo = addInfo; // Флаг, что пользователю надо показать заполнение инфо
    }

    public LogInReply(GetInfoReply getInfoReply, Boolean addInfo, Integer count) {
        this.getInfoReply = getInfoReply;
        this.addInfo = addInfo;
        this.count = count;
    }

    public GetInfoReply getGetInfoReply() {
        return getInfoReply;
    }

    public void setGetInfoReply(GetInfoReply getInfoReply) {
        this.getInfoReply = getInfoReply;
    }

    public Boolean getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(Boolean addInfo) {
        this.addInfo = addInfo;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "LogInReply{" +
                "getInfoReply=" + getInfoReply +
                ", addInfo=" + addInfo +
                ", count=" + count +
                '}';
    }
}
