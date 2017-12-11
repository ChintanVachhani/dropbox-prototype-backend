package com.dropboxPrototypeBackend.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class GroupMember implements Serializable {

    @Id
    private String _id;

    private String email;
    private String groupId;

    public GroupMember() {
    }

    public GroupMember(String email, String groupId) {
        this.email = email;
        this.groupId = groupId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
