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

package com.mroza.viewbeans.utils;

import com.mroza.models.Period;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.util.Date;

public class ScheduleDatesValidator {

    public static boolean arePeriodDatesValid(Period period, ScheduleModel scheduleModel, DefaultScheduleEvent selectedCalendarRepresentationOfPeriod) {
        Date beginDate = period.getBeginDate();
        Date endDate = period.getEndDate();
        scheduleModel.getEvents().remove(selectedCalendarRepresentationOfPeriod);
        boolean result = arePeriodDatesValid(beginDate, endDate, scheduleModel);
        scheduleModel.addEvent(selectedCalendarRepresentationOfPeriod);
        return result;
    }

    public static boolean arePeriodDatesValid(Period period, ScheduleModel scheduleModel) {
        Date beginDate = period.getBeginDate();
        Date endDate = period.getEndDate();
        return arePeriodDatesValid(beginDate, endDate, scheduleModel);
    }

    public static boolean arePeriodDatesValid(DefaultScheduleEvent event, ScheduleModel scheduleModel) {
        return !(isIntersectingWithPeriods(event, scheduleModel)
                || event.getStartDate().after(event.getEndDate()));
    }

    private static boolean arePeriodDatesValid(Date beginDate, Date endDate, ScheduleModel scheduleModel) {
        return !(isIntersectingWithPeriods(beginDate, endDate, scheduleModel)
                || beginDate.after(endDate));
    }

    private static boolean isIntersectingWithPeriods(DefaultScheduleEvent event, ScheduleModel scheduleModel) {
        return scheduleModel.getEvents().stream().anyMatch(existingEvent ->
                (isDateBetweenOrEquals(existingEvent.getStartDate(), existingEvent.getEndDate(), event.getStartDate())
                        || isDateBetweenOrEquals(existingEvent.getStartDate(), existingEvent.getEndDate(), event.getEndDate())
                        || (event.getStartDate().before(existingEvent.getStartDate()) && event.getEndDate().after(existingEvent.getEndDate()))
                ) && (event != existingEvent)
        );
    }

    private static boolean isIntersectingWithPeriods(Date beginDate, Date endDate, ScheduleModel scheduleModel) {
        return scheduleModel.getEvents().stream().anyMatch(event ->
                        isDateBetweenOrEquals(event.getStartDate(), event.getEndDate(), beginDate)
                                || isDateBetweenOrEquals(event.getStartDate(), event.getEndDate(), endDate)
                                || (beginDate.before(event.getStartDate()) && endDate.after(event.getEndDate()))

        );
    }

    private static boolean isDateBetweenOrEquals(Date startDate, Date endDate, Date testingDate) {
        return (testingDate.after(startDate) && testingDate.before(endDate))
                || testingDate.equals(startDate)
                || testingDate.equals(endDate);
    }
}
