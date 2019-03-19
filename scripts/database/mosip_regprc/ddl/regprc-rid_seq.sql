-- create table section --------------------------------------------------------
-- schema 		: regprc  		- Registration Processor schema
-- table 		: rid_seq 		-Registration tables to keep the current Registration ID sequence
-- table alias  : ridseq

-- schemas section ---------------------------------------------------------------

-- create schema if registration processor schema not exists
create schema if not exists regprc
;
 
-- table section -------------------------------------------------------------------------------

	create table regprc.rid_seq (
	
		curr_seq_no integer NOT NULL, 

		cr_by 		character varying (32) not null,
		cr_dtimes 	timestamp  not null,
		upd_by  	character varying (32),
		upd_dtimes timestamp
		
	)
;
		

-- keys section -------------------------------------------------------------------------------
alter table regprc.rid_seq add constraint pk_ridseq_id primary key (curr_seq_no)
 ;

-- indexes section -----------------------------------------------------------------------
-- create index idx_ridseq_<col> on regprc.rid_seq (col)
-- ;

-- comments section -------------------------------------------------------------------------- 
comment on table regprc.rid_seq is 'regprc tables to keep the current Registration ID sequence'
;

