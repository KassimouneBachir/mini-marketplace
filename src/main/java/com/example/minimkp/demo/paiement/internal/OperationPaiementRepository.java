package com.example.minimkp.demo.paiement.internal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface OperationPaiementRepository extends JpaRepository<OperationPaiement, Long> {

    Optional<OperationPaiement> findByCommandeId(Long commandeId);
}
