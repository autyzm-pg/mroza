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

package com.mroza.dao.ProgramDaoTests;

import com.mroza.dao.KidsDao;
import com.mroza.dao.ProgramsDao;
import com.mroza.models.Kid;
import com.mroza.models.Program;
import com.mroza.utils.ReflectionWrapper;
import com.mroza.utils.Utils;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ProgramsDaoTest {

    private static KidsDao kidsDao;
    private static List<Kid> exampleKids;
    private static SqlSession sqlSession;

    @Before
    public void setup() {
        kidsDao = new KidsDao();
        exampleKids = new ArrayList<>();
        sqlSession = Utils.getSqlSession();
        ReflectionWrapper.setPrivateField(kidsDao, "sqlSession", sqlSession);
    }

    @After
    public void teardown() {
        Utils.kidsInitializedTearDown(exampleKids);
    }

    @Test
    public void getKidProgramsNotInPeriodTest() {
        Utils.initKidDeeply(exampleKids, "QWE");
        Utils.initKidDeeply(exampleKids, "ABC");
        ProgramsDao programsDao = new ProgramsDao();
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);

        List<Program> programsNotInPeriod = programsDao.selectKidProgramsNotInPeriodByPeriodId(
                exampleKids.get(0).getPeriods().get(0).getId());
        Assert.assertEquals("Program 3", programsNotInPeriod.get(0).getName());
        Assert.assertEquals(1, programsNotInPeriod.size());
    }
}
