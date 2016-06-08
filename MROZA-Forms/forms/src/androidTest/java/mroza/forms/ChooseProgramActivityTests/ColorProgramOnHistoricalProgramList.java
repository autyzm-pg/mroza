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

package mroza.forms.ChooseProgramActivityTests;

import adapters.ChildTableListViewAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import database.*;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.R;
import mroza.forms.TestUtils.TestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ColorProgramOnHistoricalProgramList extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {

    private Child childMain;
    private DaoSession daoSession;
    private Context targetContext;

    public ColorProgramOnHistoricalProgramList() {
        super(mroza.forms.ChooseProgramActivity.class);
    }

    protected void setUp() throws Exception {
        targetContext = getInstrumentation().getTargetContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(targetContext, "mroza-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        setUpChild("AK123", childMain = new Child());

        Intent childNameActivityParameter = new Intent();
        childNameActivityParameter.putExtra("CHILD_NAME", "AK123");
        childNameActivityParameter.putExtra("CHILD_ID", (long) 1);
        setActivityIntent(childNameActivityParameter);
        setActivityInitialTouchMode(true);
    }

    private void setUpChild(String code, Child child) throws ParseException {
        child.setCode(code);
        child.setIsArchived(false);
        ChildDao childDao = daoSession.getChildDao();
        childDao.insertOrReplace(child);

        TermSolution termSolution = new TermSolution();
        Calendar c = Calendar.getInstance();
        termSolution.setChild(child);
        int dayStart = c.get(Calendar.DAY_OF_MONTH) - 10;
        int dayEnd = c.get(Calendar.DAY_OF_MONTH) -6;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date dateStart = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayStart);
        Date dateEnd = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayEnd);
        termSolution.setStartDate(dateStart);
        termSolution.setEndDate(dateEnd);
        TermSolutionDao termSolutionDao = daoSession.getTermSolutionDao();
        termSolutionDao.insertOrReplace(termSolution);

        setUpProgram(child, termSolution, "Teach letters", "A123", "C234", "Teach letters", "EMPTY");
        setUpProgram(child, termSolution, "Teach toys", "B123", "D234", "Teach teddy bear", "SAVE TEACHING");
        setUpProgram(child, termSolution, "Teach toys", "B123", "D234", "Teach teddy bear", "SAVE GENERALIZATION");
        setUpProgram(child, termSolution, "Teach letters", "A123", "C234", "Teach letters", "FINISH TEACHING");
        setUpProgram(child, termSolution, "Teach toys", "B123", "D234", "Teach teddy bear", "FINISH GENERALIZATION");

    }

    private void setUpProgram(Child child, TermSolution termSolution, String programName, String programSymbol, String tableSymbol, String tableName, String status) {

        Program program = new Program();
        program.setChild(child);
        program.setCreateDate(new Date());
        program.setDescription("DESCRIPTION");
        program.setIsFinished(false);
        program.setName(programName);
        program.setSymbol(programSymbol);

        ProgramDao programDao = daoSession.getProgramDao();
        programDao.insertOrReplace(program);

        TableTemplate tabletemplate = new TableTemplate();
        tabletemplate.setName(tableName);
        tabletemplate.setCreateDate(new Date());
        tabletemplate.setDescription("DESCRIPTION");
        tabletemplate.setIsArchived(false);
        tabletemplate.setProgram(program);

        TableTemplateDao tableTemplateDao = daoSession.getTableTemplateDao();
        tableTemplateDao.insertOrReplace(tabletemplate);

        setUpChildTable(termSolution, status, tabletemplate);
    }

    private void setUpChildTable(TermSolution termSolution, String status, TableTemplate tabletemplate) {
        ChildTable childTable = new ChildTable();

        if (status.equals("EMPTY")) {
            childTable.setTeachingFillOutDate(null);
            childTable.setGeneralizationFillOutDate(null);
            childTable.setIsGeneralizationCollected(false);
            childTable.setIsGeneralizationFinished(false);
            childTable.setIsTeachingCollected(false);
            childTable.setIsTeachingFinished(false);
        } else if (status.equals("SAVE TEACHING")) {
            childTable.setTeachingFillOutDate(new Date());
            childTable.setGeneralizationFillOutDate(null);
            childTable.setIsGeneralizationCollected(false);
            childTable.setIsGeneralizationFinished(false);
            childTable.setIsTeachingCollected(true);
            childTable.setIsTeachingFinished(false);
        } else if (status.equals("SAVE GENERALIZATION")) {
            childTable.setTeachingFillOutDate(null);
            childTable.setGeneralizationFillOutDate(new Date());
            childTable.setIsGeneralizationCollected(true);
            childTable.setIsGeneralizationFinished(false);
            childTable.setIsTeachingCollected(false);
            childTable.setIsTeachingFinished(false);
        } else if (status.equals("FINISH TEACHING")) {
            childTable.setTeachingFillOutDate(new Date());
            childTable.setGeneralizationFillOutDate(null);
            childTable.setIsGeneralizationCollected(false);
            childTable.setIsGeneralizationFinished(false);
            childTable.setIsTeachingCollected(true);
            childTable.setIsTeachingFinished(true);
        } else if (status.equals("FINISH GENERALIZATION")) {
            childTable.setTeachingFillOutDate(null);
            childTable.setGeneralizationFillOutDate(new Date());
            childTable.setIsGeneralizationCollected(true);
            childTable.setIsGeneralizationFinished(true);
            childTable.setIsTeachingCollected(false);
            childTable.setIsTeachingFinished(false);
        }

        childTable.setIsIOA(false);
        childTable.setIsPretest(false);
        childTable.setNote("NOTE");
        childTable.setTableTemplate(tabletemplate);
        childTable.setTermSolution(termSolution);

        ChildTableDao childTableDao = daoSession.getChildTableDao();
        childTableDao.insertOrReplace(childTable);
    }

    private Date getActualDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date dateNow = null;
        try {
            dateNow = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateNow;
    }

    private List<Program> getActualProgramsForChild(Child child) throws ParseException {

        Date dateNow = getActualDate();

        List<Program> programs = new ArrayList<Program>();
        for (Program program : childMain.getProgramList()) {
            for (TableTemplate table : program.getTableTemplateList()) {
                for (ChildTable childTable : table.getChildTableList()) {
                    TermSolution termSolution = childTable.getTermSolution();
                    if (termSolution.getChild().getId().equals(childMain.getId())) {
                        if ((termSolution.getEndDate().compareTo(dateNow) >= 0) && (termSolution.getStartDate().compareTo(dateNow) <= 0)) {
                            programs.add(program);
                        }
                    }
                }
            }
        }

        return programs;
    }

    public void testColorsOfProgramsOfTheListListShouldBeColoredCorrectly() throws ParseException {
        Activity mainActivity = this.getActivity();
        ListView listActivities = (ListView) mainActivity.findViewById(R.id.childTable_list);
        ChildTableListViewAdapter adapter = (ChildTableListViewAdapter) listActivities.getAdapter();

        List<Program> childPrograms = getActualProgramsForChild(childMain);

        for (Program program : childPrograms) {
            ColorDrawable color = (ColorDrawable) listActivities.getChildAt(convertProgramIdToPositionOnListView(program)).getBackground();
            int colorId = color.getColor();
            assertEquals("ColorsShouldBeTheSame ", getColorForProgram(program), colorId);

        }

    }

    private int convertProgramIdToPositionOnListView(Program program) {
        return program.getId().intValue() - 1;
    }

    private int getColorForProgram(Program program) {

        ChildTable childTable = program.getTableTemplateList().get(0).getChildTableList().get(0);
        if (childTable.getIsTeachingCollected() && childTable.getIsGeneralizationCollected()) {

            if (childTable.getTeachingFillOutDate() == null && childTable.getGeneralizationFillOutDate() == null) {
                return this.getActivity().getResources().getColor(R.color.colorNotStarted);
            }
            if (childTable.getIsTeachingFinished() && childTable.getIsGeneralizationFinished()) {
                return this.getActivity().getResources().getColor(R.color.colorFinished);
            } else {
                return this.getActivity().getResources().getColor(R.color.colorStarted);
            }
        } else if (childTable.getIsTeachingCollected()) {

            if (childTable.getTeachingFillOutDate() == null) {
                return this.getActivity().getResources().getColor(R.color.colorNotStarted);
            }
            if (childTable.getIsTeachingFinished()) {
                return this.getActivity().getResources().getColor(R.color.colorFinished);
            } else {
                return this.getActivity().getResources().getColor(R.color.colorStarted);
            }
        } else if (childTable.getIsGeneralizationCollected()) {

            if (childTable.getGeneralizationFillOutDate() == null) {
                return this.getActivity().getResources().getColor(R.color.colorNotStarted);
            }
            if (childTable.getIsGeneralizationFinished()) {
                return this.getActivity().getResources().getColor(R.color.colorFinished);
            } else {
                return this.getActivity().getResources().getColor(R.color.colorStarted);
            }
        } else {
            return this.getActivity().getResources().getColor(R.color.colorNotStarted);
        }
    }
}
