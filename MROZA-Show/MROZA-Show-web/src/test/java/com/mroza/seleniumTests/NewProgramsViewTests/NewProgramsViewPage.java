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

import com.mroza.utils.SeleniumWaiter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


public class NewProgramsViewPage {

    protected WebDriver driver;
    public NewProgramsViewPage(WebDriver driver) {
        this.driver = driver;
    }
    public void open(String url) {
        driver.get(url);
    }
    public void close() {
        driver.quit();
    }

    public void clickSaveNewProgram() {
        clickButtonNamed("Zapisz");
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public void setAddProgramFields(String symbol, String name, String description) {
        WebElement symbolInputField = getInputFieldNamed("Symbol");
        symbolInputField.sendKeys(symbol);
        WebElement nameInputField = getInputFieldNamed("Nazwa");
        nameInputField.sendKeys(name);
        WebElement descriptionInputField = getTextAreaNamed("Opis");
        descriptionInputField.sendKeys(description);
    }

    public void clickCancelNewProgram() {
        clickButtonNamed("Anuluj");
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public String getHeader() {
        WebElement header = driver.findElement(By.className("b-page-header"));
        return header.getText();
    }

    public String getShowedMessage() {
        try {
            WebElement message = driver.findElement(By.className("ui-messages-error-summary"));
            return message.getText();
        }
        catch (Exception e)
        {
            return "NO MESSAGE SHOWN";
        }
    }

    public List<String> getShowedMessages() {
        try {
            List<WebElement> messageBoxes = driver.findElements(By.className("ui-messages-error-summary"));
            List<String> messages = new ArrayList<>();
            for(WebElement messageBox : messageBoxes)
                messages.add(messageBox.getText());
            return messages;
        }
        catch (Exception e)
        {
            return new ArrayList<String>(){{add("NO MESSAGE SHOWN");}};
        }
    }

    public String getSymbol() {
        WebElement symbolInputField = getInputFieldNamed("Symbol");
        return symbolInputField.getAttribute("value");
    }

    public String getName() {
        WebElement nameInputField = getInputFieldNamed("Nazwa");
        return nameInputField.getAttribute("value");

    }

    public String getDescription() {
        WebElement descriptionInputField = getTextAreaNamed("Opis");
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

    private void clickButtonNamed(String name)
    {
        WebElement buttonsArea = driver.findElement(By.className("action-buttons-container"));
        List<WebElement> buttons = buttonsArea.findElements(By.tagName("button"));
        for(WebElement button : buttons){
            if(button.findElement(By.tagName("span")).getText().equals(name)) {
                button.click();
                break;
            }
        }

    }
}
