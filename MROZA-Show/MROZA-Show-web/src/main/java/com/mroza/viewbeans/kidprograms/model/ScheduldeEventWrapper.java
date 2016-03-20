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

package com.mroza.viewbeans.kidprograms.model;

import com.mroza.models.Period;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduldeEventWrapper {

    public static DefaultScheduleEvent calendarRepresentationFromPeriod(Period period) {
        applyHackToHandlePrimefacesComponentWayHandlingCalendarDays(period);
        String eventTitle = getCalendarRepresentationTitleFromPeriod(period);
        return new DefaultScheduleEvent(eventTitle, period.getBeginDate(), period.getEndDate(), period);
    }

    public static Period extractPeriodFromCalendarRepresentation(DefaultScheduleEvent calendarRepresentation) {
        return (Period) calendarRepresentation.getData();
    }

    public static Period extractPeriodFromCalendarRepresentation(ScheduleEvent calendarRepresentation) {
        return (Period) calendarRepresentation.getData();
    }

    public static void syncPeriodDataWithDataFromCalendarRepresentation(DefaultScheduleEvent calendarRepresentation) {
        Period period = (Period) calendarRepresentation.getData();
        period.setBeginDate(calendarRepresentation.getStartDate());
        period.setEndDate(calendarRepresentation.getEndDate());
    }

    public static void syncCalendarRepresentationDataWithDataFromPeriod(DefaultScheduleEvent calendarRepresentation) {
        Period period = (Period) calendarRepresentation.getData();
        applyHackToHandlePrimefacesComponentWayHandlingCalendarDays(period);
        calendarRepresentation.setStartDate(period.getBeginDate());
        calendarRepresentation.setEndDate(period.getEndDate());
        calendarRepresentation.setTitle(getCalendarRepresentationTitleFromPeriod(period));
    }

    private static String getCalendarRepresentationTitleFromPeriod(Period period) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(period.getBeginDate()) + " - " + dateFormat.format(period.getEndDate());
    }

    private static void applyHackToHandlePrimefacesComponentWayHandlingCalendarDays(Period period) {
        period.getEndDate().setHours(12); // primefaces schedule component doesn't show event on last day in case that hour is 0:00
    }





}
