package com.Application.replies;

public class LogInReply {

    private Long userId;
    private Boolean addInfo;

    public LogInReply(Long userId, Boolean addInfo) {
        this.userId = userId;
        this.addInfo = addInfo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
                "userId=" + userId +
                ", addInfo=" + addInfo +
                '}';
    }
}
