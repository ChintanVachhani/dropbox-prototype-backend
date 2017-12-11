package com.dropboxPrototypeBackend.controller;

import com.dropboxPrototypeBackend.GlobalConstants;
import com.dropboxPrototypeBackend.entity.*;
import com.dropboxPrototypeBackend.repository.ActivityRepository;
import com.dropboxPrototypeBackend.repository.DirectoryRepository;
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
@RequestMapping(value = "/directory")
public class DirectoryController {
    @Autowired
    private DirectoryRepository directoryRepository;
    private Directory directory;
    private List<Directory> directories;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SharedDirectoryRepository sharedDirectoryRepository;

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/link/{path}/{directoryName}", method = RequestMethod.GET)
    public Object getDirectoryByLink(@PathVariable(value = "path") String path, @PathVariable(value = "directoryName") String directoryName) {
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

            Path source = Paths.get(GlobalConstants.boxPath, strData, directoryName);
            Path sourceZip = Paths.get(GlobalConstants.boxPath, Paths.get(strData).getRoot().toString(), "tmp", directoryName + ".zip");
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
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object getAllDirectories(@RequestParam(value = "uid") String email, @RequestParam(value = "path") String path) {
        directories = directoryRepository.findByOwnerAndPath(email, path);
        if (directories != null) {
            return new SuccessResponse(200, "Directories retrieved successfully.", directories);
        } else {
            return new ErrorResponse(500, "Cannot retrieve directories.", "Internal server error.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/starred", method = RequestMethod.GET)
    public Object getAllStarredDirectories(@RequestParam(value = "uid") String email) {
        directories = directoryRepository.findByOwnerAndStarredIsTrue(email);
        if (directories != null) {
            return new SuccessResponse(200, "Directories retrieved successfully.", directories);
        } else {
            return new ErrorResponse(500, "Cannot retrieve directories.", "Internal server error.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/link", method = RequestMethod.PATCH)
    public Object createShareableLink(@RequestParam(value = "_id") String _id) {
        directory = directoryRepository.findBy_id(_id);
        if (directory != null) {
            try {
                String strData = "";
                //encryption
                SecretKeySpec skeyspec = new SecretKeySpec("link".getBytes(), "Blowfish");
                Cipher cipher = Cipher.getInstance("Blowfish");
                cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
                byte[] encrypted = cipher.doFinal(Paths.get(directory.getOwner(), directory.getPath()).toString().getBytes());
                strData = new String(encrypted);
                String link = Paths.get(GlobalConstants.server + ":" + GlobalConstants.port, "directory", "link", strData, directory.getName()).toString();

                /*//decryption
                SecretKeySpec skeyspec = new SecretKeySpec("link".getBytes(), "Blowfish");
                Cipher cipher = Cipher.getInstance("Blowfish");
                cipher.init(Cipher.DECRYPT_MODE, skeyspec);
                byte[] decrypted = cipher.doFinal(strEncrypted.getBytes());
                strData = new String(decrypted);*/

                directory.setLink(link);
                directoryRepository.save(directory);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot create shareable link.", "Internal server error.");
            }
            return new SuccessResponse(200, "Directory's shareable link successfully created.", directory.getLink());
        } else {
            return new ErrorResponse(404, "Cannot create shareable link.", "Directory not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDirectory(@RequestParam(value = "path") String path, @RequestParam(value = "name") String name, @RequestParam(value = "uid") String email) {
        Path source = Paths.get(GlobalConstants.boxPath, email, path, name);
        Path sourceZip = Paths.get(GlobalConstants.boxPath, email, "tmp", name + ".zip");
        ZipDirectory zipDirectory = new ZipDirectory(sourceZip.toString(), source.toString());
        HttpHeaders headers = null;
        ByteArrayResource resource = null;
        try {
            headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            resource = new ByteArrayResource(Files.readAllBytes(sourceZip));
        } catch (IOException e) {
            e.printStackTrace();
        }
        activityRepository.save(new Activity(email, "Downloaded " + name));
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
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Object createDirectory(@RequestParam(value = "name") String name, @RequestParam(value = "path") String path, @RequestParam(value = "owner") String owner, @RequestParam(value = "uid") String email) {
        Boolean directoryExists = false;
        String directoryName = name;
        int index = 0;
        do {
            Path source = Paths.get(GlobalConstants.boxPath, email, "root", path, directoryName);
            directoryExists = false;
            if (Files.exists(source)) {
                ++index;
                directoryName = name + " (" + index + ")";
                directoryExists = true;
            }

        } while (directoryExists);

        Path source = Paths.get(GlobalConstants.boxPath, email, "root", path, directoryName);
        try {
            Files.createDirectories(source);
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResponse(500, "Cannot create directory.", "Internal server error.");
        }
        try {
            directoryRepository.save(new Directory(directoryName, Paths.get("root", path).toString(), owner));
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse(400, "Cannot create directory.", "Invalid Data.");
        }
        activityRepository.save(new Activity(email, "Created " + directoryName));
        return new SuccessResponse(201, "Directory successfully created.", directoryName);
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/star", method = RequestMethod.PATCH)
    public Object starDirectory(@RequestParam(value = "_id") String _id, @RequestParam(value = "uid") String email) {
        directory = directoryRepository.findBy_id(_id);
        if (directory != null) {
            try {
                directory.setStarred(true);
                directoryRepository.save(directory);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot star directory.", "Directory not found.");
            }
            activityRepository.save(new Activity(email, "Toggled Star for " + directory.getName()));
            return new SuccessResponse(200, "Directory successfully starred.", directory.getName());
        } else {
            return new ErrorResponse(404, "Cannot star directory.", "Directory not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/share", method = RequestMethod.PATCH)
    public Object shareDirectory(@RequestParam(value = "_id") String _id, @RequestParam(value = "name") String name, @RequestParam(value = "path") String path, @RequestParam(value = "owner") String owner, @RequestParam(value = "sharers") List<String> sharers, @RequestParam(value = "uid") String email) {
        //TODO recursive sharing directories
        directory = directoryRepository.findBy_id(_id);
        if (directory != null) {
            for (String sharer : sharers) {
                if (sharedDirectoryRepository.findByNameAndPathAndOwnerAndSharer(name, path, owner, sharer) == null) {
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

                        sharedDirectoryRepository.save(new SharedDirectory(name, strData, owner, sharer, true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                directory.setShared(true);
                directory.setShow(true);
                directoryRepository.save(directory);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot share directory.", "Internal server error.");
            }
            return new SuccessResponse(200, "Directory successfully shared.", directory.getName());
        } else {
            return new ErrorResponse(404, "Cannot share directory.", "Directory not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public Object renameDirectory(@RequestParam(value = "_id") String _id, @RequestParam(value = "name") String name, @RequestParam(value = "path") String path, @RequestParam(value = "uid") String email) {
        directory = directoryRepository.findBy_id(_id);
        if (directory != null) {
            try {
                Path source = Paths.get(GlobalConstants.boxPath, directory.getOwner(), path, directory.getName());
                if (!Files.exists(source)) {
                    return new ErrorResponse(404, "Cannot rename directory.", "Directory not found.");
                }
                Files.move(source, source.resolveSibling(name));
                directory.setName(name);
                directoryRepository.save(directory);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot rename directory.", "Internal server error.");
            }
            return new SuccessResponse(200, "Directory successfully renamed.", name);
        } else {
            return new ErrorResponse(404, "Cannot rename directory.", "Directory not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Object deleteDirectory(@RequestParam(value = "_id") String _id, @RequestParam(value = "name") String name, @RequestParam(value = "path") String path, @RequestParam(value = "uid") String email) {
        directory = directoryRepository.findBy_id(_id);
        if (directory != null) {
            try {
                Path source = Paths.get(GlobalConstants.boxPath, directory.getOwner(), path, name);
                Files.deleteIfExists(source);
                directoryRepository.delete(directory);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(500, "Cannot delete directory.", "Internal server error.");
            }
            activityRepository.save(new Activity(email, "Deleted " + directory.getName()));
            return new SuccessResponse(200, "Directory successfully deleted.", directory.getName());
        } else {
            return new ErrorResponse(404, "Cannot delete directory.", "Directory not found.");
        }
    }
}
