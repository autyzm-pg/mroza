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

package com.mroza.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Table implements Serializable {

    private final String LEARNING_TYPE_SYMBOL = "U";
    private final String GENERALIZATION_TYPE_SYMBOL = "G";
    private boolean edited = false;
    private Integer numberOfLearningCols = 0;
    private Integer numberOfGeneralizationCols = 0;

    private int id;
    private String name;
    private String description;
    private LocalDate createDate;
    private boolean archived;

    private Integer programId;

    private Program program;
    private List<KidTable> kidTables;
    private List<TableRow> tableRows;

    public Table(String name) {
        this.id = -1;
        this.name = name;
        this.description = "Opis";
        this.createDate = null;
        this.archived = false;

        this.program = null;
        this.kidTables = new ArrayList<>();
        this.tableRows = new ArrayList<>();
    }

    public Table(String name, LocalDate createDate) {
        this.id = -1;
        this.name = name;
        this.description = "Opis";
        this.createDate = createDate;
        this.archived = false;

        this.program = null;
        this.kidTables = new ArrayList<>();
        this.tableRows = new ArrayList<>();
    }

    public Integer getNumberOfLearningCols() {
        return countColumns(LEARNING_TYPE_SYMBOL, numberOfLearningCols);
    }

    public Integer getNumberOfGeneralizationCols() {
        return countColumns(GENERALIZATION_TYPE_SYMBOL, numberOfGeneralizationCols);
    }

    private Integer countColumns(final String symbol, Integer modelValue) {
        if(isEdited()) {
            return modelValue;
        } else {
            if (!areTableRows())
                return 0;

            return tableRows.get(0).getNumberOfCols(symbol);
        }
    }

    private boolean areTableRows(){
        return tableRows != null && tableRows.size() != 0;
    }

    public void addTableRow(String rowName, Integer learningColsNum, Integer generalizationColsNum) {
        if (tableRows == null)
            tableRows = new ArrayList<>();

        tableRows.add(new TableRow(rowName, tableRows.size(), learningColsNum, generalizationColsNum));
    }

    public void addTableRow(String rowName) {
        addTableRow(rowName, getNumberOfLearningCols(), getNumberOfGeneralizationCols());
    }

    public boolean isLearningCollecting() {
        return getNumberOfLearningCols() != 0;
    }

    public boolean isGeneralizationCollecting() {
        return getNumberOfGeneralizationCols() != 0;
    }
}
