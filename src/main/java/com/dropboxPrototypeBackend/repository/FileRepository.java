package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileRepository extends MongoRepository<File, String> {
    List<File> findByEmail(String email);
}
