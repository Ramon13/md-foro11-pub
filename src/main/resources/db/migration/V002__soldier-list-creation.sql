create table DRAW_LIST(
	id int not null auto_increment,
	description varchar(1024) not null,
	creation_date date not null,
	update_date date not null,
	quarter_year varchar(6) not null,
	army_id int not null,
	primary key(id),
	constraint DRAW_LIST_ARMY_FK foreign key (army_id)
	references ARMY (id)
)engine=InnoDB default charset=utf8mb4;

create table DRAW_LIST_HAS_SOLDIER(
	draw_list_id int not null,
	soldier_id int not null,
	primary key (draw_list_id, soldier_id),
	constraint FK_DRAW_LIST_HAS_SOLDIER_SOLDIER foreign key (soldier_id)
	references SOLDIER (id)
)engine=InnoDB default charset=utf8mb4;