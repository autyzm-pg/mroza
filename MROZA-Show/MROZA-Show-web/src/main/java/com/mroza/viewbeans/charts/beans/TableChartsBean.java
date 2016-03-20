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

package com.mroza.viewbeans.charts.beans;

import com.mroza.interfaces.KidProgramsService;
import com.mroza.interfaces.ProgramsService;
import com.mroza.models.Program;
import com.mroza.models.charts.ResolvedTabData;
import com.mroza.models.charts.SimplifiedResolvedTabRow;
import com.mroza.qualifiers.ProgramsServiceImpl;
import com.mroza.viewbeans.charts.model.*;
import com.mroza.qualifiers.KidProgramsServiceImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@ViewScoped
@Named
@NoArgsConstructor
public class TableChartsBean extends BaseChartsBean implements Serializable {

    @Inject
    @KidProgramsServiceImpl
    private KidProgramsService kidProgramsService;

    @Inject
    @ProgramsServiceImpl
    private ProgramsService programsService;

    @Getter
    @Setter
    private FlexibleTable<TableChartField> tableModel;

    @Getter
    @Setter
    private ResolvedTabData selectedTab;

    @Getter
    private Program program;

    public void init() {
        List<ResolvedTabData> dataForChart = kidProgramsService.getProgramDataForChart(programId);
        program = programsService.retrieveProgram(programId);
        createTableModel(dataForChart);
    }

    private void createTableModel(List<ResolvedTabData> dataForChart) {
        List<String> dates = getDatesList(dataForChart);

        Map<String, Map<String, TableChartField>> charts = new TreeMap<>();
        for(ResolvedTabData data : dataForChart) {
            String key = getKey(data);

            String date = data.getFillDate().toString();
            TableChartField value = prepareField(data);
            if(charts.containsKey(key)) {
                Map<String, TableChartField> chart = charts.get(key);
                chart.put(date, value);
            } else {
                Map<String, TableChartField> chart = new TreeMap<>();
                chart.put(date, value);
                charts.put(key, chart);
            }
        }

        tableModel = new FlexibleTable<>(charts.size(), dates);
        int index = charts.size()-1;
        for(Map.Entry<String, Map<String, TableChartField>> row : charts.entrySet()) {
            tableModel.getRows().get(index).put("title", new TableChartField(row.getKey()));
            for(Map.Entry<String, TableChartField> field : row.getValue().entrySet()) {
                tableModel.getRows().get(index).put(field.getKey(), field.getValue());
            }

            index--;
        }
    }

    private TableChartField prepareField(ResolvedTabData rowData) {
        String label = rowData.getCorrectFields() + "/" + rowData.getNumOfFields();
        return new TableChartField(label, rowData);
    }

    private List<String> getDatesList(List<ResolvedTabData> dataForChart) {
        Set<LocalDate> dates = new HashSet<>();
        for(ResolvedTabData tabData : dataForChart) {
            if(tabData.getFillDate() != null)
                dates.add(tabData.getFillDate());
        }

        List<LocalDate> list = new ArrayList<>(dates);
        Collections.sort(list);

        List<String> result = new ArrayList<>();
        for(LocalDate date : list)
            result.add(date.toString());
        return result;
    }

    public void prepareFilledTable(TableChartField field) {
        selectedTab = field.getFieldData();
        List<SimplifiedResolvedTabRow> rows = kidProgramsService.getSimplifiedResolvedTab(selectedTab.getKidTabId(), selectedTab.getType());

        prepareResolvedTabModel(rows);
    }
}
