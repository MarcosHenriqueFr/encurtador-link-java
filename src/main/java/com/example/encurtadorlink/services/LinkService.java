package com.example.encurtadorlink.services;

import com.example.encurtadorlink.config.exception.ForbiddenException;
import com.example.encurtadorlink.config.exception.ShortURLAlreadyExistsException;
import com.example.encurtadorlink.config.exception.ShortURLNotFoundException;
import com.example.encurtadorlink.dto.AccessContextDTO;
import com.example.encurtadorlink.dto.LinkCreateDTO;
import com.example.encurtadorlink.dto.LinkResponseDTO;
import com.example.encurtadorlink.dto.LogResponseDTO;
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
    private final LogAccessService logAccessService;
    private final LinkRepository linkRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public LinkService(LinkMapper linkMapper, LinkRepository linkRepository, UserService userService, ShortCodeGenerator shortCodeGenerator, LogAccessService logAccessService){
        this.linkMapper = linkMapper;
        this.userService = userService;
        this.linkRepository = linkRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.logAccessService = logAccessService;
    }

    private boolean isShortCodeAvailable(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode).orElse(null);
        return link == null;
    }

    /**
     * <p>
     *     Essa função é responsável por agrupar todas as informações para o encurtamento do link,
     *     como: a assimilação de seu código, contexto de usuário, data de criação e qtde. de clicks.
     * </p>
     * @param dto agrupa todas as informações para a criação do link
     * @param email é o subject obtido pelo token JWT
     * @return LinkResponseDTO - Evita vazar as informações importantes do usuário
     */
    public LinkResponseDTO shortenLink(LinkCreateDTO dto, String email) {
        Link link = convertBaseLink(dto);

        assignShortCode(link);

        applyDefaults(link);

        attachUserContext(email, link);

        saveLink(link);

        return linkMapper.fromEntity(link);
    }

    private Link convertBaseLink(LinkCreateDTO dto){
        return linkMapper.toEntity(dto);
    }

    private void assignShortCode(Link link){
        String randomShortCode = shortCodeGenerator.generate();
        if (!isShortCodeAvailable(randomShortCode)){
            throw new ShortURLAlreadyExistsException("This short URI is not available.");
        }

        link.setShortCode(randomShortCode);
    }

    private void applyDefaults(Link link){
        link.setActive(true);
        link.setQtClicks(0);
        link.setCreationDate(LocalDateTime.now());
    }

    private void attachUserContext(String email, Link link){
        // SEMPRE tratar erro de email nulo para permitir usuário anônimo
        if(email != null){
            attachAuthenticatedUser(email, link);
        } else {
            attachAnonymousUser(link);
        }
    }

    private void attachAnonymousUser(Link link) {
        link.setUser(null);
        logger.info("Anonymous user created short code {}", link.getShortCode());
    }

    private void attachAuthenticatedUser(String email, Link link) {
        User user = userService.getUserByEmail(email).getUser();
        link.setUser(user);

        logger.info("User {} created short code {}", user.getName(), link.getShortCode());
    }

    /**
     * <p>Usado unicamente no controller de redirect para direcionar o usuário à página original</p>
     * @param shortCode vindo do path URI da requisição
     * @return A url original registrada no banco de dados
     */
    @Transactional
    public String resolveShortCode(String shortCode, AccessContextDTO contextDTO){
        // Evitar o orElse(null)
        Link link = linkRepository.findByShortCode(shortCode).orElseThrow(
                () -> new ShortURLNotFoundException("This URI could not be resolved.")
        );

        updateChanges(link);

        logAccessService.registerLogAccess(link, contextDTO);

        return link.getOriginalUrl();
    }

    private void updateChanges(Link link){
        int qtFinalClicks = link.getQtClicks() + 1;
        link.setQtClicks(qtFinalClicks);
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

    /**
     * <p>
     *     Essa função usa o @Transactional para tratar facilmente da bidirecionalidade sem necessitar
     *     do LinkRepository, tratando diretamente da lista de links do próprio usuário.
     * </p>
     * @param email
     * @param shortCode
     */
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

    public List<LogResponseDTO> getLinkInformation(String email, String shortCode) {
        User user = userService.getUserByEmail(email).getUser();
        Link link = getOwnedLink(shortCode, user);

        return logAccessService.formatLogs(link.getId());
    }

    /**
     * <p>
     *     Essa função é essencial para permitir a modificação do link somente por aquele quem o criou.
     *     Deve ser sempre usado para qualquer manipulação de um único link.
     * </p>
     * @param shortCode
     * @param user
     * @return o link para o seu criador
     */
    private Link getOwnedLink(String shortCode, User user){
        Link link =  linkRepository.findByShortCode(shortCode).orElseThrow(
                () -> new ShortURLNotFoundException("This URI could not be resolved")
        );

        validateOwnership(link, user);

        return link;
    }

    private void validateOwnership(Link link, User user){
        boolean sameUser = link.getUser().getId().equals(user.getId());

        if(!sameUser){
            throw new ForbiddenException("User does not own this resource.");
        }
    }
}
