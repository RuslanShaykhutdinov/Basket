package com.Application.errors;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "errors")
public class Error {

    @Id
    @SequenceGenerator(name="errorIdGenerator", sequenceName = "error_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "errorIdGenerator")
    @Column(name = "error_id")
    Long errorId;

    @Column(name = "error_num")
    Integer errorNum;

    @Column(name = "lang")
    String lang;

    @Column(name = "message")
    String message;

    public Error() {}

    public Error(Integer errorNum, String lang, String message) {
        this.errorNum = errorNum;
        this.lang = lang;
        this.message = message;
    }

    public Long getErrorId() {
        return errorId;
    }

    public void setErrorId(Long errorId) {
        this.errorId = errorId;
    }

    public Integer getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Error error = (Error) o;
        return Objects.equals(errorId, error.errorId) && Objects.equals(errorNum, error.errorNum) && Objects.equals(lang, error.lang) && Objects.equals(message, error.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorId, errorNum, lang, message);
    }

    @Override
    public String toString() {
        return "Error{" +
                "errorId=" + errorId +
                ", errorNum=" + errorNum +
                ", lang='" + lang + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
