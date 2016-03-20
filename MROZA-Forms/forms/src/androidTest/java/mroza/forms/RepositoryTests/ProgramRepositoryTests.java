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

package mroza.forms.RepositoryTests;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import database.Child;
import database.ChildTable;
import database.Program;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.TestUtils.CompareUtils;
import mroza.forms.TestUtils.TestUtils;
import repositories.ProgramRepository;

import java.util.ArrayList;
import java.util.List;


public class ProgramRepositoryTests extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {

    private Context targetContext;
    private List<Program> expectedPrograms;
    private Program expectedProgram;
    private List<ChildTable> childTables;
    private ChildTable childTable;

    public ProgramRepositoryTests() {
            super(ChooseProgramActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);

        List<Child> childs = TestUtils.setUpChildren(targetContext, 1, "CODE");
        expectedPrograms = TestUtils.setUpPrograms(targetContext, childs, 3, "PROGRAM_NAME", "PROGRAM_SYMBOL");
        expectedProgram = expectedPrograms.get(0);
        childTable = expectedProgram.getTableTemplateList().get(0).getChildTableList().get(0);

        childTables = new ArrayList<>();
        for(Program program : expectedPrograms)
        {
            childTables.add(program.getTableTemplateList().get(0).getChildTableList().get(0));
        }

    }

    public void testProgramRepositoryGetProgramForChildTable() {

        Program foundProgram = ProgramRepository.getProgramForChildTable(targetContext, childTable);
        boolean result = CompareUtils.areTwoProgramsTheSame(expectedProgram, foundProgram);
        junit.framework.Assert.assertEquals("Programs should be the same", true, result);
    }

    public void testProgramRepositoryGetProgramsForChildTables() {

        List<Program> returnedPrograms = ProgramRepository.getProgramsForChildTables(targetContext, childTables);
        boolean found;

        if(returnedPrograms.size() != expectedPrograms.size())
            fail("ReturnedPrograms are not the same length as expectedPrograms");

        for(Program returnedProgram : returnedPrograms)
        {
            found = false;
            for(Program expectedProgram : expectedPrograms )
            {
                if(CompareUtils.areTwoProgramsTheSame(expectedProgram, returnedProgram))
                    found = true;
            }
            if(!found)
                fail("ReturnedProgram was not found on expected list");

        }
    }


}
