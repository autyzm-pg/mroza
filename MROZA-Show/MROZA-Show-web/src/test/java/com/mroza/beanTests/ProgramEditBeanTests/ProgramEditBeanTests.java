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
package com.mroza.beanTests.ProgramEditBeanTests;


import com.mroza.models.*;
import com.mroza.utils.DatabaseUtils;
import com.mroza.utils.Utils;
import com.mroza.viewbeans.ProgramEditBean;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.component.paginator.PaginatorElementRenderer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

public class ProgramEditBeanTests {

    private ProgramEditBean programEditBean;
    private Table assignedTable;
    private Program program;

    @Before
    public void setup() {
        prepareAssignedTable();
        this.programEditBean = mock(ProgramEditBean.class);
    }

    @After
    public void after() {
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
    }

    @Test
    public void createAllAssignedKidTableWithResolvedFieldsAfterProgramsTableWasEditedTest() {
        when(this.programEditBean.validateTable(this.assignedTable)).thenReturn(Boolean.TRUE);
        this.programEditBean.save(this.assignedTable);

        DatabaseUtils databaseUtils = new DatabaseUtils();
        List<KidTable> kidTables = databaseUtils.getKidTablesForTable(this.assignedTable.getId());
        assertFalse("Assigned table should have kidTable after it was edited", kidTables.isEmpty());
        assertTrue("Assigned table should have the same number of kidTables as before edition", kidTables.size() == 2);
        kidTables.forEach(kidTable ->
                assertFalse("Every kidTable for assigned table should have resolvedFields", kidTable.getResolvedFields().isEmpty()));
        kidTables.forEach(kidTable ->
                kidTable.getResolvedFields().forEach((resolvedField) ->
                        assertTrue("Every resolvedField should have fieldId set", resolvedField.getTableFieldId() != null)));

    }


    private void prepareAssignedTable() {
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.cleanUpDatabase();
        Kid kid = databaseUtils.setUpKid("CODE");
        program = databaseUtils.setUpProgram("SYMBOL", "NAME", "DESCRIPTION", kid);
        List<String> rowNames = new ArrayList<String>(){{ add(new String("ROW_1")); add(new String("ROW_2"));}};
        this.assignedTable = databaseUtils.setUpTableWithRows("TABLE_NAME", rowNames, "DESCRIPTION", 2, 2, program);

        Date startDate = Utils.getDateFromNow(-2);
        Date endDate = Utils.getDateFromNow(2);
        Period period = databaseUtils.setUpPeriod(startDate, endDate, kid);

        Date startDateFuture = Utils.getDateFromNow(3);
        Date endDateFuture = Utils.getDateFromNow(5);
        Period periodFuture = databaseUtils.setUpPeriod(startDateFuture, endDateFuture, kid);

        List<KidTable> kidTables = new ArrayList<>();
        kidTables.add(databaseUtils.setUpKidTable(this.assignedTable, period));
        kidTables.add(databaseUtils.setUpKidTable(this.assignedTable, periodFuture));
    }
}
