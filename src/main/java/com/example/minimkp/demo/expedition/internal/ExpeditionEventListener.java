package com.example.minimkp.demo.expedition.internal;

import java.time.Instant;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import com.example.minimkp.demo.commande.CommandeExpedieeEvent;
import com.example.minimkp.demo.commande.LivraisonEffectueEvent;
import com.example.minimkp.demo.commande.PaiementEffectueEvent;

// Simulation pour cette etape : expedition puis livraison sont declenchees
// immediatement, sans vraie recuperation de l'adresse du client (meme
// simplification assumee que le prix dans CreationCommandeRequest - a
// corriger plus tard via un port public expose par commande/acheteur) et
// sans vrai delai transporteur. En vrai, "expediee" et "livree" seraient
// deux declencheurs bien distincts et separes dans le temps (action du
// vendeur, puis webhook transporteur) : on les garde comme deux evenements
// distincts pour que commande conserve l'historique complet des transitions,
// meme si la simulation les publie l'un juste apres l'autre.
@Component
class ExpeditionEventListener {

    private final ApplicationEventPublisher eventPublisher;

    ExpeditionEventListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @ApplicationModuleListener
    void surPaiementEffectue(PaiementEffectueEvent event) {
        eventPublisher.publishEvent(new CommandeExpedieeEvent(event.commandeId(), Instant.now()));
        eventPublisher.publishEvent(new LivraisonEffectueEvent(event.commandeId(), Instant.now()));
    }
}
