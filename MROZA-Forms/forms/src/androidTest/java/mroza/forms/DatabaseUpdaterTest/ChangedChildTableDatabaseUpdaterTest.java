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

package mroza.forms.DatabaseUpdaterTest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import database.*;
import mroza.forms.ChooseKidActivity;
import mroza.forms.TestUtils.CompareUtils;
import mroza.forms.TestUtils.TestUtils;
import syncmodels.DatabaseUpdater;
import syncmodels.SyncManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ChangedChildTableDatabaseUpdaterTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private DatabaseUpdater databaseUpdater;
    private List<Child> children;
    private List<Program> programs;
    private DaoSession daoSession;
    private List<ChildTable> childTables;
    private List<ChildTable> changedChildTables;

    public ChangedChildTableDatabaseUpdaterTest() {
        super(ChooseKidActivity.class);
    }

    protected void setUp() throws Exception{
        super.setUp();

        Context targetContext=getInstrumentation().getTargetContext();
        DaoMaster.DevOpenHelper helper=new DaoMaster.DevOpenHelper(targetContext,"mroza-db",null);
        SQLiteDatabase db=helper.getWritableDatabase();
        DaoMaster daoMaster=new DaoMaster(db);
        daoSession=daoMaster.newSession();
        databaseUpdater =new DatabaseUpdater(targetContext);
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        this.children =TestUtils.setUpChildren(targetContext, 1, "CODE.");
        this.programs=TestUtils.setUpPrograms(targetContext, this.children, 4, "UNCHANGED.Name", "UNCHANGED.Symbol");
        this.childTables = getAllChildTables(this.programs);
        this.changedChildTables = changeHalfOfChildTables(this.childTables, (this.childTables.size()) / 2, "CHANGED");

    }

    private List<ChildTable> changeHalfOfChildTables(List<ChildTable> childTables, int numberChildTablesToChange, String newNote) {
        List<ChildTable> changedChildTables = new ArrayList<>();

        for(int childTableId = 0; childTableId < numberChildTablesToChange; childTableId++)
        {
            ChildTable childTable = childTables.get(childTableId);
            childTable.setNote(newNote);
            changedChildTables.add(childTable);
        }

        return changedChildTables;
    }

    private List<ChildTable> getAllChildTables(List<Program> programs) {
        List<ChildTable> childTableList = new ArrayList<>();
        for(Program program : programs)
            for(TableTemplate tableTemplate : program.getTableTemplateList())
                for(ChildTable childTable : tableTemplate.getChildTableList())
                    childTableList.add(childTable);
        return childTableList;
    }

    public void testUpdateChangesChildTables() throws ParseException {

        databaseUpdater.updateChildTables(this.changedChildTables);
        ChildTableDao childTableDao = daoSession.getChildTableDao();
        List<ChildTable> allChildTables = childTableDao.loadAll();

        for(ChildTable changedChildTable : this.changedChildTables) {

            boolean found = false;
            for(ChildTable childTableAfterUpdate : allChildTables) {

                if(CompareUtils.areTwoChildTablesTheSame(childTableAfterUpdate, changedChildTable))
                    found = true;

            }
            if(!found)
                fail("Changes did not updated in database");


        }

    }
}
