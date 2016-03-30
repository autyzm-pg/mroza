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

import com.mroza.dao.*;
import com.mroza.interfaces.KidsService;
import com.mroza.models.Kid;
import com.mroza.qualifiers.KidsServiceImpl;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@KidsServiceImpl
public class KidsServiceDbImpl implements Serializable, KidsService {

    @Inject
    private KidsDao kidsDao;

    @Override
    public List<Kid> getAllKids() {
        return kidsDao.selectAllKids();
    }

    @Override
    public Kid getKidDetailedData(int kidsId) {
        return kidsDao.selectKidWithEdgesProgramsAndPeriods(kidsId);
    }

    @Override
    public void saveKid(Kid kid) {
        kidsDao.insertKid(kid);
    }

    @Override
    public boolean existsKidWithCode(String code) {
        List<Kid> kidsWithCode = kidsDao.selectKidsWithCode(code);
        return kidsWithCode.size() > 0;
    }

}
