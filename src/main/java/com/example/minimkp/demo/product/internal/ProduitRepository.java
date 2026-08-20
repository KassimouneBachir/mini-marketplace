package com.example.minimkp.demo.product.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.minimkp.demo.product.Produit;

interface ProduitRepository extends JpaRepository<Produit, Long> {
}
