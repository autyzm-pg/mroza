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

package mroza.forms.ChooseKidActivityTests;

import adapters.KidsViewListAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import database.Child;
import database.ChildDao;
import database.DaoMaster;
import database.DaoSession;
import mroza.forms.ChooseKidActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;


public class ChooseKidActivityTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private Child child;
    DaoMaster daoMaster;
    private Context targetContext;

    public ChooseKidActivityTest() {
        super(ChooseKidActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(targetContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        DaoSession daoSession = daoMaster.newSession();

        child = new Child();
        child.setCode("AK123");
        child.setIsArchived(false);
        ChildDao childDao = daoSession.getChildDao();
        childDao.deleteAll();
        childDao.insertOrReplace(child);

    }

    private void cleanUpDatabase() {
        DaoSession daoSession = daoMaster.newSession();
        daoSession.getChildDao().deleteAll();
        daoSession.getProgramDao().deleteAll();
        daoSession.getTableTemplateDao().deleteAll();
        daoSession.getChildTableDao().deleteAll();
        daoSession.getTermSolutionDao().deleteAll();
        daoSession.getTableRowDao().deleteAll();
        daoSession.getTableFieldDao().deleteAll();
        daoSession.getTableFieldFillingDao().deleteAll();
    }

    public void testGettingChildsFromDatabase()
    {
        Activity mainAcivity = this.getActivity();
        ListView listeActivites = (ListView) mainAcivity.findViewById(R.id.listView);
        KidsViewListAdapter adapter = (KidsViewListAdapter) listeActivites.getAdapter();
        Child child = adapter.getItem(0);
        assertEquals("Should take correct person data from db to list",this.child.getId(),child.getId());

    }

}
