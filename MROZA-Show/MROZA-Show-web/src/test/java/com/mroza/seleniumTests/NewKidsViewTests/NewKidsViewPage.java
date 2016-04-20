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
package com.mroza.seleniumTests.NewKidsViewTests;

import com.mroza.seleniumTests.MrozaViewPage;
import com.mroza.utils.SeleniumWaiter;
import com.mroza.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class NewKidsViewPage extends MrozaViewPage{

    public NewKidsViewPage(WebDriver driver) {
        super(driver);
    }
    public void open(String url) {
        driver.get(url);
    }
    public void close() {
        driver.quit();
    }

    public void setKidCode(String code) {
        WebElement table = driver.findElement(By.tagName("table"));
        List<WebElement> columns = table.findElements(By.tagName("tr")).get(0).findElements(By.tagName("td"));
        WebElement searchBoxInput = columns.get(1).findElement(By.tagName("input"));
        searchBoxInput.sendKeys(code);
    }

    public void clickSaveNewKid() {
        clickButtonInButtonContainerNamed(Utils.getMsgFromResources("main.save"));
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

    public void clickCancelNewKid() {
        clickButtonInButtonContainerNamed(Utils.getMsgFromResources("main.cancel"));
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

}
