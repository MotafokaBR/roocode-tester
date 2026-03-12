package com.aliasforwarder.repository;

import com.aliasforwarder.model.AliasCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AliasCacheRepository extends MongoRepository<AliasCache, String> {
    List<AliasCache> findByUserIdAndOriginDomain(String userId, String originDomain);
    void deleteByUserIdAndOriginDomain(String userId, String originDomain);
}
