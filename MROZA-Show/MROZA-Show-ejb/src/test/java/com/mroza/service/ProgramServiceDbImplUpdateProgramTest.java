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
import com.mroza.models.Kid;
import com.mroza.models.Program;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.ReflectionWrapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProgramServiceDbImplUpdateProgramTest {

    private ProgramsServiceDbImpl programsServiceDb;
    private Program program;
    private Program assignedProgram;
    private DatabaseUtils databaseUtils;
    private SqlSession sqlSession;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        setUpAssignedProgramToKid();
        ProgramsDao programsDao = new ProgramsDao();
        sqlSession = databaseUtils.getSqlSession();
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);
        this.programsServiceDb = new ProgramsServiceDbImpl();
        ReflectionWrapper.setPrivateField(this.programsServiceDb, "programsDao", programsDao);
    }

    @After
    public void after() {
        databaseUtils.cleanUpDatabase();
    }


    @Test
    public void editProgramsPropertiesTest() {

        String changedName = "NEW NAME";
        String changedSymbol= "NEW SYMBOL";
        String changedDescription = "NEW DESCRIPTION";

        this.program.setName(changedName);
        this.program.setSymbol(changedSymbol);
        this.program.setDescription(changedDescription);

        this.programsServiceDb.updateProgramsWithSymbol(this.program);
        sqlSession.commit();

        Program changedProgram = databaseUtils.getProgramById(this.program.getId());
        Program changedAssignedProgram = databaseUtils.getProgramById(this.assignedProgram.getId());;

        assertEquals("Program should have changed name", changedName , changedProgram.getName());
        assertEquals("Program should have changed symbol", changedSymbol, changedProgram.getSymbol());
        assertEquals("Program should have changed description", changedDescription, changedProgram.getDescription());

        assertEquals("Assigned program should have changed name", changedName, changedAssignedProgram.getName());
        assertEquals("Assigned program should have changed symbol", changedSymbol, changedAssignedProgram.getSymbol());
        assertEquals("Assigned program should have changed description", changedDescription, changedAssignedProgram.getDescription());

    }


    private void setUpAssignedProgramToKid() {
        databaseUtils.cleanUpDatabase();
        Kid kid = databaseUtils.setUpKid("CODE");
        this.program = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION");
        this.assignedProgram = databaseUtils.setUpAssignedProgram(kid, program);
    }
}
