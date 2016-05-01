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
package com.mroza.seleniumTests;

import com.mroza.utils.SeleniumWaiter;
import com.mroza.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

public class MrozaViewPage {

    protected WebDriver driver;

    public MrozaViewPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getHeader() {
        WebElement header = driver.findElement(By.className("b-page-header"));
        return header.getText();
    }

    public String getErrorMessage() {
        try {
            WebElement errorMessage = driver.findElement(By.className("ui-messages-error-summary"));
            return errorMessage.getText();
        }
        catch (Exception ex)
        {
            return "NOT MESSAGE HAS BEEN SHOWN";
        }
    }

    public List<String> getErrorMessages() {
        try {
            List<WebElement> messageBoxes = driver.findElements(By.className("ui-messages-error-summary"));
            List<String> messages = new ArrayList<>();
            messageBoxes.forEach((messageBox)->messages.add(messageBox.getText()));
            return messages;
        }
        catch (Exception e)
        {
            return new ArrayList<String>(){{add("NOT MESSAGE HAS BEEN SHOWN");}};
        }
    }

    protected void clickButtonInButtonContainerNamed(String buttonName){
        WebElement buttonsArea = driver.findElement(By.className("action-buttons-container"));
        clickButtonIn(buttonName, buttonsArea);

    }

    protected void clickButtonInButtonContainerNamed(String buttonName, WebElement parentElement) {
        WebElement buttonsArea = parentElement.findElement(By.className("action-buttons-container"));
        clickButtonIn(buttonName, buttonsArea);
    }

    protected void clickButtonIn(String buttonName, WebElement parentElement) {
        for(WebElement button : parentElement.findElements(By.tagName("button"))){
            if(button.findElement(By.tagName("span")).getText().equals(buttonName)) {
                button.click();
                SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
                break;
            }
        }
    }

    public void clickYesButtonInDialogBox() {
        clickDialogButton(Utils.getMsgFromResources("main.yes"));
    }

    protected void clickDialogButton(String buttonName) {
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

    protected WebElement getVisibleDialogBox() {
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
