/*
 * MROZA - supporting system of behavioral therapy of people with autism
 *     Copyright (C) 2015-2016 autyzm-pg
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

CREATE TABLE Therapist (
	id				BIGSERIAL PRIMARY KEY,
	login			TEXT NOT NULL UNIQUE,
	name			TEXT NOT NULL,
	surname			TEXT NOT NULL,
	password		TEXT NOT NULL
);

CREATE TABLE Kid (
	id				BIGSERIAL PRIMARY KEY,
	code			TEXT NOT NULL,
	archived		BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE ResolvePeriod (
	id				BIGSERIAL PRIMARY KEY,
	begin_date		DATE NOT NULL,
	end_date		DATE NOT NULL,
	kid_id			BIGINT REFERENCES Kid(id) ON DELETE CASCADE
);

CREATE TABLE Program (
	id				BIGSERIAL PRIMARY KEY,
	symbol			TEXT NOT NULL,
	name			TEXT NOT NULL,
	description		TEXT NOT NULL,
	create_datetime	timestamp NOT NULL DEFAULT current_timestamp,
	finished		BOOLEAN NOT NULL DEFAULT FALSE,
	kid_id			BIGINT REFERENCES Kid(id) ON DELETE CASCADE
);

CREATE TABLE Tab (
	id 				BIGSERIAL PRIMARY KEY,
	name			TEXT NOT NULL,
	description		TEXT,
	create_datetime	TIMESTAMP NOT NULL DEFAULT current_timestamp,
	archived		BOOLEAN NOT NULL DEFAULT FALSE,
	program_id		BIGINT REFERENCES Program(id) ON DELETE CASCADE
);

CREATE TABLE TabRow (
	id 				BIGSERIAL PRIMARY KEY,
	value			TEXT NOT NULL,
	ord				INT NOT NULL,
	tab_id			BIGINT REFERENCES Tab(id) ON DELETE CASCADE
);

CREATE TYPE FieldType AS ENUM ('U', 'G');

CREATE TABLE TabField (
	id 				BIGSERIAL PRIMARY KEY,
	type 			FieldType NOT NULL,
	ord				INT NOT NULL,
	row_id			BIGINT REFERENCES TabRow(id) ON DELETE CASCADE
);

CREATE TABLE KidTab (
	id 							BIGSERIAL PRIMARY KEY,
	collecting_learning			BOOLEAN NOT NULL,
	collecting_generalization	BOOLEAN NOT NULL,
	finished_learning			BOOLEAN NOT NULL DEFAULT FALSE,
	finished_generalization		BOOLEAN NOT NULL DEFAULT FALSE,
	note						TEXT,
	IOA							BOOLEAN NOT NULL DEFAULT FALSE,
	pretest						BOOLEAN NOT NULL,
	last_edit_datetime			TIMESTAMP,
	tab_id						BIGINT REFERENCES Tab(id) ON DELETE CASCADE,
	resolve_period_id			BIGINT REFERENCES ResolvePeriod(id) ON DELETE CASCADE,
	generalization_fill_date    TIMESTAMP,
	learning_fill_date    		TIMESTAMP
);

CREATE TYPE ResolvedFieldVal AS ENUM ('OK', 'NOK', 'EMPTY');

CREATE TABLE TabFieldResolve (
	id 						BIGSERIAL PRIMARY KEY,
	value					ResolvedFieldVal NOT NULL DEFAULT 'EMPTY',
	tab_field_id			BIGINT REFERENCES TabField(id) ON DELETE CASCADE,
	kid_tab_id				BIGINT REFERENCES KidTab(id) ON DELETE CASCADE
);

-- DELETE HISTORY

CREATE TABLE KidDeleteHistory (
	id 						BIGSERIAL PRIMARY KEY,
	deleted_id				BIGINT NOT NULL,
	delete_datetime			TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ResolvePeriodDeleteHistory (
	id 						BIGSERIAL PRIMARY KEY,
	deleted_id				BIGINT NOT NULL,
	delete_datetime			TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ProgramDeleteHistory (
	id 						BIGSERIAL PRIMARY KEY,
	deleted_id				BIGINT NOT NULL,
	delete_datetime			TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE TabDeleteHistory (
	id 						BIGSERIAL PRIMARY KEY,
	deleted_id				BIGINT NOT NULL,
	delete_datetime			TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE TabRowDeleteHistory (
	id 						BIGSERIAL PRIMARY KEY,
	deleted_id				BIGINT NOT NULL,
	delete_datetime			TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE TabFieldDeleteHistory (
	id 						BIGSERIAL PRIMARY KEY,
	deleted_id				BIGINT NOT NULL,
	delete_datetime			TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE KidTabDeleteHistory (
	id 						BIGSERIAL PRIMARY KEY,
	deleted_id				BIGINT NOT NULL,
	delete_datetime			TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE TabFieldResolveDeleteHistory (
	id 						BIGSERIAL PRIMARY KEY,
	deleted_id				BIGINT NOT NULL,
	delete_datetime			TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE FUNCTION log_kid_delete() RETURNS trigger AS $log_kid_delete$
    BEGIN
    	INSERT INTO KidDeleteHistory (deleted_id) VALUES (OLD.id);
    	RETURN OLD;
    END;
$log_kid_delete$ LANGUAGE plpgsql;

CREATE FUNCTION log_resolve_period_delete() RETURNS trigger AS $log_kid_delete$
    BEGIN
    	INSERT INTO ResolvePeriodDeleteHistory (deleted_id) VALUES (OLD.id);
    	RETURN OLD;
    END;
$log_kid_delete$ LANGUAGE plpgsql;

CREATE FUNCTION log_program_delete() RETURNS trigger AS $log_kid_delete$
    BEGIN
    	INSERT INTO ProgramDeleteHistory (deleted_id) VALUES (OLD.id);
    	RETURN OLD;
    END;
$log_kid_delete$ LANGUAGE plpgsql;

CREATE FUNCTION log_tab_delete() RETURNS trigger AS $log_kid_delete$
    BEGIN
    	INSERT INTO TabDeleteHistory (deleted_id) VALUES (OLD.id);
    	RETURN OLD;
    END;
$log_kid_delete$ LANGUAGE plpgsql;

CREATE FUNCTION log_tab_row_delete() RETURNS trigger AS $log_kid_delete$
    BEGIN
    	INSERT INTO TabRowDeleteHistory (deleted_id) VALUES (OLD.id);
    	RETURN OLD;
    END;
$log_kid_delete$ LANGUAGE plpgsql;

CREATE FUNCTION log_tab_field_delete() RETURNS trigger AS $log_kid_delete$
    BEGIN
    	INSERT INTO TabFieldDeleteHistory (deleted_id) VALUES (OLD.id);
    	RETURN OLD;
    END;
$log_kid_delete$ LANGUAGE plpgsql;

CREATE FUNCTION log_kid_tab_delete() RETURNS trigger AS $log_kid_delete$
    BEGIN
    	INSERT INTO KidTabDeleteHistory (deleted_id) VALUES (OLD.id);
    	RETURN OLD;
    END;
$log_kid_delete$ LANGUAGE plpgsql;

CREATE FUNCTION log_tab_field_resolve_delete() RETURNS trigger AS $log_kid_delete$
    BEGIN
    	INSERT INTO TabFieldResolveDeleteHistory (deleted_id) VALUES (OLD.id);
    	RETURN OLD;
    END;
$log_kid_delete$ LANGUAGE plpgsql;


CREATE TRIGGER log_kid_delete
    BEFORE DELETE ON kid
    FOR EACH ROW
    EXECUTE PROCEDURE log_kid_delete();

CREATE TRIGGER log_resolve_period_delete
    BEFORE DELETE ON ResolvePeriod
    FOR EACH ROW
    EXECUTE PROCEDURE log_resolve_period_delete();

CREATE TRIGGER log_program_delete
    BEFORE DELETE ON Program
    FOR EACH ROW
    EXECUTE PROCEDURE log_program_delete();

CREATE TRIGGER log_tab_delete
    BEFORE DELETE ON Tab
    FOR EACH ROW
    EXECUTE PROCEDURE log_tab_delete();

CREATE TRIGGER log_tab_row_delete
    BEFORE DELETE ON TabRow
    FOR EACH ROW
    EXECUTE PROCEDURE log_tab_row_delete();

CREATE TRIGGER log_tab_field_delete
    BEFORE DELETE ON TabField
    FOR EACH ROW
    EXECUTE PROCEDURE log_tab_field_delete();

CREATE TRIGGER log_kid_tab_delete
    BEFORE DELETE ON KidTab
    FOR EACH ROW
    EXECUTE PROCEDURE log_kid_tab_delete();

CREATE TRIGGER log_tab_field_resolve_delete
    BEFORE DELETE ON TabFieldResolve
    FOR EACH ROW
    EXECUTE PROCEDURE log_tab_field_resolve_delete();
