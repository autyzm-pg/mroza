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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import database.*;
import database.TableRow;
import mroza.forms.ProgramActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProgramActivityFinishGeneralizationTest extends ActivityInstrumentationTestCase2<ProgramActivity> {

    private Context targetContext;

    public ProgramActivityFinishGeneralizationTest() {
        super(ProgramActivity.class);
    }

    DaoMaster daoMaster;
    Child child;
    ChildTable childTable;
    Intent intent;

    final int rowsNumber = 3;
    final int teachingNumber = 2;
    final int generalizationNumber = 1;


    protected void setUp() throws Exception {
        super.setUp();

        targetContext = getInstrumentation().getTargetContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(targetContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        setUpChild("AK123", child = new Child((long) 1), childTable = new ChildTable((long) 1));

        intent = new Intent();
        intent.putExtra("CHILD_TABLE_ID", (long) 1);
        intent.putExtra("CHILD_ID", (long) 1);
        setActivityIntent(intent);
    }

    private void setUpChild(String code, Child child, ChildTable childTable) throws ParseException {
        DaoSession daoSession = daoMaster.newSession();
        child.setCode(code);
        child.setIsArchived(false);
        ChildDao childDao = daoSession.getChildDao();
        childDao.insertOrReplace(child);

        TermSolution termSolution = new TermSolution();
        Calendar c = Calendar.getInstance();
        termSolution.setChild(child);
        int dayStart = c.get(Calendar.DAY_OF_MONTH) - 5;
        int dayEnd = c.get(Calendar.DAY_OF_MONTH) + 5;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date dateStart = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayStart);
        Date dateEnd = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayEnd);
        termSolution.setStartDate(dateStart);
        termSolution.setEndDate(dateEnd);
        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();
        termSolutionDao.insertOrReplace(termSolution);

        setUpProgram(child, childTable, termSolution, "A123", "Teach letters", "C234", "Teach letters");
    }

    private void setUpProgram(Child child, ChildTable childTable, TermSolution termSolution, String programSymbol, String programName, String tableSymbol, String tableName) {
        DaoSession daoSession = daoMaster.newSession();
        ProgramDao programDao = daoSession.getProgramDao();
        Program program = new Program();
        program.setChild(child);
        program.setCreateDate(new Date());
        program.setDescription("DESCRIPTION");
        program.setIsFinished(false);
        program.setName(programName);
        program.setSymbol(programSymbol);
        programDao.insertOrReplace(program);

        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        TableTemplate tabletemplate = new TableTemplate();
        tabletemplate.setName(tableName);
        tabletemplate.setCreateDate(new Date());
        tabletemplate.setDescription("DESCRIPTION");
        tabletemplate.setIsArchived(false);
        tabletemplate.setProgram(program);
        tableTemplateDao.insertOrReplace(tabletemplate);

        ChildTableDao childTableDao = daoSession.getChildTableDao();
        childTable.setTeachingFillOutDate(new Date());
        childTable.setGeneralizationFillOutDate(new Date());
        childTable.setIsGeneralizationCollected(true);
        childTable.setIsGeneralizationFinished(false);
        childTable.setIsTeachingCollected(true);
        childTable.setIsTeachingFinished(false);
        childTable.setIsIOA(false);
        childTable.setIsPretest(false);
        childTable.setNote("NOTE");
        childTable.setTableTemplate(tabletemplate);
        childTable.setTermSolution(termSolution);
        childTableDao.insertOrReplace(childTable);

        setUpTableContent(tabletemplate, childTable);
    }

    private void setUpTableContent(TableTemplate tableTemplate, ChildTable childTable) {
        DaoSession daoSession = daoMaster.newSession();
        TableRowDao tableRowDao = daoSession.getTableRowDao();

        for (int i = 0; i < rowsNumber; i++) {
            TableRow tableRow = new TableRow();
            tableRow.setValue("Row " + i);
            tableRow.setInOrder(i);
            tableRow.setTableTemplate(tableTemplate);
            tableRowDao.insertOrReplace(tableRow);
        }

        List<TableRow> rows = tableRowDao.loadAll();
        TableFieldDao tableFieldDao = daoSession.getTableFieldDao();
        TableFieldFillingDao tableFieldFillingDao = daoSession.getTableFieldFillingDao();
        for (TableRow row : rows) {
            for (int i = 0; i < teachingNumber + generalizationNumber; i++) {
                String type = (i >= teachingNumber ? "G" : "U");

                TableField field = new TableField();
                field.setType(type);
                field.setInOrder(i);
                field.setTableRow(row);
                tableFieldDao.insertOrReplace(field);

                TableFieldFilling tableFieldFilling = new TableFieldFilling();
                tableFieldFilling.setContent("empty");
                tableFieldFilling.setChildTable(childTable);
                tableFieldFilling.setTableField(field);
                tableFieldFillingDao.insertOrReplace(tableFieldFilling);
            }

        }
    }

    public void testFinishGeneralization() throws InterruptedException {
        Activity mainActivity = this.getActivity();
        Button buttonEndGeneralization = (Button) mainActivity.findViewById(R.id.buttonEndGeneral);

        //finishing generalization with not all data filled
        TouchUtils.clickView(this, buttonEndGeneralization);
        Thread.sleep(1000);

        DaoSession daoSession = daoMaster.newSession();
        childTable = daoSession.getChildTableDao().load(childTable.getId());
        assertEquals("Generalization shouldn't be finished", childTable.getIsGeneralizationFinished(), false);

        //fill all data and finish generalization
        ListView tableListView = (ListView) mainActivity.findViewById(R.id.tableContent);
        TableElementAdapter adapter = ((TableElementAdapter) ((HeaderViewListAdapter) tableListView.getAdapter()).getWrappedAdapter());
        List<TableRow> rows = childTable.getTableTemplate().getTableRowList();
        for(int i = 0; i < rows.size(); i++){
            View elementView = adapter.getView(i, null, null);
            for(int j = 0; j < ((ViewGroup)elementView).getChildCount() - 1; j++){
                ImageButton button = (ImageButton) ((ViewGroup) elementView).getChildAt(j + 1); //at index 0 is TextView with row name
                button.performClick();
            }
        }

        TouchUtils.clickView(this, buttonEndGeneralization);
        Thread.sleep(1000);

        //testing dialog solution from: http://stackoverflow.com/a/22524041
        AlertDialog dialog = ((ProgramActivity) mainActivity).getLastAlertDialog();
        assertNotNull("Alert dialog should be shown", dialog);

        if (dialog.isShowing()) {
            try {
                performClick(dialog.getButton(DialogInterface.BUTTON_POSITIVE));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        daoSession = daoMaster.newSession();
        childTable = daoSession.getChildTableDao().load(childTable.getId());
        assertEquals("Generalization should be finished", childTable.getIsGeneralizationFinished(), true);
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
