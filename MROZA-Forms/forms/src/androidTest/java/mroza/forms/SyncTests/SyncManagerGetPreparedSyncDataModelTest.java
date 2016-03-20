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

package mroza.forms.SyncTests;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import database.*;
import mroza.forms.ChooseKidActivity;
import mroza.forms.TestUtils.CompareUtils;
import mroza.forms.TestUtils.TestUtils;
import syncmodels.SendSyncModel;
import syncmodels.SyncManager;
import syncmodels.TransferChildTable;
import syncmodels.TransferTableFieldFilling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SyncManagerGetPreparedSyncDataModelTest extends ActivityInstrumentationTestCase2<ChooseKidActivity> {


    private SyncManager syncManager;
    private SendSyncModel expectedSyncModel;
    private List<Child> childs;
    private List<Program> programs;
    private List<ChildTable> childTables;
    private List<TableFieldFilling> tableFieldFillings;

    public SyncManagerGetPreparedSyncDataModelTest() {
        super(ChooseKidActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Context targetContext = getInstrumentation().getTargetContext();
        syncManager = new SyncManager(targetContext);
        Calendar c = Calendar.getInstance();
        int dayNumber = c.get(Calendar.DAY_OF_MONTH) - 2;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = formatter.parse(c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + dayNumber);
        TestUtils.cleanUpDatabase(targetContext);
        syncManager.setSyncDate(date);

        this.childs = TestUtils.setUpChildren(targetContext, 1, "CODE.");
        this.programs = TestUtils.setUpPrograms(targetContext, this.childs, 1, "UNCHANGED.Name", "UNCHANGED.Symbol");
        this.childTables = getAllChildTable(this.programs);
        this.tableFieldFillings = getAllTableFieldFillings(this.programs);
        expectedSyncModel = setUpSyncModel(this.childTables, this.tableFieldFillings);
    }

    private SendSyncModel setUpSyncModel(List<ChildTable> childTables, List<TableFieldFilling> tableFieldFillings) {
        SendSyncModel syncModel = new SendSyncModel();
        List<TransferChildTable> transferChildTableList =  new ArrayList<>();
        List<TransferTableFieldFilling> transferTableFieldFillingList =  new ArrayList<>();
        for(ChildTable childTable : childTables)
            transferChildTableList.add(TransferChildTable.transferObjectFromServerModel(childTable));
        for(TableFieldFilling tableFieldFilling : tableFieldFillings)
            transferTableFieldFillingList.add(TransferTableFieldFilling.transferObjectFromServerModel(tableFieldFilling));
        syncModel.setTransferChildTableList(transferChildTableList);
        syncModel.setTransferTableFieldFillingList(transferTableFieldFillingList);
        return syncModel;
    }

    private List<ChildTable> getAllChildTable(List<Program> programs) {
        List<ChildTable> childTableList = new ArrayList<>();
        for(Program program : programs)
            for(TableTemplate tableTemplate : program.getTableTemplateList())
                for(ChildTable childTable : tableTemplate.getChildTableList())
                            childTableList.add(childTable);
        return childTableList;
    }

    private List<TableFieldFilling> getAllTableFieldFillings(List<Program> programs) {
        List<TableFieldFilling> tableFielFillingdList = new ArrayList<>();
        for(Program program : programs)
            for(TableTemplate tableTemplate : program.getTableTemplateList())
                for(TableRow tableRow : tableTemplate.getTableRowList())
                    for(TableField tableField : tableRow.getTableFieldList())
                        for(TableFieldFilling tableFieldFilling : tableField.getTableFieldFillingList())
                            tableFielFillingdList.add(tableFieldFilling);
        return tableFielFillingdList;
    }

    public void testGetSendSyncModel(){

        SendSyncModel sendSyncModel = syncManager.getSendSyncModel();
        for(TransferChildTable transferChildTable : sendSyncModel.getTransferChildTableList())
        {
            Boolean found = false;
            for(TransferChildTable expectedTranferChildTable : expectedSyncModel.getTransferChildTableList()) {
                if(CompareUtils.areTwoTransferChildTableTheSame(transferChildTable, expectedTranferChildTable))
                    found = true;

            }
            if(!found)
            {
                fail("Transfered childs are diffrent then expected");
            }
        }

        Boolean found = false;
        for(TransferTableFieldFilling transferTableFieldFilling : sendSyncModel.getTransferTableFieldFillingList())
        {
            found = false;
            for(TransferTableFieldFilling expectedTranferFieldFilling : expectedSyncModel.getTransferTableFieldFillingList()) {
                if(CompareUtils.areTwoTransferFieldFillingsTheSame(transferTableFieldFilling, expectedTranferFieldFilling))
                    found = true;

            }
            if(!found)
            {
                fail("Transfered tableFieldFillinga are diffrent then expected");
            }
        }
        if(!found)
        {
            fail("Transfered tableFieldFillinga are diffrent then expected");
        }


    }
}
