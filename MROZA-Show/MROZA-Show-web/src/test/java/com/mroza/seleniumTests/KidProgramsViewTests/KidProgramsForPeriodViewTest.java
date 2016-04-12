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

import com.mroza.models.*;
import com.mroza.utils.DatabaseUtils;
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

public class KidProgramsForPeriodViewTest {

    private Kid kid;
    private KidProgramsForPeriodViewPage kidProgramsForPeriodViewPage;
    private Program program;
    private Table table;
    private List<String> rowsNames = new ArrayList<String>(){{add("ROW_NAME");}};
    private Period actualPeriod;
    private KidTable kidTable;
    private Date startDate;
    private Date endDate;
    private Program programToAssign;
    private Table tableToAssign;
    private List<String> programsSymbols;

    @Before
    public void setUp() {
        SeleniumUtils.setUpDriverConnection();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        kid = databaseUtils.setUpKid("CODE_1");
        program = databaseUtils.setUpProgram("SYMBOL_1", "NAME_1", "DESCTIPTION_1", kid);
        programToAssign = databaseUtils.setUpProgram("SYMBOL_2", "NAME_2", "DESCTIPTION_2", kid);
        programsSymbols = new ArrayList<String>(){{
            add(program.getSymbol());
            add(programToAssign.getSymbol());
        }};
        table = databaseUtils.setUpTableWithRows("TABLE_1", rowsNames, "DESCRIPTION_1", 1, 1, program);
        tableToAssign = databaseUtils.setUpTableWithRows("TABLE_2", rowsNames, "DESCRIPTION_2", 1, 1, programToAssign);
        startDate = Utils.getDateFromNow(-2);
        endDate = Utils.getDateFromNow(2);
        actualPeriod = databaseUtils.setUpPeriod(startDate, endDate, kid);
        kidTable = databaseUtils.setUpKidTable(table, actualPeriod);
        kidProgramsForPeriodViewPage = PageFactory.initElements(new ChromeDriver(), KidProgramsForPeriodViewPage.class);
        kidProgramsForPeriodViewPage.open(SeleniumUtils.kidProgramsViewUrl, kid);
        kidProgramsForPeriodViewPage.turnToProgramsForPeriodPagePart();
    }

    @After
    public void tearDown() {
        kidProgramsForPeriodViewPage.close();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void showExpectedHeaderTest() {
        String header = kidProgramsForPeriodViewPage.getHeader();
        assertTrue("Header should contain kid code", header.contains(kid.getCode()));
    }

    @Test
    public void showedPeriodShouldStartAtExpectedDaysTest() {
        List<String> periodDateLabel = kidProgramsForPeriodViewPage.getPeriodDateLabel();
        assertTrue("PeriodDateLabels list should contain one period", periodDateLabel.size() > 0);
        assertTrue("PeriodDateLabel should contain correct start date", periodDateLabel.get(0).contains(Utils.dateToStr(startDate, "dd-MM-yyyy")));
        assertTrue("PeriodDateLabel should contain correct start date", periodDateLabel.get(0).contains(Utils.dateToStr(endDate, "dd-MM-yyyy")));
    }

    @Test
    public void firstChosenPeriodTest() {
        String startDateString = kidProgramsForPeriodViewPage.getActualChosenPeriodStartDate();
        String endDateString = kidProgramsForPeriodViewPage.getActualChosenPeriodEndDate();
        assertTrue("Actual chosen period should be a period with actual start date", startDateString.contains(Utils.dateToStr(startDate, "dd-MM-yyyy")));
        assertTrue("Actual chosen period should be a period with actual end date", endDateString.contains(Utils.dateToStr(endDate, "dd-MM-yyyy")));
    }


    @Test
    public void periodAssignedProgramsTest() {
        List<String> assignedProgramsSymbols = kidProgramsForPeriodViewPage.getAssignedProgramsToActualPeriod();
        assertEquals("Period should have one program assigned", assignedProgramsSymbols.size(), 1);
        assertEquals("Period should have correct symbol program assigned", assignedProgramsSymbols.get(0), program.getSymbol());
    }

    @Test
        public void assignProgramToPeriodTest() {
        kidProgramsForPeriodViewPage.clickAssignProgramButton();
        kidProgramsForPeriodViewPage.chooseProgram(programToAssign.getSymbol());
        kidProgramsForPeriodViewPage.chooseTable(tableToAssign.getName());
        List<String> assignedProgramsSymbols = kidProgramsForPeriodViewPage.getAssignedProgramsToActualPeriod();
        assertEquals("Period should have two programsSymbols assigned", assignedProgramsSymbols.size(), 2);
        assertTrue("Period should have correct symbols of program assigned", assignedProgramsSymbols.containsAll(programsSymbols));
    }

    @Test
    public void deletePeriodTest() {
        kidProgramsForPeriodViewPage.clickDeletePeriodButton();
        kidProgramsForPeriodViewPage.clickYesButtonInDialogBox();
        String notChosenPeriodMessage = kidProgramsForPeriodViewPage.getActualChosenPeriodMessage();
        assertTrue("Actual chosen program should be deleted - not chosen program message", notChosenPeriodMessage.equals(Utils.getMsgFromResources("kidProgramsView.noCurrentChosenPeriod")));
        List<String> assignedProgramsSymbols = kidProgramsForPeriodViewPage.getAssignedProgramsToActualPeriod();
        assertEquals("No programs should be assigned", assignedProgramsSymbols.size(), 1);
        assertEquals("No programs should be assigned", assignedProgramsSymbols.get(0), Utils.getMsgFromResources("main.emptyMessage"));


    }

}
