
// Setup H2 Database Services, default set to same server as this webpage and port 8082
var H2_server = location.hostname;
var H2_port = (location.port === "") ? 80 : location.port;

// declare global Variables
var filter_feld = '';
var DP_point = {};
var DP_rooms = [];
var DP_gewerk = [];
var Zeitraum_Ende = new Date(Date.now());
var Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(86400000 * 1)));
var DP_Aktive = [];
var DP_start = [];
var DP_start_room = '';
var DP_start_func = '';
var Scroll_Legend = true;
var DP_Legend = true;
var DP_Navigator = true;
var DP_Labels = false;
var DP_DayLight = true;
var DP_Limit = false;
var DP_LineColor = 0;
var DP_Grouping = 0;

/**
* create serien option and add it to HighStock Chart
*/
function addSerie(DP) {

    var chart = $('#container').highcharts();

    var unit = DP.attributes.unit;
    var valueDecimals = 1;
    var factor = 1;
    var yAxis = 0;
    var grouping = undefined;
    var marker = {
        enabled: false,
        states: {
            hover: {
                enabled: true,
                   }
                }
        };

    var type = "line";
    var step = "left";
    var dptype = DP.id.identifier;

    switch (dptype) {
    case "HUMIDITY":
    case "HUMIDITYF":
    case "ACTUAL_HUMIDITY":
    case "HUM_MAX_24H":
    case "HUM_MIN_24H":
        yAxis = 1;
    case "TEMPERATURE":
    case "ACTUAL_TEMPERATURE":
    case "ABS_HUMIDITY":
    case "DEW_POINT":
    case "TEMP_MAX_24H":
    case "TEMP_MIN_24H":
        valueDecimals = 1;
        type = "spline";
        break;
    case "MEAN5MINUTES":
        valueDecimals = 3;
        type = "spline";
        break;
    case "BRIGHTNESS":
        valueDecimals = 0;
        type = "spline";
        break;
    case "LEVEL":
        type = "line";
        step = "left";
        unit = "";
        yAxis = 2;
        valueDecimals = 2;
        break;
    case "STATE":
        yAxis = 2,
        valueDecimals = 0;
        type = "line";
        step = "left";
        break;
    case "PRESS_SHORT":
    case "PRESS_LONG":
    case "PRESS_OPEN":
    case "MOTION":
        yAxis = 2,
        marker = {
            enabled: true
        };
        factor = 5;
        type = "scatter";
        break;
    case "SETPOINT":
        marker = {
            enabled: true
        };
        valueDecimals = 1;
        type = "line";
        step = "left";
        break;
    case "VALVE_STATE":
        valueDecimals = 0;
        type = "line";
        step = "left";
        unit = "%";
        yAxis = 1;
        break;
    case "ABS_HUMIDITY":
        valueDecimals = 1;
        break;
    }

    if (DP.attributes.type === "BOOL") {
        yAxis = 2,
        valueDecimals = 0;
        type = "line";
        step = "left";
    }
    if (DP.attributes.unit === "%") {
        yAxis = 1,
        valueDecimals = 0;
        type = "line";
        step = "left";
        unit = "%";
    }
    if (DP_Grouping === 1) {
        grouping = {
            enabled: true,
        };
    } else if (DP_Grouping === 2) {
        grouping = {
            enabled: true,
            approximation: 'sum',
            groupPixelWidth: 50,
            units: [ [ 'hour', [1] ], 
                     [ 'day' , [1] ]                     
                   ]
        };
    } else {
        grouping = {
            enabled: false,
        };
    }
    if (DP.id.interfaceId === "SysVar") {
        var def_serie = {
            id: DP.idx,
            name: DP.attributes.displayName,
            type: type,
            step: step,
            yAxis: yAxis,
            marker: marker,
            visible: false,
            data: [],
            tooltip: {
                valueDecimals: valueDecimals,
                headerFormat: '<span style="font-size: 12px">{point.key}</span><br/>',
                pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.name}: <b>{point.y}</b><br/>',
                valueSuffix: ' ' + DP.attributes.unit,

            },
            dataGrouping: grouping,
            dataLabels : {
              enabled : DP_Labels,
				  allowOverlap: true,
              color: null,
              style: { "color": null, },
              formatter: function() {
                var last  = this.series.data[this.series.data.length - 1];                  
                if (last) {
                  if (this.point.category === last.category ) {
                     return this.series.name;
                }}
                return "";
              }
            },
        };
    } else {
        var def_serie = {
            id: DP.idx,
            name: DP.attributes.displayName + '.' + DP.id.identifier,
            type: type,
            step: step,
            yAxis: yAxis,
            marker: marker,
            visible: false,
            data: [],
            tooltip: {
                valueDecimals: valueDecimals,
                headerFormat: '<span style="font-size: 12px">{point.key}</span><br/>',
                pointFormat: '<span style="color:{point.color}">\u25CF</span> {series.name}: <b>{point.y}</b><br/>' + DP.id.interfaceId + '.' + DP.id.address + '.' + DP.id.identifier + '<br/>',
                valueSuffix: ' ' + DP.attributes.unit,

            },
            dataGrouping: grouping,
            dataLabels : {
              enabled : DP_Labels,
				  allowOverlap: true,
              color: null,
              style: { "color": null,  },
              formatter: function() {
                var last  = this.series.data[this.series.data.length - 1];
                if ( last ) {
                   if ( this.point.category === last.category ) {
                     return this.series.name;
                }}
                return "";
              }
            },
        };
    }

    chart.addSeries(def_serie, false, false);

}

/**
*  read timeSerien data for H2 database
*/
function getDataH2(sysvar, p_series) {
    var text;
    var chart = $('#container').highcharts();
    var series = chart.series[p_series];

    var sysvar2 = series.options.id.toString();

    var url = 'http://' + H2_server + ':' + H2_port
    url += '/query/jsonrpc.gy?j={%22id%22:' + series.index.toString()
    url += ',%22method%22:%22getTimeSeries%22'
    url += ',%22params%22:[' + sysvar2 + ',' + Zeitraum_Start.getTime() + ',' + Zeitraum_Ende.getTime() + ']}'

    // get serien data from H2 database
    $.ajax({
        type: "GET",
        url: url,
        dataType: "json",
        async: true,
        cache: false,
        success: function(result) {

            var arr = [];

            if (result.result.values) {
                // collect all timesstamps and Valuse
                if (DP_Grouping === 2) {
                   var last_value = result.result.values[0];
                   for (var i = 1; i < result.result.values.length; i++) {
                       arr.push([result.result.timestamps[i], Math.round((result.result.values[i]-last_value) * 1000) / 1000]);
                       last_value = result.result.values[i];
                   }
                } else {
                   for (var i = 0; i < result.result.values.length; i++) {
                       arr.push([result.result.timestamps[i], Math.round(result.result.values[i] * 1000) / 1000]);
                   }
                }
                if (arr.length > 0) {
                    // Add to serien data
                    var chart = $('#container').highcharts();
                    var series = chart.series[result.id];
                    if (series) {
                       series.setData(arr, true, false, false);
                    }
                    document.getElementById("count_val").innerHTML = (Number(document.getElementById("count_val").innerHTML) + result.result.values.length).toString();
                }
            }
        }
    });
    return;
}

/**
* Request data from the server, add it to the graph and set a timeout 
* to request again
*/
function requestData() {

    document.getElementById("count_val").innerHTML = "0";

    var url = 'http://' + H2_server + ':' + H2_port
    url += '/query/jsonrpc.gy?j={%22method%22:%22getDataPoint%22,%22params%22:%20[]}'

    $.ajax({
        type: "GET",
        url: url,
        dataType: "json",
        async: true,
        cache: false,
        success: function(result) {
            requestData2(result);
        },
    });
}

/**
* Request data from the server, add it to the graph and set a timeout 
* to request again
*/
function requestData2(TXT_JSON) {

    if (!TXT_JSON.result)
        return;
    // in result are all datapoint, let's check which are not hidden and active
    DP_point = TXT_JSON.result;

    // Sort data points on DisplayName
    DP_point.sort(function(a, b) {
        var x = a.attributes.displayName + '.' + a.id.identifier;
        var y = b.attributes.displayName + '.' + b.id.identifier;
        x = x.toLowerCase();
        y = y.toLowerCase();
        if (x < y) {
            return -1;
        }
        if (x > y) {
            return 1;
        }
        return 0;
    });

    // Alle Serien aufbauen und R?ume & Gewerke sammeln nur f?r anzeigbare
    for (i = 0; i < DP_point.length; i++) {
        if (!DP_point[i].historyDisabled && !DP_point[i].historyHidden) {
            //			  addSerie(DP_point[i]);

            // R?ume sammeln
            if (DP_point[i].attributes.room != null) {
                var t = DP_point[i].attributes.room.split(',');
                for (c = 0; c < t.length; c++) {
                    if (t[c] != '') {
                        if (DP_rooms.indexOf(t[c].trim()) === -1) {
                            DP_rooms.push(t[c].trim());
                        }
                    }
                }
            }
            // Gewerke sammeln
            if (DP_point[i].attributes.function != null) {
                var t = DP_point[i].attributes.function.split(',');
                for (c = 0; c < t.length; c++) {
                    if (t[c] != '') {
                        if (DP_gewerk.indexOf(t[c].trim()) === -1) {
                            DP_gewerk.push(t[c].trim());
                        }
                    }
                }
            }
        }

        // find idx of DP in link for filter
        if (DP_start.length > 0) {
           if (DP_point[i].id.interfaceId === "SysVar") {
              var txt_search = DP_point[i].attributes.displayName;
           } else {
              var txt_search = DP_point[i].id.address + '.' + DP_point[i].id.identifier;
           }
           txt_search = txt_search.toLowerCase();
           if ((DP_start.indexOf(txt_search) != -1) || (DP_start.indexOf(DP_point[i].idx.toString()) != -1)) {
               DP_Aktive.push(DP_point[i].idx);
           }
        }
    }
    // found idx from link
    if (DP_Aktive.length > 0) {
           DP_Limit = true;
    }
    DP_start = [];

    // Sort on Rooms
    DP_rooms.sort(function(a, b) {
        var x = a.toLowerCase();
        var y = b.toLowerCase();
        if (x < y) {
            return -1;
        }
        if (x > y) {
            return 1;
        }
        return 0;
    });

    var text = ''
    var select = document.getElementById("Select-Raum");

    // add default all and sysvar
    select.options[select.options.length] = new Option(ChhLanguage.default.historian.roomALL,'ALLES');
    select.options[select.options.length] = new Option(ChhLanguage.default.historian.sysvarALL,'SYSVAR');

    for (i = 0; i < DP_rooms.length; i++) {
        
        text = DP_rooms[i];

        if(ChhLanguage.default.historian[text]){
           text = ChhLanguage.default.historian[value];
		  }

        select.options[select.options.length] = new Option(text,DP_rooms[i]);
    }
    // Parameter room ?bergeben, jetzt Filter setzen
    if (DP_start_room) {
        for (i = 0; i < select.options.length; i++) {
            if (select.options[i].label.toLowerCase() === DP_start_room.toLowerCase() || select.options[i].value.toLowerCase() === DP_start_room.toLowerCase()) {
                select.value = select.options[i].value;
                break;
            }
            ;
        }
        ;
    }
    // Sort on Gewerk
    DP_gewerk.sort(function(a, b) {
        var x = a.toLowerCase();
        var y = b.toLowerCase();
        if (x < y) {
            return -1;
        }
        if (x > y) {
            return 1;
        }
        return 0;
    });

    var select = document.getElementById("Select-Gewerk");

    select.options[select.options.length] = new Option(ChhLanguage.default.historian.functionALL,'ALLES');

    for (i = 0; i < DP_gewerk.length; i++) {

        text = DP_gewerk[i];

        if(ChhLanguage.default.historian[text]){
           text = ChhLanguage.default.historian[value];
		  }

        select.options[select.options.length] = new Option(text,DP_gewerk[i]);
    };

    // Parameter function Übergeben, jetzt Filter setzen
    if (DP_start_func) {
        for (i = 0; i < select.options.length; i++) {
            if (select.options[i].label.toLowerCase() === DP_start_func.toLowerCase()) {
                select.value = select.options[i].value;
                break;
            }
        }
    };

    // Set start parameter 
    document.getElementById("filterFeld").value = filter_feld;

    // Display data
    ChangeEventRaumFilter();

    // check parameter Zoom from get-link
    if (location.search) {
        var parts = location.search.substring(1).split('&');
        for (var i = 0; i < parts.length; i++) {
            var nv = parts[i].split('=');
            if (!nv[0])
                continue;
            // parameter Zoom found
            if (nv[0].toLowerCase() === 'zoom') {
                var chart = $('#container').highcharts();
                var newStart = new Date(Zeitraum_Ende - (new Date(3600 * 1000 * parseFloat(nv[1]))));
                chart.xAxis[0].setExtremes(newStart.getTime(), Zeitraum_Ende.getTime(), true);
            }
        }
    }

    DP_start = [];

}

/**
* Create HighChart Object on loading
*/
$(document).ready(function() {

    // Translate to Language Set
    document.getElementById('button1').innerHTML = ChhLanguage.default.historian.buttonDay;
    document.getElementById('button2').innerHTML = ChhLanguage.default.historian.buttonWeek;
    document.getElementById('button3').innerHTML = ChhLanguage.default.historian.buttonMonth;
    document.getElementById('button4').innerHTML = ChhLanguage.default.historian.buttonALL;
    document.getElementById('refresh').innerHTML = ChhLanguage.default.historian.buttonRefresh;
    document.getElementById('createLink').innerHTML = ChhLanguage.default.historian.buttonLink;
    document.getElementById('count_text').innerHTML = ChhLanguage.default.historian.labelValues;
    document.getElementById('filterFeld').placeholder = ChhLanguage.default.historian.filterPlaceHolder;
    document.title = ChhLanguage.default.interface.pageTitle;

    // Add mouse wheel for legend
    (function(H) {
        H.wrap(H.Legend.prototype, 'render', function(proceed) {
            var legend = this;
            var chart = legend.chart;
            var animation = H.pick(legend.options.navigation.animation, true);

            proceed.apply(this, Array.prototype.slice.call(arguments, 1));

            $(legend.group.element).on('wheel', function(event) {
                if (Scroll_Legend) {
                    e = chart.pointer.normalize(event);
                    e.originalEvent.deltaY < 0 ? legend.scroll(-1, animation) : legend.scroll(1, animation);
                }
                Scroll_Legend = !Scroll_Legend;
            });
        });
    }(Highcharts));

    Highcharts.setOptions({
        global: {
            useUTC: false,
        },
        lang: ChhLanguage.default.highcharts,
    });
    // check parameter from get-link
    if (location.search) {
        var parts = location.search.substring(1).split('&');
        for (var i = 0; i < parts.length; i++) {
            var nv = parts[i].split('=');
            if (!nv[0])
                continue;
            // parameter Periode (Stunden)
            if (nv[0].toLowerCase() === 'periode') {
                Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(3600 * 1000 * parseInt(nv[1]))));
                // parameter Data Point
            } else if (nv[0].toLowerCase() === 'dp') {
                DP_start = decodeURIComponent(nv[1]).toLowerCase().split(',');
                // parameter Raum
            } else if (nv[0].toLowerCase() === 'room') {
                DP_start_room = decodeURIComponent(nv[1].toLowerCase());
                // parameter Gewerk
            } else if (nv[0].toLowerCase() === 'function') {
                DP_start_func = decodeURIComponent(nv[1].toLowerCase());
                // parameter FilterFeld
            } else if (nv[0].toLowerCase() === 'filterkey') {
                filter_feld = decodeURIComponent(nv[1].toLowerCase());
            } else if (nv[0].toLowerCase() === 'legend') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') { DP_Legend = false; }
            } else if (nv[0].toLowerCase() === 'navigator') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') { DP_Navigator = false; }
            } else if (nv[0].toLowerCase() === 'labels') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'true') { DP_Labels = true; }
            } else if (nv[0].toLowerCase() === 'daylight') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') { DP_DayLight = false; }
            } else if (nv[0].toLowerCase() === 'aggregation') {
                if (decodeURIComponent(nv[1].toLowerCase()) === '1') { DP_Grouping = 1; }
                if (decodeURIComponent(nv[1].toLowerCase()) === '2') { DP_Grouping = 2; }
            }
        }
    }

    if (DP_start.length >0) DP_Limit = true;

    // ajust height of content to screen height
    document.getElementById("container").setAttribute("style", "height:" + ($(document).height() - 160) + "px");

    // Create the chart
    $('#container').highcharts('StockChart', {
        chart: {
            events: {
                load: requestData,
            },
            zoomType: 'xy',
            resetZoomButton: {
               position: {
                  x: -50,
                  y: 20,
               }, 
               relativeTo: 'chart',
            },
        },

        rangeSelector: {
            buttons: [{
                count: 30,
                type: 'minute',
                text: ChhLanguage.default.highcharts.range30M
            }, {
                count: 1,
                type: 'hour',
                text: ChhLanguage.default.highcharts.rangeH
            }, {
                count: 6,
                type: 'hour',
                text: ChhLanguage.default.highcharts.range6H
            }, {
                count: 1,
                type: 'day',
                text: ChhLanguage.default.highcharts.rangeD
            }, {
                count: 1,
                type: 'week',
                text: ChhLanguage.default.highcharts.rangeW
            }, {
                count: 1,
                type: 'month',
                text: ChhLanguage.default.highcharts.rangeM
            }, {
                count: 1,
                type: 'year',
                text: ChhLanguage.default.highcharts.rangeY
            }, {
                type: 'all',
                text: ChhLanguage.default.highcharts.rangeALL
            }],
            allButtonsEnabled: true,
            inputEnabled: false,
            selected: 7,

            floating: true,
            verticalAlign: 'top',
            buttonPosition: {
                align: 'left',
                x: 0,
            },
            x: 0,
            y: 0,
        },

        navigation: {
           buttonOptions: {
               enabled: true,
           }
        },
        navigator: {
			  enabled: DP_Navigator,
        },

        exporting: {
          buttons: {
            contextButton: {
              symbol: "menu",
              enabled: true,
              menuItems: [{
                text: (DP_Legend) ? ChhLanguage.default.highcharts.legenddeactive: ChhLanguage.default.highcharts.legendactive,
                onclick: function() {
                  if (this.legend.display) {
                     $('.highcharts-contextmenu')[0].children[0].children[0].innerHTML = ChhLanguage.default.highcharts.legendactive;
                     this.legend.update( { enabled: false, } );
                  } else {
                     $('.highcharts-contextmenu')[0].children[0].children[0].innerHTML = ChhLanguage.default.highcharts.legenddeactive;
                     this.legend.update( { enabled: true, } );
                  }
                 }
              },{
                text: (DP_Navigator) ? ChhLanguage.default.highcharts.navigatordeactive: ChhLanguage.default.highcharts.navigatoractive,
                onclick: function() {
                  if (this.navigator.navigatorEnabled) {
                    $('.highcharts-contextmenu')[0].children[0].children[1].innerHTML = ChhLanguage.default.highcharts.navigatoractive;
                    this.navigator.update( { enabled: false, } );
    					 this.redraw();
                  } else {
                    $('.highcharts-contextmenu')[0].children[0].children[1].innerHTML = ChhLanguage.default.highcharts.navigatordeactive;
                    this.navigator.update( { enabled: true, } );
    					 this.redraw();
                  }
                }
              },{
                text: (DP_Labels) ? ChhLanguage.default.highcharts.labelsdeactive: ChhLanguage.default.highcharts.labelsactive,
                onclick: function() {
                  if (DP_Labels) {
                    $('.highcharts-contextmenu')[0].children[0].children[2].innerHTML = ChhLanguage.default.highcharts.labelsactive;
							DP_Labels = false;
                  } else {
                    $('.highcharts-contextmenu')[0].children[0].children[2].innerHTML = ChhLanguage.default.highcharts.labelsdeactive;
							DP_Labels = true;
                  }
						ChangeEventRaumFilter();
                },
              },{
                text: (DP_DayLight) ? ChhLanguage.default.highcharts.daylightdeactive: ChhLanguage.default.highcharts.daylightactive,
                onclick: function() {
                  if (DP_DayLight) {
                    $('.highcharts-contextmenu')[0].children[0].children[3].innerHTML = ChhLanguage.default.highcharts.daylightactive;
							DP_DayLight = false;
                  } else {
                    $('.highcharts-contextmenu')[0].children[0].children[3].innerHTML = ChhLanguage.default.highcharts.daylightdeactive;
							DP_DayLight = true;
                  }
						ChangeEventRaumFilter();
                },
              },{
                text: (DP_Limit) ? ChhLanguage.default.highcharts.limitactive: ChhLanguage.default.highcharts.limitdeactive,
                onclick: function() {
                  if (DP_Limit) {
                    $('.highcharts-contextmenu')[0].children[0].children[4].innerHTML = ChhLanguage.default.highcharts.limitdeactive;
							DP_Limit = false;
                  } else {
                    $('.highcharts-contextmenu')[0].children[0].children[4].innerHTML = ChhLanguage.default.highcharts.limitactive;
							DP_Limit = true;
                  }
						ChangeEventRaumFilter();
                },
              },{
                text: (DP_Grouping===0) ? ChhLanguage.default.highcharts.aggractive1: ((DP_Grouping===1) ? ChhLanguage.default.highcharts.aggractive2: ChhLanguage.default.highcharts.aggrdeactive),
                onclick: function() {
                  if (DP_Grouping === 0) {
                    $('.highcharts-contextmenu')[0].children[0].children[5].innerHTML = ChhLanguage.default.highcharts.aggractive2;
						  DP_Grouping = 1;
                  } else if (DP_Grouping === 1) {
                    $('.highcharts-contextmenu')[0].children[0].children[5].innerHTML = ChhLanguage.default.highcharts.aggrdeactive;
						  DP_Grouping = 2;
                  } else {
                    $('.highcharts-contextmenu')[0].children[0].children[5].innerHTML = ChhLanguage.default.highcharts.aggractive1;
						  DP_Grouping = 0;
                  }
						ChangeEventRaumFilter();
                },
              }, "separator", "printChart", "downloadPNG", "downloadJPEG", "downloadPDF", "downloadSVG",
              ]
            }
          }
        },

        title: {//            text : 'H2 Demo'
        },

	     credits: {
          enabled: false,
        },

        xAxis: {
            type: 'datetime',
            ordinal: false,
            dataMax: Date.now(),
            events: {
                afterSetExtremes: function() {

					    for (var serie = 0; serie < this.series.length; serie++) {
                       if (this.series[serie].visible && this.series[serie].options.group != "nav") {
                          var grouping = this.series[this.series.length-1].currentDataGrouping;
                          if (grouping) {
                             var text = grouping.unitName;
                             if(ChhLanguage.default.highcharts['aggr'+text]){
                                text = ChhLanguage.default.highcharts['aggr'+text];
                             }
					              document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.highcharts.aggrtxt1 + ': ' + grouping.count + '/' + text;
                          } else {
					              document.getElementById('aggr_text').innerHTML = ' -  ' + ChhLanguage.default.highcharts.aggrtxt0;
					           }
                          break;
                       }
                   };

                },
            },
        },

        yAxis: [{
            id: "AXISY0",
            lineWidth: 2,
            opposite: false,
            showEmpty: false,
        }, {
            id: "AXISY1",
            title: {
                text: ChhLanguage.default.highcharts.axisYpercentage,
            },
            softMax: 100,
            softMin: 0,
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11,
        }, {
            id: "AXISY2",
            title: {
                text: ChhLanguage.default.highcharts.axisYstate,
            },
            softMax: 1,
            softMin: 0,
            maxPadding: 0.1,
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            tickAmount: 7,
        }],

        legend: {
            enabled: DP_Legend,
            layout: 'vertical',
            backgroundColor: '#FFFFFF',
            align: 'left',
            verticalAlign: 'top',
            floating: false,
            x: 0,
            y: 0,
            navigation: {
                arrowSize: 20,
            },
        },

        plotOptions: {
            series: {
                events: {
                    legendItemClick: function(event) {
                        var visibility = this.visible ? 'visible' : 'hidden';
                        if (this.data.length === 0 && !this.visible) {
                            getDataH2(this.name, this.index);
                        }
                        if (this.visible) {
                           var pos = DP_Aktive.indexOf(this.options.id);
                           if (pos != -1) {
                              DP_Aktive.splice(pos,1);
                           }
                        } else {
                           DP_Aktive.push(this.options.id);
                        }
                        return true;
                    }
                }
            },
        },

        series: [{
            id: 'series-init',
            name: 'loading ....',
            data: [],
            yAxis: 0,
            visible: false,
        }]
    });

    // *** set function for Filter_Feld
    $("#filterFeld").on("keyup", function() {
        filter_feld = $(this).val().toLowerCase();
        ChangeEventRaumFilter();
    });

    // *** set function for Filter Room
    $("#Select-Raum").on("change", function() {
        ChangeEventRaumFilter();
    });

    // *** set function for Filter Room
    $("#Select-Gewerk").on("change", function() {
        ChangeEventRaumFilter();
    });

    // **********************
    $('#button1').click(function() {
        Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(86400000 * 1)));
        loadNewSerienData();
    });

    // **********************
    $('#button2').click(function() {
        Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(86400000 * 7)));
        loadNewSerienData();
    });

    // **********************
    $('#button3').click(function() {
        Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(86400000 * 30)));
        loadNewSerienData();
    });

    // **********************
    $('#button4').click(function() {
        Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(86400000 * 2 * 365)));
        loadNewSerienData();
    });

    // **********************
    $('#refresh').click(function() {
        Zeitraum_Ende = new Date(Date.now());
        loadNewSerienData();
    });
    // **********************
    $('#createLink').click(function() {
        createUrl();
    });

});

// *******************
function ChangeEventRaumFilter() {
    var filter_raum = document.getElementById("Select-Raum").value;
    var filter_gewerk = document.getElementById("Select-Gewerk").value;
    var save_active = [];
    var save_active_found = false;

    var chart = $('#container').highcharts();
    var series;

    // remove all old series
    for (i = chart.series.length - 1; i >= 0; i--) {
        chart.series[i].remove(false);
    }

    // add new series which are in filter
    for (i = 0; i < DP_point.length; i++) {
        if (check_filter(filter_raum, filter_gewerk, DP_point[i])) {

            addSerie(DP_point[i]);
            series = chart.get(DP_point[i].idx);

            // check if active before refresh
            if (DP_Aktive.indexOf(DP_point[i].idx) != -1) {
                series.visible = true;
                save_active_found = true;
            }
        }
    }

    loadNewPlotBand();
    if (save_active_found) {
        loadNewSerienData();
    }
    chart.redraw();
}

//*******
function check_filter(p_raum, p_gewerk, p_dp) {

    // Generell Filter
    if (p_dp.historyDisabled || p_dp.historyHidden) return false;

    // Room Filter
    if (p_raum != "ALLES" && p_raum != "SYSVAR") {
        if (p_dp.attributes.room === null) return false;
        if (p_dp.attributes.room.indexOf(p_raum) === -1) return false;
    }
    if (p_raum != "ALLES" && p_raum === "SYSVAR" && !p_dp.displayName.includes("SysVar.")) return false;

    // Function Filter
    if (p_gewerk != "ALLES") {
        if (p_dp.attributes.function === null) return false;
        if (p_dp.attributes.function.indexOf(p_gewerk) === -1) return false;
    }

    // Description Filter
    if (filter_feld != '') {
        var ft = filter_feld.split(' ');
        for (fi = 0; fi < ft.length; fi++) {
            if ((p_dp.displayName + "/" + p_dp.id.address + "/ID:" + p_dp.idx).toLowerCase().indexOf(ft[fi]) === -1) return false;
        }
    }

    // Show only DP which are in Link or aktiv marked
    if (DP_Limit && DP_Aktive.length > 0) {
       if (DP_Aktive.indexOf(p_dp.idx) === -1) return false;
    }

    return true;
}

//********************
function loadNewSerienData() {

    var chart = $('#container').highcharts();
    // Set Line Color back
	 DP_LineColor = 0;
    for (var serie = 0; serie < chart.series.length; serie++) {
        if (chart.series[serie].visible && chart.series[serie].options.group != "nav") {
            getDataH2("", serie)
        }
    };

    chart.xAxis[0].setExtremes(Zeitraum_Start.getTime(), Zeitraum_Ende.getTime(), true);

    loadNewPlotBand()
    chart.redraw();
}

//********************
function loadNewPlotBand() {

    // add plotband for every day 00-06 and 20-24 gray, 06-20 yellow mean day

  var chart = $('#container').highcharts();


  // remove all PlotBands from xAxis[0]
  for (var band = chart.xAxis[0].plotLinesAndBands.length-1; band >= 0  ; band--) {
      var band_id = chart.xAxis[0].plotLinesAndBands[band].id;
      chart.xAxis[0].removePlotBand( band_id );
  }


  if (DP_DayLight) {

    var id = 1;

    for (var loopDate = Zeitraum_Start.getTime(); loopDate <= Zeitraum_Ende.getTime(); loopDate += 86400000) {
        var start = new Date(loopDate);

        chart.xAxis[0].addPlotBand({
            color: '#EFE8E7',
            from: start.setHours(0, 0, 0, 0),
            to: start.setHours(6, 0, 0, 0),
            id: ('DayLight1' + id.toString()),
        });
        chart.xAxis[0].addPlotBand({
            color: '#FCFFC5',
            from: start.setHours(6, 0, 0, 0),
            to: start.setHours(20, 0, 0, 0),
            id: ('DayLight2' + id.toString()),
        });
        chart.xAxis[0].addPlotBand({
            color: '#EFE8E7',
            from: start.setHours(20, 0, 0, 0),
            to: start.setHours(23, 59, 59, 999),
            id: ('DayLight3' + id.toString()),
        });

        id++;
    }
  }
}

//********************
function createUrl() {

    var chart = $('#container').highcharts();

    var url = location.pathname + "?";

    // Add Periode Parameter
    url += 'periode=' + (Math.round(((Zeitraum_Ende - Zeitraum_Start) / (60 * 60 * 1000)) * 100) / 100).toString();

    var url2 = '';
    // Add DP Filter if some selected
    var chart = $('#container').highcharts();
    for (var serie = 0; serie < chart.series.length; serie++) {
        if (chart.series[serie].visible && chart.series[serie].options.group != "nav") {
            url2 += chart.series[serie].options.id + ',';
/*
            for (i = 0; i < DP_point.length; i++) {
                if (DP_point[i].idx === chart.series[serie].options.id) {
                    if (DP_point[i].id.interfaceId === "SysVar") {
                        url2 += DP_point[i].attributes.displayName + ',';
                    } else {
                        url2 += DP_point[i].id.address + '.' + DP_point[i].id.identifier + ',';
                    }
                    break;
                };
            };
*/
        }
    };
    
    if (url2.length > 0) {
        url += '&dp=' + url2.substring(0, url2.length - 1);
    }

    // Add Room to Link if needed
    var filter_raum = document.getElementById("Select-Raum").value;
    if (filter_raum != 'ALLES') {
        url += '&room=' + filter_raum;
    }

    // Add Gewerk to Link if needed
    var filter_gewerk = document.getElementById("Select-Gewerk").value;
    if (filter_gewerk != 'ALLES') {
        url += '&function=' + filter_gewerk;
    }

    // Add FilterFeld to Link if needed
    if (filter_feld != '') {
        url += '&filterkey=' + filter_feld;
    }

    // Add Zoom if not full
    var extremes = chart.xAxis[0].getExtremes();
    if (extremes.max != extremes.dataMax || extremes.min != extremes.dataMin) {
        url += '&zoom=' + (Math.round(((extremes.max - extremes.min) / (60 * 60 * 1000)) * 100) / 100).toString();
    }

	 // Legend not show    
    if (!chart.legend.display) {
        url += '&legend=false';
	 }

	 // Navigator not show    
    if (!chart.navigator.navigatorEnabled) {
        url += '&navigator=false';
    }

	 // Labels show    
    if (DP_Labels) {
        url += '&labels=true';
    }

	 // DayLight show    
    if (!DP_DayLight) {
        url += '&daylight=false';
    }

	 // Grouping show    
    if (DP_Grouping != 0) {
        url += '&aggregation='+DP_Grouping;
    }

    window.open(url, '_blank');
    window.focus();
}
