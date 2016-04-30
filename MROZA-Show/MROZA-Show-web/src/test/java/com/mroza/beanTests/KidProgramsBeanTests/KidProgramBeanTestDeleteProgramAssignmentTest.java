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
import com.mroza.interfaces.KidProgramsService;
import com.mroza.models.*;
import com.mroza.service.KidProgramsServiceDbImpl;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.Utils;
import com.mroza.viewbeans.kidprograms.beans.KidProgramsBean;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertFalse;

public class KidProgramBeanTestDeleteProgramAssignmentTest {

    private KidProgramsBean kidProgramsBean;
    private DatabaseUtils databaseUtils;
    private Program assignedProgram;
    private Program assignedProgramWithTableFieldsResolved;
    private Kid kid;
    private SqlSession sqlSession;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        setUpAssignmentPrograms();

        ProgramsDao programsDao = new ProgramsDao();
        sqlSession = databaseUtils.getSqlSession();
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);
        KidProgramsService kidProgramsService = new KidProgramsServiceDbImpl();

        ReflectionWrapper.setPrivateField(kidProgramsService, "programsDao", programsDao);
        this.kidProgramsBean = new KidProgramsBean();
        ReflectionWrapper.setPrivateField(this.kidProgramsBean, "kidProgramsService", kidProgramsService);
    }

    @After
    public void after() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void deleteProgramTest() {
        this.kidProgramsBean.deleteKidProgram(this.assignedProgram);
        sqlSession.commit();
        List<Program> programs = databaseUtils.getProgramAssignedToKid(this.kid);

        assertFalse("Deleted program should not be assigned to kid", programs.stream().anyMatch((program -> program.getId().equals(this.assignedProgram.getId()))));
        assertTrue("Not deleted program should be assigned to kid", programs.stream().anyMatch((program -> program.getId().equals(this.assignedProgramWithTableFieldsResolved.getId()))));
    }



    @Test
    public void deleteProgramWithResolvedFieldsTest() {
        this.kidProgramsBean.deleteKidProgram(this.assignedProgramWithTableFieldsResolved);
        sqlSession.commit();
        List<Program> programs = databaseUtils.getProgramAssignedToKid(this.kid);
        assertTrue("Program with resolved fields should not be deleted", programs.stream().anyMatch((program -> program.getId().equals(this.assignedProgramWithTableFieldsResolved.getId()))));

    }


    private void setUpAssignmentPrograms() {
        this.kid = databaseUtils.setUpKid("CODE");
        this.assignedProgram = databaseUtils.setUpProgram("SYMBOL_WITHOUT_TABLES", "NAME_WITHOUT_TABLES", "DESCRIPTION_WITHOUT_TABLES", kid);
        this.assignedProgramWithTableFieldsResolved = databaseUtils.setUpProgram("SYMBOL_WITH_TABLE_FIELDS_RESOLVED", "NAME_WITH_TABLE_FIELDS_RESOLVED", "DESCRIPTION_WITH_TABLE_FIELDS_RESOLVED", kid);

        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        Table table = databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, this.assignedProgramWithTableFieldsResolved);

        Date startDate = Utils.getDateFromNow(-2);
        Date endDate = Utils.getDateFromNow(2);
        Period period = databaseUtils.setUpPeriod(startDate, endDate, kid);

        KidTable kidTable = databaseUtils.setUpKidTable(table, period);
        databaseUtils.fillKidTableWithData(kidTable);
    }




}
