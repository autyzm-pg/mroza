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
import database.TermSolution;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.TestUtils.CompareUtils;
import mroza.forms.TestUtils.TestUtils;
import repositories.ChildTablesRepository;

import java.util.List;


public class ChildTableRepositoryGetTeachingSavedTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {


    private Context targetContext;
    private Child child;
    private ChildTable expectedChildTableWhereTeachingSaved;
    private ChildTable expectedHistoryChildTableWhereTeachingSaved;
    private ChildTable expectedFutureChildTableWhereTeachingSaved;

    public ChildTableRepositoryGetTeachingSavedTest() {
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
        expectedChildTableWhereTeachingSaved = TestUtils.setUpChildTable(targetContext, TestUtils.childTableOption.TEACHING_SAVED, child, termSolutionActual, "PROGRAM_NAME", "PROGRAM_SYMBOL");
        ChildTable childTableWhereTeachingFinished = TestUtils.setUpChildTable(targetContext, TestUtils.childTableOption.TEACHING_FINISHED, child, termSolutionActual, "PROGRAM_NAME", "PROGRAM_SYMBOL");

        TermSolution termSolutionHistory = TestUtils.setUpTermSolution(targetContext, child, -8, -6);
        expectedHistoryChildTableWhereTeachingSaved = TestUtils.setUpChildTable(targetContext, TestUtils.childTableOption.TEACHING_SAVED, child, termSolutionHistory, "PROGRAM_NAME_HISTORY", "PROGRAM_SYMBOL");
        ChildTable historyChildTableWhereTeachingFinished = TestUtils.setUpChildTable(targetContext, TestUtils.childTableOption.TEACHING_FINISHED, child, termSolutionHistory, "PROGRAM_NAME_HISTORY", "PROGRAM_SYMBOL");


        TermSolution termSolutionFuture = TestUtils.setUpTermSolution(targetContext, child, 8, 16);
        expectedFutureChildTableWhereTeachingSaved = TestUtils.setUpChildTable(targetContext, TestUtils.childTableOption.TEACHING_SAVED, child, termSolutionFuture, "PROGRAM_NAME_FUTURE", "PROGRAM_SYMBOL");
        ChildTable futureChildTableWhereTeachingFinished= TestUtils.setUpChildTable(targetContext, TestUtils.childTableOption.TEACHING_FINISHED, child, termSolutionFuture, "PROGRAM_NAME_FUTURE", "PROGRAM_SYMBOL");

    }

    public void testChildTableRepositoryGetActualChildTablesWithTeachingSaved() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getTeachingSavedChildTables(targetContext, ChooseProgramActivity.Term.ACTUAL, child);
        assertEquals("ReturnedList should contain only one position", 1, returnedChildTables.size());
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedChildTableWhereTeachingSaved, returnedChildTable);
        assertEquals("ChildTable should contain only childTables where teaching was saved", true, result);
    }

    public void testChildTableRepositoryGetHistoryChildTablesWithTeachingSaved() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getTeachingSavedChildTables(targetContext, ChooseProgramActivity.Term.HISTORICAL, child);
        assertEquals("ReturnedList should contain only one position", 1, returnedChildTables.size());
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedHistoryChildTableWhereTeachingSaved, returnedChildTable);
        assertEquals("HistoryChildTableList should contain only childTables where teaching was saved", true, result);
    }

    public void testChildTableRepositoryGetFutureChildTablesWithTeachingSaved() {

        List<ChildTable> returnedChildTables = ChildTablesRepository.getTeachingSavedChildTables(targetContext, ChooseProgramActivity.Term.FUTURE, child);
        assertEquals("ReturnedList should contain only one position", 1, returnedChildTables.size());
        ChildTable returnedChildTable = returnedChildTables.get(0);
        boolean result = CompareUtils.areTwoChildTablesTheSame(expectedFutureChildTableWhereTeachingSaved, returnedChildTable);
        assertEquals("FutureChildTableList should contain only childTables where teaching was saved", true, result);
    }



}
