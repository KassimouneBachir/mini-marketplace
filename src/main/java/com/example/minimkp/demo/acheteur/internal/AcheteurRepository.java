package com.example.minimkp.demo.acheteur.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.minimkp.demo.acheteur.Acheteur;

interface AcheteurRepository extends JpaRepository<Acheteur, Long> {
}
