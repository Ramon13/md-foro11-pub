#safe delete, duplicate soldier founded on old database during tests
delete from ebdb.SOLDIER s where s.id = 1306;