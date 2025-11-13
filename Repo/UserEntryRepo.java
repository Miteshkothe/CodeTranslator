package com.MK.Code_Translator.Repo;

import com.MK.Code_Translator.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserEntryRepo extends MongoRepository<User, ObjectId> {
    User findByUserName(String userName);
}
