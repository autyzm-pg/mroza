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

package com.mroza.models.transfermodels;

import com.mroza.models.Program;

import java.io.Serializable;

public class TransferProgram implements Serializable {

    private Long id;
    private String symbol;
    private String name;
    private String description;
    private boolean isFinished;

    private Long childId;

    TransferProgram() {

    }

    public TransferProgram(Long id, String symbol, String name, String description, boolean isFinished, Long childId) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.description = description;
        this.isFinished = isFinished;
        this.childId = childId;
    }

    public TransferProgram(Long id, String symbol, String name, String description, boolean isFinished, Integer childId) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.description = description;
        this.isFinished = isFinished;
        if(childId == null)
            this.childId = null;
        else
            this.childId = new Long(childId);
    }

    public static TransferProgram transferObjectFromServerModel(Program program) {
        return new TransferProgram(new Long(program.getId()), program.getSymbol(), program.getName(),
                program.getDescription(), program.isFinished(), program.getKidId());
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    public Long getChildId() {
        return childId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }
}
