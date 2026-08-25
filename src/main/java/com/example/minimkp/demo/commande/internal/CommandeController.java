package com.example.minimkp.demo.commande.internal;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minimkp.demo.commande.Commande;

@RestController
@RequestMapping("/api/commandes")
class CommandeController {

    private final CommandeService commandeService;

    CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    ResponseEntity<Commande> creer(@RequestBody CreationCommandeRequest requete) {
        return ResponseEntity.ok(commandeService.creer(requete));
    }

    @GetMapping
    List<Commande> lister() {
        return commandeService.lister();
    }

    @GetMapping("/{id}")
    ResponseEntity<Commande> consulter(@PathVariable Long id) {
        return commandeService.consulter(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/lignes")
    List<LigneCommande> lignes(@PathVariable Long id) {
        return commandeService.lignesDe(id);
    }

    @GetMapping("/{id}/historique")
    List<HistoriqueEtatCommande> historique(@PathVariable Long id) {
        return commandeService.historiqueDe(id);
    }
}
