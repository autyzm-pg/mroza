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

package com.mroza.service.KidProgramServiceDbImplTests;

import com.mroza.dao.ProgramsDao;
import com.mroza.models.*;
import com.mroza.service.KidProgramsServiceDbImpl;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.ReflectionWrapper;
import com.mroza.utils.Utils;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertTrue;

public class KidProgramServiceDbImplCheckIfProgramHasAnyCollectedTablesTest {
    private DatabaseUtils databaseUtils;
    private SqlSession sqlSession;
    private KidProgramsServiceDbImpl kidProgramsService;
    private Kid kid;
    private Program assignedProgram;
    private Program assignedProgramWithResolvedFields;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        sqlSession = databaseUtils.getSqlSession();
        ProgramsDao programsDao = new ProgramsDao();
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);
        kidProgramsService = new KidProgramsServiceDbImpl();
        ReflectionWrapper.setPrivateField(kidProgramsService, "programsDao", programsDao);
        setUpPrograms();
    }

    @After
    public void teardown() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void checkIfProgramHasAnyTablesWithCollectedDataTest(){

        assertFalse("Program should not have any tables with collected data",kidProgramsService.checkIfProgramHasAnyCollectedTables(this.assignedProgram));
        assertTrue("Program should have tables with collected data", kidProgramsService.checkIfProgramHasAnyCollectedTables(this.assignedProgramWithResolvedFields));

    }

    private void setUpPrograms() {
        this.kid = databaseUtils.setUpKid("CODE");
        this.assignedProgram = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION", this.kid);
        setUpKidTable(this.assignedProgram, this.kid);

        this.assignedProgramWithResolvedFields = databaseUtils.setUpProgram("SYMBOL_WITH_COLLECTED_DATA", "NAME_WITH_COLLECTED_DATA", "DESCRIPTION_WITH_COLLECTED_DATA", this.kid);
        KidTable kidTableWithResolvedFields = setUpKidTable(this.assignedProgramWithResolvedFields, this.kid);

        databaseUtils.fillKidTableWithData(kidTableWithResolvedFields);
    }

    private KidTable setUpKidTable(Program program, Kid kid) {
        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        Table table = databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, program);

        Date startDate = Utils.getDateFromNow(-2);
        Date endDate = Utils.getDateFromNow(2);
        Period period = databaseUtils.setUpPeriod(startDate, endDate, kid);

        return databaseUtils.setUpKidTable(table, period);
    }

}
