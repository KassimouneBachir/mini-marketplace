package com.example.minimkp.demo.commande.internal;

import java.math.BigDecimal;
import java.util.List;

// Simplification assumee pour cette etape : le client fournit prixUnitaire
// directement. Un vrai systeme ne doit JAMAIS faire confiance au prix envoye
// par le client (un client malveillant pourrait mettre n'importe quel prix) :
// il faudrait interroger le module product pour le prix reel au moment de la
// commande. On le fera quand on exposera un port public depuis product.

record CreationCommandeRequest(Long acheteurId, List<LigneRequest> lignes) {

    record LigneRequest(Long produitId, Long vendeurId, Integer quantite, BigDecimal prixUnitaire) {
    }
}
