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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import database.Child;
import database.ChildDao;
import database.ChildTable;
import database.ChildTableDao;
import database.DaoMaster;
import database.DaoSession;
import database.Program;
import database.ProgramDao;
import database.TableField;
import database.TableFieldDao;
import database.TableFieldFilling;
import database.TableFieldFillingDao;
import database.TableRow;
import database.TableRowDao;
import database.TableTemplate;
import database.TableTemplateDao;
import database.TermSolution;
import database.TermSolutionDao;

public class DatabaseUpdater {


    private final DaoSession daoSession;
    private final SQLiteDatabase db;

    public DatabaseUpdater(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "mroza-db", null);
        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public void clearUpDb() {
        daoSession.getSyncDateDao().deleteAll();
        daoSession.getChildDao().deleteAll();
        daoSession.getProgramDao().deleteAll();
        daoSession.getTableTemplateDao().deleteAll();
        daoSession.getChildTableDao().deleteAll();
        daoSession.getTermSolutionDao().deleteAll();
        daoSession.getTableRowDao().deleteAll();
        daoSession.getTableFieldDao().deleteAll();
        daoSession.getTableFieldFillingDao().deleteAll();
        daoSession.getTherapistDao().deleteAll();
    }

    public void updateDatabase(ReceiveSyncModel receiveSyncModel) {
        db.beginTransaction();
        try {
            updateChildren(receiveSyncModel.getChildList());
            updateTermSolutions(receiveSyncModel.getTermSolutionList());
            updateProgram(receiveSyncModel.getProgramList());
            updateTableTemplates(receiveSyncModel.getTableTemplateList());
            updateTableRows(receiveSyncModel.getTableRowList());
            updateTableFields(receiveSyncModel.getTableFieldList());
            updateChildTables(receiveSyncModel.getChildTableList());
            updateTableFieldFillings(receiveSyncModel.getTableFieldFillingList());
            deleteChildren(receiveSyncModel.getDeletedChildList());
            deleteTermSolutions(receiveSyncModel.getDeletedTermSolutionList());
            deleteProgram(receiveSyncModel.getDeletedProgramList());
            deleteTableTemplates(receiveSyncModel.getDeletedTableTemplateList());
            deleteTableRows(receiveSyncModel.getDeletedTableRowList());
            deleteTableFields(receiveSyncModel.getDeletedTableFieldList());
            deleteChildTables(receiveSyncModel.getDeletedChildTableList());
            deleteTableFieldFillings(receiveSyncModel.getDeletedTableFieldFillingList());
            db.setTransactionSuccessful();
        }
        catch(Exception ex){
        }finally {
            db.endTransaction();
        }
    }

    public void updateTermSolutions(List<TermSolution> changedTermSolutions) {

        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();

        for(TermSolution termSolution : changedTermSolutions)
        {
            termSolutionDao.insertOrReplace(termSolution);
        }

    }

    public void updateChildren(List<Child> changedChilds) {
        ChildDao childDao = daoSession.getChildDao();

        for(Child child : changedChilds)
        {
            childDao.insertOrReplace(child);
        }

    }

    public void updateProgram(List<Program> changedPrograms) {
        ProgramDao programDao = daoSession.getProgramDao();

        for(Program program : changedPrograms)
        {
            programDao.insertOrReplace(program);
        }
    }

    public void updateTableTemplates(List<TableTemplate> changedTableTemplates) {

        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();

        for(TableTemplate tableTemplate : changedTableTemplates)
        {
            tableTemplateDao.insertOrReplace(tableTemplate);
        }



    }

    public void updateTableRows(List<TableRow> changedTableRows) {

        TableRowDao tableRowDao = daoSession.getTableRowDao();

        for(TableRow tableRow : changedTableRows)
        {
            tableRowDao.insertOrReplace(tableRow);
        }
    }

    public void updateTableFields(List<TableField> changedTableFields) {
        TableFieldDao tableFieldDao = daoSession.getTableFieldDao();

        for(TableField tableField : changedTableFields)
        {
            tableFieldDao.insertOrReplace(tableField);
        }

    }

    public void updateTableFieldFillings(List<TableFieldFilling> changedTableFieldFillings) {

        TableFieldFillingDao tableFieldFillingDao = daoSession.getTableFieldFillingDao();

        for(TableFieldFilling tableFieldFilling : changedTableFieldFillings)
        {
            tableFieldFillingDao.insertOrReplace(tableFieldFilling);
        }

    }

    public void updateChildTables(List<ChildTable> changedChildTables) {
        ChildTableDao childTableDao = daoSession.getChildTableDao();

        for(ChildTable childTable : changedChildTables)
        {
            childTableDao.insertOrReplace(childTable);
        }
    }

    private void deleteTableFieldFillings(List<TableFieldFilling> deletedTableFieldFillingList) {
        TableFieldFillingDao tableFieldFillingDao = daoSession.getTableFieldFillingDao();

        for(TableFieldFilling tableFieldFilling : deletedTableFieldFillingList)
        {
            tableFieldFillingDao.delete(tableFieldFilling);
        }
    }

    private void deleteChildTables(List<ChildTable> deletedChildList) {
        ChildTableDao childTableDao = daoSession.getChildTableDao();

        for(ChildTable childTable : deletedChildList)
        {
            childTableDao.delete(childTable);
        }
    }

    private void deleteTableFields(List<TableField> deletedTableFieldList) {
        TableFieldDao tableFieldDao = daoSession.getTableFieldDao();

        for(TableField tableField : deletedTableFieldList)
        {
            tableFieldDao.delete(tableField);
        }
    }

    private void deleteTableRows(List<TableRow> deletedTableRowList) {
        TableRowDao tableRowDao = daoSession.getTableRowDao();

        for(TableRow tableRow : deletedTableRowList)
        {
            tableRowDao.delete(tableRow);
        }
    }

    private void deleteTableTemplates(List<TableTemplate> deletedTableTemplateList) {
        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();

        for(TableTemplate tableTemplate : deletedTableTemplateList)
        {
            tableTemplateDao.delete(tableTemplate);
        }

    }

    private void deleteProgram(List<Program> deletedProgramList) {
        ProgramDao programDao = daoSession.getProgramDao();

        for(Program program : deletedProgramList)
        {
            programDao.delete(program);
        }
    }

    private void deleteTermSolutions(List<TermSolution> deletedTermSolutionList) {
        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();

        for(TermSolution termSolution : deletedTermSolutionList)
        {
            termSolutionDao.delete(termSolution);
        }

    }

    private void deleteChildren(List<Child> deletedChildList) {
        ChildDao childDao = daoSession.getChildDao();

        for(Child child : deletedChildList)
        {
            childDao.delete(child);
        }
    }


}
