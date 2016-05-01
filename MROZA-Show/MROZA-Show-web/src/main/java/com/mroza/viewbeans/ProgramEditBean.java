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
import com.mroza.interfaces.KidProgramsService;
import com.mroza.interfaces.ProgramsService;
import com.mroza.models.Program;
import com.mroza.models.Table;
import com.mroza.models.TableRow;
import com.mroza.qualifiers.KidPeriodsServiceImpl;
import com.mroza.qualifiers.KidProgramsServiceImpl;
import com.mroza.qualifiers.ProgramsServiceImpl;
import com.mroza.viewbeans.utils.ViewMsg;
import com.mroza.viewbeans.utils.ViewMsgUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@ViewScoped
@Named
public class ProgramEditBean implements Serializable {

    @Inject
    @KidProgramsServiceImpl
    private KidProgramsService kidProgramsService;

    @Inject
    @KidPeriodsServiceImpl
    private KidPeriodsService kidPeriodsService;

    @Inject
    @ProgramsServiceImpl
    private ProgramsService programsService;

    @Getter
    @Setter
    private int programId;

    @Getter
    @Setter
    private Program program;

    @Getter
    @Setter
    private List<Table> programTables;

    public void initView() {
        if (programTables == null) {
            programTables = kidPeriodsService.getTablesByProgramId(programId);
            program = programsService.retrieveProgram(programId);
        }
    }

    public void addNewTable() {
        Table newTable = new Table("");
        Program program = new Program();
        program.setId(programId);
        newTable.setProgram(program);
        newTable.setEdited(true);
        programTables.add(newTable);
    }

    public void addRow(Table table) {
        table.addTableRow("");
    }

    public void deleteRow(Table table, TableRow tableRow) {
        table.getTableRows().remove(tableRow);
    }

    public void save(Table table) {
        if(validateTable(table)) {

            for (TableRow row : table.getTableRows()) {
                row.updateRowFields(table.getNumberOfLearningCols(), table.getNumberOfGeneralizationCols());
            }
            kidProgramsService.saveTable(table);
            table.setEdited(false);
        }
    }

    public boolean validateTable(Table table) {
        if(StringUtils.isBlank(table.getName())) {
            ViewMsgUtil.setViewErrorMsg(ViewMsg.BLANK_TABLE_NAME);
            return false;
        }
        if(table.getNumberOfGeneralizationCols() == 0 && table.getNumberOfLearningCols() == 0) {
            ViewMsgUtil.setViewErrorMsg(ViewMsg.WRONG_NUMBER_OF_COLUMNS);
            return false;
        }
        if(table.getTableRows() == null || table.getTableRows().isEmpty()) {
            ViewMsgUtil.setViewErrorMsg(ViewMsg.WRONG_NUMBER_OF_ROWS);
            return false;
        }
        for(TableRow row : table.getTableRows()) {
            if(StringUtils.isBlank(row.getName())) {
                ViewMsgUtil.setViewErrorMsg(ViewMsg.EMPTY_ROW_NAME);
                return false;
            }
        }
        return true;
    }

    public void edit(Table table) {
        if (kidPeriodsService.hasTableFilledResolvedFields(table)) {
            ViewMsgUtil.setViewErrorMsg(ViewMsg.UNABLE_TO_EDIT_TABLE);
        } else {
            table.setNumberOfLearningCols(table.getNumberOfLearningCols());
            table.setNumberOfGeneralizationCols(table.getNumberOfGeneralizationCols());
            table.setEdited(true);
        }
    }

    public void copy(Table table) {
        Table newTable = new Table("");
        Program program = new Program();
        program.setId(programId);
        newTable.setProgram(program);
        newTable.setEdited(true);

        newTable.setNumberOfGeneralizationCols(table.getNumberOfGeneralizationCols());
        newTable.setNumberOfLearningCols(table.getNumberOfLearningCols());
        for(TableRow row : table.getTableRows()) {
            newTable.addTableRow(row.getName());
        }

        programTables.add(newTable);
    }
}
