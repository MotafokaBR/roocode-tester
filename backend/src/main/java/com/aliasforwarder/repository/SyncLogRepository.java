package com.aliasforwarder.repository;

import com.aliasforwarder.model.SyncLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncLogRepository extends MongoRepository<SyncLog, String> {
    List<SyncLog> findBySyncJobId(String syncJobId);
}
