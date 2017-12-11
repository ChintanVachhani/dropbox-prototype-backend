package com.dropboxPrototypeBackend.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class Directory implements Serializable {

    @Id
    private String _id;

    private String name;
    private String path;
    private String owner;
    private Boolean starred;
    private Boolean shared;
    private String link;
    private Boolean show;

    public Directory() {
    }

    public Directory(String name, String path, String owner) {
        this.name = name;
        this.path = path;
        this.owner = owner;
        this.starred = false;
        this.shared = false;
        this.link = "";
        this.show = false;
    }

    public Directory(String name, String path, String owner, Boolean shared, Boolean show) {
        this.name = name;
        this.path = path;
        this.owner = owner;
        this.shared = shared;
        this.starred = false;
        this.link = "";
        this.show = show;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getStarred() {
        return starred;
    }

    public void setStarred(Boolean starred) {
        this.starred = starred;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
