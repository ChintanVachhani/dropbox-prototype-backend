package com.dropboxPrototypeBackend.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class SharedFile implements Serializable {

    @Id
    private String _id;

    private String name;
    private String path;
    private String owner;
    private Boolean starred;
    private String sharer;
    private String link;
    private Boolean show;

    public SharedFile() {
    }

    public SharedFile(String name, String path, String owner, String sharer) {
        this.name = name;
        this.path = path;
        this.owner = owner;
        this.sharer = sharer;
        this.starred = false;
        this.show = false;
        this.link = "";
    }

    public SharedFile(String name, String path, String owner, String sharer, Boolean show) {
        this.name = name;
        this.path = path;
        this.owner = owner;
        this.sharer = sharer;
        this.show = show;
        this.starred = false;
        this.link = "";
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

    public String getSharer() {
        return sharer;
    }

    public void setSharer(String sharer) {
        this.sharer = sharer;
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
