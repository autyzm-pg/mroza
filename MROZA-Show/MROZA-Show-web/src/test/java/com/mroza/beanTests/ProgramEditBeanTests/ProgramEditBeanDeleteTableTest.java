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
package com.mroza.beanTests.ProgramEditBeanTests;

import com.mroza.ReflectionWrapper;
import com.mroza.dao.KidTablesDao;
import com.mroza.dao.TablesDao;
import com.mroza.interfaces.KidProgramsService;
import com.mroza.models.*;
import com.mroza.service.KidProgramsServiceDbImpl;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.Utils;
import com.mroza.viewbeans.ProgramEditBean;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.testng.AssertJUnit.assertFalse;

public class ProgramEditBeanDeleteTableTest {

    private ProgramEditBean programEditBean;
    private Table programTable;
    private Program program;
    private DatabaseUtils databaseUtils;
    private Table programTableAssignedToPeriod;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        prepareProgramTables();

        TablesDao tablesDao = new TablesDao();
        KidTablesDao kidTablesDao = new KidTablesDao();
        SqlSession sqlSession = databaseUtils.getSqlSession();
        ReflectionWrapper.setPrivateField(tablesDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(kidTablesDao, "sqlSession", sqlSession);
        KidProgramsService kidProgramsService = new KidProgramsServiceDbImpl();
        ReflectionWrapper.setPrivateField(kidProgramsService, "tablesDao", tablesDao);
        ReflectionWrapper.setPrivateField(kidProgramsService, "kidTablesDao", kidTablesDao);
        programEditBean = new ProgramEditBean();
        ReflectionWrapper.setPrivateField(programEditBean, "kidProgramsService", kidProgramsService);

    }

    @After
    public void after() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void deleteProgramTableTest() {
        ProgramEditBean programEditBeanSpy = spy(programEditBean);
        doNothing().when(programEditBeanSpy).deleteTableFromTableListInView(any());

        programEditBeanSpy.deleteTable(programTable);
        List<Table> tables = databaseUtils.getTablesForProgram(program);
        assertFalse("Program table should have deleted", tables.stream()
                .anyMatch(table -> programTable.getId() == table.getId()));
    }

    @Test
    public void deleteProgramTableAssignedToPeriodTest() {
        ProgramEditBean programEditBeanSpy = spy(programEditBean);
        doNothing().when(programEditBeanSpy).showErrorMessage(any());

        programEditBeanSpy.deleteTable(programTableAssignedToPeriod);
        List<Table> tables = databaseUtils.getTablesForProgram(program);
        assertTrue("Assigned program table should have not deleted", tables.stream()
                .anyMatch(table -> programTableAssignedToPeriod.getId() == table.getId()));
    }

    private void prepareProgramTables() {

        databaseUtils.cleanUpDatabase();
        Kid kid = databaseUtils.setUpKid("CODE");
        program = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION", kid);
        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        programTable = databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, program);
        programTableAssignedToPeriod = databaseUtils.setUpTableWithRows("TABLE_NAME_ASSIGN_TO_PERIOD", rowNames, "DESCRIPTION", 2, 2, program);

        Date startDate = Utils.getDateFromNow(-2);
        Date endDate = Utils.getDateFromNow(2);
        Period period = databaseUtils.setUpPeriod(startDate, endDate, kid);
        databaseUtils.setUpKidTable(programTableAssignedToPeriod, period);
    }


}
