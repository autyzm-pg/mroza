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
import com.mroza.interfaces.KidsService;
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

public class KidsServiceDbImplTest {

    private static KidsDao kidsDao;
    private static List<Kid> exampleKids;
    private static SqlSession sqlSession;
    private static KidsService kidsService;

    @Before
    public void setup() {

        kidsDao = new KidsDao();
        exampleKids = new ArrayList<>();
        sqlSession = Utils.getSqlSession();
        kidsService = new KidsServiceDbImpl();

        ReflectionWrapper.setPrivateField(kidsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(kidsService, "kidsDao", kidsDao);
    }

    @After
    public void teardown() {
        Utils.kidsInitializedTearDown(exampleKids);
    }

    @Test
    public void getAllKidsTest() {
        Utils.initWithBasicKids(exampleKids);
        List<Kid> kids = kidsService.getAllKids();
        Assert.assertEquals(2, kids.size());
    }

    @Test
    public void getKidDetailedData() {
        Utils.initKidDeeply(exampleKids, "QWE");
        Kid kidWithDetailedData = kidsService.getKidDetailedData(exampleKids.get(0).getId());

        Assert.assertEquals(exampleKids.get(0).getPrograms().size(), kidWithDetailedData.getPrograms().size());
        Assert.assertEquals(exampleKids.get(0).getPeriods().size(), kidWithDetailedData.getPeriods().size());
    }

    @Test
    public void existsKidWithCode() {
        List<String> codes = new ArrayList<String>(){{ add("QWE");}};
        Utils.initWithBasicKidWithCode(exampleKids, codes);
        Boolean addedKidExistence = kidsService.existsKidWithCode(codes.get(0));
        Boolean notAddedKidExistence = kidsService.existsKidWithCode("ABC");

        Assert.assertTrue("Added kid should be found as existed",addedKidExistence);
        Assert.assertFalse("Not added kid should not be found as existed", notAddedKidExistence);
    }


}
