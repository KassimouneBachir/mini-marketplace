package com.example.minimkp.demo.product.internal;

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

import com.example.minimkp.demo.product.Produit;

@RestController
@RequestMapping("/api/produits")
class ProduitController {

    private final ProduitService produitService;

    ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @PostMapping
    ResponseEntity<Produit> creer(@RequestBody Produit produit) {
        return ResponseEntity.ok(produitService.creer(produit));
    }

    @GetMapping
    List<Produit> lister() {
        return produitService.lister();
    }

    @GetMapping("/{id}")
    ResponseEntity<Produit> consulter(@PathVariable Long id) {
        return produitService.consulter(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    ResponseEntity<Produit> modifier(@PathVariable Long id, @RequestBody Produit produit) {
        return produitService.modifier(id, produit)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> supprimer(@PathVariable Long id) {
        return produitService.supprimer(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
