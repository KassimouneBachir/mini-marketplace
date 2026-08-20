package com.example.minimkp.demo.vendeur;

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

// API publique du module vendeur : seule cette classe est visible des autres modules.
// La logique (Repository/Service/Controller) reste dans vendeur.internal.
// Le compte de reversement (solde, mouvements) appartient au module paiement,
// pas a cette entite : voir paiement.internal.Compte, relie par vendeurId.

@Entity
@Table(name = "vendeur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String email;
}
