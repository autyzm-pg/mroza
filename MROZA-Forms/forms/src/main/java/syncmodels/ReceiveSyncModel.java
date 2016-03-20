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

package syncmodels;

import android.widget.*;
import database.*;
import database.TableRow;

import java.util.ArrayList;
import java.util.List;


public class ReceiveSyncModel {

    private List<TableTemplate> deletedTableTemplateList;
    private List<TermSolution> deletedTermSolutionList;
    private List<TableRow> deletedTableRowList;
    private List<TableField> deletedTableFieldList;
    private List<TableFieldFilling> deletedTableFieldFillingList;
    private List<Program> deletedProgramList;
    private List<ChildTable> deletedChildTableList;
    private List<Child> deletedChildList;
    private List<Child> childList;
    private List<Program> programList;
    private List<TableTemplate> tableTemplateList;
    private List<TableRow> tableRowList;
    private List<TableField> tableFieldList;
    private List<TableFieldFilling> tableFieldFillingList;
    private List<ChildTable> childTableList;
    private List<TermSolution> termSolutionList;

    public ReceiveSyncModel()
    {
        this.childList = new ArrayList<>();
        this.childTableList = new ArrayList<>();
        this.programList = new ArrayList<>();
        this.tableFieldFillingList = new ArrayList<>();
        this.tableFieldList = new ArrayList<>();
        this.tableRowList = new ArrayList<>();
        this.tableTemplateList = new ArrayList<>();
        this.termSolutionList = new ArrayList<>();

        this.deletedChildList = new ArrayList<>();
        this.deletedChildTableList = new ArrayList<>();
        this.deletedProgramList = new ArrayList<>();
        this.deletedTableFieldFillingList = new ArrayList<>();
        this.deletedTableFieldList = new ArrayList<>();
        this.deletedTableRowList = new ArrayList<>();
        this.deletedTableTemplateList = new ArrayList<>();
        this.deletedTermSolutionList = new ArrayList<>();
    }


    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }

    public void setProgramList(List<Program> programList) {
        this.programList = programList;
    }

    public void setTableTemplateList(List<TableTemplate> tableTemplateList) {
        this.tableTemplateList = tableTemplateList;
    }

    public void setTableRowList(List<TableRow> tableRowList) {
        this.tableRowList = tableRowList;
    }

    public void setTableFieldList(List<TableField> tableFieldList) {
        this.tableFieldList = tableFieldList;
    }

    public void setTableFieldFillingList(List<TableFieldFilling> tableFieldFillingList) {
        this.tableFieldFillingList = tableFieldFillingList;
    }

    public void setChildTableList(List<ChildTable> childTableList) {
        this.childTableList = childTableList;
    }

    public void setTermSolutionList(List<TermSolution> termSolutionList) {
        this.termSolutionList = termSolutionList;
    }

    public List<Child> getChildList() {
        return childList;
    }

    public List<Program> getProgramList() {
        return programList;
    }

    public List<TableTemplate> getTableTemplateList() {
        return tableTemplateList;
    }

    public List<TableRow> getTableRowList() {
        return tableRowList;
    }

    public List<TableField> getTableFieldList() {
        return tableFieldList;
    }

    public List<TableFieldFilling> getTableFieldFillingList() {
        return tableFieldFillingList;
    }

    public List<ChildTable> getChildTableList() {
        return childTableList;
    }

    public List<TermSolution> getTermSolutionList() {
        return termSolutionList;
    }

    public List<TableTemplate> getDeletedTableTemplateList() {
        return deletedTableTemplateList;
    }

    public void setDeletedTableTemplateList(List<TableTemplate> deletedTableTemplateList) {
        this.deletedTableTemplateList = deletedTableTemplateList;
    }

    public List<TermSolution> getDeletedTermSolutionList() {
        return deletedTermSolutionList;
    }

    public void setDeletedTermSolutionList(List<TermSolution> deletedTermSolutionList) {
        this.deletedTermSolutionList = deletedTermSolutionList;
    }

    public List<TableRow> getDeletedTableRowList() {
        return deletedTableRowList;
    }

    public void setDeletedTableRowList(List<TableRow> deletedTableRowList) {
        this.deletedTableRowList = deletedTableRowList;
    }

    public List<TableField> getDeletedTableFieldList() {
        return deletedTableFieldList;
    }

    public void setDeletedTableFieldList(List<TableField> deletedTableFieldList) {
        this.deletedTableFieldList = deletedTableFieldList;
    }

    public List<TableFieldFilling> getDeletedTableFieldFillingList() {
        return deletedTableFieldFillingList;
    }

    public void setDeletedTableFieldFillingList(List<TableFieldFilling> deletedTableFieldFillingList) {
        this.deletedTableFieldFillingList = deletedTableFieldFillingList;
    }

    public List<Program> getDeletedProgramList() {
        return deletedProgramList;
    }

    public void setDeletedProgramList(List<Program> deletedProgramList) {
        this.deletedProgramList = deletedProgramList;
    }

    public List<ChildTable> getDeletedChildTableList() {
        return deletedChildTableList;
    }

    public void setDeletedChildTableList(List<ChildTable> deletedChildTableList) {
        this.deletedChildTableList = deletedChildTableList;
    }

    public List<Child> getDeletedChildList() {
        return deletedChildList;
    }

    public void setDeletedChildList(List<Child> deletedChildList) {
        this.deletedChildList = deletedChildList;
    }
}
