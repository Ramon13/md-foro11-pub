ALTER TABLE CJM_USER ADD COLUMN permission_level int not null default 1;
ALTER TABLE GROUP_USER ADD COLUMN permission_level int not null default 1;

ALTER TABLE GROUP_USER RENAME COLUMN is_credentials_expired TO credentials_expired;
ALTER TABLE CJM_USER RENAME COLUMN is_credentials_expired TO credentials_expired;