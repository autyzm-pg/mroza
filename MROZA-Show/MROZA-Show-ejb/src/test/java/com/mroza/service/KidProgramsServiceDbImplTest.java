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
import com.mroza.interfaces.KidProgramsService;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class KidProgramsServiceDbImplTest {

    private static KidsDao kidsDao;
    private static ProgramsDao programsDao;
    private static List<Kid> exampleKids;
    private static List<Program> examplePrograms;
    private static SqlSession sqlSession;
    private static KidProgramsService kidProgramsService;

    @Before
    public void setup() {
        kidsDao = new KidsDao();
        programsDao = new ProgramsDao();
        examplePrograms = new ArrayList<>();
        exampleKids = new ArrayList<>();

        sqlSession = Utils.getSqlSession();
        ReflectionWrapper.setPrivateField(kidsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);
        kidProgramsService = new KidProgramsServiceDbImpl();
        ReflectionWrapper.setPrivateField(kidProgramsService, "programsDao", programsDao);
    }

    @After
    public void teardown() {
        Utils.kidsInitializedTearDown(exampleKids);
        Utils.programsInitializedTearDown(examplePrograms);
    }

    @Test
    public void getKidTableByIdTest() {
        Utils.initKidDeeply(exampleKids, "QWE");
        Utils.initWithBasicPrograms(examplePrograms);
        List<Program> unusedPrograms = kidProgramsService.getUnusedProgramsByKidId(exampleKids.get(0).getId());
        Assert.assertEquals(3, unusedPrograms.size());
    }

    @Test
    public void changeProgramFinishedStatusTest() {
        Utils.initWithBasicPrograms(examplePrograms);
        examplePrograms.stream().forEach(exampleProgram -> {
            kidProgramsService.changeProgramFinishedStatus(exampleProgram.getId(), true);
        });
        List<Program> resultedPrograms = programsDao.selectAllTemplatePrograms();
        Assert.assertEquals(true, resultedPrograms.get(0).isFinished());
    }

    @Test
    public void assignProgramToKidTest() {
        Utils.initKidDeeply(exampleKids, "QWE");
        Utils.initWithBasicPrograms(examplePrograms);
        Program assignedProgram = kidProgramsService.assignProgramToKid(
                exampleKids.get(0).getId(), examplePrograms.get(0).getId());
        Assert.assertEquals(examplePrograms.get(0).getSymbol(), assignedProgram.getSymbol());
        Assert.assertEquals(examplePrograms.get(0).getDescription(), assignedProgram.getDescription());
        Assert.assertEquals(examplePrograms.get(0).getName(), assignedProgram.getName());
        Assert.assertEquals(exampleKids.get(0).getId(), assignedProgram.getKidId());

    }



}
