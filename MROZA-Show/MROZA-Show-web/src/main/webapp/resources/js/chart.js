var myChart = AmCharts.makeChart("graphContainer", {
    "language": "pl",
    "type": "serial",
    "theme": "light",
    "marginRight": 40,
    "marginLeft": 40,
    "autoMarginOffset": 20,
    "dataDateFormat": "YYYY-MM-DD",
    "valueAxes": [{
        "id": "v1",
        "axisAlpha": 0,
        "position": "left",
        "ignoreAxisWidth":true
    }],
    "balloon": {
        "borderThickness": 1,
        "shadowAlpha": 0
    },
    "chartScrollbar": {
        "oppositeAxis":false,
        "offset":30,
        "scrollbarHeight": 80,
        "backgroundAlpha": 0,
        "selectedBackgroundAlpha": 0.1,
        "selectedBackgroundColor": "#888888",
        "graphFillAlpha": 0,
        "graphLineAlpha": 0.5,
        "selectedGraphFillAlpha": 0,
        "selectedGraphLineAlpha": 1,
        "autoGridCount":true,
        "color":"#AAAAAA"
    },
    "chartCursor": {
        "pan": true,
        "valueLineEnabled": true,
        "valueLineBalloonEnabled": true,
        "cursorAlpha":1,
        "cursorColor":"#258cbb",
        "valueLineAlpha":0.2
    },
    "valueScrollbar":{
        "oppositeAxis":false,
        "offset":50,
        "scrollbarHeight":10
    },
    "categoryField": "date",
    "categoryAxis": {
        "parseDates": true,
        "dashLength": 1,
        "minorGridEnabled": true
    },
    "legend": {
        "useGraphSettings": true,
        "position": "top",
        "align": "center",
        "switchable": false,
        "valueText": ""
    },
    "export": {
        "enabled": true
    }
});

var kidTabIds;

myChart.addListener("clickGraphItem", function(event) {
    showTable([{name: 'kidTabId', value: kidTabIds[event.index]}, {name:"key", value: event.graph.valueField}]);
});

function drawMyGraph(xhr, status, args) {
    chartData = args.dataProvider;
    kidTabIds = eval(args.kidTabIds);

    graphs = eval(args.graphs);
    graphsTypes = eval(args.graphsTypes);
    var isGeneralizationInLegend = false;
    var isTeachingInLegend = false;
    for(i = 0; i < graphs.length; i++) {
        var g = graphs[i];
        g.balloon = {
            "drop": false,
            "adjustBorderColor": false,
            "color": "#ffffff"
        };

        if(graphsTypes[i] == "U") {
            g.bullet = "round";
            g.bulletSize = 5;
            g.lineThickness = 2;
            g.lineColor="#556c8d";
            if(!isTeachingInLegend)
                isTeachingInLegend = true;
            else
                g.visibleInLegend = false;
        }
        else {
            g.bullet = "triangleUp";
            g.bulletSize = 12;
            g.lineThickness = 0;
            g.lineColor="#182b47";
            if(!isGeneralizationInLegend)
                isGeneralizationInLegend = true;
            else
                g.visibleInLegend = false;
        }

        var label = g.valueField.split("-");
        g.title = label[label.length - 1];
        g.bulletBorderAlpha = 1;
        g.bulletColor = "#FFFFFF";
        g.hideBalloonTime = 50;
        g.useLineColorForBulletBorder = true;
        g.showHandOnHover = true;
        g.balloonText = "<span style='font-size:18px;'>[[value]]</span>";
    }
    myChart.dataProvider = eval(chartData);
    myChart.graphs = graphs;

    chartGuides = myChart.categoryAxis.guides;
    guides = eval(args.guides);
    for(i=0; i<guides.length; i++) {
        guide = {
            date: guides[i].date,
            toDate: guides[i].toDate,
            lineColor: "#CC0000",
            lineAlpha: 1,
            fillAlpha: 0.2,
            fillColor: guides[i].title.contains('Pretest') ? "#CC0000" : "#FFFFFF",
            dashLength: 2,
            inside: true,
            labelRotation: 90,
            position: "bottom",
            label: guides[i].title
        };
        chartGuides.push(guide);
    }

    myChart.write('graphContainer');
}