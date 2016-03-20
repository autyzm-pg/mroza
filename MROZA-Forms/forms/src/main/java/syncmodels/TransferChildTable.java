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

package syncmodels;

import database.ChildTable;

import java.io.Serializable;
import java.util.Date;

public class TransferChildTable implements Serializable {

    private Long id;
    private boolean isTeachingCollected;
    private boolean isGeneralizationCollected;
    private boolean isTeachingFinished;
    private boolean isGeneralizationFinished;
    private String note;
    private boolean isIOA;
    private boolean isPretest;
    private Date teachingFillOutDate;
    private Date generalizationFillOutDate;
    private Date lastEditDate;

    private long tableId;
    private long termSolutionId;

    public TransferChildTable(){};

    public TransferChildTable(Long id, boolean isTeachingCollected, boolean isGeneralizationCollected,
                              boolean isTeachingFinished, boolean isGeneralizationFinished, String note,
                              boolean isIOA, boolean isPretest, Date teachingFillOutDate,
                              Date generalizationFillOutDate, Date lastEditDate, long tableId, long termSolutionId) {
        this.id = id;
        this.isTeachingCollected = isTeachingCollected;
        this.isGeneralizationCollected = isGeneralizationCollected;
        this.isTeachingFinished = isTeachingFinished;
        this.isGeneralizationFinished = isGeneralizationFinished;
        this.note = note;
        this.isIOA = isIOA;
        this.isPretest = isPretest;
        this.teachingFillOutDate = teachingFillOutDate;
        this.generalizationFillOutDate = generalizationFillOutDate;
        this.lastEditDate = lastEditDate;
        this.tableId = tableId;
        this.termSolutionId = termSolutionId;
    }

    public static TransferChildTable transferObjectFromServerModel(ChildTable childTable) {
        return new TransferChildTable(new Long(childTable.getId()), childTable.getIsTeachingCollected(),
                childTable.getIsGeneralizationCollected(), childTable.getIsTeachingFinished(),
                childTable.getIsGeneralizationFinished(), childTable.getNote(), childTable.getIsIOA(), childTable.getIsPretest(),
                childTable.getTeachingFillOutDate(), childTable.getGeneralizationFillOutDate(),
               childTable.getLastEditDate(), childTable.getTableId(), childTable.getTermSolutionId());
    }

    public Long getId() {
        return id;
    }

    public boolean getIsTeachingCollected() {
        return isTeachingCollected;
    }

    public boolean getIsGeneralizationCollected() {
        return isGeneralizationCollected;
    }

    public boolean getIsTeachingFinished() {
        return isTeachingFinished;
    }

    public boolean getIsGeneralizationFinished() {
        return isGeneralizationFinished;
    }

    public String getNote() {
        return note;
    }

    public boolean getIsIOA() {
        return isIOA;
    }

    public boolean getIsPretest() {
        return isPretest;
    }

    public Date getTeachingFillOutDate() {
        return teachingFillOutDate;
    }

    public Date getGeneralizationFillOutDate() {
        return generalizationFillOutDate;
    }

    public Date getLastEditDate() {
        return lastEditDate;
    }

    public long getTableId() {
        return tableId;
    }

    public long getTermSolutionId() {
        return termSolutionId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIsTeachingCollected(Boolean isTeachingCollected) {
        this.isTeachingCollected = isTeachingCollected;
    }

    public void setIsGeneralizationCollected(Boolean isGeneralizationCollected) {
        this.isGeneralizationCollected = isGeneralizationCollected;
    }

    public void setIsTeachingFinished(Boolean isTeachingFinished) {
        this.isTeachingFinished = isTeachingFinished;
    }

    public void setIsGeneralizationFinished(Boolean isGeneralizationFinished) {
        this.isGeneralizationFinished = isGeneralizationFinished;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setIsIOA(Boolean isIOA) {
        this.isIOA = isIOA;
    }

    public void setIsPretest(Boolean isPretest) {
        this.isPretest = isPretest;
    }

    public void setTeachingFillOutDate(Date teachingFillOutDate) {
        this.teachingFillOutDate = teachingFillOutDate;
    }

    public void setGeneralizationFillOutDate(Date generalizationFillOutDate) {
        this.generalizationFillOutDate = generalizationFillOutDate;
    }

    public void setLastEditDate(Date lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public void setTermSolutionId(Long termSolutionId) {
        this.termSolutionId = termSolutionId;
    }
}
