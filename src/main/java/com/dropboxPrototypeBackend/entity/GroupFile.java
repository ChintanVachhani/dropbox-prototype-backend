package com.dropboxPrototypeBackend.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class GroupFile implements Serializable {

    @Id
    private String _id;

    private String name;
    private String groupId;
    private String uploader;

    public GroupFile() {
    }

    public GroupFile(String name, String groupId, String uploader) {
        this.name = name;
        this.groupId = groupId;
        this.uploader = uploader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
}
