package com.example.minimkp.demo.vendeur.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.minimkp.demo.vendeur.Vendeur;

interface VendeurRepository extends JpaRepository<Vendeur, Long> {
}
