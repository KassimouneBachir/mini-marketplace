package com.example.minimkp.demo.commande;

import java.math.BigDecimal;

// Publie par le module commande a la creation d'une commande. Public car
// c'est le "contrat" que le module paiement doit connaitre pour l'ecouter
// (@ApplicationModuleListener) - c'est le seul couplage autorise entre
// modules : le type d'evenement, pas l'implementation qui le publie.

public record DemandePaiementEvent(Long commandeId, BigDecimal montant) {
}
