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

package com.mroza.seleniumTests.NewProgramsViewTests;

import com.mroza.seleniumTests.MrozaViewPage;
import com.mroza.utils.SeleniumWaiter;
import com.mroza.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;


public class NewProgramsViewPage extends MrozaViewPage {

    public NewProgramsViewPage(WebDriver driver) {
        super(driver);
    }
    public void open(String url) {
        driver.get(url);
    }
    public void close() {
        driver.quit();
    }

    public void clickSaveNewProgram() {
        clickButtonInButtonContainerNamed(Utils.getMsgFromResources("main.save"));
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public void setAddProgramFields(String symbol, String name, String description) {
        WebElement symbolInputField = getInputFieldNamed(Utils.getMsgFromResources("newProgramsView.symbol"));
        symbolInputField.clear();
        symbolInputField.sendKeys(symbol);
        WebElement nameInputField = getInputFieldNamed(Utils.getMsgFromResources("newProgramsView.name"));
        nameInputField.clear();
        nameInputField.sendKeys(name);
        WebElement descriptionInputField = getTextAreaNamed(Utils.getMsgFromResources("newProgramsView.description"));
        descriptionInputField.clear();
        descriptionInputField.sendKeys(description);
    }

    public void clickCancelNewProgram() {
        clickButtonInButtonContainerNamed(Utils.getMsgFromResources("main.cancel"));
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public String getSymbol() {
        WebElement symbolInputField = getInputFieldNamed(Utils.getMsgFromResources("newProgramsView.symbol"));
        return symbolInputField.getAttribute("value");
    }

    public String getName() {
        WebElement nameInputField = getInputFieldNamed(Utils.getMsgFromResources("newProgramsView.name"));
        return nameInputField.getAttribute("value");

    }

    public String getDescription() {
        WebElement descriptionInputField = getTextAreaNamed(Utils.getMsgFromResources("newProgramsView.description"));
        return descriptionInputField.getText();
    }

    private WebElement getInputFieldNamed(String inputName){
        WebElement tableWithInputFields = driver.findElement(By.tagName("table"));
        WebElement tableBody = tableWithInputFields.findElement(By.tagName("tbody"));
        List<WebElement> inputRows = tableBody.findElements(By.tagName("tr"));
        for(WebElement inputRow : inputRows){
            if(inputRow.findElements(By.tagName("td")).get(0).getText().equals(inputName)){
                return inputRow.findElements(By.tagName("td")).get(1).findElement(By.tagName("input"));
            }
        }
        return null;
    }

    private WebElement getTextAreaNamed(String inputName){
        WebElement tableWithInputFields = driver.findElement(By.tagName("table"));
        WebElement tableBody = tableWithInputFields.findElement(By.tagName("tbody"));
        List<WebElement> inputRows = tableBody.findElements(By.tagName("tr"));
        for(WebElement inputRow : inputRows){
            if(inputRow.findElements(By.tagName("td")).get(0).getText().equals(inputName)){
                return inputRow.findElements(By.tagName("td")).get(1).findElement(By.tagName("textArea"));
            }
        }
        return null;
    }


}
