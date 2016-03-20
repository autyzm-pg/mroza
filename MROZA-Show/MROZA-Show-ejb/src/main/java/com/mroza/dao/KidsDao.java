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

import com.mroza.models.Kid;
import com.mroza.models.Program;
import org.apache.ibatis.session.SqlSession;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KidsDao {

    @Inject
    private SqlSession sqlSession;

    public  List<Kid> selectAllKids() {
        List<Kid> kids = sqlSession.selectList("kidsMapper.selectAllKids");
        if(kids == null)
            return new ArrayList<>();
        return kids;
    }

    public Kid selectKidWithEdgesProgramsAndPeriods(int kidId) {
        return sqlSession.selectOne("kidsMapper.selectKidWithEdgesProgramsAndPeriods", kidId);
    }

    public void insertKid(Kid kid) {
        sqlSession.insert("kidsMapper.insertKid", kid);
    }

    // TODO: to fix -> bulk delete
    public void deleteKids(List<Kid> kids) {
        for(Kid kid : kids) {
            sqlSession.delete("kidsMapper.deleteKid", kid);
        }
    }
}
