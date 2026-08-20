package com.example.minimkp.demo.commande;

import java.math.BigDecimal;

// Publie par le module paiement, mais possede par commande : cet evenement
// decrit une transition du cycle de vie de LA COMMANDE (l'agregat), donc il
// vit avec DemandePaiementEvent. Si on le mettait dans le package paiement,
// commande devrait l'importer pour l'ecouter -> dependance commande->paiement
// EN PLUS de paiement->commande (pour DemandePaiementEvent) = cycle entre
// modules, rejete par ApplicationModules.verify(). En le laissant ici,
// paiement depend de commande dans un seul sens.

public record PaiementEffectueEvent(Long commandeId, BigDecimal montant) {
}
