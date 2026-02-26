package com.example.encurtadorlink.services;

import com.example.encurtadorlink.dto.AccessContextDTO;
import com.example.encurtadorlink.dto.LogResponseDTO;
import com.example.encurtadorlink.fixtures.LinkFixture;
import com.example.encurtadorlink.fixtures.LogAccessFixture;
import com.example.encurtadorlink.mapper.LogAccessMapper;
import com.example.encurtadorlink.model.Link;
import com.example.encurtadorlink.model.LogAccess;
import com.example.encurtadorlink.repositories.LogAccessRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

class LogAccessServiceTest {

    @Mock
    private LogAccessRepository logAccessRepository;

    @Mock
    private LogAccessMapper logAccessMapper;

    private AutoCloseable closeable;
    private LogAccessService logAccessService;

    @BeforeEach
    void setUp(){
        closeable = MockitoAnnotations.openMocks(this);
        logAccessService = new LogAccessService(logAccessRepository, logAccessMapper);
    }

    @AfterEach
    void closeMocks() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Should use data from LogContext and save them correctly.")
    void registerLogAccessCorrectly() {
        Link link = LinkFixture.createLinkFix();

        AccessContextDTO dto = new AccessContextDTO(
                "127.0.0.1",
                "browser",
                "ref"
        );

        // Simular o salvamento no banco de dados
        when(logAccessRepository.save(any(LogAccess.class))).thenAnswer(i -> {
            LogAccess saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        logAccessService.registerLogAccess(link, dto);

        verify(logAccessRepository).save(
                argThat(
                    lg ->
                            lg.getLink().equals(link) &&
                            lg.getReferrer().equals(dto.referrer()) &&
                            lg.getUserAgent().equals(dto.userAgent()) &&
                            lg.getUserIp().equals(dto.clientIp()) &&
                            lg.getAccessDate() != null
                )
        );
    }

    @Test
    @DisplayName("Should format the list of log access objects to LogResponseDTO")
    void formatLogsCorrectly(){
        Long linkId = 1L;
        LogAccess log = LogAccessFixture.createLogFix();

        LogResponseDTO dtoMapped = new LogResponseDTO(
                log.getId(),
                log.getAccessDate(),
                log.getUserAgent(),
                log.getReferrer()
        );

        when(logAccessRepository.findByLinkId(1L)).thenReturn(List.of(log));
        when(logAccessMapper.fromEntity(log)).thenReturn(dtoMapped);

        List<LogResponseDTO> result = logAccessService.formatLogs(linkId);

        assertEquals(List.of(dtoMapped), result);
        verify(logAccessMapper).fromEntity(log);
        verify(logAccessRepository).findByLinkId(linkId);
    }

    @Test
    @DisplayName("Should return empty list when no logs are found")
    void formatLogsEmptyList() {
        Long linkId = 1L;

        when(logAccessRepository.findByLinkId(linkId)).thenReturn(List.of());

        List<LogResponseDTO> result = logAccessService.formatLogs(linkId);

        assertTrue(result.isEmpty());
        verify(logAccessRepository).findByLinkId(linkId);
    }
}