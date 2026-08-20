-- Etat courant + horodatage de creation seulement pour l'instant. L'historique
-- complet des transitions (avec date/heure/min/sec de chaque changement,
-- evoque dans les commentaires d'origine) viendra avec la machine d'etat.
create table commande (
    id bigserial primary key,
    acheteur_id bigint not null,
    etat varchar(20) not null,
    date_creation timestamptz not null
);

-- Une commande peut contenir plusieurs produits, potentiellement de vendeurs
-- differents. produit_id/vendeur_id/prix_unitaire sont un instantane fige au
-- moment de la commande : pas de FK vers produit/vendeur (frontiere entre
-- modules), et le prix ne doit pas bouger si le produit change de prix plus tard.
create table ligne_commande (
    id bigserial primary key,
    commande_id bigint not null,
    produit_id bigint not null,
    vendeur_id bigint not null,
    quantite integer not null,
    prix_unitaire numeric(12,2) not null
);
