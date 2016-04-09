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
import com.mroza.utils.SeleniumWaiter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class EditProgramViewPage {

    private final WebDriver driver;

    public EditProgramViewPage(WebDriver driver) {
        this.driver = driver;
    }
    public void open(String url, Program program) {
        driver.get(url + program.getId());
    }
    public void close() {
        driver.quit();
    }

    public String getHeader() {
        WebElement header = driver.findElement(By.className("b-page-header"));
        return header.getText();
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
        clickButtonNamed("Dodaj tabelkę");
    }

    private void clickButtonNamed(String buttonName) {
        WebElement buttonsArea = driver.findElement(By.className("action-buttons-container"));
        List<WebElement> buttonsList = buttonsArea.findElements(By.tagName("button"));

        for(WebElement button : buttonsList)
        {
            if(button.findElement(By.tagName("span")).getText().equals(buttonName))
            {
                button.click();
                SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
                break;
            }

        }
    }

    public void inputTableName(String tableName) {
        WebElement table = getTableWithHeader("");
        WebElement tableHeader = table.findElement(By.className("ui-datatable-header"));
        tableHeader.findElement(By.tagName("input")).sendKeys(tableName);
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    private List<WebElement> getTablesList() {
        WebElement tableAreaContent = getTableArea();
        return tableAreaContent.findElements(By.className("ui-datagrid-row"));
    }

    public void clickAddNewRowButtonForTable(String tableName) {
        clickButtonForTable(tableName, "Dodaj wiersz");
    }

    private void clickButtonForTable(String tableName, String buttonName) {
        WebElement table = getTableWithHeader(tableName);
        List<WebElement> tableButtons = table.findElements(By.tagName("button"));
        for(WebElement button : tableButtons)
            if(button.findElement(By.tagName("span")).getText().equals(buttonName)) {
                button.click();
                SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
                break;
            }
    }

    private WebElement getTableWithHeader(String header) {
        List<WebElement> tablesList = getTablesList();
        for(WebElement table : tablesList) {
            WebElement tableHeader = table.findElement(By.className("ui-datatable-header"));
            WebElement tableHeaderInput = tableHeader.findElement(By.tagName("input"));
            if(tableHeaderInput.getAttribute("value").equals(header))
            {
                return  table;
            }
        }
        return null;
    }

    public void addTableRow(String tableName, String row_name) {
        WebElement table = getTableWithHeader(tableName);
        WebElement tableContent = table.findElement(By.className("ui-datatable-tablewrapper"));
        WebElement tableContentRowsArea = tableContent.findElement(By.tagName("tbody"));
        List<WebElement> tableRow = tableContentRowsArea.findElements(By.tagName("tr"));
        List<WebElement> tableRowColumns = tableRow.get(0).findElements(By.tagName("td"));
        tableRowColumns.get(0).findElement(By.tagName("input")).sendKeys(row_name);
    }

    public void setUpTableContentHeader(String tableName, int teachingNumber, int generalizationNumber){
        WebElement table = getTableWithHeader(tableName);
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

    private void setUpHeaderParameter(WebElement tableContentHeaderColumnsElement, int number ){
        tableContentHeaderColumnsElement.findElement(By.tagName("input")).sendKeys(String.valueOf(number));
    }

    public void clickSaveTableButton(String tableName) {
        clickButtonForTable(tableName, "Zapisz");
    }
}
