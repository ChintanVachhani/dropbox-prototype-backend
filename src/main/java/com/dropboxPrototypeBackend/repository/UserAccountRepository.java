package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
    UserAccount findByEmail(String email);
}
