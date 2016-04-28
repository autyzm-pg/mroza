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
import com.mroza.models.Program;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.ReflectionWrapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProgramDaoUpdateProgramTest {

    private DatabaseUtils databaseUtils;
    private Program program;
    private Kid kid;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        this.kid = databaseUtils.setUpKid("CODE");
        this.program = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION");
        this.program.setName("NEW NAME");
        this.program.setDescription("NEW DESCRIPTION");
        this.program.setSymbol("NEW SYMBOL");
    }

    @After
    public void teardown() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void updateProgramTest() {
        SqlSession sqlSession = databaseUtils.getSqlSession();
        ProgramsDao programsDao = new ProgramsDao();
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);

        programsDao.updateProgram(this.program);
        sqlSession.commit();

        Program changedProgram = programsDao.selectProgramById(this.program.getId());

        Assert.assertEquals("Updated program's name should have been changed", this.program.getName(), changedProgram.getName());
        Assert.assertEquals("Updated program's symbol should have been changed", this.program.getSymbol(), changedProgram.getSymbol());
        Assert.assertEquals("Updated program's description should have been changed", this.program.getDescription(), changedProgram.getDescription());
    }


    @Test
    public void updateAssignedProgramTest() {
        SqlSession sqlSession = databaseUtils.getSqlSession();
        ProgramsDao programsDao = new ProgramsDao();
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);

        Program assignedProgram = databaseUtils.setUpAssignedProgram(this.kid, this.program);
        programsDao.updateProgram(this.program);
        sqlSession.commit();

        Program changedProgram = programsDao.selectProgramById(assignedProgram.getId());

        Assert.assertEquals("Updated program's assigned to kid program's name should have been changed", this.program.getName(), changedProgram.getName());
        Assert.assertEquals("Updated program's assigned to kid program's symbol should have been changed", this.program.getSymbol(), changedProgram.getSymbol());
        Assert.assertEquals("Updated program's assigned to kid program's description should have been changed", this.program.getDescription(), changedProgram.getDescription());
    }
}
