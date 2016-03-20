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
import com.mroza.models.charts.SimplifiedResolvedTabRow;
import com.mroza.models.queries.ResolvedTabQuery;
import org.apache.ibatis.session.SqlSession;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class KidTablesDao {

    @Inject
    private SqlSession sqlSession;

    public KidTable selectKidTableById(Integer kidTableId) {
        return sqlSession.selectOne("kidTablesMapper.selectKidTableById", kidTableId);
    }

    public List<KidTable> selectAllKidTables() {
        List<KidTable> kidTables = sqlSession.selectList("kidTablesMapper.selectAllKidTables");
        if(kidTables == null)
            return new ArrayList<>();
        return kidTables;
    }

    public List<KidTable> getKidsTablesByNote(String note) {
        List<KidTable> kidTables = sqlSession.selectList("kidTablesMapper.selectKidTablesByNote", note);
        if (kidTables == null)
            return new ArrayList<>();
        return kidTables;
    }

    public KidTable selectKidTableByIdWithEdgeTable(Integer kidTableId) {
        return sqlSession.selectOne("kidTablesMapper.selectKidTableByIdWithEdgeTable", kidTableId);
    }

    public List<KidTable> selectKidTablesByTableName(String tableName) {
        List<KidTable> kidTables = sqlSession.selectList("kidTablesMapper.selectKidTablesByTableName", tableName);
        if (kidTables == null)
            return new ArrayList<>();
        return kidTables;
    }

    public List<KidTable> selectKidTableByPeriodIdWithEdgeTableProgram(Integer periodId) {
        List<KidTable> kidTables = sqlSession.selectList("kidTablesMapper.selectKidTableByPeriodIdWithEdgeTableProgram", periodId);
        if (kidTables == null)
            return new ArrayList<>();
        return kidTables;
    }

    public List<KidTable> selectKidTableByPeriodIdWithEdgeTableRowFieldProgram(Integer periodId) {
        List<KidTable> kidTables = sqlSession.selectList("kidTablesMapper.selectKidTableByPeriodIdWithEdgeTableRowFieldProgram", periodId);
        if (kidTables == null)
            return new ArrayList<>();
        return kidTables;
    }

    public List<SimplifiedResolvedTabRow> selectResolvedFieldsForKidTab(ResolvedTabQuery query) {
        return sqlSession.selectList("kidTablesMapper.selectSimplifiedResolvedKidTab", query);
    }

    public void insertKidTable(KidTable kidTable) {
        sqlSession.insert("kidTablesMapper.insertKidTable", kidTable);
        kidTable.getResolvedFields().forEach(
                resolvedField -> sqlSession.insert("resolvedFieldsMapper.insertResolvedField", resolvedField));
    }

    public void updateKidTableIOA(KidTable kidTable) {
        sqlSession.update("kidTablesMapper.updateKidTableIOA", kidTable);
    }

    public void updateKidTable(KidTable kidTable) {
        sqlSession.update("kidTablesMapper.updateKidTable", kidTable);
    }

    public void deleteKidTableById(Integer kidTableId) {
        sqlSession.delete("kidTablesMapper.deleteKidTableById", kidTableId);
    }

    public void deleteKidTables(List<Integer> ids) {
        sqlSession.delete("kidTablesMapper.deleteKidTables", ids);
    }
}
