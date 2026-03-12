package com.aliasforwarder.repository;

import com.aliasforwarder.model.SyncJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncJobRepository extends MongoRepository<SyncJob, String> {
    List<SyncJob> findByUserIdOrderByStartedAtDesc(String userId);
}
