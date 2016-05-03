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


import adapters.TableElementAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import database.*;
import mroza.forms.ProgramActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProgramActivityForFutureTest extends ActivityInstrumentationTestCase2<ProgramActivity> {

    private ChildTable childTable;

    public ProgramActivityForFutureTest() {
        super(ProgramActivity.class);
        }

    protected void setUp() throws Exception {
        super.setUp();

        Context targetContext = getInstrumentation().getTargetContext();
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);

        List<Child> children = TestUtils.setUpChildren(targetContext,1, "CODE");
        TermSolution historyTermSolution = TestUtils.setUpTermSolution(targetContext, children.get(0), 10, 15);
        List<Program> programs = TestUtils.setUpProgramToTermSolution(targetContext, 1, historyTermSolution, "Uczenie literek", "A123");
        this.childTable = programs.get(0).getTableTemplateList().get(0).getChildTableList().get(0);

        Intent intent = new Intent();
        intent.putExtra("CHILD_TABLE_ID", (long) 1);
        intent.putExtra("CHILD_ID", (long) 1);
        setActivityIntent(intent);
    }

    public void testTableContentButtonsForTableFromFutureTermSolution() throws Exception {
        Activity mainActivity = this.getActivity();
        ListView tableListView = (ListView) mainActivity.findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());

        List<TableRow> rows = childTable.getTableTemplate().getTableRowList();
        for (int i = 0; i < rows.size(); i++) {
            View elementView = adapter.getView(i, null, null);
            for (int j = 0; j < ((ViewGroup) elementView).getChildCount() - 1; j++) {
                ImageButton button = (ImageButton) ((ViewGroup) elementView).getChildAt(j + 1); //at index 0 is TextView with row name
                int state = (i + j) % 3;
                for(int k = 0; k < state; k++)
                    assertEquals("Table +/- buttons should be enabled", true, button.isEnabled());
            }
        }


        Button saveButton = (Button) getActivity().findViewById(R.id.buttonSave);
        assertEquals("Save button should be enabled", true, saveButton.isEnabled());

        Button finishTeachingButton = (Button) getActivity().findViewById(R.id.buttonEndTeaching);
        assertEquals("Finish teaching button should be enabled", true, finishTeachingButton.isEnabled());

        Button finishGeneralizationButton = (Button) getActivity().findViewById(R.id.buttonEndGeneral);
        assertEquals("Finish generalization button should be enabled", true, finishGeneralizationButton.isEnabled());
    }

}
