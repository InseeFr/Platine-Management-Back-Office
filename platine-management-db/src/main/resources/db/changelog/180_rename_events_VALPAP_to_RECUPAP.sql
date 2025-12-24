--liquibase formatted sql

--changeset y72wvh:180_rename_events_VALPAP_to_RECUPAP

update
	public.interrogation_event_order
set
	status = 'RECUPAP'
where
	status = 'VALPAP';

update
	public.event_order
set
	status = 'RECUPAP'
where
	status = 'VALPAP';

update
	public.questioning_event
set
	"type" = 'RECUPAP'
where
	"type" = 'VALPAP';

update
	public.questioning
set
	highest_event_type = 'RECUPAP'
where
	highest_event_type = 'VALPAP';