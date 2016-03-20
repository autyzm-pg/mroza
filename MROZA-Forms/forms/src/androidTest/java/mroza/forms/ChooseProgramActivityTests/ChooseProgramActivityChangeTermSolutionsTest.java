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

package mroza.forms.ChooseProgramActivityTests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.TextView;
import database.Child;
import database.TermSolution;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChooseProgramActivityChangeTermSolutionsTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {

    private TermSolution termSolutionHistory;
    private TermSolution termSolutionFuture;
    private TermSolution termSolutionActual;
    private Context targetContext;
    private Button futureButton;
    private Button previousButton;
    private List<Child> childs;

    public ChooseProgramActivityChangeTermSolutionsTest() {
        super(ChooseProgramActivity.class);
    }

    protected void setUp() throws Exception {


        targetContext = getInstrumentation().getTargetContext();

        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        this.childs = TestUtils.setUpChildren(targetContext, 1, "CODE");
        this.termSolutionActual = TestUtils.setUpTermSolution(targetContext, this.childs.get(0), -1, 1);
        this.termSolutionHistory = TestUtils.setUpTermSolution(targetContext, this.childs.get(0), -2, -1);
        this.termSolutionFuture = TestUtils.setUpTermSolution(targetContext, this.childs.get(0), 1,3);
        TestUtils.setUpProgramToTermSolution(targetContext, 1, this.termSolutionActual, "NAME", "CODE");
        TestUtils.setUpProgramToTermSolution(targetContext, 1, this.termSolutionHistory, "NAME", "CODE");
        TestUtils.setUpProgramToTermSolution(targetContext, 1, this.termSolutionFuture, "NAME", "CODE");


        Intent childNameActivityParameter = new Intent();
        childNameActivityParameter.putExtra("CHILD_NAME",  this.childs.get(0).getCode());
        childNameActivityParameter.putExtra("CHILD_ID", this.childs.get(0).getId());
        setActivityIntent(childNameActivityParameter);
        setActivityInitialTouchMode(true);

        previousButton = (Button) getActivity().findViewById(R.id.buttonChangePeriodHistorical);
        futureButton = (Button)getActivity().findViewById(R.id.buttonChangePeriodFuture);
    }


    public void testChangingTermSolutions() throws InterruptedException, ParseException {
        Activity mainActivity = this.getActivity();

        String startDateShow = ((TextView) mainActivity.findViewById(R.id.termSolutionStartDate)).getText().toString();
        String endDateShow = ((TextView) mainActivity.findViewById(R.id.termSolutionEndDate)).getText().toString();

        Date startDate = formatDateFromString(startDateShow);
        Date endDate = formatDateFromString(endDateShow);

        assertEquals("Actual term start date should be the same",termSolutionActual.getStartDate(),startDate);
        assertEquals("Actual term end date should be the same", termSolutionActual.getEndDate(), endDate);

        TouchUtils.clickView(this, futureButton);
        Thread.sleep(5000);

        startDateShow = ((TextView) mainActivity.findViewById(R.id.termSolutionStartDate)).getText().toString();
        endDateShow = ((TextView) mainActivity.findViewById(R.id.termSolutionEndDate)).getText().toString();

        startDate = formatDateFromString(startDateShow);
        endDate = formatDateFromString(endDateShow);

        assertEquals("Future term start date should be the same",termSolutionFuture.getStartDate(),startDate);
        assertEquals("Futurel term end date should be the same", termSolutionFuture.getEndDate(), endDate);

        TouchUtils.clickView(this, previousButton);
        Thread.sleep(5000);
        TouchUtils.clickView(this, previousButton);
        Thread.sleep(5000);

        startDateShow = ((TextView) mainActivity.findViewById(R.id.termSolutionStartDate)).getText().toString();
        endDateShow = ((TextView) mainActivity.findViewById(R.id.termSolutionEndDate)).getText().toString();
        startDate = formatDateFromString(startDateShow);
        endDate = formatDateFromString(endDateShow);

        assertEquals("Historical term start date should be the same",termSolutionHistory.getStartDate(),startDate);
        assertEquals("Historical term end date should be the same", termSolutionHistory.getEndDate(), endDate);

    }

    private Date formatDateFromString(String startDateShow) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        return formatter.parse(startDateShow);
    }

}
