-- Table attendue par spring-modulith-starter-jpa : son registre de publication
-- d'evenements (event publication registry), utilise plus tard pour la reprise
-- apres panne (rejouer les evenements non traites). Schema genere via
-- l'export DDL de Hibernate a partir de l'entite JpaEventPublication, pour
-- garantir une correspondance exacte avec ce qu'attend Modulith.
create table event_publication (
    id uuid not null,
    listener_id varchar(255) not null,
    event_type varchar(255) not null,
    serialized_event varchar(255) not null,
    status varchar(255) check (status in ('PUBLISHED', 'PROCESSING', 'COMPLETED', 'FAILED', 'RESUBMITTED')),
    publication_date timestamp(6) with time zone not null,
    completion_date timestamp(6) with time zone,
    last_resubmission_date timestamp(6) with time zone,
    completion_attempts integer not null,
    primary key (id)
);
