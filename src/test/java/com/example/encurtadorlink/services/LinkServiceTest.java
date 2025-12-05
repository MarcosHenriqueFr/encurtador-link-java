package com.example.encurtadorlink.services;

import com.example.encurtadorlink.config.exception.ShortURLAlreadyExistsException;
import com.example.encurtadorlink.config.exception.ShortURLNotFoundException;
import com.example.encurtadorlink.config.security.userdetails.UserDetailsImpl;
import com.example.encurtadorlink.dto.LinkCreateDTO;
import com.example.encurtadorlink.dto.LinkResponseDTO;
import com.example.encurtadorlink.dto.UserResponseDTO;
import com.example.encurtadorlink.fixtures.LinkFixture;
import com.example.encurtadorlink.fixtures.UserFixture;
import com.example.encurtadorlink.mapper.LinkMapper;
import com.example.encurtadorlink.model.Link;
import com.example.encurtadorlink.model.User;
import com.example.encurtadorlink.repositories.LinkRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private LinkMapper linkMapper;

    @Mock
    private UserService userService;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    private AutoCloseable closeable;

    private LinkService linkService;

    // When injected by constructor
    @BeforeEach
    void setup(){
        closeable = MockitoAnnotations.openMocks(this);
        linkService = new LinkService(linkMapper, linkRepository, userService, shortCodeGenerator);
    }

    @AfterEach
    void closeMocks() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Should get the original url from the given short code")
    void resolveShortCodeSuccess() {
        Link link = Link.builder()
                .id(1L)
                .user(null)
                .log(null)
                .active(true)
                .creationDate(LocalDateTime.now())
                .qtClicks(0)
                .shortCode("abcGJ90")
                .originalUrl("fast.com")
                .build();


        when(linkRepository.findByShortCode("abcGJ90")).thenReturn(Optional.of(link));

        String result = linkService.resolveShortCode("abcGJ90");

        verify(linkRepository, times(1)).findByShortCode(any());
        assertEquals(1, link.getQtClicks());
        assertEquals("fast.com", result);
    }

    @Test
    @DisplayName("Should throw an exception when the shortcode isn't in database")
    void resolveShortCodeException(){
        when(linkRepository.findByShortCode(any())).thenReturn(Optional.empty());

        ShortURLNotFoundException thrown = Assertions.assertThrows(ShortURLNotFoundException.class, () -> {
            linkService.resolveShortCode(any());
        });

        assertEquals("This URI could not be resolved.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should return valid links created by the user")
    void showLinksPerUserCase1(){
        User user = UserFixture.createUserFix();

        List<Link> links = List.of(
            LinkFixture.createLinkFix().toBuilder().id(1L).shortCode("1111").user(user).build(),
            LinkFixture.createLinkFix().toBuilder().id(2L).shortCode("2222").user(user).build(),
            LinkFixture.createLinkFix().toBuilder().id(3L).shortCode("3333").user(user).build()
        );

        user.setLinks(links);

        UserResponseDTO userDTO = new UserResponseDTO(user.getId(), user.getName());

        List<LinkResponseDTO> linksDto = List.of(
                new LinkResponseDTO(1L, "teste.com", "1111", userDTO, LocalDateTime.now()),
                new LinkResponseDTO(2L, "teste.com", "2222", userDTO, LocalDateTime.now()),
                new LinkResponseDTO(3L, "teste.com", "3333", userDTO, LocalDateTime.now())
        );

        when(userService.showLinksPerUser(any())).thenReturn(user);
        when(linkMapper.fromEntity(links.get(0))).thenReturn(linksDto.get(0));
        when(linkMapper.fromEntity(links.get(1))).thenReturn(linksDto.get(1));
        when(linkMapper.fromEntity(links.get(2))).thenReturn(linksDto.get(2));

        List<LinkResponseDTO> result = linkService.showLinksPerUser("emailvalido@email.com");
        assertEquals(linksDto, result);
    }

    @Test
    @DisplayName("Should return empty list of links from the user")
    void showLinksPerUserCase2(){
        User user = UserFixture.createUserFix();
        user.setLinks(Collections.emptyList());

        when(userService.showLinksPerUser(any())).thenReturn(user);

        List<LinkResponseDTO> result = linkService.showLinksPerUser("emailvalido@email.com");

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    @DisplayName("Should return shortened link from given URI")
    void shortenLinkSuccess(){
        LinkCreateDTO linkCreateDTO = new LinkCreateDTO("teste.com");
        String email = "emailvalido@email.com";
        String expectedShortCode = "AB123C";

        User user = UserFixture.createUserFix();
        UserResponseDTO userDTO = new UserResponseDTO(user.getId(), user.getName());

        Link link = Link.builder()
                        .originalUrl(linkCreateDTO.originalUrl())
                        .build();

        when(linkMapper.toEntity(linkCreateDTO)).thenReturn(link);
        when(shortCodeGenerator.generate()).thenReturn(expectedShortCode);
        when(linkRepository.findByShortCode(any())).thenReturn(Optional.empty());
        when(userService.getUserByEmail(email)).thenReturn(new UserDetailsImpl(user));
        when(linkRepository.save(any(Link.class))).thenAnswer(i -> {
            Link linkToSave = (Link) i.getArguments()[0];
            linkToSave.setId(1L);
            return linkToSave;
        });
        when(linkMapper.fromEntity(any(Link.class))).thenAnswer(
                inv -> {
                    Link saved = (Link) inv.getArguments()[0];
                    return new LinkResponseDTO(
                            saved.getId(),
                            saved.getOriginalUrl(),
                            saved.getShortCode(),
                            userDTO,
                            saved.getCreationDate()
                    );
                }
        );

        LinkResponseDTO result = linkService.shortenLink(linkCreateDTO, email);

        assertEquals(1L, result.id());
        assertEquals(expectedShortCode, result.shortCode());
        assertEquals("teste.com", result.originalUrl());
        assertEquals(userDTO.id(), result.user().id());
        assertNotNull(result.creationDate());
    }

    @Test
    @DisplayName("Should throw an exception if shortcode already exists")
    void shortenLinkException(){
        LinkCreateDTO linkDTO = new LinkCreateDTO("teste.com");
        String email = "emailvalido@email.com";
        String existingShortCode = "TUH123";

        Link link = LinkFixture.createLinkFix().toBuilder()
                        .id(2L)
                        .shortCode(existingShortCode)
                        .originalUrl("other.com")
                        .build();

        // Sempre ver se o objeto convertido tem os mesmos valores
        when(linkMapper.toEntity(linkDTO)).thenReturn(
                LinkFixture.createLinkFix().toBuilder()
                        .originalUrl("teste.com")
                        .build()
        );
        when(shortCodeGenerator.generate()).thenReturn(existingShortCode);
        when(linkRepository.findByShortCode(existingShortCode)).thenReturn(Optional.of(link));

        ShortURLAlreadyExistsException exception = assertThrows(
                ShortURLAlreadyExistsException.class,
                () -> linkService.shortenLink(linkDTO, email)
        );

        assertEquals("This short URI is not available.", exception.getMessage());
    }
}