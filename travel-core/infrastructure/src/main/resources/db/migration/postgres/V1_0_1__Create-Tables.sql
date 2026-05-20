create table app_permission
(
    active              boolean                     not null,
    deleted             boolean                     not null,
    sort_order          integer,
    created_by_id       bigint,
    created_date        timestamp(6) with time zone not null,
    id                  bigint                      not null,
    last_modified_by_id bigint,
    last_modified_date  timestamp(6) with time zone,
    version             bigint                      not null,
    uid                 varchar(255)                not null unique,
    primary key (id)
);
create table app_role
(
    active              boolean                     not null,
    deleted             boolean                     not null,
    sort_order          integer,
    created_by_id       bigint,
    created_date        timestamp(6) with time zone not null,
    id                  bigint                      not null,
    last_modified_by_id bigint,
    last_modified_date  timestamp(6) with time zone,
    version             bigint                      not null,
    uid                 varchar(255)                not null unique,
    primary key (id)
);
create table app_role_permission
(
    permission_id bigint not null,
    role_id       bigint not null,
    primary key (permission_id, role_id)
);
create table app_user
(
    contact_id          bigint unique,
    created_by_id       bigint,
    created_date        timestamp(6) with time zone not null,
    id                  bigint                      not null,
    last_modified_by_id bigint,
    last_modified_date  timestamp(6) with time zone,
    version             bigint                      not null,
    password            varchar(255),
    username            varchar(255)                not null,
    primary key (id),
    constraint UK_USER__USERNAME unique (username)
);
create table app_user_role
(
    role_id bigint not null,
    user_id bigint not null,
    primary key (role_id, user_id)
);
create table audit_log
(
    created_by_id       bigint,
    created_date        timestamp(6) with time zone not null,
    id                  bigint                      not null,
    last_modified_by_id bigint,
    last_modified_date  timestamp(6) with time zone,
    version             bigint                      not null,
    message             varchar(1000)               not null,
    severity            varchar(255)                not null,
    primary key (id)
);
create table contact
(
    created_by_id       bigint,
    created_date        timestamp(6) with time zone not null,
    id                  bigint                      not null,
    last_modified_by_id bigint,
    last_modified_date  timestamp(6) with time zone,
    version             bigint                      not null,
    email               varchar(255),
    firstname           varchar(255),
    lastname            varchar(255),
    primary key (id)
);
create table customer_profile
(
    vip                 boolean                     not null,
    created_by_id       bigint,
    created_date        timestamp(6) with time zone not null,
    id                  bigint                      not null,
    last_modified_by_id bigint,
    last_modified_date  timestamp(6) with time zone,
    version             bigint                      not null,
    customer_id         varchar(255),
    uid                 varchar(255)                not null unique,
    primary key (id)
);
create table holiday_booking
(
    total_price            numeric(38, 2),
    created_by_id          bigint,
    created_date           timestamp(6) with time zone not null,
    id                     bigint                      not null,
    last_modified_by_id    bigint,
    last_modified_date     timestamp(6) with time zone,
    version                bigint                      not null,
    customer_id            varchar(255),
    destination            varchar(255),
    flight_confirmation_id varchar(255),
    hotel_confirmation_id  varchar(255),
    status                 varchar(255),
    uid                    varchar(255)                not null unique,
    primary key (id)
);
create table holiday_booking_addon
(
    booking_id bigint not null,
    add_on     varchar(255)
);
