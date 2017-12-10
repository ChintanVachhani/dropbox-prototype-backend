package com.dropboxPrototypeBackend.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

public class Activity implements Serializable {

    @Id
    private String _id;

    private String email;
    private String log;
    private Date createdAt;

    public Activity() {
    }

    public Activity(String email, String log) {
        this.email = email;
        this.log = log;
        this.createdAt = new Date();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
