UPDATE md_foro11.DRAW d1
   SET creation_date=(SELECT draw_date FROM md_foro11_dmp.DRAW d2 WHERE d1.id= d2.id),
   update_date = creation_date;
    
insert into md_foro11.DRAW_LIST (id, description, creation_date, update_date, quarter_year, army_id, creation_user_id)
	values (1,'Relação 3º trimestre 2021', '2021-09-01', '2021-09-01', '3/2021', 1, 1);    
update md_foro11.DRAW set draw_list_id = 1 where id in (2, 5, 6, 9);

insert into md_foro11.DRAW_LIST (id, description, creation_date, update_date, quarter_year, army_id, creation_user_id)
	values (2, 'Relação 3º trimestre 2021', '2021-09-01', '2021-09-01', '3/2021', 2, 2);        
update md_foro11.DRAW set draw_list_id = 2 where id in (3);

insert into md_foro11.DRAW_LIST (id, description, creation_date, update_date, quarter_year, army_id, creation_user_id)
	values (3, 'Relação 3º trimestre 2021', '2021-09-01', '2021-09-01', '3/2021', 3, 3);    
update md_foro11.DRAW set draw_list_id = 3 where id in (1, 4, 7, 8);
  
    
    
    
insert into md_foro11.DRAW_LIST (id, description, creation_date, update_date, quarter_year, army_id, creation_user_id)
	values (4, 'Relação 4º trimestre 2021', '2021-10-01', '2021-10-01', '4/2021', 1, 1);
update md_foro11.DRAW set draw_list_id = 4 where id in (10, 14);
    
insert into md_foro11.DRAW_LIST (id, description, creation_date, update_date, quarter_year, army_id, creation_user_id)
	values (5, 'Relação 4º trimestre 2021', '2021-10-01', '2021-10-01', '4/2021', 2, 2); 
update md_foro11.DRAW set draw_list_id = 5 where id in (11, 13);    
    
insert into md_foro11.DRAW_LIST (id, description, creation_date, update_date, quarter_year, army_id, creation_user_id)
	values (6, 'Relação 4º trimestre 2021', '2021-10-01', '2021-10-01', '4/2021', 3, 3);        
update md_foro11.DRAW set draw_list_id = 6 where id in (12, 15);


insert into md_foro11.DRAW_LIST_HAS_SOLDIER (draw_list_id, soldier_id)
	select 1, id from md_foro11.SOLDIER where army_id = 1;

insert into md_foro11.DRAW_LIST_HAS_SOLDIER (draw_list_id, soldier_id)
	select 4, id from md_foro11.SOLDIER where army_id = 1;    
    
    
insert into md_foro11.DRAW_LIST_HAS_SOLDIER (draw_list_id, soldier_id)
	select 2, id from md_foro11.SOLDIER where army_id = 2;        
    
insert into md_foro11.DRAW_LIST_HAS_SOLDIER (draw_list_id, soldier_id)
	select 5, id from md_foro11.SOLDIER where army_id = 2;        
    
insert into md_foro11.DRAW_LIST_HAS_SOLDIER (draw_list_id, soldier_id)
	select 3, id from md_foro11.SOLDIER where army_id = 3;        
    
insert into md_foro11.DRAW_LIST_HAS_SOLDIER (draw_list_id, soldier_id)
	select 6, id from md_foro11.SOLDIER where army_id = 3;            