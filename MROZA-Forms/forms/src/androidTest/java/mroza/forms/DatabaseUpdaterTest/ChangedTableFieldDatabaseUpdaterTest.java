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

public class ChangedTableFieldDatabaseUpdaterTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private DatabaseUpdater databaseUpdater;
    private List<Child> children;
    private List<Program> programs;
    private DaoSession daoSession;
    private List<TableField> tableFields;
    private List<TableField> changedTableFields;

    public ChangedTableFieldDatabaseUpdaterTest() {
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
        this.tableFields = getAllTableFields(this.programs);
        this.changedTableFields = changeHalfOfTableFields(this.tableFields, (this.tableFields.size()) / 2);

    }

    private List<TableField> changeHalfOfTableFields(List<TableField> tableFields, int numberFieldsToChange) {
        List<TableField> changedTableFields = new ArrayList<>();

        for(int tableFieldId = 0; tableFieldId < numberFieldsToChange; tableFieldId++)
        {
            TableField tableField = tableFields.get(tableFieldId);
            if(tableField.getType().equals("U"))
                tableField.setType("G");
            else
                tableField.setType("U");
            changedTableFields.add(tableField);
        }

        return changedTableFields;
    }

    private List<TableField> getAllTableFields(List<Program> programs) {
        List<TableField> tableFieldList = new ArrayList<>();
        for(Program program : programs)
            for(TableTemplate tableTemplate : program.getTableTemplateList())
                for(TableRow tableRow : tableTemplate.getTableRowList())
                    for(TableField tableField : tableRow.getTableFieldList())
                        tableFieldList.add(tableField);
        return tableFieldList;
    }

    public void testUpdateChangedTableFields() throws ParseException {

        databaseUpdater.updateTableFields(this.changedTableFields);
        TableFieldDao tableFieldDao = daoSession.getTableFieldDao();
        List<TableField> allTableFields = tableFieldDao.loadAll();

        for(TableField tableFieldChanged : this.changedTableFields) {

            boolean found = false;
            for(TableField tableFieldAfterUpdate : allTableFields) {

                if(CompareUtils.areTwoTableFieldsTheSame(tableFieldAfterUpdate, tableFieldChanged))
                    found = true;

            }
            if(!found)
                fail("Changes did not updated in database");


        }

    }
}
