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
package com.mroza.seleniumTests.KidProgramsViewTests;


import com.mroza.models.Kid;
import com.mroza.models.Program;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.SeleniumUtils;
import com.mroza.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class KidProgramsViewTest {

    private KidProgramsViewPage kidProgramsViewPage;
    private Kid kid;
    private List<Program> programs = new ArrayList<>();
    private Program programWithTable;

    @Before
    public void setUp() {
        SeleniumUtils.setUpDriverConnection();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        kid = databaseUtils.setUpKid("CODE_1");
        programs.add(databaseUtils.setUpProgram("A_SYMBOL_1", "NAME_1", "DESCRIPTION_1", kid));

        programWithTable = databaseUtils.setUpProgram("B_SYMBOL_2", "NAME_2", "DESCRIPTION_2", kid);
        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, programWithTable);
        programs.add(programWithTable);

        kidProgramsViewPage = PageFactory.initElements(new ChromeDriver(), KidProgramsViewPage.class);
        kidProgramsViewPage.open(SeleniumUtils.kidProgramsViewUrl, kid);
    }

    @After
    public void tearDown() {
        kidProgramsViewPage.close();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void showExpectedHeaderTest() {
        String header = kidProgramsViewPage.getHeader();
        assertTrue("Header should contain kid code", header.contains(kid.getCode()));
    }


    @Test
    public void showExpectedAssignedProgramsSymbolsTest() {
        List<String> symbolsList = kidProgramsViewPage.getProgramsSymbolsList();
        List<String> expectedSymbols = programs.stream().map(Program::getSymbol).collect(Collectors.toList());
        assertEquals("Program list should have one program", 2, symbolsList.size());
        assertTrue("Expected program has not been found on list", symbolsList.containsAll(expectedSymbols));
    }

    @Test
    public void showExpectedSearchedAssignedProgramsSymbolsTest() {
        String expectedSymbol = programs.get(0).getSymbol();
        kidProgramsViewPage.setSearchValue(expectedSymbol);
        List<String> symbolsList = kidProgramsViewPage.getProgramsSymbolsList();
        assertEquals("Program list should have one program", 1, symbolsList.size());
        assertEquals("Program assigned to kid should be on list", expectedSymbol, symbolsList.get(0));
    }


    @Test
    public void showExpectedSearchedAssignedProgramsNamesTest() {
        String expectedName = programs.get(0).getName();
        kidProgramsViewPage.setSearchValue(expectedName);
        List<String> namesList = kidProgramsViewPage.getProgramNamesList();
        assertEquals("Searched program list should have one program", 1, namesList.size());
        assertEquals("Searched programs name to kid should be on list",expectedName, namesList.get(0));
    }


    @Test
    public void showExpectedLetterFilterAssignedProgramsSymbolTest() {
        String expectedSymbol = programs.get(0).getSymbol();
        kidProgramsViewPage.clickLetterFilter("A");
        List<String> symbolList = kidProgramsViewPage.getProgramsSymbolsList();
        assertEquals("Selected by letter filter program list should have one program", 1, symbolList.size());
        assertEquals("Selected by letter filter programs symbols assigned to kid should be on list", expectedSymbol, symbolList.get(0));
    }

    @Test
    public void deleteProgramTest(){
        String deleteProgramSymbol = programs.get(0).getSymbol();
        String expectedSymbol = programs.get(1).getSymbol();

        kidProgramsViewPage.clickDeleteButton(deleteProgramSymbol);
        kidProgramsViewPage.clickYesButtonInDialogBox();

        List<String> symbolList = kidProgramsViewPage.getProgramsSymbolsList();
        assertEquals("After program delete list should have only one program", 1, symbolList.size());
        assertEquals("After program delete list should have only expected program", expectedSymbol, symbolList.get(0));

    }

    @Test
    public void deleteProgramWithTableTest(){
        String deleteProgramSymbol = this.programWithTable.getSymbol();

        kidProgramsViewPage.clickDeleteButton(deleteProgramSymbol);
        kidProgramsViewPage.clickYesButtonInDialogBox();

        String errorMessage = kidProgramsViewPage.getErrorMessage();
        assertEquals("Error message about program is having tables should be shown", Utils.getMsgFromResources("kidProgramsView.unableToRemoveProgramAssignment") ,errorMessage);

        List<String> symbolList = kidProgramsViewPage.getProgramsSymbolsList();
        assertEquals("Program should have not been deleted, list should contain all programs", 2, symbolList.size());
        assertTrue("Program should have not been deleted", symbolList.contains(deleteProgramSymbol));
    }


}
