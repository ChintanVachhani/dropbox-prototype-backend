package com.dropboxPrototypeBackend.controller;

import com.dropboxPrototypeBackend.GlobalConstants;
import com.dropboxPrototypeBackend.entity.*;
import com.dropboxPrototypeBackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(value = "/groupFile")
public class GroupFileController {
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    private GroupMember groupMember;

    @Autowired
    private GroupFileRepository groupFileRepository;
    private GroupFile groupFile;
    private List<GroupFile> groupFiles;

    @Autowired
    private GroupRepository groupRepository;
    private Group group;

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object getAllFiles(@RequestParam(value = "uid") String email, @RequestParam(value = "groupId") String groupId) {
        groupMember = groupMemberRepository.findByGroupIdAndEmail(groupId, email);
        if (groupMember != null) {
            groupFiles = groupFileRepository.findByGroupId(groupId);
            if (groupFiles != null) {
                return new SuccessResponse(200, "Files retrieved successfully.", groupFiles);
            } else {
                return new ErrorResponse(500, "Cannot retrieve files.", "Internal server error.");
            }
        } else {
            return new ErrorResponse(400, "Cannot retrieve files from the group.", "Group member not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(@RequestParam(value = "groupId") String groupId, @RequestParam(value = "name") String name, @RequestParam(value = "uid") String email) {
        group = groupRepository.findBy_id(groupId);
        HttpHeaders headers = null;
        ByteArrayResource resource = null;
        try {
            Path source = Paths.get(GlobalConstants.boxPath, group.getCreator(), "groups", group.getName(), name);

            headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            resource = new ByteArrayResource(Files.readAllBytes(source));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Object uploadAndSaveFile(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "groupId") String groupId, @RequestParam(value = "uid") String email) {
        if (file.isEmpty()) {
            return new ErrorResponse(400, "Cannot upload file.", "File is empty.");
        }

        group = groupRepository.findBy_id(groupId);

        try {
            // Get the file and save it in root
            byte[] bytes = file.getBytes();
            Path writePath = Paths.get(GlobalConstants.boxPath, group.getCreator(), "groups", group.getName(), file.getOriginalFilename());
            Files.write(writePath, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            groupFileRepository.save(new GroupFile(file.getOriginalFilename(), groupId, email));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SuccessResponse(201, "File successfully uploaded.", file.getOriginalFilename());
    }
}
