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


public class ChangedProgramsDatabaseUpdaterTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private DatabaseUpdater databaseUpdater;
    private List<Child> children;
    private List<Program> preparedPrograms;
    private List<Program> changedPrograms;
    private DaoSession daoSession;

    public ChangedProgramsDatabaseUpdaterTest() {
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
        this.preparedPrograms = TestUtils.setUpPrograms(targetContext, this.children, 4, "UNCHANGED.Name", "UNCHANGED.Symbol");
        this.changedPrograms = changeHalfOfPrograms(2,"CHANGED.Name", "CHANGED.Symbol");
    }

    private List<Program> changeHalfOfPrograms(int numberOfPrograms, String changedName, String changedSymbol) {
        List<Program> changedPrograms = new ArrayList<>();

        for (int programId = 0; programId < numberOfPrograms; programId++) {
                Program program = this.preparedPrograms.get(programId);
                program.setSymbol(changedSymbol + programId);
                program.setName(changedName + programId);
                changedPrograms.add(program);
        }

        return changedPrograms;
    }


    public void testUpdateChangedProgram() throws ParseException {

        databaseUpdater.updateProgram(this.changedPrograms);
        ProgramDao programDao = daoSession.getProgramDao();
        List<Program> allPrograms = programDao.loadAll();

        for(Program program : this.changedPrograms) {

            boolean found = false;
            for(Program programAfterUpdate : allPrograms) {

               if(CompareUtils.areTwoProgramsTheSame(programAfterUpdate, program))
                   found = true;

            }
            if(!found)
                fail("Changes did not updated in database");


        }

    }

}