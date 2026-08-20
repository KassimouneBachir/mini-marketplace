package com.example.minimkp.demo.commande;

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

// API publique du module commande. acheteurId reste un simple identifiant
// (pas de @ManyToOne vers le module acheteur) : meme logique que Produit.vendeurId.

@Entity
@Table(name = "commande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "acheteur_id", nullable = false)
    private Long acheteurId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumEtatCommande etat;

    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;
}
