package com.example.encurtadorlink.services;

import com.example.encurtadorlink.config.exception.ForbiddenException;
import com.example.encurtadorlink.config.exception.ShortURLAlreadyExistsException;
import com.example.encurtadorlink.config.exception.ShortURLNotFoundException;
import com.example.encurtadorlink.config.security.userdetails.UserDetailsImpl;
import com.example.encurtadorlink.dto.*;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private LogAccessService logAccessService;

    private AutoCloseable closeable;
    private LinkService linkService;

    @BeforeEach
    void setup(){
        closeable = MockitoAnnotations.openMocks(this);
        linkService = new LinkService(
                linkMapper,
                linkRepository,
                userService,
                shortCodeGenerator,
                logAccessService
        );
    }

    @AfterEach
    void closeMocks() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Should resolve shortcode and register access")
    void resolveShortCodeSuccess() {
        Link link = LinkFixture.createLinkFix().toBuilder()
                .qtClicks(0)
                .shortCode("abc123")
                .originalUrl("fast.com")
                .build();

        AccessContextDTO context = new AccessContextDTO("127.0.0.1", "agent", "ref");

        when(linkRepository.findByShortCode("abc123")).thenReturn(Optional.of(link));

        String result = linkService.resolveShortCode("abc123", context);

        assertEquals("fast.com", result);
        assertEquals(1, link.getQtClicks());

        verify(logAccessService).registerLogAccess(link, context);
    }

    @Test
    @DisplayName("Should throw when shortcode not found")
    void resolveShortCodeException(){
        when(linkRepository.findByShortCode(any())).thenReturn(Optional.empty());

        assertThrows(
                ShortURLNotFoundException.class,
                () -> linkService.resolveShortCode("invalid", new AccessContextDTO(null,null,null))
        );
    }

    @Test
    @DisplayName("Should return logs when user owns the link")
    void getLinkInformationSuccess(){
        User user = UserFixture.createUserFix();

        Link link = LinkFixture.createLinkFix().toBuilder()
                .id(1L)
                .shortCode("abc")
                .user(user)
                .build();

        List<LogResponseDTO> logs = List.of(
                new LogResponseDTO(1L, LocalDateTime.now(), "agent", "google")
        );

        when(userService.getUserByEmail(any())).thenReturn(new UserDetailsImpl(user));
        when(linkRepository.findByShortCode("abc")).thenReturn(Optional.of(link));
        when(logAccessService.formatLogs(1L)).thenReturn(logs);

        List<LogResponseDTO> result = linkService.getLinkInformation("email", "abc");

        assertEquals(logs, result);
    }

    @Test
    @DisplayName("Should throw Forbidden when user does not own link")
    void getLinkInformationForbidden(){
        User owner = UserFixture.createUserFix();
        User anotherUser = UserFixture.createUserFix().toBuilder().id(999L).build();

        Link link = LinkFixture.createLinkFix().toBuilder()
                .shortCode("abc")
                .user(owner)
                .build();

        when(userService.getUserByEmail(any())).thenReturn(new UserDetailsImpl(anotherUser));
        when(linkRepository.findByShortCode("abc")).thenReturn(Optional.of(link));

        assertThrows(
                ForbiddenException.class,
                () -> linkService.getLinkInformation("email", "abc")
        );
    }

    @Test
    @DisplayName("Should shorten link successfully")
    void shortenLinkSuccess(){
        LinkCreateDTO dto = new LinkCreateDTO("teste.com");
        String email = "email@email.com";
        String shortCode = "ABC123";

        User user = UserFixture.createUserFix();
        UserResponseDTO userDTO = new UserResponseDTO(user.getId(), user.getName());

        Link link = new Link();
        link.setOriginalUrl("teste.com");

        when(linkMapper.toEntity(dto)).thenReturn(link);
        when(shortCodeGenerator.generate()).thenReturn(shortCode);
        when(linkRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());
        when(userService.getUserByEmail(email)).thenReturn(new UserDetailsImpl(user));
        when(linkMapper.fromEntity(any())).thenAnswer(inv -> {
            Link l = inv.getArgument(0);
            return new LinkResponseDTO(
                    1L,
                    l.getOriginalUrl(),
                    l.getShortCode(),
                    userDTO,
                    l.getCreationDate()
            );
        });

        LinkResponseDTO result = linkService.shortenLink(dto, email);

        assertEquals(shortCode, result.shortCode());
        assertEquals("teste.com", result.originalUrl());
    }

    @Test
    @DisplayName("Should throw when shortcode already exists")
    void shortenLinkException(){
        LinkCreateDTO dto = new LinkCreateDTO("teste.com");

        when(linkMapper.toEntity(dto)).thenReturn(new Link());
        when(shortCodeGenerator.generate()).thenReturn("EXIST");
        when(linkRepository.findByShortCode("EXIST"))
                .thenReturn(Optional.of(new Link()));

        assertThrows(
                ShortURLAlreadyExistsException.class,
                () -> linkService.shortenLink(dto, "email")
        );
    }
}