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

package com.mroza.dao;

import com.mroza.models.transfermodels.*;
import org.apache.ibatis.session.SqlSession;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeleteHistoryDao {

    @Inject
    private SqlSession sqlSession;

    public List<TransferChild> selectDeletedChildrenAfterDate(Date limitingDate) {
        List<TransferChild> deletedChildren = sqlSession.selectList("deleteHistoryMapper.selectDeletedChildrenAfterDate", limitingDate);
        if(deletedChildren == null)
            return new ArrayList<>();
        return deletedChildren;
    }

    public List<TransferChildTable> selectDeletedChildTablesAfterDate(Date limitingDate) {
        List<TransferChildTable> deletedChildTables = sqlSession.selectList("deleteHistoryMapper.selectDeletedChildTablesAfterDate", limitingDate);
        if(deletedChildTables == null)
            return new ArrayList<>();
        return deletedChildTables;
    }

    public List<TransferProgram> selectDeletedProgramsAfterDate(Date limitingDate) {
        List<TransferProgram> deletedPrograms = sqlSession.selectList("deleteHistoryMapper.selectDeletedProgramsAfterDate", limitingDate);
        if(deletedPrograms == null)
            return new ArrayList<>();
        return deletedPrograms;
    }

    public List<TransferTableField> selectDeletedTableFieldsAfterDate(Date limitingDate) {
        List<TransferTableField> deletedTableFields = sqlSession.selectList("deleteHistoryMapper.selectDeletedTableFieldsAfterDate", limitingDate);
        if(deletedTableFields == null)
            return new ArrayList<>();
        return deletedTableFields;
    }

    public List<TransferTableFieldFilling> selectDeletedTableFieldFillingsAfterDate(Date limitingDate) {
        List<TransferTableFieldFilling> deletedTableFieldFillings = sqlSession.selectList("deleteHistoryMapper.selectDeletedTableFieldFillingsAfterDate", limitingDate);
        if(deletedTableFieldFillings == null)
            return new ArrayList<>();
        return deletedTableFieldFillings;
    }

    public List<TransferTableRow> selectDeletedTableRowsAfterDate(Date limitingDate) {
        List<TransferTableRow> deletedTableRows = sqlSession.selectList("deleteHistoryMapper.selectDeletedTableRowsAfterDate", limitingDate);
        if(deletedTableRows == null)
            return new ArrayList<>();
        return deletedTableRows;
    }

    public List<TransferTableTemplate> selectDeletedTableTemplateAfterDate(Date limitingDate) {
        List<TransferTableTemplate> deletedTableTemplate = sqlSession.selectList("deleteHistoryMapper.selectDeletedTableTemplateAfterDate", limitingDate);
        if(deletedTableTemplate == null)
            return new ArrayList<>();
        return deletedTableTemplate;
    }

    public List<TransferTermSolution> selectDeletedTermSolutionAfterDate(Date limitingDate) {
        List<TransferTermSolution> deletedTermSolution = sqlSession.selectList("deleteHistoryMapper.selectDeletedTermSolutionAfterDate", limitingDate);
        if(deletedTermSolution == null)
            return new ArrayList<>();
        return deletedTermSolution;
    }

    public void clearDeleteHistory() {
        sqlSession.delete("deleteHistoryMapper.clearDeleteHistory");
    }

}
