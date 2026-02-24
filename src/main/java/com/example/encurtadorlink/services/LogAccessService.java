package com.example.encurtadorlink.services;

import com.example.encurtadorlink.dto.AccessContextDTO;
import com.example.encurtadorlink.model.Link;
import com.example.encurtadorlink.model.LogAccess;
import com.example.encurtadorlink.repositories.LogAccessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogAccessService {

    private static final Logger logger = LoggerFactory.getLogger(LogAccessService.class);

    private final LogAccessRepository logAccessRepository;

    public LogAccessService(LogAccessRepository logAccessRepository){
        this.logAccessRepository = logAccessRepository;
    }

    public void registerLogAccess(Link link, AccessContextDTO contextDTO) {
        LogAccess logAccess = LogAccess.builder()
                .accessDate(LocalDateTime.now())
                .link(link)
                .userIp(contextDTO.clientIp())
                .userAgent(contextDTO.userAgent())
                .referrer(contextDTO.referrer())
                .build();

        saveLogAccess(logAccess);
    }

    private void saveLogAccess(LogAccess logAccess){
        logAccessRepository.save(logAccess);
    }
}
