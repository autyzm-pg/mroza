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

import repositories.TermSolutionRepository;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import database.Child;
import database.TermSolution;
import junit.framework.Assert;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.TestUtils.CompareUtils;
import mroza.forms.TestUtils.TestUtils;

import java.util.List;


public class TermSolutionRepositoryTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {


    private Context targetContext;
    private Child child;
    private TermSolution expectedTermSolutionActual;
    private TermSolution expectedTermSolutionHistorical;
    private TermSolution expectedTermSolutionFuture;

    public TermSolutionRepositoryTest() {
        super(ChooseProgramActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);

        List<Child> childs = TestUtils.setUpChildren(targetContext, 1, "CHILD_NUMBER_");
        child = childs.get(0);
        expectedTermSolutionActual = TestUtils.setUpTermSolution(targetContext, child, -2, 2);
        expectedTermSolutionHistorical = TestUtils.setUpTermSolution(targetContext,child, -10, -5);
        expectedTermSolutionFuture = TestUtils.setUpTermSolution(targetContext, child, 6, 15);
    }

    public void testGetTermSolutionActual() {
        TermSolution foundTermSolution = TermSolutionRepository.getTermSolutionByTerm(targetContext, ChooseProgramActivity.Term.ACTUAL, child);
        boolean compareResult = CompareUtils.areTermSolutionsTheSame(foundTermSolution, expectedTermSolutionActual);
        Assert.assertEquals("Actual term solutions should be the same", true, compareResult);
    }

    public void testGetTermSolutionHistorical() {
        TermSolution foundTermSolution = TermSolutionRepository.getTermSolutionByTerm(targetContext, ChooseProgramActivity.Term.HISTORICAL, child);
        boolean compareResult = CompareUtils.areTermSolutionsTheSame(foundTermSolution, expectedTermSolutionHistorical);
        Assert.assertEquals("Historical term solutions should be the same", true, compareResult);
    }

    public void testGetTermSolutionFuture() {
        TermSolution foundTermSolution = TermSolutionRepository.getTermSolutionByTerm(targetContext, ChooseProgramActivity.Term.FUTURE, child);
        boolean compareResult = CompareUtils.areTermSolutionsTheSame(foundTermSolution, expectedTermSolutionFuture);
        Assert.assertEquals("Future term solutions should be the same", true, compareResult);
    }
}