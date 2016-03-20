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


public class ChildTableRepositoryGetWhenLetterFilterTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {


    private Context targetContext;
    private ChildTable expectedChildTableForActualTermStartedWithLetterA;
    private Child child;
    private ChildTable expectedChildTableForHistoryTermStartedWithLetterA;
    private ChildTable expectedChildTableForFutureTermStartedWithLetterA;

    public ChildTableRepositoryGetWhenLetterFilterTest() {
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
        List<Program> actualProgramsStartedWithLetterA = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionActual, "PROGRAM_NAME", "A_PROGRAM_SYMBOL");
        List<Program> actualProgramsStartedWithLetterB = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionActual, "PROGRAM_NAME", "B_PROGRAM_SYMBOL");
        Program actualProgramStartedWithLetterA = actualProgramsStartedWithLetterA.get(0);
        expectedChildTableForActualTermStartedWithLetterA = actualProgramStartedWithLetterA.getTableTemplateList().get(0).getChildTableList().get(0);

        TermSolution termSolutionHistory = TestUtils.setUpTermSolution(targetContext, child, -8, -6);
        List<Program> historyProgramsStartedWithLetterA = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionHistory, "PROGRAM_NAME", "A_PROGRAM_SYMBOL");
        List<Program> historyProgramsStartedWithLetterB = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionHistory, "PROGRAM_NAME", "B_PROGRAM_SYMBOL");
        Program historyProgramStartedWithLetterA = historyProgramsStartedWithLetterA.get(0);
        expectedChildTableForHistoryTermStartedWithLetterA = historyProgramStartedWithLetterA.getTableTemplateList().get(0).getChildTableList().get(0);

        TermSolution termSolutionFuture = TestUtils.setUpTermSolution(targetContext, child, 8, 16);
        List<Program> futureProgramsStartedWithLetterA = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionFuture, "PROGRAM_NAME", "A_PROGRAM_SYMBOL");
        List<Program> futureProgramsStartedWithLetterB = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionFuture, "PROGRAM_NAME", "B_PROGRAM_SYMBOL");
        Program futureProgramStartedWithLetterA = futureProgramsStartedWithLetterA.get(0);
        expectedChildTableForFutureTermStartedWithLetterA = futureProgramStartedWithLetterA.getTableTemplateList().get(0).getChildTableList().get(0);

    }

    public void testChildTableRepositoryGetActualChildTablesForTermWhenProgramSymbolStartWithLetter() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getChildTablesWhereProgramsSymbolStartingWithLetter(targetContext, "A", ChooseProgramActivity.Term.ACTUAL, child);
        assertEquals("ReturnedList should contain only one position", 1, returnedChildTables.size());
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableForActualTermStartedWithLetterA, returnedChildTable);
        assertEquals("ChildTables should start with letter A", true, result);
    }

    public void testChildTableRepositoryGetHistoryChildTablesForTermWhenProgramSymbolStartWithLetter() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getChildTablesWhereProgramsSymbolStartingWithLetter(targetContext, "A", ChooseProgramActivity.Term.HISTORICAL, child);
        assertEquals("ReturnedList should contain only one position", 1, returnedChildTables.size());
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableForHistoryTermStartedWithLetterA, returnedChildTable);
        assertEquals("Returned HistoryChildTables should start with letter A", true, result);
    }

    public void testChildTableRepositoryGetFutureChildTablesForTermWhenProgramSymbolStartWithLetter() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getChildTablesWhereProgramsSymbolStartingWithLetter(targetContext, "A", ChooseProgramActivity.Term.FUTURE, child);
        assertEquals("ReturnedList should contain only one position", 1, returnedChildTables.size());
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableForFutureTermStartedWithLetterA, returnedChildTable);
        assertEquals("Returned FutureChildTables should start with letter A", true, result);
    }

}