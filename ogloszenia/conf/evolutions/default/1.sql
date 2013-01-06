# --- !Ups

CREATE SEQUENCE ad_id_seq;
CREATE TABLE announces (
	id integer NOT NULL DEFAULT nextval('ad_id_seq'),
	txt varchar(160),
	time long
);

# --- !Downs

DROP TABLE announces;
DROP SEQUENCE ad_id_seq;