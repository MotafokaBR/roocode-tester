package com.aliasforwarder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Client for the MXroute API. Creates email forwarders and checks for
 * existing forwarders to ensure idempotent operations.
 */
@Service
public class MxrouteService {

    private static final Logger log = LoggerFactory.getLogger(MxrouteService.class);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final ObjectProvider<WebClient.Builder> webClientBuilderProvider;
    private final String mxrouteApiUrl;

    public MxrouteService(ObjectProvider<WebClient.Builder> webClientBuilderProvider,
                          @Value("${mxroute.api.url:https://mail.mxrouting.net}") String mxrouteApiUrl) {
        this.webClientBuilderProvider = webClientBuilderProvider;
        this.mxrouteApiUrl = mxrouteApiUrl;
    }

    /**
     * Fetches all existing forwarder source addresses for a domain.
     * Use this to check existence in bulk instead of per-alias API calls.
     *
     * @return set of source email addresses that already have forwarders
     */
    public Set<String> fetchExistingForwarderSources(String apiKey, String targetDomain) {
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
                Set<String> sources = new HashSet<>();
                for (Map<String, Object> f : forwarders) {
                    Object source = f.get("source");
                    if (source != null) {
                        sources.add(source.toString());
                    }
                }
                return sources;
            }
            return Collections.emptySet();
        } catch (WebClientResponseException e) {
            log.warn("Error fetching forwarders for domain {}: {}", targetDomain, e.getMessage());
            return Collections.emptySet();
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
        return webClientBuilderProvider.getObject()
                .baseUrl(mxrouteApiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
