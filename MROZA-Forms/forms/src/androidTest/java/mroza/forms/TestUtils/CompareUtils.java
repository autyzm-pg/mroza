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

package mroza.forms.TestUtils;

import database.*;
import syncmodels.TransferChildTable;
import syncmodels.TransferTableFieldFilling;

import java.util.Date;
import java.util.List;
import java.util.Objects;


public class CompareUtils {

    public static boolean areTwoProgramsTheSame(Program program1, Program program2) {

        if(program1.getId() == program2.getId())
            if (program1.getSymbol().equals(program2.getSymbol()))
                if(program1.getName().equals(program2.getName()))
                    if(program1.getDescription().equals(program2.getDescription()))
                        if(program1.getCreateDate().compareTo(program2.getCreateDate()) == 0)
                        {

                            List<TableTemplate> program1TableTemplates = program1.getTableTemplateList();
                            List<TableTemplate> program2TableTemplates = program2.getTableTemplateList();

                            if(program1TableTemplates.size() != program2TableTemplates.size())
                                return false;


                            for(int tableTemplateId = 0; tableTemplateId < program1TableTemplates.size(); tableTemplateId++)
                                if(!areTwoTableTemplatesTheSame(program1TableTemplates.get(tableTemplateId),
                                        program2TableTemplates.get(tableTemplateId)))
                                    return false;

                            return true;
                        }

        return false;
    }

    public static boolean areTwoTableTemplatesTheSame(TableTemplate tableTemplate, TableTemplate tableTemplate1) {

        if(tableTemplate.getId().equals(tableTemplate1.getId()))
            if(tableTemplate.getName().equals(tableTemplate1.getName()))
                if(tableTemplate.getDescription().equals(tableTemplate1.getDescription()))
                    if(tableTemplate.getCreateDate().compareTo(tableTemplate1.getCreateDate()) == 0)
                        if(tableTemplate.getIsArchived() == tableTemplate1.getIsArchived())
                        {
                            List<ChildTable> tableTemplateChildTableList = tableTemplate.getChildTableList();
                            List<ChildTable> tableTemplate1ChildTableList = tableTemplate1.getChildTableList();

                            if(tableTemplate1ChildTableList.size() != tableTemplateChildTableList.size())
                                return false;

                            for(int childTableId = 0; childTableId < tableTemplate1ChildTableList.size(); childTableId++ )
                            {
                                if(!areTwoChildTablesTheSame(tableTemplate1ChildTableList.get(childTableId), tableTemplateChildTableList.get(childTableId)))
                                    return false;
                            }

                            List<TableRow> tableTemplateTableRowList = tableTemplate.getTableRowList();
                            List<TableRow> tableTemplate1TableRowList = tableTemplate1.getTableRowList();

                            if(tableTemplateTableRowList.size() != tableTemplate1TableRowList.size())
                                return false;


                            for(int tableRowId = 0; tableRowId < tableTemplateTableRowList.size(); tableRowId++ )
                            {
                                if(!areTwoTableRowTheSame(tableTemplateTableRowList.get(tableRowId), tableTemplate1TableRowList.get(tableRowId)))
                                    return false;
                            }

                            return true;
                        }


        return false;
    }

    public static boolean areTwoTableRowTheSame(TableRow tableRow, TableRow tableRow1) {
        if(tableRow.getId().equals(tableRow1.getId()))
            if(tableRow.getInOrder() == tableRow1.getInOrder())
                if(tableRow.getTableId() == tableRow1.getTableId())
                    if(tableRow.getValue().equals(tableRow1.getValue()))
                    {
                        List<TableField> tableFields = tableRow.getTableFieldList();
                        List<TableField> tableFields1 =tableRow1.getTableFieldList();

                        if(tableFields.size() != tableFields1.size())
                            return false;

                        for(int tableFieldId = 0 ; tableFieldId<tableFields.size(); tableFieldId++)
                        {
                            if(!areTwoTableFieldsTheSame(tableFields.get(tableFieldId),tableFields1.get(tableFieldId)))
                                return false;
                        }

                        return true;
                    }

        return false;
    }

    public static boolean areTwoTableFieldsTheSame(TableField tableField, TableField tableField1) {

        if(tableField.getId() == tableField1.getId())
            if(tableField.getInOrder() == tableField1.getInOrder())
                if(tableField.getType().equals(tableField1.getType()))
                    if(tableField.getTableRowId() == tableField1.getTableRowId())
                    {
                        List<TableFieldFilling> tableFieldFillingList = tableField.getTableFieldFillingList();
                        List<TableFieldFilling> tableFieldFilling1List = tableField1.getTableFieldFillingList();

                        if(tableFieldFillingList.size() != tableFieldFilling1List.size())
                            return false;

                        for(int tableFieldFillingId = 0; tableFieldFillingId<tableFieldFillingList.size(); tableFieldFillingId++)
                        {
                            if(!areTwoTableFieldFillingsTheSame(tableFieldFillingList.get(tableFieldFillingId), tableFieldFilling1List.get(tableFieldFillingId)))
                                return false;
                        }

                        return true;
                    }


        return false;
    }

    public static boolean areTwoTableFieldFillingsTheSame(TableFieldFilling tableFieldFilling, TableFieldFilling tableFieldFilling1) {

        if(tableFieldFilling.getId() == tableFieldFilling1.getId())
            if(tableFieldFilling.getContent().equals(tableFieldFilling1.getContent()))
                if(tableFieldFilling.getTableFieldId() == tableFieldFilling1.getTableFieldId())
                    if(tableFieldFilling.getChildTableId() == tableFieldFilling1.getChildTableId())
                        return true;
        return false;
    }

    public static boolean areTwoChildTablesTheSame(ChildTable childTable, ChildTable childTable1) {

        if(childTable.getId() == childTable1.getId())
            if(childTable.getIsPretest() == childTable1.getIsPretest())
                if(childTable.getNote().equals(childTable1.getNote()))
                    if(childTable.getIsGeneralizationCollected() == childTable1.getIsGeneralizationCollected())
                        if(childTable.getIsGeneralizationFinished() == childTable1.getIsGeneralizationFinished())
                            if(childTable.getIsIOA() == childTable1.getIsIOA())
                                if(childTable.getIsTeachingCollected() == childTable1.getIsTeachingCollected())
                                    if(childTable.getIsTeachingFinished() == childTable1.getIsTeachingFinished())
                                        if(compareTwoDates(childTable.getTeachingFillOutDate(), childTable1.getTeachingFillOutDate()))
                                            if(compareTwoDates(childTable.getLastEditDate(),childTable1.getLastEditDate()))
                                                if(compareTwoDates(childTable.getTeachingFillOutDate(),childTable1.getTeachingFillOutDate()))
                                                {
                                                    TermSolution termSolution = childTable.getTermSolution();
                                                    TermSolution termSolution1 = childTable1.getTermSolution();

                                                    if(!areTermSolutionsTheSame(termSolution,termSolution1))
                                                        return false;

                                                    List<TableFieldFilling> tableFieldFillingList = childTable.getTableFieldFilling();
                                                    List<TableFieldFilling> tableFieldFilling1List = childTable1.getTableFieldFilling();

                                                    if(tableFieldFillingList.size() != tableFieldFilling1List.size())
                                                        return false;

                                                    for(int tableFieldFillingId = 0; tableFieldFillingId<tableFieldFillingList.size(); tableFieldFillingId++)
                                                    {
                                                        if(!areTwoTableFieldFillingsTheSame(tableFieldFillingList.get(tableFieldFillingId), tableFieldFilling1List.get(tableFieldFillingId)))
                                                            return false;
                                                    }

                                                    return true;


                                                }

        return false;
    }

    private static boolean compareTwoDates(Date date1, Date date2) {

        if(date1 == null && date2 == null)
            return true;
        if(date1 != null && date2 != null)
            if (date1.compareTo(date2) == 0)
                return true;

        return false;
    }

    public static boolean areTermSolutionsTheSame(TermSolution termSolution, TermSolution termSolution1) {

        if(termSolution.getId() == termSolution1.getId())
            if(termSolution.getStartDate().compareTo(termSolution1.getStartDate()) == 0)
                if(termSolution.getEndDate().compareTo(termSolution1.getEndDate()) == 0)
                    if(termSolution.getChildId() == termSolution1.getChildId())
                        return true;

        return false;
    }


    public static boolean areTwoChildsTheSame(Child child1, Child child2) {

        if(child1.getId() == child2.getId())
            if(child1.getCode().equals(child2.getCode()))
                if(child1.getIsArchived() == child2.getIsArchived())
                    return true;

        return false;
    }

    public static boolean areTwoTransferChildTableTheSame(TransferChildTable transferChildTable1, TransferChildTable transferChildTable2) {

        if(transferChildTable1.getId().equals(transferChildTable2.getId()))
                if(transferChildTable1.getIsPretest() == transferChildTable2.getIsPretest())
                    if(transferChildTable1.getNote().equals(transferChildTable2.getNote()))
                        if(transferChildTable1.getIsGeneralizationCollected() == transferChildTable2.getIsGeneralizationCollected())
                            if(transferChildTable1.getIsGeneralizationFinished() == transferChildTable2.getIsGeneralizationFinished())
                                if(transferChildTable1.getIsIOA() == transferChildTable2.getIsIOA())
                                    if(transferChildTable1.getIsTeachingCollected() == transferChildTable2.getIsTeachingCollected())
                                        if(transferChildTable1.getIsTeachingFinished() == transferChildTable2.getIsTeachingFinished())
                                            if(transferChildTable1.getGeneralizationFillOutDate().compareTo(transferChildTable2.getGeneralizationFillOutDate()) == 0)
                                                if(transferChildTable1.getLastEditDate().compareTo(transferChildTable2.getLastEditDate()) == 0)
                                                    if(transferChildTable1.getTeachingFillOutDate().compareTo(transferChildTable2.getTeachingFillOutDate()) == 0)
                                                       if(transferChildTable1.getTermSolutionId() == transferChildTable2.getTermSolutionId())
                                                        return true;

        return false;
    }

    public static boolean areTwoTransferFieldFillingsTheSame(TransferTableFieldFilling transferTableFieldFilling1, TransferTableFieldFilling transferTableFieldFilling2) {
        if(transferTableFieldFilling1.getId().equals(transferTableFieldFilling2.getId()))
            if(transferTableFieldFilling1.getContent().equals(transferTableFieldFilling2.getContent()))
                if(transferTableFieldFilling1.getTableFieldId() == transferTableFieldFilling2.getTableFieldId())
                    if(transferTableFieldFilling1.getChildTableId() == transferTableFieldFilling2.getChildTableId())
                        return true;
        return false;
    }
}
