package com.aliasforwarder.service;

import com.aliasforwarder.dto.AliasDto;
import com.aliasforwarder.dto.SyncPreviewResponse;
import com.aliasforwarder.dto.SyncRequest;
import com.aliasforwarder.model.AliasCache;
import com.aliasforwarder.repository.AliasCacheRepository;
import com.aliasforwarder.repository.SyncJobRepository;
import com.aliasforwarder.repository.SyncLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    @Mock
    private AddyIoService addyIoService;

    @Mock
    private MxrouteService mxrouteService;

    @Mock
    private SyncJobRepository syncJobRepository;

    @Mock
    private AliasCacheRepository aliasCacheRepository;

    @Mock
    private SyncLogRepository syncLogRepository;

    private SyncService syncService;

    @BeforeEach
    void setUp() {
        syncService = new SyncService(
                addyIoService, mxrouteService,
                syncJobRepository, aliasCacheRepository, syncLogRepository);
    }

    @Test
    void preview_fetchesAliasesAndReturnsResponse() {
        List<AliasDto> aliases = List.of(
                new AliasDto("test@example.com", "Test alias", true),
                new AliasDto("shop@example.com", "Shopping", true)
        );

        when(addyIoService.fetchAliasesByDomain("test-key", "example.com"))
                .thenReturn(aliases);

        SyncRequest request = new SyncRequest();
        request.setAddyApiKey("test-key");
        request.setMxrouteApiKey("mx-key");
        request.setOriginDomain("example.com");
        request.setTargetDomain("mail.example.com");
        request.setTargetEmail("inbox@mail.example.com");

        SyncPreviewResponse response = syncService.preview(request, "user-1");

        assertEquals(2, response.getTotalAliases());
        assertEquals("example.com", response.getOriginDomain());
        assertEquals("mail.example.com", response.getTargetDomain());
        assertEquals(2, response.getAliases().size());
        assertEquals("test@example.com", response.getAliases().get(0).getEmail());

        verify(addyIoService).fetchAliasesByDomain("test-key", "example.com");
        verify(aliasCacheRepository).deleteByUserIdAndOriginDomain("user-1", "example.com");
        verify(aliasCacheRepository, times(2)).save(any(AliasCache.class));
    }
}
