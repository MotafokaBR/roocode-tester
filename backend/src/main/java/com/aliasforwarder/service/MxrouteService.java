package com.aliasforwarder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;

/**
 * Client for the MXroute API. Creates email forwarders and checks for
 * existing forwarders to ensure idempotent operations.
 */
@Service
public class MxrouteService {

    private static final Logger log = LoggerFactory.getLogger(MxrouteService.class);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final WebClient.Builder webClientBuilder;

    public MxrouteService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Checks if a forwarder already exists for the given alias on MXroute.
     *
     * @return true if the forwarder already exists
     */
    public boolean forwarderExists(String apiKey, String aliasEmail, String targetDomain) {
        try {
            WebClient client = buildClient(apiKey);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/domains/{domain}/forwarders")
                            .build(targetDomain))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(REQUEST_TIMEOUT);

            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                var forwarders = (java.util.List<Map<String, Object>>) response.get("data");
                return forwarders.stream()
                        .anyMatch(f -> aliasEmail.equals(f.get("source")));
            }
            return false;
        } catch (WebClientResponseException e) {
            log.warn("Error checking forwarder existence for {}: {}", aliasEmail, e.getMessage());
            return false;
        }
    }

    /**
     * Creates a forwarder on MXroute that forwards mail from aliasEmail to targetEmail.
     *
     * @return true if created successfully, false on failure
     */
    public boolean createForwarder(String apiKey, String aliasEmail, String targetEmail, String targetDomain) {
        try {
            WebClient client = buildClient(apiKey);

            Map<String, String> body = Map.of(
                    "source", aliasEmail,
                    "destination", targetEmail
            );

            client.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/domains/{domain}/forwarders")
                            .build(targetDomain))
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(REQUEST_TIMEOUT);

            log.info("Created forwarder: {} -> {}", aliasEmail, targetEmail);
            return true;

        } catch (WebClientResponseException e) {
            log.error("Failed to create forwarder for {}: {} - {}", aliasEmail,
                    e.getStatusCode(), e.getResponseBodyAsString());
            return false;
        }
    }

    private WebClient buildClient(String apiKey) {
        // MXroute API base URL - configurable via environment variable
        String baseUrl = System.getenv("MXROUTE_API_URL") != null
                ? System.getenv("MXROUTE_API_URL")
                : "https://mail.mxrouting.net";

        return webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
