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

package com.mroza.viewbeans.kidprograms.beans;

import com.mroza.interfaces.*;
import com.mroza.models.*;
import com.mroza.qualifiers.*;
import com.mroza.viewbeans.kidprograms.model.KidProgramsModel;
import com.mroza.viewbeans.kidprograms.model.ScheduldeEventWrapper;
import com.mroza.viewbeans.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@SessionScoped
@Named
public class KidProgramsBean implements Serializable {

    @Inject
    @KidsServiceImpl
    private KidsService kidsService;

    @Inject
    @ProgramsServiceImpl
    private ProgramsService programsService;

    @Inject
    @KidProgramsServiceImpl
    private KidProgramsService kidProgramsService;

    @Inject
    @KidPeriodsServiceImpl
    private KidPeriodsService kidPeriodsService;

    @Getter
    @Setter
    private boolean reloadData;

    @Getter
    @Setter
    private KidProgramsModel model;

    public KidProgramsBean() {
        model = new KidProgramsModel();
    }

    @PostConstruct
    public void init() {

    }

    public void initView() {
        if(reloadData) {
            reloadData = false;
            Kid kid = kidsService.getKidDetailedData(model.getKidId());
            List<Program> assignedPrograms = kid.getPrograms();
            List<Program> programsWithCollectedData = kidProgramsService.getKidProgramsWithCollectedData(kid.getId());
            model = new KidProgramsModel(kidPeriodsService, kid, assignedPrograms, programsWithCollectedData);
        }
    }

    // Schedule
    public void onCalendarRepresentationOfPeriodSelect(SelectEvent selectEvent) {
        DefaultScheduleEvent selectedCalendarRepresentationOfPeriod = (DefaultScheduleEvent) selectEvent.getObject();
        model.setSelectedCalendarRepresentationOfPeriod(selectedCalendarRepresentationOfPeriod);
        model.refreshProgramsForSelectedPeriod();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        Period newPeriod = new Period((Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        model.setNewPeriod(newPeriod);
    }

    public void addPeriod(ActionEvent actionEvent) {
        if(!ScheduleDatesValidator.arePeriodDatesValid(model.getNewPeriod(), model.getScheduleModel())) {
            ViewMsgUtil.setViewErrorMsg(ViewMsg.WRONG_PERIOD_DATA);
            return;
        }

        Period newPeriod = model.getNewPeriod();
        newPeriod.setKidId(model.getKid().getId());
        newPeriod.setKidTables(copyKidTablesFromPreviousPeriod(newPeriod));
        kidPeriodsService.addPeriod(newPeriod);

        model.notifyNewPeriodSaved();
    }

    private List<KidTable> copyKidTablesFromPreviousPeriod(Period newPeriod) {
        Period previousPeriod = findPreviousPeriod(newPeriod);
        List<KidTable> previousPeriodKidTables = kidPeriodsService.getKidTablesWithTableAndRowAndFieldAndProgramByPeriod(previousPeriod);
        return kidPeriodsService.getCopyOfKidTables(previousPeriodKidTables);
    }

    private Period findPreviousPeriod(Period currentPeriod) {
        List<Period> kidAllPeriods = model.getAllPeriods();
        sortPeriodsByBeginDateAscending(kidAllPeriods);
        return getPeriodJustBeforeCurrent(kidAllPeriods, currentPeriod);
    }

    private void sortPeriodsByBeginDateAscending(List<Period> periods) {
        //        TODO: check what if list is empty
        periods.sort((period1, period2) -> period1.getBeginDate().compareTo(period2.getBeginDate()));
    }

    private Period getPeriodJustBeforeCurrent(List<Period> ascendingSortedPeriods, Period currentPeriod) {
        Period periodJustBeforeCurrent = null;
        for(Period period: ascendingSortedPeriods) {
            if(currentPeriod.getBeginDate().after(period.getEndDate()))
                periodJustBeforeCurrent = period;
        }
        return periodJustBeforeCurrent;
    }

    // Actions
    public void assignProgramToKid(Program program) {
        Program assignedProgram = kidProgramsService.assignProgramToKid(model.getKid().getId(), program.getId());
        model.notifyProgramAssignedToKid(assignedProgram);
    }

    public void changeProgramStatus(int programId, boolean finished) {
//        TODO: shouldnt notify model?
        kidProgramsService.changeProgramFinishedStatus(programId, finished);
    }

    public void onTabChange(TabChangeEvent event) {
//        TODO: there ara 3 tabs now
        model.setActiveTab(model.getActiveTab() == 0 ? 1 : 0);
        model.refreshProgramsForSelectedPeriod();
    }

    public void onDateChange() {
        ScheduleModel scheduleModel = model.getScheduleModel();
        DefaultScheduleEvent selectedCalendarRepresentationOfPeriod = model.getSelectedCalendarRepresentationOfPeriod();
        Period currentPeriod = model.getCurrentSelectedPeriod();

        if(!ScheduleDatesValidator.arePeriodDatesValid(currentPeriod, scheduleModel, selectedCalendarRepresentationOfPeriod)) {
            ScheduldeEventWrapper.syncPeriodDataWithDataFromCalendarRepresentation(selectedCalendarRepresentationOfPeriod);
            showErrorMessage(ViewMsg.WRONG_PERIOD_DATA);
            return;
        }
        ScheduldeEventWrapper.syncCalendarRepresentationDataWithDataFromPeriod(selectedCalendarRepresentationOfPeriod);
        kidPeriodsService.updatePeriod(currentPeriod);
    }

    public void assignTableToPeriod(Table chosenTable) {
        kidPeriodsService.assignTableToPeriod(chosenTable, model.getCurrentSelectedPeriod());
        model.refreshProgramsForSelectedPeriod();
    }

    public void changeIOAStatus(KidTable kidTable, boolean isIOA) {
        kidTable.setIOA(isIOA);
        kidPeriodsService.changeIOAForKidTable(kidTable);
    }

    public void deleteKidTable(KidTable kidTable) {
        if(checkAndHandleKidTableDeletionLegality(kidTable)) {
            kidPeriodsService.deleteKidTable(kidTable);
            model.notifyKidTableDeleted(kidTable);
        }
    }

    private boolean checkAndHandleKidTableDeletionLegality(KidTable kidTable) {
        if (kidPeriodsService.hasKidTableFilledResolvedFields(kidTable)) {
            showErrorMessage(ViewMsg.UNABLE_TO_EDIT_PROGRAM_WITH_FILLED_RESOLVE);
            return false;
        }
        return true;
    }

    public void deleteCurrentlySelectedPeriod() {
        Period currentlySelectedPeriod = model.getCurrentSelectedPeriod();
        if(checkAndHandlePeriodDeletionLegality(currentlySelectedPeriod)) {
            kidPeriodsService.removePeriod(currentlySelectedPeriod);
            model.notifyCurrentlySelectedPeriodDeleted();
        }
    }

    private boolean checkAndHandlePeriodDeletionLegality(Period period) {
        if (period == null) {
            showErrorMessage(ViewMsg.UNABLE_TO_REMOVE_UNSELECTED_PERIOD);
            return false;
        }
        if(isAnyOfKidTableInPeriodHasFilledResolvedFields(period)) {
            showErrorMessage(ViewMsg.UNABLE_TO_REMOVE_PERIOD_WITH_FILLED_RESOLVE);
            return false;
        }
        return true;
    }

    private boolean isAnyOfKidTableInPeriodHasFilledResolvedFields(Period period) {
        List<KidTable> kidTables = kidPeriodsService.getKidTablesWithTableAndProgramByPeriod(period);
        if (kidTables.isEmpty()) {
            return false;
        }
        else {
            return kidTables.stream().filter(
                    kidTable -> kidPeriodsService.hasKidTableFilledResolvedFields(kidTable)
            ).count() != 0;
        }
    }

    // Navigation
    public void navigateToChangeKidTable(KidTable kidTableToChange) {
        if (kidPeriodsService.hasKidTableFilledResolvedFields(kidTableToChange))
            showErrorMessage(ViewMsg.UNABLE_TO_EDIT_PROGRAM_WITH_FILLED_RESOLVE);
        else
            NavigationUtil.redirect("../kidsChooseTableView.xhtml?chosenKidTableId=" + kidTableToChange.getId());
    }

    public void navigateToKidBrowser() {
        NavigationUtil.redirect("../kidsView.xhtml");
    }

    public void navigateToTableChartsView(int programId) {
        NavigationUtil.redirect("charts/tableVisualisation.xhtml?programId=" + programId);
    }

    public void navigateToLineChartsView(int programId) {
        NavigationUtil.redirect("charts/chartVisualisation.xhtml?programId=" + programId);
    }

    // Data tables


    public void refreshUnassignedPrograms() {
        List<Program> programs = kidProgramsService.getUnusedProgramsByKidId(model.getKid().getId());
        FilterableList<Program> unassignedPrograms = new FilterableList<>(programs);
        model.setUnassignedPrograms(unassignedPrograms);
    }

    public void refreshProgramsNotInCurrentPeriod() {
        List<Program> programs = kidPeriodsService.getKidProgramsNotInPeriod(model.getCurrentSelectedPeriod().getId());
        FilterableList<Program> programsNotInPeriod = new FilterableList<>(programs);
        model.setProgramsNotInPeriod(programsNotInPeriod);
    }

    public void refreshTablesInProgramByProgram(Program program) {
        List<Table> tablesInProgram = kidPeriodsService.getTablesByProgramId(program.getId());
//        TODO: select from db already with connected tables
        tablesInProgram.forEach(table -> table.setProgram(program));
        model.setTablesInProgram(tablesInProgram);
    }

    public void deleteKidProgram(Program program){
        if(!kidProgramsService.checkIfProgramHasTables(program))
        {
            kidProgramsService.deleteKidProgram(program);
            deleteProgramFromKidProgramsListInView(program);
        }
        else
            showErrorMessage(ViewMsg.UNABLE_TO_DELETE_PROGRAM_ASSIGNMENT);
    }

    public void showErrorMessage(ViewMsg viewMsg) {
        ViewMsgUtil.setViewErrorMsg(viewMsg);
    }

    public void deleteProgramFromKidProgramsListInView(Program program) {
        model.getAssignedPrograms().remove(program);
    }

}