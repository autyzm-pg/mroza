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

import database.TableFieldFilling;

import java.io.Serializable;

public class TransferTableFieldFilling implements Serializable {

    private Long id;
    private String content;

    private long tableFieldId;
    private long childTableId;

    public TransferTableFieldFilling(){};

    public TransferTableFieldFilling(Long id, String content, long tableFieldId, long childTableId) {
        this.id = id;
        this.content = content;
        this.tableFieldId = tableFieldId;
        this.childTableId = childTableId;
    }

    public static TransferTableFieldFilling transferObjectFromServerModel(TableFieldFilling tableFieldFilling) {
        return new TransferTableFieldFilling(new Long(tableFieldFilling.getId()), tableFieldFilling.getContent(),
                tableFieldFilling.getTableFieldId(), tableFieldFilling.getChildTableId());
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTableFieldId() {
        return tableFieldId;
    }

    public long getChildTableId() {
        return childTableId;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTableFieldId(Long tableFieldId) {
        this.tableFieldId = tableFieldId;
    }

    public void setChildTableId(Long childTableId) {
        this.childTableId = childTableId;
    }
}
