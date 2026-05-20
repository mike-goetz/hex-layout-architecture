create sequence app_permission_seq start with 1 increment by 50;
create sequence app_role_seq start with 1 increment by 50;
create sequence app_user_seq start with 1 increment by 50;
create sequence audit_log_seq start with 1 increment by 50;
create sequence contact_seq start with 1 increment by 50;
create sequence customer_profile_seq start with 1 increment by 50;
create sequence holiday_booking_seq start with 1 increment by 50;
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
alter table if exists app_permission add constraint FK_PERMISSION__CREATED_BY foreign key (created_by_id) references app_user;
alter table if exists app_permission add constraint FK_PERMISSION__MODIFIED_BY foreign key (last_modified_by_id) references app_user;
alter table if exists app_role add constraint FK_ROLE__CREATED_BY foreign key (created_by_id) references app_user;
alter table if exists app_role add constraint FK_ROLE__MODIFIED_BY foreign key (last_modified_by_id) references app_user;
alter table if exists app_role_permission add constraint FK_ROLE_PREMISSIONS__REMISSION foreign key (permission_id) references app_permission;
alter table if exists app_role_permission add constraint FK_ROLE_PREMISSIONS__ROLE foreign key (role_id) references app_role;
alter table if exists app_user add constraint FK_USER__CONTACT foreign key (contact_id) references contact;
alter table if exists app_user add constraint FK_USER__CREATED_BY foreign key (created_by_id) references app_user;
alter table if exists app_user add constraint FK_USER__MODIFIED_BY foreign key (last_modified_by_id) references app_user;
alter table if exists app_user_role add constraint FK_USER_ROLES__ROLE foreign key (role_id) references app_role;
alter table if exists app_user_role add constraint FK_USER_ROLES__USER foreign key (user_id) references app_user;
alter table if exists audit_log add constraint FK_AUDIT_LOG__CREATED_BY foreign key (created_by_id) references app_user;
alter table if exists audit_log add constraint FK_AUDIT_LOG__MODIFIED_BY foreign key (last_modified_by_id) references app_user;
alter table if exists contact add constraint FK_CONTACT__CREATED_BY foreign key (created_by_id) references app_user;
alter table if exists contact add constraint FK_CONTACT__MODIFIED_BY foreign key (last_modified_by_id) references app_user;
alter table if exists customer_profile add constraint FK_CUSTOMER_PROFILE__CREATED_BY foreign key (created_by_id) references app_user;
alter table if exists customer_profile add constraint FK_CUSTOMER_PROFILE__MODIFIED_BY foreign key (last_modified_by_id) references app_user;
alter table if exists holiday_booking add constraint FK_HOLIDAY_BOOKING__CREATED_BY foreign key (created_by_id) references app_user;
alter table if exists holiday_booking add constraint FK_HOLIDAY_BOOKING__MODIFIED_BY foreign key (last_modified_by_id) references app_user;
alter table if exists holiday_booking_addon add constraint FK_HOLIDAY_BOOKING__ADDON foreign key (booking_id) references holiday_booking;
