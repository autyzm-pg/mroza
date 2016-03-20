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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ChangedTermSolutionDatabaseUpdaterTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private DatabaseUpdater databaseUpdater;
    private List<Child> children;
    private DaoSession daoSession;
    private List<TermSolution> termSolutions;
    private List<TermSolution> changedTermSolutions;

    public ChangedTermSolutionDatabaseUpdaterTest() {
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
        this.children =TestUtils.setUpChildren(targetContext, 4, "CODE.");
        this.termSolutions = setUpTermSolutions(targetContext,this.children);
        this.changedTermSolutions = changeHalfOfTermSolutions(this.termSolutions, (this.termSolutions.size()) / 2);

    }

    private List<TermSolution> setUpTermSolutions(Context targetContext, List<Child> childs) throws ParseException {
        List<TermSolution> termSolutions = new ArrayList<>();
        for(Child child : childs){

            termSolutions.add(TestUtils.setUpTermSolution(targetContext,child, -4,6));
        }
        return termSolutions;
    }

    private List<TermSolution> changeHalfOfTermSolutions(List<TermSolution> termSolutions, int numberTermSolutionsToChange) throws ParseException {
        List<TermSolution> termSolutionList = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int dayStart = c.get(Calendar.DAY_OF_MONTH) - 7;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date newDate = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayStart);

        for(int termSolutionId = 0; termSolutionId < numberTermSolutionsToChange; termSolutionId++)
        {
            TermSolution termSolution = termSolutions.get(termSolutionId);
            termSolution.setStartDate(newDate);
            termSolutionList.add(termSolution);
        }

        return termSolutionList;
    }

    public void testUpdateChangesTermSolutions() throws ParseException {

        databaseUpdater.updateTermSolutions(this.changedTermSolutions);
        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();
        List<TermSolution> allTermSolutions = termSolutionDao.loadAll();

        for(TermSolution changedTermSolution : this.changedTermSolutions) {

            boolean found = false;
            for(TermSolution termSolutionAfterUpdate : allTermSolutions) {

                if(CompareUtils.areTermSolutionsTheSame(termSolutionAfterUpdate, changedTermSolution))
                    found = true;

            }
            if(!found)
                fail("Changes did not updated in database");


        }

    }
}
