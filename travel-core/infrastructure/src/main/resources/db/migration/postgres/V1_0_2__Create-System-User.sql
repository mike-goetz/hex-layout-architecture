INSERT INTO app_user (id, version, created_date, last_modified_date, username)
VALUES (nextval('app_user_seq'), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM');
