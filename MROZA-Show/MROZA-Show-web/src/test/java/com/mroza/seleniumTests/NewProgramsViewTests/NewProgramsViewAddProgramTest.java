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
package com.mroza.seleniumTests.NewProgramsViewTests;

import com.mroza.seleniumTests.ProgramDirectoryViewTests.ProgramDirectoryViewPage;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.SeleniumUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NewProgramsViewAddProgramTest {

    private NewProgramsViewPage newProgramsViewPage;
    private ProgramDirectoryViewPage programDirectoryViewPage;
    private String existingSymbol = "SYMBOL_1";
    private String expectedSymbol = "SYMBOL_2";
    private String expectedMessage = "Symbol już istnieje - wprowadź inny";
    private String[] expectedMessages = {"Nazwa: Wartość wymagana","Symbol: Wartość wymagana"};

    @Before
    public void setUp() {
        SeleniumUtils.setUpDriverConnection();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        databaseUtils.setUpProgram(existingSymbol, "NAME", "DESCRIPTION");
        newProgramsViewPage = PageFactory.initElements(new ChromeDriver(), NewProgramsViewPage.class);
        newProgramsViewPage.open(SeleniumUtils.newProgramsViewUrl);
    }

    @After
    public void tearDown() {
        newProgramsViewPage.close();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void addNewProgramWithExistingSymbolTest() {
        newProgramsViewPage.setAddProgramFields(existingSymbol, "NAME", "DESCRIPTION");
        newProgramsViewPage.clickSaveNewProgram();
        String controlMessage = newProgramsViewPage.getShowedMessage();
        assertEquals("Message should show that program symbol already exists", expectedMessage, controlMessage);
    }

    @Test
    public void addNewProgramTest() {

        newProgramsViewPage.setAddProgramFields(expectedSymbol, "NAME", "DESCRIPTION");
        newProgramsViewPage.clickSaveNewProgram();

        programDirectoryViewPage =  PageFactory.initElements(new ChromeDriver(), ProgramDirectoryViewPage.class);
        programDirectoryViewPage.open(SeleniumUtils.programDirectoryViewPageUrl);
        List<String> foundSymbols = programDirectoryViewPage.getAllProgramsSymbols();
        programDirectoryViewPage.close();

        Boolean found = false;
        for(String foundSymbol : foundSymbols)
            if(foundSymbol.equals(expectedSymbol))
                found = true;

        assertTrue("New program symbol should be on list", found);

    }

    @Test
    public void saveNewProgramWhenFieldsAreEmptyTest() {

        newProgramsViewPage.clickSaveNewProgram();
        List<String> controlMessages = newProgramsViewPage.getShowedMessages();
        assertEquals("Message list should be equal empty name and symbol fields",controlMessages.size(), 2);

        Boolean found;
        for(String controlMessage : controlMessages) {
            found = false;
            for (String expectedMessage : expectedMessages)
                if (expectedMessage.equals(controlMessage))
                    found = true;
            assertTrue("Control message should show what is empty", found);
        }
    }

    @Test
    public void cancelAddingProgramTest() {

        String canceledSymbol = "SYMBOL_3";
        newProgramsViewPage.setAddProgramFields(canceledSymbol, "NAME", "DESCRIPTION");
        newProgramsViewPage.clickCancelNewProgram();

        programDirectoryViewPage =  PageFactory.initElements(new ChromeDriver(), ProgramDirectoryViewPage.class);
        programDirectoryViewPage.open(SeleniumUtils.programDirectoryViewPageUrl);
        List<String> foundSymbols = programDirectoryViewPage.getAllProgramsSymbols();
        programDirectoryViewPage.close();

        Boolean found = false;
        for(String foundSymbol : foundSymbols)
            if(foundSymbol.equals(canceledSymbol))
                found = true;

        assertFalse("Canceled program symbol should not be on list", found);

    }


}
