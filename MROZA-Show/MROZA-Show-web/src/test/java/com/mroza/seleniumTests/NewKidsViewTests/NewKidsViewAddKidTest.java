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
package com.mroza.seleniumTests.NewKidsViewTests;

import com.mroza.seleniumTests.KidsViewTests.KidsViewPage;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.SeleniumUtils;
import com.mroza.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NewKidsViewAddKidTest {

    private NewKidsViewPage newKidsViewPage;
    private KidsViewPage kidsViewPage;
    private String expectedSymbol = "CODE_1", existedSymbol= "CODE_2";
    private String expectedMessage = Utils.getMsgFromResources("newKidsView.codeDuplicatedMsg");
    private String expectedEmptyCodeMessage = "Kod: Wartość wymagana";

    @Before
    public void setUp() {
        SeleniumUtils.setUpDriverConnection();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        databaseUtils.setUpKid(existedSymbol);
        newKidsViewPage = PageFactory.initElements(new ChromeDriver(), NewKidsViewPage.class);
        newKidsViewPage.open(SeleniumUtils.newKidsViewUrl);
    }

    @After
    public void tearDown() {
        newKidsViewPage.close();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void addNewKidTest() {
        newKidsViewPage.setKidCode(expectedSymbol);
        newKidsViewPage.clickSaveNewKid();

        kidsViewPage =  PageFactory.initElements(new ChromeDriver(), KidsViewPage.class);
        kidsViewPage.open(SeleniumUtils.kidsViewUrl);
        List<String> foundSymbols = kidsViewPage.getKidsSymbolsList();
        kidsViewPage.close();

        assertTrue("New kid code should be on list", foundSymbols.contains(expectedSymbol));

    }

    @Test
    public void addNewKidWithTheSameCodeTest() {
        newKidsViewPage.setKidCode(existedSymbol);
        newKidsViewPage.clickSaveNewKid();
        String controlMessage = newKidsViewPage.getShowedMessage();

        assertEquals("Message should show that kid symbol already exists", expectedMessage, controlMessage);

    }

    @Test
    public void addNewKidWithEmptyCodeTest() {
        newKidsViewPage.clickSaveNewKid();
        String controlMessage = newKidsViewPage.getShowedMessage();

        assertEquals("Message should show that that code is needed", expectedEmptyCodeMessage, controlMessage);

    }

    @Test
    public void addNewKidAndCancelTest() {
        newKidsViewPage.setKidCode(expectedSymbol);
        newKidsViewPage.clickCancelNewKid();

        kidsViewPage =  PageFactory.initElements(new ChromeDriver(), KidsViewPage.class);
        kidsViewPage.open(SeleniumUtils.kidsViewUrl);
        List<String> foundSymbols = kidsViewPage.getKidsSymbolsList();
        kidsViewPage.close();

        assertFalse("Canceled kid code should not be on list", foundSymbols.contains(expectedSymbol));
    }
}
