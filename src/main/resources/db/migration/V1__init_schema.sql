-- Pas de colonnes de compte bancaire ici : le solde/compte du vendeur appartient
-- au module paiement (table compte), qui referencera vendeur_id.
create table vendeur (
    id bigserial primary key,
    nom varchar(255) not null,
    email varchar(255) not null unique
);

-- Pas d'adresse ici : l'adresse de livraison est propre a chaque commande,
-- elle sera portee par le module commande, pas par le profil acheteur.
create table acheteur (
    id bigserial primary key,
    nom varchar(255) not null,
    email varchar(255) not null unique
);

-- pas de FK vers vendeur.id : le module product ne doit pas dependre du schema du module vendeur
create table produit (
    id bigserial primary key,
    nom varchar(255) not null,
    description varchar(2000),
    prix numeric(12,2) not null,
    quantite_stock integer not null default 0,
    vendeur_id bigint not null
);
