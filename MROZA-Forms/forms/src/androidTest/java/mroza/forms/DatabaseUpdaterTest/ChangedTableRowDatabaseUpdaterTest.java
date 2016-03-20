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

public class ChangedTableRowDatabaseUpdaterTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private DatabaseUpdater databaseUpdater;
    private List<Child> children;
    private List<Program> programs;
    private DaoSession daoSession;
    private List<TableRow> tableRows;
    private List<TableRow> changedTableRows;

    public ChangedTableRowDatabaseUpdaterTest() {
        super(ChooseKidActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        Context targetContext = getInstrumentation().getTargetContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(targetContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        databaseUpdater = new DatabaseUpdater(targetContext);
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        this.children = TestUtils.setUpChildren(targetContext, 1, "CODE.");
        this.programs = TestUtils.setUpPrograms(targetContext, this.children, 4, "UNCHANGED.Name", "UNCHANGED.Symbol");
        this.tableRows = getAllTableRows(this.programs);
        this.changedTableRows = changeHalfOfTableTemplates(this.tableRows,(this.tableRows.size())/2,"CHANGED.Value");
    }

    private List<TableRow> changeHalfOfTableTemplates(List<TableRow> tableRows, int numberOfRowsToChange, String newValue) {

        List<TableRow> changedTableRows = new ArrayList<>();

        for(int tableRowId = 0; tableRowId < numberOfRowsToChange; tableRowId++)
        {
            TableRow tableRow = tableRows.get(tableRowId);
            tableRow.setValue(newValue);
            changedTableRows.add(tableRow);
        }

        return changedTableRows;

    }

    private List<TableRow> getAllTableRows(List<Program> programs) {
        List<TableRow> tableRowList = new ArrayList<>();
        for(Program program : programs)
            for(TableTemplate tableTemplate : program.getTableTemplateList())
                for(TableRow tableRow : tableTemplate.getTableRowList())
                    tableRowList.add(tableRow);
        return tableRowList;
    }

    public void testUpdateChangedTableRows() throws ParseException {

        databaseUpdater.updateTableRows(this.changedTableRows);
        TableRowDao tableRowDao = daoSession.getTableRowDao();
        List<TableRow> allTableRows = tableRowDao.loadAll();

        for(TableRow tableRowChanged : this.changedTableRows) {

            boolean found = false;
            for(TableRow tableRowAfterUpdate : allTableRows) {

                if(CompareUtils.areTwoTableRowTheSame(tableRowAfterUpdate, tableRowChanged))
                    found = true;

            }
            if(!found)
                fail("Changes did not updated in database");


        }

    }

}
