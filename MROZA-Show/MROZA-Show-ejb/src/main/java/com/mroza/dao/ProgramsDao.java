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

import com.mroza.models.Program;
import com.mroza.models.charts.ResolvedTabData;
import org.apache.ibatis.session.SqlSession;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ProgramsDao {

    @Inject
    private SqlSession sqlSession;

    public List<Program> selectAllPrograms() {
        List<Program> programs = sqlSession.selectList("programsMapper.selectAllPrograms");
        if (programs == null)
            return new ArrayList<>();
        return programs;
    }

    public List<Program> selectUnusedProgramsByKidId(int kidsId) {
        List<Program> programs = sqlSession.selectList("programsMapper.selectUnusedProgramsByKidId", kidsId);
        if(programs == null)
            return new ArrayList<>();
        return programs;
    }

    public List<Program> selectKidProgramsNotInPeriodByPeriodId(int periodId) {
        List<Program> programs = sqlSession.selectList("programsMapper.selectKidProgramsNotInPeriodByPeriodId", periodId);
        if(programs == null)
            return new ArrayList<>();
        return programs;
    }

    public List<Program> selectKidProgramsWithCollectedData(int kidId) {
        List<Program> programs = sqlSession.selectList("programsMapper.selectKidResolvedPrograms", kidId);
        if(programs == null)
            return new ArrayList<>();
        return programs;
    }

    public List<ResolvedTabData> selectProgramDataForChart(int programId) {
        return sqlSession.selectList("programsMapper.selectProgramDataForChart", programId);
    }

    public void updateProgramFinishedStatus(Program program) {
        sqlSession.update("programsMapper.updateProgramFinishedStatus", program);
    }

    public List<Program> selectProgramBySymbol(String symbol) {
        List<Program> programs = sqlSession.selectList("programsMapper.selectProgramBySymbol", symbol);
        if(programs == null)
            return new ArrayList<>();
        return programs;
    }

    public Program selectProgramById(Integer programId) {
        return sqlSession.selectOne("programsMapper.selectProgramById", programId);
    }

    public List<Program> selectAllTemplatePrograms() {
        List<Program> programs = sqlSession.selectList("programsMapper.selectAllTemplatePrograms");
        if (programs ==  null)
            return new ArrayList<>();
        return programs;
    }

    public void insertProgram(Program program) {
        sqlSession.insert("programsMapper.insertProgram", program);
    }

    public void deleteProgram(Program program) {
        sqlSession.delete("programsMapper.deleteProgram", program);
    }

    public void deleteProgramBySymbol(String symbol) {
        sqlSession.delete("programsMapper.deleteProgramBySymbol", symbol);
    }

    public void deletePrograms(List<Program> programs) {
        for(Program program : programs)
            sqlSession.delete("programsMapper.deleteProgram", program);
    }

    public void updateProgram(Program program) {
        sqlSession.update("programsMapper.updateProgram", program);
    }
}
