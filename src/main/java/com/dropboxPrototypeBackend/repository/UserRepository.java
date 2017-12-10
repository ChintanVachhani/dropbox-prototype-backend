package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    List<User> findAllByEmailLikeOrFirstNameLikeOrLastNameLike(String email, String firstName, String lastName);
}
