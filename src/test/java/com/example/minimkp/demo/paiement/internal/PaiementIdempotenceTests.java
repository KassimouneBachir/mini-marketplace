package com.example.minimkp.demo.paiement.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.EnableScenarios;
import org.springframework.modulith.test.Scenario;

import com.example.minimkp.demo.commande.DemandePaiementEvent;
import com.example.minimkp.demo.commande.PaiementEffectueEvent;

// @ApplicationModuleTest ne demarre que le module paiement + ses dependances
// reelles (commande, pour les types d'evenements) - pas expedition, qui
// n'est pas une dependance de paiement. Verifie le comportement asynchrone
// reel (@ApplicationModuleListener), pas un appel direct de methode.
@ApplicationModuleTest
@EnableScenarios
class PaiementIdempotenceTests {

    @Autowired
    OperationPaiementRepository operationPaiementRepository;

    // Pas de @Transactional ici : les listeners @ApplicationModuleListener
    // s'executent dans leurs propres transactions (REQUIRES_NEW), donc un
    // rollback de la transaction du test ne les annulerait pas - on choisit
    // plutot un commandeId unique par execution pour ne pas dependre d'un
    // nettoyage entre les runs.
    @Test
    void unePublicationRejoueeNeCreeQuUneSeuleOperationEtRenvoieLeMemeMontant(Scenario scenario) {
        Long commandeId = ThreadLocalRandom.current().nextLong(1_000_000L, 2_000_000L);
        BigDecimal montant = new BigDecimal("17.50");

        scenario.publish(new DemandePaiementEvent(commandeId, montant))
                .andWaitForEventOfType(PaiementEffectueEvent.class)
                .matching(e -> e.commandeId().equals(commandeId))
                .toArriveAndVerify(e -> assertThat(e.montant()).isEqualByComparingTo(montant));

        assertThat(operationPaiementRepository.findByCommandeId(commandeId)).isPresent();

        // Rejeu du MEME evenement (simule une reprise apres crash avant que le
        // premier traitement n'ait ete marque complet, ou une resoumission
        // manuelle) : doit quand meme repondre, mais sans creer d'operation.
        scenario.publish(new DemandePaiementEvent(commandeId, montant))
                .andWaitForEventOfType(PaiementEffectueEvent.class)
                .matching(e -> e.commandeId().equals(commandeId))
                .toArriveAndVerify(e -> assertThat(e.montant()).isEqualByComparingTo(montant));

        long nbOperations = operationPaiementRepository.findAll().stream()
                .filter(operation -> commandeId.equals(operation.getCommandeId()))
                .count();
        assertThat(nbOperations).isEqualTo(1L);
    }
}
