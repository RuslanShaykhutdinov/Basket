package com.Application.replies;

public class LogInReply {

    private GetInfoReply getInfoReply;
    private Boolean addInfo;

    public LogInReply(GetInfoReply getInfoReply, Boolean addInfo) {
        this.getInfoReply = getInfoReply;
        this.addInfo = addInfo; // Флаг, что пользователю надо показать заполнение инфо
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

    @Override
    public String toString() {
        return "LogInReply{" +
                "getInfoReply=" + getInfoReply +
                ", addInfo=" + addInfo +
                '}';
    }
}
