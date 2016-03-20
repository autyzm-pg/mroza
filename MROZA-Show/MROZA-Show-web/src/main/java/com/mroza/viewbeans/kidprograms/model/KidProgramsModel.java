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

import com.mroza.interfaces.KidPeriodsService;
import com.mroza.models.*;
import com.mroza.viewbeans.utils.FilterableList;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class KidProgramsModel implements Serializable {

    @Getter
    @Setter
    private Kid kid;

    @Getter
    @Setter
    private int kidId;

    @Getter
    @Setter
    private int activeTab;

    // Schedule
    @Getter
    @Setter
    private ScheduleModel scheduleModel;

    @Getter
    private DefaultScheduleEvent selectedCalendarRepresentationOfPeriod;

    @Getter
    @Setter
    private Period newPeriod;

    // Data tables with filters
    @Getter
    @Setter
    private FilterableList<Program> assignedPrograms;

    @Getter
    @Setter
    private FilterableList<Program> unassignedPrograms;

    @Getter
    @Setter
    private FilterableList<Program> programsWithCollectedData;

    @Getter
    @Setter
    private FilterableList<KidTable> kidTablesForPeriod;

    @Getter
    @Setter
    private FilterableList<Program> programsNotInPeriod;

    @Getter
    @Setter
    private List<Table> tablesInProgram;

    private KidPeriodsService kidPeriodsService;


    public KidProgramsModel() {
        scheduleModel = new DefaultScheduleModel();
    }

    public KidProgramsModel(KidPeriodsService kidPeriodsService, Kid kid, List<Program> assignedPrograms, List<Program> programsWithCollectedData) {
        kidId = kid.getId();
        scheduleModel = new DefaultScheduleModel();
        selectedCalendarRepresentationOfPeriod = null;
        newPeriod = new Period(new Date(), new Date());
        unassignedPrograms = new FilterableList<>(new ArrayList<>());
        kidTablesForPeriod = new FilterableList<>(new ArrayList<>());
        programsNotInPeriod = new FilterableList<>(new ArrayList<>());
        tablesInProgram = new ArrayList<>();

        this.kidPeriodsService = kidPeriodsService;
        this.kid = kid;
        this.assignedPrograms = new FilterableList<>(assignedPrograms);
        this.programsWithCollectedData = new FilterableList<>(programsWithCollectedData);

        parsePeriodsToScheduleEvents();
        refreshProgramsForSelectedPeriod();
    }



    public void notifyNewPeriodSaved() {
        DefaultScheduleEvent newCalendarRepresentationOfPeriod = ScheduldeEventWrapper.calendarRepresentationFromPeriod(newPeriod);
        scheduleModel.addEvent(newCalendarRepresentationOfPeriod);
        setSelectedCalendarRepresentationOfPeriod(newCalendarRepresentationOfPeriod);
        refreshProgramsForSelectedPeriod();
    }

    public void notifyProgramAssignedToKid(Program program) {
        kid.getPrograms().add(program);
        assignedPrograms.add(program);
        unassignedPrograms.remove(program);
    }

    public void notifyKidTableDeleted(KidTable kidTable) {
        kidTablesForPeriod.remove(kidTable);
    }

    public void notifyCurrentlySelectedPeriodDeleted() {
        scheduleModel.deleteEvent(selectedCalendarRepresentationOfPeriod);
        kidTablesForPeriod = new FilterableList<>(new ArrayList<>());
        programsNotInPeriod = new FilterableList<>(new ArrayList<>());
        setSelectedCalendarRepresentationOfPeriod(null);
    }

    public Period getCurrentSelectedPeriod() {
        if(selectedCalendarRepresentationOfPeriod == null)
            return null;
        return ScheduldeEventWrapper.extractPeriodFromCalendarRepresentation(selectedCalendarRepresentationOfPeriod);
    }

    public List<Period> getAllPeriods() {
        return scheduleModel.getEvents().stream().map(
                ScheduldeEventWrapper::extractPeriodFromCalendarRepresentation
        ).collect(Collectors.toList());
    }

    public void refreshProgramsForSelectedPeriod() {
        if(selectedCalendarRepresentationOfPeriod != null) {
            Period currentlySelectedPeriod = getCurrentSelectedPeriod();
            kidTablesForPeriod = new FilterableList<>(kidPeriodsService.getKidTablesWithTableAndProgramByPeriodId(currentlySelectedPeriod.getId()));
        }
        else
            kidTablesForPeriod = new FilterableList<>();
    }

    public void setSelectedCalendarRepresentationOfPeriod(DefaultScheduleEvent event) {
        if(selectedCalendarRepresentationOfPeriod != null) {
            selectedCalendarRepresentationOfPeriod.setStyleClass("unselected");
        }
        if(event != null) {
            event.setStyleClass("selected");
        }
        selectedCalendarRepresentationOfPeriod = event;

    }

    private void parsePeriodsToScheduleEvents() {
        List<Period> periods = kid.getPeriods();
        if(periods != null && !periods.isEmpty()) {
            Date today = new Date();
            for(Period period : periods) {
                DefaultScheduleEvent calendarRepresentationOfPeriod = ScheduldeEventWrapper.calendarRepresentationFromPeriod(period);
                scheduleModel.addEvent(calendarRepresentationOfPeriod);

                if(today.after(period.getBeginDate()) && today.before(period.getEndDate()))
                    setSelectedCalendarRepresentationOfPeriod(calendarRepresentationOfPeriod);
            }
        }
    }
}
