-- FK vers commande.id autorisee ici : historique_etat_commande vit dans le
-- meme module que commande (contrairement a produit_id/vendeur_id/acheteur_id
-- ailleurs, qui traversent une frontiere de module et n'ont donc pas de FK).
create table historique_etat_commande (
    id bigserial primary key,
    commande_id bigint not null references commande(id),
    etat_avant varchar(20),
    etat_demande varchar(20) not null,
    accepte boolean not null,
    horodatage timestamptz not null
);

create index idx_historique_etat_commande_commande_id on historique_etat_commande(commande_id);
