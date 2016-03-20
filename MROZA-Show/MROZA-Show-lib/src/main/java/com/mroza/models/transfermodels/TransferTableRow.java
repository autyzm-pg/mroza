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

import com.mroza.models.TableRow;

import java.io.Serializable;

public class TransferTableRow implements Serializable {

    private Long id;
    private String value;
    private Integer inOrder;

    private long tableId;

    TransferTableRow() {

    }

    public TransferTableRow(Long id, String value, Integer inOrder, long tableId) {
        this.id = id;
        this.value = value;
        this.inOrder = inOrder;
        this.tableId = tableId;
    }

    public static TransferTableRow transferObjectFromServerModel(TableRow tableRow) {
        return new TransferTableRow(new Long(tableRow.getId()), tableRow.getName(),
                tableRow.getOrderNum(), tableRow.getTableId());
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Integer getInOrder() {
        return inOrder;
    }

    public long getTableId() {
        return tableId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setInOrder(Integer inOrder) {
        this.inOrder = inOrder;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }
}
