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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ViewMsgUtil {

    private static Map<ViewMsg, String> msgKeyForType = new HashMap<ViewMsg, String>() {
        {
            put(ViewMsg.WRONG_PERIOD_DATA, "kidProgramsView.wrongPeriodDateMsg");
            put(ViewMsg.UNABLE_TO_EDIT_PROGRAM_WITH_FILLED_RESOLVE, "kidProgramsView.unableToEditProgramWithFilledResolveMsg");
            put(ViewMsg.PROGRAM_SYMBOL_DUPLICATED, "newProgramsView.symbolDuplicateMsg");
            put(ViewMsg.BLANK_TABLE_NAME, "editProgramView.blankTableName");
            put(ViewMsg.WRONG_NUMBER_OF_COLUMNS, "editProgramView.wrongNumberOfColumns");
            put(ViewMsg.WRONG_NUMBER_OF_ROWS, "editProgramView.wrongNumberOfRows");
            put(ViewMsg.EMPTY_ROW_NAME, "editProgramView.emptyRowName");
            put(ViewMsg.UNABLE_TO_EDIT_TABLE, "editProgramView.unableToEditTable");
            put(ViewMsg.PROGRAM_INSTANCE_EXISTS, "programsDirectoryView.programsInstanceExists");
            put(ViewMsg.ILLEGAL_TABLE_REMOVE_ACTION, "kidProgramsView.illegalTableRemoveAction");
            put(ViewMsg.ILLEGAL_PERIOD_REMOVE_ACTION, "kidProgramsView.illegalPeriodRemoveAction");
            put(ViewMsg.UNABLE_TO_REMOVE_PERIOD_WITH_FILLED_RESOLVE, "kidProgramsView.unableToRemovePeriodWithFilledResolvedFields");
            put(ViewMsg.UNABLE_TO_REMOVE_UNSELECTED_PERIOD,"kidProgramsView.unableToRemoveUnselectedPeriod");
            put(ViewMsg.KID_CODE_DUPLICATED, "newKidsView.codeDuplicatedMsg");
            put(ViewMsg.UNABLE_TO_DELETE_PROGRAM_ASSIGNMENT, "kidProgramsView.unableToRemoveProgramAssignment");
            put(ViewMsg.UNABLE_TO_REMOVE_TABLE_ASSIGNED_TO_PERIOD, "editProgramView.unableToDeleteTable");
        }
    };

    public static void setViewErrorMsg(ViewMsg msgType) {
        ResourceBundle messages = ResourceBundle.getBundle("messages");
        FacesContext.getCurrentInstance().addMessage(
                null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        messages.getString(msgKeyForType.get(msgType)),
                        messages.getString(msgKeyForType.get(msgType))));
    }

}
