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

import com.mroza.models.KidTable;
import com.mroza.models.ResolvedField;
import org.apache.ibatis.session.SqlSession;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class ResolvedFieldsDao {

    @Inject
    private SqlSession sqlSession;

    public ResolvedField selectResolvedFieldById(Integer resolvedFieldId) {
        return sqlSession.selectOne("resolvedFieldsMapper.selectResolvedFieldById", resolvedFieldId);
    }

    public List<ResolvedField> selectAllResolvedFields() {
        List<ResolvedField> resolvedFields = sqlSession.selectList("resolvedFieldsMapper.selectAllResolvedFields");
        if(resolvedFields == null)
            return new ArrayList<>();
        return resolvedFields;
    }

    public List<ResolvedField> selectNonEmptyResolvedFieldsByKidTableId(Integer kidTableId) {
        return selectNonEmptyResolvedFields(kidTableId, "resolvedFieldsMapper.selectNonEmptyResolvedFieldsByKidTableId");
    }

    public List<ResolvedField> selectNonEmptyResolvedFieldsByTableId(int tableId) {
        return selectNonEmptyResolvedFields(tableId, "resolvedFieldsMapper.selectNonEmptyResolvedFieldsByTableId");
    }

    private List<ResolvedField> selectNonEmptyResolvedFields(int id, String select) {
        List<ResolvedField> resolvedFields = sqlSession.selectList(select, id);
        if (resolvedFields == null)
            return new ArrayList<>();
        return resolvedFields;
    }

    public void insertResolvedField(ResolvedField resolvedField) {
        sqlSession.insert("resolvedFieldsMapper.insertResolvedField", resolvedField);
    }

    public void updateResolvedField(ResolvedField resolvedField) {
        sqlSession.update("resolvedFieldsMapper.updateResolvedField", resolvedField);
    }

    public void deleteResolvedFieldsByKidTable(KidTable kidTable) {
        sqlSession.delete("resolvedFieldsMapper.deleteResolvedFieldsByKidTableId", kidTable.getId());
    }
}
