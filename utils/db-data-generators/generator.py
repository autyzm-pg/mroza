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
import random
from random import randrange
import string
import datetime
import math
from datetime import timedelta


def login(name, surname):
    return str(name + "." + surname)

def getSymbols():
    symbols = []
    for c in string.ascii_uppercase:
        symbols.append(str(c + str(randrange(1000, 9999))))
    return symbols

def getSymbolsTable():
    symbols = []
    for c in string.ascii_uppercase:
        symbols.append(str(c + c + str(randrange(1000, 9999))))
    return symbols

def getFieldValue(is_empty):
    if is_empty:
        return "EMPTY"
    values = ["OK", "NOK"]
    return values[random.randint(0,1)]


# configuration
numOfChildren = 2
numOfProgramsPerChild = 2
numOfRowsPerTable = 3
numOfPeriods = 7
numOfPeriodsPerTable = 3

filePath = os.getcwd() + "\\generated_inserts.sql"
file = open(filePath, "w+")

# delete data from tables
line = "DELETE FROM Therapist;" + '\n'
line += "DELETE FROM Kid;" + '\n'
line += "DELETE FROM ResolvePeriod;" + '\n'
line += "DELETE FROM Program;" + '\n'
line += "DELETE FROM Tab;" + '\n'
line += "DELETE FROM KidTab;" + '\n'
line += "DELETE FROM TabRow;" + '\n'
line += "DELETE FROM TabField;" + '\n'
line += "DELETE FROM TabFieldResolve;" + '\n'
file.write(line)

# create therapists
names = ["Alicja", "Elzbieta", "Milena", "Katarzyna", "Julita", "Iwona"]
surnames = ["Kowalska", "Nowak", "Zielinska", "Glowacka", "Wisniewska", "Dratwa"]
password = "abc123"

for therapist_id in range(len(names)):
    line = "INSERT INTO Therapist (id, login, name, surname, password) VALUES(" + str(therapist_id) + ",\'" + login(names[therapist_id], surnames[therapist_id]) + "\',\'" + names[therapist_id] + "\',\'" + surnames[therapist_id] + "\',\'" + password + "\');" + '\n'
    file.write(line)

field_id = 0
field_filling_id = 0
epoch = datetime.datetime.utcfromtimestamp(0)
for child_id in range(numOfChildren):
    # create children
    code = random.choice(string.ascii_uppercase) + random.choice(string.ascii_uppercase)
    for i in range(0, 3):
        code += random.choice(string.digits)

    line = "INSERT INTO Kid (id, code) VALUES(" + str(child_id) + ",\'" + code + "\');" + '\n'
    file.write(line)

    # create periods
    line = ""
    dtNow = datetime.datetime.now()
    dtPrev = dtNow - timedelta(days=(7*numOfPeriods))
    periods = 0
    while periods < numOfPeriods:
        dtPrev += timedelta(days=7)
        dtPrevEnd = dtPrev + timedelta(days=6)
        line += "INSERT INTO ResolvePeriod (id, begin_date, end_date, kid_id) VALUES(" + str((child_id * numOfPeriods) + periods) + ",\'" + dtPrev.strftime("%Y-%m-%d") + "\',\'" + dtPrevEnd.strftime("%Y-%m-%d") + "\'," + str(child_id) + ");" + '\n'
        periods += 1

    file.write(line)

    # create programs
    symbols = getSymbols()
    symbolsTable = getSymbolsTable()
    programName = "Nazwa programu"
    programDescription = "Opis programu"
    is_finished = 0

    for program_id in range(numOfProgramsPerChild*child_id, numOfProgramsPerChild*(child_id+1)):
        symbol = symbols[randrange(1, len(symbols))]
        line = "INSERT INTO Program (id, symbol, name, description, kid_id) VALUES(" + str(program_id) +",\'" + symbol + "\',\'" + programName + " " + symbol + "\',\'" + programDescription + " " + symbol + "\'," + str(child_id) + ");" + '\n'
        file.write(line)

        period_id = child_id * numOfPeriods - 1
        dtPrev = dtNow - timedelta(days=(7*numOfPeriods))
        numOfTablesPerProgram = math.ceil(numOfPeriods / numOfPeriodsPerTable)
        child_table_id = numOfPeriods*program_id
        for table_id in range(numOfTablesPerProgram*program_id, numOfTablesPerProgram*(program_id + 1)):
            # create table
            tableName = "Krok " + str(table_id - numOfTablesPerProgram*program_id)
            tableDescription = "Opis tabelki"

            line = "INSERT INTO Tab (id, name, description, program_id) VALUES(" + str(table_id) +",\'" + tableName + "\',\'" + tableDescription + "\'," + str(program_id) + ");" + '\n'
            file.write(line)

            # create table rows
            teaching_fields = random.randint(1, 2)
            gen_fields = random.randint(0, 2)
            for tablerow_id in range(numOfRowsPerTable*table_id, numOfRowsPerTable*(table_id+1)):
                line = "INSERT INTO TabRow (id, value, ord, tab_id) VALUES("+str(tablerow_id)+",\'Zadanie do wykonania nr "+ str(tablerow_id) + "\'," + str(tablerow_id % numOfRowsPerTable) + "," + str(table_id) + ");"+'\n'
                file.write(line)

                # create fields
                line = ""
                for order in range(0, teaching_fields):
                    line += "INSERT INTO TabField (id, type, ord, row_id) VALUES(" + str(field_id)+",\'U\',"+str(order) + "," + str(tablerow_id) + ");" + '\n'
                    field_id += 1

                for order in range(teaching_fields, teaching_fields + gen_fields):
                    line += "INSERT INTO TabField (id, type, ord, row_id) VALUES(" + str(field_id) + ",\'G\'," + str(order) + ","+ str(tablerow_id) + ");" + '\n'
                    field_id += 1

                file.write(line)

            # create child table
            max_child_table_id = child_table_id + numOfPeriodsPerTable
            while (child_table_id < max_child_table_id) and (child_table_id < numOfPeriods*(program_id + 1)):
                period_id += 1

                if teaching_fields > 0:
                    teaching_collected = "TRUE"
                else:
                    teaching_collected = "FALSE"

                if gen_fields > 0:
                    gen_collected = "TRUE"
                else:
                    gen_collected = "FALSE"

                if child_table_id != numOfPeriods*(program_id + 1) - 1:
                    teaching_finished = "TRUE"
                    gen_finished = "TRUE"
                    dtPrev += timedelta(days=7)
                    dtPrevEnd = dtPrev + timedelta(days=6)
                    teaching_fill_out_date = "\'" + dtPrevEnd.strftime("%Y-%m-%d") + "\'"
                    gen_fill_out_date = "\'" + dtPrevEnd.strftime("%Y-%m-%d") + "\'"
                else:
                    teaching_fill_out_date = "NULL"
                    teaching_finished = "FALSE"
                    gen_fill_out_date = "NULL"
                    gen_finished = "FALSE"

                last_edit_datetime = "\'" + dtPrev.strftime("%Y-%m-%d") + "\'"

                line = "INSERT INTO KidTab(id, collecting_learning, collecting_generalization, finished_learning, finished_generalization, IOA, pretest, learning_fill_date, generalization_fill_date, last_edit_datetime, tab_id, resolve_period_id) VALUES(" \
                        + str(child_table_id) + "," + teaching_collected + "," + gen_collected + "," + teaching_finished + "," + gen_finished + "," + "FALSE" + "," + "FALSE"  + "," + teaching_fill_out_date + "," + gen_fill_out_date + "," + last_edit_datetime + "," + str(table_id) + "," + str(period_id) + ");" + '\n'
                file.write(line)

                # create fields fillings
                if not (teaching_finished == "FALSE" and gen_finished == "FALSE"):
                    line = ""
                    table_field_id = field_id - numOfRowsPerTable * (teaching_fields + gen_fields)
                    for row in range(0, numOfRowsPerTable):
                        for field in range(0, teaching_fields + gen_fields):
                            value = getFieldValue(False)
                            line += "INSERT INTO TabFieldResolve (id, value, tab_field_id, kid_tab_id) VALUES(" + str(field_filling_id) + ",\'" + value + "\'," + str(table_field_id) + "," + str(child_table_id) + ");" + '\n'
                            field_filling_id += 1
                            table_field_id += 1

                    file.write(line)

                child_table_id += 1

file.close()
