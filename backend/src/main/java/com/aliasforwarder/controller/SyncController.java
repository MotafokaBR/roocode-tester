package com.aliasforwarder.controller;

import com.aliasforwarder.dto.SyncJobResponse;
import com.aliasforwarder.dto.SyncPreviewResponse;
import com.aliasforwarder.dto.SyncRequest;
import com.aliasforwarder.service.SyncService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/preview")
    public ResponseEntity<SyncPreviewResponse> preview(
            @Valid @RequestBody SyncRequest request,
            @AuthenticationPrincipal OAuth2User principal) {

        String userId = getUserId(principal);
        SyncPreviewResponse response = syncService.preview(request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/execute")
    public ResponseEntity<SyncJobResponse> execute(
            @Valid @RequestBody SyncRequest request,
            @AuthenticationPrincipal OAuth2User principal) {

        String userId = getUserId(principal);
        SyncJobResponse response = syncService.execute(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<SyncJobResponse>> listJobs(
            @AuthenticationPrincipal OAuth2User principal) {

        String userId = getUserId(principal);
        List<SyncJobResponse> jobs = syncService.getJobs(userId);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<SyncJobResponse> getJobDetail(
            @PathVariable String id,
            @AuthenticationPrincipal OAuth2User principal) {

        String userId = getUserId(principal);
        SyncJobResponse job = syncService.getJobDetail(id, userId);
        return ResponseEntity.ok(job);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleStatusError(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("error", e.getReason() != null ? e.getReason() : "Unknown error"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    private String getUserId(OAuth2User principal) {
        if (principal == null) {
            throw new RuntimeException("Authentication required");
        }
        Object sub = principal.getAttribute("sub");
        return sub != null ? sub.toString() : principal.getName();
    }
}
