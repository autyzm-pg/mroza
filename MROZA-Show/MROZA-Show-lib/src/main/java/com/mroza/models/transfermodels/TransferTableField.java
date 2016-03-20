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

import com.mroza.models.TableField;

import java.io.Serializable;

public class TransferTableField implements Serializable {

    private Long id;
    private String type;
    private Integer inOrder;

    private long tableRowId;

    TransferTableField() {

    }

    public TransferTableField(Long id, String type, Integer inOrder, long tableRowId) {
        this.id = id;
        this.type = type;
        this.inOrder = inOrder;
        this.tableRowId = tableRowId;
    }

    public static TransferTableField transferObjectFromServerModel(TableField tableField) {
        return new TransferTableField(new Long(tableField.getId()), tableField.getType(),
                tableField.getOrderNum(), tableField.getRowId());
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Integer getInOrder() {
        return inOrder;
    }

    public long getTableRowId() {
        return tableRowId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setInOrder(Integer inOrder) {
        this.inOrder = inOrder;
    }

    public void setTableRowId(long tableRowId) {
        this.tableRowId = tableRowId;
    }
}
