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

import com.mroza.dao.TablesDao;
import com.mroza.models.*;
import com.mroza.service.KidProgramsServiceDbImpl;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.ReflectionWrapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
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
        TablesDao tablesDao = new TablesDao();
        ReflectionWrapper.setPrivateField(tablesDao, "sqlSession", sqlSession);
        kidProgramsService = new KidProgramsServiceDbImpl();
        ReflectionWrapper.setPrivateField(kidProgramsService, "tablesDao", tablesDao);
        setUpPrograms();
    }

    @After
    public void teardown() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void checkIfProgramHasAnyTablesTest(){
        assertFalse("Program should not have any tables with collected data", kidProgramsService.checkIfProgramHasTables(this.assignedProgram));
        assertTrue("Program should have tables with collected data", kidProgramsService.checkIfProgramHasTables(this.assignedProgramWithResolvedFields));

    }

    private void setUpPrograms() {
        this.kid = databaseUtils.setUpKid("CODE");
        this.assignedProgram = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION", this.kid);
        this.assignedProgramWithResolvedFields = databaseUtils.setUpProgram("SYMBOL_WITH_COLLECTED_DATA", "NAME_WITH_COLLECTED_DATA", "DESCRIPTION_WITH_COLLECTED_DATA", this.kid);
        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, this.assignedProgramWithResolvedFields);
    }

}
