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

public class ChangedTableFieldFillingDatabaseUpdaterTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private DatabaseUpdater databaseUpdater;
    private List<Child> children;
    private List<Program> programs;
    private DaoSession daoSession;
    private List<TableFieldFilling> tableFieldFillings;
    private List<TableFieldFilling> changedTableFieldFillings;

    public ChangedTableFieldFillingDatabaseUpdaterTest() {
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
        this.programs=TestUtils.setUpPrograms(targetContext, this.children, 2, "UNCHANGED.Name", "UNCHANGED.Symbol");
        this.tableFieldFillings = getAllTableFieldFillings(this.programs);
        this.changedTableFieldFillings = changeHalfOfTableFieldFillings(this.tableFieldFillings, (this.tableFieldFillings.size()) / 2);

    }

    private List<TableFieldFilling> changeHalfOfTableFieldFillings(List<TableFieldFilling> tableFieldFillings, int numberFieldsToChange) {
        List<TableFieldFilling> changedTableFields = new ArrayList<>();

        for(int tableFieldId = 0; tableFieldId < numberFieldsToChange; tableFieldId++)
        {
            TableFieldFilling tableField = tableFieldFillings.get(tableFieldId);
            if(tableField.getContent().equals("fail"))
                tableField.setContent("passed");
            else
                tableField.setContent("fail");
            changedTableFields.add(tableField);
        }

        return changedTableFields;
    }

    private List<TableFieldFilling> getAllTableFieldFillings(List<Program> programs) {
        List<TableFieldFilling> tableFieldFillingsList = new ArrayList<>();
        for(Program program : programs)
            for(TableTemplate tableTemplate : program.getTableTemplateList())
                for(TableRow tableRow : tableTemplate.getTableRowList())
                    for(TableField tableField : tableRow.getTableFieldList())
                        for(TableFieldFilling tableFieldFilling : tableField.getTableFieldFillingList())
                            tableFieldFillingsList.add(tableFieldFilling);
        return tableFieldFillingsList;
    }

    public void testUpdateChangedTableFillings() throws ParseException {

        databaseUpdater.updateTableFieldFillings(this.changedTableFieldFillings);
        TableFieldFillingDao tableFieldFillingsDao = daoSession.getTableFieldFillingDao();
        List<TableFieldFilling> allTableFieldFillings = tableFieldFillingsDao.loadAll();

        for(TableFieldFilling tableFieldFillingChanged : this.changedTableFieldFillings) {

            boolean found = false;
            for(TableFieldFilling tableFieldFillingAfterUpdate : allTableFieldFillings) {

                if(CompareUtils.areTwoTableFieldFillingsTheSame(tableFieldFillingAfterUpdate, tableFieldFillingChanged))
                    found = true;

            }
            if(!found)
                fail("Changes did not updated in database");


        }

    }
}
