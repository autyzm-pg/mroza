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

import com.mroza.dao.KidTablesDao;
import com.mroza.dao.TablesDao;
import com.mroza.models.Kid;
import com.mroza.models.Period;
import com.mroza.models.Program;
import com.mroza.models.Table;
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
import static org.junit.Assert.assertTrue;

public class KidProgramServiceDbImplCheckIfTableIsAssignedToAnyPeriodTest {


    private DatabaseUtils databaseUtils;
    private KidProgramsServiceDbImpl kidProgramsService;
    private Table tableAssignedToPeriod;
    private Table tableNotAssignedToPeriod;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        SqlSession sqlSession = databaseUtils.getSqlSession();
        KidTablesDao kidTablesDao = new KidTablesDao();
        ReflectionWrapper.setPrivateField(kidTablesDao, "sqlSession", sqlSession);
        kidProgramsService = new KidProgramsServiceDbImpl();
        ReflectionWrapper.setPrivateField(kidProgramsService, "kidTablesDao", kidTablesDao);
        setUpPrograms();
    }

    @After
    public void teardown() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void checkIfTableIsAssignedToAnyPeriodTest(){
        assertTrue("Table should be assigned to period", kidProgramsService.checkIfTableIsAssignedToAnyPeriod(tableAssignedToPeriod));
        assertFalse("Table should not be assigned to period", kidProgramsService.checkIfTableIsAssignedToAnyPeriod(tableNotAssignedToPeriod));

    }

    private void setUpPrograms() {
        Kid kid = databaseUtils.setUpKid("CODE");
        Program program = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION", kid);

        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        tableAssignedToPeriod = databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, program);
        tableNotAssignedToPeriod = databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, program);

        Date startDate = Utils.getDateFromNow(-2);
        Date endDate = Utils.getDateFromNow(2);
        Period period = databaseUtils.setUpPeriod(startDate, endDate, kid);

        databaseUtils.setUpKidTable(tableAssignedToPeriod, period);

    }
}
