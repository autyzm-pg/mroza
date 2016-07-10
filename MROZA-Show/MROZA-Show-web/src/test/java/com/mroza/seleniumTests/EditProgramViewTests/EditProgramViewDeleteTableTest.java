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
package com.mroza.seleniumTests.EditProgramViewTests;

import com.mroza.models.*;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.MrozaPageFactory;
import com.mroza.utils.SeleniumUtils;
import com.mroza.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class EditProgramViewDeleteTableTest {

    private EditProgramViewPage editProgramViewPage;
    private Program program;
    private Table table;
    private Table tableAssignedToPeriod;
    private String expectedErrorMessage = Utils.getMsgFromResources("editProgramView.unableToDeleteTable");

    @Before
    public void setUp() {
        setUpTables();
        editProgramViewPage = MrozaPageFactory.initElements(EditProgramViewPage.class);
        editProgramViewPage.open(SeleniumUtils.editProgramsViewUrl, program);
    }

    @After
    public void tearDown() {
        editProgramViewPage.close();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void deleteTableTest() {
        editProgramViewPage.clickDeleteButton(table.getName());
        editProgramViewPage.clickYesButtonInDialogBox();
        List<String> tablesNamesListContent = editProgramViewPage.getTablesNamesListContent();
        assertEquals("Content should have left tables", tablesNamesListContent.size(), 1);
        assertFalse("Content should not have deleted table", tablesNamesListContent.stream()
                .anyMatch(tableName  -> table.getName().equals(tableName)));
    }


    @Test
    public void deleteTableAssignedToPeriodTest() {
        editProgramViewPage.clickDeleteButton(tableAssignedToPeriod.getName());

        editProgramViewPage.clickYesButtonInDialogBox();

        String errorMessage = editProgramViewPage.getErrorMessage();
        assertEquals("Error message about unable to delete should be shown", expectedErrorMessage, errorMessage);

        List<String> tablesNamesListContent = editProgramViewPage.getTablesNamesListContent();
        assertEquals("Content should have all tables", tablesNamesListContent.size(), 2);
    }

    private void setUpTables() {
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        Kid kid = databaseUtils.setUpKid("CODE");
        program = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION", kid);
        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        table = databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, program);
        tableAssignedToPeriod = databaseUtils.setUpTableWithRows("TABLE_NAME_ASSIGNED_TO_PERIOD", rowNames, "DESCRIPTION", 2, 2, program);

        Date startDate = Utils.getDateFromNow(-2);
        Date endDate = Utils.getDateFromNow(2);
        Period period = databaseUtils.setUpPeriod(startDate, endDate, kid);
        databaseUtils.setUpKidTable(tableAssignedToPeriod, period);
    }

}
