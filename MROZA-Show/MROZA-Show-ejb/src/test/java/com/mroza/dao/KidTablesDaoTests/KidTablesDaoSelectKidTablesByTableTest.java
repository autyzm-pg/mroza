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

package com.mroza.dao.KidTablesDaoTests;

import com.mroza.dao.KidTablesDao;
import com.mroza.models.*;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KidTablesDaoSelectKidTablesByTableTest {

    private DatabaseUtils databaseUtils;
    private Table table;
    private KidTable expectedKidTable;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        setUpKidTables();
    }

    @After
    public void teardown() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void selectKidTablesByTableTest() {
        SqlSession sqlSession = databaseUtils.getSqlSession();
        KidTablesDao kidTablesDao = new KidTablesDao();
        ReflectionWrapper.setPrivateField(kidTablesDao, "sqlSession", sqlSession);

        List<KidTable> kidTables = kidTablesDao.selectKidTablesByTable(table);

        assertTrue("Table should have only one kidTables assigned", kidTables.size() == 1);
        assertEquals("Table should have assign expected kidTable", expectedKidTable.getId(), kidTables.get(0).getId());
      }



    private void setUpKidTables() {
        Kid kid = databaseUtils.setUpKid("CODE");
        Program program = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION", kid);

        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        table = databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, program);

        Date startDate = Utils.getDateFromNow(-2);
        Date endDate = Utils.getDateFromNow(2);
        Period period = databaseUtils.setUpPeriod(startDate, endDate, kid);

        expectedKidTable = databaseUtils.setUpKidTable(table, period);
    }

}


