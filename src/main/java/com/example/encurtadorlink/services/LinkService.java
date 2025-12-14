package com.example.encurtadorlink.services;

import com.example.encurtadorlink.config.exception.ShortURLAlreadyExistsException;
import com.example.encurtadorlink.config.exception.ShortURLNotFoundException;
import com.example.encurtadorlink.dto.LinkCreateDTO;
import com.example.encurtadorlink.dto.LinkResponseDTO;
import com.example.encurtadorlink.mapper.LinkMapper;
import com.example.encurtadorlink.model.Link;
import com.example.encurtadorlink.model.User;
import com.example.encurtadorlink.repositories.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LinkService {

    private static final Logger logger = LoggerFactory.getLogger(LinkService.class);

    private final LinkMapper linkMapper;
    private final UserService userService;
    private final LinkRepository linkRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public LinkService(LinkMapper linkMapper, LinkRepository linkRepository, UserService userService, ShortCodeGenerator shortCodeGenerator){
        this.linkMapper = linkMapper;
        this.userService = userService;
        this.linkRepository = linkRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    private boolean isShortCodeAvailable(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode).orElse(null);
        return link == null;
    }

    public LinkResponseDTO shortenLink(LinkCreateDTO dto, String email) {
        Link link = linkMapper.toEntity(dto);

        String randomShortCode = shortCodeGenerator.generate();
        if (!isShortCodeAvailable(randomShortCode)){
            throw new ShortURLAlreadyExistsException("This short URI is not available.");
        }

        link.setShortCode(randomShortCode);
        link.setActive(true);
        link.setQtClicks(0);

        User user = userService.getUserByEmail(email).getUser();
        link.setUser(user);

        link.setCreationDate(LocalDateTime.now());

        saveLink(link);

        logger.info("The user {} has the current short code: {}", user.getName(), link.getShortCode());
        return linkMapper.fromEntity(link);
    }

    /**
     * <p>Usado unicamente no controller de redirect para direcionar o usuário à página original</p>
     * @param shortCode vindo do path URI da requisição
     * @return A url original registrada no banco de dados
     */
    public String resolveShortCode(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode).orElse(null);

        if (link == null){
            throw new ShortURLNotFoundException("This URI could not be resolved.");
        }

        int qtFinalClicks = link.getQtClicks() + 1;
        link.setQtClicks(qtFinalClicks);

        saveLink(link);

        return link.getOriginalUrl();
    }

    private void saveLink(Link link){
        linkRepository.save(link);
        logger.info("The shortened link {} was created successfully.", link.getShortCode());
    }

    public List<LinkResponseDTO> showLinksPerUser(String subject){
        User user = userService.showLinksPerUser(subject);
        List<Link> links = user.getLinks();

        return links.stream()
                .map(linkMapper::fromEntity)
                .toList();
    }

    // Usando o ORM
    @Transactional
    public void deleteShortLink(String email, String shortCode) {
        User user = userService.getUserByEmail(email).getUser();
        List<Link> links = user.getLinks();

        Link toBeDeleted = links.stream()
                .filter(link -> shortCode.equals(link.getShortCode()))
                .findFirst()
                .orElseThrow(() -> new ShortURLNotFoundException("This short code does not belong to this user."));

        links.remove(toBeDeleted);

        logger.info("The shortened link {} was excluded.", shortCode);
    }
}
