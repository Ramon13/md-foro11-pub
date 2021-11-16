create table DRAW_LIST(
	id int not null auto_increment,
	description varchar(1024) not null,
	creation_date date not null,
	update_date date not null,
	quarter_year varchar(6) not null,
	army_id int not null,
	creation_user_id int not null,
	primary key(id),
	constraint DRAW_LIST_ARMY_FK foreign key (army_id)
	references ARMY (id),
	constraint DRAW_LIST_GROUP_USER_FK foreign key (creation_user_id)
	references GROUP_USER (id)
)engine=InnoDB default charset=utf8mb4;

create table DRAW_LIST_HAS_SOLDIER(
	draw_list_id int not null,
	soldier_id int not null,
	primary key (draw_list_id, soldier_id),
	constraint FK_DRAW_LIST_HAS_SOLDIER_SOLDIER foreign key (soldier_id)
	references SOLDIER (id)
)engine=InnoDB default charset=utf8mb4;


ALTER TABLE SOLDIER DROP COLUMN enabled_for_draw;

ALTER TABLE DRAW DROP COLUMN quarter;
ALTER TABLE DRAW DROP COLUMN year;
ALTER TABLE DRAW DROP COLUMN draw_date;

ALTER TABLE DRAW ADD COLUMN creation_date date not null default '2021-01-01';
ALTER TABLE DRAW ADD COLUMN update_date date not null default '2021-01-01';

ALTER TABLE DRAW ADD COLUMN draw_list_id int; 
ALTER TABLE DRAW ADD CONSTRAINT FK_DRAW_DRAW_LIST foreign key (draw_list_id) references DRAW_LIST (id);