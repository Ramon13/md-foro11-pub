use md_foro11;
#CJM
insert into CJM (id, cjm_name, alias, regions) 
	values
		(1, '1ª Circunscrição Judiciária Militar', '1ª CJM', 'RJ e ES'), 
		(2, '2ª Circunscrição Judiciária Militar', '2ª CJM', 'SP');
	
#Auditorship
insert into AUDITORSHIP (id, auditorship_name, cjm_id)
	values 
		(1, '1ª Auditoria da 1ª CJM', 1),
		(2, '2ª Auditoria da 1ª CJM', 1),
		(3, '1ª Auditoria da 2ª CJM', 2);
		
#justice council
insert into JUSTICE_COUNCIL (id, council_name, council_alias, council_size)
	values
		(1, 'Conselho Permanente de Justiça', 'CPJ', 5),
		(2, 'Conselho Especial de Justiça', 'CEJ', 4);
	
#Army
insert into ARMY (id, army_name, army_alias)
	values 
		(1, 'Exército Brasileiro', 'EB'),
		(2, 'Marinha Do Brasil', 'MB'),
		(3, 'Força Aérea Brasileira', 'FAB');

#Rank
insert into MILITARY_RANK (id, rank_alias, rank_name, rank_weight)
	values 
		(1, 'CEL', 'Coronel', 5),
		(2, 'TEN CEL', 'Tenente-Coronel', 6),
		(3, 'MAJ', 'Major', 7),
		(4, 'CAP', 'Capitão', 8),
		(5, '1º TEN', 'Primeiro-Tenente', 9),
		(6, '2º TEN', 'Segundo-Tenente', 10),
		(7, 'CMG', 'Capitão-de-Mar-e-Guerra', 5),
		(8, 'CF', 'Capitão-de-Fragata', 6),
		(9, 'CC', 'Capitão-de-Corveta', 7),
		(10, 'CT', 'Capitão-Tenente', 8);
		
#army_has_military_rank
insert into ARMY_HAS_MILITARY_RANK (army_id, military_rank_id)
	values
		(1,1), (3,1), (1,2), (3,2), (1,3), (3,3), (1,4), (3,4), (1,5), (2,5), (3,5), (1,6), (2,6),
		(3,6), (2,7), (2,8), (2,9), (2,10);
		
#military_base
insert into MILITARY_BASE (id, military_base_alias, military_base_name, army_id)
	values
		(1, '11°RM', '11ª Região Militar', 1),
		(2, '11º GAAAE', '11º Grupo de Artilharia Antiaérea', 1),
		(3, '11ºDSUP', '11º Depósito de Suprimento', 1),
		(4, '1ºRCG', '1º Regimento de Cavalaria de Guardas', 1),
		(5, '2ºCGEO', '2º Centro de Geoinformação', 1),
		(6, 'CCSM', 'Centro de Comunicação Social da Marinha', 2),
		(7, 'CFB', 'Capitania Fluvial de Brasília', 2),
		(8, 'CIAB', 'Centro de Instrução e Adestramento de Brasília', 2),
		(9, 'CIM', 'Centro de Inteligência da Marinha', 2),
		(10, 'Com7ºDN', 'Comando do 7º Distrito Naval', 2),
		(11, '1º BDAAE ', 'PRIMEIRA BRIGADA DE DEFESA ANTIAÉREA', 3),
		(12, 'ALA 1', 'Área Militar do Aeroporto Internacional de Brasília', 3),
		(13, 'CCA-BR', 'Centro de Computação da Aeronáutica de Brasília', 3),
		(14, 'CENCIAR', 'Centro de Controle Interno da Aeronáutica', 3),
		(15, 'CENIPA', 'Centro de Investigação e Prevenção de Acidentes Aeronáuticos', 3);
		
#soldiers EB
insert into SOLDIER (id, email, enabled_for_draw, soldier_name, phone, army_id, cjm_id, military_base_id,
	military_rank_id, military_specialization_id)
	values
		(1, 'secretaria@11gaaae.eb.mil.br', 1, 'DERCI CASEMIRO NETTO', '31 99391-4842', 1, 1, 2, 1, null),
		(2, null, 1, 'JOHNY MIRANDA DE SOUZA MARTINS', '61 99146-7067', 1, 1, 2, 1, null),
		(3, 'marzinho5@hotmail.com', 1, 'MARCOS DE SOUZA MARTINS', null , 1, 1, 1, 2, null),
		(4, null, 1, 'FRANCISCO F DE SOUSA IBIAPINA', '61 2035-2238', 1, 1, 3, 2, null ),
		(5, 'reldesdeandrade@yahoo.com.br', 1, 'RELDES PEREIRA DE ANDRADE', '61 98101-6875', 1, 1, 4, 3, null),
		(6, 'stlima@gabcmt.eb.mil.br', 1, 'EVALDO JOSÉ PEREIRA DE LIMA', '61 98314-4520', 1, 1, 2, 3, null),
		(7, 'edqualemane@bol.com.br', 1, 'EDMILSON MANOEL MARTINS', '67 99251-9572', 1, 1, 3, 4, null),
		(8, null, 1, 'ORLAN RIBEIRO DE ALMEIDA JUNIOR', '61 99456-2148', 1, 1, 4, 4, null),
		(9, 'r.dartagnan.s.dias@gmail.com', 1, 'RICARDO DARTAGNAN SOARES DIAS', '12 98210-4981', 1, 1, 2, 5, null),
		(10, 'elviton@gmail.com', 1, 'ELVITON SOLENY GOMES PACHECO', '55 99943-9099', 1, 1, 4, 5, null);

#soldiers MB
insert into SOLDIER (id, email, enabled_for_draw, soldier_name, phone, army_id, cjm_id, military_base_id,
	military_rank_id, military_specialization_id)
	values
		(11, 'joselio@marinha.mil.br', 1, 'JOSÉLIO VIEIRA DOS SANTOS', '(61) 9 9807 9284', 2, 1, 6, 5, null),
		(12, 'davidson.david@marinha.mil.br', 1, 'DAVIDSON JUARÊZ DAVID', '(61) 9 8103 4275', 2, 1, 6, 5, null),
		(13, 'joao.moraes@marinha.mil.br', 1, 'JOÃO JOSÉ BAPTISTA DE MORAES', '(21) 9 9193-0205', 2, 1, 7, 5, null),
		(14, null, 1, 'PEDRO LUCAS LOPES PRAXEDES', '(61)3429-1173', 2, 1, 7, 5, null),
		(15, null, 1, 'HYAGO LUIZ TEIXEIRA PINTO', '(61)3429-1173', 2, 2, 8, 5, null),
		(16, 'raphael.amaral@marinha.mil.br', 1, 'RAPHAEL DE ALBUQUERQUE DO AMARAL', '(22) 99865-1604', 2, 2, 8, 5, null),
		(17, 'farah@marinha.mil.br', 1, 'CAIO PEREIRA FARAH NOLASCO', '(21)99736-5766', 2, 2, 9, 5, null),
		(18, 'sa.freire@marinha.mil.br', 1, 'RAFAEL SÁ FREIRE DIAS', '(21) 9 8074-2842', 2, 2, 9, 6, null),
		(19, 'paula.gonçalves@marinha.mil.br', 1, 'ANTONIO DE BARCELLOS NETO', '3429-1025', 2, 2, 10, 9, null),
		(20, null, 1, 'WANDERSON MORAIS RAMOS', '3429-1825', 2, 2, 10, 9, null);

#soldiers FAB
insert into SOLDIER (id, email, enabled_for_draw, soldier_name, phone, army_id, cjm_id, military_base_id,
	military_rank_id, military_specialization_id)
	values		
		(21, null, 1, 'IAGO VIEIRA DE OLIVEIRA', '61 98288-1416', 3, 1, 11, 5, null),
		(22, null, 1, 'KAREN DE OLIVEIRA VALVASSORI', '61 99175-9719', 3, 1, 11, 5, null),
		(23, null, 1, 'GUSTAVO DA MATA PETROVICH', '61 99556-7651', 3, 1, 12, 5, null),
		(24, null, 1, 'CHARLES JOSÉ DE OLIVEIRA', '(61) 98114-5297', 3, 1, 12, 5, null),
		(25, null, 1, 'FELIPE DA SILVA FERNANES', '61 98333-3040', 3, 2, 13, 5, null),
		(26, null, 1, 'RENATO PEDERSOLI', '69 99261-9984', 3, 2, 13, 5, null),
		(27, null, 1, 'FERNANDO MAYA XAVIER', '61 99639-7042', 3, 2, 14, 5, null),
		(28, null, 1, 'ISABELLE CECÍLIA DE ANDRADE', '61 99378-2772', 3, 2, 14, 5, null),
		(29, null, 1, 'SHELLY GABRIELA LEAL', '61 98243-8988', 3, 2, 15, 5, null),
		(30, null, 1, 'MARCO AURÉLIO LEITE DE PAULA', '61 98179-1248', 3, 2, 15, 5, null);

insert into DRAW_LIST (id, creation_date, update_date, army_id, quarter_year, description)
	values
		(1, '2021-10-13', '2021-10-13', 1, '4/2021', 'Primeira lista exército 4º trimestre de 2021');
		
insert into DRAW_LIST_HAS_SOLDIER (draw_list_id, soldier_id)
	values
		(1, 1), (1, 2), (1, 3), (1, 4), (1, 5);

#cjm_user 1aud1cjm
insert into CJM_USER (id, username, email, user_password, is_credentials_expired, auditorship_id)
	values
		(1, 'admin1aud1', null, '{bcrypt}$2a$10$IgXSs3978BKQMSyrIRF/ROA8V7f/v.G.Zr0F2l1yLJGamjGbYBjXe', 0, 1);

#cjm_user 2aud1cjm
insert into CJM_USER (id, username, email, user_password, is_credentials_expired, auditorship_id)
	values
		(2, 'admin2aud1', null, '{bcrypt}$2a$10$IgXSs3978BKQMSyrIRF/ROA8V7f/v.G.Zr0F2l1yLJGamjGbYBjXe', 0, 2);		
		
#cjm_user 1aud2cjm		
insert into CJM_USER (id, username, email, user_password, is_credentials_expired, auditorship_id)
	values
		(3, 'admin1aud2', null, '{bcrypt}$2a$10$IgXSs3978BKQMSyrIRF/ROA8V7f/v.G.Zr0F2l1yLJGamjGbYBjXe', 0, 3);		

#group_user EB 1cjm		
insert into GROUP_USER (id, username, email, user_password, is_credentials_expired, cjm_id, army_id)
	values
		(1, 'ebadmin', null, '{bcrypt}$2a$10$IgXSs3978BKQMSyrIRF/ROA8V7f/v.G.Zr0F2l1yLJGamjGbYBjXe', 0, 1, 1);		

#group_user MB 1cjm		
insert into GROUP_USER (id, username, email, user_password, is_credentials_expired, cjm_id, army_id)
	values
		(2, 'mbadmin', null, '{bcrypt}$2a$10$IgXSs3978BKQMSyrIRF/ROA8V7f/v.G.Zr0F2l1yLJGamjGbYBjXe', 0, 1, 2);
		
#group_user FAB 2cjm		
insert into GROUP_USER (id, username, email, user_password, is_credentials_expired, cjm_id, army_id)
	values
		(3, 'fabadmin', null, '{bcrypt}$2a$10$IgXSs3978BKQMSyrIRF/ROA8V7f/v.G.Zr0F2l1yLJGamjGbYBjXe', 0, 2, 3);
		
#draw
#cpj 0 EB 1aud1cjm
insert into DRAW (id, draw_date, process_number, quarter, year, army_id, cjm_user_id, justice_council_id, soldier_substitute_id, finished)
	values
		(1, '2021-06-08 14:24:14', null, 3, 2021, 1, 1, 1, 1, 1);
		
insert into DRAW_HAS_SOLDIER (draw_id, soldier_id)
	values
		(1, 1), (1, 2), (1, 3), (1, 4), (1, 5);
		
		
#cej 0 MB 1aud1CJM
insert into DRAW (id, draw_date, process_number, quarter, year, army_id, cjm_user_id, justice_council_id, soldier_substitute_id, finished)
	values
		(2, '2021-09-08 14:24:14', 1232133-33, null, null, 2, 1, 2, null, 0);
		
insert into DRAW_HAS_SOLDIER (draw_id, soldier_id)
	values
		(2, 11), (2, 12), (2, 13), (2, 14);

#cpj 1 fab 1aud2cjm
insert into DRAW (id, draw_date, process_number, quarter, year, army_id, cjm_user_id, justice_council_id, soldier_substitute_id, finished)
	values
		(3, '2021-06-08 14:24:14', null, 3, 2021, 3, 3, 1, 21, 1);
		
insert into DRAW_HAS_SOLDIER (draw_id, soldier_id)
	values
		(3, 21), (3, 22), (3, 23), (3, 24), (3, 25);				
						
#cpj 2 EB 2aud1CJM
insert into DRAW (id, draw_date, process_number, quarter, year, army_id, cjm_user_id, justice_council_id, soldier_substitute_id, finished)
	values
		(4, '2021-06-08 14:24:14', null, 4, 2021, 1, 2, 1, 4, 0);
		
insert into DRAW_HAS_SOLDIER (draw_id, soldier_id)
	values
		(4, 4), (4, 5), (4, 6), (4, 7), (4, 10);