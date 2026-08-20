package com.example.minimkp.demo.acheteur;

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

// API publique du module acheteur : seule cette classe est visible des autres modules.
// Pas d'adresse ici : l'adresse de livraison est propre a chaque commande,
// elle sera portee par le module commande (une commande peut etre livree
// a une adresse differente a chaque fois), pas par le profil acheteur.

@Entity
@Table(name = "acheteur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acheteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String email;
}
