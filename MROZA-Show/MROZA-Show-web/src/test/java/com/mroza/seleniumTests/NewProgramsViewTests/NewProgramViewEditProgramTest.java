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

import com.mroza.models.Program;
import com.mroza.seleniumTests.ProgramDirectoryViewTests.ProgramDirectoryViewPage;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.SeleniumUtils;
import com.mroza.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import static org.junit.Assert.assertEquals;

public class NewProgramViewEditProgramTest {

    private NewProgramsViewPage newProgramsViewPage;
    private String existingSymbol = "SYMBOL_1";
    private String existingName = "NAME";
    private String existingDescription = "DESCRIPTION";
    private String expectingHeader = Utils.getMsgFromResources("editProgramView.title");
    private Program program;

    @Before
    public void setUp() {
        SeleniumUtils.setUpDriverConnection();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        program = databaseUtils.setUpProgram(existingSymbol, existingName, existingDescription);

        newProgramsViewPage = PageFactory.initElements(new ChromeDriver(), NewProgramsViewPage.class);
        newProgramsViewPage.open(SeleniumUtils.newProgramsViewUrl + "?programId=" + program.getId());
    }

    @After
    public void tearDown() {
        newProgramsViewPage.close();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void editingProgramShouldHaveEqualHeaderTest() {

        String header = newProgramsViewPage.getHeader();
        assertEquals("Adding new program view should be correct", expectingHeader, header);

    }

    @Test
    public void editingProgramShouldHaveCorrectFilledFieldsTest() {
        String symbol = newProgramsViewPage.getSymbol();
        String name = newProgramsViewPage.getName();
        String description = newProgramsViewPage.getDescription();
        assertEquals("Symbol in field should be the same as added", existingSymbol, symbol);
        assertEquals("Name in field should be the same as added", existingName, name);
        assertEquals("Description in field should be the same as added", existingDescription, description);

    }

    @Test
    public void editingProgramShouldChangeProgramsParametersTest() {

        String changedSymbol = "NEW SYMBOL";
        String changedName = "NEW_NAME";
        String changedDescription = "NEW DESCRIPTION";
        newProgramsViewPage.setAddProgramFields(changedSymbol, changedName, changedDescription);
        newProgramsViewPage.clickSaveNewProgram();

        newProgramsViewPage.close();
        newProgramsViewPage = PageFactory.initElements(new ChromeDriver(), NewProgramsViewPage.class);
        newProgramsViewPage.open(SeleniumUtils.newProgramsViewUrl + "?programId=" + program.getId());

        String symbol = newProgramsViewPage.getSymbol();
        String name = newProgramsViewPage.getName();
        String description = newProgramsViewPage.getDescription();
        assertEquals("Symbol in field should have been changed", changedSymbol, symbol);
        assertEquals("Name in field should have been changed", changedName, name);
        assertEquals("Description in field should have benn changed", changedDescription, description);

    }

}
