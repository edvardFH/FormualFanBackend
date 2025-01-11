package com.onesquad.formulafan.security;

import com.onesquad.formulafan.adapter.dto.AuthenticationRequestDTO;
import com.onesquad.formulafan.adapter.dto.RegisterRequestDTO;
import com.onesquad.formulafan.adapter.dto.TokenAndUserDTO;
import com.onesquad.formulafan.adapter.dto.UserDTO;
import com.onesquad.formulafan.adapter.persistence.Role;
import com.onesquad.formulafan.adapter.persistence.User;
import com.onesquad.formulafan.adapter.persistence.UserRepository;
import com.onesquad.formulafan.application.exception.FieldValueAlreadyUsedException;
import com.onesquad.formulafan.application.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final int TOKEN_START_INDEX = 7;
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${auth.admin.login}")
    private String ADMIN_LOGIN;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationService(
            final PasswordEncoder passwordEncoder,
            final UserRepository userRepository,
            final JwtService jwtService,
            final AuthenticationManager authenticationManager,
            final UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public TokenAndUserDTO register(RegisterRequestDTO request) {
        User user = new User();

        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        if (request.email().equals(ADMIN_LOGIN)) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.CUSTOMER);
        }

        validateUser(user);

        User savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(UserDetailsImpl.fromEntity(user));

        return new TokenAndUserDTO(jwtToken, mapToDTO(savedUser));
    }

    public TokenAndUserDTO authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),
                                                                                   request.password()));


        var user = userRepository.findByEmail(request.email())
                                 .orElseThrow(() -> new ResourceNotFoundException(
                                         "No user with email " + request.email() +
                                                 " found."));
        var jwtToken = jwtService.generateToken(UserDetailsImpl.fromEntity(user));

        return new TokenAndUserDTO(jwtToken, mapToDTO(user));
    }

    public Boolean isUserAuthenticated(String authorizationHeader) {
        final String token;
        final String userEmail;

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return Boolean.FALSE;
        }

        token = authorizationHeader.substring(TOKEN_START_INDEX);
        userEmail = jwtService.extractUsername(token);

        if (userEmail == null) {
            return Boolean.FALSE;
        }

        UserDetails user = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtService.isTokenValid(token, user)) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public User getAuthenticatedUser(String authorizationHeader) {
        final String token;
        final String userEmail;

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        token = authorizationHeader.substring(TOKEN_START_INDEX);
        userEmail = jwtService.extractUsername(token);

        if (userEmail == null) {
            return null;
        }

        try {
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(userEmail);
            if (!jwtService.isTokenValid(token, userDetails)) {
                return null;
            }

            return userRepository.findByEmail(userEmail).orElse(null);
        } catch (UsernameNotFoundException e) {
            return null;
        }

    }


    private void validateUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new FieldValueAlreadyUsedException("Email already taken");
        }
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(user.getId(),
                           user.getEmail(),
                           user.getUsername(),
                           user.getRole().name());
    }
}
