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
import database.*;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChooseProgramActivityTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {

    private Child childMain;
    private DaoSession daoSession;
    private Button previousButton;
    private Button futureButton;
    private TermSolution termSolutionHistory;
    private TermSolution termSolutionFuture;
    private TermSolution termSolutionMoreHistory;
    private TermSolution termSolutionMoreFuture;
    private Context targetContext;

    public ChooseProgramActivityTest() {
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

        termSolutionHistory = new TermSolution();
        termSolutionHistory.setChild(child);
        int dayStartHistory = c.get(Calendar.DAY_OF_MONTH) - 10;
        int dayEndHistory = c.get(Calendar.DAY_OF_MONTH) - 8;
        Date dateStartHistory = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + dayStartHistory);
        Date dateEndHistory = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + dayEndHistory);
        termSolutionHistory.setStartDate(dateStartHistory);
        termSolutionHistory.setEndDate(dateEndHistory);
        termSolutionDao.insertOrReplace(termSolutionHistory);

        termSolutionMoreHistory = new TermSolution();
        termSolutionMoreHistory.setChild(child);
        int dayStartMoreHistory = c.get(Calendar.DAY_OF_MONTH) - 14;
        int dayEndMoreHistory = c.get(Calendar.DAY_OF_MONTH) - 11;
        Date dateStartMoreHistory = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + dayStartMoreHistory);
        Date dateEndMoreHistory = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + dayEndMoreHistory);
        termSolutionMoreHistory.setStartDate(dateStartMoreHistory);
        termSolutionMoreHistory.setEndDate(dateEndMoreHistory);
        termSolutionDao.insertOrReplace(termSolutionMoreHistory);

        termSolutionFuture = new TermSolution();
        termSolutionFuture.setChild(child);
        int dayStartFuture = c.get(Calendar.DAY_OF_MONTH) + 10;
        int dayEndFuture = c.get(Calendar.DAY_OF_MONTH) + 12;
        Date dateStartFuture = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + dayStartFuture);
        Date dateEndFuture = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + dayEndFuture);
        termSolutionFuture.setStartDate(dateStartFuture);
        termSolutionFuture.setEndDate(dateEndFuture);
        termSolutionDao.insertOrReplace(termSolutionFuture);

        termSolutionMoreFuture = new TermSolution();
        termSolutionMoreFuture.setChild(child);
        int dayStartMoreFuture = c.get(Calendar.DAY_OF_MONTH) + 14;
        int dayEndMoreFuture = c.get(Calendar.DAY_OF_MONTH) + 17;
        Date dateStartMoreFuture = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + dayStartMoreFuture);
        Date dateEndMoreFuture = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + dayEndMoreFuture);
        termSolutionMoreFuture.setStartDate(dateStartMoreFuture);
        termSolutionMoreFuture.setEndDate(dateEndMoreFuture);
        termSolutionDao.insertOrReplace(termSolutionMoreFuture);


        setUpProgram(child, termSolution, "Teach letters", "A123", "C234", "Teach letters");
        setUpProgram(child, termSolution, "Teach toys", "B123", "D234", "Teach teddy bear");
        setUpProgram(child, termSolutionHistory, "Teach to sing", "E123", "F234", "Teach song");
        setUpProgram(child, termSolutionHistory, "Teach to play", "U123", "K234", "Teach song");
        setUpProgram(child, termSolutionFuture, "Teach to count", "J123", "J234", "Teach number");
        setUpProgram(child, termSolutionFuture, "Teach to add", "P123", "P234", "Teach to add");
        setUpProgram(child, termSolutionMoreHistory, "Teach to clean", "Z123", "Z234", "Teach to clean");
        setUpProgram(child, termSolutionMoreHistory, "Teach to run", "X123", "X234", "Teach to run");
        setUpProgram(child, termSolutionMoreFuture, "Teach to paint", "M123", "M234", "Teach to paint");
        setUpProgram(child, termSolutionMoreFuture, "Teach to cut", "N123", "N234", "Teach to cut");
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

    private List<Long> getChildTablesIds(List<ChildTable> childTables) {

        List<Long> ids = new ArrayList<Long>();
        for(ChildTable childTable: childTables){
            ids.add(childTable.getId());
        }
        return ids;
    }

    private List<ChildTable> getActualProgramsForChild(Child child) throws ParseException {

        Date dateNow = getActualDate();

        List<ChildTable> childTables = new ArrayList<ChildTable>();
        for(Program program : childMain.getProgramList() )
        {
            for(TableTemplate table : program.getTableTemplateList())
            {
                for(ChildTable childTable : table.getChildTableList())
                {
                    TermSolution termSolution  = childTable.getTermSolution();
                    if(termSolution.getChild().getId().equals(childMain.getId()))
                    {
                        if((termSolution.getEndDate().compareTo(dateNow) >= 0)  && (termSolution.getStartDate().compareTo(dateNow) <= 0) )
                        {
                            childTables.add(childTable);
                        }
                    }
                }
            }
        }

        return childTables;
    }

    private List<ChildTable> getHistoricalChildTablesForChild() throws ParseException {

        List<ChildTable> childTables = new ArrayList<ChildTable>();

        // Take list with all historical childTableList from last period
        for(Program program : childMain.getProgramList() )
        {
            for(TableTemplate table : program.getTableTemplateList())
            {
                for(ChildTable childTable : table.getChildTableList())
                {
                    TermSolution termSolution  = childTable.getTermSolution();
                    if(termSolution.getChild().getId().equals(childMain.getId()))
                    {
                        if(termSolution.getEndDate().compareTo(termSolutionHistory.getEndDate()) == 0 )
                        {
                            childTables.add(childTable);
                        }
                    }
                }
            }
        }

        return childTables;
    }

    private List<ChildTable> getFutureChildTablesForChild() {

        List<ChildTable> childTables = new ArrayList<ChildTable>();

        // Take list with all historical childTableList from last period
        for(Program program : childMain.getProgramList() )
        {
            for(TableTemplate table : program.getTableTemplateList())
            {
                for(ChildTable childTable : table.getChildTableList())
                {
                    TermSolution termSolution  = childTable.getTermSolution();
                    if(termSolution.getChild().getId().equals(childMain.getId()))
                    {
                        if(termSolution.getStartDate().compareTo(termSolutionFuture.getStartDate()) == 0 )
                        {
                            childTables.add(childTable);
                        }
                    }
                }
            }
        }

        return childTables;
    }

    private Date getActualDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date dateNow = null;
        try {
            dateNow = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateNow;
    }

    public void testProgramListForActualTimeShouldContainOnlyChildTablesForKid() throws ParseException {
        Activity mainActivity = this.getActivity();
        ListView listActivities = (ListView) mainActivity.findViewById(R.id.childTable_list);
        ChildTableListViewAdapter adapter = (ChildTableListViewAdapter) listActivities.getAdapter();

        List<Long> allChildTablesIds = getChildTablesIds(getActualProgramsForChild(childMain));
        int count = adapter.getCount();
        for (int next = 0; next < count; next++)
        {
            ChildTable childTablesInList = adapter.getItem(next);
            if (!allChildTablesIds.contains(childTablesInList.getId())) {
                fail("Actual childTable list should contain only actual childTableList for one child");
            }
        }


        List<Long> programsListIds = getChildTablesIds(adapter.childTableList);
        for (Long programId : allChildTablesIds)
        {
            if (!programsListIds.contains(programId)) {
                fail("Actual program list should contain all actual childTableList for one child");
            }
        }

    }

    public void testProgramListForHistoricalTimeShouldContainOnlyChildTablesForKid() throws ParseException, InterruptedException {
        Activity mainActivity = this.getActivity();
        TouchUtils.clickView(this, previousButton);
        Thread.sleep(5000);
        ListView listActivities = (ListView) mainActivity.findViewById(R.id.childTable_list);
        ChildTableListViewAdapter adapter = (ChildTableListViewAdapter) listActivities.getAdapter();

        List<Long> allChildTablesIds = getChildTablesIds(getHistoricalChildTablesForChild());
        int count = adapter.getCount();
        for (int next = 0; next < count; next++)
        {
            ChildTable childTableList = adapter.getItem(next);
            if (!allChildTablesIds.contains(childTableList.getId())) {
                fail("Historical childTable list should contain only previous period childTableList for one child");
            }
        }


        List<Long> childTablesIds = getChildTablesIds(adapter.childTableList);
        for (Long childTableId : allChildTablesIds)
        {
            if (!childTablesIds.contains(childTableId)) {
                fail("Historical childTable list should contain all previous period childTableList for one child");
            }
        }

    }

    public void testProgramListForFutureTimeShouldContainOnlyChildTablesForKid() throws ParseException, InterruptedException {
        Activity mainActivity = this.getActivity();
        TouchUtils.clickView(this, futureButton);
        Thread.sleep(5000);
        ListView listActivities = (ListView) mainActivity.findViewById(R.id.childTable_list);
        ChildTableListViewAdapter adapter = (ChildTableListViewAdapter) listActivities.getAdapter();

        List<Long> allChildTableIds = getChildTablesIds(getFutureChildTablesForChild());
        int count = adapter.getCount();
        for (int next = 0; next < count; next++)
        {
            ChildTable childTableInList = adapter.getItem(next);
            if (!allChildTableIds.contains(childTableInList.getId())) {
                fail("Future childTable list should contain only future period childTableList for one child");
            }
        }


        List<Long> childTablesIds = getChildTablesIds(adapter.childTableList);
        for (Long childTableId : allChildTableIds)
        {
            if (!childTablesIds.contains(childTableId)) {
                fail("Future childTable list should contain all future period childTableList for one child");
            }
        }

    }


}
