package com.example.encurtadorlink.services;

import com.example.encurtadorlink.config.exception.ShortLinkNotFoundException;
import com.example.encurtadorlink.dto.LinkCreateDTO;
import com.example.encurtadorlink.dto.LinkResponseDTO;
import com.example.encurtadorlink.mapper.LinkMapper;
import com.example.encurtadorlink.model.Link;
import com.example.encurtadorlink.repositories.LinkRepository;
import com.example.encurtadorlink.util.Base62;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class LinkService {
    private static final SecureRandom random = new SecureRandom();
    private final LinkMapper linkMapper;
    private final LinkRepository linkRepository;

    public LinkService(LinkMapper linkMapper, LinkRepository linkRepository){
        this.linkMapper = linkMapper;
        this.linkRepository = linkRepository;
    }

    private boolean isShortCodeAvailable(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode).orElse(null);
        return link == null;
    }

    public LinkResponseDTO shortenLink(LinkCreateDTO dto) {
        Link link = linkMapper.toEntity(dto);

        // TODO: Futuramente trocar esse tipo de exception
        String randomShortCode = generateRandomShortCode();
        if (!isShortCodeAvailable(randomShortCode)){
            throw new RuntimeException("Esse shortcode já foi gerado!");
        }

        link.setShortCode(randomShortCode);
        link.setActive(true);
        link.setUser(null); //TODO: Mudar depois da mecânica de login de usuário
        link.setCreationDate(LocalDateTime.now());

        saveLink(link);

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
            throw new ShortLinkNotFoundException("This URI could not be resolved.");
        }

        int qtFinalClicks = link.getQtClicks() + 1;
        link.setQtClicks(qtFinalClicks);

        saveLink(link);

        return link.getOriginalUrl();
    }

    // TODO: Implementar o log do sistema aqui
    private void saveLink(Link link){
        linkRepository.save(link);
    }

    private String generateRandomShortCode(){
        long randomNumber = Math.abs(random.nextLong() % 100_000_000_000L);
        return Base62.encode(randomNumber);
    }
}
