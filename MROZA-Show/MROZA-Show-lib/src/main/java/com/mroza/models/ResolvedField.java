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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResolvedField implements Serializable {

    private Integer id;
    private String value;

    private Integer kidTableId;
    private Integer tableFieldId;

    private KidTable kidTable;
    private TableField tableField;

    public ResolvedField(String value, KidTable kidTable, TableField tableField) {
        this.id = null;
        this.value = value;
        this.kidTableId = null;
        this.tableFieldId = null;
        this.kidTable = kidTable;
        this.tableField = tableField;
    }

    public ResolvedField(Long id, String value, Long kidTableId, Long tableFieldId) {
        this.id = (int)(long)id;
        this.value = value;
        this.kidTableId = (int)(long)kidTableId;
        this.tableFieldId = (int)(long)tableFieldId;
    }
}
