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

import com.google.gson.Gson;
import com.mroza.interfaces.KidProgramsService;
import com.mroza.interfaces.ProgramsService;
import com.mroza.models.Program;
import com.mroza.models.charts.ResolvedTabData;
import com.mroza.models.charts.SimplifiedResolvedTabRow;
import com.mroza.qualifiers.KidProgramsServiceImpl;
import com.mroza.qualifiers.ProgramsServiceImpl;
import com.mroza.viewbeans.charts.model.*;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@ViewScoped
@Named
public class AmChartsBean extends BaseChartsBean implements Serializable {

    @Inject
    @KidProgramsServiceImpl
    private KidProgramsService kidProgramsService;

    @Inject
    @ProgramsServiceImpl
    private ProgramsService programsService;

    @Getter
    @Setter
    private String selectedTabHeader;

    @Getter
    @Setter
    private boolean init = false;

    @Getter
    private Program program;

 
    public void initView() {
        if(!init) {
            program = programsService.retrieveProgram(programId);
            List<ResolvedTabData> dataForChart = kidProgramsService.getProgramDataForChart(programId);
            prepareDataForChart(dataForChart);
            init = true;
        }
    }

    private void prepareDataForChart(List<ResolvedTabData> dataForChart) {
        Set<String> dataSeries = new HashSet<>();                       // graphs names need for amchart.js
        Map<String, Map<String, String>> values = new TreeMap<>();      // values for every date on chart (dataProvider for amchart.js)
        Map<String, Map<String, LocalDate>> guides = new HashMap<>();   // guide for every Table
        Map<String, Integer> kidTabIds = new TreeMap<>();               // kid tables ids

        for(ResolvedTabData data : dataForChart) {
            putGuide(guides, data);
            putValue(values, data);
            putId(kidTabIds, data);
            dataSeries.add(getKey(data));
        }

        String jsDataProvider = prepareJsDataProvider(values);
        String jsGraphs = prepareJsGraph(dataSeries);
        String jsGraphsTypes = prepareJsGraphTypes(dataSeries);
        String jsGuides = prepareJsGuides(guides);
        String jsKidTabIds = prepareJsKidTabIds(kidTabIds);

        RequestContext.getCurrentInstance().addCallbackParam("guides", jsGuides);
        RequestContext.getCurrentInstance().addCallbackParam("graphs", jsGraphs);
        RequestContext.getCurrentInstance().addCallbackParam("graphsTypes", jsGraphsTypes);
        RequestContext.getCurrentInstance().addCallbackParam("dataProvider", jsDataProvider);
        RequestContext.getCurrentInstance().addCallbackParam("kidTabIds", jsKidTabIds);
    }

    private String prepareJsDataProvider(Map<String, Map<String, String>> values) {
        Gson gson = new Gson();
        return gson.toJson(values.values()).toString();
    }

    private String prepareJsKidTabIds(Map<String, Integer> kidTabIds) {
        List<Integer> ids = new ArrayList<>();
        for (Integer value : kidTabIds.values()) {
            ids.add(value);
        }

        Gson gson = new Gson();
        return gson.toJson(ids);
    }

    private String prepareJsGraph(Set<String> dataSeries) {
        List<GraphModel> graphs = new ArrayList<>();
        for(String key : dataSeries) {
            graphs.add(new GraphModel(key));
        }
        Gson gson = new Gson();
        return gson.toJson(graphs);
    }

    private String prepareJsGraphTypes(Set<String> dataSeries){
        ResourceBundle bundle = ResourceBundle.getBundle("messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        List<String> graphsTypes = new ArrayList<>();
        for(String key : dataSeries) {
            String type;
            if(key.contains(bundle.getString("main.learning")))
                type = "U";
            else
                type = "G";
            graphsTypes.add(type);
        }
        Gson gson = new Gson();
        return gson.toJson(graphsTypes);
    }

    private String prepareJsGuides(Map<String, Map<String, LocalDate>> guides) {
        List<GuideModel> guidesList = new ArrayList<>();
        for(Map.Entry<String, Map<String, LocalDate>> guide : guides.entrySet()) {
            GuideModel g = new GuideModel(guide.getKey(),
                    guide.getValue().get("date").toString(),
                    (guide.getValue().get("toDate").plusDays(1)).toString());
            guidesList.add(g);
        }
        Gson gson = new Gson();
        return gson.toJson(guidesList).toString();
    }

    private void putValue(Map<String, Map<String, String>> values, ResolvedTabData data) {
        String date = data.getFillDate().toString();
        String value = getValue(data);
        String key = getKey(data);

        if(values.containsKey(date)) {
            Map<String, String> point = values.get(date);
            point.put(key, value);
        } else {
            Map<String, String> point = new HashMap<>();
            point.put("date", date);
            point.put(key, value);
            values.put(date, point);
        }
    }

    private void putId(Map<String, Integer> ids, ResolvedTabData data){
        String date = data.getFillDate().toString();
        if(!ids.containsKey(date)){
            ids.put(date, data.getKidTabId());
        }
    }

    private void putGuide(Map<String, Map<String, LocalDate>> guides, ResolvedTabData data) {
        if(guides.containsKey(getGuideKey(data))) {
            LocalDate date = data.getFillDate();
            Map<String, LocalDate> guide = guides.get(getGuideKey(data));
            if(date.isBefore(guide.get("date")))
                guide.put("date", date);
            else if(date.isAfter(guide.get("toDate")))
                guide.put("toDate", date);
        } else {
            Map<String, LocalDate> guide = new TreeMap<>();
            guide.put("date", data.getFillDate());
            guide.put("toDate", data.getFillDate());
            guides.put(getGuideKey(data), guide);
        }
    }

    private String getGuideKey(ResolvedTabData data) {
        if(data.isPretest()) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
            return data.getTabName() + " - " + bundle.getString("main.pretest");
        } else
            return data.getTabName();
    }

    private String getValue(ResolvedTabData data) {
        float val = (data.getCorrectFields().floatValue() / data.getNumOfFields().floatValue() * 100.0f);
        Integer value = Math.round(val);
        return value.toString();
    }

    public void prepareFilledTable(){
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        int kidTabId = Integer.parseInt(map.get("kidTabId").toString());
        String key = map.get("key").toString();
        selectedTabHeader = key;

        ResourceBundle bundle = ResourceBundle.getBundle("messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        String type;
        if(key.contains(bundle.getString("main.learning")))
            type = "U";
        else
            type = "G";

        List<SimplifiedResolvedTabRow> rows = kidProgramsService.getSimplifiedResolvedTab(kidTabId, type);
        prepareResolvedTabModel(rows);
    }
}