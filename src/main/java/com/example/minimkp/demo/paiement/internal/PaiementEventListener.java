package com.example.minimkp.demo.paiement.internal;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import com.example.minimkp.demo.commande.DemandePaiementEvent;
import com.example.minimkp.demo.commande.PaiementEffectueEvent;

// Simulation pour cette etape : le paiement est toujours accepte, sans
// verification ni debit reel. L'idempotence (rejouer une DemandePaiementEvent
// sans double-effet, via la "table des operations" evoquee dans les
// commentaires d'origine) et le vrai grand livre a double entree (Compte,
// CompteService) arrivent aux etapes suivantes.

@Component
class PaiementEventListener {

    private final ApplicationEventPublisher eventPublisher;

    PaiementEventListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @ApplicationModuleListener
    void surDemandePaiement(DemandePaiementEvent event) {
        eventPublisher.publishEvent(new PaiementEffectueEvent(event.commandeId(), event.montant()));
    }
}
