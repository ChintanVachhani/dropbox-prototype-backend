package com.dropboxPrototypeBackend.controller;

import com.dropboxPrototypeBackend.GlobalConstants;
import com.dropboxPrototypeBackend.entity.ErrorResponse;
import com.dropboxPrototypeBackend.entity.SharedDirectory;
import com.dropboxPrototypeBackend.entity.SuccessResponse;
import com.dropboxPrototypeBackend.repository.SharedDirectoryRepository;
import com.dropboxPrototypeBackend.util.ZipDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(value = "/sharedDirectory")
public class SharedDirectoryController {
    @Autowired
    private SharedDirectoryRepository sharedDirectoryRepository;
    private SharedDirectory sharedDirectory;
    private List<SharedDirectory> sharedDirectories;

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object getAllSharedDirectories(@RequestParam(value = "uid") String email, @RequestParam(value = "path") String path, @RequestParam(value = "name") String name) {
        String strData = "";
        String strDatae = "";

        try {
            //decryption
            SecretKeySpec skeyspec = new SecretKeySpec("share".getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted = cipher.doFinal(path.getBytes());
            strData = new String(decrypted);

            //encryption
            SecretKeySpec skeyspece = new SecretKeySpec("share".getBytes(), "Blowfish");
            Cipher ciphere = Cipher.getInstance("Blowfish");
            ciphere.init(Cipher.ENCRYPT_MODE, skeyspece);
            byte[] encrypted = ciphere.doFinal(Paths.get(strData, name).toString().getBytes());
            strDatae = new String(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse(500, "Cannot retrieve shared directories.", "Internal server error.");
        }
        sharedDirectories = sharedDirectoryRepository.findByPathAndSharer(strDatae, email);

        if (sharedDirectories != null) {
            return new SuccessResponse(200, "Shared directories retrieved successfully.", sharedDirectories);
        } else {
            return new ErrorResponse(500, "Cannot retrieve shared directories.", "Internal server error.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object listSharedDirectories(@RequestParam(value = "uid") String email) {
        sharedDirectories = sharedDirectoryRepository.findBySharerAndShowIsTrue(email);
        if (sharedDirectories != null) {
            return new SuccessResponse(200, "Shared directories retrieved successfully.", sharedDirectories);
        } else {
            return new ErrorResponse(500, "Cannot retrieve shared directories.", "Internal server error.");
        }
    }


    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(@RequestParam(value = "path") String path, @RequestParam(value = "owner") String owner, @RequestParam(value = "name") String name, @RequestParam(value = "uid") String email) {
        HttpHeaders headers = null;
        ByteArrayResource resource = null;
        try {
            String strData = "";
            //decryption
            SecretKeySpec skeyspec = new SecretKeySpec("share".getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted = cipher.doFinal(path.getBytes());
            strData = new String(decrypted);
            Path source = Paths.get(GlobalConstants.boxPath, owner, strData, name);
            Path sourceZip = Paths.get(GlobalConstants.boxPath, owner, "tmp", name + ".zip");
            ZipDirectory zipDirectory = new ZipDirectory(sourceZip.toString(), source.toString());

            headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            resource = new ByteArrayResource(Files.readAllBytes(sourceZip));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            Files.deleteIfExists(sourceZip);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/star", method = RequestMethod.PATCH)
    public Object starFile(@RequestParam(value = "_id") String _id, @RequestParam(value = "uid") String email) {
        sharedDirectory = sharedDirectoryRepository.findBy_id(_id);
        if (sharedDirectory != null) {
            try {
                sharedDirectory.setStarred(true);
                sharedDirectoryRepository.save(sharedDirectory);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot star shared directory.", "Shared directory not found.");
            }
            return new SuccessResponse(200, "Shared directory successfully starred.", sharedDirectory.getName());
        } else {
            return new ErrorResponse(404, "Cannot star shared directory.", "Shared directory not found.");
        }
    }
}
