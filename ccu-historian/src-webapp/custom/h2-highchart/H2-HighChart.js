/* *********************************
 * HighChart javascripts by wak 2019-2022
 ************************************/

// Version
var H2_version = 'v6.0';

/* define SLINT globals do avoid issues */
/* global ChhLanguage:false, DP_Themes:false */
/* eslint-env browser */

// Setup H2 Database Services, default set to same server as this webpage and port 8082
var H2_server = location.hostname;
var H2_port = (location.port === "") ? "80" : location.port;
var H2_refreshSec = 60;
// Refresh Time is enabled

// declare global Variables
var cntrlIsPressed = false;
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
var DP_Theme = 'standard';
var DP_FontSize = 14;
var DP_FontSizes = [8,10,12,14,16,18,20,24,30,40,50,60,70,80,100];
var DP_Theme_Setting;
var DP_DashType = ['Solid', 'Dash', 'DashDot', 'Dot', 'LongDash', 'LongDashDot', 'LongDashDotDot', 'ShortDash', 'ShortDashDot', 'ShortDashDotDot', 'ShortDot'];
var DP_Queue = [];
var DP_ColorNext = 0;
var DP_Title = '';
var DP_Subtitle = '';
var DP_Loading = 0;
var DP_Button_Jump = false;

var DP_yAxis = [{
  position: false,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText0,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 1,
  min: 10,
  max: 30,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText1,
  color: 1,
  type: 0
}, {
  position: true,
  limit: 1,
  min: -20,
  max: 50,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText2,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 1,
  min: 20,
  max: 90,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText3,
  color: 1,
  type: 0
}, {
  position: true,
  limit: 1,
  min: 0,
  max: 100,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText4,
  color: 1,
  type: 0
}, {
  position: true,
  limit: 1,
  min: 0,
  max: 2,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText5,
  color: 1,
  type: 0
}, {
  position: true,
  limit: 1,
  min: 20,
  max: 100,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText6,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 1,
  min: 900,
  max: 1000,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText7,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 1,
  min: 0,
  max: 5000,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText8,
  color: 1,
  type: 0
}, {
  position: true,
  limit: 1,
  min: 300,
  max: 3000,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText9,
  color: 1,
  type: 0
}, {
  position: true,
  limit: 1,
  min: 3,
  max: 15,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText10,
  color: 1,
  type: 0
}, {
  position: true,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText11,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText12,
  color: 1,
  type: 1
}, {
  position: false,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText13,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText14,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText15,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText16,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText17,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText18,
  color: 1,
  type: 0
}, {
  position: false,
  limit: 0,
  min: 0,
  max: 0,
  tick: 11,
  text: window.ChhLanguage.default.historian.yaxisText19,
  color: 1,
  type: 0
}];
var DP_yAxis_default = JSON.parse(JSON.stringify(DP_yAxis));

function createChart() {
  // Check DARK Mode
  let l_theme = DP_Theme;
  if (l_theme === 'standard') {
     if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
       l_theme = 'standard-dark';
     } else {
       l_theme = 'standard-light';
     }
  }
  if (l_theme !== 'standard-light' && window.DP_Themes[l_theme] && window.DP_Themes['standard-light']) {
    DP_Theme_Setting = window.Highcharts.merge(window.DP_Themes['standard-light'], window.DP_Themes[l_theme]);
  } else {
    DP_Theme_Setting = window.DP_Themes['standard-light'];
  }

  chartSetFontSize();

  window.Highcharts.setOptions(DP_Theme_Setting);

  chartSetOptions();

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
    shortname: '',
    buffer_data: {
      timestamps: [],
      values: [],
      buffer_start: 0,
      buffer_end: 0
    },
  };
  if (colorNr === -1) {
    attr.color = 'F' + (DP_ColorNext % chart.options.colors.length);
    DP_ColorNext++;
  } else {
    attr.color = 'F' + (colorNr % chart.options.colors.length);
  }

  if (DP !== -1) {
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
      case "SET_POINT_TEMPERATURE":
        attr.yaxis = 'Y1';
        attr.line = 'L2';
        break;
      case "MEAN5MINUTES":
        attr.yaxis = 'Y3';
        break;
      case "BRIGHTNESS":
        attr.yaxis = 'Y8';
        break;
      case "LEVEL":
        attr.yaxis = 'Y4';
        attr.line = 'L2';
        attr.unit = '';
        break;
      case "STATE":
        attr.yaxis = 'Y5';
        attr.line = 'L2';
        break;
      case "PRESS_SHORT":
      case "PRESS_LONG":
      case "PRESS_OPEN":
      case "MOTION":
        attr.yaxis = 'Y5';
        attr.mark = 'M1';
        attr.factor = 5;
        attr.line = 'L5';
        break;
      case "VALVE_STATE":
        attr.yaxis = 'Y4';
        attr.line = 'L2';
        attr.unit = '%';
        break;
    }

    if (DP.attributes.type === "BOOL") {
      attr.yaxis = 'Y5';
      attr.line = 'L2';
    }
    if (DP.attributes.unit === "%") {
      attr.yaxis = 'Y4';
      attr.line = 'L2';
      attr.unit = '%';
    }
    if (DP.id.interfaceId === "SysVar" && DP.attributes.unit === "°C") {
      attr.yaxis = 'Y1';
      attr.line = 'L0';
    }

    // add default from database
    if (DP.attributes.custom && DP.attributes.custom.HighChart) {

      var text2 = DP.attributes.custom.HighChart.split('|');
      if (text2.length > 0) {
        for (let l_text of text2) {
          if (l_text.substr(0, 1) === 'A') {
            attr.aggr = l_text;
          } else if (l_text.substr(0, 1) === 'Y') {
            attr.yaxis = l_text;
          } else if (l_text.substr(0, 1) === 'T') {
            attr.atime = l_text;
          } else if (l_text.substr(0, 1) === 'F') {
            attr.color = l_text;
          } else if (l_text.substr(0, 1) === 'C') {
            attr.comp = l_text;
          } else if (l_text.substr(0, 1) === 'L') {
            attr.line = l_text;
          } else if (l_text.substr(0, 1) === 'M') {
            attr.mark = l_text;
          } else if (l_text.substr(0, 1) === 'D') {
            attr.dash = l_text;
          } else if (l_text.substr(0, 1) === 'W') {
            attr.width = l_text;
          } else if (l_text.substr(0, 1) === 'V') {
            attr.visible = parseInt(l_text.substr(1, 1));
          } else if (l_text.substr(0, 1) === 'S') {
            attr.stack = parseInt(l_text.substr(1, 2));
          } else if (l_text.substr(0, 1) === 'U') {
            try {
              attr.unit = decodeURIComponent(l_text.substr(1, 20));
            } catch (err) {
              attr.unit = l_text.substr(1, 20);
            }
          } else if (l_text.substr(0, 1) === 'N') {
            try {
              attr.shortname = decodeURIComponent(l_text.substr(1, 40));
            } catch (err) {
              attr.shortname = l_text.substr(1, 40);
            }
          } else if (l_text.substr(0, 1) === 'X') {
            attr.factor = parseFloat(l_text.substr(1, 10));
          } else if (l_text.substr(0, 1) === 'O') {
            attr.offset = parseFloat(l_text.substr(1, 10));
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
  var shortname;
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

  var attrIDX = (DP_type === '') ? DP.idx.toString() : (DP_type + '_' + DP.idx.toString());

  var attr = DP_attribute.findIndex(obj => obj.id === attrIDX);

  if (attr === -1) {
    DP_attribute.push(defaultAttrib(DP, -1, attrIDX));
    attr = DP_attribute.findIndex(obj => obj.id === attrIDX);
  }

  yAxis = parseInt(DP_attribute[attr].yaxis.substr(1, 2));
  color = chart.options.colors[parseInt(DP_attribute[attr].color.substr(1, 2))];
  aggrType = parseInt(DP_attribute[attr].aggr.substr(1, 2));
  aggrTime = parseInt(DP_attribute[attr].atime.substr(1, 2));
  lineType = parseInt(DP_attribute[attr].line.substr(1, 2));
  dp_vis = DP_attribute[attr].visible;
  unit = DP_attribute[attr].unit;
  shortname = DP_attribute[attr].shortname;

  stacking = DP_attribute[attr].stack;

  dashtype = DP_DashType[parseInt(DP_attribute[attr].dash.substr(1, 2))];

  linewidth = parseInt(DP_attribute[attr].width.substr(1, 2));

  marker = defineMarker(parseInt(DP_attribute[attr].mark.substr(1, 2)), color, linewidth);

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
    groupwidth = 50;
    groupUnits = null;

    // dyna. grouping start by 15 min.
  } else if (aggrTime === 1) {
    groupforced = true;
    groupwidth = 50;
    groupUnits = [['minute', [15, 30]], ['hour', [1, 2, 3, 4, 6, 8, 12]], ['day', [1]], ['week', [1]], ['month', [1, 3, 6]], ['year', [1]],];

    // dyna. only hours, days and month
  } else if (aggrTime === 2) {
    groupUnits = [['hour', [1]], ['day', [1]], ['month', [1]], ['year', [1]],];
    // fix hours
  } else if (aggrTime === 3) {
    groupUnits = [['hour', [1]],];
    groupforced = true;
  } else if (aggrTime === 4) {
    groupUnits = [['day', [1]],];
    groupforced = true;
  } else if (aggrTime === 5) {
    groupUnits = [['week', [1]],];
    groupforced = true;
  } else if (aggrTime === 6) {
    groupUnits = [['month', [1]],];
    groupforced = true;
  } else if (aggrTime === 7) {
    groupUnits = [['month', [3]],];
    groupforced = true;
  } else if (aggrTime === 8) {
    groupUnits = [['year', [1]],];
    groupforced = true;
  } else if (aggrTime === 9) {
    groupUnits = [['hour', [3]],];
    groupforced = true;
  } else if (aggrTime === 10) {
    groupUnits = [['hour', [6]],];
    groupforced = true;
  } else if (aggrTime === 11) {
    groupUnits = [['hour', [8]],];
    groupforced = true;
  } else if (aggrTime === 12) {
    groupUnits = [['hour', [12]],];
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
  } else if (aggrType === 2 || aggrType === 4 || aggrType === 5 || aggrType === 7) {
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
    type = (type === "line") ? "spline" : type;
  } else {
    grouping = {
      enabled: false,
    };
  }

  var pointFormater = null;
  var pointFormat = null;
  var serienName = '';

  pointFormater = function() { return toolTipInfo(this); };

  if (shortname !== '') {
    serienName = shortname;
  } else if (DP_type.substr(0, 1) === 'C') {
    serienName = (DP.id.interfaceId === "SysVar")
      ? (DP.attributes.displayName)
      : (DP.attributes.displayName + '.' + DP.id.identifier) + '(' + window.ChhLanguage.default.historian['comptype' + DP_type] + ')';
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
    stacking: (stacking === 0) ? null : 'normal',
    stack: (stacking === 0) ? null : ('group' + stacking),
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
  chart.addSeries(def_serie, false, false);

}

function defineDataLabels() {

  var objLabels;

  if (DP_Labels === 0) {
    objLabels = { enabled: false };
  } else if (DP_Labels === 1) {
    objLabels = {
      enabled: true,
      allowOverlap: true,
      color: null,
      style: {
        "color": null,
      },
      formatter: function() {
        let last;
        if (this.series.data.length > 0) {
         last = this.series.data[this.series.data.length - 1];
        } else if (this.series.points.length > 0) {
         last = this.series.points[this.series.points.length - 1];
        }
        if (last && last.category) {
          if (this.point.category === last.category) {
            return this.series.name;
          }
        }
        return "";
      }
    };

  } else if (DP_Labels === 2) {
    objLabels = {
      enabled: true,
      allowOverlap: false,
      color: null,
      style: {
        "color": null,
      },
      formatter: function() {
        return window.Highcharts.numberFormat(this.y, 1);
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
  var iRadius;
  if (iLineW < 3) {
    iRadius = 4;
  } else {
    iRadius = (4 + iLineW - 2);
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
  } else if (iMarker > chart.options.symbols.length && iMarker <= chart.options.symbols.length * 2) {
    objMarker = {
      enabled: true,
      symbol: chart.options.symbols[iMarker - 1 - chart.options.symbols.length],
      radius: iRadius,
      lineColor: 'black',
      lineWidth: 1,
      fillColor: strColor,
    };
  } else if (iMarker > chart.options.symbols.length * 2 && iMarker <= chart.options.symbols.length * 3) {
    objMarker = {
      enabled: true,
      symbol: chart.options.symbols[iMarker - 1 - chart.options.symbols.length * 2],
      radius: iRadius,
      lineColor: 'white',
      lineWidth: 1,
      fillColor: strColor,
    };
  } else if (iMarker > chart.options.symbols.length * 3 && iMarker <= chart.options.symbols.length * 4) {
    objMarker = {
      enabled: true,
      symbol: chart.options.symbols[iMarker - 1 - chart.options.symbols.length * 3],
      radius: iRadius,
      lineColor: strColor,
      lineWidth: 2,
      fillColor: 'black',
    };
  } else if (iMarker > chart.options.symbols.length * 4 && iMarker <= chart.options.symbols.length * 5) {
    objMarker = {
      enabled: true,
      symbol: chart.options.symbols[iMarker - 1 - chart.options.symbols.length * 4],
      radius: iRadius,
      lineColor: strColor,
      lineWidth: 2,
      fillColor: 'white',
    };
  }

  return objMarker;
}

function setData(objSerie) {

  var datStart = Zeitraum_Start.getTime();
  var datEnd = Zeitraum_Ende.getTime();
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

  var attr = DP_attribute.findIndex(obj => obj.id === attrIDX);
  if (attr !== -1) {

    if (DP_attribute[attr].comp !== 'C0') {

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
        setSerienData(attr, objSerie);
      } else if (DP_attribute[attr].buffer_data.buffer_start > datStart
        && DP_attribute[attr].buffer_data.buffer_start <= datEnd
        && DP_attribute[attr].buffer_data.buffer_end >= datEnd
        && DP_attribute[attr].buffer_data.values.length > 0) {
        // append to begin
        datEnd = DP_attribute[attr].buffer_data.buffer_start;
      } else if (DP_attribute[attr].buffer_data.buffer_start <= datStart
        && DP_attribute[attr].buffer_data.buffer_end >= datStart
        && DP_attribute[attr].buffer_data.buffer_end < datEnd
        && DP_attribute[attr].buffer_data.values.length > 0) {
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
function bufferSerienData(id, data) {

  if (!id) {
    console.log('ID missing');
    return;
  }

  // find queue entry
  var q_i = DP_Queue.findIndex(obj => obj[0] === id);

  var attrIDX = DP_Queue[q_i][3];

  if (data.values.length > 0) {

    // correct values to round -3
    for (var i = 0; i < data.values.length; i++) {
      data.values[i] = Math.round(data.values[i] * 1000) / 1000;
    }

    if (DP_attribute[attrIDX].buffer_data.buffer_start >= DP_Queue[q_i][4]
      && DP_attribute[attrIDX].buffer_data.buffer_start === DP_Queue[q_i][5]
      && DP_attribute[attrIDX].buffer_data.values.length > 0) {

      DP_attribute[attrIDX].buffer_data.buffer_start = DP_Queue[q_i][4];
      DP_attribute[attrIDX].buffer_data.timestamps = data.timestamps.concat(DP_attribute[attrIDX].buffer_data.timestamps);
      DP_attribute[attrIDX].buffer_data.values = data.values.concat(DP_attribute[attrIDX].buffer_data.values);

    } else if (DP_attribute[attrIDX].buffer_data.buffer_end <= DP_Queue[q_i][5]
      && DP_attribute[attrIDX].buffer_data.buffer_end === DP_Queue[q_i][4]
      && DP_attribute[attrIDX].buffer_data.values.length > 0) {

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

  setSerienData(attrIDX, serie);

  loadingInfo();
}

function setSerienData(p_attr, serieObj) {

  let aggrType = parseInt(DP_attribute[p_attr].aggr.substr(1, 2));
  let compType = DP_attribute[p_attr].comp;

  let datStart = Zeitraum_Start.getTime();
  let datEnd = Zeitraum_Ende.getTime();

  let arr = [];
  let backSec = 0;

  if (typeof serieObj.options === 'undefined') {
    return;
  }
  // Min/Max not needed
  if (serieObj.options.name === 'MinMax') {
    return;
  }

  if (serieObj.options.id.toString().substr(0, 1) === 'C') {

    // Set backtime
    backSec = getComparisionBackDay(compType);

    datStart += backSec;
    datEnd += backSec;

    let attr2 = DP_attribute.findIndex(obj => obj.id === serieObj.options.id.toString());
    if (attr2) {
      aggrType = parseInt(DP_attribute[attr2].aggr.substr(1, 2));
    }
  }

    // collect all timesstamps and Values - no aggregation
  if (aggrType === 0) {

    arr = setSerienDataAggr0(p_attr, datStart, datEnd, backSec);

    // Delta +/-
  } else if (aggrType === 2) {

    arr = setSerienDataAggr2(p_attr, datStart, datEnd, backSec);

    // collect all timesstamps and Values for TIME_ON Aggregation
  } else if (aggrType === 5) {

    arr = setSerienDataAggr5(p_attr, datStart, datEnd, backSec);

    // no aggregation but rounded to min, better for mouse over sync to other lines
  } else if (aggrType === 6) {

    arr = setSerienDataAggr6(p_attr, datStart, datEnd, backSec);

    // Delta +
  } else if (aggrType === 7) {

    arr = setSerienDataAggr7(p_attr, datStart, datEnd, backSec);

    // default aggregation, repeat it to timeslots at least all 10 min
  } else {

    arr = setSerienDataAggr1(p_attr, datStart, datEnd, backSec);

  }

  if (arr.length > 0) {

    // Here Data are ready to set for Serie
    serieObj.setData(arr, true, false, false);

    // prepare and show min/max series
    if (aggrType === 3) {
      addAggregationMinMax(serieObj);
    }

    // update colors on txt
    loadNewAxisInfo();
    // Update Aggregation Text
    showAggrText();
  }

  // read data for comp series
  if (DP_attribute[p_attr].comp !== 'C0' && (serieObj.options.id.toString().substr(0, 1) !== 'C')) {

    let sobj = chart.get(DP_attribute[p_attr].comp + '_' + DP_attribute[p_attr].id);
    let attrC = DP_attribute.findIndex(obj => obj.id === DP_attribute[p_attr].comp + '_' + DP_attribute[p_attr].id);
    if (sobj && attrC !== -1 && DP_attribute[attrC].comp === 'C0' && DP_attribute[attrC].visible === 2) {
      setSerienData(p_attr, sobj);
    }
  }
}

// collect all timesstamps and Values
function setSerienDataAggr0(p_attr, datStart, datEnd, backSec) {

  let arr = [];

  // Define BufferLink
  let buffer = DP_attribute[p_attr].buffer_data;

  // get start and end position over binary search
  let arrStart;
  arrStart = sortedIndex(buffer.timestamps, datStart);
  let arrEnd;
  arrEnd = sortedIndex(buffer.timestamps, datEnd);

  if (datStart <= buffer.timestamps[arrEnd]) {
    if (arrStart > 0 && datStart !== buffer.timestamps[arrStart]) {
      arr.push([datStart - backSec, (buffer.values[arrStart - 1] * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
    }
    for (let i = arrStart; i <= arrEnd; i++) {
      arr.push([buffer.timestamps[i] - backSec, (buffer.values[i] * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
    }
  }

  return arr;
}

// default aggregation, repeat it to timeslots at least all 10 min
function setSerienDataAggr1(p_attr, datStart, datEnd, backSec) {

  let arr = [];

  // Define BufferLink
  let buffer = DP_attribute[p_attr].buffer_data;

  // get start and end position over binary search
  let arrStart;
  arrStart = sortedIndex(buffer.timestamps, datStart);
  let arrEnd;
  arrEnd = sortedIndex(buffer.timestamps, datEnd);

  if (arrStart < buffer.timestamps.length) {
    let last_value = buffer.values[arrStart];
    let last_time = buffer.timestamps[arrStart];

    for (let i = arrStart; i <= arrEnd; i++) {

      // fill long empty periods with last_value, that aggregation works
      if ((buffer.timestamps[i] - last_time) > 600000) {
        last_time = Math.round((last_time + 600000) / 60000) * 60000;
        for (let t = last_time; t < buffer.timestamps[i] - 300000; t = t + 600000) {
          arr.push([t - backSec, last_value]);
        }
      }

      last_value = (buffer.values[i] * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset;
      last_time = buffer.timestamps[i];

      arr.push([buffer.timestamps[i] - backSec, last_value]);
    }
  }

  return arr;
}

// Delta +/-
function setSerienDataAggr2(p_attr, datStart, datEnd, backSec) {

  let arr = [];

  // Define BufferLink
  let buffer = DP_attribute[p_attr].buffer_data;

  // get start and end position over binary search
  let arrStart;
  arrStart = sortedIndex(buffer.timestamps, datStart);
  let arrEnd;
  arrEnd = sortedIndex(buffer.timestamps, datEnd);

  // only if values found
  if (arrStart < buffer.timestamps.length) {
    let last_value = buffer.values[arrStart];
    let last_time = buffer.timestamps[arrStart];

    for (let i = arrStart + 1; i <= arrEnd; i++) {

      if (buffer.timestamps[i] >= datStart && buffer.timestamps[i] <= datEnd) {
        // fill missing times with delta 0 every 10 min.
        if ((buffer.timestamps[i] - last_time) > 600000) {
          last_time = Math.round((last_time + 600000) / 60000) * 60000;
          for (let t = last_time; t < buffer.timestamps[i]; t = t + 600000) {
            arr.push([t, 0]);
          }
        }

        arr.push([buffer.timestamps[i] - backSec, ((buffer.values[i] - last_value) * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);

        last_value = buffer.values[i];
        last_time = buffer.timestamps[i];
      }
    }
  }

  return arr;
}

// collect all timesstamps and Values for TIME_ON Aggregation
function setSerienDataAggr5(p_attr, datStart, datEnd, backSec) {

  let arr = [];
  let t = 0;

  // Define BufferLink
  let buffer = DP_attribute[p_attr].buffer_data;

  // get start and end position over binary search
  let arrStart;
  arrStart = sortedIndex(buffer.timestamps, datStart);
  let arrEnd;
  arrEnd = sortedIndex(buffer.timestamps, datEnd);

  // only if values found
  if (arrStart < buffer.timestamps.length) {

    let last_value = (buffer.values[arrStart] > 0) ? 1 : 0;
    let last_time = buffer.timestamps[arrStart];

    for (let i = arrStart + 1; i <= arrEnd; i++) {
      if (last_value > 0 && buffer.values[i] === 0) {

        last_value = buffer.timestamps[i] - last_time;
        t = last_time;
        // fill every minute with 1 as run time
        if (last_value > 60000) {
          for (t = last_time; t < buffer.timestamps[i] - 60000; t = t + 60000) {
            arr.push([t - backSec, (1 * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
            last_value -= 60000;
          }
        }
        if (last_value > 0) {
          arr.push([t - 1 - backSec, (Math.round(last_value / 60) / 1000 * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
        }
        last_value = 0;
        last_time = buffer.timestamps[i];

      } else if (last_value === 0 && buffer.values[i] > 0) {

        last_value = buffer.timestamps[i] - last_time;
        // fill every minute with 1 as run time
        if (last_value > 60000) {
          for (t = last_time; t < buffer.timestamps[i] - 60000; t = t + 60000) {
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
        for (t = last_time; t < datEnd - 60000; t = t + 60000) {
          arr.push([t - backSec, (1 * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
          last_value -= 60000;
        }
      }
      if (last_value > 0) {
        arr.push([datEnd - backSec, (Math.round(last_value / 60) / 1000 * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
      }
    }
  }
  return arr;
}

// no aggregation but rounded to min, better for mouse over sync to other lines
function setSerienDataAggr6(p_attr, datStart, datEnd, backSec) {

  let arr = [];

  // Define BufferLink
  let buffer = DP_attribute[p_attr].buffer_data;

  // get start and end position over binary search
  let arrStart;
  arrStart = sortedIndex(buffer.timestamps, datStart);
  let arrEnd;
  arrEnd = sortedIndex(buffer.timestamps, datEnd);

  if (datStart <= buffer.timestamps[arrEnd]) {

    for (let i = arrStart; i <= arrEnd; i++) {
  
      let timestamprounded = Math.round((buffer.timestamps[i] - backSec) / 60000) * 60000;
      arr.push([timestamprounded, (buffer.values[i] * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset]);
  
    }
  }

  return arr;
}

// Delta +
function setSerienDataAggr7(p_attr, datStart, datEnd, backSec) {

  var arr = [];

  // Define BufferLink
  let buffer = DP_attribute[p_attr].buffer_data;

  // get start and end position over binary search
  let arrStart;
  arrStart = sortedIndex(buffer.timestamps, datStart);
  let arrEnd;
  arrEnd = sortedIndex(buffer.timestamps, datEnd);

  // only if values found
  if (arrStart < buffer.timestamps.length) {
    let last_value = buffer.values[arrStart];
    let last_time = buffer.timestamps[arrStart];

    for (let i = arrStart + 1; i <= arrEnd; i++) {

      if (buffer.timestamps[i] >= datStart && buffer.timestamps[i] <= datEnd) {
        // fill missing times with delta 0 every 10 min.
        if ((buffer.timestamps[i] - last_time) > 600000) {
          last_time = Math.round((last_time + 600000) / 60000) * 60000;
          for (let t = last_time; t < buffer.timestamps[i]; t = t + 600000) {
            arr.push([t, 0]);
          }
        }

        // only + values, - are ignored
        let delta_val = ((buffer.values[i] - last_value) * DP_attribute[p_attr].factor) + DP_attribute[p_attr].offset;
        if (delta_val >= 0) {
          arr.push([buffer.timestamps[i] - backSec, delta_val]);
        }

        last_value = buffer.values[i];
        last_time = buffer.timestamps[i];
      }
    }
  }
  return arr;
}


// Find next timestamp in array by binary search
function sortedIndex(array, value) {
  let low = 0, high = array.length - 1, mid;
  if (array[low] >= value) {
    return 0;
  }
  if (array[high] <= value) {
    return high;
  }
  while (low < high) {
    mid = Math.floor((low + high) / 2);
    if (array[mid] < value) {
      low = mid + 1;
    } else {
      high = mid;
    }
  }
  if (low > array.length - 1) {
    low = array.length - 1;
  }
  return low;
}

/**
*  read timeSerien data for H2 database
*/
function getDataH2(p_series, p_attrID, p_attr, datStart, datEnd) {
  // Refresh for Min/Max Aggregation done directly
  if (p_series.options.name === 'MinMax') {
    return;
  }

  var key = p_attrID + '_' + Date.now();
  var p_id = p_series.options.id.toString();

  // refresh for comparisation done over real ID
  if (p_series.options.id.toString().substr(0, 1) === 'C') {
    p_id = p_id.split('_')[1];
  }

  // save request to queue
  DP_Queue.push([key, p_attrID, p_series, p_attr, datStart, datEnd]);

  // display loading info
  loadingInfo();

  // get serien data from H2 database
  var url = 'http://' + H2_server + ':' + H2_port;
  url += '/query/jsonrpc.gy';
  url += (DP_ApiKey === "") ? "" : "?" + DP_ApiKey;

  var postData = {
    id: key,
    method: 'getTimeSeriesRaw',
    params: [p_id, datStart, datEnd]
  };

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
      ajaxErrorOutput(xhr, status, error);
    },
    success: function(result) {
      if (!result.result) {
        console.log(result);
      } else if (result.result) {
        bufferSerienData(result.id, result.result);
      }
    }
  });
}

/**
* Request default settings from the server and check local storage
*/
function requestInitData() {

  if (DP_Navigator < 3) {
    document.getElementById("count_val").innerHTML = "0";
    document.getElementById('count_text').innerHTML = window.ChhLanguage.default.historian.labelValues;
  } else {
    document.getElementById("count_val").innerHTML = "";
    document.getElementById('count_text').innerHTML = "";
  }

// set default global chart object
  chart = $('#container').highcharts();

  // get LocalData DataPoints
  var loc_dataPoints = getLocalData('DataPoints');
  if (loc_dataPoints) {

    // speed up with local data and read actual one later

    try {
      DP_point = JSON.parse(loc_dataPoints);
    } catch {
      DP_point = {};
    }
    if (DP_point[1] && DP_point[1].attributes) {
      parseDataPoints();

      // read datapoints only if old
      var loc_datatime = getLocalData('DataPointsTime');
      if (loc_datatime === null || parseInt(loc_datatime) + 3600000 <= Date.now()) {
        // actual data will be read in 2 sec.
        setTimeout(requestData, 2000);
      }
    } else {
      requestData();
    }
  } else {
    requestData();
  }

  // show HighChart Stock Tools hidden
  if (chart.stockTools) {
    chart.stockTools.showhideBtn.click();
  }
}

/**
* Request data from the server, add it to the graph and set a timeout
* to request again
*/
function requestData() {

  var url = 'http://' + H2_server + ':' + H2_port;
  url += '/query/jsonrpc.gy';
  url += (DP_ApiKey === "") ? "" : "?" + DP_ApiKey;

  var postData = {
    id: 'DP',
    method: 'getDataPoint',
    params: []
  };

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
      ajaxErrorOutput(xhr, status, error);
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
  url += (DP_ApiKey === "") ? "" : "?" + DP_ApiKey;

  var postData = {
    id: 'Setup',
    method: 'getConfig',
    params: ['HighChart']
  };

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
      ajaxErrorOutput(xhr, status, error);
    },
    success: function(result) {
      let strSetNew;
      // Get Settings from H2 database as String
      if (result.result) {
        try {
          strSetNew = result.result.replace(/'/g, '"');

          if (strSetNew && strSetNew.substring(0, 2) === '{"') {
            DP_settings = JSON.parse(strSetNew);
            DP_settings_old = JSON.parse(strSetNew);
          } else {
            DP_settings = { 'Setting': '' };
            strSetNew = JSON.stringify(DP_settings);
          }
        }
        catch (e) {
          DP_settings = { 'Setting': '' };
          strSetNew = JSON.stringify(DP_settings);
        }
      } else {
        DP_settings = { 'Setting': '' };
        strSetNew = JSON.stringify(DP_settings);
      }
      if (strSetNew !== getLocalData('setting')) {
        // save LocalData Settings
        setLocalData('setting', strSetNew);

        parseSetting();

        readLinkData();
      }
      setLocalData('settingTime', Date.now());
    },
  });
}

function parseSetting() {

  // read default from YAXIS
  // take default values from database
  var x;
  var text2;
  var k;

  if (DP_settings) {
    for (x = 0; x < DP_yAxis.length; x++) {
      if (DP_settings['YAXIS' + x]) {
        text2 = DP_settings['YAXIS' + x].split('|');
        var axis_id = x;
        for (k = 0; k < text2.length; k++) {
          if (text2[k].substr(0, 1) === 'P') {
            if (text2[k].substr(1, 1) === '0') {
              DP_yAxis[axis_id].position = false;
            }
            if (text2[k].substr(1, 1) === '1') {
              DP_yAxis[axis_id].position = true;
            }
          } else if (text2[k].substr(0, 1) === 'C') {
            if (text2[k].substr(1, 1) === '0') {
              DP_yAxis[axis_id].type = 0;
            }
            if (text2[k].substr(1, 1) === '1') {
              DP_yAxis[axis_id].type = 1;
            }
          } else if (text2[k].substr(0, 1) === 'A') {
            if (text2[k].substr(1, 1) >= '0' && text2[k].substr(1, 1) <= '2') {
              DP_yAxis[axis_id].limit = parseInt(text2[k].substr(1, 1));
            }
          } else if (text2[k].substr(0, 1) === 'L') {
            DP_yAxis[axis_id].min = parseFloat(text2[k].substr(1, 15));
          } else if (text2[k].substr(0, 1) === 'H') {
            DP_yAxis[axis_id].max = parseFloat(text2[k].substr(1, 15));
          } else if (text2[k].substr(0, 1) === 'G') {
            DP_yAxis[axis_id].tick = parseInt(text2[k].substr(1, 15));
          } else if (text2[k].substr(0, 1) === 'F') {
            DP_yAxis[axis_id].color = parseInt(text2[k].substr(1, 2));
          } else if (text2[k].substr(0, 1) === 'T') {
            try {
              DP_yAxis[axis_id].text = decodeURIComponent(text2[k].substr(1, 50));
            } catch {
              DP_yAxis[axis_id].text = text2[k].substr(1, 50);
            }
          }
        }
      }
    }
    // Read default Settings
    if (DP_settings['Setting']) {
      text2 = DP_settings['Setting'].split('|');
      for (k = 0; k < text2.length; k++) {
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
          DP_Theme = text2[k].substr(1, 30);
          if (DP_Theme === 'standard_groß') {    // check old version
            DP_Theme = 'standard-light';
            DP_FontSize = 20;
          } else if (DP_Theme === 'standard_groesser') {     // check old version
            DP_Theme = 'standard-light';
            DP_FontSize = 30;
          }
        } else if (text2[k].substr(0, 1) === 'O') {
          DP_FontSize = parseInt(text2[k].substr(1, 2));
        } else if (text2[k].substr(0, 1) === 'R') {
          H2_refreshSec = parseInt(text2[k].substr(1, 2));
        } else if (text2[k].substr(0, 1) === 'T') {
          try {
            DP_Title = decodeURIComponent(text2[k].substr(1, 50));
          } catch {
            DP_Title = text2[k].substr(1, 50);
          }
        } else if (text2[k].substr(0, 1) === 'S') {
          try {
            DP_Subtitle = decodeURIComponent(text2[k].substr(1, 60));
          } catch {
            DP_Subtitle = text2[k].substr(1, 60);
          }
        }
      }
    }
  }
}

function readLinkData() {
  // check parameter from get-link
  if (location.search) {
    var parts = decodeURIComponent(location.search.substring(1)).split('&');
    for (let l_part of parts) {
      var nv = l_part.split('=');
      if (!nv[0]) {
        continue;
      }

      if (nv[0].toLowerCase() === 'dp') {
        DP_Limit = true;
        // parameter Periode (Stunden)
      } else if ((nv[0].toLowerCase() === 'periode') || (nv[0].toLowerCase() === 'period')) {
        Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(3600 * 1000 * parseInt(nv[1]))));
        // parameter Data Point
      } else if (nv[0].toLowerCase() === 'setting') {
        readLinkDataSetting(nv[1]);
      } else if (nv[0].toLowerCase() === 'filterkey') {
        filter_feld = decodeURIComponent(nv[1].toLowerCase());
      } else if (nv[0].toLowerCase() === 'title') {
        DP_Title = decodeURIComponent(nv[1]);
        DP_Title = DP_Title.replaceAll('§', '%').replaceAll('µ', '&');
      } else if (nv[0].toLowerCase() === 'subtitle') {
        DP_Subtitle = decodeURIComponent(nv[1]);
        DP_Subtitle = DP_Subtitle.replaceAll('§', '%').replaceAll('µ', '&');
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
        if (decodeURIComponent(nv[1].toLowerCase()) >= '0' && decodeURIComponent(nv[1].toLowerCase()) <= '4') {
          DP_Navigator = parseInt(decodeURIComponent(nv[1]));
        }
      } else if (nv[0].toLowerCase() === 'theme') {
        DP_Theme = decodeURIComponent(nv[1].toLowerCase());
        if (DP_Theme === 'standard_groß') {      // check old version
          DP_Theme = 'standard-light';
          DP_FontSize = 20;
        } else if (DP_Theme === 'standard_groesser') {      // check old version
          DP_Theme = 'standard-light';
          DP_FontSize = 30;
        }
      } else if (nv[0].toLowerCase() === 'fontsize') {
        DP_FontSize = parseInt(decodeURIComponent(nv[1]));
      } else if (nv[0].toLowerCase() === 'dpfilter') {
        DP_DataPointFilter = parseInt(decodeURIComponent(nv[1]));
      } else if (nv[0].toLowerCase() === 'labels') {
        if (decodeURIComponent(nv[1].toLowerCase()) === 'true') {
          DP_Labels = 1;
        }
        if (decodeURIComponent(nv[1].toLowerCase()) >= '0' && decodeURIComponent(nv[1].toLowerCase()) <= '2') {
          DP_Labels = parseInt(decodeURIComponent(nv[1]));
        }
      } else if (nv[0].toLowerCase() === 'daylight') {
        if (decodeURIComponent(nv[1].toLowerCase()) === 'false') {
          DP_DayLight = 0;
        }
        if (decodeURIComponent(nv[1].toLowerCase()) >= '0' && decodeURIComponent(nv[1].toLowerCase()) <= '3') {
          DP_DayLight = parseInt(decodeURIComponent(nv[1]));
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
    setTimeout(autoRefresh, 1000);
  }

  createChart();

}

function readLinkDataSetting(text) {
  if (text) {
    let text2 = text.split('|');
    for (let setting of text2) {
      switch (setting.substr(0, 1)) {
      case 'L':
        DP_Legend = parseInt(setting.substr(1, 2));
        break;
      case 'N':
        DP_Navigator = parseInt(setting.substr(1, 2));
        break;
      case 'P':
        DP_Labels = parseInt(setting.substr(1, 2));
        break;
      case 'D':
        DP_DayLight = parseInt(setting.substr(1, 2));
        break;
      case 'G':
        DP_Grid = parseInt(setting.substr(1, 2));
        break;
      case 'F':
        DP_ShowFilter = parseInt(setting.substr(1, 2));
        break;
      case 'I':
        DP_DataPointFilter = parseInt(setting.substr(1, 2));
        break;
      case 'B':
        DP_Theme = setting.substr(1, 30);
        if (DP_Theme === 'standard_groß') {    // check old version
          DP_Theme = 'standard-light';
          DP_FontSize = 20;
        } else if (DP_Theme === 'standard_groesser') {     // check old version
          DP_Theme = 'standard-light';
          DP_FontSize = 30;
        }
        break;
      case 'O':
        DP_FontSize = parseInt(setting.substr(1, 2));
        break;
      case 'R':
        H2_refreshSec = parseInt(setting.substr(1, 2));
        break;
      case 'T':
        try {
          DP_Title = decodeURIComponent(setting.substr(1, 50));
        } catch {
          DP_Title = setting.substr(1, 50);
        }
        break;
      case 'S':
        try {
          DP_Subtitle = decodeURIComponent(setting.substr(1, 60));
        } catch {
          DP_Subtitle = setting.substr(1, 60);
        }
        break;
      }
    }
  }
}

/**
* Request data from the server, add it to the graph and set a timeout
* to request again
*/
function requestData2(TXT_JSON) {

  if (!TXT_JSON.result) {
    return;
  }

  var DP_point_loc = [];
  for (let dppoint of TXT_JSON.result) {
    DP_point_loc.push(dppoint);
  }

  // Sort data points on DisplayName
  DP_point_loc.sort(function(a, b) {
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

  if (JSON.stringify(DP_point_loc) !== getLocalData('DataPoints')) {
    // save LocalData DataPoints
    setLocalData('DataPoints', JSON.stringify(DP_point_loc));

    DP_point = DP_point_loc;

    parseDataPoints();
  }
  setLocalData('DataPointsTime', Date.now());

}


function parseDataPoints() {
  var i;
  var text = '';
  DP_attribute = [];


  // Alle Serien aufbauen und Räume & Gewerke sammeln nur für anzeigbare
  parseDataPointFill();

  var nv;
  var parts;
  var select;
  // check parameter from get-link
  if (location.search) {
    parts = decodeURIComponent(location.search.substring(1)).split('&');
    for (i = 0; i < parts.length; i++) {
      nv = parts[i].split('=');
      if (!nv[0]) {
        continue;
      }
      // nur noch DP Werte
      if (nv[0].toLowerCase() === 'dp') {
        text = decodeURIComponent(nv[1]).toLowerCase().split(',');
        parseDataPointsDP(text,nv);

        // parameter YAXIS
      } else if (nv[0].toLowerCase() === 'yaxis') {
        text = decodeURIComponent(nv[1]).toLowerCase().split(',');
        parseDataPointsAxis(text,nv);

        // parameter Raum
      } else if (nv[0].toLowerCase() === 'room') {
        var DP_start_room = decodeURIComponent(nv[1].toLowerCase());
        select = document.getElementById("Select-Raum");
        for (let l_opt of select.options) {
          if (l_opt.label.toLowerCase() === DP_start_room.toLowerCase() || l_opt.value.toLowerCase() === DP_start_room.toLowerCase()) {
            select.value = l_opt.value;
            break;
          }
        }
        // parameter Gewerk
      } else if (nv[0].toLowerCase() === 'function') {
        var DP_start_func = decodeURIComponent(nv[1].toLowerCase());
        select = document.getElementById("Select-Gewerk");
        for (let l_opt of select.options) {
          if (l_opt.label.toLowerCase() === DP_start_func.toLowerCase() || l_opt.value.toLowerCase() === DP_start_func.toLowerCase()) {
            select.value = l_opt.value;
            break;
          }
        }

        // FilterLine
      } else if (nv[0].toLowerCase() === 'filterline') {
        if (decodeURIComponent(nv[1].toLowerCase()) === 'false' || decodeURIComponent(nv[1].toLowerCase()) === '0') {
          DP_ShowFilter = 0;
        }
        // only filterline both
        if (decodeURIComponent(nv[1].toLowerCase()) === '1') {
          DP_ShowFilter = 1;
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
  chart.update({
    yAxis: defineYAxis(),
    legend: defineLegend()
  }, false, false);

  // Yaxis options
  $("#Select-Yaxis").empty();
  select = document.getElementById("Select-Yaxis");
  for (i = 0; i < DP_yAxis.length; i++) {
    let option = document.createElement("option");
    if (DP_yAxis[i].text !== "" && DP_yAxis[i].text !== null) {
      option.text = DP_yAxis[i].text;
    } else {
      option.text = window.ChhLanguage.default.historian['yaxis' + i];
    }
    option.value = 'Y' + i;
    select.add(option);
  }

  // show menü & filter if wanted
  showFilterLine();

  // Display data
  changeEventRaumFilter();

  // check parameter Zoom from get-link
  parseDataPointsZoom();

}

function parseDataPointFill() {
  var DP_rooms = [];
  var DP_gewerk = [];
  var t;
  var text2;
  var attr;

  // reset DP attributes
  DP_attribute = [];
  DP_ColorNext = 0;

  // Alle Serien aufbauen und Räume & Gewerke sammeln nur für anzeigbare
  for (let dp of DP_point) {

// nur valide DPs
   if (checkFilter("ALLES", "ALLES", "", null, dp)) {

      // Räme sammeln
      if (dp.attributes.room !== null) {
        t = dp.attributes.room.split(',');
        for (let c of t) {
          if (c !== '') {
            if (DP_rooms.indexOf(c.trim()) === -1) {
              DP_rooms.push(c.trim());
            }
          }
        }
      }

      // Gewerke sammeln
      if (dp.attributes.function !== null) {
        t = dp.attributes.function.split(',');
        for (let c of t) {
          if (c !== '') {
            if (DP_gewerk.indexOf(c.trim()) === -1) {
              DP_gewerk.push(c.trim());
            }
          }
        }
      }

      // take default values from database
      if (dp.attributes.custom && dp.attributes.custom.HighChart) {
        text2 = dp.attributes.custom.HighChart.split('|');
        if (text2.length > 0) {
          attr = defaultAttrib(dp, -1, dp.idx.toString());
          DP_attribute.push(attr);
        }
      }
    }
  }

  // Sort on Rooms
  DP_rooms.sort( sortLowercase );

  $("#Select-Raum").empty();
  var select = document.getElementById("Select-Raum");

  // add default all and sysvar
  select.options[select.options.length] = new Option(window.ChhLanguage.default.historian.roomALL, 'ALLES');
  select.options[select.options.length] = new Option(window.ChhLanguage.default.historian.sysvarALL, 'SYSVAR');
  for (let c of DP_rooms) {
    text2 = c;
    if (window.ChhLanguage.default.historian[c]) {
      text2 = window.ChhLanguage.default.historian[c];
    }
    select.options[select.options.length] = new Option(text2, c);
  }

  // Sort on Gewerk
  DP_gewerk.sort( sortLowercase );

  $("#Select-Gewerk").empty();
  select = document.getElementById("Select-Gewerk");
  select.options[select.options.length] = new Option(window.ChhLanguage.default.historian.functionALL, 'ALLES');
  for (let c of DP_gewerk) {
    text2 = c;
    if (window.ChhLanguage.default.historian[c]) {
      text2 = window.ChhLanguage.default.historian[c];
    }
    select.options[select.options.length] = new Option(text2, c);
  }

  // Set start parameter
  document.getElementById("filterFeld").value = filter_feld;
}

/*****/
function parseDataPointsZoom() {
  if (location.search) {
    let parts = decodeURIComponent(location.search.substring(1)).split('&');
    for (let part of parts) {
      let nv = part.split('=');
      if (!nv[0]) {
        continue;
      }
      // parameter Zoom found
      if (nv[0].toLowerCase() === 'zoom') {
        let newStart = new Date(Zeitraum_Ende - (new Date(3600 * 1000 * parseFloat(nv[1]))));
        chart.xAxis[0].setExtremes(newStart.getTime(), Zeitraum_Ende.getTime(), true);
      }
    }
  }
}

/*****/
function parseDataPointsDP(text,nv) {
  for (let j = 0; j < text.length; j++) {
    let text2 = text[j].toUpperCase().split('|');
    let dp_id = text2[0];

    if (text2.length > 0) {

      let DP_pos = DP_point.findIndex(obj => obj.idx.toString().toUpperCase() === dp_id.toString().toUpperCase()
        || ((obj.attributes.displayName) && obj.attributes.displayName.toUpperCase() === dp_id.toUpperCase())
        || (obj.id.address + '.' + obj.id.identifier).toUpperCase() === dp_id.toUpperCase());
      if (DP_pos !== -1) {
        dp_id = DP_point[DP_pos].idx.toString();
      }
      let attrpos = DP_attribute.findIndex(obj => obj.id === dp_id);
      if (attrpos === -1) {
        let attr;
        if (DP_pos !== -1) {
          attr = defaultAttrib(DP_point[DP_pos], j, dp_id);
        } else {
          attr = defaultAttrib(-1, j, dp_id);
        }

        DP_attribute.push(attr);
        attrpos = DP_attribute.findIndex(obj => obj.id === dp_id);
      }
      DP_attribute[attrpos].visible = 2;
      for (let k = 1; k < text2.length; k++) {
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
          DP_attribute[attrpos].unit = decodeURIComponent(nv[1]).split(',')[j].split('|')[k].substr(1, 20).replaceAll("§", "%").replaceAll('µ', '&');
        } else if (text2[k].substr(0, 1) === 'N') {
          DP_attribute[attrpos].shortname = decodeURIComponent(nv[1]).split(',')[j].split('|')[k].substr(1, 40).replaceAll("§", "%").replaceAll('µ', '&');
        } else if (text2[k].substr(0, 1) === 'X') {
          DP_attribute[attrpos].factor = parseFloat(text2[k].substr(1, 10));
        } else if (text2[k].substr(0, 1) === 'O') {
          DP_attribute[attrpos].offset = parseFloat(text2[k].substr(1, 10));
        }
      }
    }
  }
}

/*****/
function parseDataPointsAxis(text,nv) {
  for (let j = 0; j < text.length; j++) {
    let text2 = text[j].toUpperCase().split('|');
    let axis_id = parseInt(text2[0].substr(1, 2));
    if (axis_id >= 0 && axis_id < DP_yAxis.length) {
      if (text2.length > 0) {
        for (let k = 1; k < text2.length; k++) {
          if (text2[k].substr(0, 1) === 'P') {
            if (text2[k].substr(1, 1) === '0') {
              DP_yAxis[axis_id].position = false;
            }
            if (text2[k].substr(1, 1) === '1') {
              DP_yAxis[axis_id].position = true;
            }
          } else if (text2[k].substr(0, 1) === 'C') {
            if (text2[k].substr(1, 1) === '0') {
              DP_yAxis[axis_id].type = 0;
            }
            if (text2[k].substr(1, 1) === '1') {
              DP_yAxis[axis_id].type = 1;
            }
          } else if (text2[k].substr(0, 1) === 'A') {
            if (text2[k].substr(1, 1) === '0') {
              DP_yAxis[axis_id].limit = 0;
            }
            if (text2[k].substr(1, 1) === '1') {
              DP_yAxis[axis_id].limit = 1;
            }
            if (text2[k].substr(1, 1) === '2') {
              DP_yAxis[axis_id].limit = 2;
            }
          } else if (text2[k].substr(0, 1) === 'L') {
            DP_yAxis[axis_id].min = parseFloat(text2[k].substr(1, 15));
          } else if (text2[k].substr(0, 1) === 'H') {
            DP_yAxis[axis_id].max = parseFloat(text2[k].substr(1, 15));
          } else if (text2[k].substr(0, 1) === 'G') {
            DP_yAxis[axis_id].tick = parseInt(text2[k].substr(1, 15));
          } else if (text2[k].substr(0, 1) === 'F') {
            DP_yAxis[axis_id].color = parseInt(text2[k].substr(1, 2));
          } else if (text2[k].substr(0, 1) === 'T') {
            DP_yAxis[axis_id].text = decodeURIComponent(nv[1]).split(',')[j].split('|')[k].substr(1, 50).replaceAll("§", "%").replaceAll('µ', '&');
          }
        }
      }
    }
  }
}

/**
* Create HighChart Object on loading
*/
$(document).ready(function() {

  window.Highcharts.setOptions({
    global: {
      useUTC: false,
    },
    lang: window.ChhLanguage.default.highcharts,
  });

  DP_ApiKey = "";
  if (typeof apiKey === "string" && apiKey !== "") {
    DP_ApiKey = apiKey.substring(1, apiKey.length);
  }

  var loc_setting = getLocalData('setting');
  if (loc_setting && loc_setting.substring(0, 2) === '{"') {
    try {
      DP_settings = JSON.parse(loc_setting);
      parseSetting();
      readLinkData();

      // read config data only if old
      var loc_settime = getLocalData('settingTime');
      if (loc_settime === null || parseInt(loc_settime) + 3600000 <= Date.now()) {
        // read data in delay of 1 sec
        setTimeout(requestSettings, 1000);
      }
    } catch {
      requestSettings();
    }
  } else {
    requestSettings();
  }

  document.getElementById("container").setAttribute("style", "height:" + ($(document).height() - 160) + "px");

  // Translate to Language Set
  document.getElementById('refresh').innerHTML = window.ChhLanguage.default.historian.buttonRefresh;
  document.getElementById('createLink').innerHTML = window.ChhLanguage.default.historian.buttonLink;
  document.getElementById('bntFavorit').innerHTML = window.ChhLanguage.default.historian.favoritTxt;
  document.getElementById('filterFeld').placeholder = window.ChhLanguage.default.historian.filterPlaceHolder;
  document.title = window.ChhLanguage.default.interface.pageTitle;

  // Define a custom symbol PLUS
  window.Highcharts.SVGRenderer.prototype.symbols.plus = function(x, y, w, h) {
    return ['M', x + w * 0.3, y,
      'L', x + w * 0.7, y,
      'L', x + w * 0.7, y + h * 0.3,
      'L', x + w, y + h * 0.3,
      'L', x + w, y + h * 0.7,
      'L', x + w * 0.7, y + h * 0.7,
      'L', x + w * 0.7, y + h,
      'L', x + w * 0.3, y + h,
      'L', x + w * 0.3, y + h * 0.7,
      'L', x, y + h * 0.7,
      'L', x, y + h * 0.3,
      'L', x + w * 0.3, y + h * 0.3,
      'L', x + w * 0.3, y,
      'Z'];
  };

  if (window.Highcharts.VMLRenderer) {
    window.Highcharts.VMLRenderer.prototype.symbols.plus = window.Highcharts.SVGRenderer.prototype.symbols.plus;
  }
  window.Highcharts.defaultOptions.symbols.push('plus');

  // Define a custom symbol CROSS
  window.Highcharts.SVGRenderer.prototype.symbols.cross = function(x, y, w, h) {
    return ['M', x, y + h * 0.2,
      'L', x + w * 0.3, y + h * 0.5,
      'L', x, y + h * 0.8,
      'L', x + w * 0.2, y + h,
      'L', x + w * 0.5, y + h * 0.7,
      'L', x + w * 0.8, y + h,
      'L', x + w, y + h * 0.8,
      'L', x + w * 0.7, y + h * 0.5,
      'L', x + w, y + h * 0.2,
      'L', x + w * 0.8, y,
      'L', x + w * 0.5, y + h * 0.3,
      'L', x + w * 0.2, y,
      'L', x, y + h * 0.2,
      'Z'];
  };
  if (window.Highcharts.VMLRenderer) {
    window.Highcharts.VMLRenderer.prototype.symbols.cross = window.Highcharts.SVGRenderer.prototype.symbols.cross;
  }
  window.Highcharts.defaultOptions.symbols.push('cross');

  // Define a custom symbol STAR
  window.Highcharts.SVGRenderer.prototype.symbols.star = function(x, y, w, h) {
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
      'Z'];
  };
  if (window.Highcharts.VMLRenderer) {
    window.Highcharts.VMLRenderer.prototype.symbols.star = window.Highcharts.SVGRenderer.prototype.symbols.star;
  }
  window.Highcharts.defaultOptions.symbols.push('star');


  var select;
  var option;
  var i;
  var key;

  // aggregation options
  select = document.getElementById("Select-Aggregation");
  for (i = 0; i < 8; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['aggrtxt' + i];
    option.value = 'A' + i;
    select.add(option);
  }

  // aggrtime options
  select = document.getElementById("Select-AggrTime");
  for (i = 0; i < 13; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['atimetxt' + i];
    option.value = 'T' + i;
    select.add(option);
  }

  // CompareType options
  select = document.getElementById("Select-Compare");
  for (i = 0; i < 14; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['comptype' + i];
    option.value = 'C' + i;
    select.add(option);
  }

  // LineType options
  select = document.getElementById("Select-Line");
  for (i = 0; i < 12; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['linetype' + i];
    option.value = 'L' + i;
    select.add(option);
  }

  // DashType options
  select = document.getElementById("Select-DashType");
  for (i = 0; i < DP_DashType.length; i++) {
    option = document.createElement("option");
    if (window.ChhLanguage.default.historian['dashtype' + i]) {
      option.text = window.ChhLanguage.default.historian['dashtype' + i];
    } else {
      option.text = DP_DashType[i];
    }
    option.value = 'D' + i;
    select.add(option);
  }

  // LineType options
  select = document.getElementById("Select-LineWidth");
  for (i = 0; i < 11; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['linewidth' + i];
    option.value = 'W' + i;
    select.add(option);
  }

  // Legend options
  select = document.getElementById("Select-Legend");
  for (i = 0; i < 7; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['legendtxt' + i];
    option.value = i;
    select.add(option);
  }

  // Navigator options
  select = document.getElementById("Select-Navigator");
  for (i = 0; i < 5; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['navitxt' + i];
    option.value = i;
    select.add(option);
  }

  // Label options
  select = document.getElementById("Select-Label");
  for (i = 0; i < 3; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['labeltxt' + i];
    option.value = i;
    select.add(option);
  }

  // Layout options
  select = document.getElementById("Select-Layout");
  for (i = 0; i < 4; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['layouttxt' + i];
    option.value = i;
    select.add(option);
  }

  // Grid options
  select = document.getElementById("Select-Grid");
  for (i = 0; i < 7; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['gridtxt' + i];
    option.value = i;
    select.add(option);
  }

  // Content options
  select = document.getElementById("Select-Content");
  for (i = 0; i < 4; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['contenttxt' + i];
    option.value = i;
    select.add(option);
  }

  // DataPoint options
  select = document.getElementById("Select-DataPoint");
  for (i = 0; i < 4; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['datapoint' + i];
    option.value = i;
    select.add(option);
  }

  // themes
  select = document.getElementById("Select-Theme");
  option = document.createElement("option");
  option.text = 'Standard hell/dunkel';
  option.value = 'standard';
  select.add(option);
  for (key in window.DP_Themes) {
    option = document.createElement("option");
    option.text = key;
    option.value = key;
    select.add(option);
  }

  // FontSize options
  select = document.getElementById("Select-FontSize");
  for (key in window.DP_FontSizes) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian.fontsize + "-" + DP_FontSizes[key].toString();
    option.value = DP_FontSizes[key];
    select.add(option);
  }

  // Axis Type
  select = document.getElementById("Select-AxisType");
  option = document.createElement("option");
  option.text = window.ChhLanguage.default.historian.yaxistype0;
  option.value = '0';
  select.add(option);
  option = document.createElement("option");
  option.text = window.ChhLanguage.default.historian.yaxistype1;
  option.value = '1';
  select.add(option);

  // Axis Position
  select = document.getElementById("Select-Position");
  option = document.createElement("option");
  option.text = window.ChhLanguage.default.historian.yaxispos0;
  option.value = '0';
  select.add(option);
  option = document.createElement("option");
  option.text = window.ChhLanguage.default.historian.yaxispos1;
  option.value = '1';
  select.add(option);

  // Axis min/max
  select = document.getElementById("Select-Limit");
  option = document.createElement("option");
  option.text = window.ChhLanguage.default.historian.yaxislimit0;
  option.value = '0';
  select.add(option);
  option = document.createElement("option");
  option.text = window.ChhLanguage.default.historian.yaxislimit1;
  option.value = '1';
  select.add(option);
  option = document.createElement("option");
  option.text = window.ChhLanguage.default.historian.yaxislimit2;
  option.value = '2';
  select.add(option);

  // Stacking
  select = document.getElementById("Select-Stacking");
  for (i = 0; i < 6; i++) {
    option = document.createElement("option");
    option.text = window.ChhLanguage.default.historian['Stacking' + i];
    option.value = i;
    select.add(option);
  }

  // Add mouse wheel for legend
  (function(H) {
    H.wrap(H.Legend.prototype, 'render', function(proceed) {
      let legend = this;
      let animation = H.pick(legend.options.navigation.animation, true);

      proceed.apply(this, Array.prototype.slice.call(arguments, 1));

      $(legend.group.element).on('wheel', function(event) {
        if (Scroll_Legend) {
          let e = legend.chart.pointer.normalize(event);
          e.originalEvent.deltaY < 0 ? legend.scroll(-1, animation) : legend.scroll(1, animation);
        }
        Scroll_Legend = !Scroll_Legend;
      });
    });
  }(window.Highcharts));

  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
    if (DP_Theme === 'standard') {
      createChart();
    }
  });

  setStockToolLang();

});

// *******************
// Stock Tools translate Bug fix
// *******************
function setStockToolLang() {
  for (let seriesType in window.Highcharts.seriesTypes) {
    let objType = window.Highcharts.seriesTypes[seriesType];
    if (objType.defaultOptions && objType.defaultOptions.params) {
      if (objType.prototype && objType.prototype.type) {
        if (window.Highcharts.defaultOptions.lang.navigation.popup.indicatorAliases) {
          if (window.Highcharts.defaultOptions.lang.navigation.popup.indicatorAliases[objType.prototype.type]) {
            objType.prototype.nameBase = window.Highcharts.defaultOptions.lang.navigation.popup.indicatorAliases[objType.prototype.type][0];
          }
        }
      }
    }
  }
}

// *******************
function changeEventRaumFilter() {
  var filter_raum = document.getElementById("Select-Raum").value;
  var filter_gewerk = document.getElementById("Select-Gewerk").value;
  var save_active_found = false;
  var attr2;

  var series;

  // remove all old series
  for (let i = chart.series.length - 1; i >= 0; i--) {
    chart.series[i].remove(false);
  }

  // add new series which are in filter
  for (let dppoint of DP_point) {
    if (checkFilter(filter_raum, filter_gewerk, filter_feld, DP_Limit, dppoint)) {

      addSerie(dppoint, '');
      series = chart.get(dppoint.idx.toString());

      // check if should be visible
      var attr = DP_attribute.findIndex(obj => obj.id === dppoint.idx.toString());
      if (attr !== -1) {

        if (DP_attribute[attr].visible === 2) {
          series.visible = true;
          save_active_found = true;
        } else {
          series.visible = false;
        }

        // load comparisation series
        var compType = DP_attribute[attr].comp;
        if (compType !== 'C0') {
          // check if options exist, if not create it with default and C0
          attr2 = DP_attribute.findIndex(obj => obj.id === compType + '_' + dppoint.idx.toString());
          if (attr2 === -1) {
            DP_attribute.push({
              id: compType + '_' + dppoint.idx.toString(),
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

          addSerie(dppoint, compType);

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
function checkFilter(p_raum, p_gewerk, p_textfilter, p_limit, p_dp) {

  // Generell Filter
  if (p_dp.historyDisabled && (DP_DataPointFilter === 0 || DP_DataPointFilter === 2)) {
    return false;
  }

  // Generell Filter
  if (p_dp.historyHidden && (DP_DataPointFilter === 0 || DP_DataPointFilter === 1)) {
    return false;
  }

  // Room Filter
  if (p_raum !== "ALLES" && p_raum !== "SYSVAR") {
    if (p_dp.attributes.room === null) {
      return false;
    }
    if (p_dp.attributes.room.indexOf(p_raum) === -1) {
      return false;
    }
  }
  if (p_raum !== "ALLES" && p_raum === "SYSVAR" && p_dp.id.interfaceId !== "SysVar") {
    return false;
  }

  // Function Filter
  if (p_gewerk !== "ALLES") {
    if (p_dp.attributes.function === null) {
      return false;
    }
    if (p_dp.attributes.function.indexOf(p_gewerk) === -1) {
      return false;
    }
  }

  // Description Filter
  if (p_textfilter !== '') {
    var ft = p_textfilter.split(' ');
    for (let fi of ft) {
      if ((p_dp.displayName + "/" + p_dp.id.address + "/ID:" + p_dp.idx).toLowerCase().indexOf(fi) === -1) {
        return false;
      }
    }
  }

  // only marked series are needed ?
  if (p_limit) {
    var attr = DP_attribute.findIndex(obj => obj.id === p_dp.idx.toString());
    if (attr === -1) {
      return false;
    }
    if (DP_attribute[attr].visible === 0) {
      return false;
    }
  }

  return true;
}

//********************
function loadNewSerienData() {
  for (let serie of chart.series) {
    if (serie.visible && serie.options.group !== "nav") {
      setData(serie);
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

  var axispos;
  for (axispos = 0; axispos < DP_yAxis.length; axispos++) {

    var axVisible = false;
    for (let serie of chart.yAxis[axispos].series) {
      if (serie.visible) {
        axVisible = true;
        break;
      }
    }

    if (axVisible) {
      yaxis_count++;

      var axiscolor = null;
      if (DP_yAxis[axispos].color === 0) {
        axiscolor = window.Highcharts.defaultOptions.yAxis.lineColor;
      } else if (DP_yAxis[axispos].color === 1 && chart.yAxis[axispos].series.length > 0) {
        for (let serie of chart.yAxis[axispos].series) {
          if (serie.visible) {
            axiscolor = serie.color;
            break;
          }
        }
      } else if (DP_yAxis[axispos].color > 1 && DP_yAxis[axispos].color < chart.options.colors.length + 2) {
        axiscolor = chart.options.colors[DP_yAxis[axispos].color - 2];
      }
      if (axiscolor !== null && axiscolor !== chart.yAxis[axispos].options.lineColor) {

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
          minorTickInterval: (DP_Grid === 5 || DP_Grid === 6) ? 'auto' : null,
          visible: true
        }, false);
      } else {
        chart.yAxis[axispos].update({
          // set gridlines only on 1
          gridLineWidth: (yaxis_count === 1) ? yaxis_grid : 0,
          minorGridLineWidth: (yaxis_count === 1) ? yaxis_mgrid : 0,
          minorTickInterval: (DP_Grid === 5 || DP_Grid === 6) ? 'auto' : null,
          visible: true
        }, false);

      }

      if (DP_yAxis[axispos].limit === 0 || DP_yAxis[axispos].limit === 1 ) {
        chart.yAxis[axispos].setExtremes(null, null);
      } else if (DP_yAxis[axispos].limit === 2) {
        // set extrem if config Dynamic or HARD
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
  $('.highcharts-yaxis').off("click", function() { clickShowDialogYAxis(this); });
  $('.highcharts-yaxis').on("click", function() { clickShowDialogYAxis(this); });

  $('.highcharts-yaxis-labels').off("click", function() { clickShowDialogYAxis(this); });
  $('.highcharts-yaxis-labels').on("click", function() { clickShowDialogYAxis(this); });

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

  var loopDate;
  var start;
  var id = 1;

  // Define whole workarea ...
  let minDate = chart.xAxis.reduce((a,b)=>a.min>b.min?a:b).min;
  let maxDate = chart.xAxis.reduce((a,b)=>a.max>b.max?a:b).max;
  if (!minDate || minDate > Zeitraum_Start.getTime()) {
    minDate = Zeitraum_Start.getTime();
  }
  if (!maxDate || maxDate < Zeitraum_Ende.getTime()) {
    maxDate = Zeitraum_Ende.getTime();
  }
  // gray in night, day yellow
  if (DP_DayLight === 1) {
    for (loopDate = minDate; loopDate <= maxDate; loopDate += 86400000) {
      start = new Date(loopDate);
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
    for (loopDate = minDate; loopDate <= maxDate; loopDate += 86400000) {
      start = new Date(loopDate);
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
    for (loopDate = minDate; loopDate <= maxDate; loopDate += 86400000) {
      start = new Date(loopDate);
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

  // Add Periode Parameter
  url += generateUrl();

  window.open(url, '_blank');
  window.focus();
  return true;
}

function generateUrl() {

  // Add Periode Parameter
  let url = 'periode=' + (Math.round(((Zeitraum_Ende - Zeitraum_Start) / (60 * 60 * 1000)) * 100) / 100).toString();

  var url2 = createUrlSerie();
  if (url2.length > 0) {
    url += '&dp=' + url2.substring(0, url2.length - 1);
  }

  url2 = createUrlAxis();

  if (url2.length > 0) {
    url += '&yaxis=' + url2.substring(0, url2.length - 1);
  }

  url += '&setting=';
  url += 'L' + DP_Legend.toString();
  url += '|N' + DP_Navigator.toString();
  url += '|P' + DP_Labels.toString();
  url += '|D' + DP_DayLight.toString();
  url += '|G' + DP_Grid.toString();
  url += '|F' + DP_ShowFilter.toString();
  url += '|I' + DP_DataPointFilter.toString();
  url += '|B' + DP_Theme;
  url += '|O' + DP_FontSize;
  url += '|R' + DP_AutoRefresh;
  url += '|T' + encodeURIComponent(DP_Title).replace(/'/g, '%27');
  url += '|S' + encodeURIComponent(DP_Subtitle).replace(/'/g, '%27');

  // Add Room to Link if needed
  var filter_raum = document.getElementById("Select-Raum").value;
  if (filter_raum !== 'ALLES') {
    url += '&room=' + filter_raum;
  }

  // Add Gewerk to Link if needed
  var filter_gewerk = document.getElementById("Select-Gewerk").value;
  if (filter_gewerk !== 'ALLES') {
    url += '&function=' + filter_gewerk;
  }

  // Add FilterFeld to Link if needed
  if (filter_feld !== '') {
    url += '&filterkey=' + filter_feld;
  }

  // Add Zoom if not full
  var extremes = chart.xAxis[0].getExtremes();
  if (extremes.max !== extremes.dataMax || extremes.min !== extremes.dataMin) {
    url += '&zoom=' + (Math.round(((extremes.max - extremes.min) / (60 * 60 * 1000)) * 100) / 100).toString();
  }

  return url;
}

//********************
function createUrlSerie() {
  let DP_pos;
  let url2 = '';
  // Add DP Filter if some selected
  for (let lserie of chart.series) {
    if (lserie.options.group !== "nav" && lserie.options.name !== 'MinMax') {
      // add Attribute if exist
      let attr = DP_attribute.findIndex(obj => obj.id === lserie.options.id.toString());
      if (attr !== -1) {
        if (DP_attribute[attr].visible === 2 || (DP_attribute[attr].visible === 1 && DP_Limit)) {
          url2 += lserie.options.id;
          url2 += '|' + DP_attribute[attr].aggr;
          url2 += '|' + DP_attribute[attr].atime;
          url2 += '|' + DP_attribute[attr].yaxis;
          url2 += '|' + DP_attribute[attr].line;
          url2 += '|' + DP_attribute[attr].color;
          url2 += '|' + DP_attribute[attr].comp;
          url2 += '|' + DP_attribute[attr].mark;
          url2 += '|' + DP_attribute[attr].dash;
          url2 += '|' + DP_attribute[attr].width;
          url2 += '|S' + DP_attribute[attr].stack;
          url2 += '|X' + DP_attribute[attr].factor;
          url2 += '|O' + DP_attribute[attr].offset;
          // check if still default unit, otherwise add to url
          if (lserie.options.id.substr(0, 1) === 'C') {
            DP_pos = DP_point.findIndex(obj => obj.idx.toString() === lserie.options.id.split('_')[1].toString());
          } else {
            DP_pos = DP_point.findIndex(obj => obj.idx.toString() === lserie.options.id.toString());
          }
          if (DP_pos === -1 || DP_point[DP_pos].attributes.unit !== DP_attribute[attr].unit) {
            url2 += '|U' + DP_attribute[attr].unit.replaceAll("%", "§").replaceAll('&', 'µ');
          }
          if (DP_attribute[attr].shortname !== '') {
            url2 += '|N' + DP_attribute[attr].shortname.replaceAll("%", "§").replaceAll('&', 'µ');
          }

          url2 += '|V' + DP_attribute[attr].visible;
          url2 += ',';
        }
      }
    }
  }
  return url2;
}


//********************
function createUrlAxis() {
  let url2 = '';
  for (let axispos = 0; axispos < DP_yAxis.length; axispos++) {
    if (chart.yAxis[axispos].visible && chart.yAxis[axispos].hasVisibleSeries) {
      url2 += 'Y' + axispos;
      url2 += '|P' + ((DP_yAxis[axispos].position) ? '1' : '0');
      url2 += '|C' + DP_yAxis[axispos].type;
      url2 += '|A' + DP_yAxis[axispos].limit;
      url2 += '|L' + DP_yAxis[axispos].min;
      url2 += '|H' + DP_yAxis[axispos].max;
      url2 += '|G' + DP_yAxis[axispos].tick;
      url2 += '|F' + DP_yAxis[axispos].color;
      if (DP_yAxis[axispos].text) {
        url2 += '|T' + DP_yAxis[axispos].text.replaceAll("%", "§").replaceAll('&', 'µ');
      }else {
        url2 += '|T';
      }
      url2 += ',';
    }
  }
  return url2;
}

//********************
function autoRefresh() {
  if (DP_AutoRefresh > 0) {
    setTimeout(autoRefresh, 1000);
    if (DP_Navigator < 3) {
      document.getElementById('autorefresh').innerHTML = ' - ' + window.ChhLanguage.default.historian.autorefreshText + ':' + AutoRefreshCount + ' Sek.';
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
  if (DP_Queue.length > 0) {
    chart.showLoading();
  } else {
    chart.hideLoading();
    if (DP_Button_Jump) {
      chart.xAxis[0].setExtremes(Zeitraum_Start.getTime(), Zeitraum_Ende.getTime(), true);
      DP_Button_Jump = false;
    }
  }
  if (DP_Queue.length > 0 && DP_Navigator < 3) {
    if (DP_Loading !== DP_Queue.length) {
      document.getElementById('loading').innerHTML = ' (Loading - ' + DP_Queue.length + ') <img src="loading.gif" alt="loading" height="20" width="20">';
      DP_Loading = DP_Queue.length;
    }
    setTimeout(loadingInfo, 500);
  } else {
    document.getElementById('loading').innerHTML = '';
    DP_Loading = 0;
  }
}


//********************
function addAggregationMinMax(serieObj) {

  var arr_dp = [];

  // first delete all linked series
  for (var i = chart.series.length - 1; i >= 0; i--) {
    if (serieObj.options.id === chart.series[i].options.linkedTo && chart.series[i].options.name === 'MinMax') {
      chart.series[i].remove(false);
    }
  }

  serieObj.userOptions.data.forEach(function(p) {
    arr_dp.push([p[0], p[1], p[1]]);
  });

  chart.addSeries({
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
  });
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
    let DP_pos;
    if (serieObj.options.id.substr(0, 1) === 'C') {
      DP_pos = DP_point.findIndex(obj => obj.idx.toString() === serieObj.options.id.split('_')[1].toString());
    } else {
      DP_pos = DP_point.findIndex(obj => obj.idx.toString() === serieObj.options.id.toString());
    }

    if (DP_pos === -1) {
      techName = 'n/a';
    } else if (DP_point[DP_pos].id.interfaceId === "SysVar") {
      techName = '<br/>Systemvariable';
    } else {
      techName = '<br/>' + DP_point[DP_pos].id.interfaceId + '.' + DP_point[DP_pos].id.address + '.' + DP_point[DP_pos].id.identifier;
    }

    DP_PopupID = serieObj.options.id.toString();

    var attr = DP_attribute.findIndex(obj => obj.id === serieObj.options.id.toString());
    if (attr === -1) {

      var ArrAttr;
      if (DP_pos === -1) {
        ArrAttr = defaultAttrib(-1, serieObj.colorIndex, DP_PopupID);
      } else {
        ArrAttr = defaultAttrib(DP_point[DP_pos], serieObj.colorIndex, DP_PopupID);
      }

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
    document.getElementById("Line-ShortName").value = DP_attribute[attr].shortname;

    document.getElementById("Select-Color").style.backgroundColor = chart.options.colors[parseInt(document.getElementById("Select-Color").value.substr(1, 2))];

    $("#LinePopup").modal();
  }
}

// Close Dialog
$("#DialogBtnOK").click(function() {
  getDialogLine();
  return true;
});

//Close Dialog and save as default
$("#LineDefault").click(function() {
  saveLine();
  return true;
});

function saveLine() {

  getDialogLine();

  var attr = DP_attribute.findIndex(obj => obj.id === DP_PopupID);
  if (attr === -1) {
    return;
  }
  var strCustom = '';
  strCustom += DP_attribute[attr].aggr;
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
  strCustom += '|U' + encodeURIComponent(DP_attribute[attr].unit).replace(/'/g, '%27');
  strCustom += '|N' + encodeURIComponent(DP_attribute[attr].shortname).replace(/'/g, '%27');

  var DP_pos = DP_point.findIndex(obj => obj.idx.toString() === DP_PopupID);
  var key = 'POINT' + DP_PopupID;

  // define customer if still NULL
  if (!DP_point[DP_pos].attributes.custom) {
    DP_point[DP_pos].attributes.custom = {};
  }

  if (DP_point[DP_pos].attributes.custom.HighChart !== strCustom) {

    DP_point[DP_pos].attributes.custom.HighChart = strCustom;

    // Save local cache for start performance
    setLocalData('DataPoints', JSON.stringify(DP_point));
    setLocalData('DataPointsTime', Date.now());

    var url = 'http://' + H2_server + ':' + H2_port;
    url += '/query/jsonrpc.gy';
    url += (DP_ApiKey === "") ? "" : "?" + DP_ApiKey;

    var postData = {
      id: key,
      method: 'updateDataPoint',
      params: [{
        'id': {
          'interfaceId': DP_point[DP_pos].id.interfaceId,
          'address': DP_point[DP_pos].id.address,
          'identifier': DP_point[DP_pos].id.identifier
        },
        'attributes': { 'custom': { 'HighChart': strCustom } }
      }]
    };

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
        ajaxErrorOutput(xhr, status, error);
      },
      success: function(result) {
        console.log(result);
      }
    });

  }
}

// Close Dialog Line
$("#DialogBtnClose").click(function() {
  $("#LinePopup").modal('hide');
  return true;
});

//Show Dialog
function getDialogLine() {
  var attr = DP_attribute.findIndex(obj => obj.id === DP_PopupID);

  if (DP_attribute[attr].comp !== document.getElementById("Select-Compare").value && document.getElementById("Select-Compare").value !== 'C0' && DP_attribute[attr].comp !== 'C0') {
    // change comparisation ID on old one, search any old one to update ID
    var attrC = DP_attribute.findIndex(obj => obj.id.substr(0, 1) === 'C' && obj.id.split('_')[1] === DP_PopupID);
    if (attrC !== -1) {
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
  DP_attribute[attr].shortname = document.getElementById("Line-ShortName").value;

  // ignor 0 values for faktor
  if (isNaN(DP_attribute[attr].factor) || DP_attribute[attr].factor === 0.0) {
    DP_attribute[attr].factor = 1;
  }

  $("#LinePopup").modal('hide');

  changeEventRaumFilter();
}

// Show Dialog
function showDialogSettings() {

  // set value on Popup
  document.getElementsByClassName("modal-title2")[0].innerHTML = window.ChhLanguage.default.historian.settings;
  document.getElementById("Select-Legend").value = DP_Legend.toString();
  document.getElementById("Select-Navigator").value = DP_Navigator.toString();
  document.getElementById("Select-Label").value = DP_Labels.toString();
  document.getElementById("Select-Layout").value = DP_DayLight.toString();
  document.getElementById("Select-Grid").value = DP_Grid.toString();
  document.getElementById("Select-Content").value = DP_ShowFilter.toString();
  document.getElementById("Select-DataPoint").value = DP_DataPointFilter.toString();
  document.getElementById("Select-Theme").value = DP_Theme;
  document.getElementById("Select-FontSize").value = DP_FontSize;
  document.getElementById("Line-Refresh").value = DP_AutoRefresh;
  document.getElementById("Line-Title").value = DP_Title;
  document.getElementById("Line-Subtitle").value = DP_Subtitle;

  $("#SettingPopup").modal();
}

// Close Dialog Settings
$("#Dialog2BtnOK").click(function() {
  getDialogSetting();
  return true;
});


//Close Dialog and save as default
$("#SettingDefault").click(function() {
  saveSetting();
  return true;
});

function saveSetting() {

  getDialogSetting();

  var strCustom = '';
  strCustom += 'L' + DP_Legend.toString();
  strCustom += '|N' + DP_Navigator.toString();
  strCustom += '|P' + DP_Labels.toString();
  strCustom += '|D' + DP_DayLight.toString();
  strCustom += '|G' + DP_Grid.toString();
  strCustom += '|F' + DP_ShowFilter.toString();
  strCustom += '|I' + DP_DataPointFilter.toString();
  strCustom += '|B' + DP_Theme;
  strCustom += '|O' + DP_FontSize;
  strCustom += '|R' + DP_AutoRefresh;
  strCustom += '|T' + encodeURIComponent(DP_Title).replace(/'/g, '%27');
  strCustom += '|S' + encodeURIComponent(DP_Subtitle).replace(/'/g, '%27');

  // Save to Global Settings
  DP_settings.Setting = strCustom;

  saveSettingsH2();
}

function saveSettingsH2() {

  var key = 'SETTING';
  var strSetNew = JSON.stringify(DP_settings);
  var strSetOld = JSON.stringify(DP_settings_old);

  if (strSetNew !== strSetOld) {

    setLocalData('setting', strSetNew);
    setLocalData('settingTime', Date.now());

    DP_settings_old = JSON.parse(strSetNew);

    var url = 'http://' + H2_server + ':' + H2_port;
    url += '/query/jsonrpc.gy';
    url += (DP_ApiKey === "") ? "" : "?" + DP_ApiKey;

    strSetNew = strSetNew.replace(/'/g, "'");

    var postData = {
      id: key,
      method: 'setConfig',
      params: ['HighChart', strSetNew]
    };

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
        ajaxErrorOutput(xhr, status, error);
      },
      success: function(result) {
        console.log(result);
      }
    });
  }
}

function getDialogSetting() {

  $("#SettingPopup").modal('hide');

  var filterrefresh = false;
  var chartrefresh = false;

  // Legend
  if (DP_Legend.toString() !== document.getElementById("Select-Legend").value) {
    DP_Legend = parseInt(document.getElementById("Select-Legend").value);
    chartrefresh = true;
  }

  // Navigator
  if (DP_Navigator.toString() !== document.getElementById("Select-Navigator").value) {
    DP_Navigator = parseInt(document.getElementById("Select-Navigator").value);
    chartrefresh = true;
  }

  // Title
  if (DP_Title !== document.getElementById("Line-Title").value) {
    DP_Title = document.getElementById("Line-Title").value;
    chartrefresh = true;
  }

  // Subtitle
  if (DP_Subtitle !== document.getElementById("Line-Subtitle").value) {
    DP_Subtitle = document.getElementById("Line-Subtitle").value;
    chartrefresh = true;
  }

  // Labels
  if (DP_Labels.toString() !== document.getElementById("Select-Label").value) {
    DP_Labels = parseInt(document.getElementById("Select-Label").value);
    filterrefresh = true;
  }

  // Layout
  if (DP_DayLight.toString() !== document.getElementById("Select-Layout").value) {
    DP_DayLight = parseInt(document.getElementById("Select-Layout").value);
    filterrefresh = true;
  }

  // Grid
  if (DP_Grid.toString() !== document.getElementById("Select-Grid").value) {
    DP_Grid = parseInt(document.getElementById("Select-Grid").value);
    chartSetOptions();
    filterrefresh = true;
  }

  // FilterLine
  if (DP_ShowFilter.toString() !== document.getElementById("Select-Content").value) {
    DP_ShowFilter = parseInt(document.getElementById("Select-Content").value);
    showFilterLine();
    chartSetOptions();
    filterrefresh = true;
  }

  // DataPointFilter
  if (DP_DataPointFilter.toString() !== document.getElementById("Select-DataPoint").value) {
    DP_DataPointFilter = parseInt(document.getElementById("Select-DataPoint").value);
    chartrefresh = true;
  }

  // Theme
  if (DP_Theme !== document.getElementById("Select-Theme").value) {
    DP_Theme = document.getElementById("Select-Theme").value;
    chartrefresh = true;
  }

  // FontSize
  if (DP_FontSize.toString() !== document.getElementById("Select-FontSize").value) {
    DP_FontSize = parseInt(document.getElementById("Select-FontSize").value);
    chartrefresh = true;
  }

  // AutoRefresh
  if (DP_AutoRefresh !== parseInt(document.getElementById("Line-Refresh").value)) {
    H2_refreshSec = parseInt(document.getElementById("Line-Refresh").value);
    AutoRefreshCount = H2_refreshSec;
    if (DP_AutoRefresh === 0 && H2_refreshSec > 0) {
      setTimeout(autoRefresh, 1000);
    }
    DP_AutoRefresh = H2_refreshSec;
  }

  if (chartrefresh) {
    createChart();
  } else if (filterrefresh) {
    changeEventRaumFilter();
  }

}

// Show Dialog
function showDialogFav() {

  // set value on Popup
  document.getElementsByClassName("modal-title4")[0].innerHTML = window.ChhLanguage.default.historian.favoritTitle;
  document.getElementById("Line-Title4").value = '';

  document.getElementById("Text-Title4").innerHTML = window.ChhLanguage.default.historian.favoritName;
  document.getElementById("favAdd").innerHTML = window.ChhLanguage.default.historian.favoritNEW;

// genearte Fav-List on Popup

  if (DP_settings.Favorites) {
    let favList = document.getElementById('favList');
    if (favList.childNodes && favList.childNodes.length > 0) {
      favList.removeChild(favList.childNodes[0]);
    }

    let mytable = document.createElement("table");
    mytable.setAttribute("width","100%");
    mytable.setAttribute("style","border: 0px; margin-top: 22px;");
    let mytablebody = document.createElement("tbody");

    for(let i = 0; i<DP_settings.Favorites.length;i++) {
      let mycurrent_row = document.createElement("tr");

      let mycurrent_cell1 = document.createElement("td");
      mycurrent_cell1.setAttribute("style","border: 0px;");

      let currenttext1 = document.createElement("button");
      currenttext1.innerHTML = decodeURIComponent(DP_settings.Favorites[i].Name);
      currenttext1.setAttribute("class","bnt btn-default");
      currenttext1.setAttribute("onclick","executeFav("+i+");");
      currenttext1.setAttribute("style","width: 90%;");

      mycurrent_cell1.appendChild(currenttext1);
      mycurrent_row.appendChild(mycurrent_cell1);

      let mycurrent_cell2 = document.createElement("td");
      mycurrent_cell2.setAttribute("style","border: 0px;");

      let currenttext2 = document.createElement("button");
      currenttext2.setAttribute("class","bnt btn-default");
      currenttext2.setAttribute("onclick","deleteFav("+i+");");
      currenttext2.setAttribute("style","font-size: 21px;");
      currenttext2.textContent = 'x';

      mycurrent_cell2.appendChild(currenttext2);
      mycurrent_row.appendChild(mycurrent_cell2);

      mytablebody.appendChild(mycurrent_row);
    }

    mytable.appendChild(mytablebody);
    favList.appendChild(mytable);
    mytable.setAttribute("border","2");
  }

  $("#FavPopup").modal();
}

// Close Dialog Settings
$("#Dialog4BtnOK").click(function() {
  $("#FavPopup").modal('hide');
  document.getElementById("Line-Title4").value = '';
  return true;
});

// Close Dialog Settings
$("#favAdd").click(function() {
  getDialogFav();
  document.getElementById("Line-Title4").value = '';
  return true;
});

// Close Dialog Settings
$("#Dialog4BtnClose").click(function() {
  document.getElementById("Line-Title4").value = '';
  $("#FavPopup").modal('hide');
  return true;
});

function deleteFav(favorit) {

 $("#FavPopup").modal('hide');

  if (DP_settings.Favorites[favorit]) {
  // delete Favorite entry on position
    DP_settings.Favorites.splice(favorit,1);

  // Save to H2 database
    saveSettingsH2();
  }
}

function executeFav(favorit) {
 $("#FavPopup").modal('hide');

  if (DP_settings.Favorites[favorit]) {

// execute Favorit
    var url = location.pathname + "?" + decodeURIComponent(DP_settings.Favorites[favorit].Url);
    window.open(url,"_self");

  }
}

function getDialogFav() {

  $("#FavPopup").modal('hide');

  // Favorite Title
  if (document.getElementById("Line-Title4").value) {

    if (!DP_settings.Favorites) {
      DP_settings.Favorites = [];
    }
    DP_settings.Favorites.push({ Name: encodeURIComponent(document.getElementById("Line-Title4").value).replace(/'/g, '%27'),
                                 Url: encodeURIComponent(generateUrl()).replace(/'/g, '%27') });

    saveSettingsH2();
  }

}

// Close Dialog Settings
$("#Dialog2BtnClose").click(function() {
  $("#SettingPopup").modal('hide');
});

function showFilterLine() {

  // ajust height of content to screen height
  if (DP_ShowFilter === 0) {
    document.getElementById("filter").style.display = "none";
    $('nav.navbar.navbar-default')[0].style.display = "none";
  } else if (DP_ShowFilter === 1) {
    document.getElementById("filter").style.display = "block";
    $('nav.navbar.navbar-default')[0].style.display = "block";
  } else if (DP_ShowFilter === 2) {
    document.getElementById("filter").style.display = "block";
    $('nav.navbar.navbar-default')[0].style.display = "none";
  } else if (DP_ShowFilter === 3) {
    document.getElementById("filter").style.display = "none";
    $('nav.navbar.navbar-default')[0].style.display = "block";
  }

  document.getElementById("container").setAttribute("style", "height:" + calcContSize().toString() + "px");

  if (chart) {
    chart.setSize(null, null, false);
  }
}

function defineLegend() {
  var ret = {};
  if (DP_Legend === 0) {
    ret = {
      enabled: false,
    };
  } else if (DP_Legend === 2) {
    ret = {
      enabled: true,
      layout: 'vertical',
      align: 'right',
      verticalAlign: 'top',
      floating: false,
      y: (DP_Navigator < 4) ? -30 : 0,
      maxHeight: $(window).height() - (DP_ShowFilter === 0 ? 90 : 0) - (DP_ShowFilter === 1 ? 200 : 0) - (DP_ShowFilter === 2 ? 120 : 0) - (DP_ShowFilter === 3 ? 180 : 0)
    };
  } else if (DP_Legend === 3) {
    ret = {
      enabled: true,
      layout: 'horizontal',
      align: 'center',
      verticalAlign: 'top',
      floating: true,
      y: 10 + (DP_Title === '' ? 0 : DP_FontSize + 20) + (DP_Subtitle === '' ? 0 : (DP_FontSize/6+5) + 15),
      maxHeight: 200
    };
  } else if (DP_Legend === 4) {
    ret = {
      enabled: true,
      layout: 'horizontal',
      align: 'center',
      verticalAlign: 'bottom',
      floating: true,
      y: -30 - DP_FontSize - ( DP_Navigator === 0 ? 20+DP_FontSize*4.5 : 0 ) - ( DP_Navigator === 1 ? 15+DP_FontSize*3.5 : 0 ) - ( DP_Navigator === 2 ? 11+DP_FontSize*2 : 0 )  ,
      maxHeight: 200
    };
  } else if (DP_Legend === 5) {
    ret = {
      enabled: true,
      layout: 'horizontal',
      align: 'center',
      verticalAlign: 'top',
      floating: false,
      y: 0,
      maxHeight: 200
    };
  } else if (DP_Legend === 6) {
    ret = {
      enabled: true,
      layout: 'horizontal',
      align: 'center',
      verticalAlign: 'bottom',
      floating: false,
      y: 0,
      maxHeight: 200
    };
    // on DP_Legend = 1 and default
  } else {
    ret = {
      enabled: true,
      layout: 'vertical',
      align: 'left',
      verticalAlign: 'top',
      floating: false,
      y: (DP_Navigator < 4) ? -30 : 0,
      maxHeight: $(window).height() - (DP_ShowFilter === 0 ? 90 : 0) - (DP_ShowFilter === 1 ? 200 : 0) - (DP_ShowFilter === 2 ? 120 : 0) - (DP_ShowFilter === 3 ? 180 : 0)
    };
  }
  ret['x'] = 0;

  return ret;
}

// Show Dialog
function showDialogYAxis(id) {

  document.getElementsByClassName("modal-title3")[0].innerHTML = window.ChhLanguage.default.historian.axissetting + ' ' + id.substr(5, 2);

  // find axis object
  var axispos = parseInt(id.substr(5, 2));
  if (axispos >= 0 && axispos < chart.options.yAxis.length) {
    DP_PopupAxisPos = axispos;
    document.getElementById("Line-Title3").value = DP_yAxis[DP_PopupAxisPos].text;
    document.getElementById("Select-Position").value = DP_yAxis[DP_PopupAxisPos].position ? '1' : '0';
    document.getElementById("Line-Min").value = DP_yAxis[DP_PopupAxisPos].min;
    document.getElementById("Line-Max").value = DP_yAxis[DP_PopupAxisPos].max;
    document.getElementById("Line-TickAmount").value = DP_yAxis[DP_PopupAxisPos].tick;
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
  return true;
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
  strCustom += '|T' + encodeURIComponent(DP_yAxis[DP_PopupAxisPos].text).replace(/'/g, '%27');

  // Save to global Settings
  DP_settings['YAXIS' + DP_PopupAxisPos] = strCustom;

  saveSettingsH2();

  return true;
});

function getDialogAxis() {

  $("#AxisPopup").modal('hide');

  // Update YAxis parameter
  let newOptions = {
    title: {
      text: document.getElementById("Line-Title3").value
    },
    lineWidth: 2,
    opposite: (document.getElementById("Select-Position").value === '1') ? true : false,
    type: (document.getElementById("Select-AxisType").value === '1') ? 'logarithmic' : 'linear',
    tickAmount: parseInt(document.getElementById("Line-TickAmount").value),
    min: (document.getElementById("Select-Limit").value === '2') ? parseFloat(document.getElementById("Line-Min").value) : null,
    max: (document.getElementById("Select-Limit").value === '2') ? parseFloat(document.getElementById("Line-Max").value) : null,
    softMin: (document.getElementById("Select-Limit").value === '1') ? parseFloat(document.getElementById("Line-Min").value) : null,
    softMax: (document.getElementById("Select-Limit").value === '1') ? parseFloat(document.getElementById("Line-Max").value) : null,
    startOnTick: (document.getElementById("Select-Limit").value === '2') ? false : true,
    endOnTick: (document.getElementById("Select-Limit").value === '2') ? false : true,
    allowDecimals: true,
    tickPositioner: null,
  };
  if (document.getElementById("Select-Limit").value === '2') {
    newOptions.tickPositioner =  function () {
      const axis = this;
      return axis.tickPositions.map((pos) => tickPos(axis,pos));
    };
  }

  chart.yAxis[DP_PopupAxisPos].update(newOptions);

  DP_yAxis[DP_PopupAxisPos].text = document.getElementById("Line-Title3").value;
  DP_yAxis[DP_PopupAxisPos].position = (parseInt(document.getElementById("Select-Position").value) === 0) ? false : true;
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
    if (DP_yAxis[i].text !== "" && DP_yAxis[i].text !== null) {
      option.text = DP_yAxis[i].text;
    } else {
      option.text = window.ChhLanguage.default.historian['yaxis' + i];
    }
    option.value = 'Y' + i;
    select.add(option);
  }

  loadNewAxisInfo();

}

// Close Dialog Settings
$("#Dialog3BtnClose").click(function() {
  $("#AxisPopup").modal('hide');
  return true;
});

// define Y-Axis array
function defineYAxis() {
  var arr = [];
  for (var y = 0; y < DP_yAxis.length; y++) {
    let newOptions = {
      id: 'AXISY' + y,
      className: 'axisy' + y,
      type: (DP_yAxis[y].type === 1) ? 'logarithmic' : 'linear',
      title: {
        text: DP_yAxis[y].text
      },
      lineWidth: 2,
      // showEmpty: false,
      opposite: DP_yAxis[y].position,
      tickAmount: DP_yAxis[y].tick,
      min: (DP_yAxis[y].limit === 2) ? DP_yAxis[y].min : null,
      max: (DP_yAxis[y].limit === 2) ? DP_yAxis[y].max : null,
      softMin: (DP_yAxis[y].limit === 1) ? DP_yAxis[y].min : null,
      softMax: (DP_yAxis[y].limit === 1) ? DP_yAxis[y].max : null,
      startOnTick: (DP_yAxis[y].limit === 2) ? false : true,
      endOnTick: (DP_yAxis[y].limit === 2) ? false : true,
      allowDecimals: true,
      visible: false,
      tickPositioner: null,
    };
    if (DP_yAxis[y].limit === 2) {
      newOptions.tickPositioner =  function () {
        const axis = this;
        return axis.tickPositions.map((pos) => tickPos(axis,pos));
      };
    }
    arr.push(newOptions);
  }
  return arr;
}

function tickPos(axis,pos) {
  let l_pos = pos;
  if (l_pos > axis.max) {
    l_pos = axis.max;
  } else if (l_pos < axis.min) {
    l_pos = axis.min;
  }
  return l_pos;
}

function showDialogYAxisUpdatColor() {
  var colorPos = parseInt(document.getElementById("Select-AxisColor").value);
  if (colorPos === 0 || colorPos === 1) {
    document.getElementById("Select-AxisColor").style.backgroundColor = window.Highcharts.defaultOptions.yAxis.lineColor;
  } else {
    colorPos -= 2;   // set back -2
    document.getElementById("Select-AxisColor").style.backgroundColor = chart.options.colors[colorPos];
  }
}

// define Comparisation days back
function getComparisionBackDay(str_compType) {
  if (str_compType === 'C1') { return -1 * 86400000; }
  if (str_compType === 'C2') { return -2 * 86400000; }
  if (str_compType === 'C3') { return -3 * 86400000; }
  if (str_compType === 'C4') { return -4 * 86400000; }
  if (str_compType === 'C5') { return -1 * 7 * 86400000; }
  if (str_compType === 'C6') { return -2 * 7 * 86400000; }
  if (str_compType === 'C7') { return -3 * 7 * 86400000; }
  if (str_compType === 'C8') { return -4 * 7 * 86400000; }
  if (str_compType === 'C9') { return -1 * 7 * 4 * 86400000; }
  if (str_compType === 'C10') { return -2 * 7 * 4 * 86400000; }
  if (str_compType === 'C11') { return -3 * 7 * 4 * 86400000; }
  if (str_compType === 'C12') { return -4 * 7 * 4 * 86400000; }
  if (str_compType === 'C13') { return -365 * 86400000; }
  return 0;
}

function chartSetOptions() {

  let myText = '';
  if (window.Highcharts.getOptions().navigation && window.Highcharts.getOptions().navigation.bindings && window.Highcharts.getOptions().navigation.bindings.labelAnnotation) {
     myText = window.Highcharts.getOptions().navigation.bindings.labelAnnotation;
  }

  let options = {
    lang: window.ChhLanguage.default.highcharts,
    chart: {
      events: {
        load: requestInitData,
        beforePrint: function () {
          if ( window.DP_Themes.transparent ) {
            let DP_Theme_Print = window.Highcharts.merge(window.DP_Themes['standard-light'], window.DP_Themes.transparent);
            this.update(DP_Theme_Print);
          }
        },
        afterPrint: function () {
          this.update(DP_Theme_Setting);
          chartSetOptions();
          chartSetElements();
        }
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
        theme: {
          style: {
            'font-size': DP_FontSize.toString() + "px"
          },
        },
      },
    },

    rangeSelector: {
      enabled: (DP_Navigator < 4) ? true : false,
      buttons: [{
        count: 30,
        type: 'minute',
        text: window.ChhLanguage.default.historian.range30M,
        events: { click: function() { checkZeitraum(this); return true; } },
        title: window.ChhLanguage.default.historian.rangeTitle30M
      }, {
        count: 1,
        type: 'hour',
        text: window.ChhLanguage.default.historian.rangeH,
        events: { click: function() { checkZeitraum(this); return true; } },
        title: window.ChhLanguage.default.historian.rangeTitleH
      }, {
        count: 6,
        type: 'hour',
        text: window.ChhLanguage.default.historian.range6H,
        events: { click: function() { checkZeitraum(this); return true; } },
        title: window.ChhLanguage.default.historian.rangeTitle6H
      }, {
        count: 1,
        type: 'day',
        text: window.ChhLanguage.default.historian.rangeD,
        events: { click: function() { checkZeitraum(this); return true; } },
        title: window.ChhLanguage.default.historian.rangeTitleD
      }, {
        count: 1,
        type: 'week',
        text: window.ChhLanguage.default.historian.rangeW,
        events: { click: function() { checkZeitraum(this); return true; } },
        title: window.ChhLanguage.default.historian.rangeTitleW
      }, {
        count: 1,
        type: 'month',
        text: window.ChhLanguage.default.historian.rangeM,
        events: { click: function() { checkZeitraum(this); return true; } },
        title: window.ChhLanguage.default.historian.rangeTitleM
      }, {
        count: 1,
        type: 'year',
        text: window.ChhLanguage.default.historian.rangeY,
        events: { click: function() { checkZeitraum(this); return true; } },
        title: window.ChhLanguage.default.historian.rangeTitleY
      }, {
        type: 'all',
        text: window.ChhLanguage.default.historian.rangeALL,
        events: { click: function() { checkZeitraum(this); return true; } },
        title: window.ChhLanguage.default.historian.rangeTitleALL
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
      y: 10,
    },
    navigator: {
      enabled: (DP_Navigator === 0 || DP_Navigator === 1) ? true : false,
    },
    scrollbar: {
      enabled: (DP_Navigator === 0 || DP_Navigator === 2) ? true : false,
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
            text: window.ChhLanguage.default.historian.favoritTxt,
            onclick: function() {
              showDialogFav();
            }
          }, {
            text: window.ChhLanguage.default.historian.settings,
            onclick: function() {
              showDialogSettings();
            }
          }, {
            text: (DP_Limit) ? window.ChhLanguage.default.historian.limitactive : window.ChhLanguage.default.historian.limitdeactive,
            onclick: function() {
              if (DP_Limit) {
                $('.highcharts-contextmenu')[0].children[0].children[2].innerHTML = window.ChhLanguage.default.historian.limitdeactive;
                DP_Limit = false;
              } else {
                $('.highcharts-contextmenu')[0].children[0].children[2].innerHTML = window.ChhLanguage.default.historian.limitactive;
                DP_Limit = true;
              }
              changeEventRaumFilter();
              return true;
            },
          }, {
            text: window.ChhLanguage.default.historian.buttonRefresh,
            onclick: function() {
              refreshClick();
              return true;
            },
          }, {
            text: window.ChhLanguage.default.historian.buttonLink,
            onclick: function() {
              createUrl();
              return true;
            },
          }, "separator", "viewFullscreen", "printChart", "downloadPNG", "downloadJPEG", "downloadPDF", "downloadSVG", "separator", "downloadCSV", "downloadXLS", "viewData"]
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
      gridLineWidth: (DP_Grid === 1 || DP_Grid === 3 || DP_Grid === 4 || DP_Grid === 6) ? 1 : 0,
      minorGridLineWidth: (DP_Grid === 4 || DP_Grid === 6) ? 1 : 0,
      minorTickInterval: (DP_Grid === 4 || DP_Grid === 6) ? 'auto' : null,
      dataMax: Date.now(),
      events: {
        afterSetExtremes: function() {
          showAggrText();
        },
      },
    },

    yAxis: defineYAxis(),

    plotOptions: {
      series: {
        events: {
          legendItemClick: function(event) {
            let attr;
            if (event.browserEvent.shiftKey) {
              showDialogLine(this);
              return false;
            } else {
              if (!this.visible) {
                setData(this);
              }
              if (this.visible) {
                attr = DP_attribute.findIndex(obj => obj.id === this.options.id.toString());
                if (attr !== -1) {
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
                attr = DP_attribute.findIndex(obj => obj.id === this.options.id.toString());
                if (attr !== -1) {
                  DP_attribute[attr].visible = 2;
                }
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
            // skip if annotations are active ...
            let skip = false;
            if (arguments[0] && arguments[0].activeAnnotation) {
              skip = true;
            }
            if (!skip)  {
              showDialogLine(this);
            }
            return true;
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
    }],
    navigation: {
      buttonOptions: {
        enabled: (DP_Navigator < 4) ? true : false,
      },
      bindings: {
        myText: myText
      }
    },
    stockTools: {
      gui: {
        iconsURL: 'stock-tools/stock-icons/',
        buttons: ["indicators", "separator", "myText", "lines", "measure", "toggleAnnotations", "separator", "verticalLabels", "separator", "zoomChange", "fullScreen"],
        definitions: {
          zoomChange: {
            items: ["zoomXY", "zoomY", "zoomX" ],
          },
          myText: {
            className: "highcharts-label-annotation",
            symbol: "label.svg"
          }
        },
      }
    }
  };

  chart = window.Highcharts.stockChart('container', options );
  if (!chart) {
    alert( 'HighChart Option error!');
  }
}

function showAggrText() {
  var attr;
  var aggrType;
  if (DP_Navigator < 3) {
    for (let lserie of chart.series) {
      if (lserie.visible && lserie.options.group !== "nav") {
        var grouping = lserie.currentDataGrouping;
        if (grouping) {
          let text = grouping.unitName + ((grouping.count > 1) ? '2' : '').toString();
          if (window.ChhLanguage.default.historian['aggr' + text]) {
            text = window.ChhLanguage.default.historian['aggr' + text];
          }
          if (lserie.options.id) {
            attr = DP_attribute.findIndex(obj => obj.id === lserie.options.id.toString());
            aggrType = 0;
            if (attr !== -1) {
              aggrType = parseInt(DP_attribute[attr].aggr.substr(1, 2));
            }
          }

          if (aggrType === 1) {
            document.getElementById('aggr_text').innerHTML = ' - ' + window.ChhLanguage.default.historian.aggrtxt1 + ': ' + grouping.count + '/' + text;
          } else if (aggrType === 2) {
            document.getElementById('aggr_text').innerHTML = ' - ' + window.ChhLanguage.default.historian.aggrtxt2 + ': ' + grouping.count + '/' + text;
          } else if (aggrType === 3) {
            document.getElementById('aggr_text').innerHTML = ' - ' + window.ChhLanguage.default.historian.aggrtxt3 + ': ' + grouping.count + '/' + text;
          } else if (aggrType === 4) {
            document.getElementById('aggr_text').innerHTML = ' - ' + window.ChhLanguage.default.historian.aggrtxt4 + ': ' + grouping.count + '/' + text;
          } else if (aggrType === 5) {
            document.getElementById('aggr_text').innerHTML = ' - ' + window.ChhLanguage.default.historian.aggrtxt5 + ': ' + grouping.count + '/' + text;
          } else if (aggrType === 6) {
            document.getElementById('aggr_text').innerHTML = ' - ' + window.ChhLanguage.default.historian.aggrtxt6;
          } else if (aggrType === 7) {
            document.getElementById('aggr_text').innerHTML = ' - ' + window.ChhLanguage.default.historian.aggrtxt7 + ': ' + grouping.count + '/' + text;
          } else {
            document.getElementById('aggr_text').innerHTML = ' - ' + window.ChhLanguage.default.historian.aggrtxt1 + ': ' + grouping.count + '/' + text;
          }
        } else {
          document.getElementById('aggr_text').innerHTML = ' -  ' + window.ChhLanguage.default.historian.aggrtxt0;
        }
        break;
      }
    }
  } else {
    document.getElementById('aggr_text').innerHTML = '';
  }
}

function chartSetElements() {

  // dark themes need black borders, update to like chart background
  if ((typeof chart.options.chart.backgroundColor) === 'string') {
    $('body').css('background-color', chart.options.chart.backgroundColor);
  } else if ((typeof chart.options.background2) === 'string') {
    $('body').css('background-color', chart.options.background2);
  } else if ((typeof chart.options.chart.borderColor) === 'string') {
    $('body').css('background-color', chart.options.chart.borderColor);
  }
  $('#message').css('color', chart.options.labels.style.color);

  if ((typeof DP_Theme_Setting.background2) === 'string') {
    $('.navbar-default').css('background-color', DP_Theme_Setting.background2);
    $('.btn-default').css('background-color', DP_Theme_Setting.background2);
    $('.form-select-h2').css('background-color', DP_Theme_Setting.background2);
    $('.form-input-h2').css('background-color', DP_Theme_Setting.background2);
    $('div.modal-content').css('background-color', DP_Theme_Setting.background2);
  }
  if ((typeof DP_Theme_Setting.textColor) === 'string') {
    $('.navbar-default').css('color', DP_Theme_Setting.textColor);
    $('.btn-default').css('color', DP_Theme_Setting.textColor);
    $('.btn-default').css('border-color', DP_Theme_Setting.textColor);
    $('.form-select-h2').css('color', DP_Theme_Setting.textColor);
    $('.form-select-h2').css('border-color', DP_Theme_Setting.textColor);
    $('.form-input-h2').css('color', DP_Theme_Setting.textColor);
    $('.form-input-h2').css('border-color', DP_Theme_Setting.textColor);
    $('div.modal-content').css('color', DP_Theme_Setting.textColor);
    $('div.modal-content').css('border-color', DP_Theme_Setting.textColor);
  }

  var select;
  var option;
  var i;

  $("#Select-Color").empty();
  // Color options
  select = document.getElementById("Select-Color");
  for (i = 0; i < chart.options.colors.length; i++) {
    option = document.createElement("option");
    option.text = 'Color ' + i;
    option.value = 'F' + i;
    option.style.backgroundColor = chart.options.colors[i];
    select.add(option);
  }

  $("#Select-Marker").empty();
  // Marker options
  select = document.getElementById("Select-Marker");
  option = document.createElement("option");
  option.text = 'none';
  option.value = 'M0';
  select.add(option);

  for (i = 0; i < chart.options.symbols.length; i++) {
    option = document.createElement("option");
    option.text = chart.options.symbols[i];
    option.value = 'M' + (i + 1);
    select.add(option);
  }
  for (i = 0; i < chart.options.symbols.length; i++) {
    option = document.createElement("option");
    option.text = chart.options.symbols[i] + '-RS';
    option.value = 'M' + (i + 1 + chart.options.symbols.length);
    select.add(option);
  }
  for (i = 0; i < chart.options.symbols.length; i++) {
    option = document.createElement("option");
    option.text = chart.options.symbols[i] + '-RW';
    option.value = 'M' + (i + 1 + chart.options.symbols.length * 2);
    select.add(option);
  }
  for (i = 0; i < chart.options.symbols.length; i++) {
    option = document.createElement("option");
    option.text = chart.options.symbols[i] + '-FS';
    option.value = 'M' + (i + 1 + chart.options.symbols.length * 3);
    select.add(option);
  }
  for (i = 0; i < chart.options.symbols.length; i++) {
    option = document.createElement("option");
    option.text = chart.options.symbols[i] + '-FW';
    option.value = 'M' + (i + 1 + chart.options.symbols.length * 4);
    select.add(option);
  }

  $("#Select-AxisColor").empty();
  // Color options
  select = document.getElementById("Select-AxisColor");
  option = document.createElement("option");
  option.text = 'Theme';
  option.value = '0';
  option.style.backgroundColor = window.Highcharts.defaultOptions.yAxis.lineColor;
  select.add(option);
  option = document.createElement("option");
  option.text = '1.Serie';
  option.value = '1';
  option.style.backgroundColor = window.Highcharts.defaultOptions.yAxis.lineColor;
  select.add(option);
  for (i = 0; i < chart.options.colors.length; i++) {
    option = document.createElement("option");
    option.text = 'Color ' + (i);
    option.value = (i + 2).toString();
    option.style.backgroundColor = chart.options.colors[i];
    select.add(option);
  }


// disable StockTools Button on hide Menue-Buttons
  if (DP_Navigator < 4) {
    $(".highcharts-stocktools-wrapper").css("display", "block");
  } else {
    $(".highcharts-stocktools-wrapper").css("display", "none");
  }

  $(window).resize(function() {

    document.getElementById("container").setAttribute("style", "height:" + calcContSize().toString() + "px");

    chart.legend.update(defineLegend());
    chart.reflow();

  });

}

// avoid double register same event
function eventSingleRegister(el_name,ev_name,ev_func) {
  if (undefined !== jQuery._data( $(el_name)[0], "events" )) {
    if (undefined !== jQuery._data( $(el_name)[0], "events" )[ev_name]) {
      return false;
    }
  }
  $(el_name).on(ev_name, ev_func);
  return true;
}

// *** set function for Filter_Feld
eventSingleRegister("#filterFeld", "keyup", function() {
  filter_feld = $(this).val().toLowerCase();
  changeEventRaumFilter();
});

// *** set function for Filter Room
eventSingleRegister("#Select-Raum", "change", function() {
  changeEventRaumFilter();
});

// *** set function for Filter Room
eventSingleRegister("#Select-Gewerk", "change", function() {
  changeEventRaumFilter();
});

// **********************
eventSingleRegister("#refresh", "click", function() {
   refreshClick();
   return true;
});

// **********************
eventSingleRegister("#createLink", "click", function() {
  createUrl();
  return true;
});

// *** set function for Favorit Button
eventSingleRegister("#bntFavorit", "click", function() {
  showDialogFav();
  return true;
});

// *** update background color on Field Select-Color
eventSingleRegister("#Select-Color", "change", function() {
  document.getElementById("Select-Color").style.backgroundColor = chart.options.colors[parseInt(document.getElementById("Select-Color").value.substr(1, 2))];
});

//*** update background color on Field Select-Color
eventSingleRegister("#Select-AxisColor", "change", function() {
  showDialogYAxisUpdatColor();
});

function refreshClick() {
  Zeitraum_Ende = new Date(Date.now());
  loadNewSerienData();
  return true;
}

// save to Local Browser Storage
function setLocalData(cname, cvalue) {
  try {
    let storage_name = H2_server + '_'+ H2_port + '_' + H2_version + '_' + cname;
    localStorage.setItem(storage_name, cvalue);
  } catch { }
}

// read Local Browser Storage to speed up 1 display
function getLocalData(cname) {
  try {
    let storage_name = H2_server + '_'+ H2_port + '_' + H2_version + '_' + cname;
    return localStorage.getItem(storage_name);
  } catch {
    return "";
  }
}

// check if new data should be loaded
function checkZeitraum(rangInfo) {
  var datNew = new Date(Zeitraum_Ende - (new Date(rangInfo._range)));
// Patch for remove zoom reset: if (Zeitraum_Start > datNew) {
    Zeitraum_Start = datNew;
    loadNewSerienData();
    DP_Button_Jump = true;
    return false;
// Patch for remove zoom reset: }
// Patch for remove zoom reset: return true;
}

function calcContSize() {
  let nav_height;
  let char_height;
  let font_factor;
  let cont_height;

  font_factor = DP_FontSize / 14;

  if (DP_Navigator === 4 || DP_Navigator === 3) {
    nav_height = 10;
  } else {
    nav_height = 55;
  }
  nav_height = Math.round(nav_height * font_factor);

  if (DP_ShowFilter === 0) {
    char_height = 0;
  } else if (DP_ShowFilter === 1) {
    char_height = 105;
  } else if (DP_ShowFilter === 2) {
    char_height = 35;
  } else if (DP_ShowFilter === 3) {
    char_height = 70;
  }
  char_height = Math.round(char_height * font_factor);

  cont_height = $(window).height() - nav_height - char_height;
  return( cont_height );

}

function ajaxErrorOutput(xhr, status, error) {
  console.log('AXAJ-error:');
  console.log(xhr);
  console.log(status);
  console.log(error);
}

function sortLowercase(a, b) {
  let x = a.toLowerCase();
  let y = b.toLowerCase();
  if (x < y) {
    return -1;
  }
  if (x > y) {
    return 1;
  }
  return 0;
}

function clickShowDialogYAxis(sobj) {
  if (sobj.classList) {
    for (let axispos of sobj.classList) {
      if (axispos.substr(0, 5) === 'axisy') {
        showDialogYAxis(axispos);
        break;
      }
    }
  }
  return true;
}

function toolTipInfo(sobj) {
  let xDate = new Date(sobj.x + (getComparisionBackDay(sobj.series.options.id.split('_')[0])));

  let txta = "<span style='fill:" + sobj.color + "'>\u25CF </span>"
    + sobj.series.name + ": <b>"
    + window.Highcharts.numberFormat(sobj.y, 2, ",", ".") + " "
    + sobj.series.tooltipOptions.valueSuffix + "</b><br/>";

  if (sobj.series.hasGroupedData) {

    let lAggrType = '';
    if (sobj.series.options.id) {
      let attr = DP_attribute.findIndex(obj => obj.id === sobj.series.options.id.toString());
      lAggrType = 0;
      if (attr !== -1) {
        lAggrType = parseInt(DP_attribute[attr].aggr.substr(1, 2));
      }
    }

    let pointRange;
    if (sobj.series.currentDataGrouping.totalRange) {
      pointRange = sobj.series.currentDataGrouping.totalRange;
    }

    let xEnde = window.Highcharts.dateFormat('%H:%M', xDate.getTime() + pointRange);
    if (xEnde === '00:00') {
      xEnde = '24:00';
    }

    // get Timeframe text
    if (pointRange < 3600000) {
      txta += "<b>" + window.Highcharts.dateFormat('%A, %e. %b %Y, %H:%M', xDate) + '-' + xEnde + "</b>";
    } else if (pointRange < 86400000) {
      txta += "<b>" + window.Highcharts.dateFormat('%A, %e. %b %Y, %H:%M', xDate) + '-' + xEnde + "</b>";
    } else if (pointRange < 86400000 * 20) {
      txta += "<b>" + window.Highcharts.dateFormat('%e. %b', xDate) + '-'
        + window.Highcharts.dateFormat('%e. %b %Y', xDate.getTime() + pointRange - 86400000) + "</b>";
    } else {
      txta += "<b>" + window.Highcharts.dateFormat('%b %Y', xDate) + "</b>";
    }

    // get Aggregation Symbol
    txta += '<i> (<b>';
    if (lAggrType === 1) { txta += jQuery('<div/>').html('&#x00d8; ').text(); }
    // average
    if (lAggrType === 2) { txta += jQuery('<div/>').html('&#x0394; ').text(); }
    // delta
    if (lAggrType === 3) { txta += jQuery('<div/>').html('&#x03a8; ').text(); }
    // min/max
    if (lAggrType === 4) { txta += jQuery('<div/>').html('&#x01a9; ').text(); }
    // sum
    if (lAggrType === 5) { txta += jQuery('<div/>').html('&#x01ac; ').text(); }
    // TIME_ON
    if (lAggrType === 6) { txta += jQuery('<div/>').html('&#x2248; ').text(); }
    // delta+
    if (lAggrType === 7) { txta += jQuery('<div/>').html('&#x2359; ').text(); }

    if (sobj.series.currentDataGrouping) {
      let text = sobj.series.currentDataGrouping.unitName + ((sobj.series.currentDataGrouping.count > 1) ? '2' : '').toString();
      if (window.ChhLanguage.default.historian['aggr' + text]) {
        text = window.ChhLanguage.default.historian['aggr' + text];
      }
      txta += '</b> ' + sobj.series.currentDataGrouping.count + ' ' + text;
    }
    txta += ")</i><br/>";

  } else {
    txta += "<b>" + window.Highcharts.dateFormat('%A, %b %e, %H:%M:%S', xDate) + "</b>";
  }
  return txta;
}

function chartSetFontSize() {

  let Size_N = DP_FontSize.toString() + "px";
  let Size_S = Math.round(DP_FontSize / 6 * 5).toString() + "px";
  let Size_H = Math.round(DP_FontSize / 2).toString() + "px";

  let Fontsize = {
    title: { style: { "fontSize": Size_N } },
    subtitle: { style: { "fontSize": Size_S } },
    xAxis: {
      labels: { style: { "fontSize": Size_N } },
      title: { style: { "fontSize": Size_N } },
    },
    yAxis: {
      labels: { style: { "fontSize": Size_N } },
      title: { style: { "fontSize": Size_N } },
    },
    tooltip: {
      headerFormat: "<span style=\"font-size: " + Size_N + "\">{point.key}</span><br/>",
      style: { fontSize: Size_S }
    },
    legend: {
      itemStyle: { fontSize: Size_S },
      navigation: {
        arrowSize: Math.round(DP_FontSize / 6 * 5),
        style: { fontSize: Size_S }
      },
      title: { style: { fontSize: Size_N } }
    },
    credits: { style: { fontSize: Size_H } },
    labels: { style: { fontSize: Size_S } },
    rangeSelector: {
      buttonTheme: { // styles for the buttons
        style: { fontSize: Size_S } //width: (DP_FontSize*2+20).toString() + "px", height: (DP_FontSize+20).toString() + "px" }
      },
//      height: (DP_FontSize + 20).toString() + "px",
      buttonSpacing: Math.round( DP_FontSize - 7 ),
    },
    navigator: { height: DP_FontSize * 3 },
    scrollbar: { height: DP_FontSize     },
    navigation: {
//      annotationsOptions: { labelOptions: { style: { "font-size": Size_H } } },
      buttonOptions: {
        symbolSize: DP_FontSize,
        height: DP_FontSize + 8,
        width: DP_FontSize + 10
      },
      menuItemStyle: { "font-size": Size_S }
    }
  };

  DP_Theme_Setting = window.Highcharts.merge(DP_Theme_Setting, Fontsize);


// calculate Font Sizes from Setting
  if (DP_FontSize) {
    $('body').css('font-size', DP_FontSize.toString() + "px");
    $('.form-select-h2').css('font-size', DP_FontSize.toString() + "px");
    $('.form-input-h2').css('font-size', DP_FontSize.toString() + "px");
    $('.modal-title').css('font-size', DP_FontSize.toString() + "px");
    $('.modal-title2').css('font-size', DP_FontSize.toString() + "px");
    $('.modal-title3').css('font-size', DP_FontSize.toString() + "px");
    $('.btn-default').css('font-size', DP_FontSize.toString() + "px");
    $('.LinePopup-text').css('width', 140 + (DP_FontSize * 6) + "px");
    $('.modal-dialog').css('width', 400 + (DP_FontSize * 17) + "px");
    $('.close').css('font-size', (DP_FontSize/2*3).toString() + "px");
    $('.navbar-brand').css('font-size', (DP_FontSize + 4).toString() + "px");
    $('.highcharts-button-box').css('height', (DP_FontSize + 4).toString() + "px");
    $('#bntFavorit').css('width', 16 + (DP_FontSize * 6) + "px");

    let dStyle = document.querySelector('style');

    dStyle.innerHTML =  '.highcharts-toggle-toolbar.highcharts-arrow-left  { \n'+
                        '  width: ' + (DP_FontSize + 6).toString() + 'px;\n' +
                        '  height: ' + (DP_FontSize + 6).toString() + 'px;\n' +
                        '  background-color: ' + DP_Theme_Setting.background2 + ';\n' +
                        '}\n' +
                        '.highcharts-toggle-toolbar.highcharts-arrow-left.highcharts-arrow-right {\n' +
                        '  width: ' + (DP_FontSize + 6).toString() + 'px;\n' +
                        '  height: ' + (DP_FontSize + 6).toString() + 'px;\n' +
                        '  background-color: ' + DP_Theme_Setting.background2 + ';\n' +
                        '}\n' +
                        'div.highcharts-bindings-wrapper li > span.highcharts-menu-item-btn { \n'+
                        '  background-size: ' + (DP_FontSize + 20).toString() + 'px 100%;\n' +
//                        '  filter: invert(100%);\n' +
//                        '  -webkit-filter: invert(100%);\n' +
                        '}\n';

    dStyle.innerHTML += 'div.highcharts-menu-wrapper, div.highcharts-bindings-wrapper ul { \n'+
                        '  width: ' + (DP_FontSize + 20).toString() + 'px;\n' +
                        '}\n' +
                        'li.highcharts-segment > ul.highcharts-submenu-wrapper { \n'+
                        '  width: ' + (DP_FontSize + 20).toString() + 'px;\n' +
                        '  background: ' + DP_Theme_Setting.background2 + ';\n' +
                        '}\n' +
                        'div.highcharts-bindings-wrapper .highcharts-stocktools-toolbar li { \n'+
                        '  height: ' + (DP_FontSize + 20).toString() + 'px;\n' +
                        '  background-color: ' + DP_Theme_Setting.background2 + ';\n' +
                        '}\n';
// Indicator Separator Height
    dStyle.innerHTML += 'div.highcharts-bindings-wrapper .highcharts-stocktools-toolbar li.highcharts-separator {\n' +
                        '  height: ' + (DP_FontSize).toString() + 'px;\n' +
                        '}\n';

    dStyle.innerHTML += 'div#filter select, div#filter input, div#filter button { \n'+
                        '  height: ' + (34 / 14 * DP_FontSize).toString() + 'px;\n' +
                        '}\n';
// Indicator Popup Colors:
    dStyle.innerHTML += '.highcharts-indicator-list {\n'+
                        '  background: ' + DP_Theme_Setting.background2 + ';\n'+
                        '  color: ' + DP_Theme_Setting.textColor + ';\n'+
                        '}\n';
    dStyle.innerHTML += '.highcharts-input-search-indicators-label {\n'+
                        '  color: ' + DP_Theme_Setting.textColor + ';\n'+
                        '  background: ' + DP_Theme_Setting.background2 + ';\n'+
                        '}\n';
    dStyle.innerHTML += '.highcharts-popup {\n'+
                        '  background-color: ' + DP_Theme_Setting.background2 + ';\n'+
                        '  color: ' + DP_Theme_Setting.textColor + ';\n'+
                        '  border: 1px solid ' + DP_Theme_Setting.chart.borderColor + ';\n'+
                        '}\n';
// RangeSelektor Button selektionSize
    dStyle.innerHTML += 'rect.highcharts-button-box {\n'+
                        '  width: ' + (DP_FontSize + 20).toString() + 'px;\n'+
                        '  height: ' + (DP_FontSize + 10).toString() + 'px;\n'+
                        '  x: ' + Math.round((DP_FontSize - 14)/2*-1).toString() + 'px;\n'+
                        '  y: ' + Math.round((DP_FontSize - 14)/2*-1).toString() + 'px;\n'+
                        '}\n';
// Zoom text box
    dStyle.innerHTML += '.highcharts-reset-zoom rect.highcharts-button-box {\n'+
                        '  width: ' + (DP_FontSize + 135).toString() + 'px;\n'+
                        '  height: ' + (DP_FontSize + 20).toString() + 'px;\n'+
                        '}\n';
// Bug Menue Button hide footer
    dStyle.innerHTML += 'div#container{\n'+
                        '  z-index: auto !important;\n'+
                        '  overflow: visible!important;\n'+
                        '}\n';
  }
}