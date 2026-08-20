package com.example.minimkp.demo.commande;

// Public (pas .internal) : c'est le vocabulaire du contrat de commande,
// les autres modules et les clients de l'API ont besoin de connaitre ces
// valeurs. Ce qui reste interne, c'est la logique qui DECIDE des transitions.

public enum EnumEtatCommande {
    CREEE,
    PAYEE,
    EXPEDIEE,
    LIVREE,
    CLOTUREE,
    ANNULEE
}
