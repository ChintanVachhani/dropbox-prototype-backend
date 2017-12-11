package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileRepository extends MongoRepository<File, String> {
    List<File> findByOwnerAndPath(String email, String path);
    List<File> findByOwnerAndStarredIsTrue(String email);
    File findBy_id(String id);
    File findByOwnerAndNameAndPath(String owner, String name, String path);
}
