package com.dropboxPrototypeBackend.controller;

import com.dropboxPrototypeBackend.GlobalConstants;
import com.dropboxPrototypeBackend.entity.*;
import com.dropboxPrototypeBackend.repository.SharedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(value = "/sharedFile")
public class SharedFileController {
    @Autowired
    private SharedFileRepository sharedFileRepository;
    private SharedFile sharedFile;
    private List<SharedFile> sharedFiles;

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object getAllSharedFiles(@RequestParam(value = "uid") String email, @RequestParam(value = "path") String path, @RequestParam(value = "name") String name) {
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
            return new ErrorResponse(500, "Cannot retrieve shared files.", "Internal server error.");
        }
        sharedFiles = sharedFileRepository.findByPathAndSharer(strDatae, email);

        if (sharedFiles != null) {
            return new SuccessResponse(200, "Shared files retrieved successfully.", sharedFiles);
        } else {
            return new ErrorResponse(500, "Cannot retrieve shared files.", "Internal server error.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Object listSharedFiles(@RequestParam(value = "uid") String email) {
        sharedFiles = sharedFileRepository.findBySharerAndShowIsTrue(email);
        if (sharedFiles != null) {
            return new SuccessResponse(200, "Shared files retrieved successfully.", sharedFiles);
        } else {
            return new ErrorResponse(500, "Cannot retrieve shared files.", "Internal server error.");
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
            Path source = Paths.get(GlobalConstants.boxPath, email, strData, name);

            headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            resource = new ByteArrayResource(Files.readAllBytes(source));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/star", method = RequestMethod.PATCH)
    public Object starFile(@RequestParam(value = "_id") String _id, @RequestParam(value = "uid") String email) {
        sharedFile = sharedFileRepository.findBy_id(_id);
        if (sharedFile != null) {
            try {
                sharedFile.setStarred(true);
                sharedFileRepository.save(sharedFile);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot star shared file.", "Shared file not found.");
            }
            return new SuccessResponse(200, "Shared file successfully starred.", sharedFile.getName());
        } else {
            return new ErrorResponse(404, "Cannot star shared file.", "Shared file not found.");
        }
    }
}
