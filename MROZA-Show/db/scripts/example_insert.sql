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

INSERT INTO Kid (code) VALUES 
	('ANW-xyz'),
	('FR-xyz');

INSERT INTO ResolvePeriod (begin_date, end_date, kid_id) VALUES
	('2016-01-15', '2016-01-25', (SELECT id FROM Kid WHERE code = 'ANW-xyz')),
	('2015-10-01', '2015-10-05', (SELECT id FROM Kid WHERE code = 'ANW-xyz'));

INSERT INTO Program (symbol, name, description, kid_id) VALUES
	('J.XYZ.12', 'Nauka wymowy', 'Program polega na nauce wymowy różnorakich słów', NULL),
	('J.XYZ.15', 'Nauka wymowy - krótkie słowa', 'Program polega na nauce wymowy krótkich słów', NULL),
	('A.BCD.33', 'Budowanie z klocków', 'Program polega na budowaniu z klocków LEGO', NULL),
	('J.XYZ.12', 'Nauka wymowy', 'Program polega na nauce wymowy różnorakich słów', (SELECT id FROM Kid WHERE code = 'ANW-xyz')),
	('J.XYZ.15', 'Nauka wymowy - krótkie słowa', 'Program polega na nauce wymowy krótkich słów', (SELECT id FROM Kid WHERE code = 'ANW-xyz'));

INSERT INTO Tab (name, program_id) VALUES
	('Krok 1', (SELECT id FROM Program WHERE symbol = 'J.XYZ.12' AND kid_id = (SELECT id FROM Kid WHERE code = 'ANW-xyz'))),
	('Krok 1b', (SELECT id FROM Program WHERE symbol = 'J.XYZ.12' AND kid_id = (SELECT id FROM Kid WHERE code = 'ANW-xyz'))),
	('Krok 2', (SELECT id FROM Program WHERE symbol = 'J.XYZ.12' AND kid_id = (SELECT id FROM Kid WHERE code = 'ANW-xyz'))),
	('Krok 3', (SELECT id FROM Program WHERE symbol = 'J.XYZ.12' AND kid_id = (SELECT id FROM Kid WHERE code = 'ANW-xyz'))),
	('Krok 1A', (SELECT id FROM Program WHERE symbol = 'J.XYZ.15' AND kid_id = (SELECT id FROM Kid WHERE code = 'ANW-xyz'))),
	('Krok 1B', (SELECT id FROM Program WHERE symbol = 'J.XYZ.15' AND kid_id = (SELECT id FROM Kid WHERE code = 'ANW-xyz'))),
	('Krok 2C', (SELECT id FROM Program WHERE symbol = 'J.XYZ.15' AND kid_id = (SELECT id FROM Kid WHERE code = 'ANW-xyz'))),
	('Krok 3D', (SELECT id FROM Program WHERE symbol = 'J.XYZ.15' AND kid_id = (SELECT id FROM Kid WHERE code = 'ANW-xyz')));


INSERT INTO TabRow (value, ord, tab_id) VALUES
	('Jabłko', 0, (SELECT id FROM Tab WHERE name = 'Krok 1')),
	('Mapa', 1, (SELECT id FROM Tab WHERE name = 'Krok 1')),
	('Testowy', 2, (SELECT id FROM Tab WHERE name = 'Krok 1')),
('Ale', 0, (SELECT id FROM Tab WHERE name = 'Krok 1b')),
	('Wcale', 1, (SELECT id FROM Tab WHERE name = 'Krok 1b')),
	('Nic', 2, (SELECT id FROM Tab WHERE name = 'Krok 1b')),
('Ktoś', 0, (SELECT id FROM Tab WHERE name = 'Krok 2')),
	('Coś', 1, (SELECT id FROM Tab WHERE name = 'Krok 2')),
	('Gdzieś', 2, (SELECT id FROM Tab WHERE name = 'Krok 2')),
('No', 0, (SELECT id FROM Tab WHERE name = 'Krok 3')),
	('Drzewo', 1, (SELECT id FROM Tab WHERE name = 'Krok 3')),
	('Brak', 2, (SELECT id FROM Tab WHERE name = 'Krok 3'));

INSERT INTO TabField (type, ord, row_id) VALUES
	('U', 0, (SELECT id FROM TabRow WHERE value = 'Jabłko')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Jabłko')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Jabłko')),
	('U', 0, (SELECT id FROM TabRow WHERE value = 'Mapa')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Mapa')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Mapa')),
	('U', 0, (SELECT id FROM TabRow WHERE value = 'Testowy')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Testowy')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Testowy')),
('U', 0, (SELECT id FROM TabRow WHERE value = 'Ale')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Ale')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Ale')),
	('U', 0, (SELECT id FROM TabRow WHERE value = 'Wcale')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Wcale')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Wcale')),
	('U', 0, (SELECT id FROM TabRow WHERE value = 'Nic')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Nic')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Nic')),
('U', 0, (SELECT id FROM TabRow WHERE value = 'Ktoś')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Ktoś')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Ktoś')),
	('U', 0, (SELECT id FROM TabRow WHERE value = 'Coś')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Coś')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Coś')),
	('U', 0, (SELECT id FROM TabRow WHERE value = 'Gdzieś')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Gdzieś')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Gdzieś')),
('U', 0, (SELECT id FROM TabRow WHERE value = 'No')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'No')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'No')),
	('U', 0, (SELECT id FROM TabRow WHERE value = 'Drzewo')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Drzewo')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Drzewo')),
	('U', 0, (SELECT id FROM TabRow WHERE value = 'Brak')),
	('U', 1, (SELECT id FROM TabRow WHERE value = 'Brak')),
	('G', 2, (SELECT id FROM TabRow WHERE value = 'Brak'));

INSERT INTO KidTab (collecting_learning, collecting_generalization, pretest, last_edit_datetime, tab_id, resolve_period_id, generalization_fill_date, learning_fill_date) VALUES 
	(TRUE, TRUE, FALSE, CURRENT_TIMESTAMP, (SELECT id FROM Tab WHERE name = 'Krok 1'), (SELECT id FROM ResolvePeriod WHERE begin_date = '2016-01-15'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO TabFieldResolve (value, tab_field_id, kid_tab_id) VALUES 
	('EMPTY', (SELECT id FROM TabField WHERE ord = 0 AND row_id = (SELECT id FROM TabRow WHERE value = 'Jabłko')), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	('EMPTY', (SELECT id FROM TabField WHERE ord = 1 AND row_id = (SELECT id FROM TabRow WHERE value = 'Jabłko')), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	('EMPTY', (SELECT id FROM TabField WHERE ord = 2 AND row_id = (SELECT id FROM TabRow WHERE value = 'Jabłko')), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	('EMPTY', (SELECT id FROM TabField WHERE ord = 0 AND row_id = (SELECT id FROM TabRow WHERE value = 'Mapa')), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	('EMPTY', (SELECT id FROM TabField WHERE ord = 1 AND row_id = (SELECT id FROM TabRow WHERE value = 'Mapa')), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	('EMPTY', (SELECT id FROM TabField WHERE ord = 2 AND row_id = (SELECT id FROM TabRow WHERE value = 'Mapa')), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	('EMPTY', (SELECT id FROM TabField WHERE ord = 0 AND row_id = (SELECT id FROM TabRow WHERE value = 'Testowy')), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	('OK', (SELECT id FROM TabField WHERE ord = 1 AND row_id = (SELECT id FROM TabRow WHERE value = 'Testowy')), (SELECT id FROM KidTab WHERE collecting_learning = TRUE)),
	('NOK', (SELECT id FROM TabField WHERE ord = 2 AND row_id = (SELECT id FROM TabRow WHERE value = 'Testowy')), (SELECT id FROM KidTab WHERE collecting_learning = TRUE));















