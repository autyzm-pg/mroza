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

import com.mroza.seleniumTests.MrozaViewPage;
import com.mroza.utils.SeleniumWaiter;
import com.mroza.utils.Utils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

public class ProgramDirectoryViewPage extends MrozaViewPage {

    public ProgramDirectoryViewPage(WebDriver driver) {
        super(driver);
    }

    public void open(String url) {
        driver.get(url);
    }
    public void close() {
        driver.quit();
    }

    public List<String> getAllProgramsSymbols() {
        List<WebElement> tableRows = getTableRows();
        List<String> programSymbolsList = new ArrayList<>();
        for(WebElement tableRow : tableRows)
        {
            List<WebElement> symbolsElement = tableRow.findElements(By.tagName("td"));
            WebElement symbolElement = symbolsElement.get(0);
            String programSymbol = symbolElement.getText();
            programSymbolsList.add(programSymbol);
        }
        return  programSymbolsList;
    }

    public void setSearchValue(String expectedSearchedSymbol) {
        WebElement searchBoxInput = driver.findElement(By.className("ui-outputpanel"));
        searchBoxInput.findElement(By.tagName("input")).sendKeys(expectedSearchedSymbol);
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public void setLetterFilterValue(String buttonValue) {
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

    public void clickDeleteProgramButtonForProgramWithSymbol(String programSymbol) {
        List<WebElement> tableRows = getTableRows();
        for(WebElement tableRow : tableRows)
        {
            List<WebElement> columns = tableRow.findElements(By.tagName("td"));
            if(columns.get(0).getText().equals(programSymbol)){
                WebElement deleteButton = columns.get(3).findElement(By.tagName("button"));
                deleteButton.click();
            }
        }
    }

    private List<WebElement> getTableRows() {
        WebElement tableContent = driver.findElement(By.tagName("table"));
        WebElement tableBody = tableContent.findElement(By.tagName("tbody"));
        return tableBody.findElements(By.tagName("tr"));
    }


}
