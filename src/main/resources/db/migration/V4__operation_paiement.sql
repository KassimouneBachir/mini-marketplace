-- Table d'operations pour l'idempotence du paiement : une DemandePaiementEvent
-- rejouee (ex. apres reprise suite a un crash, via le registre d'evenements
-- Modulith) pour une commande deja traitee ne doit jamais redebiter. La
-- contrainte unique sur commande_id est le vrai garde-fou (pas seulement une
-- verification applicative) - meme principe que le verrou pessimiste cote
-- commande pour la meme famille de probleme (concurrence entre transitions).
create table operation_paiement (
    id bigserial primary key,
    commande_id bigint not null unique,
    montant numeric(12,2) not null,
    date_traitement timestamptz not null
);
