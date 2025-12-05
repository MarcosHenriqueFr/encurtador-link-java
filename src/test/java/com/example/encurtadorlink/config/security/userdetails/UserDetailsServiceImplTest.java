package com.example.encurtadorlink.config.security.userdetails;

import com.example.encurtadorlink.fixtures.UserFixture;
import com.example.encurtadorlink.model.RoleName;
import com.example.encurtadorlink.model.User;
import com.example.encurtadorlink.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private AutoCloseable closeable;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setup(){
        closeable = MockitoAnnotations.openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @AfterEach
    void closeMocks() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Should load user properly.")
    void loadUserByUsernameSuccess(){
        String email = "emailvalido@email.com";

        User user = UserFixture.createUserFix().toBuilder()
                .password("Crip21$10$")
                .email(email)
                .build();

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals("Crip21$10$", result.getPassword());
        assertTrue(result.isEnabled());
        verify(userRepository, times(1)).findUserByEmail(email);
    }

    @Test
    @DisplayName("Should throw an Exception if user not found in database.")
    void loadUserByUsernameException(){
        String email = "emailvalido@email.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );

        assertEquals("User not found.", exception.getMessage());

        verify(userRepository, times(1)).findUserByEmail(email);
    }

    @Test
    @DisplayName("Should return correct authorities based on the role")
    void loadUserByUsernameAuthorities(){
        String email = "emailvalido@email.com";

        User user = UserFixture.createUserFix().toBuilder()
                .role(RoleName.ADMINISTRATOR)
                .email(email)
                .build();

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername(email);

        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATOR"))
        );
    }
}