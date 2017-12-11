package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.SharedDirectory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SharedDirectoryRepository extends MongoRepository<SharedDirectory, String> {
    SharedDirectory findBy_id(String _id);
    List<SharedDirectory> findBySharerAndShowIsTrue(String email);
    SharedDirectory findByNameAndPathAndOwnerAndSharer(String name, String path, String owner, String sharer);
    List<SharedDirectory> findByPathAndSharer(String path, String sharer);
}
