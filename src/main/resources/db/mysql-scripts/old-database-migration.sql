insert into CJM (id, cjm_name, alias, regions) select id, name, alias, regions from ebdb.CJM;


insert into AUDITORSHIP (id, auditorship_name, cjm_id) select id, name, cjm_id from ebdb.AUDITORSHIP;


insert into CJM_USER (id, username, email, user_password, is_credentials_expired, auditorship_id)
	select id, username, email, password, is_credentials_expired, auditorship_id from ebdb.CJM_USER;

	
insert into ARMY (id, army_name, army_alias) select id, name, name_alias from ebdb.ARMY;


insert into GROUP_USER (id, username, email, user_password, is_credentials_expired, cjm_id, army_id)
	select id, username, email, password, is_credentials_expired, cjm_id, army_id from ebdb.GROUP_USER;

	
insert into JUSTICE_COUNCIL (id, council_name, council_alias, council_size)
	select id, name, alias, council_size from ebdb.JUSTICE_COUNCIL;

	
insert into MILITARY_BASE (id, military_base_name, military_base_alias, army_id)
	select id, name, name_alias, army_id from ebdb.MILITARY_ORGANIZATION;

	
insert into MILITARY_SPECIALIZATION (id, specialization_name, specialization_alias, army_id)
	select id, name, name_alias, army_id from ebdb.MILITARY_SPECIALIZATION;

	
insert into MILITARY_RANK (id, rank_name, rank_alias, rank_weight)
	select id, name, name_alias, rank_weight from ebdb.MILITARY_RANK;
    
	
insert into ARMY_HAS_MILITARY_RANK (army_id, military_rank_id)
	select army_id, military_rank_id from ebdb.ARMY_HAS_MILITARY_RANK;
   

insert into SOLDIER (id, soldier_name, phone, email, enabled_for_draw, army_id, military_base_id, military_specialization_id, military_rank_id, cjm_id)
	select id, name, phone, email, enabled_for_draw, army_id, military_organization_id, military_specialization_id, military_rank_id, cjm_id 
	from ebdb.SOLDIER;

	
insert into DRAW_EXCLUSION (id, start_date, end_date, creation_date, message, group_user_id, soldier_id)
	select id, start_date, end_date, creation_date, message, group_user_id, soldier_id from ebdb.DRAW_EXCLUSION;
    
	
insert into DRAW (id, draw_date, quarter, year, process_number, finished, justice_council_id, cjm_user_id, army_id, soldier_substitute_id)
	select id, draw_date, quarter, year, process_number, finished, justice_council_id, cjm_user_id, army_id, soldier_substitute_id
	from ebdb.DRAW;

	
insert into DRAW_HAS_SOLDIER (draw_id, soldier_id)
	select draw_id, soldier_id from ebdb.DRAW_HAS_SOLDIER;