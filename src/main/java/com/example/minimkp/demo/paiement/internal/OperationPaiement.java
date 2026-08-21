package com.example.minimkp.demo.paiement.internal;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// "Table des operations" pour l'idempotence (voir commentaire d'origine de
// paiementController) : une commande ne doit avoir qu'une seule operation de
// paiement, jamais deux, meme si sa DemandePaiementEvent est rejouee (reprise
// apres crash, resoumission manuelle...). commandeId est unique en base -
// c'est ce qui garantit reellement l'idempotence, pas seulement le code Java.

@Entity
@Table(name = "operation_paiement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OperationPaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commande_id", nullable = false, unique = true)
    private Long commandeId;

    @Column(nullable = false)
    private BigDecimal montant;

    @Column(name = "date_traitement", nullable = false)
    private Instant dateTraitement;
}
