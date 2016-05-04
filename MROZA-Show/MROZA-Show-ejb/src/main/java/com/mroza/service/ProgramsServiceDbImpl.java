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

import com.mroza.dao.ProgramsDao;
import com.mroza.interfaces.ProgramsService;
import com.mroza.models.Program;
import com.mroza.qualifiers.ProgramsServiceImpl;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@ProgramsServiceImpl
public class ProgramsServiceDbImpl implements Serializable, ProgramsService {

    @Inject
    private ProgramsDao programsDao;

    @Override
    public List<Program> getAllTemplatePrograms() {
        return programsDao.selectAllTemplatePrograms();
    }

    @Override
    public void addProgram(Program program) {
        programsDao.insertProgram(program);
    }

    @Override
    public boolean existsProgramWithSymbol(String symbol) {
        List<Program> programWithSymbol = programsDao.selectProgramBySymbol(symbol);
        return !programWithSymbol.isEmpty();
    }

    @Override
    public boolean existsProgramInstanceWithSymbol(String symbol) {
        List<Program> programWithSymbol = programsDao.selectProgramBySymbol(symbol);
        return programWithSymbol.size() > 1;
    }

    @Override
    public void deleteProgram(String symbol) {
        programsDao.deleteProgramBySymbol(symbol);
    }

    @Override
    public Program retrieveProgram(Integer id) {
        return programsDao.selectProgramById(id);
    }

    @Override
    public void updateProgramsWithSymbol(Program program) {
        String symbol = programsDao.selectProgramById(program.getId()).getSymbol();
        List<Program> programsToUpdate = programsDao.selectProgramBySymbol(symbol);
        programsToUpdate.forEach(programToUpdate -> {
            programToUpdate.setName(program.getName());
            programToUpdate.setSymbol(program.getSymbol());
            programToUpdate.setDescription(program.getDescription());
            programsDao.updateProgram(programToUpdate);});

    }


}
