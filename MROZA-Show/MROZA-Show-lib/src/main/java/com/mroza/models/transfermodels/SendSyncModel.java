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

package com.mroza.models.transfermodels;

import com.mroza.models.transfermodels.*;

import java.util.ArrayList;
import java.util.List;

public class SendSyncModel {

    private List<TransferChild> childList;
    private List<TransferProgram> programList;
    private List<TransferTableTemplate> tableTemplateList;
    private List<TransferTableRow> tableRowList;
    private List<TransferTableField> tableFieldList;
    private List<TransferTableFieldFilling> tableFieldFillingList;
    private List<TransferChildTable> childTableList;
    private List<TransferTermSolution> termSolutionList;

    private List<TransferChild> deletedChildList;
    private List<TransferProgram> deletedProgramList;
    private List<TransferTableTemplate> deletedTableTemplateList;
    private List<TransferTableRow> deletedTableRowList;
    private List<TransferTableField> deletedTableFieldList;
    private List<TransferTableFieldFilling> deletedTableFieldFillingList;
    private List<TransferChildTable> deletedChildTableList;
    private List<TransferTermSolution> deletedTermSolutionList;

    public SendSyncModel()
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

    public List<TransferChild> getChildList() {
        return childList;
    }

    public void setChildList(List<TransferChild> childList) {
        this.childList = childList;
    }

    public List<TransferProgram> getProgramList() {
        return programList;
    }

    public void setProgramList(List<TransferProgram> programList) {
        this.programList = programList;
    }

    public List<TransferTableTemplate> getTableTemplateList() {
        return tableTemplateList;
    }

    public void setTableTemplateList(List<TransferTableTemplate> tableTemplateList) {
        this.tableTemplateList = tableTemplateList;
    }

    public List<TransferTableRow> getTableRowList() {
        return tableRowList;
    }

    public void setTableRowList(List<TransferTableRow> tableRowList) {
        this.tableRowList = tableRowList;
    }

    public List<TransferTableField> getTableFieldList() {
        return tableFieldList;
    }

    public void setTableFieldList(List<TransferTableField> tableFieldList) {
        this.tableFieldList = tableFieldList;
    }

    public List<TransferTableFieldFilling> getTableFieldFillingList() {
        return tableFieldFillingList;
    }

    public void setTableFieldFillingList(List<TransferTableFieldFilling> tableFieldFillingList) {
        this.tableFieldFillingList = tableFieldFillingList;
    }

    public List<TransferChildTable> getChildTableList() {
        return childTableList;
    }

    public void setChildTableList(List<TransferChildTable> childTableList) {
        this.childTableList = childTableList;
    }

    public List<TransferTermSolution> getTermSolutionList() {
        return termSolutionList;
    }

    public void setTermSolutionList(List<TransferTermSolution> termSolutionList) {
        this.termSolutionList = termSolutionList;
    }

    public List<TransferChild> getDeletedChildList() {
        return deletedChildList;
    }

    public void setDeletedChildList(List<TransferChild> deletedChildList) {
        this.deletedChildList = deletedChildList;
    }

    public List<TransferProgram> getDeletedProgramList() {
        return deletedProgramList;
    }

    public void setDeletedProgramList(List<TransferProgram> deletedProgramList) {
        this.deletedProgramList = deletedProgramList;
    }

    public List<TransferTableTemplate> getDeletedTableTemplateList() {
        return deletedTableTemplateList;
    }

    public void setDeletedTableTemplateList(List<TransferTableTemplate> deletedTableTemplateList) {
        this.deletedTableTemplateList = deletedTableTemplateList;
    }

    public List<TransferTableRow> getDeletedTableRowList() {
        return deletedTableRowList;
    }

    public void setDeletedTableRowList(List<TransferTableRow> deletedTableRowList) {
        this.deletedTableRowList = deletedTableRowList;
    }

    public List<TransferTableField> getDeletedTableFieldList() {
        return deletedTableFieldList;
    }

    public void setDeletedTableFieldList(List<TransferTableField> deletedTableFieldList) {
        this.deletedTableFieldList = deletedTableFieldList;
    }

    public List<TransferTableFieldFilling> getDeletedTableFieldFillingList() {
        return deletedTableFieldFillingList;
    }

    public void setDeletedTableFieldFillingList(List<TransferTableFieldFilling> deletedTableFieldFillingList) {
        this.deletedTableFieldFillingList = deletedTableFieldFillingList;
    }

    public List<TransferChildTable> getDeletedChildTableList() {
        return deletedChildTableList;
    }

    public void setDeletedChildTableList(List<TransferChildTable> deletedChildTableList) {
        this.deletedChildTableList = deletedChildTableList;
    }

    public List<TransferTermSolution> getDeletedTermSolutionList() {
        return deletedTermSolutionList;
    }

    public void setDeletedTermSolutionList(List<TransferTermSolution> deletedTermSolutionList) {
        this.deletedTermSolutionList = deletedTermSolutionList;
    }
}
