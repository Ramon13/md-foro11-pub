ALTER TABLE CJM_USER ADD COLUMN permission_level int not null default 1;
ALTER TABLE GROUP_USER ADD COLUMN permission_level int not null default 1;

ALTER TABLE GROUP_USER RENAME COLUMN is_credentials_expired TO credentials_expired;
ALTER TABLE CJM_USER RENAME COLUMN is_credentials_expired TO credentials_expired;

ALTER TABLE CJM_USER ADD COLUMN active bit not null default 1;
ALTER TABLE GROUP_USER ADD COLUMN active bit not null default 1;

ALTER TABLE SOLDIER ADD COLUMN active bit not null default 1;
ALTER TABLE SOLDIER DROP INDEX soldier_name;
ALTER TABLE SOLDIER DROP INDEX email;

ALTER TABLE DRAW_LIST ADD COLUMN active bit not null default 1;
ALTER TABLE DRAW_LIST ADD COLUMN enable_for_draw bit not null default 0;
ALTER TABLE DRAW_LIST MODIFY quarter_year varchar(6) not null;
ALTER TABLE DRAW_LIST RENAME COLUMN quarter_year TO year_quarter;