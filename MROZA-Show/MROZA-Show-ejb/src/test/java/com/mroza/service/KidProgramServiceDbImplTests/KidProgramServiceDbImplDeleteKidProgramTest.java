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
import com.mroza.models.Kid;
import com.mroza.models.Program;
import com.mroza.service.KidProgramsServiceDbImpl;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.ReflectionWrapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

public class KidProgramServiceDbImplDeleteKidProgramTest {

    private DatabaseUtils databaseUtils;
    private KidProgramsServiceDbImpl kidProgramsService;
    private Program program;
    private Kid kid;
    private Program assignedProgram;
    private SqlSession sqlSession;

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
    public void deleteKidProgramTest(){

        kidProgramsService.deleteKidProgram(this.assignedProgram);
        sqlSession.commit();

        assertNull(databaseUtils.getProgramById(this.assignedProgram.getId()));
        assertNotNull(databaseUtils.getProgramById(this.program.getId()));
    }

    private void setUpPrograms() {
        this.kid = databaseUtils.setUpKid("CODE");
        this.program = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION");
        this.assignedProgram = databaseUtils.setUpAssignedProgram(this.kid, this.program);
    }

}
