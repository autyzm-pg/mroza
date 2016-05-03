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
import com.mroza.utils.ReflectionWrapper;
import com.mroza.utils.Utils;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class KidPeriodsServiceDbImplTest {

    private static KidsDao kidsDao;
    private static List<Kid> exampleKids;
    private static SqlSession sqlSession;
    private static KidPeriodsService kidPeriodsService;
    private static KidTablesDao kidTablesDao;
    private static TablesDao tablesDao;
    private static ResolvedFieldsDao resolvedFieldsDao;
    private static PeriodsDao periodsDao;
    private static ProgramsDao programsDao;

    @Before
    public void setup() {
        kidsDao = new KidsDao();
        kidTablesDao = new KidTablesDao();
        tablesDao = new TablesDao();
        exampleKids = new ArrayList<>();
        sqlSession = Utils.getSqlSession();
        resolvedFieldsDao = new ResolvedFieldsDao();
        periodsDao = new PeriodsDao();
        programsDao = new ProgramsDao();
        kidPeriodsService = new KidPeriodsServiceDbImpl();

        ReflectionWrapper.setPrivateField(kidsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(kidTablesDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(tablesDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(resolvedFieldsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(periodsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(kidPeriodsService, "kidTablesDao", kidTablesDao);
        ReflectionWrapper.setPrivateField(kidPeriodsService, "tablesDao", tablesDao);
        ReflectionWrapper.setPrivateField(kidPeriodsService, "resolvedFieldsDao", resolvedFieldsDao);
        ReflectionWrapper.setPrivateField(kidPeriodsService, "periodsDao", periodsDao);
        ReflectionWrapper.setPrivateField(kidPeriodsService, "programsDao", programsDao);
        Utils.initKidDeeply(exampleKids, "QWE");
    }

    @After
    public void teardown() {
        Utils.kidsInitializedTearDown(exampleKids);
    }

    @Test
    public void getKidTableByIdTest() {
        KidTable expectedKidTable = kidTablesDao.getKidsTablesByNote("Notatka 2").get(0);

        KidTable resultedKidTable = kidPeriodsService.getKidTableById(expectedKidTable.getId());

        Assert.assertTrue(new KidTableArgumentMatcher(expectedKidTable).matches(resultedKidTable));
    }

    @Test
    public void getTablesByProgramIdTest() {
        List<Table> resultedTables = kidPeriodsService.getTablesByProgramId(exampleKids.get(0).getPrograms().get(0).getId());

        Assert.assertEquals(2, resultedTables.size());

        Table testingTable;
        if (resultedTables.get(0).getName().equals("Prog 1 - krok 1"))
            testingTable = resultedTables.get(0);
        else
            testingTable = resultedTables.get(0);

        Assert.assertEquals(new Integer(3), testingTable.getNumberOfLearningCols());
        Assert.assertEquals(new Integer(2), testingTable.getNumberOfGeneralizationCols());
    }

    @Test
    public void replaceAssignmentTableToPeriodTest() {
        KidTable previousKidTable = kidTablesDao.getKidsTablesByNote("Notatka 2").get(0);
        Table newTable = exampleKids.get(0).getPrograms().get(0).getTables().get(1);

        kidPeriodsService.replaceAssignmentTableToPeriod(previousKidTable, newTable);

        sqlSession.commit();
        List<KidTable> resultedKidTables = kidTablesDao.selectKidTablesByTableName(newTable.getName());
        Assert.assertEquals(1, resultedKidTables.size());
    }

    @Test
    public void assignTableToPeriodTest() {
        Table exampleTable = new Table();
        exampleTable.setId(5);
        List<TableRow> tableRowList = new ArrayList<>();
        for (int rowNumber = 0; rowNumber < 5; rowNumber++)
            tableRowList.add(new TableRow("Akcja", rowNumber, 3, 0));
        exampleTable.setTableRows(tableRowList);
        Period examplePeriod = new Period();
        examplePeriod.setId(10);
        KidTable expectedKidTable = new KidTable(true, false,  exampleTable, examplePeriod);

        KidTablesDao kidTablesDaoMock = mock(KidTablesDao.class);
        ReflectionWrapper.setPrivateField(kidPeriodsService, "kidTablesDao", kidTablesDaoMock);

        kidPeriodsService.assignTableToPeriod(exampleTable, examplePeriod);
        verify(kidTablesDaoMock).insertKidTable((KidTable) argThat(new KidTableArgumentMatcher(expectedKidTable)));
    }

    @Test
    public void hasKidTableResultFieldsTest() {
        KidTable kidTable = kidTablesDao.getKidsTablesByNote("Notatka").get(0);

        boolean result = kidPeriodsService.hasKidTableFilledResolvedFields(kidTable);

        Assert.assertTrue(result);
    }

    @Test
    public void hasKidTableResultFieldsWhenHasOnlyEmptyTest() {
        KidTable kidTable = kidTablesDao.getKidsTablesByNote("Notatka 2").get(0);

        boolean result = kidPeriodsService.hasKidTableFilledResolvedFields(kidTable);

        Assert.assertFalse(result);
    }

    @Test
    public void hasTableResultFieldsTest() {
        Table tab = exampleKids.get(0).getPrograms().get(0).getTables().get(0);
        boolean result = kidPeriodsService.hasTableFilledResolvedFields(tab);
        Assert.assertTrue(result);
    }

    @Test
    public void hasTableResultFieldsWhenHasOnlyEmptyTest() {
        Table tab = exampleKids.get(0).getPrograms().get(0).getTables().get(1);
        boolean result = kidPeriodsService.hasTableFilledResolvedFields(tab);
        Assert.assertFalse(result);
    }

    @Test
    public void addPeriodTest() {
        Period examplePeriod = new Period(
                Utils.strToDate("14-12-2015"), Utils.strToDate("17-12-2015"), exampleKids.get(0).getId());

        kidPeriodsService.addPeriod(examplePeriod);
        sqlSession.commit();

        Period resultedPeriod = periodsDao.selectPeriodById(examplePeriod.getId());
        Assert.assertEquals(examplePeriod.getBeginDate(), resultedPeriod.getBeginDate());
        Assert.assertEquals(examplePeriod.getEndDate(), resultedPeriod.getEndDate());
    }

    @Test
    public void updatePeriodTest() {
        Period examplePeriod = exampleKids.get(0).getPeriods().get(0);
        examplePeriod.setBeginDate(Utils.strToDate("14-12-2015"));
        examplePeriod.setEndDate(Utils.strToDate("17-12-2015"));

        kidPeriodsService.updatePeriod(examplePeriod);
        sqlSession.commit();

        Period resultedPeriod = periodsDao.selectPeriodById(examplePeriod.getId());
        Assert.assertEquals(examplePeriod.getBeginDate(), resultedPeriod.getBeginDate());
        Assert.assertEquals(examplePeriod.getEndDate(), resultedPeriod.getEndDate());
    }

    @Test
    public void getKidTableByPeriodIdTest() {
        List<KidTable> kidTables = kidPeriodsService.getKidTablesWithTableAndProgramByPeriodId(exampleKids.get(0).getPeriods().get(0).getId());

        Assert.assertEquals(2, kidTables.size());
        Assert.assertNotNull(kidTables.get(0).getTable().getProgram());
    }

    @Test
    public void getKidProgramsNotInPeriodTest() {
        List<Program> programs = kidPeriodsService.getKidProgramsNotInPeriod(exampleKids.get(0).getPeriods().get(1).getId());
        Assert.assertEquals(exampleKids.get(0).getPrograms().size(), programs.size());
    }

    @Test
    public void removePeriodTest() {
        Period period = periodsDao.selectPeriodById(exampleKids.get(0).getPeriods().get(0).getId());
        KidTable kidTable = kidTablesDao.selectKidTableByIdWithEdgeTable(
                exampleKids.get(0).getPeriods().get(0).getKidTables().get(0).getId());
        Assert.assertNotNull(period);
        Assert.assertNotNull(kidTable);

        kidPeriodsService.removePeriod(exampleKids.get(0).getPeriods().get(0));
        period = periodsDao.selectPeriodById(exampleKids.get(0).getPeriods().get(0).getId());
        kidTable = kidTablesDao.selectKidTableByIdWithEdgeTable(exampleKids.get(0).getPeriods().get(0).getId());
        Assert.assertNull(period);
        Assert.assertNull(kidTable);
    }
}
