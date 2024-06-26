package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.RecordingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecordingRepository extends MongoRepository<RecordingEntity, String> {
}
