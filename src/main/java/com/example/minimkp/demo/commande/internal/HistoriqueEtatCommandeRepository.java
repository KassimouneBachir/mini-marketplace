package com.example.minimkp.demo.commande.internal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface HistoriqueEtatCommandeRepository extends JpaRepository<HistoriqueEtatCommande, Long> {

    List<HistoriqueEtatCommande> findByCommandeIdOrderByHorodatageAsc(Long commandeId);
}
