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

package com.mroza.service;

import com.mroza.models.KidTable;
import org.mockito.ArgumentMatcher;

class KidTableArgumentMatcher implements ArgumentMatcher {

    private KidTable kidTable;

    public KidTableArgumentMatcher(KidTable kidTable) {
        this.kidTable = kidTable;
    }

    public boolean matches(Object o) {
        if (o instanceof KidTable) {
            KidTable kidTable = (KidTable) o;
            if (!(kidTable.isCollectingLearning() == this.kidTable.isCollectingLearning()))
                return false;
            if (!(kidTable.isCollectingGeneralization() == this.kidTable.isCollectingGeneralization()))
                return false;
            if (!(kidTable.isFinishedLearning() == this.kidTable.isFinishedLearning()))
                return false;
            if (!(kidTable.isFinishedGeneralization() == this.kidTable.isFinishedGeneralization()))
                return false;
            if (!kidTable.getNote().equals(this.kidTable.getNote()))
                return false;
            if (!(kidTable.isIOA() == this.kidTable.isIOA()))
                return false;
            if (!(kidTable.isPretest() == this.kidTable.isPretest()))
                return false;
            if (kidTable.getTable() != null) {
                if (this.kidTable.getTable() != null) {
                    if (!(kidTable.getTable().getId() == this.kidTable.getTable().getId()))
                        return false;
                } else {
                    if (!(kidTable.getTable().getId() == this.kidTable.getTableId()))
                        return false;
                }

            } else {
                if (!(kidTable.getTableId().equals(this.kidTable.getTableId())))
                    return false;
            }
            if (kidTable.getPeriod() != null) {
                if (!(kidTable.getPeriod().getId() == this.kidTable.getPeriod().getId()))
                    return false;
            } else {
                if (!(kidTable.getPeriodId().equals(this.kidTable.getPeriodId())))
                    return false;
            }


            return true;
        }
        return false;
    }
}
