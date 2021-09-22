create table CJM(
	id int not null auto_increment,
	cjm_name varchar(255) not null,
	alias varchar(16) not null,
	regions varchar(32) not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table AUDITORSHIP(
	id int not null auto_increment,
	auditorship_name varchar(255) not null,
	cjm_id int not null,
    primary key (id),
	constraint FK_AUDITORSHIP_CJM foreign key (cjm_id)
    references CJM(id)
) engine=InnoDB default charset=utf8mb4;

create table CJM_USER(
	id int not null auto_increment,
    username varchar(20) not null unique,
    email varchar(64) unique,
    user_password varchar(255) not null,
    is_credentials_expired bit not null default 0,
    auditorship_id int not null,
    primary key (id),
    constraint FK_CJM_USER_AUDITORSHIP foreign key (auditorship_id)
    references AUDITORSHIP (id)
) engine=InnoDB default charset=utf8mb4;

create table ARMY(
	id int not null auto_increment,
	army_name varchar(64) not null unique,
    army_alias varchar(10) not null unique,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table GROUP_USER(
	id int not null auto_increment,
    username varchar(20) not null unique,
    email varchar(64) unique,
    user_password varchar(255) not null,
    is_credentials_expired bit not null default 0,
    cjm_id int not null,
    army_id int not null,
    primary key (id),
    constraint FK_GROUP_USER_CJM foreign key (cjm_id) 
    references CJM (id),
    constraint FK_GROUP_USER_ARMY foreign key (army_id)
    references ARMY (id)
) engine=InnoDB default charset=utf8mb4;

create table JUSTICE_COUNCIL(
	id int not null auto_increment,
    council_name varchar(128) not null unique,
    council_alias varchar(16) not null unique,
    council_size tinyint unsigned not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table MILITARY_BASE(
	id int not null auto_increment,
    military_base_name varchar(128) not null unique,
    military_base_alias varchar(16) not null unique,
    army_id int not null,
    primary key (id),
    constraint FK_MILITARY_BASE_ARMY foreign key (army_id)
    references ARMY (id)
) engine=InnoDB default charset=utf8mb4;

create table MILITARY_SPECIALIZATION(
	id int not null auto_increment,
    specialization_name varchar(128) not null unique,
    specialization_alias varchar(16) not null unique,
    army_id int not null,
    primary key (id),
    constraint FK_MILITARY_SPECIALIZATION_ARMY foreign key (army_id)
    references ARMY (id)
) engine=InnoDB default charset=utf8mb4;

create table MILITARY_RANK(
	id int not null auto_increment,
    rank_name varchar(64) not null unique,
    rank_alias varchar(16) not null unique,
    rank_weight tinyint unsigned not null,
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table ARMY_HAS_MILITARY_RANK(
	army_id int not null,
    military_rank_id int not null,
    primary key (army_id, military_rank_id),
    constraint FK_ARMY_HAS_RANK_MILITARY_RANK foreign key (military_rank_id)
    references MILITARY_RANK (id)
) engine=InnoDB default charset=utf8mb4;

create table SOLDIER(
	id int not null auto_increment,
    soldier_name varchar(64) not null unique,
    phone varchar(32),
    email varchar(64) unique,
    enabled_for_draw bit not null default 1,
    army_id int not null,
    military_base_id int not null,
    military_specialization_id int,
    military_rank_id int not null,
    cjm_id int not null,
    primary key (id),
    constraint SOLDIER_ARMY foreign key (army_id)
    references ARMY (id),
    constraint SOLDIER_MILITARY_BASE foreign key (military_base_id)
    references MILITARY_BASE (id),
    constraint SOLDIER_MILITARY_SPECIALIZATION foreign key (military_specialization_id)
    references MILITARY_SPECIALIZATION (id),
    constraint SOLDIER_MILITARY_RANK foreign key (military_rank_id)
    references MILITARY_RANK (id),
    constraint SOLDIER_CJM foreign key (cjm_id)
    references CJM (id)
) engine=InnoDB default charset=utf8mb4;

create table DRAW_EXCLUSION(
	id int not null auto_increment,
    start_date date not null,
	end_date date not null,
    creation_date datetime not null,
    message varchar(10240) not null,
    group_user_id int not null,
    soldier_id int not null,
    primary key (id),
    constraint FK_DRAW_EXCLUSION_GROUP_USER foreign key (group_user_id)
    references GROUP_USER (id),
    constraint FK_DRAW_EXCLUSION_SOLDIER foreign key (soldier_id)
    references SOLDIER (id)
) engine=InnoDB default charset=utf8mb4;

create table DRAW(
	id int not null auto_increment,
    draw_date datetime not null,
    quarter tinyint,
    year smallint,
    process_number varchar(64) unique,
    finished bit not null default 1,
    justice_council_id int not null,
    cjm_user_id int not null, 
    army_id int not null,
    soldier_substitute_id int,
    primary key (id),
    constraint FK_DRAW_JUSTICE_COUNCIL foreign key (justice_council_id)
    references JUSTICE_COUNCIL (id),
    constraint FK_DRAW_CJM_USER foreign key (cjm_user_id)
    references CJM_USER (id),
    constraint FK_DRAW_ARMY foreign key (army_id)
    references ARMY (id),
    constraint FK_DRAW_SOLDIER foreign key (soldier_substitute_id)
    references SOLDIER (id)
) engine=InnoDB default charset=utf8mb4;

create table DRAW_HAS_SOLDIER(
	draw_id int not null,
    soldier_id int not null,
    constraint FK_DRAW_HAS_SOLDIER_DRAW foreign key (draw_id)
    references DRAW (id),
    constraint FK_DRAW_HAS_SOLDIER_SOLDIER foreign key (soldier_id)
    references SOLDIER (id)
) engine=InnoDB default charset=utf8mb4;
