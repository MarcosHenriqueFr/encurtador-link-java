package com.example.encurtadorlink.services;

import com.example.encurtadorlink.config.exception.UserAlreadyExistsException;
import com.example.encurtadorlink.dto.UserCreateDTO;
import com.example.encurtadorlink.dto.UserResponseDTO;
import com.example.encurtadorlink.mapper.UserMapper;
import com.example.encurtadorlink.model.RoleName;
import com.example.encurtadorlink.model.User;
import com.example.encurtadorlink.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService (UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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

        saveUser(user);

        return userMapper.fromEntity(user);
    }

    private void saveUser(User user) {
        userRepository.save(user);
    }
}
