-- create table section -----------------------------------------------------------
-- schema 		: ida	- Id-Authentication 
-- table 		: uin	- Master - UIN table.  
-- table alias  : uin

-- schemas section -----------------------------------------------------------------

-- create schema if ida schema for Id-Authentication schema not exists
create schema if not exists ida
;

--- drop constraints if exists -----
alter table if exists ida.vid drop constraint if exists fk_vid_uin_ref_id ;

alter table if exists ida.indv_demographic drop constraint if exists fk_idemo_uin_ref_id;

alter table if exists ida.indv_biometric drop constraint if exists fk_ibio_uin_ref_id;

alter table if exists ida.vid drop constraint if exists pk_vid_id;

alter table if exists ida.indv_biometric drop constraint if exists pk_ibio_uin_ref_id;

alter table if exists ida.indv_demographic drop constraint if exists pk_idemo_uin_ref_id;

alter table if exists ida.auth_transaction drop constraint if exists pk_atrn_id;

alter table if exists ida.indv_demographic drop constraint if exists pk_idemo_uin_ref_id;

alter table if exists master.location drop constraint if exists pk_loc_code;

alter table if exists ida.uin drop constraint if exists pk_uin_id ;

--------------------------------------


-- table section --------------------------------------------------------------------
create table if not exists ida.uin (
	id character varying(28) not null, 
	uin_ref_id character varying(28) not null,
	
	is_active boolean not null,
	cr_by character varying (32) not null,
	cr_dtimes timestamp not null,
	upd_by  character varying (32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp
)
;

-- keys section ---------------------------------------------------------------------
alter table ida.uin add constraint pk_uin_id primary key (id) 
;
-- 

-- indexes section -------------------------------------------------------------------
create unique index if not exists idx_uin_uin_ref_id on ida.uin(uin_ref_id)
;

-- comments section ------------------------------------------------------------------- 
comment on table ida.uin is 'To store master list of UIN for Authentication'
;
comment on column ida.uin.uin_ref_id is 'Reference key, key to be used in other tables for UIN reference'
;

-- create table section -----------------------------------------------------------
-- schema 		: ida	- Id-Authentication 
-- table 		: vid	- Virtual ID for authentication
-- table alias  : vid

-- schemas section -----------------------------------------------------------------

-- create schema if ida schema for Id-Authentication schema not exists
create schema if not exists ida
;

-- table section --------------------------------------------------------------------
create table if not exists ida.vid (
	id character varying(28) not null,
	uin_ref_id character varying(28) not null,
	generated_dtimes timestamp,
	validation_retry_count smallint,
	expiry_dtimes timestamp,
	
	is_active boolean not null,
	cr_by character varying(32) not null,
	cr_dtimes timestamp not null,
	upd_by  character varying (32),
	upd_dtimes timestamp,
	is_deleted boolean,
	del_dtimes timestamp
)
;

-- keys section --------------------------------------------------------------------------

alter table ida.vid add constraint pk_vid_id primary key (id)
 ;

alter table ida.vid add constraint fk_vid_uin_ref_id foreign key (uin_ref_id) references 
ida.uin (uin_ref_id) on delete cascade on update cascade
;

-- indexes section ------------------------------------------------------------------------
-- create index idx_vid_<colX> on ida.vid(ColX)
-- ;

-- comments section ------------------------------------------------------------------------ 
comment on table ida.vid is 'To store generated list of Virtual IDs for Authentication'
;
comment on column ida.vid.id is 'Unique Virtual id generated by the system for authentication'
;

-- create table section -----------------------------------------------------------
-- schema 		: ida	- Id-Authentication 
-- table 		: auth_transaction - Authentication request details
-- table alias  : atrn

-- schemas section -----------------------------------------------------------------

-- create schema if ida schema for Id-Authentication schema not exists
create schema if not exists ida
;

-- table section --------------------------------------------------------------------
create table if not exists ida.auth_transaction (
	id character varying(28) not null,  			
	request_dtimes timestamp not null,
	response_dtimes timestamp not null,
	request_trn_id character varying(64),
	auth_type_code character varying(64) not null,       
	status_code character varying(64) not null,
	status_comment character varying(1024),
	--static_tkn_id  character varying(64),
	ref_id character varying(64),
	ref_id_type character varying(64),
	
	is_active boolean not null,
	cr_by character varying (32) not null,
	cr_dtimes timestamp not null,
	upd_by  character varying (32),
	upd_dtimes timestamp
)
;

-- keys section ---------------------------------------------------------------------------
alter table ida.auth_transaction add constraint pk_atrn_id primary key (id)
 ;


-- indexes section -----------------------------------------------------------------------
-- create index idx_atrn_<colX> on ida.auth_transaction(colX)
-- ;

-- comments section ---------------------------------------------------------------------- 
comment on table ida.auth_transaction is 'auth_transaction table is used to store all authentication request transaction details'
;

comment on column ida.auth_transaction.auth_type_code is 'Authentication type code referenced from master table type lists'
;

-- create table section -----------------------------------------------------------
-- schema 		: ida	- Id-Authentication 
-- table 		: indv_biometric	- Individuals biometric data used while authentication
-- table alias  : ibio

-- schemas section -----------------------------------------------------------------

-- create schema if ida schema for Id-Authentication schema not exists
create schema if not exists ida
;

-- table section -------------------------------------------------------------------
create table if not exists ida.indv_biometric (
	uin_ref_id character varying(28) not null,
	
	rh_thumb bytea, 
	rh_index bytea,
	rh_middle bytea,
	rh_ring bytea,
	rh_little bytea,
	
	lh_thumb bytea,
	lh_index bytea,
	lh_middle bytea,
	lh_ring bytea,
	lh_little bytea,
	
	left_iris bytea,
	right_iris bytea,
	
	photo bytea,
	
	is_active boolean not null,
	cr_by character varying (32) not null,
	cr_dtimes timestamp not null,
	upd_by  character varying (32),
	upd_dtimes timestamp
)
;

-- keys section -------------------------------------------------------------------
alter table ida.indv_biometric add constraint pk_ibio_uin_ref_id primary key (uin_ref_id)
 ;
 
alter table ida.indv_biometric add constraint fk_ibio_uin_ref_id foreign key (uin_ref_id) references ida.uin (uin_ref_id) on delete cascade on update cascade
;

-- indexes section -----------------------------------------------------------------
-- create index idx_ibio_biometric on ida.indv_biometric(biometric)
-- ;

-- comments section ---------------------------------------------------------------- 
comment on table ida.indv_biometric is 'indv_biometric table is to store indivisuals biometric details'
;

-- create table section -----------------------------------------------------------
-- schema 		: ida	- Id-Authentication 
-- table 		: indv_demographic - Individuals demographic details
-- table alias  : idemo

-- schemas section -----------------------------------------------------------------

-- create schema if ida schema for Id-Authentication schema not exists
create schema if not exists ida
;

-- table section ---------------------------------------------------------------------
create table if not exists ida.indv_demographic (

		uin_ref_id character varying(28) not null,
	
		firstname		character varying(64) ,
		forename 		character varying(64) ,
		givenname 		character varying(64) ,
		middlename 		character varying(64) ,
		middleinitial	character varying(16) ,
		lastname   		character varying(64) ,
		surname 		character varying(64) ,
		familyname  	character varying(64) ,
		fullname 		character varying(256) ,

		gender_code 		character varying(8) not null ,
								
		parent_fullname		character varying(256) ,
		parent_ref_id_type 	character varying(64) ,
		parent_ref_id 		character varying(64) ,

		dob 	date ,
		age		numeric(3,0) ,
		
		addr_line1 		character varying(256) ,
		addr_line2 		character varying(256) ,
		addr_line3 		character varying(256) ,
			 
		location_code 	character varying(32) not null ,
			
		mobile 			character varying(16) ,	
		email 			character varying(64) ,
				
		applicant_type 	character varying (64) not null ,
				
		nationalid		character varying (32) ,
			
		status_code 	character varying(64) not null ,
		lang_code  		character varying(3) not null ,
		

	is_active boolean,
	cr_by character varying (32),
	cr_dtimes timestamp,
	upd_by  character varying (32),
	upd_dtimes timestamp
)
;

-- keys section --------------------------------------------------------------------
alter table ida.indv_demographic add constraint pk_idemo_uin_ref_id primary key (uin_ref_id, lang_code)
;
 
alter table ida.indv_demographic add constraint fk_idemo_uin_ref_id foreign key (uin_ref_id) references 
ida.uin (uin_ref_id) on delete cascade on update cascade
;
-- indexes section ------------------------------------------------------------------
-- create index idx_idemo_<> on ida.<>(<>)
-- ;

-- comments section ---------------------------------------------------------------------- 
comment on table ida.indv_demographic is 'This table is used to store all authentication request details'
;
comment on column ida.indv_demographic.uin_ref_id is ' Reference key from UIN Master table'
;

-- create table section --------------------------------------------------------
-- schema 		: master  	- Master Reference schema
-- table 		: location  - Master location list
-- table alias  : loc	

-- schemas section ---------------------------------------------------------------

-- create schema if master reference schema not exists
create schema if not exists master
;

-- table section -------------------------------------------------------------------------------

	create table if not exists master.location (
	
		code character varying (32) not null , 
		name character varying (128) not null ,
		
		hierarchy_level smallint not null ,
		hierarchy_level_name character varying (64) not null ,
		
		parent_loc_code character varying (32) ,
		
		lang_code  character varying(3) not null ,

		is_active 	boolean not null,
		cr_by 		character varying (24) not null,
		cr_dtimesz 	timestamp  with time zone not null,
		upd_by  	character varying (24),
		upd_dtimesz timestamp  with time zone,
		is_deleted 	boolean,
		del_dtimesz	timestamp  with time zone

		)
	;
		
	--  Below is sample data for understanding the hierarchy_level and data to be populated.
	--  code(unique)	name  			level	levelname	parent code 	

	--  IND				INDIA			0		COUNTRY		NULL	

	-- 	KAR				KARNATAKA		1		STATE		IND	
	--  TN				TAMILNADU		1		STATE		IND
	--  KL				KERALA			1		STATE		IND

	-- 	BLR				BANGALURU		2		CITY		KAR	
	-- 	MLR	 			MANGALORE		2		CITY		KAR	
	-- 	MSR				MYSURU			2		CITY		KAR	
	-- 	KLR				KOLAR			2		CITY		KAR

	-- 	CHNN			CHENNAI 		2		CITY		TN
	-- 	CBE				COIMBATORE		2		CITY		TN			

	--  RRN				RRNAGAR			3		AREA		BLR
	--  560029			560029			4		ZIPCODE		RRN	
	-- 								(  for pin/zip, both code and name can be same)
			
	--  600001			600001			3		ZIPCODE		CHN		


-- keys section -------------------------------------------------------------------------------
alter table master.location add constraint pk_loc_code primary key (code, is_active)
 ;

-- indexes section -----------------------------------------------------------------------
create index if not exists idx_loc_name on master.location (name)
;

-- comments section -------------------------------------------------------------------------- 
comment on table master.location is 'Master location table'
;

