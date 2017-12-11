package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findAllByEmail(String email);
    List<Activity> findFirst5ByEmailOrderByCreatedAtDesc(String email);
}
