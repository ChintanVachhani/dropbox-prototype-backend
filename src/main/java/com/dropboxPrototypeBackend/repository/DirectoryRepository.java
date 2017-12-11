package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.Directory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DirectoryRepository extends MongoRepository<Directory, String> {
    List<Directory> findByOwnerAndPath(String email, String path);
    List<Directory> findByOwnerAndStarredIsTrue(String email);
    Directory findBy_id(String id);
    Directory findByOwnerAndNameAndPath(String owner, String name, String path);
}
