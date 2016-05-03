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

package com.mroza.seleniumTests.KidsViewTests;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.SeleniumUtils;
import com.mroza.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import static org.junit.Assert.assertEquals;

public class KidsViewHeaderTest {

    private KidsViewPage page;

    @Before
    public void setUp() {
        SeleniumUtils.setUpDriverConnection();
        page = PageFactory.initElements(new ChromeDriver(), KidsViewPage.class);
        page.open(SeleniumUtils.kidsViewUrl);
    }

    @After
    public void tearDown() {
        page.close();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void showCorrectHeaderTest() {
        assertEquals("Headers should be the same", Utils.getMsgFromResources("kidsView.kids"), page.getHeader());
    }
}
