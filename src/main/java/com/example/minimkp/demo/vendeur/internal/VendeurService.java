package com.example.minimkp.demo.vendeur.internal;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.minimkp.demo.vendeur.Vendeur;

@Service
class VendeurService {

    private final VendeurRepository vendeurRepository;

    VendeurService(VendeurRepository vendeurRepository) {
        this.vendeurRepository = vendeurRepository;
    }

    Vendeur creer(Vendeur vendeur) {
        vendeur.setId(null);
        return vendeurRepository.save(vendeur);
    }

    Optional<Vendeur> consulter(Long id) {
        return vendeurRepository.findById(id);
    }

    List<Vendeur> lister() {
        return vendeurRepository.findAll();
    }

    Optional<Vendeur> modifier(Long id, Vendeur donnees) {
        return vendeurRepository.findById(id).map(existant -> {
            existant.setNom(donnees.getNom());
            existant.setEmail(donnees.getEmail());
            return vendeurRepository.save(existant);
        });
    }

    boolean supprimer(Long id) {
        if (!vendeurRepository.existsById(id)) {
            return false;
        }
        vendeurRepository.deleteById(id);
        return true;
    }
}
