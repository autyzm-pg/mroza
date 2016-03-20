##
 # MROZA - supporting system of behavioral therapy of people with autism
 #     Copyright (C) 2015-2016 autyzm-pg
 #
 #     This program is free software: you can redistribute it and/or modify
 #     it under the terms of the GNU General Public License as published by
 #     the Free Software Foundation, either version 3 of the License, or
 #     (at your option) any later version.
 #
 #     This program is distributed in the hope that it will be useful,
 #     but WITHOUT ANY WARRANTY; without even the implied warranty of
 #     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 #     GNU General Public License for more details.
 #
 #     You should have received a copy of the GNU General Public License
 #     along with this program.  If not, see <http://www.gnu.org/licenses/>.
##

import os
import calendar
import time
import string;
import random
from random import randrange, uniform
import datetime
from datetime import timedelta

epoch = datetime.datetime.utcfromtimestamp(0)

def unix_time_millis(dt):
    return (dt - epoch).total_seconds() * 1000.0

def login(name,surname):
	return str(name + "." + surname)

def getSymbols():
	symbols = []
	for c in string.ascii_uppercase:
		symbols.append(str(c + str(randrange(1000,9999))))
	return symbols

def getSymbolsTable():
	symbols = []
	for c in string.ascii_uppercase:
		symbols.append(str(c + c + str(randrange(1000,9999))))
	return symbols

#CONFIGURE
numOfChilds = 4
numOfProgramsPerChild = 10
numOfRowsPerTable = 5
numOfTablesPerProgram = 1
numOfTherapist = 6
tablefield_id = 0

filePath = os.getcwd() + "\\gen.txt"
file = open(filePath, "w+")

line = "DELETE FROM THERAPIST;" + '\n'
line += "DELETE FROM CHILD;" + '\n'
line += "DELETE FROM TERM_SOLUTION;" + '\n'
line += "DELETE FROM PROGRAM;" + '\n'
line += "DELETE FROM TABLE_TEMPLATE;" + '\n'
line += "DELETE FROM CHILD_TABLE;" + '\n'
line += "DELETE FROM TABLE_ROW;" + '\n'
line += "DELETE FROM TABLE_FIELD;" + '\n'
line += "DELETE FROM TABLE_FIELD_FILLING;" + '\n'

file.write(line)
#Therapists
names = ["Alicja", "Elzbieta", "Milena", "Katarzyna", "Julita", "Iwona"]
surnames = ["Kowalska", "Nowak", "Zielinska", "Glowacka", "Wisniewska", "Dratwa"]
password = "abc123"

for therapis_id in range(numOfTherapist):
	line = "INSERT INTO THERAPIST (_id,login,password,name,surname) VALUES(" + str(therapis_id) + ",\"" + login(names[therapis_id], surnames[therapis_id]) + "\",\"" + password + "\",\"" + names[therapis_id] + "\",\"" + surnames[therapis_id] + "\");" + '\n'
	file.write(line)


#Childs
for child_id in range(numOfChilds):
	code = random.choice(string.ascii_uppercase) + random.choice(string.ascii_uppercase);
	for i in range(0,3):
		code += random.choice(string.digits)
		
	birthdate = calendar.timegm(time.strptime(str(randrange(1,28))+'/'+str(randrange(1,12))+'/' + str(randrange(2005,2010)) , '%d/%m/%Y'))
	line = "INSERT INTO CHILD (_id,code,is_archived) VALUES(" + str(child_id) + ",\"" + code+ "\",\"0\");" + '\n'

	#wyznaczanie czasow
	year = 2015
	monthNow = 10
	dayNow = 20

	# timePrev = calendar.timegm(time.strptime(str(dayNow-7)+'/'+str(monthNow)+'/' + str(year), '%d/%m/%Y'))
	# timeNow = calendar.timegm(time.strptime(str(dayNow)+'/'+str(monthNow)+'/' + str(year), '%d/%m/%Y'))
	# timeFutureStart = calendar.timegm(time.strptime(str(dayNow+7)+'/'+str(monthNow)+'/'+str(year), '%d/%m/%Y'))
	# timeFutureEnd =calendar.timegm(time.strptime(str(3)+'/'+str(11)+'/'+str(year), '%d/%m/%Y'))
	dtNow = datetime.datetime.now()
	dtPrev = dtNow - timedelta(days=8)
	dtPrevEnd = dtNow - timedelta(days=2)
	dtNowEnd = dtNow + timedelta(days=6)
	dtFutureStart = dtNow + timedelta(days=7)
	dtFutureEnd = dtNow + timedelta(days=14)
	dtNow = dtNow - timedelta(days=1)
	timePrev = unix_time_millis(dtPrev)
	timePrevEnd = unix_time_millis(dtPrevEnd)
	timeNow = unix_time_millis(dtNow)
	timeNowEnd = unix_time_millis(dtNowEnd)
	timeFutureStart = unix_time_millis(dtFutureStart)
	timeFutureEnd = unix_time_millis(dtFutureEnd)

	#okres historyczny
	line += "INSERT INTO TERM_SOLUTION (_id, start_date, end_date, child_id) VALUES(" + str(child_id) + "," + str(timePrev) + "," + str(timePrevEnd) + "," + str(child_id) + ");" + '\n'
	#okres terazniejszy
	line += "INSERT INTO TERM_SOLUTION (_id, start_date, end_date, child_id) VALUES(" + str(child_id + numOfChilds ) + "," + str(timeNow) + "," + str(timeNowEnd) + "," + str(child_id) + ");" + '\n'
	#okres przyszly
	line += "INSERT INTO TERM_SOLUTION (_id, start_date, end_date, child_id) VALUES(" + str(child_id + 2*numOfChilds) + "," + str(timeFutureStart) + "," + str(timeFutureEnd) + "," + str(child_id) + ");" + '\n'
	file.write(line)

	#programy
	symbols = getSymbols()
	symbolsTable = getSymbolsTable()
	programName = "Nazwa programu"
	description = ["Brak opisu","Ktorki opis","Dlugi opis"]
	create_date = calendar.timegm(time.strptime(str(dayNow)+'/'+str(monthNow)+'/' + str(year), '%d/%m/%Y'))
	is_finished = 0
	table_id_fun_iterator = 0
	for program_id in range(numOfProgramsPerChild*child_id, numOfProgramsPerChild*(child_id+1)):
		symbol = symbols[randrange(1,len(symbols))]
		line = "INSERT INTO PROGRAM (_id, symbol, name, description, create_date, is_finished, child_id) VALUES(" + str(program_id) +",\"" + symbol + "\",\"" + programName + " " + symbol  + "\",\"" + description[randrange(1,len(description))] + "\"," + str(create_date) + "," + str(is_finished) + "," + str(child_id) + ");" + '\n'
		file.write(line)

		#tabelki

		for table_id in range(numOfTablesPerProgram*program_id,numOfTablesPerProgram*(program_id+1)):
			symbol = symbolsTable[randrange(1,len(symbolsTable))]
			is_archived = 0
			false = "0"
			true = "1"
			term_solution_fun_id = child_id + (table_id_fun_iterator%3) * numOfChilds
			table_id_fun_iterator += 1
			
			#generate number of teaching and generalization fields
			teaching_fields = random.randint(1, 2)
			gen_fields = random.randint(0, 2)
			
			#set teaching and generalization statuses
			if(teaching_fields > 0):
				teaching_collected = true
			else:
				teaching_collected = false
			teaching_fill_out_date = "null"
			teaching_finished = false
			
			if(gen_fields > 0):
				gen_collected = true
			else:
				gen_collected = false
			gen_fill_out_date = "null"
			gen_finished = false
				
			line = "INSERT INTO TABLE_TEMPLATE (_id, name, description, create_date, is_archived, program_id) VALUES(" + str(program_id) +",\"" + programName + " " + symbol  + "\",\"" + description[randrange(1,len(description))] + "\"," + str(create_date) + "," + str(is_archived) + "," + str(program_id) + ");" + '\n'
			line += "INSERT INTO CHILD_TABLE(_id,is_teaching_collected,is_generalization_collected,is_teaching_finished,is_generalization_finished,note,is_ioa,is_pretest,teaching_fill_out_date, generalization_fill_out_date,last_edit_date,table_id,term_solution_id) VALUES(" + str(table_id)+ "," + teaching_collected + "," + gen_collected + ","+ teaching_finished + "," + gen_finished + ",\"Notatka\"," + false + "," + false + ","+ str(teaching_fill_out_date) + "," + str(gen_fill_out_date) + ","+ str(gen_fill_out_date) + ","+ str(program_id) + "," + str(term_solution_fun_id) + ");" + '\n'

			file.write(line)
			
			#row tabelek
			for tablerow_id in range(numOfRowsPerTable*table_id, numOfRowsPerTable*(table_id+1)):
				line = "INSERT INTO TABLE_ROW (_id,value,in_order,table_id) VALUES("+str(tablerow_id)+",\"Zadanie do wykonania nr "+ str(tablerow_id) + "\"," + str(tablerow_id%numOfRowsPerTable) + ","+ str(table_id)+ ");"+'\n'
				file.write(line)
				
				line = ""
				for order in range(0, teaching_fields):
					line += "INSERT INTO TABLE_FIELD (_id,type,in_order,table_row_id)VALUES("+str(tablefield_id)+",\"U\","+str(order)+","+ str(tablerow_id) + ");" + '\n'
					line += "INSERT INTO TABLE_FIELD_FILLING (_id,content,table_field_id,child_table_id)VALUES("+str(tablefield_id)+",\"empty\","+str(tablefield_id)+","+ str(table_id) + ");" + '\n'
					tablefield_id += 1
					
				for order in range(teaching_fields, teaching_fields + gen_fields):
					line += "INSERT INTO TABLE_FIELD (_id,type,in_order,table_row_id)VALUES("+str(tablefield_id)+",\"G\","+str(order)+","+ str(tablerow_id) + ");" + '\n'
					line += "INSERT INTO TABLE_FIELD_FILLING (_id,content,table_field_id,child_table_id)VALUES("+str(tablefield_id)+",\"empty\","+str(tablefield_id)+","+ str(table_id) + ");" + '\n'
					tablefield_id += 1
			
				file.write(line)
