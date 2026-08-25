package com.example.minimkp.demo.commande.internal;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.minimkp.demo.commande.EnumEtatCommande;

// "Table des etats" evoquee dans les commentaires d'origine : journalise
// CHAQUE tentative de transition, acceptee ou refusee, avec horodatage.
// Contrairement au champ Commande.etat (qui ne retient que l'etat courant),
// rien n'est jamais perdu ici - meme une transition refusee (evenement
// hors-ordre ou duplique) laisse une trace consultable.

@Entity
@Table(name = "historique_etat_commande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class HistoriqueEtatCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commande_id", nullable = false)
    private Long commandeId;

    // null uniquement pour la toute premiere entree (creation de la commande).
    @Enumerated(EnumType.STRING)
    @Column(name = "etat_avant")
    private EnumEtatCommande etatAvant;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_demande", nullable = false)
    private EnumEtatCommande etatDemande;

    @Column(nullable = false)
    private boolean accepte;

    @Column(nullable = false)
    private Instant horodatage;
}
