package com.dropboxPrototypeBackend.controller;

import com.dropboxPrototypeBackend.GlobalConstants;
import com.dropboxPrototypeBackend.entity.*;
import com.dropboxPrototypeBackend.repository.ActivityRepository;
import com.dropboxPrototypeBackend.repository.FileRepository;
import com.dropboxPrototypeBackend.repository.SharedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping(value = "/file")
public class FileController {
    @Autowired
    private FileRepository fileRepository;
    private File file;
    private List<File> files;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SharedFileRepository sharedFileRepository;

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/link/{path}/{fileName}", method = RequestMethod.GET)
    public Object getFileByLink(@PathVariable(value = "path") String path, @PathVariable(value = "fileName") String fileName) {
        HttpHeaders headers = null;
        ByteArrayResource resource = null;
        try {
            String strData = "";
            //decryption
            SecretKeySpec skeyspec = new SecretKeySpec("link".getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted = cipher.doFinal(path.getBytes());
            strData = new String(decrypted);

            Path source = Paths.get(GlobalConstants.boxPath, strData, fileName);

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
                SecretKeySpec skeyspec = new SecretKeySpec("link".getBytes(), "Blowfish");
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

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(@RequestParam(value = "path") String path, @RequestParam(value = "name") String name, @RequestParam(value = "uid") String email) {
        HttpHeaders headers = null;
        ByteArrayResource resource = null;
        try {
            Path source = Paths.get(GlobalConstants.boxPath, email, path, name);

            headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            resource = new ByteArrayResource(Files.readAllBytes(source));
        } catch (IOException e) {
            e.printStackTrace();
        }
        activityRepository.save(new Activity(email, "Downloaded " + name));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Object uploadAndSaveFile(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "path") String path, @RequestParam(value = "owner") String owner, @RequestParam(value = "uid") String email) {
        if (file.isEmpty()) {
            return new ErrorResponse(400, "Cannot upload file.", "File is empty.");
        }

        try {
            // Get the file and save it in root
            byte[] bytes = file.getBytes();
            Path writePath = Paths.get(GlobalConstants.boxPath, email, "root", path, file.getOriginalFilename());
            Files.write(writePath, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fileRepository.findByOwnerAndNameAndPath(owner, file.getOriginalFilename(), Paths.get("root", path).toString()) == null) {
                fileRepository.save(new File(file.getOriginalFilename(), Paths.get("root", path).toString(), owner));
            }
            activityRepository.save(new Activity(email, "Uploaded " + file.getOriginalFilename()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SuccessResponse(201, "File successfully uploaded.", file.getOriginalFilename());
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/star", method = RequestMethod.PATCH)
    public Object starFile(@RequestParam(value = "_id") String _id, @RequestParam(value = "uid") String email) {
        file = fileRepository.findBy_id(_id);
        if (file != null) {
            try {
                file.setStarred(true);
                fileRepository.save(file);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot star file.", "File not found.");
            }
            activityRepository.save(new Activity(email, "Toggled Star for " + file.getName()));
            return new SuccessResponse(200, "File successfully starred.", file.getName());
        } else {
            return new ErrorResponse(404, "Cannot star file.", "File not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/share", method = RequestMethod.PATCH)
    public Object shareFile(@RequestParam(value = "_id") String _id, @RequestParam(value = "name") String name, @RequestParam(value = "path") String path, @RequestParam(value = "owner") String owner, @RequestParam(value = "sharers") List<String> sharers, @RequestParam(value = "uid") String email) {
        file = fileRepository.findBy_id(_id);
        if (file != null) {
            for (String sharer : sharers) {
                if (sharedFileRepository.findByNameAndPathAndOwnerAndSharer(name, path, owner, sharer) == null) {
                    try {
                        String strData = "";
                        //encryption
                        SecretKeySpec skeyspec = new SecretKeySpec("share".getBytes(), "Blowfish");
                        Cipher cipher = Cipher.getInstance("Blowfish");
                        cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
                        byte[] encrypted = cipher.doFinal(path.getBytes());
                        strData = new String(encrypted);

                        /*//decryption
                        SecretKeySpec skeyspec = new SecretKeySpec("share".getBytes(), "Blowfish");
                        Cipher cipher = Cipher.getInstance("Blowfish");
                        cipher.init(Cipher.DECRYPT_MODE, skeyspec);
                        byte[] decrypted = cipher.doFinal(strEncrypted.getBytes());
                        strData = new String(decrypted);*/

                        sharedFileRepository.save(new SharedFile(name, strData, owner, sharer, true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                file.setShared(true);
                file.setShow(true);
                fileRepository.save(file);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot share file.", "Internal server error.");
            }
            return new SuccessResponse(200, "File successfully shared.", file.getName());
        } else {
            return new ErrorResponse(404, "Cannot share file.", "File not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public Object renameFile(@RequestParam(value = "_id") String _id, @RequestParam(value = "name") String name, @RequestParam(value = "path") String path, @RequestParam(value = "uid") String email) {
        file = fileRepository.findBy_id(_id);
        if (file != null) {
            try {
                Path source = Paths.get(GlobalConstants.boxPath, file.getOwner(), path, file.getName());
                if (!Files.exists(source)) {
                    return new ErrorResponse(404, "Cannot rename file.", "File not found.");
                }
                Files.move(source, source.resolveSibling(name));
                file.setName(name);
                fileRepository.save(file);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot rename file.", "Internal server error.");
            }
            return new SuccessResponse(200, "File successfully renamed.", name);
        } else {
            return new ErrorResponse(404, "Cannot rename file.", "File not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Object deleteFile(@RequestParam(value = "_id") String _id, @RequestParam(value = "name") String name, @RequestParam(value = "path") String path, @RequestParam(value = "uid") String email) {
        file = fileRepository.findBy_id(_id);
        if (file != null) {
            try {
                Path source = Paths.get(GlobalConstants.boxPath, file.getOwner(), path, name);
                Files.deleteIfExists(source);
                fileRepository.delete(file);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot delete file.", "Internal server error.");
            }
            activityRepository.save(new Activity(email, "Deleted " + file.getName()));
            return new SuccessResponse(200, "File successfully deleted.", file.getName());
        } else {
            return new ErrorResponse(404, "Cannot delete file.", "File not found.");
        }
    }
}
