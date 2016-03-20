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

package com.mroza.viewbeans.utils;

import com.mroza.models.FilterableModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class FilterableList<T extends FilterableModel> implements Serializable {

    @Getter
    @Setter
    private String textFilter = "";

    @Getter
    @Setter
    private List<T> completeList;

    @Getter
    @Setter
    private List<T> filteredList;

    @Getter
    @Setter
    private List<T> pfFilteredList;

    public FilterableList(List<T> completeList) {
        this.completeList = new ArrayList<>(completeList);
        this.filteredList = new ArrayList<>(completeList);
        this.pfFilteredList = new ArrayList<>(completeList);
    }

    public void filterProgramsByTextFilter() {
        if(textFilter.equals("")) {
            filteredList = new ArrayList<>(completeList);
            return;
        }

        if(completeList != null) {
            List<T> result = new ArrayList<>();
            for (T listElement : completeList) {
                if (listElement.containsText(textFilter)) {
                    result.add(listElement);
                }
            }

            filteredList = result;
        }
    }

    public void add(T t) {
        completeList.add(t);
        if(t.containsText(textFilter)) {
            filteredList.add(t);
        }
    }

    public void remove(T t) {
        completeList.remove(t);
        filteredList.remove(t);
        pfFilteredList.remove(t);
    }
}
