package com.example.minimkp.demo.commande;

import java.time.Instant;

// Publie par le module expedition, mais possede par commande - meme
// raisonnement que PaiementEffectueEvent : decrit une transition du cycle
// de vie de la commande, donc appartient a son package pour eviter un cycle
// entre modules (voir PaiementEffectueEvent pour le detail).

public record CommandeExpedieeEvent(Long commandeId, Instant dateExpedition) {
}
