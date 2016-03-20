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

package com.mroza.dao;

import com.mroza.models.Kid;
import com.mroza.models.charts.SimplifiedResolvedTabRow;
import com.mroza.models.queries.ResolvedTabQuery;
import com.mroza.utils.ReflectionWrapper;
import com.mroza.utils.Utils;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class KidTablesDaoTest {

    private static KidTablesDao kidTablesDao;
    private static KidsDao kidsDao;
    private static List<Kid> exampleKids;
    private static SqlSession sqlSession;

    @Before
    public void setup() {
        kidTablesDao = new KidTablesDao();
        kidsDao = new KidsDao();
        exampleKids = new ArrayList<>();
        sqlSession = Utils.getSqlSession();
        ReflectionWrapper.setPrivateField(kidTablesDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(kidsDao, "sqlSession", sqlSession);
        Utils.initKidDeeply(exampleKids, "TFG");
    }

    @After
    public void teardown() {
        Utils.kidsInitializedTearDown(exampleKids);
    }

    @Test
    public void getKidProgramsNotInPeriodTest() {
        ResolvedTabQuery query = new ResolvedTabQuery
                (exampleKids.get(0).getPrograms().get(0).getTables().get(0).getKidTables().get(0).getId(), "U");

        List<SimplifiedResolvedTabRow> result = kidTablesDao.selectResolvedFieldsForKidTab(query);
        Assert.assertNotNull(result);
        Assert.assertTrue(!result.isEmpty());
        Assert.assertEquals(3, result.get(0).getRow().size());
    }
}
