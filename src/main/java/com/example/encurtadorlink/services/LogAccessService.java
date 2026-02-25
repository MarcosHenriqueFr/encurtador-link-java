package com.example.encurtadorlink.services;

import com.example.encurtadorlink.dto.AccessContextDTO;
import com.example.encurtadorlink.dto.LogResponseDTO;
import com.example.encurtadorlink.mapper.LogAccessMapper;
import com.example.encurtadorlink.model.Link;
import com.example.encurtadorlink.model.LogAccess;
import com.example.encurtadorlink.repositories.LogAccessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogAccessService {

    private static final Logger logger = LoggerFactory.getLogger(LogAccessService.class);

    private final LogAccessRepository logAccessRepository;
    private final LogAccessMapper logAccessMapper;

    public LogAccessService(LogAccessRepository logAccessRepository, LogAccessMapper logAccessMapper){
        this.logAccessRepository = logAccessRepository;
        this.logAccessMapper = logAccessMapper;
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
        logger.info("The LOG ACCESS ID {} was saved.", logAccess.getId());
    }

    public List<LogResponseDTO> formatLogs(Long linkId) {

        List<LogAccess> logs = getLogsByLinkId(linkId);

        return logs.stream()
                .map(logAccessMapper::fromEntity)
                .toList();
    }

    private List<LogAccess> getLogsByLinkId(Long id){
        return logAccessRepository.findByLinkId(id);
    }
}
