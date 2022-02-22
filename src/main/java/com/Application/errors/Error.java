package com.Application.errors;

import javax.persistence.*;

@Entity
@Table(name = "Errors", uniqueConstraints = @UniqueConstraint(columnNames = {"error_id", "lang"}))
public class Error {

    @Id
    @Column(name = "error_id")
    Long errorId;

    @Column(name = "lang")
    String lang;

    @Column(name = "message")
    String message;

    public Error() {}

    public Error(Long errorId, String lang, String message) {
        this.errorId = errorId;
        this.lang = lang;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "errorId=" + errorId +
                ", lang='" + lang + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
