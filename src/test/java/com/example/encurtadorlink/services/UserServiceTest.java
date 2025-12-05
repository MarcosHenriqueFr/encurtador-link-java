package com.example.encurtadorlink.services;

import com.example.encurtadorlink.config.exception.UserAlreadyExistsException;
import com.example.encurtadorlink.config.security.userdetails.UserDetailsServiceImpl;
import com.example.encurtadorlink.dto.UserCreateDTO;
import com.example.encurtadorlink.dto.UserResponseDTO;
import com.example.encurtadorlink.fixtures.UserFixture;
import com.example.encurtadorlink.mapper.UserMapper;
import com.example.encurtadorlink.model.User;
import com.example.encurtadorlink.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.WARN)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    private AutoCloseable closeable;

    private UserService userService;

    @BeforeEach
    void setup(){
        closeable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, userMapper, passwordEncoder, userDetailsService);
    }

    @AfterEach
    void closeMocks() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("User should be created from given data.")
    void createUserSuccess(){
        UserCreateDTO userDTO = new UserCreateDTO(
                "User Tester",
                "emailvalido@email.com",
                "Valida123$"
        );

        User user = User.builder()
                .name(userDTO.name())
                .email(userDTO.email())
                .password(userDTO.password())
                .build();

        String encodedPassword = "1$10$Encoded";

        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User userToSave = (User) i.getArguments()[0];
            userToSave.setId(1L);
            return userToSave;
        });
        when(userMapper.fromEntity(any(User.class))).thenAnswer(i -> {
            User saved = i.getArgument(0);
            return new UserResponseDTO(
                    saved.getId(),
                    saved.getName()
            );
        });

        UserResponseDTO result = userService.createUser(userDTO);

        assertEquals(1L, result.id());
        assertEquals("User Tester", result.name());

        // Ter em mente que ele tem que validar os valores antigos
        verify(userRepository).findUserByEmail(userDTO.email());
        verify(passwordEncoder).encode(userDTO.password());

        // Validando as alterações de cada atributo
        verify(userRepository).save(argThat(u ->
            u.getName().equals(userDTO.name()) &&
            u.getEmail().equals(userDTO.email()) &&
            u.getPassword().equals(encodedPassword)
        ));
    }

    @Test
    @DisplayName("Should throw an exception because email is already in use.")
    void createUserException(){

        String existingEmail = "alternative@email.com";
        UserCreateDTO userCreateDTO = new UserCreateDTO(
                "User Tester",
                existingEmail,
                "Valida123$"
        );

        User user = UserFixture.createUserFix().toBuilder()
                .id(2L)
                .email(existingEmail)
                .build();

        // Como o usuário não é útil para o teste, somente muda o email
        when(userMapper.toEntity(userCreateDTO)).thenReturn(
                UserFixture.createUserFix().toBuilder()
                        .email(existingEmail)
                        .build()
        );
        when(userRepository.findUserByEmail(existingEmail)).thenReturn(Optional.of(user));

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(userCreateDTO)
        );

        assertEquals("This email is not available.", exception.getMessage());
    }
}