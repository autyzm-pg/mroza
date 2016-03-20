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

package com.mroza.mocks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mroza.interfaces.KidsService;
import com.mroza.models.Kid;
import com.mroza.models.Program;
import com.mroza.qualifiers.KidsServiceMock;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

@KidsServiceMock
public class KidsServiceMockImpl implements Serializable, KidsService {

    private List<Kid> kidsList;

    private List<Program> programsList;

    public KidsServiceMockImpl() {}

    @Override
    public List<Kid> getAllKids() {
        if(kidsList == null) {
            kidsList = readObjectsListFromJson("/com/mroza/mocks/kids.json");
        }

        return kidsList;
    }

    @Override
    public Kid getKidDetailedData(int kidsId) {
        if(programsList == null) {
            programsList = readObjectsListFromJson("/com/mroza/mocks/therapyPrograms.json");
        }

        Kid kid = kidsList.get(kidsId);
        kid.setPrograms(programsList);

        return kid;
    }

    @Override
    public void saveKid(Kid kid) {
        kidsList.add(kid);
    }

    private <T> List<T> readObjectsListFromJson(String fileName) {
        Gson gson = new Gson();
        List<T> list = null;

        try {
            InputStream is = KidsServiceMockImpl.class.getResourceAsStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF8"));

            Type collectionType = new TypeToken<List<T>>() {}.getType();
            list = gson.fromJson(br, collectionType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return list;
    }
}
