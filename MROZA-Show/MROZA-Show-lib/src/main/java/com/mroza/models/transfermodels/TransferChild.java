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


import com.mroza.models.Kid;

import java.io.Serializable;

public class TransferChild implements Serializable {

    private Long id;
    private String code;
    private boolean isArchived;

    public TransferChild() {

    }

    public TransferChild(Long id, String code, boolean isArchived) {
        this.id = id;
        this.code = code;
        this.isArchived = isArchived;
    }

    public static TransferChild transferObjectFromServerModel(Kid kid) {
//        TODO handle isArchived in Kid
        return new TransferChild(new Long(kid.getId()), kid.getCode(), kid.isArchived());
    }

    public Long getId() {
        return id;
    }

    public boolean getIsArchived() {
        return isArchived;
    }

    public String getCode() {
        return code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }



}
