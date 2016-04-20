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
import com.mroza.seleniumTests.MrozaViewPage;
import com.mroza.utils.SeleniumWaiter;
import com.mroza.utils.Utils;
import javassist.NotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class KidProgramsForPeriodViewPage extends MrozaViewPage{

    public KidProgramsForPeriodViewPage(WebDriver driver) {
        super(driver);
    }

    public void open(String url, Kid kid) {
        driver.get(url + kid.getId() + "&reloadData=true");
    }

    public void close() {
        driver.quit();
    }

    public void turnToProgramsForPeriodPagePart() {
        WebElement  navigationArea = driver.findElement(By.className("ui-tabs-nav"));
        List<WebElement> navigationButtons = navigationArea.findElements(By.tagName("li"));
        for(WebElement navigationButton : navigationButtons){
            if(navigationButton.findElement(By.tagName("a")).getText().equals(Utils.getMsgFromResources("kidProgramsView.programsForPeriod"))){
                navigationButton.click();
                break;
            }
        }
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public List<String> getPeriodDateLabel() {
        List<WebElement> periodsDataLabels = driver.findElements(By.className("fc-title"));
        List<String> periodsDataLabelsText = new ArrayList<>();
        periodsDataLabels.forEach((element) -> periodsDataLabelsText.add(element.getText()));
        return periodsDataLabelsText;
    }

    public String getActualChosenPeriodEndDate() {
        WebElement input = getPeriodDateInput(3);
        return input.getAttribute("value");
    }

    private WebElement getPeriodDateInput(int index) {
        List<WebElement> spans = getActualChosenPeriodDisplayAreaSpans();
        WebElement span = spans.get(index);
        return span.findElement(By.tagName("input"));
    }

    public String getActualChosenPeriodStartDate() {
        WebElement input = getPeriodDateInput(1);
        return input.getAttribute("value");
    }

    public List<String> getAssignedProgramsToActualPeriod() {
        List<WebElement> tableRows = getAssignedProgramTableRows();
        List<String> assignedProgramsSymbols = new ArrayList<>();
        for(WebElement tableRow : tableRows){
            List<WebElement> tableColumns = tableRow.findElements(By.tagName("td"));
            assignedProgramsSymbols.add(tableColumns.get(0).getText());
        }
        return assignedProgramsSymbols;
    }

    private List<WebElement> getActualChosenPeriodDisplayAreaSpans() {
        WebElement form = getProgramsForPeriodFormContainer();
        WebElement outputPanel = form.findElement(By.className("ui-outputpanel"));
        return outputPanel.findElements(By.tagName("span"));
    }

    public void chooseProgram(String symbol) {
        WebElement dialog = getVisibleDialogBox();
        WebElement dialogContent = dialog.findElement(By.className("ui-dialog-content"));
        WebElement dialogForm = dialogContent.findElement(By.tagName("form"));
        WebElement tableWrapper = dialogForm.findElement(By.className("ui-datatable")).findElement(By.className("ui-datatable-tablewrapper"));
        WebElement table = tableWrapper.findElement(By.tagName("table"));
        WebElement tableBody = table.findElement(By.tagName("tbody"));
        List<WebElement> tableRows = tableBody.findElements(By.tagName("tr"));

        for(WebElement tableRow : tableRows){
            List<WebElement> tableColumns = tableRow.findElements(By.tagName("td"));
            if(tableColumns.get(0).getText().equals(symbol)){
                tableColumns.get(2).findElement(By.tagName("button")).click();
                SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
                break;
            }
        }
    }

    public void chooseTable(String name) {
        WebElement dialog = getVisibleDialogBox();
        WebElement dialogContent = dialog.findElement(By.className("ui-dialog-content"));
        WebElement dialogForm = dialogContent.findElement(By.tagName("form"));
        WebElement tableWrapper = dialogForm.findElement(By.className("ui-datatable")).findElement(By.className("ui-datatable-tablewrapper"));
        WebElement table = tableWrapper.findElement(By.tagName("table"));
        WebElement tableBody = table.findElement(By.tagName("tbody"));
        List<WebElement> tableRows = tableBody.findElements(By.tagName("tr"));

        for(WebElement tableRow : tableRows){
            List<WebElement> tableColumns = tableRow.findElements(By.tagName("td"));
            if(tableColumns.get(0).getText().equals(name)){
                tableColumns.get(1).findElement(By.tagName("button")).click();
                SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
                break;
            }
        }
    }

    private List<WebElement> getAssignedProgramTableRows() {
        WebElement tableBody = driver.findElement(By.id("kid-programs-tab-view:periodProgramsForm:programsPerPeriodTab_data"));
        return tableBody.findElements(By.tagName("tr"));
    }

    private WebElement getProgramsForPeriodFormContainer() {
        WebElement mainBody = driver.findElement(By.className("b-main-content"));
        WebElement tabs = mainBody.findElement(By.className("ui-tabs"));
        WebElement tabsPanel = tabs.findElement(By.className("ui-tabs-panels"));
        List<WebElement> panels = tabsPanel.findElements(By.className("ui-tabs-panel"));
        return panels.get(1).findElement(By.tagName("form"));
    }


    public void clickDeletePeriodButton() {
       clickButton(Utils.getMsgFromResources("kidProgramsView.deletePeriod"));
    }

    public void clickAssignProgramButton() {
        clickButton(Utils.getMsgFromResources("kidProgramsView.addProgram"));
    }

    private void clickButton(String buttonName) {
        WebElement form = getProgramsForPeriodFormContainer();
        clickButtonInButtonContainerNamed(buttonName, form);
    }

    public String getActualChosenPeriodMessage() {
        WebElement form = getProgramsForPeriodFormContainer();
        WebElement outputPanel = form.findElement(By.className("ui-outputpanel"));
        return outputPanel.getText();
    }

    public void changeActualPeriodStartDate(Date date) {
        WebElement input = getPeriodDateInput(1);
        input.click();
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
        WebElement dataPicker = driver.findElement(By.className("ui-datepicker-calendar"));
        List<WebElement> calendarFields = dataPicker.findElements(By.tagName("td"));

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        for(WebElement calendarField : calendarFields){
            List<WebElement> calendarFieldValues = calendarField.findElements(By.tagName("a"));
            if(calendarFieldValues.size() > 0){
               String chosenDay = calendarFieldValues.get(0).getText();
                if(chosenDay.equals(String.valueOf(dayOfMonth))){
                    calendarField.click();
                    break;
                }
            }
        }
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public void deleteAssignedPrograms() {
        List<WebElement> tableRows = getAssignedProgramTableRows();
        for(WebElement tableRow : tableRows){
            List<WebElement> columns = tableRow.findElements(By.tagName("td"));
            WebElement buttonsColumn = columns.get(3);
            List<WebElement> buttons = buttonsColumn.findElements(By.tagName("a"));
            buttons.get(1).click();
            SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
            clickYesButtonInDialogBox();
            SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
        }

    }
}
