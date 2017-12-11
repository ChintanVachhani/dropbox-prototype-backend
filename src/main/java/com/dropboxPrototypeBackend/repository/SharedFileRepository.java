package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.SharedFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SharedFileRepository extends MongoRepository<SharedFile, String> {
    SharedFile findBy_id(String _id);
    List<SharedFile> findBySharerAndShowIsTrue(String email);
    SharedFile findByNameAndPathAndOwnerAndSharer(String name, String path, String owner, String sharer);
    List<SharedFile> findByPathAndSharer(String path, String sharer);
}
