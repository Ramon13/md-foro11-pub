ALTER TABLE CJM_USER ADD COLUMN permission_level int not null default 1;
ALTER TABLE GROUP_USER ADD COLUMN permission_level int not null default 1;

ALTER TABLE GROUP_USER RENAME COLUMN is_credentials_expired TO credentials_expired;
ALTER TABLE CJM_USER RENAME COLUMN is_credentials_expired TO credentials_expired;

ALTER TABLE CJM_USER ADD COLUMN active bit not null default 1;
ALTER TABLE GROUP_USER ADD COLUMN active bit not null default 1;

ALTER TABLE SOLDIER ADD COLUMN active bit not null default 1;
ALTER TABLE DRAW_LIST ADD COLUMN active bit not null default 1;
