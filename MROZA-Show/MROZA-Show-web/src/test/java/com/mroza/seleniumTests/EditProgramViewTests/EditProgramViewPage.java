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

import com.mroza.models.Program;
import com.mroza.seleniumTests.MrozaViewPage;
import com.mroza.utils.SeleniumWaiter;
import com.mroza.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class EditProgramViewPage extends MrozaViewPage{

    public EditProgramViewPage(WebDriver driver) {
        super(driver);
    }
    public void open(String url, Program program) {
        driver.get(url + program.getId());
    }
    public void close() {
        driver.quit();
    }

    public List<String> getTablesNamesListContent() {
        WebElement tableAreaContent = getTableArea();
        List<WebElement> tablesList = getTablesList();
        if(tablesList.size() == 0)
            return new ArrayList<String>(){{add(tableAreaContent.getText());}};

        List<String> tablesHeaders = new ArrayList<>();
        for(WebElement table : tablesList)
        {
            WebElement tableHeader = table.findElement(By.className("ui-datatable-header"));
            tablesHeaders.add(tableHeader.getText());
        }
        return tablesHeaders;
    }

    private WebElement getTableArea() {
        WebElement tableArea = driver.findElement(By.className("ui-datagrid"));
        return tableArea.findElement(By.className("ui-datagrid-content"));
    }

    public void clickAddNewTableButton() {
        clickButtonInButtonContainerNamed(Utils.getMsgFromResources("editProgramView.addTable"));
    }


    public void inputTableName(String tableName) {
        WebElement table = getTableWithHeader("", true);
        WebElement tableHeader = table.findElement(By.className("ui-datatable-header"));
        tableHeader.findElement(By.tagName("input")).sendKeys(tableName);
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }


    public void changeTableName(String tableName, String oldTableName) {
        WebElement table = getTableWithHeader(oldTableName, true);
        WebElement tableHeader = table.findElement(By.className("ui-datatable-header"));
        tableHeader.findElement(By.tagName("input")).clear();
        tableHeader.findElement(By.tagName("input")).sendKeys(tableName);
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public void clickAddNewRowButtonForTable(String tableName) {
        clickButtonForTable(tableName, Utils.getMsgFromResources("editProgramView.addRow"), true);
    }

    public void clickSaveTableButton(String tableName) {
        clickButtonForTable(tableName, Utils.getMsgFromResources("main.save"), true);
    }

    public void clickEditTableButton(String tableName) {
        clickButtonForTable(tableName,  Utils.getMsgFromResources("main.edit"), false);
    }

    private void clickButtonForTable(String tableName, String buttonName, Boolean isEditing) {
        WebElement table = getTableWithHeader(tableName, isEditing);
        clickButtonIn(buttonName, table);
    }

    public void changeTableRow(String tableName, String rowName, String oldRowName) {
        WebElement tableRow = getRowForTable(tableName, oldRowName, true);
        List<WebElement> tableRowColumns = tableRow.findElements(By.tagName("td"));
        tableRowColumns.get(0).findElement(By.tagName("input")).clear();
        tableRowColumns.get(0).findElement(By.tagName("input")).sendKeys(rowName);
    }

    public void setUpTableContentHeader(String tableName, int teachingNumber, int generalizationNumber){
        WebElement table = getTableWithHeader(tableName, true);
        WebElement tableContent = table.findElement(By.className("ui-datatable-tablewrapper"));
        WebElement tableContentHeader = tableContent.findElement(By.tagName("thead"));
        List<WebElement> tableContentHeaderColumns = tableContentHeader.findElements(By.className("checkbox-cell"));
        for(WebElement tableContentHeaderColumn : tableContentHeaderColumns)
        {
            if(tableContentHeaderColumn.findElement(By.className("ui-column-title")).getText().equals("U:"))
            {
                setUpHeaderParameter(tableContentHeaderColumn,teachingNumber);
            }
            if(tableContentHeaderColumn.findElement(By.className("ui-column-title")).getText().equals("G:"))
            {
                setUpHeaderParameter(tableContentHeaderColumn,generalizationNumber);
            }
        }

    }

    public void clickDeleteRow(String tableName, String rowsName) {
        WebElement tableRow = getRowForTable(tableName, rowsName, true);
        List<WebElement> tableRowColumns = tableRow.findElements(By.tagName("td"));
        tableRowColumns.get(3).findElement(By.tagName("button")).click();
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public void clickCopyTableButton(String tableName) {
        clickButtonForTable(tableName, Utils.getMsgFromResources("editProgramView.copyTable"), false);
    }

    public List<String> getTableRowsNamesForTable(String newTableName, Boolean isEditing) {
        WebElement table = getTableWithHeader(newTableName, isEditing);
        WebElement tableContent = table.findElement(By.className("ui-datatable-tablewrapper"));
        WebElement tableContentRowsArea = tableContent.findElement(By.tagName("tbody"));
        List<WebElement> tableRows = tableContentRowsArea.findElements(By.tagName("tr"));
        List<String> tableRowNames = new ArrayList<>();
        for(WebElement tableRow : tableRows){
            List<WebElement> tableRowColumns = tableRow.findElements(By.tagName("td"));
            if(isEditing){
                tableRowNames.add(tableRowColumns.get(0).findElement(By.tagName("input")).getAttribute("value"));
            }
            else {
                tableRowNames.add(tableRowColumns.get(0).getText());
            }
        }
        return tableRowNames;
    }



    private List<WebElement> getTablesList() {
        WebElement tableAreaContent = getTableArea();
        return tableAreaContent.findElements(By.className("ui-datagrid-row"));
    }

    private WebElement getTableWithHeader(String header, Boolean isEditing) {
        List<WebElement> tablesList = getTablesList();
        for(WebElement table : tablesList) {
            WebElement tableHeader = table.findElement(By.className("ui-datatable-header"));
            if(isEditing){
                WebElement tableHeaderInput = tableHeader.findElement(By.tagName("input"));
                if(tableHeaderInput.getAttribute("value").equals(header))
                    return  table;
            }
            else if(tableHeader.getText().equals(header))
                return table;
        }
        return null;
    }

    private void setUpHeaderParameter(WebElement tableContentHeaderColumnsElement, int number ){
        tableContentHeaderColumnsElement.findElement(By.tagName("input")).sendKeys(String.valueOf(number));
    }

    private WebElement getRowForTable(String tableName, String rowName, Boolean isEditing)
    {
        WebElement table = getTableWithHeader(tableName, isEditing);
        WebElement tableContent = table.findElement(By.className("ui-datatable-tablewrapper"));
        WebElement tableContentRowsArea = tableContent.findElement(By.tagName("tbody"));
        List<WebElement> tableRows = tableContentRowsArea.findElements(By.tagName("tr"));
        for(WebElement tableRow : tableRows)
        {
            List<WebElement> tableRowColumns = tableRow.findElements(By.tagName("td"));

            if(isEditing){
                if( tableRowColumns.get(0).findElement(By.tagName("input")).getAttribute("value").equals(rowName)){
                    return tableRow;
                }
            }
            else {
                if( tableRowColumns.get(0).getText().equals(rowName)){
                    return tableRow;
                }
            }

        }
        return null;
    }

    public void clickDeleteButton(String tableName) {
        clickButtonForTable(tableName,  Utils.getMsgFromResources("main.delete"), false);
    }
}
