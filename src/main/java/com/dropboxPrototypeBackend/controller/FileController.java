package com.dropboxPrototypeBackend.controller;

import com.dropboxPrototypeBackend.GlobalConstants;
import com.dropboxPrototypeBackend.entity.Activity;
import com.dropboxPrototypeBackend.entity.ErrorResponse;
import com.dropboxPrototypeBackend.entity.File;
import com.dropboxPrototypeBackend.entity.SuccessResponse;
import com.dropboxPrototypeBackend.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(value = "/file")
public class FileController {
    @Autowired
    private FileRepository fileRepository;
    private File file;
    private List<File> files;

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/link/{path}/{fileName}", method = RequestMethod.GET)
    public Object getFileByLink(@PathVariable(value = "path") String path, @PathVariable(value = "fileName") String fileName) {
        //TODO implement download
        return null;
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object getAllFiles(@RequestParam(value = "uid") String email, @RequestParam(value = "path") String path) {
        files = fileRepository.findByOwnerAndPath(email, path);
        if (files != null) {
            return new SuccessResponse(200, "Files retrieved successfully.", files);
        } else {
            return new ErrorResponse(500, "Cannot retrieve files.", "Internal server error.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/starred", method = RequestMethod.GET)
    public Object getAllStarredFiles(@RequestParam(value = "uid") String email) {
        files = fileRepository.findByOwnerAndStarredIsTrue(email);
        if (files != null) {
            return new SuccessResponse(200, "Files retrieved successfully.", files);
        } else {
            return new ErrorResponse(500, "Cannot retrieve files.", "Internal server error.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/link", method = RequestMethod.PATCH)
    public Object createShareableLink(@RequestParam(value = "_id") String _id) {
        file = fileRepository.findBy_id(_id);
        if (file != null) {
            try {
                String strData = "";
                //encryption
                SecretKeySpec skeyspec = new SecretKeySpec("link".getBytes(), "Blowfish");
                Cipher cipher = Cipher.getInstance("Blowfish");
                cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
                byte[] encrypted = cipher.doFinal(Paths.get(file.getOwner(), file.getPath()).toString().getBytes());
                strData = new String(encrypted);
                String link = Paths.get(GlobalConstants.server + ":" + GlobalConstants.port, "file", "link", strData, file.getName()).toString();

                /*//decryption
                SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
                Cipher cipher = Cipher.getInstance("Blowfish");
                cipher.init(Cipher.DECRYPT_MODE, skeyspec);
                byte[] decrypted = cipher.doFinal(strEncrypted.getBytes());
                strData = new String(decrypted);*/

                file.setLink(link);
                fileRepository.save(file);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot create shareable link.", "Internal server error.");
            }
            return new SuccessResponse(200, "File's shareable link successfully created.", file.getLink());
        } else {
            return new ErrorResponse(404, "Cannot create shareable link.", "File not found.");
        }
    }
}
