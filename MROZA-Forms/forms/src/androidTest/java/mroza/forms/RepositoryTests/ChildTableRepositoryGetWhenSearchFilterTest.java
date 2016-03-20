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


public class ChildTableRepositoryGetWhenSearchFilterTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {


    private Context targetContext;
    private ChildTable expectedChildTableForActualTermWithSearch;
    private Child child;
    private ChildTable expectedChildTableForHistoryTermWithSearch;
    private ChildTable expectedChildTableForFutureTermWithSearch;

    public ChildTableRepositoryGetWhenSearchFilterTest() {
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
        List<Program> actualProgramsWithSearchQuery = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionActual, "SEARCH_PROGRAM_NAME", "A_PROGRAM_SYMBOL");
        List<Program> actualPrograms = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionActual, "PROGRAM_NAME", "B_PROGRAM_SYMBOL");
        Program actualProgramWithSearchQuery = actualProgramsWithSearchQuery.get(0);
        expectedChildTableForActualTermWithSearch = actualProgramWithSearchQuery.getTableTemplateList().get(0).getChildTableList().get(0);

        TermSolution termSolutionHistory = TestUtils.setUpTermSolution(targetContext, child, -8, -6);
        List<Program> historyProgramsWithSearchQuery = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionHistory, "SEARCH_PROGRAM_NAME", "A_PROGRAM_SYMBOL");
        List<Program> historyPrograms = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionHistory, "PROGRAM_NAME", "B_PROGRAM_SYMBOL");
        Program historyProgramWithSearchQuery = historyProgramsWithSearchQuery.get(0);
        expectedChildTableForHistoryTermWithSearch = historyProgramWithSearchQuery.getTableTemplateList().get(0).getChildTableList().get(0);

        TermSolution termSolutionFuture = TestUtils.setUpTermSolution(targetContext, child, 8, 16);
        List<Program> futureProgramsWithSearchQuery = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionFuture, "SEARCH_PROGRAM_NAME", "A_PROGRAM_SYMBOL");
        List<Program> futurePrograms = TestUtils.setUpProgramToTermSolution(targetContext, 1, termSolutionFuture, "PROGRAM_NAME", "B_PROGRAM_SYMBOL");
        Program futureProgramWithSearchQuery = futureProgramsWithSearchQuery.get(0);
        expectedChildTableForFutureTermWithSearch = futureProgramWithSearchQuery.getTableTemplateList().get(0).getChildTableList().get(0);

    }

    public void testChildTableRepositoryGetActualChildTablesForTermWhenSearchQuery() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getChildTablesByTermSelectedBySearch(targetContext, "SEARCH_", ChooseProgramActivity.Term.ACTUAL, child);
        assertEquals("ReturnedList should contain only one position", 1, returnedChildTables.size());
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableForActualTermWithSearch, returnedChildTable);
        assertEquals("ChildTables should contain search query", true, result);
    }

    public void testChildTableRepositoryGetHistoryChildTablesForTermWhenSearchQuery() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getChildTablesByTermSelectedBySearch(targetContext, "SEARCH_", ChooseProgramActivity.Term.HISTORICAL, child);
        assertEquals("ReturnedList should contain only one position", 1, returnedChildTables.size());
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableForHistoryTermWithSearch, returnedChildTable);
        assertEquals("Returned HistoryChildTables should contain search query", true, result);
    }

    public void testChildTableRepositoryGetFutureChildTablesForTermWhenSearchQuery() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getChildTablesByTermSelectedBySearch(targetContext, "SEARCH_", ChooseProgramActivity.Term.FUTURE, child);
        assertEquals("ReturnedList should contain only one position", 1, returnedChildTables.size());
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableForFutureTermWithSearch, returnedChildTable);
        assertEquals("Returned FutureChildTables should contain search query", true, result);
    }

}