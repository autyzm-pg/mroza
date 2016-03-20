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
import com.mroza.interfaces.KidPeriodsService;
import com.mroza.models.*;
import com.mroza.qualifiers.KidPeriodsServiceImpl;
import org.mybatis.cdi.Transactional;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@KidPeriodsServiceImpl
public class KidPeriodsServiceDbImpl implements Serializable, KidPeriodsService {

    @Inject
    private KidTablesDao kidTablesDao;

    @Inject
    private TablesDao tablesDao;

    @Inject
    private ResolvedFieldsDao resolvedFieldsDao;

    @Inject
    private PeriodsDao periodsDao;

    @Inject
    private ProgramsDao programsDao;

    @Override
    public KidTable getKidTableById(Integer kidTableId) {
        return kidTablesDao.selectKidTableByIdWithEdgeTable(kidTableId);
    }

    @Override
    public List<Table> getTablesByProgramId(Integer programId) {
        return tablesDao.selectTablesByProgramIdWithEdgesRowsFields(programId);
    }

    @Override
    @Transactional
    public void replaceAssignmentTableToPeriod(KidTable previousKidTable, Table newTable) {
        kidTablesDao.deleteKidTableById(previousKidTable.getId());
        this.assignTableToPeriod(newTable, new Period(previousKidTable.getPeriodId()));
    }

    @Override
    public void deleteKidTable(KidTable table) {
        kidTablesDao.deleteKidTableById(table.getId());
    }

    @Override
    public void changeIOAForKidTable(KidTable kidTable) {
        // TODO: generate/remove IOA kidTable
//        TODO: test
        kidTablesDao.updateKidTableIOA(kidTable);
    }

    @Override
    public void assignTableToPeriod(Table table, Period period) {
        KidTable newKidTable = new KidTable(
                table.isLearningCollecting(), table.isGeneralizationCollecting(), table, period);
        kidTablesDao.insertKidTable(newKidTable);
    }

    @Override
    public boolean hasKidTableFilledResolvedFields(KidTable kidTable) {
        List<ResolvedField> nonEmptyResolvedFields = resolvedFieldsDao.selectNonEmptyResolvedFieldsByKidTableId(
                kidTable.getId());
        return !(nonEmptyResolvedFields.size() == 0);
    }

    @Override
    public boolean hasTableFilledResolvedFields(Table table) {
        List<ResolvedField> nonEmptyResolvedFields = resolvedFieldsDao.selectNonEmptyResolvedFieldsByTableId(table.getId());
        return !(nonEmptyResolvedFields.size() == 0);
    }

    @Override
    @Transactional
    public void addPeriod(Period period) {
//        TODO: refactor this ugly model mix
        periodsDao.insertPeriod(period);
        if(period.getKidTables() != null && !period.getKidTables().isEmpty()) {
            for(KidTable table : period.getKidTables())
                table.setPeriodId(period.getId());
            period.getKidTables().forEach(kidTablesDao::insertKidTable);
        }
    }

    @Override
    public void updatePeriod(Period period) {
        periodsDao.updatePeriodBeginAndEndDate(period);
    }

    @Override
    public List<KidTable> getKidTablesWithTableAndProgramByPeriodId(Integer periodId) {
        return kidTablesDao.selectKidTableByPeriodIdWithEdgeTableProgram(periodId);
    }

    @Override
    public List<KidTable> getKidTablesWithTableAndProgramByPeriod(Period period) {
        if(period != null && period.getId() != null)
            return kidTablesDao.selectKidTableByPeriodIdWithEdgeTableProgram(period.getId());
        else
            return new ArrayList<>();
    }

    @Override
    public List<KidTable> getKidTablesWithTableAndRowAndFieldAndProgramByPeriod(Period period) {
        if(period != null && period.getId() != null)
            return kidTablesDao.selectKidTableByPeriodIdWithEdgeTableRowFieldProgram(period.getId());
        else
            return new ArrayList<>();
    }

    @Override
    public List<Program> getKidProgramsNotInPeriod(int periodId) {
        return programsDao.selectKidProgramsNotInPeriodByPeriodId(periodId);
    }

    @Override
    public List<KidTable> getCopyOfKidTables(List<KidTable> kidTables) {
        return kidTables.stream().map(KidTable::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removePeriod(Period period) {
        if(period.getKidTables() != null && !period.getKidTables().isEmpty()) {
            List<Integer> ids = new ArrayList<>();
            for (KidTable table : period.getKidTables())
                ids.add(table.getId());
            kidTablesDao.deleteKidTables(ids);
        }
        periodsDao.removePeriod(period);
    }
}
