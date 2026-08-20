package com.example.minimkp.demo.product.internal;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.minimkp.demo.product.Produit;

@Service
class ProduitService {

    private final ProduitRepository produitRepository;

    ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    Produit creer(Produit produit) {
        produit.setId(null);
        return produitRepository.save(produit);
    }

    Optional<Produit> consulter(Long id) {
        return produitRepository.findById(id);
    }

    List<Produit> lister() {
        return produitRepository.findAll();
    }

    Optional<Produit> modifier(Long id, Produit donnees) {
        return produitRepository.findById(id).map(existant -> {
            existant.setNom(donnees.getNom());
            existant.setDescription(donnees.getDescription());
            existant.setPrix(donnees.getPrix());
            existant.setQuantiteStock(donnees.getQuantiteStock());
            return produitRepository.save(existant);
        });
    }

    boolean supprimer(Long id) {
        if (!produitRepository.existsById(id)) {
            return false;
        }
        produitRepository.deleteById(id);
        return true;
    }
}
