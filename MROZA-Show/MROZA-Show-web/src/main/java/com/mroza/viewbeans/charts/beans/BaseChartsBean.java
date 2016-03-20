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

package com.mroza.viewbeans.charts.beans;

import com.mroza.models.charts.ResolvedTabData;
import com.mroza.models.charts.SimplifiedResolvedTabRow;
import com.mroza.viewbeans.charts.model.FlexibleTable;
import com.mroza.viewbeans.charts.model.ResolvedTabField;
import lombok.Getter;
import lombok.Setter;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public abstract class BaseChartsBean implements Serializable {

    @Getter
    @Setter
    protected Integer programId;

    @Getter
    @Setter
    protected FlexibleTable<ResolvedTabField> resolvedTabModel;

    protected String getKey(ResolvedTabData data) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        String label = data.getTabName() + " - ";
        if(data.isPretest())
            label += bundle.getString("main.pretest") + " - " +
                    (data.getType().equals("U") ? bundle.getString("main.learning") : bundle.getString("main.generalization"));
        else
            label += (data.getType().equals("U") ? bundle.getString("main.learning") : bundle.getString("main.generalization"));

        return label;
    }

    protected void prepareResolvedTabModel(List<SimplifiedResolvedTabRow> rows){
        List<String> columns = new ArrayList<>();
        for(int i=0; i<rows.get(0).getRow().size(); i++)
            columns.add("c"+i);

        resolvedTabModel = new FlexibleTable<>(rows.size(), columns);
        for(int i=0; i<rows.size(); i++) {
            resolvedTabModel.getRows().get(i).put("title", new ResolvedTabField(rows.get(i).getTitle(), false));

            for(int j=0; j<rows.get(i).getRow().size(); j++) {
                resolvedTabModel.getRows().get(i).put(resolvedTabModel.getColumns().get(j),
                        new ResolvedTabField("", rows.get(i).getRow().get(j)));
            }
        }
    }
}