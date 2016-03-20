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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import mroza.forms.ChooseKidActivity;
import syncmodels.SyncManager;


public class SyncManagerGetUrlToGetAllDataTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private SyncManager syncManager;
    private String expectedUrl = "http://10.0.2.2:8080/ws/rest/getAllData";

    public SyncManagerGetUrlToGetAllDataTest() {
        super(ChooseKidActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Context context = getInstrumentation().getTargetContext();
        syncManager = new SyncManager(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("URL_TO_RECEIVE_ALL", expectedUrl);
        editor.commit();
    }

    public void testGetUrlToServer()
    {
        String url = syncManager.getUrlToServer(SyncManager.SYNC_OPTION.RECEIVE_ALL_DATA);
        assertEquals("Url to server should be the same", url, expectedUrl);

    }
}
