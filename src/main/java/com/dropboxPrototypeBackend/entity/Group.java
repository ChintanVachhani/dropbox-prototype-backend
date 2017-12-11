package com.dropboxPrototypeBackend.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

public class Group implements Serializable {

    @Id
    private String _id;

    private String name;
    private String creator;

    public Group() {
    }

    public Group(String name, String creator) {
        this.name = name;
        this.creator = creator;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
