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


public class ChangedTableTemplatesDatabaseUpdaterTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private DatabaseUpdater databaseUpdater;
    private List<Child> children;
    private List<Program> programs;
    private DaoSession daoSession;
    private List<TableTemplate> tableTemplates;
    private List<TableTemplate> expectedChangedTableTemplates;
    private List<TableTemplate> allChangedTableTemplatesWithDescendants;

    public ChangedTableTemplatesDatabaseUpdaterTest() {
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
        this.tableTemplates = getAllTableTemplates(this.programs);
        this.expectedChangedTableTemplates = changeHalfOfTableTemplates(this.tableTemplates, 2, "CHANGED.Name");
        this.allChangedTableTemplatesWithDescendants = changeWithDescendantHalfOfTableTemplates(this.tableTemplates, 2, "CHANGED.Name");
    }

    private List<TableTemplate> changeWithDescendantHalfOfTableTemplates(List<TableTemplate> tableTamplates, int numberOfTableTemplatesChanged, String newName) {
        List<TableTemplate> changedTableTemplateList = new ArrayList<>();

        for(int tableTemplateId = 0; tableTemplateId < numberOfTableTemplatesChanged; tableTemplateId++)
        {
            TableTemplate tableTemplateToChange = tableTamplates.get(tableTemplateId);
            TableTemplate tableTemplate = new TableTemplate();
            tableTemplate.setId(tableTemplateToChange.getId());
            tableTemplate.setCreateDate(tableTemplateToChange.getCreateDate());
            tableTemplate.setDescription(tableTemplateToChange.getDescription());
            tableTemplate.setName(newName);
            changedTableTemplateList.add(tableTemplate);

        }

        return changedTableTemplateList;
    }

    private List<TableTemplate> changeHalfOfTableTemplates(List<TableTemplate> tableTamplates, int numberOfTableTemplatesChanged, String newName) {
        List<TableTemplate> changedTableTemplateList = new ArrayList<>();

        for(int tableTemplateId = 0; tableTemplateId < numberOfTableTemplatesChanged; tableTemplateId++)
        {
            TableTemplate tableTemplate = tableTamplates.get(tableTemplateId);
            tableTemplate.setName(newName);
            changedTableTemplateList.add(tableTemplate);

        }

        return changedTableTemplateList;
    }

    private List<TableTemplate> getAllTableTemplates(List<Program> programs) {

        List<TableTemplate> tableTemplateList = new ArrayList<>();
        for(Program program : programs)
        {
            for(TableTemplate tableTemplate : program.getTableTemplateList())
                tableTemplateList.add(tableTemplate);
        }
        return tableTemplateList;
    }

    public void testUpdateChangedTableTemplate() throws ParseException {

        databaseUpdater.updateTableTemplates(this.expectedChangedTableTemplates);
        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        List<TableTemplate> allTableTemplates = tableTemplateDao.loadAll();

        for(TableTemplate tableTemplateChanged : this.expectedChangedTableTemplates) {

            boolean found = false;
            for(TableTemplate tableTemplateAfterUpdate : allTableTemplates) {

                if(CompareUtils.areTwoTableTemplatesTheSame(tableTemplateAfterUpdate, tableTemplateChanged))
                    found = true;

            }
            if(!found)
                fail("Changes did not updated in database");


        }

    }

    public void testUpdateChangedTableTemplateWithoutDescendants() throws ParseException {

        databaseUpdater.updateTableTemplates(this.allChangedTableTemplatesWithDescendants);
        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        List<TableTemplate> allTableTemplates = tableTemplateDao.loadAll();

        for(TableTemplate tableTemplateChanged : this.expectedChangedTableTemplates) {

            boolean found = false;
            for(TableTemplate tableTemplateAfterUpdate : allTableTemplates) {

                if(CompareUtils.areTwoTableTemplatesTheSame(tableTemplateAfterUpdate, tableTemplateChanged))
                    found = true;

            }
            if(!found)
                fail("Changes did not updated in database");


        }

    }



}
