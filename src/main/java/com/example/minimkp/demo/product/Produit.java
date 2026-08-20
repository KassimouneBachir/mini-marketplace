package com.example.minimkp.demo.product;

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

// API publique du module product : seule cette classe est visible des autres modules.
// vendeurId est un simple identifiant (pas de @ManyToOne) : le module product ne connait
// pas l'entite Vendeur, il respecte la frontiere entre modules.

@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String description;

    private BigDecimal prix;

    private Integer quantiteStock;

    @Column(name = "vendeur_id", nullable = false)
    private Long vendeurId;
}
