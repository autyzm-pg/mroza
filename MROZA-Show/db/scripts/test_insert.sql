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

INSERT INTO Therapist (login, name, surname, password) VALUES 
	('mikolaj', 'Mikołaj', 'Lewandowski', 'abc1234'),
	('drugi', 'Jacek', 'Placek', 'qwerty');

INSERT INTO Kid (name, surname, birthdate) VALUES 
	('Ania', 'Wesołowska', '2007-03-24'),
	('Filip', 'Rogowski', '2008-02-03');

INSERT INTO ResolvePeriod (begin_date, end_date, kid_id) VALUES
	('2015-09-10', '2015-09-21', (SELECT id FROM Kid WHERE name = 'Ania')),
	('2015-10-01', '2015-10-05', (SELECT id FROM Kid WHERE name = 'Ania'));

INSERT INTO Program (symbol, name, description, kid_id) VALUES
	('J.XYZ.12', 'Nauka wymowy', 'Program polega na nauce wymowy różnorakich słów', (SELECT id FROM Kid WHERE name = 'Ania')),
	('J.XYZ.15', 'Nauka wymowy - krótkie słowa', 'Program polega na nauce wymowy krótkich słów', (SELECT id FROM Kid WHERE name = 'Ania'));

INSERT INTO Tab (name, program_id) VALUES
	('Krok 1', (SELECT id FROM Program WHERE symbol = 'J.XYZ.12')),
	('Krok 1b', (SELECT id FROM Program WHERE symbol = 'J.XYZ.12')),
	('Krok 2', (SELECT id FROM Program WHERE symbol = 'J.XYZ.12')),
	('Krok 3', (SELECT id FROM Program WHERE symbol = 'J.XYZ.12'));

INSERT INTO TabRow (value, ord, tab_id) VALUES
	('Jabłko', 0, (SELECT id FROM Tab WHERE name = 'Krok 1')),
	('Mapa', 1, (SELECT id FROM Tab WHERE name = 'Krok 1')),
	('Zażółć żółtą jaźń', 2, (SELECT id FROM Tab WHERE name = 'Krok 1'));

INSERT INTO TabField (type, ord, row_id) VALUES
	('U', 0, (SELECT id FROM TabRow WHERE ord = 0)),
	('U', 1, (SELECT id FROM TabRow WHERE ord = 0)),
	('G', 2, (SELECT id FROM TabRow WHERE ord = 0)),
	('U', 0, (SELECT id FROM TabRow WHERE ord = 1)),
	('U', 1, (SELECT id FROM TabRow WHERE ord = 1)),
	('G', 2, (SELECT id FROM TabRow WHERE ord = 1)),
	('U', 0, (SELECT id FROM TabRow WHERE ord = 2)),
	('U', 1, (SELECT id FROM TabRow WHERE ord = 2)),
	('G', 2, (SELECT id FROM TabRow WHERE ord = 2));

INSERT INTO KidTab (collecting_learning, collecting_generalization, pretest, last_edit_datetime, tab_id, resolve_period_id) VALUES 
	(TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, (SELECT id FROM Tab WHERE name = 'Krok 1'), (SELECT id FROM ResolvePeriod WHERE begin_date = '2015-09-10'));

INSERT INTO TabFieldResolve (tab_field_id, kid_tab_id) VALUES 
	((SELECT id FROM TabField WHERE ord = 0 AND row_id = (SELECT id FROM TabRow WHERE ord = 0)), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	((SELECT id FROM TabField WHERE ord = 1 AND row_id = (SELECT id FROM TabRow WHERE ord = 0)), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	((SELECT id FROM TabField WHERE ord = 2 AND row_id = (SELECT id FROM TabRow WHERE ord = 0)), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	((SELECT id FROM TabField WHERE ord = 0 AND row_id = (SELECT id FROM TabRow WHERE ord = 1)), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	((SELECT id FROM TabField WHERE ord = 1 AND row_id = (SELECT id FROM TabRow WHERE ord = 1)), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	((SELECT id FROM TabField WHERE ord = 2 AND row_id = (SELECT id FROM TabRow WHERE ord = 1)), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	((SELECT id FROM TabField WHERE ord = 0 AND row_id = (SELECT id FROM TabRow WHERE ord = 2)), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	((SELECT id FROM TabField WHERE ord = 1 AND row_id = (SELECT id FROM TabRow WHERE ord = 2)), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	((SELECT id FROM TabField WHERE ord = 2 AND row_id = (SELECT id FROM TabRow WHERE ord = 2)), (SELECT id FROM KidTab WHERE collecting_learning = TRUE));















