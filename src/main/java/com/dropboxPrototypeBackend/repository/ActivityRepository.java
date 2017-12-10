package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, String> {
    Activity findByEmail(String email);
}
