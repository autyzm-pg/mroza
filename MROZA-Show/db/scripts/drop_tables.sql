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

DROP TRIGGER log_kid_delete ON Kid;
DROP TRIGGER log_resolve_period_delete ON ResolvePeriod;
DROP TRIGGER log_program_delete ON Program;
DROP TRIGGER log_tab_delete ON Tab;
DROP TRIGGER log_tab_row_delete ON TabRow;
DROP TRIGGER log_tab_field_delete ON TabField;
DROP TRIGGER log_kid_tab_delete ON KidTab;
DROP TRIGGER log_tab_field_resolve_delete ON TabFieldResolve;

DROP FUNCTION log_kid_delete();
DROP FUNCTION log_resolve_period_delete();
DROP FUNCTION log_program_delete();
DROP FUNCTION log_tab_delete();
DROP FUNCTION log_tab_row_delete();
DROP FUNCTION log_tab_field_delete();
DROP FUNCTION log_kid_tab_delete();
DROP FUNCTION log_tab_field_resolve_delete();


DROP TABLE KidDeleteHistory;
DROP TABLE ResolvePeriodDeleteHistory;
DROP TABLE ProgramDeleteHistory;
DROP TABLE TabDeleteHistory;
DROP TABLE TabRowDeleteHistory;
DROP TABLE TabFieldDeleteHistory;
DROP TABLE KidTabDeleteHistory;
DROP TABLE TabFieldResolveDeleteHistory;

DROP TABLE TabFieldResolve;
DROP TYPE ResolvedFieldVal;
DROP TABLE KidTab;
DROP TABLE TabField;
DROP TYPE FieldType;
DROP TABLE TabRow;
DROP TABLE Tab;
DROP TABLE Program;
DROP TABLE ResolvePeriod;
DROP TABLE Kid;
DROP TABLE Therapist;

