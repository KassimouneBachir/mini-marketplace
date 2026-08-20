package com.example.minimkp.demo.commande.internal;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final ApplicationEventPublisher eventPublisher;

    CommandeService(CommandeRepository commandeRepository, LigneCommandeRepository ligneCommandeRepository,
            ApplicationEventPublisher eventPublisher) {
        this.commandeRepository = commandeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
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

    // @ApplicationModuleListener regroupe @Async + @Transactional(REQUIRES_NEW)
    // + @TransactionalEventListener(AFTER_COMMIT) : cette methode s'execute
    // sur un thread separe, dans sa propre transaction, une fois que paiement
    // a lui-meme commite la publication de PaiementEffectueEvent.
    @ApplicationModuleListener
    void surPaiementEffectue(PaiementEffectueEvent event) {
        appliquerTransition(event.commandeId(), EnumEtatCommande.PAYEE, EnumEtatCommande.CREEE);
    }

    // N'accepte EXPEDIEE que depuis PAYEE : si LIVREE a deja ete applique
    // (LivraisonEffectueEvent traite avant CommandeExpedieeEvent - possible,
    // aucun ordre garanti entre listeners @Async independants), l'etat courant
    // sera LIVREE et non PAYEE, donc cette transition est refusee. Voulu :
    // EXPEDIEE precede LIVREE dans le cycle de vie, un retour en arriere
    // n'a pas de sens metier. Le verrou pessimiste de appliquerTransition
    // garantit que cette lecture voit bien l'etat a jour (pas une version
    // perimee lue avant l'ecriture concurrente de surLivraisonEffectuee).
    @ApplicationModuleListener
    void surCommandeExpediee(CommandeExpedieeEvent event) {
        appliquerTransition(event.commandeId(), EnumEtatCommande.EXPEDIEE, EnumEtatCommande.PAYEE);
    }

    // Accepte LIVREE depuis PAYEE OU EXPEDIEE : contrairement a surCommandeExpediee,
    // on tolere ici que la livraison "double" l'expedition si son evenement
    // arrive en premier - on prefere ne jamais perdre le fait "livre" (l'etat
    // final le plus important) plutot que d'exiger un ordre strict. Cout
    // connu : si CommandeExpedieeEvent arrive ensuite, il sera rejete par la
    // garde ci-dessus et l'horodatage d'expedition ne sera jamais enregistre -
    // limite acceptee pour l'instant, a combler par l'historique de l'etape
    // machine d'etat.
    @ApplicationModuleListener
    void surLivraisonEffectuee(LivraisonEffectueEvent event) {
        appliquerTransition(event.commandeId(), EnumEtatCommande.LIVREE, EnumEtatCommande.PAYEE, EnumEtatCommande.EXPEDIEE);
    }

    // Garde minimale contre un evenement recu hors-ordre ou en double : on ne
    // transitionne que si l'etat actuel est bien l'un de ceux attendus avant
    // ce changement. Le verrou pessimiste (findByIdPourMiseAJour) est ce qui
    // rend cette garde fiable : sans lui, deux transitions concurrentes
    // pourraient chacune lire l'etat AVANT l'ecriture de l'autre et toutes
    // les deux passer la garde (lost update - vecu et corrige sur ce projet,
    // voir memoire). La vraie machine d'etat avec historique horodate viendra
    // a l'etape dediee ; ceci n'est qu'un garde-fou de coherence en attendant -
    // mais un garde-fou qui refuse silencieusement n'est pas acceptable pour
    // un systeme fiable : toute transition refusee est donc journalisee en
    // warning, pour rester visible meme si personne ne regarde a ce moment-la.
    private void appliquerTransition(Long commandeId, EnumEtatCommande nouvelEtat, EnumEtatCommande... etatsAttendus) {
        commandeRepository.findByIdPourMiseAJour(commandeId).ifPresentOrElse(commande -> {
            for (EnumEtatCommande etatAttendu : etatsAttendus) {
                if (commande.getEtat() == etatAttendu) {
                    commande.setEtat(nouvelEtat);
                    commandeRepository.save(commande);
                    return;
                }
            }
            log.warn("Transition de la commande {} vers {} refusee : etat actuel {} n'est dans aucun des etats acceptes {} "
                    + "(evenement hors-ordre ou duplique)",
                    commandeId, nouvelEtat, commande.getEtat(), etatsAttendus);
        }, () -> log.warn("Transition vers {} ignoree : commande {} introuvable", nouvelEtat, commandeId));
    }
}
