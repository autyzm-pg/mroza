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
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.Suppress;
import android.widget.ListView;
import database.*;
import mroza.forms.ChooseKidActivity;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.CompareUtils;
import mroza.forms.TestUtils.TestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChooseProgramActivitySearchTest extends ActivityUnitTestCase<ChooseProgramActivity> {

    private Context targetContext;

    public ChooseProgramActivitySearchTest() {
        super(ChooseProgramActivity.class);
    }

    Intent intent;
    Child child;
    TableTemplate tableTemplate;
    TermSolution termSolution;
    ChildTable expectedChildTable;
    ArrayList<Program> programs = new ArrayList<Program>();
    DaoSession daoSession;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        targetContext = getInstrumentation().getTargetContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(targetContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        setUpChild("AK123");
        setUpProgram(child, termSolution, new ChildTable(), "Teach letters", "A123", "C234", "Teach letter");
        setUpProgram(child, termSolution, expectedChildTable = new ChildTable(), "Teach toys", "B123", "D234", "Teach teddy bear");

        intent = new Intent(getInstrumentation().getTargetContext(), ChooseKidActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, "Teach toys");
        intent.putExtra("CHILD_NAME", child.getCode());
        intent.putExtra("CHILD_ID", child.getId());

        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
        setActivityContext(context);
    }

    private void setUpChild(String code) throws ParseException {
        child = new Child();
        child.setCode(code);
        child.setIsArchived(false);
        ChildDao childDao = daoSession.getChildDao();
        childDao.insertOrReplace(child);

        termSolution = new TermSolution();
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
    }

    private void setUpProgram(Child child, TermSolution termSolution, ChildTable childTable, String programName, String programSymbol, String tableSymbol, String tableName) {
        Program program = new Program();
        program.setChild(child);
        program.setCreateDate(new Date());
        program.setDescription("DESCRIPTION");
        program.setIsFinished(false);
        program.setName(programName);
        program.setSymbol(programSymbol);
        programs.add(program);

        ProgramDao programDao = daoSession.getProgramDao();
        programDao.insertOrReplace(program);

        tableTemplate = new TableTemplate();
        tableTemplate.setName(tableName);
        tableTemplate.setCreateDate(new Date());
        tableTemplate.setDescription("DESCRIPTION");
        tableTemplate.setIsArchived(false);
        tableTemplate.setProgram(program);

        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        tableTemplateDao.insertOrReplace(tableTemplate);


        childTable.setTeachingFillOutDate(new Date());
        childTable.setGeneralizationFillOutDate(new Date());
        childTable.setIsGeneralizationCollected(false);
        childTable.setIsGeneralizationFinished(false);
        childTable.setIsTeachingCollected(false);
        childTable.setIsTeachingFinished(false);
        childTable.setIsIOA(false);
        childTable.setIsPretest(false);
        childTable.setNote("NOTE");
        childTable.setTableTemplate(tableTemplate);
        childTable.setTermSolution(termSolution);

        ChildTableDao childTableDao = daoSession.getChildTableDao();
        childTableDao.insertOrReplace(childTable);
    }

    @Suppress
    public void testSearch() throws InterruptedException {
        startActivity(intent, null, null);
        getInstrumentation().callActivityOnResume(getActivity());
        final ListView listView = (ListView)getActivity().findViewById(R.id.childTable_list);
        ChildTableListViewAdapter adapter = (ChildTableListViewAdapter)listView.getAdapter();
        ChildTable childTable = adapter.getItem(0);
        boolean compareResult = CompareUtils.areTwoChildTablesTheSame(childTable,expectedChildTable);
        assertEquals("Should search fo correct program, given by query", true, compareResult);
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getInstrumentation().getTargetContext(), "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        ProgramDao programDao = daoSession.getProgramDao();
        for(Program program : programs)
            programDao.delete(program);

        ChildDao childDao = daoSession.getChildDao();
        childDao.delete(child);

        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();
        termSolutionDao.delete(termSolution);

        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        tableTemplateDao.delete(tableTemplate);

        ChildTableDao childTableDao = daoSession.getChildTableDao();
        childTableDao.delete(expectedChildTable);
    }


}
