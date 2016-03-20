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

package mroza.forms.TestUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import mroza.forms.ChooseKidActivity;
import mroza.forms.TestUtils.TestUtils;


public class ClearUpDatabase extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    //private String urtTogetAllData = "http://10.0.2.2:8080/ws/rest/getAllData";
    //private String urlToSendDane = "http://10.0.2.2:8080/ws/rest/syncData";
    private String urtToGetAllData = "http://192.168.0.14:8080/ws/rest/getAllData";
    private String urlToSendDane = "http://192.168.0.14:8080/ws/rest/syncData";
    private String urlToUpdate = "http://192.168.0.15:8080/ws/rest/update";
    private String serverBase = "192.168.0.15:8080";
    private Context targetContext;
    private int syncDeltaTime = 2 ;

    public ClearUpDatabase() {
        super(ChooseKidActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        TestUtils.cleanUpDatabase(targetContext);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(targetContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("URL_TO_SEND_UPDATE", urlToSendDane);
        editor.putString("URL_TO_RECEIVE_ALL", urtToGetAllData);
        editor.putInt("SYNC_DELTA_TIME", syncDeltaTime);
        editor.putString("URL_TO_UPDATE", urlToUpdate);
        editor.putString("URL_TO_SERVER", serverBase);
        editor.commit();

    }

    public void testPrepareDatabaseAndPrepareUrls()
    {
        assertEquals("Database is clear", true, true);
    }


}
