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

package mroza.forms.ProgramActivityTests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import database.*;
import mroza.forms.ProgramActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProgramActivityRestartTableIOATest extends ActivityInstrumentationTestCase2<ProgramActivity> {

    private Context targetContext;

    public ProgramActivityRestartTableIOATest() {
        super(ProgramActivity.class);
    }

    DaoMaster daoMaster;
    Child child;
    ChildTable childTable;
    Intent intent;

    final int rowsNumber = 3;
    final int teachingNumber = 2;
    final int generalizationNumber = 1;

    protected void setUp() throws Exception {
        super.setUp();

        targetContext = getInstrumentation().getTargetContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(targetContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        setUpChild("AK123", child = new Child((long) 1), childTable = new ChildTable((long) 1));

        intent = new Intent();
        intent.putExtra("CHILD_TABLE_ID", (long) 1);
        intent.putExtra("CHILD_ID", (long) 1);
        setActivityIntent(intent);
    }

    private void setUpChild(String code, Child child, ChildTable childTable) throws ParseException {
        DaoSession daoSession = daoMaster.newSession();
        child.setCode(code);
        child.setIsArchived(false);
        ChildDao childDao = daoSession.getChildDao();
        childDao.insertOrReplace(child);

        TermSolution termSolution = new TermSolution();
        Calendar c = Calendar.getInstance();
        termSolution.setChild(child);
        int dayStart = c.get(Calendar.DAY_OF_MONTH) - 5;
        int dayEnd = c.get(Calendar.DAY_OF_MONTH) + 5;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date dateStart = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayStart);
        Date dateEnd = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayEnd);
        termSolution.setStartDate(dateStart);
        termSolution.setEndDate(dateEnd);
        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();
        termSolutionDao.insertOrReplace(termSolution);

        setUpProgram(child, childTable, termSolution, "A123", "Uczenie literek", "C234", "Uczenie liczenia");
    }

    private void setUpProgram(Child child, ChildTable childTable, TermSolution termSolution, String programSymbol, String programName, String tableSymbol, String tableName) {
        DaoSession daoSession = daoMaster.newSession();
        ProgramDao programDao = daoSession.getProgramDao();
        Program program = new Program();
        program.setChild(child);
        program.setCreateDate(new Date());
        program.setDescription("Opis dlugi");
        program.setIsFinished(false);
        program.setName(programName);
        program.setSymbol(programSymbol);
        programDao.insertOrReplace(program);

        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        TableTemplate tabletemplate = new TableTemplate();
        tabletemplate.setName(tableName);
        tabletemplate.setCreateDate(new Date());
        tabletemplate.setDescription("Krotki opis");
        tabletemplate.setIsArchived(false);
        tabletemplate.setProgram(program);
        tableTemplateDao.insertOrReplace(tabletemplate);

        ChildTableDao childTableDao = daoSession.getChildTableDao();
        childTable.setTeachingFillOutDate(new Date());
        childTable.setGeneralizationFillOutDate(new Date());
        childTable.setIsGeneralizationCollected(true);
        childTable.setIsGeneralizationFinished(true);
        childTable.setIsTeachingCollected(true);
        childTable.setIsTeachingFinished(true);
        childTable.setIsIOA(true);
        childTable.setIsPretest(false);
        childTable.setNote("Jest ok");
        childTable.setTableTemplate(tabletemplate);
        childTable.setTermSolution(termSolution);
        childTableDao.insertOrReplace(childTable);

        setUpTableContent(tabletemplate, childTable);
    }

    private void setUpTableContent(TableTemplate tableTemplate, ChildTable childTable) {
        DaoSession daoSession = daoMaster.newSession();
        TableRowDao tableRowDao = daoSession.getTableRowDao();

        for (int i = 0; i < rowsNumber; i++) {
            TableRow tableRow = new TableRow();
            tableRow.setValue("Row " + i);
            tableRow.setInOrder(i);
            tableRow.setTableTemplate(tableTemplate);
            tableRowDao.insertOrReplace(tableRow);
        }

        List<TableRow> rows = tableRowDao.loadAll();
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

                TableFieldFilling filling = new TableFieldFilling();
                filling.setTableField(field);
                filling.setChildTable(childTable);
                filling.setContent("passed");
                tableFieldFillingDao.insertOrReplace(filling);
            }

        }
    }


    public void testRestartTable() throws InterruptedException {
        Activity mainActivity = this.getActivity();
        Button buttonRestart = (Button) mainActivity.findViewById(R.id.buttonRestart);

        assertEquals("Table is IOA, restart button should be disabled", buttonRestart.isEnabled(), false);
    }

}
