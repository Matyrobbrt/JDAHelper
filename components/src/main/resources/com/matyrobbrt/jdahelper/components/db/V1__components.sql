create table components
(
    feature   text      not null,
    id        text      not null,
    arguments text      not null,
    lifespan  bigint    not null,
    last_used timestamp not null,
    constraint pk_components primary key (feature, id)
);