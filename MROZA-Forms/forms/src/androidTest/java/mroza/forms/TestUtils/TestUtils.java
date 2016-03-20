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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import database.*;
import syncmodels.SyncManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TestUtils {

    public enum childTableOption {TEACHING_NOT_STARTED, TEACHING_SAVED, GENERALIZATION_NOT_STARTED, GENERALIZATION_SAVED, GENERALIZATION_FINISHED, TEACHING_FINISHED}

    public static void cleanUpDatabase(Context context) {

        DaoSession daoSession = getDaoSession(context);
        daoSession.getSyncDateDao().deleteAll();
        daoSession.getChildDao().deleteAll();
        daoSession.getProgramDao().deleteAll();
        daoSession.getTableTemplateDao().deleteAll();
        daoSession.getChildTableDao().deleteAll();
        daoSession.getTermSolutionDao().deleteAll();
        daoSession.getTableRowDao().deleteAll();
        daoSession.getTableFieldDao().deleteAll();
        daoSession.getTableFieldFillingDao().deleteAll();
    }

    public static void setUpSyncDateLaterThenTestExecute(Context context) throws ParseException {
        Calendar c = Calendar.getInstance();
        int deltaMinutes = c.get(Calendar.MINUTE) + 30;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date futureSyncDate = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + (c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + deltaMinutes + ":" + c.get(Calendar.SECOND)));

        SyncManager syncManager = new SyncManager(context);
        syncManager.setSyncDate(futureSyncDate);
    }



    private static DaoSession getDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }


    public static List<Child> setUpChildren(Context context, int numberOfChildren, String code){

        List<Child> childList = new ArrayList<>();
        ChildDao childDao = getDaoSession(context).getChildDao();
        for(int childId = 0; childId< numberOfChildren; childId++)
        {
            Child child = new Child();
            child.setCode(code + childId);
            child.setIsArchived(false);
            childDao.insertOrReplace(child);
            childList.add(child);

        }
        return childList;
    }

    public static List<Program> setUpPrograms(Context context, List<Child> children, int numberOfPrograms, String programName, String programSymbol) throws ParseException {

        List<Program> programs = new ArrayList<>();
        DaoSession daoSession =  getDaoSession(context);


        for(Child child : children){

            TermSolution termSolution = setUpTermSolution(context, child, -5, 5);

            for(int programId=0; programId< numberOfPrograms; programId++)
            {
                ProgramDao programDao = daoSession.getProgramDao();
                Program program = new Program();
                program.setChild(child);
                program.setCreateDate(new Date());
                program.setDescription("Opis dlugi");
                program.setIsFinished(false);
                program.setName(programName);
                program.setSymbol(programSymbol + programId);
                programDao.insertOrReplace(program);

                TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
                TableTemplate tabletemplate = new TableTemplate();
                tabletemplate.setName(programSymbol + " " + programName);
                tabletemplate.setCreateDate(new Date());
                tabletemplate.setDescription("Krotki opis");
                tabletemplate.setIsArchived(false);
                tabletemplate.setProgram(program);
                tableTemplateDao.insertOrReplace(tabletemplate);

                ChildTableDao childTableDao = daoSession.getChildTableDao();
                ChildTable childTable = new ChildTable();
                childTable.setTeachingFillOutDate(new Date());
                childTable.setGeneralizationFillOutDate(new Date());
                childTable.setLastEditDate(new Date());
                childTable.setIsGeneralizationCollected(true);
                childTable.setIsGeneralizationFinished(false);
                childTable.setIsTeachingCollected(true);
                childTable.setIsTeachingFinished(false);
                childTable.setIsIOA(false);
                childTable.setIsPretest(false);
                childTable.setNote("Jest ok");
                childTable.setTableTemplate(tabletemplate);
                childTable.setTermSolution(termSolution);
                childTableDao.insertOrReplace(childTable);

                setUpTableContent(context, tabletemplate, childTable, 2, 2, 1);
                programs.add(program);
            }
        }

        return programs;

    }

    public static TermSolution setUpTermSolution(Context context, Child child, int daysFromNowToStart, int daysFromNowToEnd) throws ParseException {

            TermSolutionDao termSolutionDao = getDaoSession(context).getTermSolutionDao();
            TermSolution termSolution = new TermSolution();
            Calendar c = Calendar.getInstance();
            termSolution.setChild(child);
            int dayStart = c.get(Calendar.DAY_OF_MONTH) + daysFromNowToStart;
            int dayEnd = c.get(Calendar.DAY_OF_MONTH) + daysFromNowToEnd;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            Date dateStart = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayStart + " 00:00:00");
            Date dateEnd = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayEnd + " 00:00:00");
            termSolution.setStartDate(dateStart);
            termSolution.setEndDate(dateEnd);
            termSolutionDao.insertOrReplace(termSolution);

        return termSolution;
    }

    private static void setUpTableContent(Context context, TableTemplate tableTemplate, ChildTable childTable, int rowsNumber, int teachingNumber, int generalizationNumber) {

     DaoSession daoSession = getDaoSession(context);
        TableRowDao tableRowDao = daoSession.getTableRowDao();
        List<TableRow> rows = new ArrayList<>();
        for (int i = 0; i < rowsNumber; i++) {
            TableRow tableRow = new TableRow();
            tableRow.setValue("Row " + i);
            tableRow.setInOrder(i);
            tableRow.setTableTemplate(tableTemplate);
            tableRowDao.insertOrReplace(tableRow);
            rows.add(tableRow);
        }

        TableFieldDao tableFieldDao = daoSession.getTableFieldDao();
        TableFieldFillingDao tableFieldFillingDao = daoSession.getTableFieldFillingDao();
        for (TableRow row : rows) {

            for (int i = 0; i < teachingNumber + generalizationNumber; i++) {
                String type = (i >= teachingNumber ? "G" : "U");

                TableField field = new TableField();
                field.setType(type);
                field.setInOrder(i);
                field.setTableRow(row);
                tableFieldDao.insertOrReplace(field);

                TableFieldFilling tableFieldFilling = new TableFieldFilling();
                String content = (i >= teachingNumber ? "passed" : "failed");

                tableFieldFilling.setContent(content);
                tableFieldFilling.setTableField(field);
                tableFieldFilling.setChildTable(childTable);
                tableFieldFillingDao.insertOrReplace(tableFieldFilling);


            }

        }
   }

    public static List<Program> setUpProgramToTermSolution(Context context, int numberOfPrograms, TermSolution termSolutionActual, String programName, String programSymbol) {

        List<Program> programs = new ArrayList<>();
        DaoSession daoSession =  getDaoSession(context);
        ProgramDao programDao = daoSession.getProgramDao();
        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        ChildTableDao childTableDao = daoSession.getChildTableDao();

            for(int programId=0; programId< numberOfPrograms; programId++)
            {
                Program program = new Program();
                program.setChild(termSolutionActual.getChild());
                program.setCreateDate(new Date());
                program.setDescription("Opis dlugi");
                program.setIsFinished(false);
                program.setName(programName);
                program.setSymbol(programSymbol);
                programDao.insertOrReplace(program);

                TableTemplate tabletemplate = new TableTemplate();
                tabletemplate.setName(programSymbol + " " + programName);
                tabletemplate.setCreateDate(new Date());
                tabletemplate.setDescription("Krotki opis");
                tabletemplate.setIsArchived(false);
                tabletemplate.setProgram(program);
                tableTemplateDao.insertOrReplace(tabletemplate);

                ChildTable childTable = new ChildTable();
                childTable.setTeachingFillOutDate(new Date());
                childTable.setGeneralizationFillOutDate(new Date());
                childTable.setLastEditDate(new Date());
                childTable.setIsGeneralizationCollected(true);
                childTable.setIsGeneralizationFinished(false);
                childTable.setIsTeachingCollected(true);
                childTable.setIsTeachingFinished(false);
                childTable.setIsIOA(false);
                childTable.setIsPretest(false);
                childTable.setNote("Jest ok");
                childTable.setTableTemplate(tabletemplate);
                childTable.setTermSolution(termSolutionActual);
                childTableDao.insertOrReplace(childTable);

                setUpTableContent(context, tabletemplate, childTable, 2, 2, 1);
                programs.add(program);
            }

        return programs;

    }

    public static ChildTable setUpChildTable(Context context, childTableOption option, Child child , TermSolution termSolution, String programName, String programSymbol) throws ParseException {

        DaoSession daoSession = getDaoSession(context);
        ChildTableDao childTableDao = daoSession.getChildTableDao();

        Program program = setUpProgramWithoutAnyConnections(context, child, programName, programSymbol);
        TableTemplate tabletemplate = setUpTableTemplateToProgramWithoutAnyConnections(context, programName + " " + programSymbol, program);

        ChildTable childTable = new ChildTable();

        switch (option) {
            case TEACHING_NOT_STARTED:
                childTable.setTeachingFillOutDate(null);
                childTable.setIsTeachingCollected(true);
                childTable.setIsTeachingFinished(false);
                childTable.setGeneralizationFillOutDate(null);
                childTable.setLastEditDate(null);
                childTable.setIsGeneralizationCollected(true);
                childTable.setIsGeneralizationFinished(false);
                break;
            case TEACHING_SAVED:
                childTable.setTeachingFillOutDate(new Date());
                childTable.setIsTeachingCollected(true);
                childTable.setIsTeachingFinished(false);
                childTable.setGeneralizationFillOutDate(null);
                childTable.setLastEditDate(new Date());
                childTable.setIsGeneralizationCollected(true);
                childTable.setIsGeneralizationFinished(false);
                break;
            case TEACHING_FINISHED:
                childTable.setTeachingFillOutDate(new Date());
                childTable.setIsTeachingCollected(true);
                childTable.setIsTeachingFinished(true);
                childTable.setGeneralizationFillOutDate(null);
                childTable.setLastEditDate(new Date());
                childTable.setIsGeneralizationCollected(true);
                childTable.setIsGeneralizationFinished(false);
                break;
            case GENERALIZATION_NOT_STARTED:
                childTable.setTeachingFillOutDate(new Date());
                childTable.setIsTeachingCollected(true);
                childTable.setIsTeachingFinished(true);
                childTable.setGeneralizationFillOutDate(null);
                childTable.setLastEditDate(new Date());
                childTable.setIsGeneralizationCollected(true);
                childTable.setIsGeneralizationFinished(false);
                break;
            case GENERALIZATION_SAVED:
                childTable.setTeachingFillOutDate(new Date());
                childTable.setIsTeachingCollected(true);
                childTable.setIsTeachingFinished(true);
                childTable.setGeneralizationFillOutDate(new Date());
                childTable.setLastEditDate(new Date());
                childTable.setIsGeneralizationCollected(true);
                childTable.setIsGeneralizationFinished(false);
                break;
            case GENERALIZATION_FINISHED:
                childTable.setTeachingFillOutDate(new Date());
                childTable.setIsTeachingCollected(true);
                childTable.setIsTeachingFinished(true);
                childTable.setGeneralizationFillOutDate(new Date());
                childTable.setLastEditDate(new Date());
                childTable.setIsGeneralizationCollected(true);
                childTable.setIsGeneralizationFinished(true);
                break;

        }

        childTable.setIsIOA(false);
        childTable.setIsPretest(false);
        childTable.setNote("Jest ok");
        childTable.setTableTemplate(tabletemplate);
        childTable.setTermSolution(termSolution);
        childTableDao.insertOrReplace(childTable);

        setUpTableContent(context, tabletemplate, childTable, 2, 2, 1);

        return childTable;

    }

    private static TableTemplate setUpTableTemplateToProgramWithoutAnyConnections(Context context, String tableTemplateName, Program program) {
        DaoSession daoSession = getDaoSession(context);
        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        TableTemplate tabletemplate = new TableTemplate();
        tabletemplate.setName(tableTemplateName);
        tabletemplate.setCreateDate(new Date());
        tabletemplate.setDescription("Krotki opis");
        tabletemplate.setIsArchived(false);
        tabletemplate.setProgram(program);
        tableTemplateDao.insertOrReplace(tabletemplate);
        return tabletemplate;
    }

    private static Program setUpProgramWithoutAnyConnections(Context context, Child child, String programName, String programSymbol) {

        DaoSession daoSession = getDaoSession(context);
        ProgramDao programDao = daoSession.getProgramDao();
        Program program = new Program();
        program.setChild(child);
        program.setCreateDate(new Date());
        program.setDescription("Opis dlugi");
        program.setIsFinished(false);
        program.setName(programName);
        program.setSymbol(programSymbol);
        programDao.insertOrReplace(program);
        return program;
    }

}
