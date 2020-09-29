/* *********************************
 * HighChart javascripts by wak 2019
 ************************************/

// Setup H2 Database Services, default set to same server as this webpage and port 8082
var H2_server = location.hostname;
var H2_port = (location.port === "") ? 80 : location.port;
var H2_refreshSec = 60;
// Refresh Time is enabled

// declare global Variables
var H2_version = 'v3.10';
var chart;
var filter_feld = '';
var DP_point = [];
var DP_settings = {};
var DP_settings_old = {};
var DP_ApiKey = "";
var DP_DataPointFilter = 0; 
var Zeitraum_Ende = new Date(Date.now());
var Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(86400000 * 1)));
var Scroll_Legend = true;
var DP_Legend = 1;
var DP_Navigator = 0;
var DP_Labels = 0;
var DP_Grid = 2;
var DP_DayLight = 1;
var DP_Limit = false;
var DP_AutoRefresh = 0;
var DP_ShowFilter = 1;
var AutoRefreshCount = 0;
var DP_attribute = [];
var DP_PopupID;
var DP_PopupAxisObj;
var DP_PopupAxisPos;
var DP_Theme = 'Standard';
var DP_Theme_Setting;
var DP_DashType = ['Solid', 'Dash', 'DashDot', 'Dot', 'LongDash', 'LongDashDot', 'LongDashDotDot', 'ShortDash', 'ShortDashDot', 'ShortDashDotDot', 'ShortDot'];
var DP_Queue = [];
var DP_ColorNext = 0;
var DP_Title = '';
var DP_Subtitle = '';
var DP_Loading = 0;
var DP_yAxis = [{
    position: false,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis0,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 1,
    min: 10,
    max: 30,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis1,
    color: 1,
    type: 0
}, {
    position: true,
    limit: 1,
    min: -20,
    max: 50,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis2,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 1,
    min: 20,
    max: 90,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis3,
    color: 1,
    type: 0
}, {
    position: true,
    limit: 1,
    min: 0,
    max: 100,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis4,
    color: 1,
    type: 0
}, {
    position: true,
    limit: 1,
    min: 0,
    max: 2,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis5,
    color: 1,
    type: 0
}, {
    position: true,
    limit: 1,
    min: 20,
    max: 100,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis6,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 1,
    min: 900,
    max: 1000,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis7,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 1,
    min: 0,
    max: 5000,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis8,
    color: 1,
    type: 0
}, {
    position: true,
    limit: 1,
    min: 300,
    max: 3000,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis9,
    color: 1,
    type: 0
}, {
    position: true,
    limit: 1,
    min: 3,
    max: 15,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis10,
    color: 1,
    type: 0
}, {
    position: true,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis11,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis12,
    color: 1,
    type: 1
}, {
    position: false,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis13,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis14,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis15,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis16,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis17,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis18,
    color: 1,
    type: 0
}, {
    position: false,
    limit: 0,
    min: 0,
    max: 0,
    tick: 11,
    text: ChhLanguage.default.highcharts.yaxis19,
    color: 1,
    type: 0
}];
var DP_yAxis_default = JSON.parse(JSON.stringify(DP_yAxis));

function createChart() {
    if (DP_Theme != 'Standard' && DP_Themes[DP_Theme]) {
    	DP_Theme_Setting = Highcharts.merge(DP_Themes.Standard, DP_Themes[DP_Theme]);
    } else {
    	DP_Theme_Setting = Highcharts.merge(DP_Themes.Standard, {});
    }
    Highcharts.setOptions(DP_Theme_Setting);
    ChartSetOptions();
    chartSetElements();
}

/**
* define default display attributes
*/
function defaultAttrib(DP, colorNr, idx) {

    // add default from fix settings

    var attr = {
                id: idx,
                aggr: 'A0',
                atime: 'T1',
                yaxis: 'Y0',
                comp: 'C0',
                line: 'L0',
                mark: 'M0',
                color: 'F0',
                visible: 0,
                dash: 'D0',
                width: 'W2',
                stack: 0,
                factor: 1,            
                offset: 0,
                unit: '',
                buffer_data: {
                              timestamps: [],
                              values: [],
                              buffer_start: 0,
                              buffer_end: 0
                             },
               };
  if (colorNr === -1) {
     attr.color = 'F' + ( DP_ColorNext % chart.options.colors.length );
     DP_ColorNext++;
  } else {
     attr.color = 'F' + ( colorNr % chart.options.colors.length );
  }

  if (DP != -1) {
    attr.unit = DP.attributes.unit;

    switch (DP.id.identifier) {
    case "ABS_HUMIDITY":
        attr.yaxis = 'Y10';
        break;
    case "HUMIDITY":
    case "HUMIDITYF":
    case "ACTUAL_HUMIDITY":
    case "HUM_MAX_24H":
    case "HUM_MIN_24H":
        attr.yaxis = 'Y6';
        break;
    case "TEMPERATURE":
    case "ACTUAL_TEMPERATURE":
    case "ABS_HUMIDITY":
    case "DEW_POINT":
    case "TEMP_MAX_24H":
    case "TEMP_MIN_24H":
        attr.yaxis = 'Y1';
        break;
    case "SET_TEMPERATURE":
    case "SETPOINT":
        attr.yaxis = 'Y1';
        attr.line  = 'L2';
        break;
    case "MEAN5MINUTES":
        attr.yaxis = 'Y3';
        break;
    case "BRIGHTNESS":
        attr.yaxis = 'Y8';
        break;
    case "LEVEL":
        attr.yaxis = 'Y4';
        attr.line  = 'L2';
        attr.unit  = '';
        break;
    case "STATE":
        attr.yaxis = 'Y5';
        attr.line  = 'L2';
        break;
    case "PRESS_SHORT":
    case "PRESS_LONG":
    case "PRESS_OPEN":
    case "MOTION":
        attr.yaxis = 'Y5';
        attr.mark  = 'M1';
        attr.factor  = 5;
        attr.line  = 'L5';
        break;
    case "VALVE_STATE":
        attr.yaxis = 'Y4';
        attr.line  = 'L2';
        attr.unit  = '%';
        break;
    }

    if (DP.attributes.type === "BOOL") {
        attr.yaxis = 'Y5';
        attr.line  = 'L2';
    }
    if (DP.attributes.unit === "%") {
        attr.yaxis = 'Y4';
        attr.line  = 'L2';
        attr.unit  = '%';
    }
    if (DP.id.interfaceId === "SysVar" && DP.attributes.unit === "°C") {
        attr.yaxis = 'Y1';
        attr.line  = 'L0';
    }

    // add default from database
    if (DP.attributes.custom && DP.attributes.custom.HighChart) {
    
      var text2 = DP.attributes.custom.HighChart.split('|');
      if (text2.length > 0) {
        for (var k = 0; k < text2.length; k++) {
            if (text2[k].substr(0, 1) === 'A') {
              	attr.aggr = text2[k];
            } else if (text2[k].substr(0, 1) === 'Y') {
              	attr.yaxis = text2[k];
            } else if (text2[k].substr(0, 1) === 'T') {
              	attr.atime = text2[k];
            } else if (text2[k].substr(0, 1) === 'F') {
              	attr.color = text2[k];
            } else if (text2[k].substr(0, 1) === 'C') {
              	attr.comp = text2[k];
            } else if (text2[k].substr(0, 1) === 'L') {
              	attr.line = text2[k];
            } else if (text2[k].substr(0, 1) === 'M') {
              	attr.mark = text2[k];
            } else if (text2[k].substr(0, 1) === 'D') {
              	attr.dash = text2[k];
            } else if (text2[k].substr(0, 1) === 'W') {
              	attr.width = text2[k];
            } else if (text2[k].substr(0, 1) === 'V') {
              	attr.visible = parseInt(text2[k].substr(1, 1));
            } else if (text2[k].substr(0, 1) === 'S') {
              	attr.stack = parseInt(text2[k].substr(1, 2));
            } else if (text2[k].substr(0, 1) === 'U') {
              	attr.unit = text2[k].substr(1, 20).replace('*','%');
            } else if (text2[k].substr(0, 1) === 'X') {
              	attr.factor = parseFloat(text2[k].substr(1, 10));
            } else if (text2[k].substr(0, 1) === 'O') {
              	attr.offset = parseFloat(text2[k].substr(1, 10));
            }
        }
      }
    }
   }
   // give back default values 
   return attr;
}


/**
* create serien option and add it to HighStock Chart
*/
function addSerie(DP, DP_type) {

    var unit;
    var yAxis;
    var dp_vis;
    var type;
    var step;
    var color;
    var lineType;
    var aggrType;
    var aggrTime;
    var stacking;
    var dashtype;
    var linewidth;
    var marker;

    var grouping = undefined;
    var dptype = DP.id.identifier;
    var valueDecimals = 1;
    var factor = 1;

    var attrIDX = (DP_type === '') ? DP.idx.toString() : (DP_type + '_' + DP.idx.toString());

    var attr = DP_attribute.findIndex(obj => obj.id === attrIDX);

    if (attr === -1) {
        DP_attribute.push( defaultAttrib(DP, -1, attrIDX ) );
        attr = DP_attribute.findIndex(obj => obj.id === attrIDX);
    }

    yAxis = parseInt(DP_attribute[attr].yaxis.substr(1, 2));
    color = chart.options.colors[parseInt(DP_attribute[attr].color.substr(1, 2))];
    aggrType = parseInt(DP_attribute[attr].aggr.substr(1, 2))
    aggrTime = parseInt(DP_attribute[attr].atime.substr(1, 2))
    lineType = parseInt(DP_attribute[attr].line.substr(1, 2))
    dp_vis = DP_attribute[attr].visible;
    unit = DP_attribute[attr].unit;

    stacking = DP_attribute[attr].stack;

    dashtype = DP_DashType[ parseInt(DP_attribute[attr].dash.substr(1, 2)) ];

    linewidth = parseInt(DP_attribute[attr].width.substr(1, 2));

    marker = defineMarker( parseInt(DP_attribute[attr].mark.substr(1, 2)), color, linewidth );

    switch (dptype) {
    case "MEAN5MINUTES":
        valueDecimals = 3;
        break;
    case "STATE":
    case "VALVE_STATE":
        valueDecimals = 0;
        break;
    }

    if (DP.attributes.type === "BOOL") {
        valueDecimals = 0;
    }
    if (DP.attributes.unit === "%") {
        valueDecimals = 0;
    }
    if (DP.id.interfaceId === "SysVar" && DP.attributes.unit === "°C") {
        valueDecimals = 1;
    }

    if (lineType === 0) {
        type = "spline";
        step = "";
    } else if (lineType === 1) {
        type = "line";
        step = "";
    } else if (lineType === 2) {
        type = "line";
        step = "left";
    } else if (lineType === 3) {
        type = "line";
        step = "center";
    } else if (lineType === 4) {
        type = "line";
        step = "right";
    } else if (lineType === 5) {
        type = "scatter";
        step = "";
    } else if (lineType === 6) {
        type = "areaspline";
        step = "";
    } else if (lineType === 7) {
        type = "area";
        step = "";
    } else if (lineType === 8) {
        type = "area";
        step = "left";
    } else if (lineType === 9) {
        type = "area";
        step = "center";
    } else if (lineType === 10) {
        type = "area";
        step = "right";
    } else if (lineType === 11) {
        type = "column";
        step = "";
    }

    var groupUnits = [];
    var groupforced = false;
    var groupsmoothed = false;
    var groupwidth = 2;

    // fine grouping Highchart standard
    if (aggrTime === 0) {
        groupforced = false;
        groupwidth = 50;
        groupUnits = null;

        // dyna. grouping start by 15 min.
    } else if (aggrTime === 1) {
        groupforced = true;
        groupwidth = 50;
        groupUnits = [['minute', [15, 30]], ['hour', [1, 2, 3, 4, 6, 8, 12]], ['day', [1]], ['week', [1]], ['month', [1, 3, 6]], ['year', [1]], ];

        // dyna. only hours, days and month
    } else if (aggrTime === 2) {
        groupUnits = [['hour', [1]], ['day', [1]], ['month', [1]], ['year', [1]], ];
        // fix hours
    } else if (aggrTime === 3) {
        groupUnits = [['hour', [1]], ];
        groupforced = true;
    } else if (aggrTime === 4) {
        groupUnits = [['day', [1]], ];
        groupforced = true;
    } else if (aggrTime === 5) {
        groupUnits = [['week', [1]], ];
        groupforced = true;
    } else if (aggrTime === 6) {
        groupUnits = [['month', [1]], ];
        groupforced = true;
    } else if (aggrTime === 7) {
        groupUnits = [['month', [3]], ];
        groupforced = true;
    } else if (aggrTime === 8) {
        groupUnits = [['year', [1]], ];
        groupforced = true;
    } else if (aggrTime === 9) {
        groupUnits = [['hour', [3]], ];
        groupforced = true;
    } else if (aggrTime === 10) {
        groupUnits = [['hour', [6]], ];
        groupforced = true;
    } else if (aggrTime === 11) {
        groupUnits = [['hour', [8]], ];
        groupforced = true;
    } else if (aggrTime === 12) {
        groupUnits = [['hour', [12]], ];
        groupforced = true;
    }

    if (aggrType === 1) {
        grouping = {
            enabled: true,
            approximation: 'average',
            forced: groupforced,
            smoothed: groupsmoothed,
            groupPixelWidth: groupwidth,
            units: groupUnits,
        };
    } else if (aggrType === 2) {
        grouping = {
            enabled: true,
            approximation: 'sum',
            forced: groupforced,
            smoothed: groupsmoothed,
            groupPixelWidth: groupwidth,
            units: groupUnits,
        };
    } else if (aggrType === 3) {
        grouping = {
            enabled: true,
            approximation: 'average',
            forced: groupforced,
            smoothed: groupsmoothed,
            groupPixelWidth: groupwidth,
            units: groupUnits,
        };
        type = (type = "line") ? "spline" : type;
    } else if (aggrType === 4) {
        grouping = {
            enabled: true,
            approximation: 'sum',
            forced: groupforced,
            smoothed: groupsmoothed,
            groupPixelWidth: groupwidth,
            units: groupUnits,
        };
    } else if (aggrType === 5) {
        grouping = {
            enabled: true,
            approximation: 'sum',
            forced: groupforced,
            smoothed: groupsmoothed,
            groupPixelWidth: groupwidth,
            units: groupUnits,
        };
    } else if (aggrType === 6) {
        grouping = {
            enabled: false,
        };
    } else if (aggrType === 7) {
        grouping = {
            enabled: true,
            approximation: 'sum',
            forced: groupforced,
            smoothed: groupsmoothed,
            groupPixelWidth: groupwidth,
            units: groupUnits,
        };
    } else {
        grouping = {
            enabled: false,
        };
    }

    var pointFormater = null;
    var pointFormat = null;
    var serienName = '';

    pointFormater = function() {
        var xDate = new Date(this.x + (getComparisionBackDay(this.series.options.id.split('_')[0])));

        var txta = "<span style='fill:" + this.color + "'>\u25CF </span>" + this.series.name + ": <b>" + Highcharts.numberFormat(this.y, 2, ",", ".") + " " + this.series.tooltipOptions.valueSuffix + "</b><br/>";

        if (this.series.hasGroupedData) {

            var aggrType = '';
            if (this.series.options.id) {
                attr = DP_attribute.findIndex(obj=>obj.id === this.series.options.id.toString());
                aggrType = 0;
                if (attr != -1) {
                    aggrType = parseInt(DP_attribute[attr].aggr.substr(1, 2));
                }
            }

            var pointRange;
            if (this.series.currentDataGrouping.totalRange) {
                pointRange = this.series.currentDataGrouping.totalRange;
            }

            var xEnde = Highcharts.dateFormat('%H:%M', xDate.getTime() + pointRange);
            if (xEnde == '00:00')
                xEnde = '24:00';

            // get Timeframe text
            if (pointRange < 3600000) {
                txta += "<b>" + Highcharts.dateFormat('%A, %e. %b %Y, %H:%M', xDate) + '-' + xEnde + "</b>";
            } else if (pointRange < 86400000) {
                txta += "<b>" + Highcharts.dateFormat('%A, %e. %b %Y, %H:%M', xDate) + '-' + xEnde + "</b>";
            } else if (pointRange < 86400000*20) {
                txta += "<b>" + Highcharts.dateFormat('%e. %b', xDate) + '-' +Highcharts.dateFormat('%e. %b %Y', xDate.getTime() + pointRange - 86400000) + "</b>";
            } else {
                txta += "<b>" + Highcharts.dateFormat('%b %Y', xDate) + "</b>";
            }

            // get Aggregation Symbol
            txta += '<i> (<b>';
            if (aggrType === 1)
                txta += jQuery('<div/>').html('&#x00d8; ').text();
            // average
            if (aggrType === 2)
                txta += jQuery('<div/>').html('&#x0394; ').text();
            // delta
            if (aggrType === 3)
                txta += jQuery('<div/>').html('&#x03a8; ').text();
            // min/max
            if (aggrType === 4)
                txta += jQuery('<div/>').html('&#x01a9; ').text();
            // sum
            if (aggrType === 5)
                txta += jQuery('<div/>').html('&#x01ac; ').text();
            // TIME_ON
            if (aggrType === 6)
                txta += jQuery('<div/>').html('&#x2248; ').text();
            // delta+
            if (aggrType === 7)
                txta += jQuery('<div/>').html('&#x2359; ').text();

            var grouping = this.series.currentDataGrouping;
            if (grouping) {
                var text = grouping.unitName + ((grouping.count > 1) ? '2' : '').toString();
                if (ChhLanguage.default.highcharts['aggr' + text]) {
                    text = ChhLanguage.default.highcharts['aggr' + text];
                }
                txta += '</b> ' + grouping.count + ' ' + text;
            }
            txta += ")</i><br/>";

        } else {
            txta += "<b>" + Highcharts.dateFormat('%A, %b %e, %H:%M:%S', xDate) + "</b>";
        }
        return txta;
    }

    if (DP_type.substr(0, 1) === 'C') {
        serienName = (DP.id.interfaceId === "SysVar") ? (DP.attributes.displayName) : (DP.attributes.displayName + '.' + DP.id.identifier) + '(' + ChhLanguage.default.historian['comptype' + DP_type] + ')'
    } else if (DP.id.interfaceId === "SysVar") {
        serienName = DP.attributes.displayName;
    } else {
        serienName = DP.attributes.displayName + '.' + DP.id.identifier;
    }

    var def_serie = {
        id: attrIDX,
        name: serienName,
        type: type,
        step: step,
        yAxis: yAxis,
        marker: marker,
        visible: (dp_vis === 2) ? true : false,
        color: color,
        dashStyle: dashtype,
        lineWidth: linewidth,
        fillOpacity: 0.4,
        borderColor: color,
        borderWidth: 2,
        stacking: (stacking == 0)?null:'normal',
        stack: (stacking == 0)?null:('group'+stacking),
        data: [],
        tooltip: {
            valueDecimals: valueDecimals,
            pointFormat: pointFormat,
            pointFormatter: pointFormater,
            valueSuffix: ' ' + unit,
        },
        dataGrouping: grouping,
        dataLabels: defineDataLabels(),
    };

    // Create Chart Serie !!!
    var serie2 = chart.addSeries(def_serie, false, false);

}

function defineDataLabels() {
	
	var objLabels;
	
	if (DP_Labels == 0) {
		objLabels = { enabled: false }; 
	} else if (DP_Labels == 1) {
		objLabels = { enabled: true,
	    	          allowOverlap: true,
	    	          color: null,
	    	          style: {
	    	              "color": null,
	    	          },
	    	          formatter: function() {
	    	              var last = this.series.data[this.series.data.length - 1];
                          if (this.point.category === last.category) {
                              return this.series.name;
	    	              }
	    	              return "";
	    	          }
		            };
	
	} else if (DP_Labels == 2) {
		objLabels = { enabled: true,
  	                  allowOverlap: false,
  	                  color: null,
  	                  style: {
  	                      "color": null,
  	                  },
  	                  formatter: function() {
  	        	          return Highcharts.numberFormat(this.y,1);;
  	                  }
                    };
	}
    
    return objLabels;
}

function defineMarker(iMarker, strColor, iLineW) {
	
    var objMarker = {
            enabled: false,
            states: {
                hover: {
                    enabled: true,
                }
            }
        };
    if (iLineW<3) {
        var iRadius = 4;
    } else {
        var iRadius = (4 + iLineW - 2 );
    }
    if (iMarker > 0 && iMarker <= chart.options.symbols.length) {
        objMarker = {
                enabled: true,
                symbol: chart.options.symbols[iMarker - 1],
                radius: iRadius,
                lineColor: strColor,
                lineWidth: 0,
                fillColor: strColor,
            };
    } else if (iMarker > chart.options.symbols.length && iMarker <= chart.options.symbols.length*2) {
        objMarker = {
                enabled: true,
                symbol: chart.options.symbols[iMarker - 1 - chart.options.symbols.length],
                radius: iRadius,
                lineColor: 'black',
                lineWidth: 1,
                fillColor: strColor,
            };
    } else if (iMarker > chart.options.symbols.length*2 && iMarker <= chart.options.symbols.length*3) {
        objMarker = {
                enabled: true,
                symbol: chart.options.symbols[iMarker - 1 - chart.options.symbols.length*2],
                radius: iRadius,
                lineColor: 'white',
                lineWidth: 1,
                fillColor: strColor,
            };
    } else if (iMarker > chart.options.symbols.length*3 && iMarker <= chart.options.symbols.length*4) {
        objMarker = {
                enabled: true,
                symbol: chart.options.symbols[iMarker - 1 - chart.options.symbols.length*3],
                radius: iRadius,
                lineColor: strColor,
                lineWidth: 2,
                fillColor: 'black',
            };
    } else if (iMarker > chart.options.symbols.length*4 && iMarker <= chart.options.symbols.length*5) {
        objMarker = {
                enabled: true,
                symbol: chart.options.symbols[iMarker - 1 - chart.options.symbols.length*4],
                radius: iRadius,
                lineColor: strColor,
                lineWidth: 2,
                fillColor: 'white',
            };
    }
	
	return objMarker;
}

function SetData(objSerie) {

    var datStart = Zeitraum_Start.getTime();
    var datEnd = Zeitraum_Ende.getTime();
    var serie = objSerie;
    var attrIDX;

    if (objSerie.options.name === 'MinMax') {
        return;
    }

    // get main DP
    if (objSerie.options.id.toString().substr(0, 1) === 'C') {
        attrIDX = objSerie.options.id.toString().split('_')[1];
    } else {
        attrIDX = objSerie.options.id.toString();
    }

    var found = false;

    var attr = DP_attribute.findIndex(obj=>obj.id === attrIDX);
    if (attr != -1) {

        if (DP_attribute[attr].comp != 'C0') {

            if (objSerie.options.id.toString().substr(0, 1) === 'C' && DP_attribute[attr].visible === 2) {
                // main is visible and all will be loaded together
                return;
            }

            // correct start for comparisation data, do read only once
            datStart += getComparisionBackDay(DP_attribute[attr].comp);

            if (objSerie.options.id.toString().substr(0, 1) === 'C') {
                datEnd += getComparisionBackDay(DP_attribute[attr].comp);
            }
        }

        // check buffer timestamps and decide to read additional data
        if ((DP_attribute[attr].buffer_data) && (DP_attribute[attr].buffer_data.buffer_start) && (DP_attribute[attr].buffer_data.buffer_end)) {
            if ((DP_attribute[attr].buffer_data.buffer_start <= datStart) && (DP_attribute[attr].buffer_data.buffer_end >= datEnd)) {
                // all data already in the buffer
                found = true;
                SetSerienData(attr, objSerie);
            } else if (DP_attribute[attr].buffer_data.buffer_start > datStart && DP_attribute[attr].buffer_data.buffer_start <= datEnd && DP_attribute[attr].buffer_data.buffer_end >= datEnd && DP_attribute[attr].buffer_data.values.length > 0) {
                // append to begin
                datEnd = DP_attribute[attr].buffer_data.buffer_start;
            } else if (DP_attribute[attr].buffer_data.buffer_start <= datStart && DP_attribute[attr].buffer_data.buffer_end >= datStart && DP_attribute[attr].buffer_data.buffer_end < datEnd && DP_attribute[attr].buffer_data.values.length > 0) {
                // append to end by refresh button
                datStart = DP_attribute[attr].buffer_data.buffer_end;
            }
        }
        // missing data found ?
        if (!found) {
            getDataH2(objSerie, DP_attribute[attr].id, attr, datStart, datEnd);
        }
    }
}

// save received data 
function BufferSerienData(id, data) {

    if (!id) {
        console.log('ID missing');
        return;
    }

    // find queue entry
    var q_i = DP_Queue.findIndex(obj=>obj[0] === id);

    var attrIDX = DP_Queue[q_i][3];

    if (data.values.length > 0) {

        // correct values to round -3
        for (var i = 0; i < data.values.length; i++) {
            data.values[i] = Math.round(data.values[i] * 1000) / 1000;
        }

        if (DP_attribute[attrIDX].buffer_data.buffer_start >= DP_Queue[q_i][4] && DP_attribute[attrIDX].buffer_data.buffer_start === DP_Queue[q_i][5] && DP_attribute[attrIDX].buffer_data.values.length > 0) {

            DP_attribute[attrIDX].buffer_data.buffer_start = DP_Queue[q_i][4];
            DP_attribute[attrIDX].buffer_data.timestamps = data.timestamps.concat(DP_attribute[attrIDX].buffer_data.timestamps);
            DP_attribute[attrIDX].buffer_data.values = data.values.concat(DP_attribute[attrIDX].buffer_data.values);

        } else if (DP_attribute[attrIDX].buffer_data.buffer_end <= DP_Queue[q_i][5] && DP_attribute[attrIDX].buffer_data.buffer_end === DP_Queue[q_i][4] && DP_attribute[attrIDX].buffer_data.values.length > 0) {

            DP_attribute[attrIDX].buffer_data.buffer_end = DP_Queue[q_i][5];
            DP_attribute[attrIDX].buffer_data.timestamps = DP_attribute[attrIDX].buffer_data.timestamps.concat(data.timestamps);
            DP_attribute[attrIDX].buffer_data.values = DP_attribute[attrIDX].buffer_data.values.concat(data.values);
        } else {
            DP_attribute[attrIDX].buffer_data.buffer_start = DP_Queue[q_i][4];
            DP_attribute[attrIDX].buffer_data.buffer_end = DP_Queue[q_i][5];
            DP_attribute[attrIDX].buffer_data.timestamps = data.timestamps;
            DP_attribute[attrIDX].buffer_data.values = data.values;
        }

        // update counter
        if (DP_Navigator < 3) {
        	document.getElementById("count_val").innerHTML = (Number(document.getElementById("count_val").innerHTML) + data.values.length).toString();
        } else {
        	document.getElementById("count_val").innerHTML = "";
        }
    }

    // get which serie has to be updated
    var serie = DP_Queue[q_i][2];

    // queue clear for this one
    DP_Queue.splice(q_i, 1);
    loadingInfo();

    SetSerienData(attrIDX, serie);
}

function SetSerienData(p_attr, serieObj) {

    var aggrType = parseInt(DP_attribute[p_attr].aggr.substr(1, 2));
    var compType = DP_attribute[p_attr].comp;

    var datStart = Zeitraum_Start.getTime();
    var datEnd = Zeitraum_Ende.getTime();

    var id = DP_attribute[p_attr].id;

    var arr = [];
    var backSec = 0;

    // Min/Max not needed         
    if (serieObj.options.name === 'MinMax') {
        return;
    }

    if (serieObj.options.id.toString().substr(0, 1) === 'C') {

        // Set backtime        
        backSec = getComparisionBackDay(compType);

        datStart += backSec;
        datEnd += backSec;

        var attr2 = DP_attribute.findIndex(obj=>obj.id === serieObj.options.id.toString());
        if (attr2) {
            aggrType = parseInt(DP_attribute[attr2].aggr.substr(1, 2));
        }
    }

    // Define BufferLink
    var buffer = DP_attribute[p_attr].buffer_data;

    // collect all timesstamps and Values
    if (aggrType === 0) {

        // get start and end position over binary search
        var arrStart = sortedIndex(buffer.timestamps, datStart);
        var arrEnd = sortedIndex(buffer.timestamps, datEnd);

        if (arrStart>0 && datStart != buffer.timestamps[arrStart]) {
            arr.push([datStart - backSec, (buffer.values[arrStart-1] * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
        }
        for (var i = arrStart; i <= arrEnd; i++) {
            arr.push([buffer.timestamps[i] - backSec, (buffer.values[i] * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
        }
//        if (buffer.timestamps.length>0 && datEnd != buffer.timestamps[arrEnd]) {
//            arr.push([datEnd - backSec, (buffer.values[arrEnd] * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
//        }
        
        // no aggregation but rounded to min, better for mouse over sync to other lines
    } else if (aggrType === 6) {

        // get start and end position over binary search
        var arrStart = sortedIndex(buffer.timestamps, datStart);
        var arrEnd = sortedIndex(buffer.timestamps, datEnd);
        for (var i = arrStart; i <= arrEnd; i++) {

            var timestamprounded = Math.round((buffer.timestamps[i] - backSec) / 60000) * 60000;
            arr.push([timestamprounded, (buffer.values[i] * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);

        }
    // Delta +/-
    } else if (aggrType === 2) {

        // get start and end position if buffer over binary search
        var arrStart = sortedIndex(buffer.timestamps, datStart);
        var arrEnd = sortedIndex(buffer.timestamps, datEnd);

        // only if values found
        if (arrStart < buffer.timestamps.length) {
            var last_value = buffer.values[arrStart];
            var last_time = buffer.timestamps[arrStart];

            for (var i = arrStart + 1; i <= arrEnd; i++) {

                if (buffer.timestamps[i] >= datStart && buffer.timestamps[i] <= datEnd) {
                    // fill missing times with delta 0 every 10 min.
                    if ((buffer.timestamps[i] - last_time) > 600000) {
                        last_time = Math.round((last_time + 600000) / 60000) * 60000;
                        for (var t = last_time; t < buffer.timestamps[i]; t = t + 600000) {
                            arr.push([t, 0]);
                        }
                    }

                    arr.push([buffer.timestamps[i] - backSec, ((buffer.values[i] - last_value) * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);

                    last_value = buffer.values[i];
                    last_time = buffer.timestamps[i];
                }
            }
        }

    // Delta +
    } else if (aggrType === 7) {

        // get start and end position if buffer over binary search
        var arrStart = sortedIndex(buffer.timestamps, datStart);
        var arrEnd = sortedIndex(buffer.timestamps, datEnd);

        // only if values found
        if (arrStart < buffer.timestamps.length) {
            var last_value = buffer.values[arrStart];
            var last_time = buffer.timestamps[arrStart];

            for (var i = arrStart + 1; i <= arrEnd; i++) {

                if (buffer.timestamps[i] >= datStart && buffer.timestamps[i] <= datEnd) {
                    // fill missing times with delta 0 every 10 min.
                    if ((buffer.timestamps[i] - last_time) > 600000) {
                        last_time = Math.round((last_time + 600000) / 60000) * 60000;
                        for (var t = last_time; t < buffer.timestamps[i]; t = t + 600000) {
                            arr.push([t, 0]);
                        }
                    }

                    // only + values, - are ignored
                    var delta_val = ((buffer.values[i] - last_value) * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset;
                    if (delta_val >= 0) {
                        arr.push([buffer.timestamps[i] - backSec, delta_val ]);
                    }

                    last_value = buffer.values[i];
                    last_time = buffer.timestamps[i];
                }
            }
        }

    // collect all timesstamps and Values for TIME_ON Aggregation
    } else if (aggrType === 5) {

        // get start and end position if buffer over binary search
        var arrStart = sortedIndex(buffer.timestamps, datStart);
        var arrEnd = sortedIndex(buffer.timestamps, datEnd);

        // only if values found
        if (arrStart < buffer.timestamps.length) {

            var last_value = (buffer.values[arrStart] > 0) ? 1 : 0;
            var last_time = buffer.timestamps[arrStart];

            for (var i = arrStart + 1; i <= arrEnd; i++) {
                if (last_value > 0 && buffer.values[i] === 0) {

                    last_value = buffer.timestamps[i] - last_time;
                    // fill every minute with 1 as run time
                    if (last_value > 60000) {
                        for (var t = last_time; t < buffer.timestamps[i] - 60000; t = t + 60000) {
                            arr.push([t - backSec, (1 * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
                            last_value -= 60000;
                        }
                    }
                    if (last_value > 0) {
                        arr.push([t - backSec, (Math.round(last_value / 60) / 1000 * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
                    }
                    last_value = 0;
                    last_time = buffer.timestamps[i];

                } else if (last_value === 0 && buffer.values[i] > 0) {

                    last_value = buffer.timestamps[i] - last_time;
                    // fill every minute with 1 as run time
                    if (last_value > 60000) {
                        for (var t = last_time; t < buffer.timestamps[i] - 60000; t = t + 60000) {
                            arr.push([t - backSec, 0]);
                            last_value -= 60000;
                        }
                    }

                    last_value = 1;
                    last_time = buffer.timestamps[i];
                }
            }
            // fill also last minutes if still on
            if (last_value > 0) {
                last_value = datEnd - last_time;
                if (last_value > 60000) {
                    for (var t = last_time; t < arrEnd - 60000; t = t + 60000) {
                        arr.push([t - backSec, (1 * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
                        last_value -= 60000;
                    }
                }
                if (last_value > 0) {
                    arr.push([t - backSec, (Math.round(last_value / 60) / 1000 * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
                }
            }
        }

    } else {

        // get start and end position over binary search
        var arrStart = sortedIndex(buffer.timestamps, datStart);
        var arrEnd = sortedIndex(buffer.timestamps, datEnd);

        var last_value = buffer.values[arrStart];
        var last_time = buffer.timestamps[arrStart];

        for (var i = arrStart; i <= arrEnd; i++) {

            // fill long empty periods with last_value, that aggregation works
            if ((buffer.timestamps[i] - last_time) > 600000) {
                last_time = Math.round((last_time + 600000) / 60000) * 60000;
                for (var t = last_time; t < buffer.timestamps[i] - 300000; t = t + 600000) {
                    arr.push([t - backSec, last_value]);
                }
            }

            last_value = (buffer.values[i] * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset;
            last_time = buffer.timestamps[i];

            arr.push([buffer.timestamps[i] - backSec, last_value]);
        }
    }

    if (arr.length > 0) {

        // Here Data are ready to set for Serie
        serieObj.setData(arr, true, false, false);

        // prepare and show min/max series
        if (aggrType === 3) {
            AddAggregationMinMax(serieObj);
        }

        // update colors on txt
        loadNewAxisInfo();
        // Update Aggregation Text
        showAggrText();
    }

    // read data for comp series
    if (DP_attribute[p_attr].comp != 'C0' && (serieObj.options.id.toString().substr(0, 1) != 'C')) {

        var sobj = chart.get(DP_attribute[p_attr].comp + '_' + DP_attribute[p_attr].id);
        var attrC = DP_attribute.findIndex(obj=>obj.id === DP_attribute[p_attr].comp + '_' + DP_attribute[p_attr].id);
        if (sobj && attrC != -1 && DP_attribute[attrC].comp === 'C0' && DP_attribute[attrC].visible === 2) {
            SetSerienData(p_attr, sobj);
        }
    }
}

// Find next timestamp in array by binary search
function sortedIndex(array, value) {
    var low = 0, high = array.length-1, mid;
    if (array[low] >= value)
        return 0;
    if (array[high] <= value)
        return high;
    while (low < high) {
        mid = Math.floor((low + high) / 2);
        if (array[mid] < value)
            low = mid + 1;
        else
            high = mid;
    }
    if (low > array.length-1) {
        low = array.length-1
    }
    return low;
}

/**
*  read timeSerien data for H2 database
*/
function getDataH2(p_series, p_attrID, p_attr, datStart, datEnd) {
    var text;

    // Refresh for Min/Max Aggregation done directly
    if (p_series.options.name === 'MinMax')
        return;

    var key = p_attrID + '_' + Date.now();
    var p_id = p_series.options.id.toString();

    // refresh for comparisation done over real ID
    if (p_series.options.id.toString().substr(0, 1) === 'C') {
        p_id = p_id.split('_')[1];
    }

    // save request to queue
    DP_Queue.push([key, p_attrID, p_series, p_attr, datStart, datEnd]);

    // display loading info
    setTimeout(loadingInfo, 500);

    // get serien data from H2 database
       var url = 'http://' + H2_server + ':' + H2_port;
       url += '/query/jsonrpc.gy';
       url += (DP_ApiKey=="")?"":"?"+DP_ApiKey;

       var postData = {id: key,
                       method: 'getTimeSeriesRaw',
                       params: [p_id, datStart, datEnd ]};

       postData = JSON.stringify(postData);

	    $.ajax({
	        url: url,
	        dataType: "json",
	        contentType: "application/json",
	        type: "post",
            data: postData,
	        cache: false,
	        async: true,
	        error: function(xhr, status, error) {
	            console.log('AXAJ-error:');
	            console.log(xhr);
	            console.log(status);
	            console.log(error);
	        },
	        success: function(result) {
               if (!result.result) {
                   console.log(result);
               } else if (result.result) {
                   BufferSerienData(result.id, result.result);
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

    if (DP_Navigator < 3) {
    	document.getElementById("count_val").innerHTML = "0";
        document.getElementById('count_text').innerHTML = ChhLanguage.default.historian.labelValues;
    } else {
      	document.getElementById("count_val").innerHTML = "";
        document.getElementById('count_text').innerHTML = "";
    }

    // display loading info
    setTimeout(loadingInfo, 500);
   
       var url = 'http://' + H2_server + ':' + H2_port;
       url += '/query/jsonrpc.gy';
       url += (DP_ApiKey=="")?"":"?"+DP_ApiKey;

       var postData = {id: 'DP',
                       method: 'getDataPoint',
                       params: []};

       postData = JSON.stringify(postData);

	    $.ajax({
	        url: url,
	        dataType: "json",
	        contentType: "application/json",
	        type: "post",
            data: postData,
	        cache: false,
	        async: true,
	        error: function(xhr, status, error) {
	            console.log('AXAJ-error:');
	            console.log(xhr);
	            console.log(status);
	            console.log(error);
	        },
	        success: function(result) {
               requestData2(result);
            }
	    });
    
}

/**
* Request data from the server, add it to the graph and set a timeout 
* to request again
*/
function requestSettings() {



    var url = 'http://' + H2_server + ':' + H2_port;
    url += '/query/jsonrpc.gy';
    url += (DP_ApiKey=="")?"":"?"+DP_ApiKey;

    var postData = {id: 'Setup',
                    method: 'getConfig',
                    params: ['HighChart']};

    postData = JSON.stringify(postData);

    $.ajax({
	        url: url,
	        dataType: "json",
	        contentType: "application/json",
	        type: "post",
            data: postData,
	        cache: false,
	        async: true,
	        error: function(xhr, status, error) {
	            console.log('AXAJ-error:');
	            console.log(xhr);
	            console.log(status);
	            console.log(error);
	        },
	        success: function(result) {
        	// Get Settings from H2 database as String
        	if (result.result) {
        	    try {
        	    	var strSetNew = result.result.replace(new RegExp("'", 'g'),'"' );

	        	    DP_settings = JSON.parse(strSetNew);
	        	    DP_settings_old = JSON.parse(strSetNew);
	             // console.log(DP_settings);
        	    }
        	    catch (e) {
        	        console.log(e);
        	    }
            }           
            
            // read default from YAXIS
            // take default values from database
            if (DP_settings) {
                for (var x = 0; x < DP_yAxis.length; x++) {
                	if (DP_settings['YAXIS'+x]) {
                        var text2 = DP_settings['YAXIS'+x].split('|');
                        var axis_id = x;
                        for (var k = 0; k < text2.length; k++) {
                            if (text2[k].substr(0, 1) === 'P') {
                                if (text2[k].substr(1, 1) === '0')
                                    DP_yAxis[axis_id].position = false;
                                if (text2[k].substr(1, 1) === '1')
                                    DP_yAxis[axis_id].position = true;
                            } else if (text2[k].substr(0, 1) === 'C') {
                                if (text2[k].substr(1, 1) === '0')
                                    DP_yAxis[axis_id].type = 0;
                                if (text2[k].substr(1, 1) === '1')
                                    DP_yAxis[axis_id].type = 1;
                            } else if (text2[k].substr(0, 1) === 'A') {
                                if (text2[k].substr(1, 1) === '0')
                                    DP_yAxis[axis_id].limit = 0;
                                if (text2[k].substr(1, 1) === '1')
                                    DP_yAxis[axis_id].limit = 1;
                                if (text2[k].substr(1, 1) === '2')
                                    DP_yAxis[axis_id].limit = 2;
                            } else if (text2[k].substr(0, 1) === 'L') {
                                DP_yAxis[axis_id].min = parseFloat(text2[k].substr(1, 15));
                            } else if (text2[k].substr(0, 1) === 'H') {
                                DP_yAxis[axis_id].max = parseFloat(text2[k].substr(1, 15));
                            } else if (text2[k].substr(0, 1) === 'G') {
                                DP_yAxis[axis_id].tick = parseInt(text2[k].substr(1, 15));
                            } else if (text2[k].substr(0, 1) === 'F') {
                                DP_yAxis[axis_id].color = parseInt(text2[k].substr(1, 2));
                            } else if (text2[k].substr(0, 1) === 'T') {
                                DP_yAxis[axis_id].text = text2[k].substr(1, 50);
                            }
                        }
                        
                	}
                }
                // Read default Settings
            	if (DP_settings['Setting']) {
                    var text2 = DP_settings['Setting'].split('|');
                    for (var k = 0; k < text2.length; k++) {
                        if (text2[k].substr(0, 1) === 'L') {
                        	DP_Legend = parseInt(text2[k].substr(1, 2));
                        } else if (text2[k].substr(0, 1) === 'N') {
                        	DP_Navigator = parseInt(text2[k].substr(1, 2));
                        } else if (text2[k].substr(0, 1) === 'P') {
                        	DP_Labels = parseInt(text2[k].substr(1, 2));
                        } else if (text2[k].substr(0, 1) === 'D') {
                        	DP_DayLight = parseInt(text2[k].substr(1, 2));
                        } else if (text2[k].substr(0, 1) === 'G') {
                        	DP_Grid = parseInt(text2[k].substr(1, 2));
                        } else if (text2[k].substr(0, 1) === 'F') {
                        	DP_ShowFilter = parseInt(text2[k].substr(1, 2));
                        } else if (text2[k].substr(0, 1) === 'I') {
                        	DP_DataPointFilter = parseInt(text2[k].substr(1, 2));
                        } else if (text2[k].substr(0, 1) === 'B') {
                        	DP_Theme = text2[k].substr(1,30);
                        } else if (text2[k].substr(0, 1) === 'R') {
                        	H2_refreshSec = parseInt(text2[k].substr(1, 2));
                        } else if (text2[k].substr(0, 1) === 'T') {
                        	DP_Title = text2[k].substr(1,50);
                        } else if (text2[k].substr(0, 1) === 'S') {
                        	DP_Subtitle = text2[k].substr(1,60);
                        }
                    }
            	}
            } else {
            	DP_settings = {};
            }
            readLinkData();

        },
    });
}


function readLinkData() {
    // check parameter from get-link
    if (location.search) {
        var parts = decodeURIComponent(location.search.substring(1)).split('&');
        for (var i = 0; i < parts.length; i++) {
            var nv = parts[i].split('=');
            if (!nv[0])
                continue;

            if (nv[0].toLowerCase() === 'dp') {
                DP_Limit = true;
                // parameter Periode (Stunden)
            } else if ((nv[0].toLowerCase() === 'periode') || (nv[0].toLowerCase() === 'period')) {
                Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(3600 * 1000 * parseInt(nv[1]))));
                // parameter Data Point
            } else if (nv[0].toLowerCase() === 'filterkey') {
                filter_feld = decodeURIComponent(nv[1].toLowerCase());
            } else if (nv[0].toLowerCase() === 'title') {
                DP_Title = decodeURIComponent(nv[1]);
                DP_Title = DP_Title.replace('§','%');
            } else if (nv[0].toLowerCase() === 'subtitle') {
                DP_Subtitle = decodeURIComponent(nv[1]);
                DP_Subtitle = DP_Subtitle.replace('§','%');
            } else if (nv[0].toLowerCase() === 'legend') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') {
                    DP_Legend = 0;
                }
                if (parseInt(decodeURIComponent(nv[1])) >= 0 && parseInt(decodeURIComponent(nv[1])) < 7) {
                    DP_Legend = parseInt(decodeURIComponent(nv[1]));
                }
            } else if (nv[0].toLowerCase() === 'navigator') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') {
                    DP_Navigator = 3;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '0') {
                    DP_Navigator = 0;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '1') {
                    DP_Navigator = 1;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '2') {
                    DP_Navigator = 2;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '3') {
                    DP_Navigator = 3;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '4') {
                    DP_Navigator = 4;
                }
            } else if (nv[0].toLowerCase() === 'theme') {
                DP_Theme = decodeURIComponent(nv[1].toLowerCase());
            } else if (nv[0].toLowerCase() === 'dpfilter') {
            	DP_DataPointFilter = parseInt(decodeURIComponent(nv[1]));
            } else if (nv[0].toLowerCase() === 'labels') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'true') {
                    DP_Labels = 1;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '0') {
                    DP_Labels = 0;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '1') {
                    DP_Labels = 1;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '2') {
                    DP_Labels = 2;
                }
            } else if (nv[0].toLowerCase() === 'daylight') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') {
                    DP_DayLight = 0;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '0') {
                    DP_DayLight = 0;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '1') {
                    DP_DayLight = 1;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '2') {
                    DP_DayLight = 2;
                }
                if (decodeURIComponent(nv[1].toLowerCase()) === '3') {
                    DP_DayLight = 3;
                }
            } else if (nv[0].toLowerCase() === 'grid') {
            	DP_Grid = parseInt(decodeURIComponent(nv[1]));
            } else if (nv[0].toLowerCase() === 'refresh') {
                if (parseInt(decodeURIComponent(nv[1])) > 0) {
                    H2_refreshSec = parseInt(decodeURIComponent(nv[1]));
                } else if (decodeURIComponent(nv[1].toLowerCase()) === 'true') {
                	H2_refreshSec = 60;
                }
            }
        }
    }
    if (H2_refreshSec > 0) {
        DP_AutoRefresh = H2_refreshSec;
        AutoRefreshCount = DP_AutoRefresh;
        setTimeout(AutoRefresh, 1000);
    } 
    
    createChart();
	
}


/**
* Request data from the server, add it to the graph and set a timeout 
* to request again
*/
function requestData2(TXT_JSON) {

    var DP_rooms = [];
    var DP_gewerk = [];

    if (!TXT_JSON.result)
        return;

    // in result are all datapoint, let's check which are not hidden and active

    // DP_point = TXT_JSON.result;
    DP_point = [];
    for (i = 0; i < TXT_JSON.result.length; i++) {
        DP_point.push(TXT_JSON.result[i]);
    }

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

    // Alle Serien aufbauen und Räume & Gewerke sammeln nur für anzeigbare
    for (i = 0; i < DP_point.length; i++) {

        // Räme sammeln
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
        
        
        // take default values from database
        if (DP_point[i].attributes.custom && DP_point[i].attributes.custom.HighChart) {
            var text2 = DP_point[i].attributes.custom.HighChart.split('|');
            if (text2.length > 0) {
               var attr = defaultAttrib(DP_point[i], i, DP_point[i].idx.toString());
               DP_attribute.push(attr);
            }
        }
    }
    
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
        if (ChhLanguage.default.historian[text]) {
            text = ChhLanguage.default.historian[text];
        }
        select.options[select.options.length] = new Option(text,DP_rooms[i]);
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
        if (ChhLanguage.default.historian[text]) {
            text = ChhLanguage.default.historian[text];
        }
        select.options[select.options.length] = new Option(text,DP_gewerk[i]);
    }

    // Set start parameter 
    document.getElementById("filterFeld").value = filter_feld;

    // check parameter from get-link
    if (location.search) {
        var parts = decodeURIComponent(location.search.substring(1)).split('&');
        for (var i = 0; i < parts.length; i++) {
            var nv = parts[i].split('=');
            if (!nv[0])
                continue;
            // nur noch DP Werte
            if (nv[0].toLowerCase() === 'dp') {
                var text = decodeURIComponent(nv[1]).toLowerCase().split(',');
                for (var j = 0; j < text.length; j++) {
                    var text2 = text[j].toUpperCase().split('|');
                    var dp_id = text2[0];

                    if (text2.length > 0) {

                        var DP_pos = DP_point.findIndex(obj=>obj.idx.toString().toUpperCase() === dp_id.toString().toUpperCase() || ((obj.attributes.displayName) && obj.attributes.displayName.toUpperCase() === dp_id.toUpperCase()) || (obj.id.address + '.' + obj.id.identifier).toUpperCase() === dp_id.toUpperCase());
                        if (DP_pos != -1) {
                            dp_id = DP_point[DP_pos].idx.toString();
                        }

                        var attrpos = DP_attribute.findIndex(obj=>obj.id === dp_id);
                        if (attrpos === -1) {
                            var attr = {};
                            if (DP_pos != -1) {
                               attr = defaultAttrib(DP_point[DP_pos], j, dp_id);
                            } else {
                               attr = defaultAttrib(-1, j, dp_id);
                            }

                            DP_attribute.push(attr);
                            attrpos = DP_attribute.findIndex(obj=>obj.id === dp_id);
                        }
                        DP_attribute[attrpos].visible = 2;
                        for (var k = 1; k < text2.length; k++) {
                            if (text2[k].substr(0, 1) === 'A') {
                                DP_attribute[attrpos].aggr = text2[k];
                            } else if (text2[k].substr(0, 1) === 'Y') {
                                DP_attribute[attrpos].yaxis = text2[k];
                            } else if (text2[k].substr(0, 1) === 'T') {
                                DP_attribute[attrpos].atime = text2[k];
                            } else if (text2[k].substr(0, 1) === 'F') {
                                DP_attribute[attrpos].color = text2[k];
                            } else if (text2[k].substr(0, 1) === 'C') {
                                DP_attribute[attrpos].comp = text2[k];
                            } else if (text2[k].substr(0, 1) === 'L') {
                                DP_attribute[attrpos].line = text2[k];
                            } else if (text2[k].substr(0, 1) === 'M') {
                                DP_attribute[attrpos].mark = text2[k];
                            } else if (text2[k].substr(0, 1) === 'D') {
                                DP_attribute[attrpos].dash = text2[k];
                            } else if (text2[k].substr(0, 1) === 'W') {
                                DP_attribute[attrpos].width = text2[k];
                            } else if (text2[k].substr(0, 1) === 'S') {
                            	DP_attribute[attrpos].stack = parseInt(text2[k].substr(1, 2));
                            } else if (text2[k].substr(0, 1) === 'V') {
                                DP_attribute[attrpos].visible = parseInt(text2[k].substr(1, 1));
                            } else if (text2[k].substr(0, 1) === 'U') {
                                DP_attribute[attrpos].unit = decodeURIComponent(nv[1]).split(',')[j].split('|')[k].substr(1, 20).replace("§","%");
                            } else if (text2[k].substr(0, 1) === 'X') {
                                DP_attribute[attrpos].factor = parseFloat(text2[k].substr(1, 10));
                            } else if (text2[k].substr(0, 1) === 'O') {
                                DP_attribute[attrpos].offset = parseFloat(text2[k].substr(1, 10));
                            }
                        }
                    }
                }

            // parameter YAXIS
            } else if (nv[0].toLowerCase() === 'yaxis') {
                var text = decodeURIComponent(nv[1]).toLowerCase().split(',');
                for (var j = 0; j < text.length; j++) {
                    var text2 = text[j].toUpperCase().split('|');
                    var axis_id = parseInt(text2[0].substr(1, 2));
                    if (axis_id >= 0 && axis_id < DP_yAxis.length) {
                        if (text2.length > 0) {
                            for (var k = 1; k < text2.length; k++) {
                                if (text2[k].substr(0, 1) === 'P') {
                                    if (text2[k].substr(1, 1) === '0')
                                        DP_yAxis[axis_id].position = false;
                                    if (text2[k].substr(1, 1) === '1')
                                        DP_yAxis[axis_id].position = true;
                                } else if (text2[k].substr(0, 1) === 'C') {
                                    if (text2[k].substr(1, 1) === '0')
                                        DP_yAxis[axis_id].type = 0;
                                    if (text2[k].substr(1, 1) === '1')
                                        DP_yAxis[axis_id].type = 1;
                                } else if (text2[k].substr(0, 1) === 'A') {
                                    if (text2[k].substr(1, 1) === '0')
                                        DP_yAxis[axis_id].limit = 0;
                                    if (text2[k].substr(1, 1) === '1')
                                        DP_yAxis[axis_id].limit = 1;
                                    if (text2[k].substr(1, 1) === '2')
                                        DP_yAxis[axis_id].limit = 2;
                                } else if (text2[k].substr(0, 1) === 'L') {
                                    DP_yAxis[axis_id].min = parseFloat(text2[k].substr(1, 15));
                                } else if (text2[k].substr(0, 1) === 'H') {
                                    DP_yAxis[axis_id].max = parseFloat(text2[k].substr(1, 15));
                                } else if (text2[k].substr(0, 1) === 'G') {
                                    DP_yAxis[axis_id].tick = parseInt(text2[k].substr(1, 15));
                                } else if (text2[k].substr(0, 1) === 'F') {
                                    DP_yAxis[axis_id].color = parseInt(text2[k].substr(1, 2));
                                } else if (text2[k].substr(0, 1) === 'T') {
                                    DP_yAxis[axis_id].text = decodeURIComponent(nv[1]).split(',')[j].split('|')[k].substr(1, 50).replace("§","%");
                                }
                            }
                        }
                    }
                }
            // parameter Raum
            } else if (nv[0].toLowerCase() === 'room') {
                var DP_start_room = decodeURIComponent(nv[1].toLowerCase());
                var select = document.getElementById("Select-Raum");
                for (ir = 0; ir < select.options.length; ir++) {
                    if (select.options[ir].label.toLowerCase() === DP_start_room.toLowerCase() || select.options[ir].value.toLowerCase() === DP_start_room.toLowerCase()) {
                        select.value = select.options[ir].value;
                        break;
                    }
                }
            // parameter Gewerk
            } else if (nv[0].toLowerCase() === 'function') {
                var DP_start_func = decodeURIComponent(nv[1].toLowerCase());
                var select = document.getElementById("Select-Gewerk");
                for (ir = 0; ir < select.options.length; ir++) {
                    if (select.options[ir].label.toLowerCase() === DP_start_func.toLowerCase()) {
                        select.value = select.options[ir].value;
                        break;
                    }
                }

            // FilterLine
            } else if (nv[0].toLowerCase() === 'filterline') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false' || decodeURIComponent(nv[1].toLowerCase()) === '0') {
                    DP_ShowFilter = 0;
                }
                // only filterline without menue
                if (decodeURIComponent(nv[1].toLowerCase()) === '2') {
                    DP_ShowFilter = 2;
                }
                // only menue without filterline
                if (decodeURIComponent(nv[1].toLowerCase()) === '3') {
                    DP_ShowFilter = 3;
                }
            }
        }
    }
    
    // Update Chart
    chart.update({ yAxis: defineYAxis() },false,false);

    // Yaxis options
    $("#Select-Yaxis").empty();
    var select = document.getElementById("Select-Yaxis");
    for (var i = 0; i < DP_yAxis.length; i++) {
        var option = document.createElement("option");
        if (DP_yAxis[i].text != "" && DP_yAxis[i].text != null) {
            option.text = DP_yAxis[i].text;
        } else {
            option.text = ChhLanguage.default.historian['yaxis' + i];
        }
        option.value = 'Y' + i;
        select.add(option);
    }
    
    // show menü & filter if wanted
    showFilterLine();

    // Display data
    ChangeEventRaumFilter();

    // check parameter Zoom from get-link
    if (location.search) {
        var parts = decodeURIComponent(location.search.substring(1)).split('&');
        for (var i = 0; i < parts.length; i++) {
            var nv = parts[i].split('=');
            if (!nv[0])
                continue;
            // parameter Zoom found
            if (nv[0].toLowerCase() === 'zoom') {
                var newStart = new Date(Zeitraum_Ende - (new Date(3600 * 1000 * parseFloat(nv[1]))));
                chart.xAxis[0].setExtremes(newStart.getTime(), Zeitraum_Ende.getTime(), true);
            }
        }
    }

}

/**
* Create HighChart Object on loading
*/
$(document).ready(function() {

	DP_ApiKey = "";
	if (apiKey != "") {
		DP_ApiKey = apiKey.substring(1,apiKey.length);	
	}

	requestSettings();

    document.getElementById("container").setAttribute("style", "height:" + ($(document).height() - 160) + "px");

    // Translate to Language Set
    document.getElementById('button1').innerHTML = ChhLanguage.default.historian.buttonDay;
    document.getElementById('button2').innerHTML = ChhLanguage.default.historian.buttonWeek;
    document.getElementById('button3').innerHTML = ChhLanguage.default.historian.buttonMonth;
    document.getElementById('button4').innerHTML = ChhLanguage.default.historian.buttonYear;
    document.getElementById('button5').innerHTML = ChhLanguage.default.historian.buttonALL;
    document.getElementById('refresh').innerHTML = ChhLanguage.default.historian.buttonRefresh;
    document.getElementById('createLink').innerHTML = ChhLanguage.default.historian.buttonLink;
    document.getElementById('filterFeld').placeholder = ChhLanguage.default.historian.filterPlaceHolder;
    document.title = ChhLanguage.default.interface.pageTitle;
  
    // Define a custom symbol PLUS
    Highcharts.SVGRenderer.prototype.symbols.plus = function (x, y, w, h) {
        return [ 'M', x + w*0.3, y,
                 'L', x + w*0.7, y,
                 'L', x + w*0.7, y + h*0.3,
                 'L', x + w    , y + h*0.3,
                 'L', x + w    , y + h*0.7,
                 'L', x + w*0.7, y + h*0.7,
                 'L', x + w*0.7, y + h,
                 'L', x + w*0.3, y + h,
                 'L', x + w*0.3, y + h*0.7,
                 'L', x        , y + h*0.7,
                 'L', x        , y + h*0.3,
                 'L', x + w*0.3, y + h*0.3,
                 'L', x + w*0.3, y,
                 'Z' ];
    };

    if (Highcharts.VMLRenderer) {
        Highcharts.VMLRenderer.prototype.symbols.plus = Highcharts.SVGRenderer.prototype.symbols.plus;
    }
    Highcharts.defaultOptions.symbols.push('plus');
    
    // Define a custom symbol CROSS
    Highcharts.SVGRenderer.prototype.symbols.cross = function (x, y, w, h) {
        return [ 'M', x        , y + h*0.2,
                 'L', x + w*0.3, y + h*0.5,
                 'L', x        , y + h*0.8,
                 'L', x + w*0.2, y + h,
                 'L', x + w*0.5, y + h*0.7,
                 'L', x + w*0.8, y + h,
                 'L', x + w    , y + h*0.8,
                 'L', x + w*0.7, y + h*0.5,
                 'L', x + w    , y + h*0.2,
                 'L', x + w*0.8, y,
                 'L', x + w*0.5, y + h*0.3,
                 'L', x + w*0.2, y,
                 'L', x        , y + h*0.2,
                 'Z' ];
  	
    	
//    	return ['M', x, y, 'L', x + w, y + h, 'M', x + w, y, 'L', x, y + h, 'z'];
    };
    if (Highcharts.VMLRenderer) {
        Highcharts.VMLRenderer.prototype.symbols.cross = Highcharts.SVGRenderer.prototype.symbols.cross;
    }
    Highcharts.defaultOptions.symbols.push('cross');

    // Define a custom symbol STAR
    Highcharts.SVGRenderer.prototype.symbols.star = function(x, y, w, h) {
        return [
          'M', x, y + 0.4 * h,
          'L', x + 0.35 * w, y + 0.35 * h,
          'L', x + 0.5 * w, y,
          'L', x + 0.65 * w, y + 0.35 * h,
          'L', x + w, y + 0.4 * h,
          'L', x + 0.75 * w, y + 0.65 * h,
          'L', x + 0.85 * w, y + h,
          'L', x + 0.5 * w, y + 0.8 * h,
          'L', x + w * 0.15, y + h,
          'L', x + 0.25 * w, y + 0.65 * h,
          'Z'
        ];
      };
      if (Highcharts.VMLRenderer) {
        Highcharts.VMLRenderer.prototype.symbols.star = Highcharts.SVGRenderer.prototype.symbols.star;
      }   
      Highcharts.defaultOptions.symbols.push('star');
    
    
    
    
    // aggregation options
    var select = document.getElementById("Select-Aggregation");
    for (var i = 0; i < 8; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['aggrtxt' + i];
        option.value = 'A' + i;
        select.add(option);
    }

    // aggrtime options
    var select = document.getElementById("Select-AggrTime");
    for (var i = 0; i < 13; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['atimetxt' + i];
        option.value = 'T' + i;
        select.add(option);
    }

    // CompareType options
    var select = document.getElementById("Select-Compare");
    for (var i = 0; i < 14; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['comptype' + i];
        option.value = 'C' + i;
        select.add(option);
    }

    // LineType options
    var select = document.getElementById("Select-Line");
    for (var i = 0; i < 12; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['linetype' + i];
        option.value = 'L' + i;
        select.add(option);
    }

    // DashType options
    var select = document.getElementById("Select-DashType");
    for (var i = 0; i < DP_DashType.length; i++) {
        var option = document.createElement("option");
        if (ChhLanguage.default.historian['dashtype' + i]) {
            option.text = ChhLanguage.default.historian['dashtype' + i];
        } else {
            option.text = DP_DashType[i];
        }
        option.value = 'D' + i;
        select.add(option);
    }

    // LineType options
    var select = document.getElementById("Select-LineWidth");
    for (var i = 0; i < 11; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['linewidth' + i];
        option.value = 'W' + i;
        select.add(option);
    }

    // Legend options
    var select = document.getElementById("Select-Legend");
    for (var i = 0; i < 7; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['legendtxt' + i];
        option.value = i;
        select.add(option);
    }

    // Navigator options
    var select = document.getElementById("Select-Navigator");
    for (var i = 0; i < 5; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['navitxt' + i];
        option.value = i;
        select.add(option);
    }

    // Label options
    var select = document.getElementById("Select-Label");
    for (var i = 0; i < 3; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['labeltxt' + i];
        option.value = i;
        select.add(option);
    }

    // Layout options
    var select = document.getElementById("Select-Layout");
    for (var i = 0; i < 4; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['layouttxt' + i];
        option.value = i;
        select.add(option);
    }
    
    // Grid options
    var select = document.getElementById("Select-Grid");
    for (var i = 0; i < 7; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['gridtxt' + i];
        option.value = i;
        select.add(option);
    }

    // Content options
    var select = document.getElementById("Select-Content");
    for (var i = 0; i < 4; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['contenttxt' + i];
        option.value = i;
        select.add(option);
    }

    // DataPoint options
    var select = document.getElementById("Select-DataPoint");
    for (var i = 0; i < 4; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['datapoint' + i];
        option.value = i;
        select.add(option);
    }
    
    // themes
    var select = document.getElementById("Select-Theme");
    for (var key in DP_Themes) {
        var option = document.createElement("option");
        option.text = key;
        option.value = key;
        select.add(option);
    }
    
    // Axis Type
    var select = document.getElementById("Select-AxisType");
    var option = document.createElement("option");
    option.text = ChhLanguage.default.historian.yaxistype0;
    option.value = '0';
    select.add(option);
    var option = document.createElement("option");
    option.text = ChhLanguage.default.historian.yaxistype1;
    option.value = '1';
    select.add(option);

    // Axis Position
    var select = document.getElementById("Select-Position");
    var option = document.createElement("option");
    option.text = ChhLanguage.default.historian.yaxispos0;
    option.value = '0';
    select.add(option);
    var option = document.createElement("option");
    option.text = ChhLanguage.default.historian.yaxispos1;
    option.value = '1';
    select.add(option);

    // Axis min/max
    var select = document.getElementById("Select-Limit");
    var option = document.createElement("option");
    option.text = ChhLanguage.default.historian.yaxislimit0;
    option.value = '0';
    select.add(option);
    var option = document.createElement("option");
    option.text = ChhLanguage.default.historian.yaxislimit1;
    option.value = '1';
    select.add(option);
    var option = document.createElement("option");
    option.text = ChhLanguage.default.historian.yaxislimit2;
    option.value = '2';
    select.add(option);
    
    // Stacking
    var select = document.getElementById("Select-Stacking");
    for (var i = 0; i < 6; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['Stacking' + i];
        option.value = i;
        select.add(option);
    }
    
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

});

// *******************
function ChangeEventRaumFilter() {
    var filter_raum = document.getElementById("Select-Raum").value;
    var filter_gewerk = document.getElementById("Select-Gewerk").value;
    var save_active = [];
    var save_active_found = false;
    var attr2

    chart = $('#container').highcharts();
    var series;

    // remove all old series
    for (i = chart.series.length - 1; i >= 0; i--) {
        chart.series[i].remove(false);
    }

    // add new series which are in filter
    for (i = 0; i < DP_point.length; i++) {
        if (check_filter(filter_raum, filter_gewerk, DP_point[i])) {

            addSerie(DP_point[i], '');
            series = chart.get(DP_point[i].idx.toString());

            // check if should be visible
            var attr = DP_attribute.findIndex(obj=>obj.id === DP_point[i].idx.toString());
            if (attr != -1) {

                if (DP_attribute[attr].visible === 2) {
                    series.visible = true;
                    save_active_found = true;
                } else {
                    series.visible = false;
                }

                // load comparisation series
                var compType = DP_attribute[attr].comp;
                if (compType != 'C0') {
                    // check if options exist, if not create it with default and C0
                    attr2 = DP_attribute.findIndex(obj=>obj.id === compType + '_' + DP_point[i].idx.toString());
                    if (attr2 === -1) {
                        DP_attribute.push({
                            id: compType + '_' + DP_point[i].idx.toString(),
                            aggr: DP_attribute[attr].aggr,
                            atime: DP_attribute[attr].atime,
                            yaxis: DP_attribute[attr].yaxis,
                            comp: 'C0',
                            line: DP_attribute[attr].line,
                            mark: DP_attribute[attr].mark,
                            color: DP_attribute[attr].color,
                            visible: DP_attribute[attr].visible,
                            dash: 'D1',
                            width: DP_attribute[attr].width,
                            stack: DP_attribute[attr].stack,
                            factor: DP_attribute[attr].factor,
                            offset: DP_attribute[attr].offset,
                            unit: DP_attribute[attr].unit,
                            buffer_data: {
                                timestamps: [],
                                values: [],
                                buffer_start: 0,
                                buffer_end: 0
                            },

                        });
                        // Pointer setzen
                        attr2 = DP_attribute.length - 1;
                    }

                    addSerie(DP_point[i], compType);

                    series = chart.get(DP_attribute[attr2].id);
                    if (series) {
                        if (DP_attribute[attr2].visible === 2) {
                            series.visible = true;
                            save_active_found = true;
                        } else {
                            series.visible = false;
                        }
                    }

                }
            }
        }
    }

    if (save_active_found) {
        loadNewSerienData();
        loadNewAxisInfo();
    } else {
        loadNewPlotBand();
        loadNewAxisInfo();
        chart.redraw();
    }
}

//*******
function check_filter(p_raum, p_gewerk, p_dp) {

    // Generell Filter
    if (p_dp.historyDisabled && ( DP_DataPointFilter == 0 || DP_DataPointFilter == 2 ) )
        return false;

    // Generell Filter
    if (p_dp.historyHidden && ( DP_DataPointFilter == 0 || DP_DataPointFilter == 1 ) )
        return false;

    // Room Filter
    if (p_raum != "ALLES" && p_raum != "SYSVAR") {
        if (p_dp.attributes.room === null)
            return false;
        if (p_dp.attributes.room.indexOf(p_raum) === -1)
            return false;
    }
    if (p_raum != "ALLES" && p_raum === "SYSVAR" && !p_dp.displayName.includes("SysVar."))
        return false;

    // Function Filter
    if (p_gewerk != "ALLES") {
        if (p_dp.attributes.function === null)
            return false;
        if (p_dp.attributes.function.indexOf(p_gewerk) === -1)
            return false;
    }

    // Description Filter
    if (filter_feld != '') {
        var ft = filter_feld.split(' ');
        for (fi = 0; fi < ft.length; fi++) {
            if ((p_dp.displayName + "/" + p_dp.id.address + "/ID:" + p_dp.idx).toLowerCase().indexOf(ft[fi]) === -1)
                return false;
        }
    }

    // only marked series are needed ?
    if (DP_Limit) {
        var attr = DP_attribute.findIndex(obj=>obj.id === p_dp.idx.toString());
        if (attr === -1)
            return false;
        if (DP_attribute[attr].visible === 0)
            return false;
    }

    return true;
}

//********************
function loadNewSerienData() {
    for (var serie = 0; serie < chart.series.length; serie++) {
        if (chart.series[serie].visible && chart.series[serie].options.group != "nav") {
            SetData(chart.series[serie]);
        }
    }
    chart.xAxis[0].setExtremes(Zeitraum_Start.getTime(), Zeitraum_Ende.getTime(), true);
    loadNewPlotBand();
    chart.redraw();
    loadNewAxisInfo();
}

function loadNewAxisInfo() {
	var yaxis_count = 0;
	
	var yaxis_grid = (DP_Grid === 2 || DP_Grid === 3 || DP_Grid === 5 || DP_Grid === 6) ? 1 : 0;
	var yaxis_mgrid = (DP_Grid === 5 || DP_Grid === 6) ? 1 : 0;

    for (var axispos = 0; axispos < DP_yAxis.length; axispos++) {
    	var axVisible = false;
        for (var serie = 0; serie < chart.yAxis[axispos].series.length; serie++) {
            if (chart.yAxis[axispos].series[serie].visible) {
            	axVisible = true;
            	break;
            }
        }   	
        
//         if (chart.yAxis[axispos].hasVisibleSeries) {
        if (axVisible) {
        	yaxis_count++;

            var axiscolor = null;
            if (DP_yAxis[axispos].color == 0) {
                axiscolor = Highcharts.defaultOptions.yAxis.lineColor;
            } else if (DP_yAxis[axispos].color == 1 && chart.yAxis[axispos].series.length > 0) {
                for (var s = 0; s < chart.yAxis[axispos].series.length; s++) {
                    if (chart.yAxis[axispos].series[s].visible) {
                        axiscolor = chart.yAxis[axispos].series[s].color;
                        break;
                    }
                }
            } else if (DP_yAxis[axispos].color > 1 && DP_yAxis[axispos].color < chart.options.colors.length + 2) {
                axiscolor = chart.options.colors[DP_yAxis[axispos].color - 2];
            }
            if (axiscolor != null && axiscolor != chart.yAxis[axispos].options.lineColor) {

                chart.yAxis[axispos].update({
                    lineColor: axiscolor,
                    labels: {
                        style: {
                            "color": axiscolor
                        }
                    },
                    title: {
                        style: {
                            "color": axiscolor
                        }
                    },

                    // set gridlines only on 1 
                	gridLineWidth: (yaxis_count === 1) ? yaxis_grid : 0,
                   	minorGridLineWidth: (yaxis_count === 1) ? yaxis_mgrid : 0,
                    minorTickInterval: (DP_Grid == 5 || DP_Grid == 6) ? 'auto' : null,
                    visible: true
                }, false);
            } else {
                chart.yAxis[axispos].update({
                    // set gridlines only on 1 
                	gridLineWidth: (yaxis_count === 1) ? yaxis_grid : 0,
                   	minorGridLineWidth: (yaxis_count === 1) ? yaxis_mgrid : 0,
                    minorTickInterval: (DP_Grid == 5 || DP_Grid == 6) ? 'auto' : null,
                    visible: true
                }, false);

            }

            // set extrem if config HARD
            if (DP_yAxis[axispos].limit == '2') {
                chart.yAxis[axispos].setExtremes(parseFloat(DP_yAxis[axispos].min), parseFloat(DP_yAxis[axispos].max));
            }
           
        } else {
            chart.yAxis[axispos].update({
                visible: false
            }, false);
        }
    }
    chart.redraw();

    // Reset Axis Click Event
    $('.highcharts-axis').click(function(event) {
        if (this.classList) {
            for (var axispos = 0; axispos < this.classList.length; axispos++) {
                if (this.classList[axispos].substr(0, 5) === 'axisy') {
                    var axisID = this.classList[axispos];
                    showDialogYAxis(axisID);
                    break;
                }
            }
        }
    });
    $('.highcharts-axis-labels').click(function(event) {
        if (this.classList) {
            for (var axispos = 0; axispos < this.classList.length; axispos++) {
                if (this.classList[axispos].substr(0, 5) === 'axisy') {
                    var axisID = this.classList[axispos];
                    showDialogYAxis(axisID);
                    break;
                }
            }
        }
    });

}

//********************
function loadNewPlotBand() {
    // add plotband for every day 00-06 and 20-24 gray, 06-20 yellow mean day

    // remove all PlotBands from xAxis[0]
    for (var band = chart.xAxis[0].plotLinesAndBands.length - 1; band >= 0; band--) {
        var band_id = chart.xAxis[0].plotLinesAndBands[band].id;
        if (chart.xAxis[0].plotLinesAndBands[band].options.to) {
            chart.xAxis[0].removePlotBand(band_id);
        } else {
            chart.xAxis[0].removePlotLine(band_id);
        }
    }

    // gray in night, day yellow
    if (DP_DayLight === 1) {
        var id = 1;
        for (var loopDate = Zeitraum_Start.getTime(); loopDate <= Zeitraum_Ende.getTime(); loopDate += 86400000) {
            var start = new Date(loopDate);
            chart.xAxis[0].addPlotBand({
                color: 'rgba(239,232,231,0.5)',
                //            color: '#EFE8E7',
                from: start.setHours(0, 0, 0, 0),
                to: start.setHours(6, 0, 0, 0),
                id: ('DayLight1' + id.toString()),
            });
            chart.xAxis[0].addPlotBand({
                color: 'rgba(251,252,227,0.5)',
                //            color: '#fbfce3',
                from: start.setHours(6, 0, 0, 0),
                to: start.setHours(20, 0, 0, 0),
                id: ('DayLight2' + id.toString()),
            });
            chart.xAxis[0].addPlotBand({
                color: 'rgba(239,232,231,0.5)',
                //            color: '#EFE8E7',
                from: start.setHours(20, 0, 0, 0),
                to: start.setHours(23, 59, 59, 999),
                id: ('DayLight3' + id.toString()),
            });
            id++;
        }
        // only line at 06:00 and 20:00
    } else if (DP_DayLight === 2) {
        var id = 1;
        for (var loopDate = Zeitraum_Start.getTime(); loopDate <= Zeitraum_Ende.getTime(); loopDate += 86400000) {
            var start = new Date(loopDate);
            chart.xAxis[0].addPlotLine({
                color: '#EFE8E7',
                value: start.setHours(6, 0, 0, 0),
                width: 2,
                id: ('DayLight1' + id.toString()),
            });
            chart.xAxis[0].addPlotBand({
                color: '#EFE8E7',
                value: start.setHours(20, 0, 0, 0),
                width: 2,
                id: ('DayLight2' + id.toString()),
            });
            id++;
        }
        // only line at 00:00
    } else if (DP_DayLight === 3) {
        var id = 1;
        for (var loopDate = Zeitraum_Start.getTime(); loopDate <= Zeitraum_Ende.getTime(); loopDate += 86400000) {
            var start = new Date(loopDate);
            chart.xAxis[0].addPlotLine({
                color: '#EFE8E7',
                value: start.setHours(0, 0, 0, 0),
                width: 2,
                id: ('DayLight1' + id.toString()),
            });
            id++;
        }
    }
}

//********************
function createUrl() {
    var url = location.pathname + "?";
    var attr;

    // Add Periode Parameter
    url += 'periode=' + (Math.round(((Zeitraum_Ende - Zeitraum_Start) / (60 * 60 * 1000)) * 100) / 100).toString();

    var url2 = '';
    // Add DP Filter if some selected
    for (var serie = 0; serie < chart.series.length; serie++) {
        if (chart.series[serie].options.group != "nav" && chart.series[serie].options.name != 'MinMax') {
            // add Attribute if exist
            attr = DP_attribute.findIndex(obj=>obj.id === chart.series[serie].options.id.toString());
            if (attr != -1) {
                if (DP_attribute[attr].visible == 2 || (DP_attribute[attr].visible == 1 && DP_Limit)) {
                    url2 += chart.series[serie].options.id;
                    url2 += (DP_attribute[attr].aggr === 'A0') ? '' : '|' + DP_attribute[attr].aggr;
                    url2 += (DP_attribute[attr].atime === 'T1') ? '' : '|' + DP_attribute[attr].atime;
                    url2 += (DP_attribute[attr].yaxis === 'Y0') ? '' : '|' + DP_attribute[attr].yaxis;
                    url2 += (DP_attribute[attr].line === 'L0') ? '' : '|' + DP_attribute[attr].line;
                    url2 += (DP_attribute[attr].color === 'F0') ? '' : '|' + DP_attribute[attr].color;
                    url2 += (DP_attribute[attr].comp === 'C0') ? '' : '|' + DP_attribute[attr].comp;
                    url2 += (DP_attribute[attr].mark === 'M0') ? '' : '|' + DP_attribute[attr].mark;
                    url2 += (DP_attribute[attr].dash === 'D0') ? '' : '|' + DP_attribute[attr].dash;
                    url2 += (DP_attribute[attr].width === 'W2') ? '' : '|' + DP_attribute[attr].width;
                    url2 += (DP_attribute[attr].stack === 0) ? '' : '|S' + DP_attribute[attr].stack;
                    url2 += (DP_attribute[attr].factor === 1) ? '' : '|X' + DP_attribute[attr].factor;
                    url2 += (DP_attribute[attr].offset === 0) ? '' : '|O' + DP_attribute[attr].offset;

                    // check if still default unit, otherwise add to url
                    if (chart.series[serie].options.id.substr(0, 1) === 'C') {
                        DP_pos = DP_point.findIndex(obj=>obj.idx.toString() === chart.series[serie].options.id.split('_')[1].toString());
                    } else {
                        DP_pos = DP_point.findIndex(obj=>obj.idx.toString() === chart.series[serie].options.id.toString());
                    }
                    if (DP_pos === -1 || DP_point[DP_pos].attributes.unit != DP_attribute[attr].unit) {
                        url2 += (DP_attribute[attr].unit === 'xx') ? '' : '|U' + DP_attribute[attr].unit.replace("%","§");
                    }

                    url2 += (DP_attribute[attr].visible === 2) ? '' : '|V' + DP_attribute[attr].visible;
                    url2 += ',';
                }
            }
        }
    }
    if (url2.length > 0) {
        url += '&dp=' + url2.substring(0, url2.length - 1);
    }

    var url2 = '';
    for (var axispos = 0; axispos < DP_yAxis.length; axispos++) {
        if (chart.yAxis[axispos].visible && chart.yAxis[axispos].hasVisibleSeries) {
            if (JSON.stringify(DP_yAxis[axispos]) != JSON.stringify(DP_yAxis_default[axispos])) {

                url2 += 'Y' + axispos;
                url2 += (DP_yAxis[axispos].position == DP_yAxis_default[axispos].position) ? '' : '|P' + ((DP_yAxis[axispos].position) ? '1' : '0');
                url2 += (DP_yAxis[axispos].type == DP_yAxis_default[axispos].type) ? '' : '|C' + DP_yAxis[axispos].type;
                url2 += (DP_yAxis[axispos].limit == DP_yAxis_default[axispos].limit) ? '' : '|A' + DP_yAxis[axispos].limit;
                url2 += (DP_yAxis[axispos].min == DP_yAxis_default[axispos].min) ? '' : '|L' + DP_yAxis[axispos].min;
                url2 += (DP_yAxis[axispos].max == DP_yAxis_default[axispos].max) ? '' : '|H' + DP_yAxis[axispos].max;
                url2 += (DP_yAxis[axispos].tick == DP_yAxis_default[axispos].tick) ? '' : '|G' + DP_yAxis[axispos].tick;
                url2 += (DP_yAxis[axispos].color == DP_yAxis_default[axispos].color) ? '' : '|F' + DP_yAxis[axispos].color;
                url2 += (DP_yAxis[axispos].text == DP_yAxis_default[axispos].text) ? '' : '|T' + DP_yAxis[axispos].text.replace("%","§");
                url2 += ',';
            }
        }
    }

    if (url2.length > 0) {
        url += '&yaxis=' + url2.substring(0, url2.length - 1);
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
    if (DP_Legend != 1) {
        url += '&legend=' + DP_Legend;
    }

    // Navigator not show    
    if (DP_Navigator != 0) {
        url += '&navigator=' + DP_Navigator.toString();
    }

    // Labels show    
    if (DP_Labels != 0) {
        url += '&labels=' + DP_Labels;
    }

    // DayLight show    
    if (DP_DayLight != 1) {
        url += '&daylight=' + DP_DayLight;
    }

    // Grid show    
    if (DP_Grid != 2) {
        url += '&grid=' + DP_Grid;
    }
    // AutoRefresh    
    if (DP_AutoRefresh != 0) {
        url += '&refresh=' + (DP_AutoRefresh === 60 ? true : DP_AutoRefresh);
    }

    // showFilterLine()    
    if (DP_ShowFilter === 0) {
        url += '&filterline=false';
    } else if (DP_ShowFilter != 1) {
        url += '&filterline=' + DP_ShowFilter;
    }

    // showFilterLine()    
    if (DP_DataPointFilter != 0) {
        url += '&dpfilter='+DP_DataPointFilter;
    }

    // Theme    
    if (DP_Theme != '' && DP_Theme != 'Standard') {
        url += '&theme=' + DP_Theme;
    }

    // Title    
    if (DP_Title != '') {
        url += '&title=' + DP_Title.replace("%","§");
    }

    // Subtitle    
    if (DP_Subtitle != '') {
        url += '&subtitle=' + DP_Subtitle.replace("%","§");
    }

    window.open(url, '_blank');
    window.focus();
}

//********************
function AutoRefresh() {
    if (DP_AutoRefresh > 0) {
        setTimeout(AutoRefresh, 1000);
        if (DP_Navigator < 3) {
        	document.getElementById('autorefresh').innerHTML = ' - ' + ChhLanguage.default.highcharts.autorefreshText + ':' + AutoRefreshCount + ' Sek.';
        }
        AutoRefreshCount--;
        if (AutoRefreshCount <= 0) {
            AutoRefreshCount = DP_AutoRefresh;
            var dauer = Zeitraum_Ende.getTime() - Zeitraum_Start.getTime();
            Zeitraum_Ende = new Date(Date.now());
            Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(dauer)));
            loadNewSerienData();
        }
    } else {
        document.getElementById('autorefresh').innerHTML = '';
    }
}


//********************
function loadingInfo() {
    if (DP_Queue.length > 0 && DP_Navigator < 3) {
        if (DP_Loading != DP_Queue.length) {
           document.getElementById('loading').innerHTML = ' (Loading - '+DP_Queue.length+') <img src="loading.gif" alt="loading" height="20" width="20">';
           DP_Loading = DP_Queue.length;
        }
        setTimeout(loadingInfo, 500);
    } else {
        document.getElementById('loading').innerHTML = '';
        DP_Loading = 0;
    }
}


//********************
function AddAggregationMinMax(serieObj) {

    var arr_dp = [];

    // first delete all linked series
    for (var i = chart.series.length - 1; i >= 0; i--) {
        if (serieObj.options.id === chart.series[i].options.linkedTo && chart.series[i].options.name === 'MinMax') {
            chart.series[i].remove(false);
        }
    }

    Highcharts.each(serieObj.userOptions.data, function(p, i) {
        arr_dp.push([p[0], p[1], p[1]]);
    })

    var serie2 = chart.addSeries({
        name: 'MinMax',
        fillOpacity: 0.4,
        color: serieObj.color,
        yAxis: serieObj.options.yAxis,
        linkedTo: serieObj.options.id,
        type: 'arearange',
        lineWidth: 1,
        dataGrouping: {
            enabled: true,
            forced: true,
            //               approximation: 'averages',
            groupPixelWidth: 10,
            units: serieObj.userOptions.dataGrouping.units
        },
        data: arr_dp,
        tooltip: {
            valueDecimals: serieObj.userOptions.tooltip.valueDecimals,
            valueSuffix: serieObj.userOptions.tooltip.valueSuffix,
        },
    })
}

// Show Dialog
function showDialogLine(serieObj) {

    // Set Dialog Values
    if (serieObj.options.id) {

        if ('C' === serieObj.options.id.toString().substr(0, 1)) {
            document.getElementById("compare").style.display = 'none';
        } else {
            document.getElementById("compare").style.display = '';
        }

        var techName = '';
        if (serieObj.options.id.substr(0, 1) === 'C') {
            DP_pos = DP_point.findIndex(obj=>obj.idx.toString() === serieObj.options.id.split('_')[1].toString());
        } else {
            DP_pos = DP_point.findIndex(obj=>obj.idx.toString() === serieObj.options.id.toString());
        }

        if (DP_pos === -1) {
            techName = 'n/a';
        } else if (DP_point[DP_pos].id.interfaceId === "SysVar") {
            techName = '<br/>Systemvariable';
        } else {
            techName = '<br/>' + DP_point[DP_pos].id.interfaceId + '.' + DP_point[DP_pos].id.address + '.' + DP_point[DP_pos].id.identifier;
        }

        DP_PopupID = serieObj.options.id.toString();

        var attr = DP_attribute.findIndex(obj=>obj.id === serieObj.options.id.toString());
        if (attr === -1) {

            var ArrAttr;
            if (DP_pos === -1) {
               ArrAttr = defaultAttrib(-1, serieObj.colorIndex, DP_PopupID);
            } else {
               ArrAttr = defaultAttrib(DP_point[DP_pos], serieObj.colorIndex, DP_PopupID);
            }
  
            // ArrAttr.yasix = 'Y' + serieObj.options.yAxis;

            DP_attribute.push(ArrAttr);

            attr = DP_attribute.length - 1;

        }

        // set value on Popup
        document.getElementsByClassName("modal-title")[0].innerHTML = serieObj.name + techName;

        document.getElementById("Select-Aggregation").value = DP_attribute[attr].aggr;
        document.getElementById("Select-AggrTime").value = DP_attribute[attr].atime;
        document.getElementById("Select-Yaxis").value = DP_attribute[attr].yaxis;
        document.getElementById("Select-Compare").value = DP_attribute[attr].comp;
        document.getElementById("Select-Stacking").value = DP_attribute[attr].stack;
        document.getElementById("Select-Line").value = DP_attribute[attr].line;
        document.getElementById("Select-Color").value = DP_attribute[attr].color;
        document.getElementById("Select-Marker").value = DP_attribute[attr].mark;
        document.getElementById("Select-DashType").value = DP_attribute[attr].dash;
        document.getElementById("Select-LineWidth").value = DP_attribute[attr].width;
        document.getElementById("Line-Factor").value = DP_attribute[attr].factor;
        document.getElementById("Line-OffSet").value = DP_attribute[attr].offset;
        document.getElementById("Line-Unit").value = DP_attribute[attr].unit;

        document.getElementById("Select-Color").style.backgroundColor = chart.options.colors[parseInt(document.getElementById("Select-Color").value.substr(1, 2))];

        $("#LinePopup").modal();
    }
}

// Close Dialog
$("#DialogBtnOK").click(function() {
	getDialogLine();
});

//Close Dialog and save as default
$("#LineDefault").click(function() {
	saveLine();
});

function saveLine() {	

	getDialogLine();

	var attr = DP_attribute.findIndex(obj=>obj.id === DP_PopupID);
    if (attr == -1) {
    	return
    }
    var strCustom = '';
    strCustom +=       DP_attribute[attr].aggr;
    strCustom += '|' + DP_attribute[attr].atime;
    strCustom += '|' + DP_attribute[attr].yaxis;
    strCustom += '|' + DP_attribute[attr].line;
    strCustom += '|' + DP_attribute[attr].color;
    strCustom += '|' + DP_attribute[attr].comp;
    strCustom += '|' + DP_attribute[attr].mark;
    strCustom += '|' + DP_attribute[attr].dash;
    strCustom += '|' + DP_attribute[attr].width;
    strCustom += '|S' + DP_attribute[attr].stack;
    strCustom += '|X' + DP_attribute[attr].factor;
    strCustom += '|O' + DP_attribute[attr].offset;
    strCustom += '|U' + DP_attribute[attr].unit;

    var DP_pos = DP_point.findIndex(obj=>obj.idx.toString() === DP_PopupID);
    var key = 'POINT'+DP_PopupID;

    // define customer if still NULL
    if (!DP_point[DP_pos].attributes.custom) {
       DP_point[DP_pos].attributes.custom = {};
    }

    if (DP_point[DP_pos].attributes.custom.HighChart != strCustom ) {
    	
    	DP_point[DP_pos].attributes.custom.HighChart = strCustom;

       var url = 'http://' + H2_server + ':' + H2_port;
       url += '/query/jsonrpc.gy';
       url += (DP_ApiKey=="")?"":"?"+DP_ApiKey;

       var postData = {id: key,
                       method: 'updateDataPoint',
                       params: [{ 'id': {'interfaceId': DP_point[DP_pos].id.interfaceId,
                                         'address':     DP_point[DP_pos].id.address,
                                         'identifier':  DP_point[DP_pos].id.identifier },
                                  'attributes': {'custom': {'HighChart': strCustom }}}]};

       postData = JSON.stringify(postData);

	    $.ajax({
	        url: url,
	        dataType: "json",
	        contentType: "application/json",
	        type: "post",
           data: postData,
	        cache: false,
	        async: true,
	        error: function(xhr, status, error) {
	            console.log('AXAJ-error:');
	            console.log(xhr);
	            console.log(status);
	            console.log(error);
	        },
	        success: function(result) {
	            console.log(result);
	        }
	    });
	    
    }
    return;	
	
}

// Close Dialog Line
$("#DialogBtnClose").click(function() {
    $("#LinePopup").modal('hide');
});

//Show Dialog
function getDialogLine() {
    var attr = DP_attribute.findIndex(obj=>obj.id === DP_PopupID);

    if (DP_attribute[attr].comp != document.getElementById("Select-Compare").value && document.getElementById("Select-Compare").value != 'C0' && DP_attribute[attr].comp != 'C0') {
        // change comparisation ID on old one, search any old one to update ID
        var attrC = DP_attribute.findIndex(obj=>obj.id.substr(0, 1) === 'C' && obj.id.split('_')[1] === DP_PopupID);
        if (attrC != -1) {
            DP_attribute[attrC].id = document.getElementById("Select-Compare").value + '_' + DP_PopupID;
        }
    }

    // get value on Popup
    DP_attribute[attr].aggr = document.getElementById("Select-Aggregation").value;
    DP_attribute[attr].atime = document.getElementById("Select-AggrTime").value;
    DP_attribute[attr].yaxis = document.getElementById("Select-Yaxis").value;
    DP_attribute[attr].comp = document.getElementById("Select-Compare").value;
    DP_attribute[attr].stack = parseInt(document.getElementById("Select-Stacking").value);
    DP_attribute[attr].line = document.getElementById("Select-Line").value;
    DP_attribute[attr].color = document.getElementById("Select-Color").value;
    DP_attribute[attr].mark = document.getElementById("Select-Marker").value;
    DP_attribute[attr].dash = document.getElementById("Select-DashType").value;
    DP_attribute[attr].width = document.getElementById("Select-LineWidth").value;
    DP_attribute[attr].factor = parseFloat(document.getElementById("Line-Factor").value);
    DP_attribute[attr].offset = parseFloat(document.getElementById("Line-OffSet").value);
    DP_attribute[attr].unit = document.getElementById("Line-Unit").value;

    // ignor 0 values for faktor
    if (isNaN(DP_attribute[attr].factor) || DP_attribute[attr].factor === 0.0)
        DP_attribute[attr].factor = 1;

    $("#LinePopup").modal('hide');

    ChangeEventRaumFilter();	
}

// Show Dialog
function showDialogSettings() {

    // set value on Popup
    document.getElementsByClassName("modal-title2")[0].innerHTML = ChhLanguage.default.historian.settings;
    document.getElementById("Select-Legend").value = DP_Legend.toString();
    document.getElementById("Select-Navigator").value = DP_Navigator.toString();
    document.getElementById("Select-Label").value = DP_Labels.toString();
    document.getElementById("Select-Layout").value = DP_DayLight.toString();
    document.getElementById("Select-Grid").value = DP_Grid.toString();
    document.getElementById("Select-Content").value = DP_ShowFilter.toString();
    document.getElementById("Select-DataPoint").value = DP_DataPointFilter.toString();
    document.getElementById("Select-Theme").value = DP_Theme;
    document.getElementById("Line-Refresh").value = DP_AutoRefresh;
    document.getElementById("Line-Title").value = DP_Title;
    document.getElementById("Line-Subtitle").value = DP_Subtitle;

    $("#SettingPopup").modal();
}

// Close Dialog Settings
$("#Dialog2BtnOK").click(function() {
	getDialogSetting();
});

	
//Close Dialog and save as default
$("#SettingDefault").click(function() {
	saveSetting();
});
	
function saveSetting() {
	
	getDialogSetting();
	
	
//  var text2 = DP_settings['HighChart_YAXIS'+x].split('|');
//  var text2 = DP_settings['HighChart_Setting'].split('|');	
	
	
    var strCustom = '';
    strCustom += 'L' + DP_Legend.toString();
    strCustom += '|N' + DP_Navigator.toString();
    strCustom += '|P' + DP_Labels.toString();
    strCustom += '|D' + DP_DayLight.toString();
    strCustom += '|G' + DP_Grid.toString();
    strCustom += '|F' + DP_ShowFilter.toString();
    strCustom += '|I' + DP_DataPointFilter.toString();
    strCustom += '|B' + DP_Theme;
    strCustom += '|R' + DP_AutoRefresh;
    strCustom += '|T' + DP_Title;
    strCustom += '|S' + DP_Subtitle;

// Save to Global Settings    
    DP_settings.Setting = strCustom;
    
    saveSettingsH2();
}

function saveSettingsH2() {
    	
    var key = 'SETTING';
	var strSetNew = JSON.stringify(DP_settings);
    var strSetOld = JSON.stringify(DP_settings_old);
	
    if (strSetNew != strSetOld) {
    	
    	DP_settings_old = JSON.parse(strSetNew);

       var url = 'http://' + H2_server + ':' + H2_port;
       url += '/query/jsonrpc.gy';
       url += (DP_ApiKey=="")?"":"?"+DP_ApiKey;


       strSetNew = strSetNew.replace(new RegExp('"', 'g'), "'");

       var postData = {id: key,
                       method: 'setConfig',
                       params: [ 'HighChart', strSetNew ]};

       postData = JSON.stringify(postData);

	    $.ajax({
	        url: url,
	        dataType: "json",
	        contentType: "application/json",
	        type: "post",
            data: postData,
	        cache: false,
	        async: true,
	        error: function(xhr, status, error) {
	            console.log('AXAJ-error:');
	            console.log(xhr);
	            console.log(status);
	            console.log(error);
	        },
	        success: function(result) {
	            console.log(result);
	        }
	    });
    }
    return;	
}

function getDialogSetting() {

	$("#SettingPopup").modal('hide');

    var filterrefresh = false;

    // Legend
    if (DP_Legend.toString() != document.getElementById("Select-Legend").value) {
        DP_Legend = parseInt(document.getElementById("Select-Legend").value);
        chart.legend.update(defineLegend());
        if (DP_Legend == 3 || DP_Legend == 4 || DP_Legend == 5 | DP_Legend == 6) {
            if (!DP_Limit) {
                DP_Limit = true;
                filterrefresh = true;
                $('.highcharts-contextmenu')[0].children[0].children[1].innerHTML = ChhLanguage.default.highcharts.limitactive;
            }
        }
    }

    // Navigator
    if (DP_Navigator.toString() != document.getElementById("Select-Navigator").value) {
        DP_Navigator = parseInt(document.getElementById("Select-Navigator").value);
        
        /* chart.navigator.update({
            enabled: (DP_Navigator == 0 || DP_Navigator == 1 ) ? true : false,
        });
        chart.scrollbar.update({
            enabled: (DP_Navigator == 0 || DP_Navigator == 2) ? true : false,
        });
        chart.credits.update({
            enabled: (DP_Navigator != 3) ? true : false,
        });

        // chart.legend.update(defineLegend());
        // chart.redraw();
        */
        ChartSetOptions();
        chartSetElements();
        filterrefresh = false;
    }

    // Title
    if (DP_Title != document.getElementById("Line-Title").value) {
        DP_Title = document.getElementById("Line-Title").value;
        chart.title.update({
            text: DP_Title
        });
    }

    // Subtitle
    if (DP_Subtitle != document.getElementById("Line-Subtitle").value) {
        DP_Subtitle = document.getElementById("Line-Subtitle").value;
        chart.subtitle.update({
            text: DP_Subtitle
        });
    }

    // Labels
    if (DP_Labels.toString() != document.getElementById("Select-Label").value) {
        DP_Labels = parseInt(document.getElementById("Select-Label").value);
        filterrefresh = true;
    }

    // Layout
    if (DP_DayLight.toString() != document.getElementById("Select-Layout").value) {
        DP_DayLight = parseInt(document.getElementById("Select-Layout").value);
        filterrefresh = true;
    }

    // Grid
    if (DP_Grid.toString() != document.getElementById("Select-Grid").value) {
        DP_Grid = parseInt(document.getElementById("Select-Grid").value);
        ChartSetOptions();
        filterrefresh = true;
    }

    // FilterLine
    if (DP_ShowFilter.toString() != document.getElementById("Select-Content").value) {
        DP_ShowFilter = parseInt(document.getElementById("Select-Content").value);
        showFilterLine();
        filterrefresh = true;
    }
    
    // DataPointFilter
    if (DP_DataPointFilter.toString() != document.getElementById("Select-DataPoint").value) {
    	DP_DataPointFilter = parseInt(document.getElementById("Select-DataPoint").value);
        filterrefresh = true;
    }


    // Theme
    if (DP_Theme != document.getElementById("Select-Theme").value) {
        DP_Theme = document.getElementById("Select-Theme").value;
        if (DP_Theme == 'Standard') {
            Highcharts.setOptions(DP_Themes.Standard);
        } else {
            if (DP_Themes[DP_Theme]) {
                var chartingOptions = Highcharts.merge(DP_Themes.Standard, DP_Themes[DP_Theme]);
                Highcharts.setOptions(chartingOptions);
            }
        }

        ChartSetOptions();

        chartSetElements();

        filterrefresh = false;
    }
    // AutoRefresh

    if (DP_AutoRefresh != parseInt(document.getElementById("Line-Refresh").value)) {
        H2_refreshSec = parseInt(document.getElementById("Line-Refresh").value);
        AutoRefreshCount = H2_refreshSec;
        if (DP_AutoRefresh == 0 && H2_refreshSec > 0) {
            setTimeout(AutoRefresh, 1000);
        }
        DP_AutoRefresh = H2_refreshSec;
    }

    if (filterrefresh) {
        ChangeEventRaumFilter();
    }

};

// Close Dialog Settings
$("#Dialog2BtnClose").click(function() {
    $("#SettingPopup").modal('hide');
});

function showFilterLine() {
	// check height of navigator+messages+range selector
	if (DP_Navigator == 4 || DP_Navigator == 3) {
		var nav_height = 10;
	} else {
		var nav_height = 55;
	}		
	
    // ajust height of content to screen height
    if (DP_ShowFilter === 0) {
        document.getElementById("container").setAttribute("style", "height:" + ($(document).height() - nav_height - 0) + "px");
        document.getElementById("filter").style.display = "none";
        $('nav.navbar.navbar-default')[0].style.display = "none";
    } else if (DP_ShowFilter === 1) {
        document.getElementById("container").setAttribute("style", "height:" + ($(document).height() - nav_height - 105) + "px");
        document.getElementById("filter").style.display = "block";
        $('nav.navbar.navbar-default')[0].style.display = "block";
    } else if (DP_ShowFilter === 2) {
        document.getElementById("container").setAttribute("style", "height:" + ($(document).height() - nav_height - 35) + "px");
        document.getElementById("filter").style.display = "block";
        $('nav.navbar.navbar-default')[0].style.display = "none";
    } else if (DP_ShowFilter === 3) {
        document.getElementById("container").setAttribute("style", "height:" + ($(document).height() - nav_height - 70) + "px");
        document.getElementById("filter").style.display = "none";
        $('nav.navbar.navbar-default')[0].style.display = "block";
    }
    if (chart) {
        chart.setSize(null, null, false);
    }
}

function defineLegend() {
    var ret = {};
    if (DP_Legend == 0) {
        ret = {
            enabled: false,
        };
    } else if (DP_Legend == 2) {
        ret = {
            enabled: true,
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            floating: false,
            y: 30,
        };
    } else if (DP_Legend == 3) {
        ret = {
            enabled: true,
            layout: 'horizontal',
            align: 'center',
            verticalAlign: 'top',
            floating: true,
            y: 25,
        };
    } else if (DP_Legend == 4) {
        ret = {
            enabled: true,
            layout: 'horizontal',
            align: 'center',
            verticalAlign: 'bottom',
            floating: true,
            y: (DP_Navigator == 1) ? -50 : -70,
        };
    } else if (DP_Legend == 5) {
        ret = {
            enabled: true,
            layout: 'horizontal',
            align: 'center',
            verticalAlign: 'top',
            floating: false,
            y: 40,
        };
    } else if (DP_Legend == 6) {
        ret = {
            enabled: true,
            layout: 'horizontal',
            align: 'center',
            verticalAlign: 'bottom',
            floating: false,
            y: 0,
        };
        // on DP_Legend = 1 and default
    } else {
        ret = {
            enabled: true,
            layout: 'vertical',
            align: 'left',
            verticalAlign: 'top',
            floating: false,
            y: 0,
        };
    }
    ret['backgroundColor'] = '#FFFFFF';
    ret['x'] = 0;
    ret['navigation'] = {
        arrowSize: 20
    };

    return ret;
}

// Show Dialog
function showDialogYAxis(id) {

    document.getElementsByClassName("modal-title3")[0].innerHTML = ChhLanguage.default.historian.axissetting + ' ' + id.substr(5, 2);

    // find axis object
    var axispos = parseInt(id.substr(5, 2));
    if (axispos >= 0 && axispos < chart.options.yAxis.length) {
        var DP_PopupAxisObj = chart.options.yAxis[axispos];
        DP_PopupAxisPos = axispos;

        document.getElementById("Line-Title3").value = DP_PopupAxisObj.title.text;
        document.getElementById("Select-Position").value = DP_PopupAxisObj.opposite ? '1' : '0';
        if (DP_PopupAxisObj.softMax) {
            document.getElementById("Line-Min").value = DP_PopupAxisObj.softMin;
            document.getElementById("Line-Max").value = DP_PopupAxisObj.softMax;
        } else if (DP_PopupAxisObj.max) {
            document.getElementById("Line-Min").value = DP_PopupAxisObj.min;
            document.getElementById("Line-Max").value = DP_PopupAxisObj.max;
        } else {
            document.getElementById("Line-Min").value = 0;
            document.getElementById("Line-Max").value = 0;
        }
        document.getElementById("Line-TickAmount").value = DP_PopupAxisObj.tickAmount;
        document.getElementById("Select-AxisColor").value = DP_yAxis[DP_PopupAxisPos].color;
        document.getElementById("Select-Limit").value = DP_yAxis[DP_PopupAxisPos].limit;
        document.getElementById("Select-AxisType").value = DP_yAxis[DP_PopupAxisPos].type;

    } else {
        return;
    }
    
    showDialogYAxisUpdatColor();

    $("#AxisPopup").modal();
}

// Close Dialog Settings
$("#Dialog3BtnOK").click(function() {
	getDialogAxis();
});

//Close Dialog and save as default
$("#AxisDefault").click(function() {
	getDialogAxis();

    var strCustom = '';
    strCustom += 'P' + ((DP_yAxis[DP_PopupAxisPos].position) ? '1' : '0');
    strCustom += '|C' + DP_yAxis[DP_PopupAxisPos].type;
    strCustom += '|A' + DP_yAxis[DP_PopupAxisPos].limit;
    strCustom += '|L' + DP_yAxis[DP_PopupAxisPos].min;
    strCustom += '|H' + DP_yAxis[DP_PopupAxisPos].max;
    strCustom += '|G' + DP_yAxis[DP_PopupAxisPos].tick;
    strCustom += '|F' + DP_yAxis[DP_PopupAxisPos].color;
    strCustom += '|T' + DP_yAxis[DP_PopupAxisPos].text;
    
// Save to global Settings    
    DP_settings['YAXIS'+DP_PopupAxisPos] = strCustom; 
    
    saveSettingsH2();
    
    return;	
	
});

function getDialogAxis() {
	
    $("#AxisPopup").modal('hide');

    // Update YAxis parameter
    chart.yAxis[DP_PopupAxisPos].update({
        title: {
            text: document.getElementById("Line-Title3").value
        },
        lineWidth: 2,
        opposite: (document.getElementById("Select-Position").value == '1') ? true : false,
        type: (document.getElementById("Select-AxisType").value == '1') ? 'logarithmic' : 'linear',
        tickAmount: parseInt(document.getElementById("Line-TickAmount").value),
        min: (document.getElementById("Select-Limit").value == '2') ? parseFloat(document.getElementById("Line-Min").value) : null,
        max: (document.getElementById("Select-Limit").value == '2') ? parseFloat(document.getElementById("Line-Max").value) : null,
        softMin: (document.getElementById("Select-Limit").value == '1') ? parseFloat(document.getElementById("Line-Min").value) : null,
        softMax: (document.getElementById("Select-Limit").value == '1') ? parseFloat(document.getElementById("Line-Max").value) : null,
        startOnTick: (document.getElementById("Select-Limit").value == '2') ? true : false,
        endOnTick: (document.getElementById("Select-Limit").value == '2') ? true : false,
        allowDecimals: true,
    });

    DP_yAxis[DP_PopupAxisPos].text = document.getElementById("Line-Title3").value;
    DP_yAxis[DP_PopupAxisPos].position = (parseInt(document.getElementById("Select-Position").value) == 0) ? false : true;
    DP_yAxis[DP_PopupAxisPos].limit = parseInt(document.getElementById("Select-Limit").value);
    DP_yAxis[DP_PopupAxisPos].type = parseInt(document.getElementById("Select-AxisType").value);
    DP_yAxis[DP_PopupAxisPos].min = parseFloat(document.getElementById("Line-Min").value);
    DP_yAxis[DP_PopupAxisPos].max = parseFloat(document.getElementById("Line-Max").value);
    DP_yAxis[DP_PopupAxisPos].tick = parseInt(document.getElementById("Line-TickAmount").value);
    DP_yAxis[DP_PopupAxisPos].color = parseInt(document.getElementById("Select-AxisColor").value);

    // Yaxis options
    $("#Select-Yaxis").empty();
    var select = document.getElementById("Select-Yaxis");
    for (var i = 0; i < DP_yAxis.length; i++) {
        var option = document.createElement("option");
        if (DP_yAxis[i].text != "" && DP_yAxis[i].text != null) {
            option.text = DP_yAxis[i].text;
        } else {
            option.text = ChhLanguage.default.historian['yaxis' + i];
        }
        option.value = 'Y' + i;
        select.add(option);
    }
    
    loadNewAxisInfo();

};


// Close Dialog Settings
$("#Dialog3BtnClose").click(function() {
    $("#AxisPopup").modal('hide');
});

// define Y-Axis array
function defineYAxis() {
    var arr = [];
    for (var y = 0; y < DP_yAxis.length; y++) {
        arr.push({
            id: 'AXISY' + y,
            className: 'axisy' + y,
            type: (DP_yAxis[y].type == 1) ? 'logarithmic' : 'linear',
            title: {
                text: DP_yAxis[y].text
            },
            lineWidth: 2,
            // showEmpty: false,
            opposite: (DP_yAxis[y].position == 1) ? true : false,
            tickAmount: DP_yAxis[y].tick,
            min: (DP_yAxis[y].limit == 2) ? DP_yAxis[y].min : null,
            max: (DP_yAxis[y].limit == 2) ? DP_yAxis[y].max : null,
            softMin: (DP_yAxis[y].limit == 1) ? DP_yAxis[y].min : null,
            softMax: (DP_yAxis[y].limit == 1) ? DP_yAxis[y].max : null,
            startOnTick: (DP_yAxis[y].limit == 2) ? true : false,
            endOnTick: (DP_yAxis[y].limit == 2) ? true : false,
            allowDecimals: true,
            visible: false,
        });
    }
    return arr;
}

// *** update background color on Field Select-Color
$("#Select-Color").on("change", function() {
    document.getElementById("Select-Color").style.backgroundColor = chart.options.colors[parseInt(document.getElementById("Select-Color").value.substr(1, 2))];
});

//*** update background color on Field Select-Color
$("#Select-AxisColor").on("change", function() {
   showDialogYAxisUpdatColor();
});


function showDialogYAxisUpdatColor() {
    var colorPos = parseInt(document.getElementById("Select-AxisColor").value); 
    if (colorPos == 0 || colorPos == 1) {
        document.getElementById("Select-AxisColor").style.backgroundColor = Highcharts.defaultOptions.yAxis.lineColor;
    } else {
	   colorPos -= 2;   // set back -2
	   document.getElementById("Select-AxisColor").style.backgroundColor = chart.options.colors[colorPos];
	} 	
}

// define Comparisation days back
function getComparisionBackDay(str_compType) {
    if (str_compType === 'C1')
        return -1 * 86400000;
    if (str_compType === 'C2')
        return -2 * 86400000;
    if (str_compType === 'C3')
        return -3 * 86400000;
    if (str_compType === 'C4')
        return -4 * 86400000;
    if (str_compType === 'C5')
        return -1 * 7 * 86400000;
    if (str_compType === 'C6')
        return -2 * 7 * 86400000;
    if (str_compType === 'C7')
        return -3 * 7 * 86400000;
    if (str_compType === 'C8')
        return -4 * 7 * 86400000;
    if (str_compType === 'C9')
        return -1 * 7 * 4 * 86400000;
    if (str_compType === 'C10')
        return -2 * 7 * 4 * 86400000;
    if (str_compType === 'C11')
        return -3 * 7 * 4 * 86400000;
    if (str_compType === 'C12')
        return -4 * 7 * 4 * 86400000;
    if (str_compType === 'C13')
        return -365 * 86400000;
    return 0
}

function ChartSetOptions() {

    Highcharts.stockChart('container', {
        chart: {
            events: {
                load: requestData,
            },
            panning: true,
            panKey: 'shift',
            zoomType: 'xy',
            resetZoomButton: {
                position: {
                    x: -50,
                    y: 20,
                },
                relativeTo: 'plot',
            },
        },

        rangeSelector: {
        	enabled: (DP_Navigator < 4) ? true : false,
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
                text: ChhLanguage.default.highcharts.rangeD,
            }, {
                count: 1,
                type: 'week',
                text: ChhLanguage.default.highcharts.rangeW,
            }, {
                count: 1,
                type: 'month',
                text: ChhLanguage.default.highcharts.rangeM,
            }, {
                count: 1,
                type: 'year',
                text: ChhLanguage.default.highcharts.rangeY,
            }, {
                type: 'all',
                text: ChhLanguage.default.highcharts.rangeALL
            }],
            allButtonsEnabled: true,
            inputEnabled: false,
            selected: 7,

            floating: false,
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
                enabled: (DP_Navigator < 4) ? true : false,
            }
        },
        navigator: {
            enabled: (DP_Navigator == 0 || DP_Navigator == 1 ) ? true : false,
        },
        scrollbar: {
            enabled: (DP_Navigator == 0 || DP_Navigator == 2) ? true : false,
        },
        credits: {
            enabled: (DP_Navigator < 3) ? true : false,
            text: '(c) wak - H2-HighChart version ' + H2_version + ' - verwendet Highstock http://www.highcharts.com - Kommerzielle Nutzung untersagt',
            href: 'https://github.com/wakr70/CCU-Historian-HC'
        },

        exporting: {
            buttons: {
                contextButton: {
                    symbol: "menu",
                    enabled: (DP_Navigator < 4) ? true : false,
                    menuItems: [{
                        text: ChhLanguage.default.historian.settings,
                        onclick: function() {
                            showDialogSettings();
                        }
                    }, {
                        text: (DP_Limit) ? ChhLanguage.default.highcharts.limitactive : ChhLanguage.default.highcharts.limitdeactive,
                        onclick: function() {
                            if (DP_Limit) {
                                $('.highcharts-contextmenu')[0].children[0].children[1].innerHTML = ChhLanguage.default.highcharts.limitdeactive;
                                DP_Limit = false;
                            } else {
                                $('.highcharts-contextmenu')[0].children[0].children[1].innerHTML = ChhLanguage.default.highcharts.limitactive;
                                DP_Limit = true;
                            }
                            ChangeEventRaumFilter();
                        },
                    }, {
                        text: ChhLanguage.default.historian.buttonRefresh,
                        onclick: function() {
                            Zeitraum_Ende = new Date(Date.now());
                            loadNewSerienData();
                        },
                    }, {
                        text: ChhLanguage.default.historian.buttonLink,
                        onclick: function() {
                            createUrl();
                        },
                    }, "separator", "printChart", "downloadPNG", "downloadJPEG", "downloadPDF", "downloadSVG", ]
                }
            }
        },

        title: {
            text: DP_Title,
            floating: false,
        },
        subtitle: {
            text: DP_Subtitle,
            floating: false,
        },

        xAxis: {
            type: 'datetime',
            ordinal: false,
        	gridLineWidth: (DP_Grid == 1 || DP_Grid == 3 || DP_Grid == 4 || DP_Grid == 6) ? 1 : 0,
        	minorGridLineWidth: (DP_Grid == 4 || DP_Grid == 6) ? 1 : 0,
        	minorTickInterval: (DP_Grid == 4 || DP_Grid == 6) ? 'auto' : null,
            dataMax: Date.now(),
            events: {
                afterSetExtremes: function() {
                	showAggrText();
                },
            },
        },

        yAxis: defineYAxis(),

        legend: defineLegend(),

        plotOptions: {
            series: {
                events: {
                    legendItemClick: function(event) {
                        if (event.browserEvent.shiftKey) {
                            showDialogLine(this);
                            return false;
                        } else {
                            var visibility = this.visible ? 'visible' : 'hidden';
                            if (!this.visible) {
                                SetData(this);
                            }
                            if (this.visible) {
                                var attr = DP_attribute.findIndex(obj=>obj.id === this.options.id.toString());
                                if (attr != -1) {
                                    DP_attribute[attr].visible = (DP_Limit) ? 1 : 0;
                                    if (DP_attribute[attr].aggr === 'A3') {
                                        for (var i = chart.series.length - 1; i >= 0; i--) {
                                            if (this.options.id === chart.series[i].options.linkedTo && chart.series[i].options.name === 'MinMax') {
                                                chart.series[i].remove(false);
                                            }
                                        }
                                    }
                                }
                            } else {
                                var attr = DP_attribute.findIndex(obj=>obj.id === this.options.id.toString());
                                if (attr != -1)
                                    DP_attribute[attr].visible = 2;
                            }
                            return true;
                        }
                    },
                    show: function() {
                        loadNewAxisInfo();
                    },
                    hide: function() {
                    	loadNewAxisInfo();
                    },
                    click: function() {
                        showDialogLine(this);
                    },
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

}

function showAggrText() {
	var attr;
    var aggrType;
    if (DP_Navigator < 3) {
	    for (var serie = 0; serie < chart.series.length; serie++) {
	        if (chart.series[serie].visible && chart.series[serie].options.group != "nav") {
	            var grouping = chart.series[serie].currentDataGrouping;
	            if (grouping) {
	                var text = grouping.unitName;
	                if (ChhLanguage.default.highcharts['aggr' + text]) {
	                    text = ChhLanguage.default.highcharts['aggr' + text];
	                }
	                if (chart.series[serie].options.id) {
	                    attr = DP_attribute.findIndex(obj=>obj.id === chart.series[serie].options.id.toString());
	                    aggrType = 0;
	                    if (attr != -1) {
	                        aggrType = parseInt(DP_attribute[attr].aggr.substr(1, 2))
	                    }
	                }
	
	                if (aggrType === 1) {
	                    document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.historian.aggrtxt1 + ': ' + grouping.count + '/' + text;
	                } else if (aggrType === 2) {
	                    document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.historian.aggrtxt2 + ': ' + grouping.count + '/' + text;
	                } else if (aggrType === 3) {
	                    document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.historian.aggrtxt3 + ': ' + grouping.count + '/' + text;
	                } else if (aggrType === 4) {
	                    document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.historian.aggrtxt4 + ': ' + grouping.count + '/' + text;
	                } else if (aggrType === 5) {
	                    document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.historian.aggrtxt5 + ': ' + grouping.count + '/' + text;
	                } else if (aggrType === 6) {
	                    document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.historian.aggrtxt6;
	                } else if (aggrType === 7) {
	                    document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.historian.aggrtxt7 + ': ' + grouping.count + '/' + text;
	                } else {
	                    document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.historian.aggrtxt1 + ': ' + grouping.count + '/' + text;
	                }
	            } else {
	                document.getElementById('aggr_text').innerHTML = ' -  ' + ChhLanguage.default.historian.aggrtxt0;
	            }
	            break;
	        }
	    }
    } else {
        document.getElementById('aggr_text').innerHTML = '';
    }
}

function chartSetElements() {

    chart = $('#container').highcharts();

    // dark themes need black borders, update to like chart background
    if ((typeof chart.options.chart.backgroundColor) === 'string') {
        $('body').css('background-color', chart.options.chart.backgroundColor);
    } else if ((typeof chart.options.background2) === 'string') {
        $('body').css('background-color', chart.options.background2);
    } else if ((typeof chart.options.chart.borderColor) === 'string') {
        $('body').css('background-color', chart.options.chart.borderColor);
    }
    $('#message').css('color', chart.options.labels.style.color);

    $("#Select-Color").empty();
    // Color options
    var select = document.getElementById("Select-Color");
    for (i = 0; i < chart.options.colors.length; i++) {
        var option = document.createElement("option");
        option.text = 'Color ' + i;
        option.value = 'F' + i;
        option.style.backgroundColor = chart.options.colors[i];
        select.add(option);
    }

    $("#Select-Marker").empty();
    // Marker options
    var select = document.getElementById("Select-Marker");
    var option = document.createElement("option");
    option.text = 'none';
    option.value = 'M0';
    select.add(option);

    for (i = 0; i < chart.options.symbols.length; i++) {
        var option = document.createElement("option");
        option.text = chart.options.symbols[i];
        option.value = 'M' + (i + 1);
        select.add(option);
    }
    for (i = 0; i < chart.options.symbols.length; i++) {
        var option = document.createElement("option");
        option.text = chart.options.symbols[i] + '-RS';
        option.value = 'M' + (i + 1 + chart.options.symbols.length);
        select.add(option);
    }
    for (i = 0; i < chart.options.symbols.length; i++) {
        var option = document.createElement("option");
        option.text = chart.options.symbols[i] + '-RW';
        option.value = 'M' + (i + 1 + chart.options.symbols.length*2);
        select.add(option);
    }
    for (i = 0; i < chart.options.symbols.length; i++) {
        var option = document.createElement("option");
        option.text = chart.options.symbols[i] + '-FS';
        option.value = 'M' + (i + 1 + chart.options.symbols.length*3);
        select.add(option);
    }
    for (i = 0; i < chart.options.symbols.length; i++) {
        var option = document.createElement("option");
        option.text = chart.options.symbols[i] + '-FW';
        option.value = 'M' + (i + 1 + chart.options.symbols.length*4);
        select.add(option);
    }

    $("#Select-AxisColor").empty();
    // Color options
    var select = document.getElementById("Select-AxisColor");
    var option = document.createElement("option");
    option.text = 'Theme';
    option.value = '0';
    option.style.backgroundColor = Highcharts.defaultOptions.yAxis.lineColor;
    select.add(option);
    var option = document.createElement("option");
    option.text = '1.Serie';
    option.value = '1';
    option.style.backgroundColor = Highcharts.defaultOptions.yAxis.lineColor;
    select.add(option);
    for (i = 0; i < chart.options.colors.length; i++) {
        var option = document.createElement("option");
        option.text = 'Color ' + (i);
        option.value = (i + 2).toString();
        option.style.backgroundColor = chart.options.colors[i];
        select.add(option);
    }

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
        Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(86400000 * 1 * 365)));
        loadNewSerienData();
    });

    // **********************
    $('#button5').click(function() {
        Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(86400000 * 5 * 365)));
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

}

