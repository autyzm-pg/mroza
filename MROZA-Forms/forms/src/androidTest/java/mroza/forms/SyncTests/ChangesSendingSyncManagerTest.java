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

package mroza.forms.SyncTests;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import database.*;
import mroza.forms.ChooseKidActivity;
import mroza.forms.TestUtils.TestUtils;
import syncmodels.SyncManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChangesSendingSyncManagerTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private SyncManager syncManager;
    private List<ChildTable> unchangedChildTables;
    private List<ChildTable> changesChildTables;
    private List<Child> childs;
    private DaoSession daoSession;
    private Context targetContext;

    public ChangesSendingSyncManagerTest() {
        super(ChooseKidActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        Calendar c = Calendar.getInstance();
        int lastSyncDay = c.get(Calendar.DAY_OF_MONTH) - 1;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date lastSyncDate = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + lastSyncDay);

        targetContext = getInstrumentation().getTargetContext();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(targetContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        TestUtils.cleanUpDatabase(targetContext);
        syncManager = new SyncManager(targetContext);
        syncManager.setSyncDate(lastSyncDate);
        this.childs = setUpChilds();
        setUpTermSolutions(this.childs);

        int dayBeforeLastSyncDay = c.get(Calendar.DAY_OF_MONTH) - 2;
        Date dayBeforeLastSyncDate = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayBeforeLastSyncDay);

        this.unchangedChildTables = setUpUnchangedTables(dayBeforeLastSyncDate);
        this.changesChildTables = setUpChangedTables(new Date());
    }

    private List<Child> setUpChilds() {

        List<Child> childList = new ArrayList<>();
        ChildDao childDao = daoSession.getChildDao();
        int numberOfElements = 2;
        for(int childId = 0; childId< numberOfElements; childId++)
       {
           Child child = new Child();
           child.setCode("ABC" + childId);
           child.setIsArchived(false);
           childDao.insertOrReplace(child);
           childList.add(child);

       }
        return childList;
    }

    private List<ChildTable> setUpChangedTables(Date date) {
        List<ChildTable> changedTables = new ArrayList<>();
        for(Child child : this.childs)
        {
            changedTables.add(setUpChildTable(child, child.getTermSolutionList().get(0), date, "CHANGED"));
        }
        return changedTables;
    }

    private List<ChildTable> setUpUnchangedTables(Date dayBeforeLastSyncDate) {

        List<ChildTable> unchangedTables = new ArrayList<>();
        for(Child child : this.childs)
        {
            unchangedTables.add(setUpChildTable(child, child.getTermSolutionList().get(0), dayBeforeLastSyncDate, "UNCHANGED"));
        }
        return unchangedTables;
    }

    private void setUpTermSolutions(List<Child> childs) throws ParseException {

        for(Child child : childs) {

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
        }
    }


    private ChildTable setUpChildTable(Child child, TermSolution termSolution, Date changedDate, String isChanged) {

        Program program = new Program();
        program.setChild(child);
        program.setCreateDate(new Date());
        program.setDescription("Opis długi");
        program.setIsFinished(false);
        program.setName("Program - " + child.getId() + " -" + isChanged);
        program.setSymbol("P." + child.getId());

        ProgramDao programDao = daoSession.getProgramDao();
        programDao.insertOrReplace(program);

        TableTemplate tabletemplate = new TableTemplate();
        tabletemplate.setName("T." + child.getId()+ " -" + isChanged);
        tabletemplate.setCreateDate(new Date());
        tabletemplate.setDescription("Krótki opis");
        tabletemplate.setIsArchived(false);
        tabletemplate.setProgram(program);

        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        tableTemplateDao.insertOrReplace(tabletemplate);

        ChildTable childTable = new ChildTable();
        childTable.setTeachingFillOutDate(changedDate);
        childTable.setGeneralizationFillOutDate(changedDate);
        childTable.setLastEditDate(changedDate);
        childTable.setIsGeneralizationCollected(true);
        childTable.setIsGeneralizationFinished(false);
        childTable.setIsTeachingCollected(true);
        childTable.setIsTeachingFinished(false);
        childTable.setIsIOA(false);
        childTable.setIsPretest(false);
        childTable.setNote("Jest ok");
        childTable.setTableTemplate(tabletemplate);
        childTable.setTermSolution(termSolution);

        ChildTableDao childTableDao = daoSession.getChildTableDao();
        childTableDao.insertOrReplace(childTable);

        return childTable;
    }

    public void testGetAllChangedChildTables() throws ParseException {

        List<ChildTable> pendingChanges =  syncManager.getPendingChanges();

        for(ChildTable pendingChangesChildTable : pendingChanges)
        {
             if(!pendingChangesChildTable.getTableTemplate().getName().contains("-CHANGED"))
             {
                 fail("Found pending should be on prepared changed child tables");
             }

        }

        assertEquals("Prepared changes should be the same size as found pending changes", pendingChanges.size(), this.changesChildTables.size());

    }
}
