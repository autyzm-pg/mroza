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

import com.mroza.dao.ProgramsDao;
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


public class ProgramsServiceDbImplTest {

    private static List<Program> examplePrograms;
    private static ProgramsDao programsDao;
    private static SqlSession sqlSession;
    private static ProgramsServiceDbImpl programsServiceDb;

    @Before
    public void setup() {
        examplePrograms = new ArrayList<>();
        programsDao = new ProgramsDao();
        sqlSession = Utils.getSqlSession();
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);
        programsServiceDb = new ProgramsServiceDbImpl();
        ReflectionWrapper.setPrivateField(programsServiceDb, "programsDao", programsDao);
    }

    @After
    public void teardown() {
        Utils.programsInitializedTearDown(examplePrograms);
    }

    @Test
    public void getAllTemplateProgramsTest() {
        Utils.initWithBasicPrograms(examplePrograms);

        List<Program> allPrograms = programsServiceDb.getAllTemplatePrograms();

        Assert.assertEquals(3, allPrograms.size());
    }

    @Test
    public void addProgramTest() {
        Program program = new Program("UJU");
        examplePrograms.add(program);

        programsServiceDb.addProgram(program);

        List<Program> programs = programsDao.selectAllTemplatePrograms();
        Assert.assertNotNull(program.getId());
        Assert.assertEquals(1, programs.size());
    }

    @Test
    public void existsProgramWithSymbolNotExistsTest() {
        Utils.initWithBasicPrograms(examplePrograms);

        Assert.assertFalse(programsServiceDb.existsProgramWithSymbol("GGG"));
    }


    @Test
    public void existsProgramWithSymbolExistsTest() {
        Utils.initWithBasicPrograms(examplePrograms);

        Assert.assertTrue(programsServiceDb.existsProgramWithSymbol("ABC"));
    }

    @Test
    public void existsProgramWithSymbolExistsMultiTest() {
        Utils.initWithMultiPrograms(examplePrograms);

        Assert.assertTrue(programsServiceDb.existsProgramWithSymbol("ABC"));
    }
}
