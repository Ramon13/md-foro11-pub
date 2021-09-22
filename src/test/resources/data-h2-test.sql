/**
//test data init
INSERT INTO ARMY (name, name_alias) VALUES ('Exército Brasileiro', 'EB');
INSERT INTO CJM (name, alias, regions) VALUES ('11ª circunscrição judiciária militar', '11CJM', 'DF, GO e TO');
//test data end


INSERT INTO ARMY (name, name_alias) VALUES ('Exército Brasileiro', 'EB');
INSERT INTO ARMY (name, name_alias) VALUES ('Marinha Do Brasil', 'MB');
INSERT INTO ARMY (name, name_alias) VALUES ('Força Aérea Brasileira', 'FAB');

INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Centro de Controle Interno do Exército', 'CCIEX', 1);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Estado-Maior do Exército', 'EME', 1);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Diretoria de Gestão Orçamentária', 'DGO', 1);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Comando Logístico', 'COLOG', 1);

INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('BATALHÃO DE INFANTARIA DA AERONÁUTICA ESPECIAL DE BRASÍLIA', 'BINFAE-BR', 3);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('CENTRO DE COMPUTAÇÃO DA AERONÁUTICA DE BRASÍLIA', 'CCA-BR', 3);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('CENTRO DE CONTROLE INTERNO DA AERONÁUTICA', 'CENCIAR', 3);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('CENTRO DE INVESTIGAÇÃO E PREVENÇÃO DE ACIDENTES AERONÁUTICOS', 'CENIPA', 3);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Comando de Defesa Cibernética', 'COMDCIBER', 3);

INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Centro de Comunicação Social da Marinha', 'CCSM', 2);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Capitania Fluvial de Brasília', 'CFB', 2);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Centro de Instrução e Adestramento de Brasília Almirante Domingos de Mattos Cortez', 'CIAB', 2);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Centro de Inteligência da Marinha', 'CIM', 2);
INSERT INTO MILITARY_ORGANIZATION (name, name_alias, army_id) VALUES ('Comando do 7º Distrito Naval', 'Com7ºDN', 2);

INSERT INTO MILITARY_RANK (name, name_alias) VALUES ('Marechal-do-Ar', 'Mar Ar');
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (3, 1);
INSERT INTO MILITARY_RANK (name, name_alias) VALUES ('Tenente-Brigadeiro', 'Mar Brig');
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (3, 2);

INSERT INTO MILITARY_RANK (name, name_alias) VALUES ('General-de-Divisão', 'Gen Div');
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (1, 3);
INSERT INTO MILITARY_RANK (name, name_alias) VALUES ('General-de-Brigada', 'Gen Bda');
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (1, 4);

INSERT INTO MILITARY_RANK (name, name_alias) VALUES ('Capitão-de-Mar-e-Guerra', 'CMG');
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (2, 5);
INSERT INTO MILITARY_RANK (name, name_alias) VALUES ('Capitão-de-Fragata', 'CF');
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (2, 6);

INSERT INTO MILITARY_RANK (name, name_alias) VALUES ('Coronel', 'Cel');
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (1, 7);
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (3, 7);
INSERT INTO MILITARY_RANK (name, name_alias) VALUES ('Tenente-Coronel', 'Ten Cel');
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (1, 8);
INSERT INTO ARMY_HAS_MILITARY_RANK (army_id, military_rank_id) VALUES (3, 8);
**/