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
import android.test.ActivityInstrumentationTestCase2;
import mroza.forms.ChooseKidActivity;
import syncmodels.SyncManager;

import java.util.Date;

public class SyncManagerTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private SyncManager syncManager;

    public SyncManagerTest() {
            super(ChooseKidActivity.class);
            }

    protected void setUp() throws Exception {
        super.setUp();
        syncManager = new SyncManager(getInstrumentation().getTargetContext());
    }

    public void testSetSyncDate()
    {
        Date firstSyncDate = new Date();

        syncManager.setSyncDate(firstSyncDate);
        Date lastSyncDate = syncManager.getLastSyncDate();
        assertEquals("Sync dates shoud be the same", lastSyncDate, firstSyncDate);

    }

}
