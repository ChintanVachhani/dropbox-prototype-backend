package com.dropboxPrototypeBackend.repository;

import com.dropboxPrototypeBackend.entity.GroupMember;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupMemberRepository extends MongoRepository<GroupMember, String> {
    List<GroupMember> findByGroupId(String groupId);
}
