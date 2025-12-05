package com.example.encurtadorlink.config.security.auth;


import com.example.encurtadorlink.model.RoleName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    private AutoCloseable closeable;

    private JwtService jwtService;

    @BeforeEach
    void setup(){
        closeable = MockitoAnnotations.openMocks(this);
        jwtService = new JwtService(jwtEncoder);

        // Precisam ser injetados de forma manual
        ReflectionTestUtils.setField(jwtService, "ISSUER", "test-issuer");
    }

    @AfterEach
    void closeMocks() throws Exception{
        closeable.close();
    }

    @Test
    @DisplayName("Should create the generated token from authentication")
    void generateTokenSuccessfully(){
        Authentication authentication = createAuthentication("emailvalido@email.com", RoleName.BASIC.name());
        String expectedToken = "abcEfGHiJk.teste";

        Jwt jwtMock = mock(Jwt.class);

        when(jwtMock.getTokenValue()).thenReturn(expectedToken);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwtMock);

        String result = jwtService.generateToken(authentication);

        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    // Método dedicado para a criação do objeto Authentication
    private Authentication createAuthentication(String email, String role){
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        when(authentication.getAuthorities()).thenAnswer(inv -> authorities);

        return authentication;
    }

    // TODO: Outros testes que verificam o claimset
}