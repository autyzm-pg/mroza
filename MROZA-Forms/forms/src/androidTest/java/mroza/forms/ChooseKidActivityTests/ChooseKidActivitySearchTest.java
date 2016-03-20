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
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.Suppress;
import android.widget.ListView;
import database.Child;
import database.ChildDao;
import database.DaoMaster;
import database.DaoSession;
import mroza.forms.ChooseKidActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;

import java.util.ArrayList;

public class ChooseKidActivitySearchTest extends ActivityUnitTestCase<ChooseKidActivity> {

    private Context targetContext;

    public ChooseKidActivitySearchTest() {
        super(ChooseKidActivity.class);
    }

    Intent intent;
    ArrayList<Child> children = new ArrayList<Child>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        intent = new Intent(getInstrumentation().getTargetContext(), ChooseKidActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, "BC234");

        targetContext = getInstrumentation().getTargetContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(targetContext, "mroza-db", null);
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        ChildDao childDao = daoSession.getChildDao();

        Child child = new Child();
        child.setCode("AK123");
        child.setIsArchived(false);
        childDao.insertOrReplace(child);
        children.add(child);

        child = new Child();
        child.setCode("BC234");
        child.setIsArchived(false);
        childDao.insertOrReplace(child);
        children.add(child);

        child = new Child();
        child.setCode("KL245");
        child.setIsArchived(false);
        childDao.insertOrReplace(child);
        children.add(child);

        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
        setActivityContext(context);
    }


    @Suppress
    public void testSearch() {
        startActivity(intent, null, null);
        getInstrumentation().callActivityOnResume(getActivity());

        final ListView listView = (ListView)getActivity().findViewById(R.id.listView);
        KidsViewListAdapter adapter = (KidsViewListAdapter) listView.getAdapter();
        String code = adapter.getItem(0).getCode();
        assertEquals("Should search fo correct person, given by query", "BC234", code);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getInstrumentation().getTargetContext(), "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        ChildDao childDao = daoSession.getChildDao();
        for(Child child : children)
            childDao.delete(child);
    }


}
