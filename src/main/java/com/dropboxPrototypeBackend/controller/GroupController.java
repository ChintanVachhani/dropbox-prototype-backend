package com.dropboxPrototypeBackend.controller;

import com.dropboxPrototypeBackend.GlobalConstants;
import com.dropboxPrototypeBackend.entity.*;
import com.dropboxPrototypeBackend.repository.GroupFileRepository;
import com.dropboxPrototypeBackend.repository.GroupMemberRepository;
import com.dropboxPrototypeBackend.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(value = "/group")
public class GroupController {
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    private GroupMember groupMember;
    private List<GroupMember> groupMembers;

    @Autowired
    private GroupRepository groupRepository;
    private Group group;

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Object createGroup(@RequestParam(value = "uid") String email, @RequestParam(value = "name") String name, @RequestParam(value = "creator") String creator) {
        group = groupRepository.findByName(name);
        if (group != null) {
            try {
                groupRepository.save(new Group(name, creator));
                groupMemberRepository.save(new GroupMember(email, group.get_id()));
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot create group.", "Internal server error.");
            }
            Path source = Paths.get(GlobalConstants.boxPath, creator, "groups", group.getName());
            try {
                Files.createDirectories(source);
            } catch (IOException e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot create group.", "Internal server error.");
            }
            return new SuccessResponse(201, "Group created successfully.", group.getName());
        } else {
            return new ErrorResponse(404, "Cannot create group.", "Group already exists.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/member", method = RequestMethod.GET)
    public Object getAllGroupMembers(@RequestParam(value = "uid") String email, @RequestParam(value = "groupId") String groupId) {
        group = groupRepository.findBy_id(groupId);
        if (group != null) {
            groupMembers = groupMemberRepository.findByGroupId(groupId);
            if (groupMembers != null) {
                return new SuccessResponse(200, "Group members retrieved successfully.", groupMembers);
            } else {
                return new ErrorResponse(500, "Cannot retrieve group members.", "Internal server error.");
            }
        } else {
            return new ErrorResponse(404, "Cannot retrieve group members.", "Group not found.");
        }
    }

}
