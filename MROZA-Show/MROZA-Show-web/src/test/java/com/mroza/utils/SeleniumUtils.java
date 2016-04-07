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

package com.mroza.utils;
import java.io.InputStream;
import java.util.Properties;

public class SeleniumUtils {

    public static final String kidsViewUrl = "http://localhost:8080/web/faces/kidsView.xhtml";
    public static final String newKidsViewUrl = "http://localhost:8080/web/faces/newKidsView.xhtml";
    public static final String programDirectoryViewPageUrl = "http://localhost:8080/web/faces/programsDirectoryView.xhtml";
    public static final String newProgramsViewUrl = "http://localhost:8080/web/faces/newProgramsView.xhtml";
    public static final String kidProgramsViewUrl = "http://localhost:8080/web/faces/kidProgramsView/kidProgramsView.xhtml?kidId=";


    public static void setUpDriverConnection()
    {
        try {
            System.setProperty("webdriver.chrome.driver", getPropertyValue("chrome-web-driver-path"));
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
    }

    private static String getPropertyValue(String propertyName) throws Exception {

        Properties properties = new Properties();
        InputStream inputStream = SeleniumUtils.class.getClassLoader().getResourceAsStream("selenium_config.properties");


        if (inputStream != null) {
            properties.load(inputStream);
            return properties.getProperty(propertyName);
        } else {
            throw new Exception("Selenium tests configuration file was not found. " +
                    "Create file selenium_config.properties in directory test/resources " +
                    "and set in it value for key chrome-web-driver-path (your path to driver).");
        }
    }



}
