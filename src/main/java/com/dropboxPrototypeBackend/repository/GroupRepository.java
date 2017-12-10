package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupRepository extends MongoRepository<Group, String> {
    List<Group> findByCreator(String creator);
}
