package com.example.minimkp.demo.commande.internal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    List<LigneCommande> findByCommandeId(Long commandeId);
}
