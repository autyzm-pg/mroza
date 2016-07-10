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


import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MrozaPageFactory extends PageFactory {

    static final String CI_ENV_NAME = "CI";
    static final String TRUE_ENV_VAR_VALUE = "true";

    public static <T> T initElements(Class<T> pageClassToProxy) {
        if (isCIBuild()) {
            String sauceLabsUsername = "";
            String sauceLabsAccessKey = "";
            String hubURL = String.format("http://%s:%s@localhost:4445/wd/hub", sauceLabsUsername, sauceLabsAccessKey);
            Map<String, String> desiredCapabilities = new HashMap<>();
            String travisJobNumber = getEnvVarValue("TRAVIS_JOB_NUMBER");
            desiredCapabilities.put("tunnel-identifier", travisJobNumber);

            try {
                return PageFactory.initElements(new RemoteWebDriver(new URL(hubURL), new DesiredCapabilities(desiredCapabilities)), pageClassToProxy);
            } catch (Exception error) {
                System.out.printf("An error occurred: %s", error.getMessage());
                return null;
            }
        } else {
            SeleniumUtils.setUpDriverConnection();
            return PageFactory.initElements(new ChromeDriver(), pageClassToProxy);
        }
    }

    public static boolean isCIBuild() {
        return (TRUE_ENV_VAR_VALUE.equals(getEnvVarValue(CI_ENV_NAME)));
    }

    private static String getEnvVarValue(String variableName) {
        Map<String, String> environmentVars = System.getenv();
        return environmentVars.get(variableName);
    }

}
