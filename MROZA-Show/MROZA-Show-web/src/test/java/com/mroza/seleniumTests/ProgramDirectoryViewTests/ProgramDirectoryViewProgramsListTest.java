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
package com.mroza.seleniumTests.ProgramDirectoryViewTests;

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
import static org.junit.Assert.assertTrue;

public class ProgramDirectoryViewProgramsListTest {

    private ProgramDirectoryViewPage programDirectoryViewPage;
    private List<Program> expectedPrograms;
    private List<String> expectedSymbols;
    private DatabaseUtils databaseUtils;
    private List<KidTable> expectedKidTables;

    @Before
    public void setUp() {
        databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        setUpPrograms();

        programDirectoryViewPage = MrozaPageFactory.initElements(ProgramDirectoryViewPage.class);
        programDirectoryViewPage.open(SeleniumUtils.programDirectoryViewPageUrl);
    }

    @After
    public void tearDown() {
        programDirectoryViewPage.close();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void showProgramsListTest() {
        List<String> foundSymbols = programDirectoryViewPage.getAllProgramsSymbols();

        assertEquals("Found symbols list should be the same size as expected", expectedSymbols.size(), foundSymbols.size());
        assertTrue("Expected symbol should be found on list", foundSymbols.containsAll(expectedSymbols));
    }

    @Test
    public void showExpectedProgramSymbolsOnListAfterSearchTest() {
        String expectedSearchedSymbol = expectedSymbols.get(0);
        programDirectoryViewPage.setSearchValue(expectedSearchedSymbol);
        List<String> programSymbols = programDirectoryViewPage.getAllProgramsSymbols();

        assertEquals("ProgramSymbols list should have the same size as expected", 1 ,programSymbols.size());
        assertEquals("SearchedSymbol should be the same ad ExpectedSearchSymbol", expectedSearchedSymbol, programSymbols.get(0));
    }

    @Test
    public void showExpectedProgramSymbolsOnListAfterSearchByFilterTest() {
        String expectedSearchedSymbol = expectedSymbols.get(0);
        programDirectoryViewPage.setLetterFilterValue("A");
        List<String> programSymbols = programDirectoryViewPage.getAllProgramsSymbols();

        assertEquals("ProgramSymbols list should have the same size as expected", 1 ,programSymbols.size());
        assertEquals("SearchedSymbol should be the same ad ExpectedSearchSymbol",expectedSearchedSymbol ,programSymbols.get(0));
    }


    @Test
    public void deleteProgramTest() {
        programDirectoryViewPage.clickDeleteProgramButtonForProgramWithSymbol(expectedPrograms.get(0).getSymbol());
        programDirectoryViewPage.clickYesButtonInDialogBox();
        List<String> programSymbols = programDirectoryViewPage.getAllProgramsSymbols();
        assertEquals("ProgramSymbols after delete should have smaller size", 1 ,programSymbols.size());
        assertEquals("Program left on list should have expected symbol", expectedPrograms.get(1).getSymbol(), programSymbols.get(0));
    }

    private void setUpPrograms() {
        expectedPrograms = new ArrayList<>();
        expectedKidTables = new ArrayList<>();
        expectedSymbols = new ArrayList<String>(){{
            add("A_SYMBOL_1");
            add("B_SYMBOL_2");}};
        List<String> rowsNames = new ArrayList<String>(){{add("ROW_NAME");}};
        Date startDate = Utils.getDateFromNow(-2);
        Date endDate = Utils.getDateFromNow(2);
        Kid kid = this.databaseUtils.setUpKid("CODE_1");
        Period actualPeriod = databaseUtils.setUpPeriod(startDate, endDate, kid);

        for(String symbol : expectedSymbols)
        {
            Program expectedProgram = databaseUtils.setUpProgram(symbol, "NAME " + symbol, "DECRYPTION");
            expectedPrograms.add(expectedProgram);
            Table table = databaseUtils.setUpTableWithRows("TABLE_" + symbol, rowsNames, "DESCRIPTION_" + symbol, 1, 1, expectedProgram);
            KidTable kidTable = databaseUtils.setUpKidTable(table, actualPeriod);
            expectedKidTables.add(kidTable);
        }
    }

}
