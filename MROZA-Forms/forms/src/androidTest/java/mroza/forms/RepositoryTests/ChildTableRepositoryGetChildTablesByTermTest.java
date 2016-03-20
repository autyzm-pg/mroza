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
import database.TermSolution;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.TestUtils.CompareUtils;
import mroza.forms.TestUtils.TestUtils;
import repositories.ChildTablesRepository;

import java.util.List;


public class ChildTableRepositoryGetChildTablesByTermTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {


    private Context targetContext;
    private ChildTable expectedChildTableForActualTerm;
    private Child child;
    private ChildTable expectedChildTableForHistoricalTerm;
    private ChildTable expectedChildTableForFutureTerm;

    public ChildTableRepositoryGetChildTablesByTermTest() {
        super(ChooseProgramActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);

        List<Child> childs = TestUtils.setUpChildren(targetContext, 1, "CODE");
        child = childs.get(0);
        TermSolution termSolutionActual = TestUtils.setUpTermSolution(targetContext, child, -3, 3);
        List<Program> actualPrograms = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionActual, "PROGRAM_NAME", "PROGRAM_SYMBOL");
        Program actualProgram = actualPrograms.get(0);
        expectedChildTableForActualTerm = actualProgram.getTableTemplateList().get(0).getChildTableList().get(0);

        TermSolution termSolutionHistory = TestUtils.setUpTermSolution(targetContext, child, -8, -6);
        List<Program> historyPrograms = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionHistory, "PROGRAM_NAME", "PROGRAM_SYMBOL");
        Program historyProgram = historyPrograms.get(0);
        expectedChildTableForHistoricalTerm = historyProgram.getTableTemplateList().get(0).getChildTableList().get(0);

        TermSolution termSolutionFuture = TestUtils.setUpTermSolution(targetContext, child, 8, 16);
        List<Program> futurePrograms = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionFuture, "PROGRAM_NAME", "PROGRAM_SYMBOL");
        Program futureProgram = futurePrograms.get(0);
        expectedChildTableForFutureTerm = futureProgram.getTableTemplateList().get(0).getChildTableList().get(0);

    }

    public void testChildTableRepositoryGetActualChildTablesForTerm() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getChildTableByTerm(targetContext, ChooseProgramActivity.Term.ACTUAL, child);
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableForActualTerm, returnedChildTable);
        assertEquals("ChildTables should be the same", true, result);
    }


    public void testChildTableRepositoryGetHistoryChildTablesForTerm() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getChildTableByTerm(targetContext, ChooseProgramActivity.Term.HISTORICAL, child);
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableForHistoricalTerm, returnedChildTable);
        assertEquals("HistoricalChildTables should be the same", true, result);
    }

    public void testChildTableRepositoryGetFutureChildTablesForTerm() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getChildTableByTerm(targetContext, ChooseProgramActivity.Term.FUTURE, child);
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableForFutureTerm, returnedChildTable);
        assertEquals("FutureChildTables should be the same", true, result);
    }

}
