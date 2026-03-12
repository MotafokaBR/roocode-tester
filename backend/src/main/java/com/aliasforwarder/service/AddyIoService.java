package com.aliasforwarder.service;

import com.aliasforwarder.dto.AliasDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Client for the Addy.io API. Fetches aliases with pagination and
 * filters them client-side by origin domain.
 */
@Service
public class AddyIoService {

    private static final Logger log = LoggerFactory.getLogger(AddyIoService.class);
    private static final String ADDY_BASE_URL = "https://app.addy.io";
    private static final int PAGE_SIZE = 100;
    private static final int MAX_RATE_LIMIT_RETRIES = 5;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final ObjectProvider<WebClient.Builder> webClientBuilderProvider;

    public AddyIoService(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        this.webClientBuilderProvider = webClientBuilderProvider;
    }

    /**
     * Fetches all aliases from Addy.io and filters by the given origin domain.
     */
    public List<AliasDto> fetchAliasesByDomain(String apiKey, String originDomain) {
        WebClient client = webClientBuilderProvider.getObject()
                .baseUrl(ADDY_BASE_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();

        List<AliasDto> allAliases = new ArrayList<>();
        int page = 1;
        int rateLimitRetries = 0;
        boolean hasMore = true;

        while (hasMore) {
            try {
                log.info("Fetching Addy.io aliases page {}", page);

                final int currentPage = page;

                @SuppressWarnings("unchecked")
                Map<String, Object> response = client.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/v1/aliases")
                                .queryParam("page[number]", currentPage)
                                .queryParam("page[size]", PAGE_SIZE)
                                .build())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block(REQUEST_TIMEOUT);

                // Reset retry counter on successful request
                rateLimitRetries = 0;

                if (response == null || !response.containsKey("data")) {
                    log.warn("Empty or invalid response from Addy.io on page {}", page);
                    break;
                }

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

                if (data == null || data.isEmpty()) {
                    hasMore = false;
                    continue;
                }

                for (Map<String, Object> alias : data) {
                    String email = (String) alias.get("email");
                    if (email != null && email.endsWith("@" + originDomain)) {
                        String description = (String) alias.get("description");
                        Boolean active = (Boolean) alias.get("active");
                        allAliases.add(new AliasDto(email, description, active != null && active));
                    }
                }

                // Check if there are more pages
                @SuppressWarnings("unchecked")
                Map<String, Object> meta = (Map<String, Object>) response.get("meta");
                if (meta != null) {
                    Object lastPage = meta.get("last_page");
                    Object metaCurrentPage = meta.get("current_page");
                    if (lastPage != null && metaCurrentPage != null) {
                        int last = ((Number) lastPage).intValue();
                        int current = ((Number) metaCurrentPage).intValue();
                        hasMore = current < last;
                    } else {
                        hasMore = data.size() == PAGE_SIZE;
                    }
                } else {
                    hasMore = data.size() == PAGE_SIZE;
                }

                page++;

            } catch (WebClientResponseException e) {
                if (e.getStatusCode().value() == 429) {
                    rateLimitRetries++;
                    if (rateLimitRetries > MAX_RATE_LIMIT_RETRIES) {
                        throw new RuntimeException(
                                "Addy.io rate limit exceeded after " + MAX_RATE_LIMIT_RETRIES + " retries", e);
                    }
                    log.warn("Rate limited by Addy.io, waiting before retry (attempt {}/{})...",
                            rateLimitRetries, MAX_RATE_LIMIT_RETRIES);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while waiting for rate limit", ie);
                    }
                    // Retry the same page
                } else {
                    throw new RuntimeException("Addy.io API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
                }
            }
        }

        log.info("Fetched {} aliases from Addy.io for domain {}", allAliases.size(), originDomain);
        return allAliases;
    }
}
