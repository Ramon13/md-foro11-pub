ALTER TABLE CJM_USER ADD COLUMN `recovery_token` VARCHAR(32) DEFAULT null;
ALTER TABLE GROUP_USER ADD COLUMN `recovery_token` VARCHAR(32) DEFAULT null;