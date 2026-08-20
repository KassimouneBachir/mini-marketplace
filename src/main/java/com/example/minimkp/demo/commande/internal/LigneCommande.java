package com.example.minimkp.demo.commande.internal;

import java.math.BigDecimal;

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

// Detail interne : les autres modules n'ont pas besoin de voir les lignes
// d'une commande directement, seulement les evenements qu'elle publie.
// produitId/vendeurId/prixUnitaire sont un instantane (snapshot) au moment
// de la commande : pas de FK vers product/vendeur (frontiere entre modules),
// et le prix ne doit pas bouger si le produit change de prix plus tard.

@Entity
@Table(name = "ligne_commande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commande_id", nullable = false)
    private Long commandeId;

    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @Column(name = "vendeur_id", nullable = false)
    private Long vendeurId;

    private Integer quantite;

    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;
}
