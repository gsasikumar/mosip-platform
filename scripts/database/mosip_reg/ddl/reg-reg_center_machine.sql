-- create table section -------------------------------------------------
-- schema 		: reg	   			 - Registration Module
-- table 		: reg_center_machine - MOSIP Registration center and Machine mapping
-- table alias  : cntrmac	

-- schemas section -------------------------------------------------

-- create schema if reg schema for Registration Module is not exists
create schema if not exists reg
;
 
-- table section -------------------------------------------------
create table reg.reg_center_machine (

	regcntr_id 	character varying (10) not null,  -- reg.registration_center.id
	machine_id 	character varying (10) not null,  -- reg.machine_master.id

	lang_code 	character varying (3) not null ,  -- master.language.code

	is_active 	boolean not null,
	cr_by 		character varying (32) not null,
	cr_dtimes	timestamp not null,
	upd_by  	character varying (32),
	upd_dtimes  timestamp,
	is_deleted 	boolean,
	del_dtimes  timestamp

)
;

-- keys section -------------------------------------------------
 alter table reg.reg_center_machine add constraint pk_cntrmac_usr_id primary key (regcntr_id, machine_id)
 ;

-- indexes section -------------------------------------------------
-- create index idx_cntrmac_<colX> on reg.reg_center_machine (colX )
-- ;


