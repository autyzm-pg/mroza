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

package com.mroza.interfaces;

import com.mroza.models.Program;
import com.mroza.models.Table;
import com.mroza.models.charts.ResolvedTabData;
import com.mroza.models.charts.SimplifiedResolvedTabRow;

import javax.transaction.Transactional;
import java.util.List;

public interface KidProgramsService {

    @Transactional void addNewTable(Table table);

    @Transactional
    void updateTable(Table table);

    List<Program> getUnusedProgramsByKidId(Integer kidId);
    void changeProgramFinishedStatus(int programId, boolean finished);
    Program assignProgramToKid(int kidId, int programId);
    void saveTable(Table table);
    List<Program> getKidProgramsWithCollectedData(Integer kidId);
    List<ResolvedTabData> getProgramDataForChart(Integer programId);
    List<SimplifiedResolvedTabRow> getSimplifiedResolvedTab(Integer kidTabId, String type);
}
