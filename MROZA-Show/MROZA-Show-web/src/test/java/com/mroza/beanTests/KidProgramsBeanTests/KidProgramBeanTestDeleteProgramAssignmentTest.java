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

package com.mroza.beanTests.KidProgramsBeanTests;

import com.mroza.models.Kid;
import com.mroza.models.Program;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.Utils;
import com.mroza.viewbeans.kidprograms.beans.KidProgramsBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;

public class KidProgramBeanTestDeleteProgramAssignmentTest {

    private KidProgramsBean kidProgramsBean;
    private DatabaseUtils databaseUtils;
    private Program assignedProgram;
    private Program assignedProgramWithTableFieldsResolved;

    @Before
    public void setup() {
        databaseUtils = new DatabaseUtils();
        setUpAssignmentPrograms();
        this.kidProgramsBean = mock(KidProgramsBean.class);
    }

    @After
    public void after() {
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void deleteProgram() {
        this.kidProgramsBean.deleteProgram(this.assignedProgram);


    }


    private void setUpAssignmentPrograms() {
        Kid kid = databaseUtils.setUpKid("CODE");
        this.assignedProgram = databaseUtils.setUpProgram("SYMBOL_WITHOUT_TABLES", "NAME_WITHOUT_TABLES", "DESCRIPTION_WITHOUT_TABLES", kid);
        this.assignedProgramWithTableFieldsResolved = databaseUtils.setUpProgram("SYMBOL_WITH_TABLE_FIELDS_RESOLVED", "NAME_WITH_TABLE_FIELDS_RESOLVED", "DESCRIPTION_WITH_TABLE_FIELDS_RESOLVED", kid);
    }




}
