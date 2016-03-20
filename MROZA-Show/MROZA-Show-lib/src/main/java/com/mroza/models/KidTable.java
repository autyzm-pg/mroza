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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KidTable extends FilterableModel implements Serializable {

    private Integer id;
    private boolean collectingLearning;
    private boolean collectingGeneralization;
    private boolean finishedLearning;
    private boolean finishedGeneralization;
    private String note;
    private boolean isIOA;
    private boolean isPretest;
    private Date lastModDate;
    private Date generalizationFillDate;
    private Date learningFillDate;

    private Integer tableId;
    private Integer periodId;

    private Table table;
    private Period period;
    private List<ResolvedField> resolvedFields;

    @Override
    public boolean containsText(String text) {
        return table.getProgram().getName().toLowerCase().contains(text.toLowerCase())
                || table.getProgram().getSymbol().toLowerCase().contains(text.toLowerCase());
    }

    public KidTable(boolean collectingLearning, boolean collectingGeneralization, Integer tableId, Integer periodId) {
        this.collectingLearning = collectingLearning;
        this.collectingGeneralization = collectingGeneralization;
        this.finishedLearning = false;
        this.finishedGeneralization = false;
        this.note = "";
        this.isIOA = false;
        this.isPretest = false;
        this.tableId = tableId;
        this.periodId = periodId;
        this.resolvedFields = new ArrayList<>();
    }

    public KidTable(boolean collectingLearning, boolean collectingGeneralization, Table table, Period period) {
        this.collectingLearning = collectingLearning;
        this.collectingGeneralization = collectingGeneralization;
        this.finishedLearning = false;
        this.finishedGeneralization = false;
        this.note = "";
        this.isIOA = false;
        this.isPretest = false;
        this.table = table;
        this.period = period;
        this.resolvedFields = new ArrayList<>();
        if(table.getTableRows() != null) {
            table.getTableRows().forEach(
                    tableRow -> tableRow.getRowFields().forEach(
                            tableField -> this.resolvedFields.add(new ResolvedField("EMPTY", this, tableField))));
        }
    }

    public KidTable(Long id, boolean collectingLearning, boolean collectingGeneralization, boolean finishedLearning,
                    boolean finishedGeneralization, String note, boolean isIOA, boolean isPretest, Date lastModDate,
                    Date generalizationFillDate, Date learningFillDate, long tableId, long periodId) {
        this.id = (int)(long) id;
        this.collectingLearning = collectingLearning;
        this.collectingGeneralization = collectingGeneralization;
        this.finishedLearning = finishedLearning;
        this.finishedGeneralization = finishedGeneralization;
        this.note = note;
        this.isIOA = isIOA;
        this.isPretest = isPretest;
        this.lastModDate = lastModDate;
        this.generalizationFillDate = generalizationFillDate;
        this.learningFillDate = learningFillDate;

        this.tableId = (int) tableId;
        this.periodId = (int) periodId;

        this.resolvedFields = new ArrayList<>();
    }
    
    public KidTable(KidTable kidTable) {
        this.id = null;
        this.collectingLearning = kidTable.isCollectingLearning();
        this.collectingGeneralization = kidTable.isCollectingGeneralization();
        this.finishedLearning = kidTable.isFinishedLearning();
        this.finishedGeneralization = kidTable.isFinishedGeneralization();
        this.note = kidTable.getNote();
        this.isIOA = kidTable.isIOA();
        this.isPretest = kidTable.isPretest();
        this.lastModDate = kidTable.getLastModDate();
        this.generalizationFillDate = kidTable.getGeneralizationFillDate();
        this.learningFillDate = kidTable.getLearningFillDate();

        this.tableId = kidTable.getTableId();
        this.periodId = kidTable.getPeriodId();

        this.table = kidTable.getTable();
        this.period = kidTable.getPeriod();
        this.resolvedFields = new ArrayList<>();
        if(table.getTableRows() != null) {
            table.getTableRows().forEach(
                    tableRow -> tableRow.getRowFields().forEach(
                            tableField -> this.resolvedFields.add(new ResolvedField("EMPTY", this, tableField))));
        }
    }
}
