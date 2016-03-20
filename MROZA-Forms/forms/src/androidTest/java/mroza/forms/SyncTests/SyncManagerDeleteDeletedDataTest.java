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
import syncmodels.ReceiveSyncModel;
import syncmodels.SyncManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SyncManagerDeleteDeletedDataTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private SyncManager syncManager;
    private List<Child> childs;
    private List<Program> programs;
    private List<ChildTable> childTables;
    private List<ChildTable> deletedChildTables;
    private ReceiveSyncModel receiveSyncModel;
    private Context targetContext;

    public SyncManagerDeleteDeletedDataTest() {
        super(ChooseKidActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        syncManager = new SyncManager(targetContext);
        Calendar c = Calendar.getInstance();
        int dayNumber = c.get(Calendar.DAY_OF_MONTH) - 2;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayNumber);
        syncManager.setSyncDate(date);

        TestUtils.cleanUpDatabase(targetContext);
        this.childs = TestUtils.setUpChildren(targetContext, 1, "CODE.");
        this.programs = TestUtils.setUpPrograms(targetContext, this.childs, 2, "UNCHANGED.Name", "UNCHANGED.Symbol");
        this.childTables = getAllChildTable(this.programs);
        this.deletedChildTables = getChildTablesToDelete(this.childTables);
        this.receiveSyncModel = setUpReceiveSyncModel(this.deletedChildTables);
    }

    private List<ChildTable> getChildTablesToDelete(List<ChildTable> childTables) {
        List<ChildTable> childTablesToDelete = new ArrayList<>();
        for(ChildTable childTable : childTables)
        {
            ChildTable childTableToDelete = new ChildTable();
            childTableToDelete.setId(childTable.getId());
            childTablesToDelete.add(childTableToDelete);
        }
        return childTablesToDelete;
    }

    private ReceiveSyncModel setUpReceiveSyncModel(List<ChildTable> childTablesToDelete) {
        ReceiveSyncModel syncModel = new ReceiveSyncModel();
        syncModel.setDeletedChildTableList(childTablesToDelete);
        return syncModel;
    }

    private List<ChildTable> getAllChildTable(List<Program> programs) {
        List<ChildTable> childTableList = new ArrayList<>();
        for (Program program : programs)
            for (TableTemplate tableTemplate : program.getTableTemplateList())
                for (ChildTable childTable : tableTemplate.getChildTableList())
                    childTableList.add(childTable);
        return childTableList;
    }


    public void testDeleteChildTablesGetInReceiverSyncModel() {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this.targetContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        syncManager.updateDatabaseAfterSync(this.receiveSyncModel);
        ChildTableDao childTableDao = daoSession.getChildTableDao();
        List<ChildTable> childTablesFromDb = childTableDao.loadAll();

        for (ChildTable deletedChildTable : this.deletedChildTables){
            Boolean found = false;
            for(ChildTable childTable : childTablesFromDb)
                if(childTable.getId() == deletedChildTable.getId())
                    found = true;
            if (found) {
                fail("ChildTable isn't deleted but it should.");
            }
        }
    }
}