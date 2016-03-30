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
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.SeleniumUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class KidsViewKidsListTest {

    private KidsViewPage page;
    private List<String> expectedSymbols;

    @Before
    public void setUp() {
        SeleniumUtils.setUpDriverConnection();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        setUpKids(databaseUtils);
        page = PageFactory.initElements(new ChromeDriver(), KidsViewPage.class);
        page.open(SeleniumUtils.kidsViewUrl);
    }

    @After
    public void tearDown() {
        page.close();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void showExpectedKidsSymbolsOnListTest() {
        List<String> kidsSymbols = page.getKidsSymbolsList();

        assertEquals("KidsSymbols list should have the same size as expected", expectedSymbols.size(),kidsSymbols.size());
        Boolean found;
        for(String expectedSymbol : expectedSymbols)
        {
                found = false;
                for(String kidsSymbol : kidsSymbols)
                {
                    if(expectedSymbol.equals(kidsSymbol))
                        found = true;
                }
                if(!found)
                {
                    fail("KidSymbol: "+expectedSymbol+" is not on the list.");
                }
        }
    }

    @Test
    public void showExpectedKidsSymbolsOnListAfterSearchTest() {
        String expectedSearchedSymbol = expectedSymbols.get(0);
        page.setSearchValue(expectedSearchedSymbol);
        List<String> kidsSymbols = page.getKidsSymbolsList();

        assertEquals("KidsSymbols list should have the same size as expected", 1 ,kidsSymbols.size());
        assertEquals("SearchedSymbol should be the same ad ExpectedSearchSymbol",expectedSearchedSymbol ,kidsSymbols.get(0));
    }


    private void setUpKids(DatabaseUtils databaseUtils) {
        expectedSymbols = new ArrayList<String>() {{
            add("Symbol1");
            add("Symbol2");
            add("Symbol3");
        }};
        expectedSymbols.forEach(databaseUtils::setUpKid);
    }
}
