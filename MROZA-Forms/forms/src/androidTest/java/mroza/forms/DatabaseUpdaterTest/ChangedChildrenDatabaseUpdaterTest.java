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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ChangedChildrenDatabaseUpdaterTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private DatabaseUpdater databaseUpdater;
    private List<Child> unchangedChildren;
    private DaoSession daoSession;
    private List<Child> changedChildren;

    public ChangedChildrenDatabaseUpdaterTest() {
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
        this.unchangedChildren = TestUtils.setUpChildren(targetContext, 4, "UNCHANGED.");
        this.changedChildren = changeHalfOfChildren(this.unchangedChildren);
    }

    private List<Child> changeHalfOfChildren(List<Child> allChildList) {
        List<Child> changedChildList = new ArrayList<>();
        int numberOfElements = 2;

        for (int childId = 0; childId < numberOfElements; childId++) {
            Child child = allChildList.get(childId);
            child.setCode("CHANGED." + childId);
            changedChildList.add(child);
        }
        return changedChildList;

    }

    public void testUpdateChangedChild() throws ParseException {

        databaseUpdater.updateChildren(this.changedChildren);
        ChildDao childDao = daoSession.getChildDao();
        List<Child> allChildren = childDao.loadAll();

        for(Child child : this.changedChildren) {

            boolean found = false;
            for(Child childrenAfterUpdate : allChildren) {

                if(CompareUtils.areTwoChildsTheSame(childrenAfterUpdate, child))
                   found = true;

            }
            if(!found)
                fail("Changes did not updated in database");
        }

    }
}
