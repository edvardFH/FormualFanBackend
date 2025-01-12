package com.onesquad.formulafan.application.service;

import com.onesquad.formulafan.adapter.dto.GrandPrixDTO;
import com.onesquad.formulafan.adapter.persistence.GrandPrix;
import com.onesquad.formulafan.adapter.persistence.GrandPrixRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrandPrixService {

    private final GrandPrixRepository grandPrixRepository;

    public GrandPrixService(GrandPrixRepository grandPrixRepository) {
        this.grandPrixRepository = grandPrixRepository;
    }

    public Optional<GrandPrixDTO> getGrandPrixById(Long id) {
        return grandPrixRepository.findById(id).map(this::mapToDTO);
    }

    public List<GrandPrixDTO> getAllGrandPrix() {
        return grandPrixRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    private GrandPrixDTO mapToDTO(GrandPrix grandPrix) {
        return new GrandPrixDTO(grandPrix.getId(), grandPrix.getName());
    }
}
