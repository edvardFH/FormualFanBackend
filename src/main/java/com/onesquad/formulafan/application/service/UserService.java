package com.onesquad.formulafan.application.service;

import com.onesquad.formulafan.adapter.persistence.User;
import com.onesquad.formulafan.adapter.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
