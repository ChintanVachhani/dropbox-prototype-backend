package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.SharedFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SharedFileRepository extends MongoRepository<SharedFile, String> {
    List<SharedFile> findByOwner(String email);
    List<SharedFile> findBySharer(String email);
    SharedFile findByNameAndPathAndOwnerAndSharer(String name, String path, String owner, String sharer);
}
