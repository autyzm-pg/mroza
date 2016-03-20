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

package com.mroza.interfaces;

import com.mroza.models.KidTable;
import com.mroza.models.Period;
import com.mroza.models.Program;
import com.mroza.models.Table;

import java.util.List;

public interface KidPeriodsService {

    KidTable getKidTableById(Integer kidTableId);
    List<Table> getTablesByProgramId(Integer programId);
    void replaceAssignmentTableToPeriod(KidTable previousKidTable, Table newTable);
    void deleteKidTable(KidTable table);
    void changeIOAForKidTable(KidTable kidTable);
    void assignTableToPeriod(Table table, Period period);
    boolean hasKidTableFilledResolvedFields(KidTable kidTable);

    boolean hasTableFilledResolvedFields(Table kidTable);

    void addPeriod(Period period);
    void updatePeriod(Period period);
    List<KidTable> getKidTablesWithTableAndProgramByPeriodId(Integer periodId);
    List<KidTable> getKidTablesWithTableAndProgramByPeriod(Period period);
    List<KidTable> getKidTablesWithTableAndRowAndFieldAndProgramByPeriod(Period period);
    List<KidTable> getCopyOfKidTables(List<KidTable> kidTables);
    List<Program> getKidProgramsNotInPeriod(int periodId);
    void removePeriod(Period period);
}
