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

package com.mroza.seleniumTests.KidsViewTests;
import com.mroza.utils.SeleniumWaiter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;

public class KidsViewPage {

    protected WebDriver driver;
    private WebElement header;

    public KidsViewPage(WebDriver driver) {
        this.driver = driver;
    }
    public void open(String url) {
        driver.get(url);
    }
    public void close() {
        driver.quit();
    }
    public String getHeader() {
        header = driver.findElement(By.className("b-page-header"));
        return header.getText();
    }


    public List<String> getKidsSymbolsList() {
        WebElement tableContent = driver.findElement(By.className("ui-datatable-tablewrapper"));
        WebElement tableBody = tableContent.findElement(By.tagName("tbody"));
        List<WebElement> tableRows = tableBody.findElements(By.tagName("tr"));
        List<String> kidsSymbolsList = new ArrayList<>();
        for(WebElement tableRow : tableRows)
        {
            WebElement symbolElement = tableRow.findElement(By.tagName("td"));
            String kidSymbol = symbolElement.getText();
            kidsSymbolsList.add(kidSymbol);
        }

        return kidsSymbolsList;
    }

    public void setSearchValue(String expectedSearchedSymbol) {
        WebElement searchboxArea = driver.findElement(By.className("ui-datatable-header"));
        WebElement searchBoxInput = searchboxArea.findElement(By.tagName("input"));
        searchBoxInput.sendKeys(expectedSearchedSymbol);
        SeleniumWaiter.waitForJQueryAndPrimeFaces(driver);
    }

}
