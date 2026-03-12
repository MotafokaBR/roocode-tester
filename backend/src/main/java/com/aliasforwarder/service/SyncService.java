package com.aliasforwarder.service;

import com.aliasforwarder.dto.AliasDto;
import com.aliasforwarder.dto.SyncJobResponse;
import com.aliasforwarder.dto.SyncPreviewResponse;
import com.aliasforwarder.dto.SyncRequest;
import com.aliasforwarder.model.AliasCache;
import com.aliasforwarder.model.SyncJob;
import com.aliasforwarder.model.SyncLog;
import com.aliasforwarder.repository.AliasCacheRepository;
import com.aliasforwarder.repository.SyncJobRepository;
import com.aliasforwarder.repository.SyncLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Orchestrates the sync operation between Addy.io and MXroute.
 * Supports preview (dry run) and execute (actual forwarder creation) modes.
 */
@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final AddyIoService addyIoService;
    private final MxrouteService mxrouteService;
    private final SyncJobRepository syncJobRepository;
    private final AliasCacheRepository aliasCacheRepository;
    private final SyncLogRepository syncLogRepository;

    public SyncService(AddyIoService addyIoService,
                       MxrouteService mxrouteService,
                       SyncJobRepository syncJobRepository,
                       AliasCacheRepository aliasCacheRepository,
                       SyncLogRepository syncLogRepository) {
        this.addyIoService = addyIoService;
        this.mxrouteService = mxrouteService;
        this.syncJobRepository = syncJobRepository;
        this.aliasCacheRepository = aliasCacheRepository;
        this.syncLogRepository = syncLogRepository;
    }

    /**
     * Preview mode: fetches aliases from Addy.io and returns the list
     * without creating any forwarders.
     */
    public SyncPreviewResponse preview(SyncRequest request, String userId) {
        log.info("Running sync preview for user {} domain {}", userId, request.getOriginDomain());

        List<AliasDto> aliases = addyIoService.fetchAliasesByDomain(
                request.getAddyApiKey(), request.getOriginDomain());

        // Cache the aliases
        cacheAliases(userId, request.getOriginDomain(), aliases);

        SyncPreviewResponse response = new SyncPreviewResponse();
        response.setOriginDomain(request.getOriginDomain());
        response.setTargetDomain(request.getTargetDomain());
        response.setTargetEmail(request.getTargetEmail());
        response.setTotalAliases(aliases.size());
        response.setAliases(aliases);

        return response;
    }

    /**
     * Execute mode: fetches aliases from Addy.io, creates forwarders on MXroute,
     * and logs results per alias in MongoDB.
     */
    public SyncJobResponse execute(SyncRequest request, String userId) {
        log.info("Running sync execute for user {} domain {}", userId, request.getOriginDomain());

        // Create the sync job record
        SyncJob job = new SyncJob();
        job.setUserId(userId);
        job.setOriginDomain(request.getOriginDomain());
        job.setTargetDomain(request.getTargetDomain());
        job.setTargetEmail(request.getTargetEmail());
        job.setStatus(SyncJob.SyncStatus.RUNNING);
        job.setStartedAt(Instant.now());
        job = syncJobRepository.save(job);

        try {
            // Fetch aliases from Addy.io
            List<AliasDto> aliases = addyIoService.fetchAliasesByDomain(
                    request.getAddyApiKey(), request.getOriginDomain());

            job.setAliasCount(aliases.size());
            cacheAliases(userId, request.getOriginDomain(), aliases);

            // Fetch existing forwarders once for the domain to avoid N API calls
            Set<String> existingForwarders = mxrouteService.fetchExistingForwarderSources(
                    request.getMxrouteApiKey(), request.getTargetDomain());

            int created = 0;
            int skipped = 0;
            int failed = 0;

            // Process each alias
            for (AliasDto alias : aliases) {
                SyncLog syncLog = new SyncLog();
                syncLog.setSyncJobId(job.getId());
                syncLog.setAliasEmail(alias.getEmail());
                syncLog.setTimestamp(Instant.now());

                try {
                    // Check if forwarder already exists (using pre-fetched set)
                    if (existingForwarders.contains(alias.getEmail())) {
                        syncLog.setStatus("SKIPPED");
                        syncLog.setForwarderCreated(false);
                        skipped++;
                        log.info("Forwarder already exists for {}, skipping", alias.getEmail());
                    } else {
                        // Create forwarder
                        boolean success = mxrouteService.createForwarder(
                                request.getMxrouteApiKey(),
                                alias.getEmail(),
                                request.getTargetEmail(),
                                request.getTargetDomain());

                        if (success) {
                            syncLog.setStatus("CREATED");
                            syncLog.setForwarderCreated(true);
                            created++;
                        } else {
                            syncLog.setStatus("FAILED");
                            syncLog.setForwarderCreated(false);
                            syncLog.setErrorMessage("MXroute API returned failure");
                            failed++;
                        }
                    }
                } catch (Exception e) {
                    syncLog.setStatus("FAILED");
                    syncLog.setForwarderCreated(false);
                    syncLog.setErrorMessage(e.getMessage());
                    failed++;
                    log.error("Error processing alias {}: {}", alias.getEmail(), e.getMessage());
                }

                syncLogRepository.save(syncLog);
            }

            // Update job with results
            job.setCreatedCount(created);
            job.setSkippedCount(skipped);
            job.setFailedCount(failed);
            job.setStatus(SyncJob.SyncStatus.COMPLETED);
            job.setCompletedAt(Instant.now());
            job = syncJobRepository.save(job);

            log.info("Sync completed: {} created, {} skipped, {} failed", created, skipped, failed);

        } catch (Exception e) {
            job.setStatus(SyncJob.SyncStatus.FAILED);
            job.setCompletedAt(Instant.now());
            syncJobRepository.save(job);
            log.error("Sync job failed: {}", e.getMessage(), e);
            throw new RuntimeException("Sync failed: " + e.getMessage(), e);
        }

        SyncJobResponse response = SyncJobResponse.fromJob(job);
        response.setLogs(syncLogRepository.findBySyncJobId(job.getId()));
        return response;
    }

    /**
     * Returns all sync jobs for the given user, most recent first.
     */
    public List<SyncJobResponse> getJobs(String userId) {
        return syncJobRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(SyncJobResponse::fromJob)
                .toList();
    }

    /**
     * Returns a specific sync job with its detailed logs.
     */
    public SyncJobResponse getJobDetail(String jobId, String userId) {
        SyncJob job = syncJobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sync job not found: " + jobId));

        if (!job.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to view this sync job");
        }

        SyncJobResponse response = SyncJobResponse.fromJob(job);
        response.setLogs(syncLogRepository.findBySyncJobId(jobId));
        return response;
    }

    private void cacheAliases(String userId, String originDomain, List<AliasDto> aliases) {
        aliasCacheRepository.deleteByUserIdAndOriginDomain(userId, originDomain);

        for (AliasDto alias : aliases) {
            AliasCache cache = new AliasCache();
            cache.setUserId(userId);
            cache.setOriginDomain(originDomain);
            cache.setAliasEmail(alias.getEmail());
            cache.setDescription(alias.getDescription());
            cache.setActive(alias.isActive());
            cache.setLastSeenAt(Instant.now());
            aliasCacheRepository.save(cache);
        }
    }
}
