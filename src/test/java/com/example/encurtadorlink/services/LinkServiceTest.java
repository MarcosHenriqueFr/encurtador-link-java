package com.example.encurtadorlink.services;

import com.example.encurtadorlink.config.exception.ShortLinkNotFoundException;
import com.example.encurtadorlink.mapper.LinkMapper;
import com.example.encurtadorlink.model.Link;
import com.example.encurtadorlink.repositories.LinkRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private LinkMapper linkMapper;

    private AutoCloseable closeable;

    private LinkService linkService;

    // When injected by constructor
    @BeforeEach
    void setup(){
        closeable = MockitoAnnotations.openMocks(this);
        linkService = new LinkService(linkMapper, linkRepository);
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

        ShortLinkNotFoundException thrown = Assertions.assertThrows(ShortLinkNotFoundException.class, () -> {
            linkService.resolveShortCode(any());
        });

        assertEquals("This URI could not be resolved.", thrown.getMessage());
    }
}