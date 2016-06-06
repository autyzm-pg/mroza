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

import adapters.ChildTableListViewAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
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

public class ChooseProgramActivityWhenNotExistTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {

    private Child childMain;
    private DaoSession daoSession;
    private Button previousButton;
    private Button futureButton;
    private Context targetContext;

    public ChooseProgramActivityWhenNotExistTest() {
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
        setUpChild("AK123", childMain = new Child());
        setUpChild("ON987", new Child());

        Intent childNameActivityParameter = new Intent();
        childNameActivityParameter.putExtra("CHILD_NAME", "AK123");
        childNameActivityParameter.putExtra("CHILD_ID", (long) 1);
        setActivityIntent(childNameActivityParameter);
        setActivityInitialTouchMode(true);
        previousButton = (Button)getActivity().findViewById(R.id.buttonChangePeriodHistorical);
        futureButton = (Button)getActivity().findViewById(R.id.buttonChangePeriodFuture);
    }

    private void setUpChild(String code, Child child) throws ParseException {
        child.setCode(code);
        child.setIsArchived(false);
        ChildDao childDao = daoSession.getChildDao();
        childDao.insertOrReplace(child);

        TermSolution termSolution = new TermSolution();
        Calendar c = Calendar.getInstance();
        termSolution.setChild(child);
        int dayStart = c.get(Calendar.DAY_OF_MONTH)-5;
        int dayEnd = c.get(Calendar.DAY_OF_MONTH)+5;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date dateStart = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + dayStart);
        Date dateEnd = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayEnd);
        termSolution.setStartDate(dateStart);
        termSolution.setEndDate(dateEnd);
        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();
        termSolutionDao.insertOrReplace(termSolution);

        setUpProgram(child, termSolution, "Teach letters", "A123", "C234", "Teach letters");
        setUpProgram(child, termSolution, "Teach toys", "B123", "D234", "Teach toys");
    }

    private void setUpProgram(Child child, TermSolution termSolution, String programName, String programSymbol, String tableSymbol, String tableName) {

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

        ChildTable childTable = new ChildTable();
        childTable.setTeachingFillOutDate(new Date());
        childTable.setGeneralizationFillOutDate(new Date());
        childTable.setIsGeneralizationCollected(false);
        childTable.setIsGeneralizationFinished(false);
        childTable.setIsTeachingCollected(false);
        childTable.setIsTeachingFinished(false);
        childTable.setIsIOA(false);
        childTable.setIsPretest(false);
        childTable.setNote("NOTE");
        childTable.setTableTemplate(tabletemplate);
        childTable.setTermSolution(termSolution);

        ChildTableDao childTableDao = daoSession.getChildTableDao();
        childTableDao.insertOrReplace(childTable);
    }


    public void testProgramListForHistoricalTimeShouldNotContainAnyProgramsForKid() throws ParseException, InterruptedException {
        Activity mainActivity = this.getActivity();
        TouchUtils.clickView(this, previousButton);
        Thread.sleep(5000);
        ListView listActivities = (ListView) mainActivity.findViewById(R.id.childTable_list);
        ChildTableListViewAdapter adapter = (ChildTableListViewAdapter) listActivities.getAdapter();

        int count = adapter.getCount();

        assertEquals("Historical childTableList should be empty", 0, count);
        final TextView periodDateStart = (TextView) mainActivity.findViewById(R.id.termSolutionStartDate);
        assertEquals("Text message should talk about empty program", mainActivity.getString(R.string.empty_term_solution), periodDateStart.getText());
      }

    public void testProgramListForFutureTimeShouldNotContainAnyProgramsForKid() throws ParseException, InterruptedException {
        Activity mainActivity = this.getActivity();
        TouchUtils.clickView(this, futureButton);
        Thread.sleep(5000);
        ListView listActivities = (ListView) mainActivity.findViewById(R.id.childTable_list);
        ChildTableListViewAdapter adapter = (ChildTableListViewAdapter) listActivities.getAdapter();

        int count = adapter.getCount();
        assertEquals("Actual childTableList should be empty", 0, count);
        final TextView periodDateStart = (TextView) mainActivity.findViewById(R.id.termSolutionStartDate);
        assertEquals("Text message should talk about empty program",mainActivity.getString(R.string.empty_term_solution), periodDateStart.getText());

    }


}

