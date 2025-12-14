package com.example.encurtadorlink.services;

import com.example.encurtadorlink.config.exception.UserAlreadyExistsException;
import com.example.encurtadorlink.config.security.gconfig.SecurityConfig;
import com.example.encurtadorlink.config.security.userdetails.UserDetailsImpl;
import com.example.encurtadorlink.config.security.userdetails.UserDetailsServiceImpl;
import com.example.encurtadorlink.dto.LinkResponseDTO;
import com.example.encurtadorlink.dto.UserCreateDTO;
import com.example.encurtadorlink.dto.UserResponseDTO;
import com.example.encurtadorlink.mapper.UserMapper;
import com.example.encurtadorlink.model.Link;
import com.example.encurtadorlink.model.RoleName;
import com.example.encurtadorlink.model.User;
import com.example.encurtadorlink.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService (UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserDetailsImpl getUserByEmail(String email){
        return (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
    }

    private boolean isEmailAvailable(String email) {
        User user = userRepository.findUserByEmail(email).orElse(null);
        return user == null;
    }

    public UserResponseDTO createUser(UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);

        if (!isEmailAvailable(user.getEmail())){
            throw new UserAlreadyExistsException("This email is not available.");
        }

        user.setRole(RoleName.BASIC);

        String password = user.getPassword();
        password = passwordEncoder.encode(password);

        user.setPassword(password);

        saveUser(user);

        return userMapper.fromEntity(user);
    }

    public User showLinksPerUser(String email){
        UserDetailsImpl userAuthenticated = getUserByEmail(email);
        return userAuthenticated.getUser();
    }

    private void saveUser(User user) {
        userRepository.save(user);
        logger.info("The user {} was created successfully.", user.getEmail());
    }
}
