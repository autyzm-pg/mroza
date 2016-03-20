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

import com.mroza.models.Period;

import java.io.Serializable;
import java.util.Date;

public class TransferTermSolution implements Serializable {

    private Long id;
    private Date startDate;
    private Date endDate;

    private long childId;

    TransferTermSolution() {

    }

    public TransferTermSolution(Long id, Date startDate, Date endDate, long childId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.childId = childId;
    }

    public static TransferTermSolution transferObjectFromServerModel(Period period) {
        Date periodBeginDate = period.getBeginDate();
        Date periodEndDate = period.getEndDate();
        periodBeginDate.setHours(2);
        periodEndDate.setHours(22);
        return new TransferTermSolution(new Long(period.getId()), periodBeginDate, periodEndDate, period.getKidId());
    }

    public Long getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public long getChildId() {
        return childId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }
}
