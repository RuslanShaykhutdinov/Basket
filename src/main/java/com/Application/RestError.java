package com.Application;

import org.springframework.http.HttpStatus;

public class RestError {
    private int error = 0;
    private String errMessage = null;
    private Object data = null;
    private HttpStatus status;

    public RestError() {
    }

    public RestError(Object data, HttpStatus status) {
        this.data = data;
        this.status = status;
    }

    public RestError(int error, String errMessage, HttpStatus status) {
        this.error = error;
        this.errMessage = errMessage;
        this.status = status;
    }

    public RestError(int error, String errMessage, Object data, HttpStatus status) {
        this.error = error;
        this.errMessage = errMessage;
        this.data = data;
        this.status = status;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RestError{" +
                "error=" + error +
                ", errMessage='" + errMessage + '\'' +
                ", data=" + data +
                '}';
    }
}
