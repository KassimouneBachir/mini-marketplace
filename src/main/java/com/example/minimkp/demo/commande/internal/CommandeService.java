package com.example.minimkp.demo.commande.internal;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.minimkp.demo.commande.Commande;
import com.example.minimkp.demo.commande.CommandeExpedieeEvent;
import com.example.minimkp.demo.commande.DemandePaiementEvent;
import com.example.minimkp.demo.commande.EnumEtatCommande;
import com.example.minimkp.demo.commande.LivraisonEffectueEvent;
import com.example.minimkp.demo.commande.PaiementEffectueEvent;

@Service
class CommandeService {

    private static final Logger log = LoggerFactory.getLogger(CommandeService.class);

    // Machine d'etat explicite : source unique de verite pour "quel etat peut
    // devenir quel etat". CREEE n'apparait jamais comme cle car ce n'est
    // jamais une cible de transition (c'est l'etat initial, fixe dans creer()).
    // CLOTUREE et ANNULEE ne sont pas encore cablees a des evenements - a
    // faire quand les flux de cloture/annulation seront construits.
    private static final Map<EnumEtatCommande, Set<EnumEtatCommande>> TRANSITIONS_AUTORISEES = Map.of(
            EnumEtatCommande.PAYEE, EnumSet.of(EnumEtatCommande.CREEE),
            EnumEtatCommande.EXPEDIEE, EnumSet.of(EnumEtatCommande.PAYEE),
            EnumEtatCommande.LIVREE, EnumSet.of(EnumEtatCommande.PAYEE, EnumEtatCommande.EXPEDIEE));

    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final HistoriqueEtatCommandeRepository historiqueRepository;
    private final ApplicationEventPublisher eventPublisher;

    CommandeService(CommandeRepository commandeRepository, LigneCommandeRepository ligneCommandeRepository,
            HistoriqueEtatCommandeRepository historiqueRepository, ApplicationEventPublisher eventPublisher) {
        this.commandeRepository = commandeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.historiqueRepository = historiqueRepository;
        this.eventPublisher = eventPublisher;
    }

    // @Transactional : la commande + ses lignes + la publication de
    // l'evenement doivent reussir ou echouer ensemble. Grace au registre
    // d'evenements de Modulith, DemandePaiementEvent n'est effectivement
    // livre au module paiement qu'APRES le commit de cette transaction -
    // jamais de demande de paiement pour une commande qui n'a pas ete
    // sauvegardee.
    @Transactional
    Commande creer(CreationCommandeRequest requete) {
        Commande commande = Commande.builder()
                .acheteurId(requete.acheteurId())
                .etat(EnumEtatCommande.CREEE)
                .dateCreation(Instant.now())
                .build();
        commande = commandeRepository.save(commande);

        historiqueRepository.save(HistoriqueEtatCommande.builder()
                .commandeId(commande.getId())
                .etatAvant(null)
                .etatDemande(EnumEtatCommande.CREEE)
                .accepte(true)
                .horodatage(commande.getDateCreation())
                .build());

        BigDecimal montantTotal = BigDecimal.ZERO;
        for (CreationCommandeRequest.LigneRequest ligneRequete : requete.lignes()) {
            LigneCommande ligne = LigneCommande.builder()
                    .commandeId(commande.getId())
                    .produitId(ligneRequete.produitId())
                    .vendeurId(ligneRequete.vendeurId())
                    .quantite(ligneRequete.quantite())
                    .prixUnitaire(ligneRequete.prixUnitaire())
                    .build();
            ligneCommandeRepository.save(ligne);
            montantTotal = montantTotal.add(ligneRequete.prixUnitaire().multiply(BigDecimal.valueOf(ligneRequete.quantite())));
        }

        eventPublisher.publishEvent(new DemandePaiementEvent(commande.getId(), montantTotal));
        return commande;
    }

    Optional<Commande> consulter(Long id) {
        return commandeRepository.findById(id);
    }

    List<Commande> lister() {
        return commandeRepository.findAll();
    }

    List<LigneCommande> lignesDe(Long commandeId) {
        return ligneCommandeRepository.findByCommandeId(commandeId);
    }

    List<HistoriqueEtatCommande> historiqueDe(Long commandeId) {
        return historiqueRepository.findByCommandeIdOrderByHorodatageAsc(commandeId);
    }

    // @ApplicationModuleListener regroupe @Async + @Transactional(REQUIRES_NEW)
    // + @TransactionalEventListener(AFTER_COMMIT) : cette methode s'execute
    // sur un thread separe, dans sa propre transaction, une fois que paiement
    // a lui-meme commite la publication de PaiementEffectueEvent.
    @ApplicationModuleListener
    void surPaiementEffectue(PaiementEffectueEvent event) {
        appliquerTransition(event.commandeId(), EnumEtatCommande.PAYEE);
    }

    @ApplicationModuleListener
    void surCommandeExpediee(CommandeExpedieeEvent event) {
        appliquerTransition(event.commandeId(), EnumEtatCommande.EXPEDIEE);
    }

    @ApplicationModuleListener
    void surLivraisonEffectuee(LivraisonEffectueEvent event) {
        appliquerTransition(event.commandeId(), EnumEtatCommande.LIVREE);
    }

    // Applique une transition si et seulement si l'etat courant fait partie
    // des sources autorisees pour l'etat demande (TRANSITIONS_AUTORISEES).
    // Le verrou pessimiste (findByIdPourMiseAJour) rend cette lecture fiable
    // sous concurrence : sans lui, deux transitions independantes pourraient
    // chacune lire l'etat AVANT l'ecriture de l'autre et toutes les deux
    // passer la garde (lost update - vecu et corrige sur ce projet, voir
    // memoire). Chaque tentative - acceptee ou non - est journalisee dans
    // historique_etat_commande : rien n'est jamais silencieusement perdu,
    // meme une transition refusee reste consultable.
    private void appliquerTransition(Long commandeId, EnumEtatCommande etatDemande) {
        Set<EnumEtatCommande> etatsAcceptes = TRANSITIONS_AUTORISEES.get(etatDemande);
        commandeRepository.findByIdPourMiseAJour(commandeId).ifPresentOrElse(commande -> {
            EnumEtatCommande etatAvant = commande.getEtat();
            boolean accepte = etatsAcceptes.contains(etatAvant);

            if (accepte) {
                commande.setEtat(etatDemande);
                commandeRepository.save(commande);
            } else {
                log.warn("Transition de la commande {} vers {} refusee : etat actuel {} n'est dans aucun des etats "
                        + "acceptes {} (evenement hors-ordre ou duplique)",
                        commandeId, etatDemande, etatAvant, etatsAcceptes);
            }

            historiqueRepository.save(HistoriqueEtatCommande.builder()
                    .commandeId(commandeId)
                    .etatAvant(etatAvant)
                    .etatDemande(etatDemande)
                    .accepte(accepte)
                    .horodatage(Instant.now())
                    .build());
        }, () -> log.warn("Transition vers {} ignoree : commande {} introuvable", etatDemande, commandeId));
    }
}
