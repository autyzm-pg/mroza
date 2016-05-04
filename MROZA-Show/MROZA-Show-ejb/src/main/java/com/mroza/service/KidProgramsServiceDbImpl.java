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

package com.mroza.service;

import com.mroza.dao.*;
import com.mroza.interfaces.KidProgramsService;
import com.mroza.models.*;
import com.mroza.models.charts.ResolvedTabData;
import com.mroza.models.charts.SimplifiedResolvedTabRow;
import com.mroza.models.queries.ResolvedTabQuery;
import com.mroza.qualifiers.KidProgramsServiceImpl;
import org.mybatis.cdi.Transactional;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@KidProgramsServiceImpl
public class KidProgramsServiceDbImpl implements Serializable, KidProgramsService {

    @Inject
    private TableRowsDao tableRowsDao;

    @Inject
    private TablesDao tablesDao;

    @Inject
    private KidTablesDao kidTablesDao;

    @Inject
    private TableFieldsDao tableFieldsDao;

    @Inject
    private ProgramsDao programsDao;

    @Inject
    private ResolvedFieldsDao resolvedFieldsDao;


    @Override
    public void saveTable(Table table) {
        if(table.getId() == -1) {
            addNewTable(table);
        } else {
            updateTable(table);
        }
    }

    @Override
    @Transactional
    public void addNewTable(Table table) {
        tablesDao.insertTable(table);
        insertRowsAndFields(table);
    }

    @Override
    @Transactional
    public void updateTable(Table table) {

        tableFieldsDao.deleteFields(table);
        tableRowsDao.deleteRows(table);
        tablesDao.updateTable(table);
        insertRowsAndFields(table);
        List<KidTable> kidTables = kidTablesDao.selectKidTablesWithResolvedFieldsByTableId(table.getId());
        if(!(kidTables.isEmpty()))
        {
            for(KidTable kidTable : kidTables) {
                kidTablesDao.updateKidTable(kidTable);
                resolvedFieldsDao.deleteResolvedFieldsByKidTable(kidTable);
                insertResolvedFields(kidTable, table);
            }
        }
    }

    private void insertResolvedFields(KidTable kidTable, Table table) {
        for(TableRow row : table.getTableRows()) {
            for(TableField field : row.getRowFields()) {
                resolvedFieldsDao.insertResolvedField(new ResolvedField("EMPTY", kidTable, field));
            }
        }
    }

    private void insertRowsAndFields(Table table) {
        for(TableRow row : table.getTableRows()) {
            row.setTableId(table.getId());
            tableRowsDao.insertTableRow(row);
        }

        for(TableRow row : table.getTableRows()) {
            for(TableField field : row.getRowFields()) {
                field.setRowId(row.getId());
                tableFieldsDao.insertTableField(field);
            }
        }
    }

    @Override
    public List<Program> getUnusedProgramsByKidId(Integer kidId) {
        return programsDao.selectUnusedProgramsByKidId(kidId);
    }

    @Override
    public void changeProgramFinishedStatus(int programId, boolean finished) {
        programsDao.updateProgramFinishedStatus(new Program(programId, finished));
    }

    @Override
    @Transactional
    public Program assignProgramToKid(int kidId, int programId) {
        Program templateProgram = programsDao.selectProgramById(programId);

        Program newProgram = new Program(templateProgram.getSymbol(), templateProgram.getName(),
                templateProgram.getDescription(), kidId);
        programsDao.insertProgram(newProgram);
        return newProgram;
    }

    @Override
    public List<Program> getKidProgramsWithCollectedData(Integer kidId) {
        return programsDao.selectKidProgramsWithCollectedData(kidId);
    }

    @Override
    public List<ResolvedTabData> getProgramDataForChart(Integer programId) {
        return programsDao.selectProgramDataForChart(programId);
    }

    @Override
    public List<SimplifiedResolvedTabRow> getSimplifiedResolvedTab(Integer kidTabId, String type) {
        ResolvedTabQuery query = new ResolvedTabQuery(kidTabId, type);
        return kidTablesDao.selectResolvedFieldsForKidTab(query);
    }

    @Override
    public void deleteKidProgram(Program program) {
        programsDao.deleteProgram(program);
    }

    @Override
    public boolean checkIfProgramHasTables(Program program) {
        List<Table> tables = tablesDao.selectTablesByProgramIdWithEdgesRowsFields(program.getId());
        if(tables.isEmpty())
            return false;;
        return true;
    }

    @Override
    public void deleteProgramTable(Table table) {
        tablesDao.deleteTable(table);
    }

    @Override
    public boolean checkIfTableIsAssignedToAnyPeriod(Table table) {
        List<KidTable> kidTablesAssignedToTable = kidTablesDao.selectKidTablesByTable(table);

        if(kidTablesAssignedToTable.isEmpty())
            return false;
        return true;
    }
}