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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class KidProgramsViewPage {

    protected WebDriver driver;
    private WebElement header;

    public KidProgramsViewPage(WebDriver driver) {
        this.driver = driver;
    }
    public void open(String url, Kid kid) {
        driver.get(url + kid.getId() + "&reloadData=true");
    }
    public void close() {
        driver.quit();
    }
    public String getHeader() {
        header = driver.findElement(By.className("b-page-header"));
        return header.getText();
    }

    public void setSearchValue(String symbol) {
        WebElement searchBoxInput = driver.findElement(By.className("ui-inputtext"));
        searchBoxInput.sendKeys(symbol);
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public List<String> getProgramsSymbolsList()
    {
        return getColumnValues(0);
    }

    private WebElement getDataTableElement() {
        return driver.findElement(By.className("ui-datatable"));
    }

    private List<WebElement> getProgramElementsList(WebElement dataTable) {
        WebElement dataTableContent =  dataTable.findElement(By.className("ui-datatable-tablewrapper"));
        WebElement dataTableContentList = dataTableContent.findElement(By.className("ui-datatable-data"));
        List<WebElement> programElementsList = dataTableContentList.findElements(By.tagName("tr"));
        return programElementsList;
    }

    public List<String> getProgramNamesList() {
        return getColumnValues(1);
    }

    private List<String> getColumnValues(int columnNumber) {
        WebElement dataTable = getDataTableElement();
        List<WebElement> programElements = getProgramElementsList(dataTable);
        List<String> programSymbolElementsList = new ArrayList<>();

        for(WebElement programElement : programElements)
        {
            List<WebElement> programElementColumn = programElement.findElements(By.tagName("td"));
            programSymbolElementsList.add(programElementColumn.get(columnNumber).getText());
        }
        return  programSymbolElementsList;
    }

    public void clickLetterFilter(String buttonValue) {
        WebElement buttonsArea = driver.findElement(By.className("ui-selectonebutton"));
        List<WebElement> letterButtons = buttonsArea.findElements(By.className("ui-button"));
        for(WebElement letterButton : letterButtons ){
            String buttonTextValue = letterButton.findElement(By.className("ui-button-text")).getText();
            if(buttonTextValue.equals(buttonValue)){
                letterButton.click();
                break;
            }
        }
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }
}
