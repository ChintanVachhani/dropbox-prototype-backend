package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.SharedDirectory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SharedDirectoryRepository extends MongoRepository<SharedDirectory, String> {
    List<SharedDirectory> findByOwner(String email);
    List<SharedDirectory> findBySharer(String email);
}
