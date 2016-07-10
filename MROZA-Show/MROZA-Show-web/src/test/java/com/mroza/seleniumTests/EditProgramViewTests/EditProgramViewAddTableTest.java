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

import com.mroza.models.Kid;
import com.mroza.models.Program;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.MrozaPageFactory;
import com.mroza.utils.SeleniumUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EditProgramViewAddTableTest {

    private Program program;
    private EditProgramViewPage editProgramViewPage;

    @Before
    public void setUp() {
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        Kid kid = databaseUtils.setUpKid("CODE_1");
        program  = databaseUtils.setUpProgram("A_SYMBOL_1", "NAME_1", "DESCRIPTION_1", kid);
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
    public void showExpectedHeaderTest() {
        String header = editProgramViewPage.getHeader();
        assertTrue("Header should contain program symbol", header.contains(program.getSymbol()));
    }

    @Test
    public void showEmptyListWithoutAnyTablesTest() {
        List<String> tablesListContent = editProgramViewPage.getTablesNamesListContent();
        assertEquals("Content should show only one empty message", tablesListContent.size(), 1);
        assertEquals("Content should show empty message", tablesListContent.get(0), "No records found.");
    }

    @Test
    public void addTableToProgramTest() {
        editProgramViewPage.clickAddNewTableButton();
        List<String> tablesNamesListContent = editProgramViewPage.getTablesNamesListContent();
        assertEquals("Content should only one table", tablesNamesListContent.size(), 1);
        assertEquals("Table should have empty header", tablesNamesListContent.get(0), "");

        String tableName = "TABLE_NAME";
        editProgramViewPage.inputTableName(tableName);
        editProgramViewPage.clickAddNewRowButtonForTable(tableName);
        editProgramViewPage.setUpTableContentHeader(tableName, 1, 1);
        editProgramViewPage.changeTableRow(tableName, "ROW_NAME","");
        editProgramViewPage.clickSaveTableButton(tableName);


        refreshEditProgramView();

        tablesNamesListContent = editProgramViewPage.getTablesNamesListContent();
        assertEquals("Content should only one table", tablesNamesListContent.size(), 1);
        assertEquals("Table should have expected header", tablesNamesListContent.get(0), tableName);

    }

    private void refreshEditProgramView() {
        editProgramViewPage.close();
        editProgramViewPage = PageFactory.initElements(new ChromeDriver(), EditProgramViewPage.class);
        editProgramViewPage.open(SeleniumUtils.editProgramsViewUrl, program);
    }


}
