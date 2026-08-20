package com.example.minimkp.demo.acheteur.internal;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.minimkp.demo.acheteur.Acheteur;

@Service
class AcheteurService {

    private final AcheteurRepository acheteurRepository;

    AcheteurService(AcheteurRepository acheteurRepository) {
        this.acheteurRepository = acheteurRepository;
    }

    Acheteur creer(Acheteur acheteur) {
        acheteur.setId(null);
        return acheteurRepository.save(acheteur);
    }

    Optional<Acheteur> consulter(Long id) {
        return acheteurRepository.findById(id);
    }

    List<Acheteur> lister() {
        return acheteurRepository.findAll();
    }

    Optional<Acheteur> modifier(Long id, Acheteur donnees) {
        return acheteurRepository.findById(id).map(existant -> {
            existant.setNom(donnees.getNom());
            existant.setEmail(donnees.getEmail());
            return acheteurRepository.save(existant);
        });
    }

    boolean supprimer(Long id) {
        if (!acheteurRepository.existsById(id)) {
            return false;
        }
        acheteurRepository.deleteById(id);
        return true;
    }
}
