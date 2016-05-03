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

package mroza.forms.ProgramActivityTests;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;

import database.*;
import mroza.forms.ProgramActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;
import java.util.List;

public class ProgramActivityRestartTableTest extends ActivityInstrumentationTestCase2<ProgramActivity> {

    private Context targetContext;

    public ProgramActivityRestartTableTest() {
        super(ProgramActivity.class);
    }

    ChildTable childTable;

    protected void setUp() throws Exception {
        super.setUp();

        targetContext = getInstrumentation().getTargetContext();

        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);

        List<Child> children = TestUtils.setUpChildren(targetContext,1, "CODE");
        TermSolution historyTermSolution = TestUtils.setUpTermSolution(targetContext, children.get(0), -10, -5);
        List<Program> programs = TestUtils.setUpProgramToTermSolution(targetContext, 1, historyTermSolution, "Uczenie literek", "A123");
        this.childTable = programs.get(0).getTableTemplateList().get(0).getChildTableList().get(0);

        Intent intent = new Intent();
        intent.putExtra("CHILD_TABLE_ID", (long) 1);
        intent.putExtra("CHILD_ID", (long) 1);
        setActivityIntent(intent);
    }


    public void testRestartTable() throws InterruptedException {

        ProgramActivity mainActivity = this.getActivity();
        Button buttonRestart = (Button) mainActivity.findViewById(R.id.buttonRestart);

        assertEquals("Teaching and generalization aren't finished restart button should be disabled", buttonRestart.isEnabled(), false);

        //finish teaching and generalization
        Button buttonEndTeaching = (Button) mainActivity.findViewById(R.id.buttonEndTeaching);
        TouchUtils.clickView(this, buttonEndTeaching);
        Thread.sleep(1000);

        AlertDialog dialog = ((ProgramActivity) mainActivity).getLastAlertDialog();
        assertNotNull("Finish teaching alert dialog should be shown", dialog);

        if (dialog.isShowing()) {
            try {
                performClick(dialog.getButton(DialogInterface.BUTTON_POSITIVE));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        //check if restart button is enabled
        assertEquals("Teaching and generalization are finished restart button should be enabled", buttonRestart.isEnabled(), true);

        Thread.sleep(5000);
        //restart table
        TouchUtils.clickView(this, buttonRestart);

        dialog = ((ProgramActivity) mainActivity).getLastAlertDialog();
        assertNotNull("Restart alert dialog should be shown", dialog);

        if (dialog.isShowing()) {
            try {
                performClick(dialog.getButton(DialogInterface.BUTTON_POSITIVE));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        //check if teaching / generalization fields are set to not finished
        DaoSession daoSession = TestUtils.getDaoSession(targetContext);
        childTable = daoSession.getChildTableDao().load(childTable.getId());
        assertEquals("Teaching shouldn't be finished", false, childTable.getIsTeachingFinished());
        assertEquals("Generalization shouldn't be finished", false, childTable.getIsGeneralizationFinished());

        //check if save dates are set to null
        assertEquals("Teaching date should be null", null, childTable.getTeachingFillOutDate());
        assertEquals("Generalization date should be null", null, childTable.getGeneralizationFillOutDate());

        //check if restart button is disabled again
        assertEquals("Teaching and generalization are cleaned restart button should be disabled", false, buttonRestart.isEnabled());
    }




    private void performClick(final Button button) throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
    }
}
