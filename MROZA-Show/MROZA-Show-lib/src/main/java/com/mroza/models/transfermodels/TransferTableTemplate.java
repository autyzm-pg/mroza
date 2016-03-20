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

import com.mroza.models.Table;

import java.io.Serializable;
import java.util.Date;

public class TransferTableTemplate implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Date createDate;
    private boolean isArchived;
    private long programId;

    TransferTableTemplate() {

    }

    public TransferTableTemplate(Long id, String name, String description, Date createDate, boolean isArchived, long programId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createDate = createDate;
        this.isArchived = isArchived;
        this.programId = programId;
    }

    public static TransferTableTemplate transferObjectFromServerModel(Table table) {
        return new TransferTableTemplate(new Long(table.getId()), table.getName(), table.getDescription(),
                null, table.isArchived(), table.getProgramId());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public boolean getIsArchived() {
        return isArchived;
    }

    public long getProgramId() {
        return programId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }
}
