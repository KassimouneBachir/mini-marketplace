package com.example.minimkp.demo.commande;

import java.time.Instant;

// Publie par le module expedition, possede par commande (meme raisonnement
// que CommandeExpedieeEvent et PaiementEffectueEvent).

public record LivraisonEffectueEvent(Long commandeId, Instant dateLivraison) {
}
