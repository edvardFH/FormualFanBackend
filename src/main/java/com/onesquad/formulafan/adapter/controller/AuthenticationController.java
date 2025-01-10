package com.onesquad.formulafan.adapter.controller;

import com.onesquad.formulafan.adapter.dto.AuthenticationRequestDTO;
import com.onesquad.formulafan.adapter.dto.RegisterRequestDTO;
import com.onesquad.formulafan.adapter.dto.TokenAndUserDTO;
import com.onesquad.formulafan.security.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenAndUserDTO> register(@RequestBody RegisterRequestDTO registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenAndUserDTO> logIn(@RequestBody AuthenticationRequestDTO authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(
                authenticationRequest));
    }


    // todo: handle logout ?
    @PostMapping("/logout")
    public ResponseEntity<String> logOut() {
        return ResponseEntity.ok().body("Successfully logged out.");
    }

    // todo: remove and create a filter instead
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(
            @RequestHeader(AUTHORIZATION_HEADER) String authorizationHeader) {
        boolean isUserAuthenticated =
                authenticationService.isUserAuthenticated(authorizationHeader);

        if (isUserAuthenticated) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
