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

package com.mroza.viewbeans;

import com.mroza.interfaces.ProgramsService;
import com.mroza.models.Program;
import com.mroza.qualifiers.ProgramsServiceImpl;
import com.mroza.viewbeans.utils.NavigationUtil;
import com.mroza.viewbeans.utils.ViewMsg;
import com.mroza.viewbeans.utils.ViewMsgUtil;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;


@ViewScoped
@Named
public class NewProgramsBean implements Serializable {

    @Inject
    @ProgramsServiceImpl
    ProgramsService programsService;

    @Getter
    @Setter
    Program program;

    @PostConstruct
    public void init() {
        program = new Program();
    }

    public void initView() {
        if(program.getId() != null) {
            program = programsService.retrieveProgram(program.getId());
        }
    }

    public void saveProgram() {
        if (programsService.existsProgramWithSymbol(program.getSymbol())) {
            ViewMsgUtil.setViewErrorMsg(ViewMsg.PROGRAM_SYMBOL_DUPLICATED);
        } else {
            programsService.addProgram(program);
            NavigationUtil.redirect("programsDirectoryView.xhtml");
        }
    }

    public void updateProgram() {
        programsService.updateProgramsWithSymbol(program);
        NavigationUtil.redirect("programsDirectoryView.xhtml");
    }

    public void validateSymbol() {
        if (programsService.existsProgramWithSymbol(program.getSymbol()))
            ViewMsgUtil.setViewErrorMsg(ViewMsg.PROGRAM_SYMBOL_DUPLICATED);
    }

}
