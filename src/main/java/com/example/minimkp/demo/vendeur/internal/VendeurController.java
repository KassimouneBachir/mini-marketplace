package com.example.minimkp.demo.vendeur.internal;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minimkp.demo.vendeur.Vendeur;

@RestController
@RequestMapping("/api/vendeurs")
class VendeurController {

    private final VendeurService vendeurService;

    VendeurController(VendeurService vendeurService) {
        this.vendeurService = vendeurService;
    }

    @PostMapping
    ResponseEntity<Vendeur> creer(@RequestBody Vendeur vendeur) {
        return ResponseEntity.ok(vendeurService.creer(vendeur));
    }

    @GetMapping
    List<Vendeur> lister() {
        return vendeurService.lister();
    }

    @GetMapping("/{id}")
    ResponseEntity<Vendeur> consulter(@PathVariable Long id) {
        return vendeurService.consulter(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    ResponseEntity<Vendeur> modifier(@PathVariable Long id, @RequestBody Vendeur vendeur) {
        return vendeurService.modifier(id, vendeur)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> supprimer(@PathVariable Long id) {
        return vendeurService.supprimer(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
