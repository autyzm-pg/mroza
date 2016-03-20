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

package com.mroza;

import com.mroza.models.Period;
import com.mroza.utils.Utils;
import com.mroza.viewbeans.utils.ScheduleDatesValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import java.util.Date;

public class ScheduleTestValidatorTest {

    private ScheduleModel scheduleModel;

    @Before
    public void setup() {
        Date[] exampleBegin = new Date[]{Utils.strToDate("10-10-2014", "dd-MM-yyyy"), Utils.strToDate("10-11-2014", "dd-MM-yyyy")};
        Date[] exampleEnd = new Date[]{Utils.strToDate("20-10-2014", "dd-MM-yyyy"), Utils.strToDate("20-11-2014", "dd-MM-yyyy")};
        scheduleModel = new DefaultScheduleModel();
        scheduleModel.addEvent(new DefaultScheduleEvent("", exampleBegin[0], exampleEnd[0], new Period(exampleBegin[0], exampleEnd[0])));
        scheduleModel.addEvent(new DefaultScheduleEvent("", exampleBegin[1], exampleEnd[1], new Period(exampleBegin[1], exampleEnd[1])));
    }

    @Test
    public void isIntersectingWithPeriodsNotIntersectTest() {
        DefaultScheduleEvent event = new DefaultScheduleEvent("",
                Utils.strToDate("21-10-2014", "dd-MM-yyyy"), Utils.strToDate("23-10-2014", "dd-MM-yyyy"),
                new Period(Utils.strToDate("21-10-2014", "dd-MM-yyyy"), Utils.strToDate("23-10-2014", "dd-MM-yyyy")));

        boolean isIntersection = ScheduleDatesValidator.arePeriodDatesValid(event, scheduleModel);
        Assert.assertTrue(isIntersection);
    }

    @Test
    public void isIntersectingWithPeriodsStartBetweenTest() {
        DefaultScheduleEvent event = new DefaultScheduleEvent("",
                Utils.strToDate("15-10-2014", "dd-MM-yyyy"), Utils.strToDate("23-10-2014", "dd-MM-yyyy"),
                new Period(Utils.strToDate("15-10-2014", "dd-MM-yyyy"), Utils.strToDate("23-10-2014", "dd-MM-yyyy")));

        boolean isIntersection = ScheduleDatesValidator.arePeriodDatesValid(event, scheduleModel);
        Assert.assertFalse(isIntersection);
    }

    @Test
    public void isIntersectingWithPeriodsStartEqualsTest() {
        DefaultScheduleEvent event = new DefaultScheduleEvent("",
                Utils.strToDate("20-11-2014", "dd-MM-yyyy"), Utils.strToDate("30-11-2014", "dd-MM-yyyy"),
                new Period(Utils.strToDate("20-11-2014", "dd-MM-yyyy"), Utils.strToDate("30-11-2014", "dd-MM-yyyy")));

        boolean isIntersection = ScheduleDatesValidator.arePeriodDatesValid(event, scheduleModel);
        Assert.assertFalse(isIntersection);
    }

    @Test
    public void isIntersectingWithPeriodsEndBetweenTest() {
        DefaultScheduleEvent event = new DefaultScheduleEvent("",
                Utils.strToDate("5-11-2014", "dd-MM-yyyy"), Utils.strToDate("12-11-2014", "dd-MM-yyyy"),
                new Period(Utils.strToDate("5-11-2014", "dd-MM-yyyy"), Utils.strToDate("12-11-2014", "dd-MM-yyyy")));

        boolean isIntersection = ScheduleDatesValidator.arePeriodDatesValid(event, scheduleModel);
        Assert.assertFalse(isIntersection);
    }

    @Test
    public void isIntersectingWithPeriodsBeforeAndAfterTest() {
        DefaultScheduleEvent event = new DefaultScheduleEvent("",
                Utils.strToDate("1-10-2014", "dd-MM-yyyy"), Utils.strToDate("22-10-2014", "dd-MM-yyyy"),
                new Period(Utils.strToDate("1-10-2014", "dd-MM-yyyy"), Utils.strToDate("22-10-2014", "dd-MM-yyyy")));

        boolean isIntersection = ScheduleDatesValidator.arePeriodDatesValid(event, scheduleModel);
        Assert.assertFalse(isIntersection);
    }
}
