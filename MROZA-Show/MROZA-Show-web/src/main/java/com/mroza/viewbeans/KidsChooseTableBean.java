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

package com.mroza.viewbeans;

import com.mroza.interfaces.KidPeriodsService;
import com.mroza.interfaces.ProgramsService;
import com.mroza.models.KidTable;
import com.mroza.models.Program;
import com.mroza.models.Table;
import com.mroza.qualifiers.KidPeriodsServiceImpl;
import com.mroza.qualifiers.ProgramsServiceImpl;
import com.mroza.viewbeans.utils.NavigationUtil;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@ViewScoped
@Named
public class KidsChooseTableBean implements Serializable {

    @Inject
    @KidPeriodsServiceImpl
    private KidPeriodsService kidPeriodsService;

    @Inject
    @ProgramsServiceImpl
    private ProgramsService programsService;

    @Getter
    @Setter
    private Integer chosenKidTableId;

    private KidTable previousKidTable;

    @Getter
    @Setter
    private Table chosenTable;

    @Getter
    @Setter
    private List<Table> tables;

    @Getter
    private Program program;

    @PostConstruct
    private void init() {

    }

    public void initView() {
        if (tables == null) {
            previousKidTable = kidPeriodsService.getKidTableById(chosenKidTableId);
            Integer programId = previousKidTable.getTable().getProgramId();
            tables = kidPeriodsService.getTablesByProgramId(programId);
            program = programsService.retrieveProgram(programId);
            refreshChosenTable(previousKidTable.getTableId());
        }
    }

    public void saveChanges() {
        kidPeriodsService.replaceAssignmentTableToPeriod(previousKidTable, chosenTable);
        NavigationUtil.redirect("kidProgramsView/kidProgramsView.xhtml");
    }

    private void refreshChosenTable(Integer chosenTabId) {
        chosenTable = tables.stream().filter(table -> table.getId() == chosenTabId).findFirst().get();
    }

    public void changeTableSelection(int selectedTabId) {
        refreshChosenTable(selectedTabId);
    }
}
