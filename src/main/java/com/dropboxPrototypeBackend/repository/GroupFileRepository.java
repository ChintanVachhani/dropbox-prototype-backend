package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.GroupFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupFileRepository extends MongoRepository<GroupFile, String> {
    List<GroupFile> findByGroupId(String groupId);
}
