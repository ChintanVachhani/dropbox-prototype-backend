package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.SharedFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SharedFileRepository extends MongoRepository<SharedFile, String> {
    List<SharedFile> findByEmail(String email);
}
