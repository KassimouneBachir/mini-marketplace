package com.example.minimkp.demo.paiement.internal;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import com.example.minimkp.demo.commande.DemandePaiementEvent;
import com.example.minimkp.demo.commande.PaiementEffectueEvent;

// Simulation pour cette etape : le paiement est toujours ACCEPTE, sans
// verification ni debit reel (le vrai grand livre a double entree, Compte /
// CompteService, arrive a une etape suivante). Ce qui EST reel ici :
// l'idempotence - une DemandePaiementEvent rejouee (reprise apres crash via
// le registre d'evenements Modulith, resoumission manuelle, etc.) pour une
// commande deja traitee ne doit jamais creer une deuxieme operation ni
// publier un montant different.
@Component
class PaiementEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaiementEventListener.class);

    private final ApplicationEventPublisher eventPublisher;
    private final OperationPaiementRepository operationPaiementRepository;

    PaiementEventListener(ApplicationEventPublisher eventPublisher, OperationPaiementRepository operationPaiementRepository) {
        this.eventPublisher = eventPublisher;
        this.operationPaiementRepository = operationPaiementRepository;
    }

    @ApplicationModuleListener
    void surDemandePaiement(DemandePaiementEvent event) {
        Optional<OperationPaiement> operationExistante = operationPaiementRepository.findByCommandeId(event.commandeId());
        if (operationExistante.isPresent()) {
            OperationPaiement operation = operationExistante.get();
            log.info("DemandePaiementEvent rejouee pour la commande {} (operation deja enregistree le {}) : "
                    + "reponse renvoyee sans nouveau debit (idempotence)",
                    event.commandeId(), operation.getDateTraitement());
            eventPublisher.publishEvent(new PaiementEffectueEvent(event.commandeId(), operation.getMontant()));
            return;
        }

        // La contrainte unique sur commande_id (voir V4__operation_paiement.sql)
        // est le vrai garde-fou : si un doublon concurrent passe quand meme le
        // controle ci-dessus (fenetre entre le SELECT et l'INSERT), cet appel
        // echoue et la transaction de CET appel est annulee - l'evenement reste
        // non complete dans le registre Modulith et sera rejoue plus tard, ce qui
        // empruntera alors le chemin ci-dessus. On ne tente pas de rattraper
        // l'exception dans la meme transaction : une fois une contrainte violee,
        // Hibernate marque la transaction en cours rollback-only, la rattraper ne
        // l'empecherait pas d'echouer au commit (meme piege que pour le verrou
        // pessimiste cote commande, version "detection" plutot que "prevention").
        operationPaiementRepository.save(OperationPaiement.builder()
                .commandeId(event.commandeId())
                .montant(event.montant())
                .dateTraitement(Instant.now())
                .build());

        eventPublisher.publishEvent(new PaiementEffectueEvent(event.commandeId(), event.montant()));
    }
}
