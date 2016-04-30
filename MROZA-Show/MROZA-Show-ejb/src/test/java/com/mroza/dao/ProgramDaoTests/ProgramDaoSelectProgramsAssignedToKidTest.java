package com.mroza.dao.ProgramDaoTests;


import com.mroza.dao.ProgramsDao;
import com.mroza.models.Kid;
import com.mroza.models.Program;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.ReflectionWrapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ProgramDaoSelectProgramsAssignedToKidTest {


    private DatabaseUtils databaseUtils;
    private Kid kid;
    private Program program;
    private Program notAssignedProgram;
    private Program programAssignedToOtherKid;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        this.kid = databaseUtils.setUpKid("CODE");
        Kid otherKid = databaseUtils.setUpKid("OTHER_CODE");
        this.program = databaseUtils.setUpProgram("SYMBOL_ASSIGNED", "NAME_ASSIGNED", "DESCRIPTION_ASSIGNED", kid);
        this.notAssignedProgram = databaseUtils.setUpProgram("SYMBOL_NOT_ASSIGNED", "NAME_NOT_ASSIGNED", "DESCRIPTION_NOT_ASSIGNED");
        this.programAssignedToOtherKid = databaseUtils.setUpProgram("SYMBOL_ASSIGNED_TO_OTHER_KID", "NAME_ASSIGNED_TO_OTHER_KID", "DESCRIPTION_ASSIGNED_TO_OTHER_KID", otherKid);

    }

    @After
    public void teardown() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void selectProgramsAssignedToKidTest() {
        SqlSession sqlSession = databaseUtils.getSqlSession();
        ProgramsDao programsDao = new ProgramsDao();
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);

        List<Program> programsAssignedToKid = programsDao.selectProgramAssignedToKid(this.kid);
        sqlSession.commit();

        Assert.assertTrue("Programs assigned to kid should contain only expected program", programsAssignedToKid.stream().anyMatch(
                (program) -> program.getId().equals(this.program.getId())));
        Assert.assertFalse("Programs assigned to kid should not contain unexpected program", programsAssignedToKid.stream().anyMatch(
                (program) -> program.getId().equals(this.notAssignedProgram.getId())));
        Assert.assertFalse("Programs assigned to kid should not contain unexpected program", programsAssignedToKid.stream().anyMatch(
                (program) -> program.getId().equals(this.programAssignedToOtherKid.getId())));
    }


}
