package com.onesquad.formulafan.adapter.controller;

import com.onesquad.formulafan.adapter.dto.GrandPrixDTO;
import com.onesquad.formulafan.application.service.GrandPrixService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grand-prix")
public class GrandPrixController {
    private final GrandPrixService grandPrixService;

    public GrandPrixController(GrandPrixService grandPrixService) {
        this.grandPrixService = grandPrixService;
    }

    @GetMapping
    public ResponseEntity<List<GrandPrixDTO>> getAllGrandPrix() {
        return ResponseEntity.ok(grandPrixService.getAllGrandPrix());
    }
}
