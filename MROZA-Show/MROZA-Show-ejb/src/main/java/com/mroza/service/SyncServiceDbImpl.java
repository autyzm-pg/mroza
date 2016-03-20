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
import com.mroza.models.transfermodels.*;
import com.mroza.qualifiers.SyncServiceImpl;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SyncServiceImpl
public class SyncServiceDbImpl implements Serializable, SyncService {

    @Inject
    private KidsDao kidsDao;

    @Inject
    private KidTablesDao kidTablesDao;

    @Inject
    private PeriodsDao periodsDao;

    @Inject
    private ProgramsDao programsDao;

    @Inject
    private ResolvedFieldsDao resolvedFieldsDao;

    @Inject
    private TableFieldsDao tableFieldsDao;

    @Inject
    private TableRowsDao tableRowsDao;

    @Inject
    private TablesDao tablesDao;

    @Inject
    private DeleteHistoryDao deleteHistoryDao;


    @Override
    public SendSyncModel getAllData() {
        List<Kid> kids = kidsDao.selectAllKids();
        List<Program> programs = programsDao.selectAllPrograms();
        List<Period> periods = periodsDao.selectAllPeriods();
        List<Table> tables = tablesDao.selectAllTables();
        List<KidTable> kidTables = kidTablesDao.selectAllKidTables();
        List<TableRow> tableRows = tableRowsDao.selectAllTableRows();
        List<TableField> tableFields = tableFieldsDao.selectAllTableFields();
        List<ResolvedField> resolvedFields = resolvedFieldsDao.selectAllResolvedFields();

        List<TransferChild> transferChildren = kids.stream().map(
                TransferChild::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferProgram> transferPrograms = programs.stream().map(
                TransferProgram::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTermSolution> transferTermSolutions = periods.stream().map(
                TransferTermSolution::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTableTemplate> transferTableTemplates = tables.stream().map(
                TransferTableTemplate::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferChildTable> transferChildTables = kidTables.stream().map(
                TransferChildTable::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTableRow> transferTableRows = tableRows.stream().map(
                TransferTableRow::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTableField> transferTableFields = tableFields.stream().map(
                TransferTableField::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTableFieldFilling> transferTableFieldFillings = resolvedFields.stream().map(
                TransferTableFieldFilling::transferObjectFromServerModel).collect(Collectors.toList());

        SendSyncModel syncModel = new SendSyncModel();
        syncModel.setChildList(transferChildren);
        syncModel.setProgramList(transferPrograms);
        syncModel.setTermSolutionList(transferTermSolutions);
        syncModel.setTableTemplateList(transferTableTemplates);
        syncModel.setChildTableList(transferChildTables);
        syncModel.setTableRowList(transferTableRows);
        syncModel.setTableFieldList(transferTableFields);
        syncModel.setTableFieldFillingList(transferTableFieldFillings);

        return syncModel;
    }

    @Override
    @Transactional
    public void updateModelWithDataFromAndroid(ReceiveSyncModel receiveSyncModel) {
        List<TransferChildTable> transferChildTables = receiveSyncModel.getTransferChildTableList();
        List<TransferTableFieldFilling> transferTableFieldFillings = receiveSyncModel.getTransferTableFieldFillingList();
        List<KidTable> kidTables = transferChildTables.stream().map(
                TransferChildTable::serverModelFromTransferObject).collect(Collectors.toList());
        List<ResolvedField> resolvedFields = transferTableFieldFillings.stream().map(
                TransferTableFieldFilling::serverModelFromTransferObject).collect(Collectors.toList());
        kidTables.forEach(this::updateKidTableData);
        resolvedFields.forEach(this::updateResolvedField);
    }

    private void updateKidTableData(KidTable kidTable) {
        KidTable kidTableToMerge = kidTablesDao.selectKidTableById(kidTable.getId());
        if(kidTableToMerge != null) {
//            TODO: add modify data checking
            kidTablesDao.updateKidTable(kidTable);
        }
    }

    private void updateResolvedField(ResolvedField resolvedField) {
        ResolvedField resolvedFieldToMerge = resolvedFieldsDao.selectResolvedFieldById(resolvedField.getId());
        if(resolvedFieldToMerge == null)
            resolvedFieldsDao.insertResolvedField(resolvedField);
        else {
//            TODO: add modify data checking
            resolvedFieldsDao.updateResolvedField(resolvedField);
        }
    }

    @Override
    public SendSyncModel getDataAfterLastSync(Date lastSyncDatetime) {
//        @TODO return only modified data
        List<Kid> kids = kidsDao.selectAllKids();
        List<Program> programs = programsDao.selectAllPrograms();
        List<Period> periods = periodsDao.selectAllPeriods();
        List<Table> tables = tablesDao.selectAllTables();
        List<KidTable> kidTables = kidTablesDao.selectAllKidTables();
        List<TableRow> tableRows = tableRowsDao.selectAllTableRows();
        List<TableField> tableFields = tableFieldsDao.selectAllTableFields();
        List<ResolvedField> resolvedFields = resolvedFieldsDao.selectAllResolvedFields();

        List<TransferChild> transferChildren = kids.stream().map(
                TransferChild::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferProgram> transferPrograms = programs.stream().map(
                TransferProgram::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTermSolution> transferTermSolutions = periods.stream().map(
                TransferTermSolution::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTableTemplate> transferTableTemplates = tables.stream().map(
                TransferTableTemplate::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferChildTable> transferChildTables = kidTables.stream().map(
                TransferChildTable::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTableRow> transferTableRows = tableRows.stream().map(
                TransferTableRow::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTableField> transferTableFields = tableFields.stream().map(
                TransferTableField::transferObjectFromServerModel).collect(Collectors.toList());
        List<TransferTableFieldFilling> transferTableFieldFillings = resolvedFields.stream().map(
                TransferTableFieldFilling::transferObjectFromServerModel).collect(Collectors.toList());

        SendSyncModel syncModel = new SendSyncModel();
        syncModel.setChildList(transferChildren);
        syncModel.setProgramList(transferPrograms);
        syncModel.setTermSolutionList(transferTermSolutions);
        syncModel.setTableTemplateList(transferTableTemplates);
        syncModel.setChildTableList(transferChildTables);
        syncModel.setTableRowList(transferTableRows);
        syncModel.setTableFieldList(transferTableFields);
        syncModel.setTableFieldFillingList(transferTableFieldFillings);

        syncModel.setDeletedChildList(deleteHistoryDao.selectDeletedChildrenAfterDate(lastSyncDatetime));
        syncModel.setDeletedProgramList(deleteHistoryDao.selectDeletedProgramsAfterDate(lastSyncDatetime));
        syncModel.setDeletedTermSolutionList(deleteHistoryDao.selectDeletedTermSolutionAfterDate(lastSyncDatetime));
        syncModel.setDeletedTableTemplateList(deleteHistoryDao.selectDeletedTableTemplateAfterDate(lastSyncDatetime));
        syncModel.setDeletedChildTableList(deleteHistoryDao.selectDeletedChildTablesAfterDate(lastSyncDatetime));
        syncModel.setDeletedTableRowList(deleteHistoryDao.selectDeletedTableRowsAfterDate(lastSyncDatetime));
        syncModel.setDeletedTableFieldList(deleteHistoryDao.selectDeletedTableFieldsAfterDate(lastSyncDatetime));
        syncModel.setDeletedTableFieldFillingList(deleteHistoryDao.selectDeletedTableFieldFillingsAfterDate(lastSyncDatetime));

        return syncModel;
    }
}
