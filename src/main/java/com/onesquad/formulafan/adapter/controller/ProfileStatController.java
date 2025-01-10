package com.onesquad.formulafan.adapter.controller;

import com.onesquad.formulafan.adapter.dto.ProfileStatDTO;
import com.onesquad.formulafan.application.service.ProfileStatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
public class ProfileStatController {

    private final ProfileStatService profileStatService;

    public ProfileStatController(ProfileStatService profileStatService) {
        this.profileStatService = profileStatService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileStatDTO> getProfileStats(@PathVariable("userId") Long userId) {
        ProfileStatDTO profileStats = profileStatService.getProfileStats(userId);
        return ResponseEntity.ok(profileStats);
    }
}


