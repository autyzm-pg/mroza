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

package com.mroza.service;

import com.mroza.dao.*;
import com.mroza.interfaces.SyncService;
import com.mroza.models.*;
import com.mroza.models.transfermodels.ReceiveSyncModel;
import com.mroza.models.transfermodels.SendSyncModel;
import com.mroza.models.transfermodels.TransferChildTable;
import com.mroza.models.transfermodels.TransferTableFieldFilling;
import com.mroza.utils.ReflectionWrapper;
import com.mroza.utils.Utils;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SyncServiceDbImplTest {

    private static KidsDao kidsDao;
    private static List<Kid> exampleKids;
    private static SqlSession sqlSession;
    private static SyncService syncService;
    private static KidTablesDao kidTablesDao;
    private static TablesDao tablesDao;
    private static ResolvedFieldsDao resolvedFieldsDao;
    private static PeriodsDao periodsDao;
    private static ProgramsDao programsDao;
    private static TableFieldsDao tableFieldsDao;
    private static TableRowsDao tableRowsDao;
    private static DeleteHistoryDao deleteHistoryDao;

    private static Date mockDate;



    @Before
    public void setup() {
        mockDate = new Date();
        exampleKids = new ArrayList<>();
        sqlSession = Utils.getSqlSession();


        kidsDao = new KidsDao();
        kidTablesDao = new KidTablesDao();
        periodsDao = new PeriodsDao();
        programsDao = new ProgramsDao();
        resolvedFieldsDao = new ResolvedFieldsDao();
        tableFieldsDao = new TableFieldsDao();
        tableRowsDao = new TableRowsDao();
        tablesDao = new TablesDao();
        deleteHistoryDao = new DeleteHistoryDao();

        syncService = new SyncServiceDbImpl();

        ReflectionWrapper.setPrivateField(kidsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(kidTablesDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(tablesDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(resolvedFieldsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(periodsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(tableFieldsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(tableRowsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(programsDao, "sqlSession", sqlSession);
        ReflectionWrapper.setPrivateField(deleteHistoryDao, "sqlSession", sqlSession);

        ReflectionWrapper.setPrivateField(syncService, "kidTablesDao", kidTablesDao);
        ReflectionWrapper.setPrivateField(syncService, "tablesDao", tablesDao);
        ReflectionWrapper.setPrivateField(syncService, "resolvedFieldsDao", resolvedFieldsDao);
        ReflectionWrapper.setPrivateField(syncService, "periodsDao", periodsDao);
        ReflectionWrapper.setPrivateField(syncService, "kidsDao", kidsDao);
        ReflectionWrapper.setPrivateField(syncService, "programsDao", programsDao);
        ReflectionWrapper.setPrivateField(syncService, "tableFieldsDao", tableFieldsDao);
        ReflectionWrapper.setPrivateField(syncService, "tableRowsDao", tableRowsDao);
        ReflectionWrapper.setPrivateField(syncService, "deleteHistoryDao", deleteHistoryDao);
        Utils.initKidDeeply(exampleKids, "QWE");
    }

    @After
    public void teardown() {
        Utils.kidsInitializedTearDown(exampleKids);
    }

    @Test
    public void getAllKidsTest() {
        SendSyncModel syncModel = syncService.getAllData();
        Assert.assertEquals(1, syncModel.getChildList().size());
        Assert.assertEquals(3, syncModel.getProgramList().size());
        Assert.assertEquals(2, syncModel.getTermSolutionList().size());
        Assert.assertEquals(3, syncModel.getTableTemplateList().size());
        Assert.assertEquals(2, syncModel.getChildTableList().size());
        Assert.assertEquals(9, syncModel.getTableRowList().size());
        Assert.assertEquals(49, syncModel.getTableFieldList().size());
        Assert.assertEquals(43, syncModel.getTableFieldFillingList().size());
    }

    @Test
    public void updateModelWithDataFromAndroidTest() {
        Table updateTable = exampleKids.get(0).getPrograms().get(0).getTables().get(0);
        Period updatePeriod = exampleKids.get(0).getPeriods().get(0);
        KidTable kidTableToUpdate = exampleKids.get(0).getPrograms().get(0).getTables().get(0).getKidTables().get(0);
        Date mockModDatetime = new Date();

        KidTable updateKidTable = new KidTable(true, true, updateTable, updatePeriod);
        updateKidTable.setId(kidTableToUpdate.getId());
        updateKidTable.setTableId(updateTable.getId());
        updateKidTable.setPeriodId(updatePeriod.getId());
        updateKidTable.setNote("Nowa notatka");
        updateKidTable.setLastModDate(mockModDatetime);
        List<ResolvedField> updateResolvedFields = updateKidTable.getResolvedFields();
        updateResolvedFields.get(0).setValue("OK");
        updateResolvedFields.get(0).setId(kidTableToUpdate.getResolvedFields().get(0).getId());
        updateResolvedFields.get(0).setTableFieldId(kidTableToUpdate.getResolvedFields().get(0).getTableField().getId());
        updateResolvedFields.get(0).setKidTableId(kidTableToUpdate.getId());
        updateResolvedFields.get(1).setValue("NOK");
        updateResolvedFields.get(1).setId(kidTableToUpdate.getResolvedFields().get(1).getId());
        updateResolvedFields.get(1).setTableFieldId(kidTableToUpdate.getResolvedFields().get(1).getTableField().getId());
        updateResolvedFields.get(1).setKidTableId(kidTableToUpdate.getId());

        ReceiveSyncModel receiveSyncModel = new ReceiveSyncModel();
        receiveSyncModel.getTransferChildTableList().add(TransferChildTable.transferObjectFromServerModel(updateKidTable));
        receiveSyncModel.getTransferTableFieldFillingList().add(TransferTableFieldFilling.transferObjectFromServerModel(updateResolvedFields.get(0)));
        receiveSyncModel.getTransferTableFieldFillingList().add(TransferTableFieldFilling.transferObjectFromServerModel(updateResolvedFields.get(1)));

        syncService.updateModelWithDataFromAndroid(receiveSyncModel);
        sqlSession.commit();

        Assert.assertEquals("Nowa notatka", kidTablesDao.selectKidTableById(kidTableToUpdate.getId()).getNote());
        Assert.assertEquals(true, kidTablesDao.selectKidTableById(kidTableToUpdate.getId()).isCollectingLearning());
        Assert.assertEquals(true, kidTablesDao.selectKidTableById(kidTableToUpdate.getId()).isCollectingGeneralization());
        Assert.assertEquals(mockModDatetime, kidTablesDao.selectKidTableById(kidTableToUpdate.getId()).getLastModDate());
        Assert.assertEquals(2, kidTablesDao.selectAllKidTables().size());

        Assert.assertEquals("OK", resolvedFieldsDao.selectResolvedFieldById(updateResolvedFields.get(0).getId()).getValue());
        Assert.assertEquals(kidTableToUpdate.getId(), resolvedFieldsDao.selectResolvedFieldById(updateResolvedFields.get(0).getId()).getKidTableId());

    }

    @Test
    public void getDataAfterLastSyncGettingDeleteHistoryTest() {
        kidsDao.deleteKids(exampleKids);
        SendSyncModel sendSyncModel = syncService.getDataAfterLastSync(mockDate);
        Assert.assertEquals(1, sendSyncModel.getDeletedChildList().size());
        Assert.assertEquals(3, sendSyncModel.getDeletedProgramList().size());
        Assert.assertEquals(2, sendSyncModel.getDeletedTermSolutionList().size());
        Assert.assertEquals(3, sendSyncModel.getDeletedTableTemplateList().size());
        Assert.assertEquals(2, sendSyncModel.getDeletedChildTableList().size());
        Assert.assertEquals(9, sendSyncModel.getDeletedTableRowList().size());
        Assert.assertEquals(49, sendSyncModel.getDeletedTableFieldList().size());
        Assert.assertEquals(43, sendSyncModel.getDeletedTableFieldFillingList().size());
        exampleKids = new ArrayList<>();
    }

    @Test
    public void getDataAfterLastSyncTest() {
        SendSyncModel sendSyncModel = syncService.getDataAfterLastSync(mockDate);
        Assert.assertEquals(1, sendSyncModel.getChildList().size());
        Assert.assertEquals(3, sendSyncModel.getProgramList().size());
        Assert.assertEquals(2, sendSyncModel.getTermSolutionList().size());
        Assert.assertEquals(3, sendSyncModel.getTableTemplateList().size());
        Assert.assertEquals(2, sendSyncModel.getChildTableList().size());
        Assert.assertEquals(9, sendSyncModel.getTableRowList().size());
        Assert.assertEquals(49, sendSyncModel.getTableFieldList().size());
        Assert.assertEquals(43, sendSyncModel.getTableFieldFillingList().size());
        Assert.assertEquals(0, sendSyncModel.getDeletedChildList().size());
        Assert.assertEquals(0, sendSyncModel.getDeletedProgramList().size());
        Assert.assertEquals(0, sendSyncModel.getDeletedTermSolutionList().size());
        Assert.assertEquals(0, sendSyncModel.getDeletedTableTemplateList().size());
        Assert.assertEquals(0, sendSyncModel.getDeletedChildTableList().size());
        Assert.assertEquals(0, sendSyncModel.getDeletedTableRowList().size());
        Assert.assertEquals(0, sendSyncModel.getDeletedTableFieldList().size());
        Assert.assertEquals(0, sendSyncModel.getDeletedTableFieldFillingList().size());
    }



}
