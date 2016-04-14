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
import com.mroza.utils.SeleniumWaiter;
import com.mroza.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

public class KidProgramsForPeriodViewPage {

    protected WebDriver driver;

    public KidProgramsForPeriodViewPage(WebDriver driver) {
        this.driver = driver;
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

    public String getHeader() {
        WebElement header = driver.findElement(By.className("b-page-header"));
        return header.getText();
    }

    public List<String> getPeriodDateLabel() {
        List<WebElement> periodsDataLabels = driver.findElements(By.className("fc-title"));
        List<String> periodsDataLabelsText = new ArrayList<>();
        periodsDataLabels.forEach((element) -> periodsDataLabelsText.add(element.getText()));
        return periodsDataLabelsText;
    }

    public String getActualChosenPeriodEndDate() {
        List<WebElement> spans = getActualChosenPeriodDisplayAreaSpans();
        WebElement endSpan = spans.get(3);
        WebElement input = endSpan.findElement(By.tagName("input"));
        return input.getAttribute("value");
    }

    public String getActualChosenPeriodStartDate() {
        List<WebElement> spans = getActualChosenPeriodDisplayAreaSpans();
        WebElement actualSpan = spans.get(1);
        WebElement input = actualSpan.findElement(By.tagName("input"));
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

    public void clickYesButtonInDialogBox() {
        clickDialogButton(Utils.getMsgFromResources("main.yes"));
    }

    public void clickAssignProgramButton() {
        clickButton(Utils.getMsgFromResources("kidProgramsView.addProgram"));
    }

    private void clickDialogButton(String buttonName) {
        WebElement dialog = getVisibleDialogBox();
        WebElement buttonPanel = dialog.findElement(By.className("ui-dialog-buttonpane"));
        List<WebElement> buttons = buttonPanel.findElements(By.tagName("button"));

        for(WebElement button :  buttons){
            List<WebElement> spanElements = button.findElements(By.tagName("span"));
            if(spanElements.get(1).getText().equals(buttonName)) {
                Actions actions = new Actions(driver);
                actions.moveToElement(button).click().perform();
                SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
                break;
            }
        }
    }

    private void clickButton(String buttonName) {
        WebElement form = getProgramsForPeriodFormContainer();
        WebElement actionButtons = form.findElement(By.className("action-buttons-container"));
        for(WebElement button : actionButtons.findElements(By.tagName("button"))){
            if(button.findElement(By.tagName("span")).getText().equals(buttonName)) {
                button.click();
                SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
            }
        }
    }

    public String getActualChosenPeriodMessage() {
        WebElement form = getProgramsForPeriodFormContainer();
        WebElement outputPanel = form.findElement(By.className("ui-outputpanel"));
        return outputPanel.getText();
    }

    public String getErrorMessage() {
        WebElement errorMessage = driver.findElement(By.className("ui-messages-error-summary"));
        return errorMessage.getText();
    }

    private WebElement getVisibleDialogBox() {
        List<WebElement> dialogs = driver.findElements(By.className("ui-dialog"));
        WebElement goodDialog = null;
        SeleniumWaiter.waitForDialogBoxAppears(driver);
        for(WebElement dialog : dialogs){
            if(dialog.getAttribute("aria-hidden").equals("false"))
                goodDialog = dialog;
        }
        return goodDialog;
    }
}
