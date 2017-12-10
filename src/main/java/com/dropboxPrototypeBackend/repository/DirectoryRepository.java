package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.Directory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DirectoryRepository extends MongoRepository<Directory, String> {
    List<Directory> findByEmail(String email);
}
