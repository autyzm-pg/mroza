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
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import database.*;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChooseProgramActivityPretestTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {

    private DaoSession daoSession;
    private TermSolution termSolutionFuture;
    private TermSolution termSolution;
    final int rowsNumber = 3;
    final int teachingNumber = 2;
    final int generalizationNumber = 1;
    private Context targetContext;

    public ChooseProgramActivityPretestTest() {
        super(ChooseProgramActivity.class);
    }

    protected void setUp() throws Exception {
        targetContext = getInstrumentation().getTargetContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(targetContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        setUpChild("AK123", new Child());

        Intent childNameActivityParameter = new Intent();
        childNameActivityParameter.putExtra("CHILD_NAME", "AK123");
        childNameActivityParameter.putExtra("CHILD_ID", (long) 1);
        setActivityIntent(childNameActivityParameter);
        setActivityInitialTouchMode(true);

    }

    private void setUpChild(String code, Child child) throws ParseException {
        child.setCode(code);
        child.setIsArchived(false);
        ChildDao childDao = daoSession.getChildDao();
        childDao.insertOrReplace(child);

        termSolution = new TermSolution();
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

        termSolutionFuture = new TermSolution();
        termSolutionFuture.setChild(child);
        int dayFutureStart = c.get(Calendar.DAY_OF_MONTH) + 6;
        int dayFutureEnd = c.get(Calendar.DAY_OF_MONTH) + 10;
        Date dateFutureStart = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayFutureStart);
        Date dateFutureEnd = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayFutureEnd);
        termSolutionFuture.setStartDate(dateFutureStart);
        termSolutionFuture.setEndDate(dateFutureEnd);
        termSolutionDao.insertOrReplace(termSolutionFuture);

        setUpProgram(child, "PROGRAM_1", "A123", "C234", "TABLE_1", true);
        setUpProgram(child,  "PROGRAM_2", "B123", "D234", "TABLE_2", false);

    }

    private void setUpProgram(Child child, String programName, String programSymbol, String tableSymbol, String tableName, Boolean isPretest) {

        Program program = new Program();
        program.setChild(child);
        program.setCreateDate(new Date());
        program.setDescription("DESCRIPTION");
        program.setIsFinished(false);
        program.setName(programName);
        program.setSymbol(programSymbol);

        ProgramDao programDao = daoSession.getProgramDao();
        programDao.insertOrReplace(program);

        TableTemplate tabletemplate = new TableTemplate();
        tabletemplate.setName(tableName);
        tabletemplate.setCreateDate(new Date());
        tabletemplate.setDescription("DESCRIPTION");
        tabletemplate.setIsArchived(false);
        tabletemplate.setProgram(program);

        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        tableTemplateDao.insertOrReplace(tabletemplate);

        setUpTableContent(tabletemplate);
        setUpChildTable(termSolution, isPretest, tabletemplate);
        setUpChildTable(termSolutionFuture, false, tabletemplate);

    }

    private void setUpChildTable(TermSolution termSolution, Boolean isPretest, TableTemplate tabletemplate) {
        ChildTable childTable = new ChildTable();

        childTable.setTeachingFillOutDate(new Date());
        childTable.setGeneralizationFillOutDate(new Date());
        childTable.setIsGeneralizationCollected(true);
        childTable.setIsGeneralizationFinished(false);
        childTable.setIsTeachingCollected(true);
        childTable.setIsTeachingFinished(false);
        childTable.setIsIOA(false);
        childTable.setIsPretest(isPretest);
        childTable.setNote("NOTE");
        childTable.setTableTemplate(tabletemplate);
        childTable.setTermSolution(termSolution);

        ChildTableDao childTableDao = daoSession.getChildTableDao();
        childTableDao.insertOrReplace(childTable);

    }

    private void setUpTableContent(TableTemplate tableTemplate) {

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
        for (TableRow row : rows) {
            for (int i = 0; i < teachingNumber + generalizationNumber; i++) {
                String type = (i >= teachingNumber ? "G" : "U");

                TableField field = new TableField();
                field.setType(type);
                field.setInOrder(i);
                field.setTableRow(row);
                tableFieldDao.insertOrReplace(field);
            }

        }
    }

    public void testProgramsWithPretestShouldHaveLabelPretestOnList() throws ParseException, InterruptedException {
        Activity mainActivity = this.getActivity();
        ListView listActivities = (ListView) mainActivity.findViewById(R.id.childTable_list);


        for(int i = 0; i < listActivities.getChildCount(); i++){

            View listElement = listActivities.getChildAt(i);
            TextView symbolView = (TextView)listElement.findViewById(R.id.textViewSymbol);
            if( symbolView.getText().toString().equals("A123") ) {

                TextView programTextView = (TextView)listElement.findViewById(R.id.textViewName);
                assertEquals("Program with pretest should contains PRETEST in its label",programTextView.getText().toString().contains("PRETEST"), true);
            }

        }


    }

}
