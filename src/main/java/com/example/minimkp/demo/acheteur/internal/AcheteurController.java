package com.example.minimkp.demo.acheteur.internal;

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

import com.example.minimkp.demo.acheteur.Acheteur;

@RestController
@RequestMapping("/api/acheteurs")
class AcheteurController {

    private final AcheteurService acheteurService;

    AcheteurController(AcheteurService acheteurService) {
        this.acheteurService = acheteurService;
    }

    @PostMapping
    ResponseEntity<Acheteur> creer(@RequestBody Acheteur acheteur) {
        return ResponseEntity.ok(acheteurService.creer(acheteur));
    }

    @GetMapping
    List<Acheteur> lister() {
        return acheteurService.lister();
    }

    @GetMapping("/{id}")
    ResponseEntity<Acheteur> consulter(@PathVariable Long id) {
        return acheteurService.consulter(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    ResponseEntity<Acheteur> modifier(@PathVariable Long id, @RequestBody Acheteur acheteur) {
        return acheteurService.modifier(id, acheteur)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> supprimer(@PathVariable Long id) {
        return acheteurService.supprimer(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
