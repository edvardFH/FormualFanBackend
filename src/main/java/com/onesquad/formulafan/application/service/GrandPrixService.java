package com.onesquad.formulafan.application.service;

import com.onesquad.formulafan.adapter.persistence.GrandPrix;
import com.onesquad.formulafan.adapter.persistence.GrandPrixRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GrandPrixService {

    private final GrandPrixRepository grandPrixRepository;

    public GrandPrixService(GrandPrixRepository grandPrixRepository) {
        this.grandPrixRepository = grandPrixRepository;
    }

    public Optional<GrandPrix> getGrandPrixById(Long id) {
        return grandPrixRepository.findById(id);
    }
}
