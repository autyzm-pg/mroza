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

package com.mroza.beanTests.KidProgramsBeanTests;

import com.mroza.ReflectionWrapper;
import com.mroza.dao.ProgramsDao;
import com.mroza.dao.TablesDao;
import com.mroza.interfaces.KidProgramsService;
import com.mroza.models.*;
import com.mroza.service.KidProgramsServiceDbImpl;
import com.mroza.utils.DatabaseUtils;
import com.mroza.viewbeans.kidprograms.beans.KidProgramsBean;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertFalse;

public class KidProgramBeanTestDeleteProgramAssignmentTest {

    private KidProgramsBean kidProgramsBean;
    private DatabaseUtils databaseUtils;
    private Program assignedProgram;
    private Program assignedProgramWithTables;
    private Kid kid;
    private SqlSession sqlSession;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        setUpAssignmentPrograms();

        ProgramsDao programsDao = new ProgramsDao();
        TablesDao tablesDao = new TablesDao();
        sqlSession = databaseUtils.getSqlSession();
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(tablesDao, "sqlSession", sqlSession);
        KidProgramsService kidProgramsService = new KidProgramsServiceDbImpl();

        ReflectionWrapper.setPrivateField(kidProgramsService, "programsDao", programsDao);
        ReflectionWrapper.setPrivateField(kidProgramsService, "tablesDao", tablesDao);
        this.kidProgramsBean = new KidProgramsBean();
        ReflectionWrapper.setPrivateField(this.kidProgramsBean, "kidProgramsService", kidProgramsService);
    }

    @After
    public void after() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void deleteProgramTest() {

        KidProgramsBean kidProgramsBeanSpy = spy(this.kidProgramsBean);
        doNothing().when(kidProgramsBeanSpy).refreshAssignedKidProgramsList(any());

        kidProgramsBeanSpy.deleteKidProgram(this.assignedProgram);
        sqlSession.commit();
        List<Program> programs = databaseUtils.getProgramAssignedToKid(this.kid);

        assertFalse("Deleted program should not be assigned to kid", programs.stream().anyMatch(
                program -> program.getId().equals(this.assignedProgram.getId())));

        assertTrue("Not deleted program should be assigned to kid", programs.stream().anyMatch(
            program -> program.getId().equals(this.assignedProgramWithTables.getId())));
    }



    @Test
    public void deleteProgramWithTablesTest() {
        KidProgramsBean kidProgramsBeanSpy = spy(this.kidProgramsBean);
        doNothing().when(kidProgramsBeanSpy).showErrorMessage(any());

        kidProgramsBeanSpy.deleteKidProgram(this.assignedProgramWithTables);
        sqlSession.commit();
        List<Program> programs = databaseUtils.getProgramAssignedToKid(this.kid);
        assertTrue("Program with tables should not be deleted", programs.stream().anyMatch((program -> program.getId().equals(this.assignedProgramWithTables.getId()))));

    }


    private void setUpAssignmentPrograms() {
        this.kid = databaseUtils.setUpKid("CODE");
        this.assignedProgram = databaseUtils.setUpProgram("SYMBOL_WITHOUT_TABLES", "NAME_WITHOUT_TABLES", "DESCRIPTION_WITHOUT_TABLES", kid);
        this.assignedProgramWithTables = databaseUtils.setUpProgram("SYMBOL_WITH_TABLES", "NAME_WITH_TABLES", "DESCRIPTION_WITH_TABLES", kid);

        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, this.assignedProgramWithTables);
    }




}
