package com.example.minimkp.demo.commande.internal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.minimkp.demo.commande.Commande;

import jakarta.persistence.LockModeType;

interface CommandeRepository extends JpaRepository<Commande, Long> {

    // Verrou pessimiste : utilise uniquement pour les transitions d'etat
    // (voir CommandeService.appliquerTransition), pas pour les lectures
    // simples (consulter/lister), pour ne pas bloquer inutilement les GET.
    // Necessaire car plusieurs @ApplicationModuleListener peuvent modifier
    // la meme commande en parallele, sur des threads/transactions distincts
    // (ex: CommandeExpedieeEvent et LivraisonEffectueEvent quasi simultanes) :
    // sans ce verrou, le second a lire peut ecraser le resultat du premier
    // meme s'il a commite apres (lost update).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Commande c where c.id = :id")
    Optional<Commande> findByIdPourMiseAJour(@Param("id") Long id);
}
