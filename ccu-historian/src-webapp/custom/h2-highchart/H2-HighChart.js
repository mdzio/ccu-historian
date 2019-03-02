
// Setup H2 Database Services, default set to same server as this webpage and port 8082
var H2_server = location.hostname;
var H2_port = (location.port === "") ? 80 : location.port;
var H2_refreshSec = 60;   // Refresh Time is enabled

// declare global Variables
var chart;
var filter_feld = '';
var DP_point = [];
var Zeitraum_Ende = new Date(Date.now());
var Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(86400000 * 1)));
var Scroll_Legend = true;
var DP_Legend = true;
var DP_Navigator = true;
var DP_Labels = false;
var DP_DayLight = 1;
var DP_Limit = false;
var DP_Grouping = 0;
var DP_AutoRefresh = 0;
var DP_ShowFilter = 1;
var AutoRefreshCount = 0;
var DP_attribute = [];
var DP_PopupID;
var DP_Theme = '';
var HCDefaults;
var DP_DashType = ['Solid','Dash','DashDot','Dot','LongDash','LongDashDot','LongDashDotDot','ShortDash','ShortDashDot','ShortDashDotDot','ShortDot'];
var DP_Queue = [ ];

function createChart() {
//    var chartingOptions = HCDefaults;
    if (DP_Themes[DP_Theme]) {
       var chartingOptions = Highcharts.merge(HCDefaults,DP_Themes[DP_Theme]);
       Highcharts.setOptions(chartingOptions);
    }   
    
    
//    chart = new Highcharts.StockChart(chartingOptions);

    ChartSetOptions();

    chartSetElements();

}

/**
* create serien option and add it to HighStock Chart
*/
function addSerie(DP,DP_type) {

    var unit = DP.attributes.unit;
    var valueDecimals = 1;
    var factor = 1;
    var yAxis = 0;
    var dp_vis = 0;
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
    var color = null;
    var lineType = 0;
    var aggrType = DP_Grouping; 
    var aggrTime = 1;
    var dptype = DP.id.identifier;
    var dashtype = DP_DashType[0];
    var linewidth = 2;

    var attrIDX = (DP_type === '')?DP.idx.toString():(DP_type+'_'+DP.idx.toString()) ;
    
    switch (dptype) {
    case "ABS_HUMIDITY":
        yAxis = 10;
        valueDecimals = 1;
        lineType = 0;
        break;
    case "HUMIDITY":
    case "HUMIDITYF":
    case "ACTUAL_HUMIDITY":
    case "HUM_MAX_24H":
    case "HUM_MIN_24H":
        yAxis = 6;
        valueDecimals = 1;
        lineType = 0;
        break;
    case "TEMPERATURE":
    case "ACTUAL_TEMPERATURE":
    case "ABS_HUMIDITY":
    case "DEW_POINT":
    case "TEMP_MAX_24H":
    case "TEMP_MIN_24H":
        yAxis = 1;
        lineType = 0;
        valueDecimals = 1;
        break;
    case "SET_TEMPERATURE":
    case "SETPOINT":
        yAxis = 1;
        lineType = 2;
        valueDecimals = 1;
        break;
    case "MEAN5MINUTES":
        valueDecimals = 3;
        lineType = 0;
        break;
    case "BRIGHTNESS":
        yAxis = 8;
        valueDecimals = 0;
        lineType = 0;
        break;
    case "LEVEL":
        lineType = 2;
        unit = "";
        yAxis = 4;
        valueDecimals = 1;
        break;
    case "STATE":
        yAxis = 5,
        valueDecimals = 0;
        lineType = 2;
        break;
    case "PRESS_SHORT":
    case "PRESS_LONG":
    case "PRESS_OPEN":
    case "MOTION":
        yAxis = 5,
        marker = {
            enabled: true
        };
        factor = 5;
        lineType = 5;
        break;
    case "VALVE_STATE":
        valueDecimals = 0;
        lineType = 2;
        unit = "%";
        yAxis = 4;
        break;
    }

    if (DP.attributes.type === "BOOL") {
        yAxis = 5,
        valueDecimals = 0;
        type = "line";
        step = "left";
    }
    if (DP.attributes.unit === "%") {
        yAxis = 4,
        valueDecimals = 0;
        type = "line";
        step = "left";
        unit = "%";
    }
    if (DP.id.interfaceId === "SysVar" && DP.attributes.unit === "°C") {
        yAxis = 1;
        lineType = 0;
        valueDecimals = 1;
    }


    // Popup Change types

    var attr = DP_attribute.findIndex( obj => obj.id === attrIDX );
    if (attr != -1) {
       yAxis = parseInt(DP_attribute[attr].yaxis.substr(1,2));
       color = chart.options.colors[ parseInt(DP_attribute[attr].color.substr(1,2)) ];
       aggrType = parseInt(DP_attribute[attr].aggr.substr(1,2))
       aggrTime = parseInt(DP_attribute[attr].atime.substr(1,2))
       lineType = parseInt(DP_attribute[attr].line.substr(1,2))
       dp_vis = DP_attribute[attr].visible;
       unit = DP_attribute[attr].unit;

       var dashID = parseInt(DP_attribute[attr].dash.substr(1,2));
       if (dashID > 0) { dashtype = DP_DashType[dashID]; }

       linewidth = parseInt(DP_attribute[attr].width.substr(1,2)); 

       var markerID = parseInt(DP_attribute[attr].mark.substr(1,2))
       if (markerID > 0) {
          marker = {
              enabled: true,
              symbol: chart.options.symbols[markerID-1],
              radius: 4,
              // lineColor: '#666666',
              lineWidth: 1,
          };
       }
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
    };


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
       groupUnits =[ [ 'minute', [15,30] ], 
                     [ 'hour', [1,2,3,4,6,8,12] ], 
                     [ 'day' , [1] ],                     
                     [ 'week' , [1] ],                     
                     [ 'month' , [1,3,6] ],                     
                     [ 'year' , [1] ],                     
                   ];

    // dyna. only hours, days and month
    } else if (aggrTime === 2) {
       groupUnits =[ [ 'hour', [1] ], 
                     [ 'day' , [1] ],                     
                     [ 'month' , [1] ],                     
                     [ 'year' , [1] ],                     
                   ];
    // fix hours
    } else if (aggrTime === 3) {
       groupUnits =[ [ 'hour', [1] ], ];
       groupforced = true;
    } else if (aggrTime === 4) {
       groupUnits =[ [ 'day', [1] ], ];
       groupforced = true;
    } else if (aggrTime === 5) {
       groupUnits =[ [ 'week', [1] ], ];
       groupforced = true;
    } else if (aggrTime === 6) {
       groupUnits =[ [ 'month', [1] ], ];
       groupforced = true;
    } else if (aggrTime === 7) {
       groupUnits =[ [ 'month', [3] ], ];
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
        type = (type="line")?"spline":type;
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
    } else {
        grouping = {
            enabled: false,
        };
    }

    var pointFormater = null;
    var pointFormat   = null;
    var serienName    = '';

    if (DP_type.substr(0,1) === 'C') {
       serienName    = (DP.id.interfaceId === "SysVar")? (DP.attributes.displayName): (DP.attributes.displayName + '.' + DP.id.identifier) + '('+ChhLanguage.default.historian['comptype'+DP_type]+')'
       pointFormat   = null;
       pointFormater = function () { var xDate = new Date(this.x + (3600*24*1000*getComparisionBackDay(this.series.options.id.split('_')[0])));
                                     return "<tspan style='fill:"+this.color+"'>● </tspan>" + this.series.name + ": <b>" + 
                                     Highcharts.numberFormat(this.y , 2, ",", ".") +" "+ this.series.tooltipOptions.valueSuffix +"</b><br/>" +
                                     "<b>"+Highcharts.dateFormat('%A, %b %e, %H:%M:%S', xDate )+"</b>" 
                                  ;} 

    } else if (DP.id.interfaceId === "SysVar") {
       serienName    = DP.attributes.displayName;
       pointFormat  = '<span style="color:{point.color}">\u25CF</span> {series.name}: <b>{point.y}</b><br/>';
    } else {
       serienName    = DP.attributes.displayName + '.' + DP.id.identifier;
       pointFormat  = '<span style="color:{point.color}">\u25CF</span> {series.name}: <b>{point.y}</b><br/>' + DP.id.interfaceId + '.' + DP.id.address + '.' + DP.id.identifier + '<br/>';
    }

    var def_serie = {
        id: attrIDX,
        name: serienName,
        type: type,
        step: step,
        yAxis: yAxis,
        marker: marker,
        visible: (dp_vis===2)?true:false,
        color: color,
        dashStyle: dashtype,
        lineWidth: linewidth,
        fillOpacity: 0.4,
        borderColor: color,
        borderWidth: 2,
        data: [],
        tooltip: {
                  valueDecimals: valueDecimals,
                  pointFormat:   pointFormat,
                  pointFormatter: pointFormater,
                  valueSuffix: ' ' + unit,
               },
        dataGrouping: grouping,
        dataLabels : {
          enabled : DP_Labels,
				lowOverlap: true,
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

    // Create Chart Serie !!!
    var serie2 = chart.addSeries(def_serie, false, false);
/*    
    $(serie2.legendItem.element).bind('dblclick', function(){
            ShowDialog(serie2);;
    });
*/
    attr = DP_attribute.findIndex( obj => obj.id === attrIDX );
    if (attr === -1) {
        DP_attribute.push( {id: serie2.options.id.toString(),
                 aggr:  'A'+aggrType,
                 atime: 'T'+aggrTime,
                 yaxis: 'Y'+yAxis,
                 comp:  'C0',
                 line:  'L'+lineType,
                 mark:  'M0',
	              color: 'F'+serie2.colorIndex,
                 visible: dp_vis,
                 dash:  'D0',
                 width: 'W2',
                 factor: 1,
                 offset: 0,
                 unit: unit,
                 buffer_data: { timestamps: [], values: [], buffer_start: 0 , buffer_end: 0 },
              });
    }

}

function SetData(objSerie) {

    var datStart = Zeitraum_Start.getTime();
    var datEnd   = Zeitraum_Ende.getTime();
    var serie    = objSerie;
    var attrIDX;

    if (objSerie.options.name === 'MinMax') { return; }

    // get main DP
    if (objSerie.options.id.toString().substr(0,1) === 'C') {
       attrIDX = objSerie.options.id.toString().split('_')[1];
    } else {
       attrIDX = objSerie.options.id.toString();
    }

    var found = false;

    var attr = DP_attribute.findIndex( obj => obj.id === attrIDX );
    if (attr != -1) {
       
       if (DP_attribute[attr].comp != 'C0') {

          if (objSerie.options.id.toString().substr(0,1) === 'C' && DP_attribute[attr].visible === 2 ) { 
             // main is visible and all will be loaded together
            return;
          }

          // correct start for comparisation data, do read only once
          datStart += getComparisionBackDay(DP_attribute[attr].comp)*86400000;

          if (objSerie.options.id.toString().substr(0,1) === 'C') {
             datEnd   += getComparisionBackDay(DP_attribute[attr].comp)*86400000;
          }
       }

       // check buffer timestamps and decide to read additional data
       if ((DP_attribute[attr].buffer_data) && (DP_attribute[attr].buffer_data.buffer_start) && (DP_attribute[attr].buffer_data.buffer_end) ) {
          if ((DP_attribute[attr].buffer_data.buffer_start <= datStart) && ( DP_attribute[attr].buffer_data.buffer_end >= datEnd)) {
              // all data already in the buffer
              found = true;
              SetSerienData(attr, objSerie);
          } else if (DP_attribute[attr].buffer_data.buffer_start > datStart && DP_attribute[attr].buffer_data.buffer_start <= datEnd && DP_attribute[attr].buffer_data.buffer_end >= datEnd && DP_attribute[attr].buffer_data.values.length > 0 ) {
              // append to begin
              datEnd = DP_attribute[attr].buffer_data.buffer_start;
          } else if (DP_attribute[attr].buffer_data.buffer_start <= datStart && DP_attribute[attr].buffer_data.buffer_end >= datStart && DP_attribute[attr].buffer_data.buffer_end < datEnd && DP_attribute[attr].buffer_data.values.length > 0 ) {
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

/* not used at the moment 
// queue Ajax calls
var Queue = function() {
  var previous = new $.Deferred().resolve();

  return function(fn, fail) {
    if (typeof fn !== 'function') {
      throw 'must be a function';
    }
    return previous = previous.then(fn, fail || fn);
  };
};
*/

// save received data 
function BufferSerienData(id,data) {

   if (!id) { console.log('ID missing'); return;}

   // find queue entry
   // DP_Queue.push({key,p_attrID,series,datStart,datEnd);

   var q_i = DP_Queue.findIndex( obj => obj[0] === id );

   var attrIDX = DP_Queue[q_i][3];

   if (data.values.length > 0) {
    
     // correct values to round -3
     for (var i = 0 ; i < data.values.length; i++) {
        data.values[i] = Math.round(data.values[i] * 1000) / 1000;
     } 

     if ( DP_attribute[attrIDX].buffer_data.buffer_start >= DP_Queue[q_i][4] 
        && DP_attribute[attrIDX].buffer_data.buffer_start === DP_Queue[q_i][5] 
        && DP_attribute[attrIDX].buffer_data.values.length > 0 ) {

        DP_attribute[attrIDX].buffer_data.buffer_start = DP_Queue[q_i][4];
        DP_attribute[attrIDX].buffer_data.timestamps = data.timestamps.concat( DP_attribute[attrIDX ].buffer_data.timestamps ) ;
        DP_attribute[attrIDX].buffer_data.values     = data.values.concat( DP_attribute[attrIDX ].buffer_data.values ) ;

     } else if ( DP_attribute[attrIDX].buffer_data.buffer_end <= DP_Queue[q_i][5] 
                && DP_attribute[attrIDX].buffer_data.buffer_end === DP_Queue[q_i][4] 
                && DP_attribute[attrIDX].buffer_data.values.length > 0 ) {

        DP_attribute[attrIDX].buffer_data.buffer_end   = DP_Queue[q_i][5];
        DP_attribute[attrIDX].buffer_data.timestamps = DP_attribute[attrIDX ].buffer_data.timestamps.concat( data.timestamps ) ;
        DP_attribute[attrIDX].buffer_data.values     = DP_attribute[attrIDX ].buffer_data.values.concat( data.values ) ;
     } else {
        DP_attribute[attrIDX].buffer_data.buffer_start = DP_Queue[q_i][4];
        DP_attribute[attrIDX].buffer_data.buffer_end   = DP_Queue[q_i][5];
        DP_attribute[attrIDX].buffer_data.timestamps = data.timestamps;
        DP_attribute[attrIDX].buffer_data.values     = data.values;
     }

     // update counter
     document.getElementById("count_val").innerHTML = (Number(document.getElementById("count_val").innerHTML) + data.values.length).toString();

   }

   // get which serie has to be updated
   var serie = DP_Queue[q_i][2];

   // queue clear for this one
   DP_Queue.splice(q_i,1);
   
   SetSerienData(attrIDX, serie );
}

function SetSerienData(p_attr, serieObj) {

    var aggrType = parseInt(DP_attribute[p_attr].aggr.substr(1,2));
    var compType = DP_attribute[p_attr].comp;

    var datStart = Zeitraum_Start.getTime();
    var datEnd   = Zeitraum_Ende.getTime();

    var id     = DP_attribute[p_attr].id;

    var arr = [];
    var backSec = 0;
    
    // Min/Max not needed         
    if (serieObj.options.name === 'MinMax') { return; }

    if (serieObj.options.id.toString().substr(0,1) === 'C') {

        datStart += getComparisionBackDay(compType)*86400000;
        datEnd   += getComparisionBackDay(compType)*86400000;

        // Set backtime        
        backSec = getComparisionBackDay(compType)*3600*24*1000;

        var attr2 = DP_attribute.findIndex( obj => obj.id === serieObj.options.id.toString() );
        if (attr2) {
           aggrType = parseInt(DP_attribute[attr2].aggr.substr(1,2));
        }
    }

    // Define BufferLink
    var buffer = DP_attribute[p_attr].buffer_data;

    // collect all timesstamps and Values
    if (aggrType === 2) {

       // get start and end position if buffer over binary search
       var arrStart = sortedIndex(buffer.timestamps, datStart );
       var arrEnd   = sortedIndex(buffer.timestamps, datEnd   );

       // only if values found
       if (arrStart < buffer.timestamps.length) {
          var last_value = buffer.values[arrStart];
          var last_time  = buffer.timestamps[arrStart];
  
          for (var i = arrStart+1 ; i < arrEnd ; i++) {

            if ( buffer.timestamps[i] >= datStart && buffer.timestamps[i] <= datEnd) {
               // fill missing times with delta 0 every 10 min.
               if ((buffer.timestamps[i] - last_time) > 600000) {
                  for (var t = last_time; t < buffer.timestamps[i]; t=t+600000) {
                     arr.push([t, 0 ]);
                  }
               }
               arr.push([ buffer.timestamps[i]-backSec, 
                          ( ( buffer.values[i]-last_value) * DP_attribute[p_attr].factor ) + DP_attribute[p_attr].offset
                        ]);

               last_value = buffer.values[i];
               last_time  = buffer.timestamps[i];
            }
          }
       }

    // collect all timesstamps and Values for TIME_ON Aggregation
    } else if (aggrType === 5) {

       // get start and end position if buffer over binary search
       var arrStart = sortedIndex(buffer.timestamps, datStart );
       var arrEnd   = sortedIndex(buffer.timestamps, datEnd   );

       // only if values found
       if (arrStart < buffer.timestamps.length) {

          var last_value = (buffer.values[arrStart] > 0)?1:0;
          var last_time  = buffer.timestamps[arrStart];
  
          for (var i = arrStart+1 ; i < arrEnd ; i++) {
            if (last_value>0 && buffer.values[i] === 0) {

               last_value = buffer.timestamps[i] - last_time;
               // fill every minute with 1 as run time
               if (last_value > 60000) {
                  for (var t = last_time; t < buffer.timestamps[i]-60000; t=t+60000) {
                     arr.push([t - backSec, ( 1 * DP_attribute[p_attr].factor ) + DP_attribute[p_attr].offset ]);
                     last_value -= 60000;
                  }
               }
               if (last_value > 0) {
                  arr.push([t - backSec , ( Math.round(last_value/60)/1000  * DP_attribute[p_attr].factor ) + DP_attribute[p_attr].offset ]);
               };
               last_value = 0;
               last_time  = buffer.timestamps[i];
            } else if (last_value === 0 && buffer.values[i] > 0){

               last_value = buffer.timestamps[i] - last_time;
               // fill every minute with 1 as run time
               if (last_value > 60000) {
                  for (var t = last_time; t < buffer.timestamps[i]-60000; t=t+60000) {
                     arr.push([t - backSec, 0 ]);
                     last_value -= 60000;
                  }
               }

              last_value = 1;
              last_time  = buffer.timestamps[i];
            }
          }
          // fill also last minutes if still on
          if (last_value>0 ) {
               last_value = arrEnd - last_time;
               if (last_value > 60000) {
                  for (var t = last_time; t < arrEnd-60000; t=t+60000) {
                     arr.push([t - backSec, ( 1 * DP_attribute[p_attr].factor ) + DP_attribute[p_attr].offset ]);
                     last_value -= 60000;
                  }
               }
               if (last_value > 0) {
                  arr.push([t - backSec , ( Math.round(last_value/60)/1000  * DP_attribute[p_attr].factor ) + DP_attribute[p_attr].offset ]);
               };
            }
       }
    } else if (aggrType === 0) {

       // get start and end position over binary search
       var arrStart = sortedIndex(buffer.timestamps, datStart );
       var arrEnd   = sortedIndex(buffer.timestamps, datEnd   );
       for (var i = arrStart ; i < arrEnd; i++) {
           arr.push([ buffer.timestamps[i]-backSec,
                      ( buffer.values[i] * DP_attribute[p_attr].factor ) + DP_attribute[p_attr].offset
                    ]);
       }  

    } else {

       // get start and end position over binary search
       var arrStart = sortedIndex(buffer.timestamps, datStart );
       var arrEnd   = sortedIndex(buffer.timestamps, datEnd   );

       var last_value = buffer.values[arrStart];
       var last_time  = buffer.timestamps[arrStart];

       for (var i = arrStart ; i < arrEnd; i++) {

           if ((buffer.timestamps[i] - last_time) > 600000) {
              for (var t = last_time; t < buffer.timestamps[i]-300000; t=t+600000) {
                 arr.push([t, last_value ]);
              }
           }
           
           last_value = ( buffer.values[i] * DP_attribute[p_attr].factor ) + DP_attribute[p_attr].offset;
           last_time  = buffer.timestamps[i];

           arr.push([ buffer.timestamps[i]-backSec,
                      last_value ]);
       }  
    }

    if (arr.length > 0) {

       // Here Data are ready to set for Serie
       serieObj.setData(arr, true, false, false);

      // prepare and show min/max series
      if (aggrType === 3) {
         AddAggregationMinMax(serieObj);
      }

    }

    // read data for comp series
    if (DP_attribute[p_attr].comp != 'C0' && (serieObj.options.id.toString().substr(0,1) != 'C')) {

       var sobj = chart.get(DP_attribute[p_attr].comp + '_' + DP_attribute[p_attr].id);
       var attrC = DP_attribute.findIndex( obj => obj.id === DP_attribute[p_attr].comp + '_' + DP_attribute[p_attr].id );
       if (sobj && attrC != -1 && DP_attribute[attrC].comp === 'C0' && DP_attribute[attrC].visible === 2 ) {
          SetSerienData(p_attr, sobj);
       }
    }

}

// Find next timestamp in array by binary search
function sortedIndex(array, value) {
   var low = 0, high = array.length, mid;
   if (array[low] > value) return 0;
   if (array[high] < value) return high;
	while (low < high) {
      mid = Math.floor((low + high) / 2);
      if (array[mid] < value) low = mid + 1;
      else high = mid;
	}
	return low;
}

/**
*  read timeSerien data for H2 database
*/
function getDataH2(p_series,p_attrID, p_attr,datStart,datEnd) {
    var text;

// Refresh for Min/Max Aggregation done directly
    if (p_series.options.name === 'MinMax') return;
    
    var key =  p_attrID+'_'+Date.now();
    var p_id = p_series.options.id.toString();

    // refresh for comparisation done over real ID
    if (p_series.options.id.toString().substr(0,1) === 'C') {
       p_id = p_id.split('_')[1];
    };

    // save request to queue
    DP_Queue.push([key,p_attrID,p_series,p_attr,datStart,datEnd]);

    var url = 'http://' + H2_server + ':' + H2_port;
    url += '/query/jsonrpc.gy?j={%22id%22:%22' + key +'%22';
    url += ',%22method%22:%22getTimeSeriesRaw%22';
    url += ',%22params%22:[' + p_id + ',' + datStart + ',' + datEnd + ']}';

    // get serien data from H2 database
    $.ajax({
        type: "GET",
        url: url,
        dataType: "json",
        async: true,
        cache: false,
        error: function(xhr,status,error) {
                 console.log('AXAJ-error:'); 
                 console.log(xhr); 
                 console.log(status); 
                 console.log(error); 
               },
        success: function(result) {
                   if (!result.result) {
                       console.log(result);
                   } else if (result.result) {
                       BufferSerienData(result.id,result.result);
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
        error: function(xhr,status,error) {
                 console.log('AXAJ-error:'); 
                 console.log(xhr); 
                 console.log(status); 
                 console.log(error); 
               },
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

    var DP_rooms = [];
    var DP_gewerk = [];

    if (!TXT_JSON.result) return;

    // in result are all datapoint, let's check which are not hidden and active

    // DP_point = TXT_JSON.result;
    DP_point = [];
    for (i = 0; i < TXT_JSON.result.length; i++) {
        if (!TXT_JSON.result[i].historyDisabled && !TXT_JSON.result[i].historyHidden) {
           DP_point.push(TXT_JSON.result[i]);
        }
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
        if (!DP_point[i].historyDisabled && !DP_point[i].historyHidden) {

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
        }

    }

    // Sort on Rooms
    DP_rooms.sort(function(a, b) {
        var x = a.toLowerCase();
        var y = b.toLowerCase();
        if (x < y) { return -1; }
        if (x > y) { return 1; }
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
           text = ChhLanguage.default.historian[text];
		  }
        select.options[select.options.length] = new Option(text,DP_rooms[i]);
    }

    // Sort on Gewerk
    DP_gewerk.sort(function(a, b) {
        var x = a.toLowerCase();
        var y = b.toLowerCase();
        if (x < y) { return -1; }
        if (x > y) { return 1; }
        return 0;
    });

    var select = document.getElementById("Select-Gewerk");
    select.options[select.options.length] = new Option(ChhLanguage.default.historian.functionALL,'ALLES');
    for (i = 0; i < DP_gewerk.length; i++) {
        text = DP_gewerk[i];
        if(ChhLanguage.default.historian[text]){
           text = ChhLanguage.default.historian[text];
		  }
        select.options[select.options.length] = new Option(text,DP_gewerk[i]);
    };

    // Set start parameter 
    document.getElementById("filterFeld").value = filter_feld;


    // check parameter from get-link
    if (location.search) {
        var parts = location.search.substring(1).split('&');
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

                        var default_unit = '';
                        var DP_pos = DP_point.findIndex( obj => obj.idx.toString() === dp_id.toString() || 
                                                               ((obj.attributes.displayName) && obj.attributes.displayName.toUpperCase() === dp_id.toUpperCase()) ||
                                                                (obj.id.address + '.' + obj.id.identifier).toUpperCase() === dp_id.toUpperCase() );
                        if (DP_pos != -1) {
                           dp_id = DP_point[DP_pos].idx.toString();
                           default_unit = DP_point[DP_pos].attributes.unit;
                        }

                        var attrpos = DP_attribute.findIndex( obj => obj.id === dp_id );
                        if (attrpos === -1) {
                           var attr = {id: dp_id,
                                       aggr: 'A0',
                                       atime: 'T1',
                                       yaxis: 'Y0',
                                       comp: 'C0',
                                       line: 'L0',
                                       mark: 'M0',
                                       color: 'F0',
                                       visible: 2,
                                       dash: 'D0',
                                       width: 'W2',
                                       factor: 1,
                                       offset: 0,
                                       unit: default_unit,
                                       buffer_data: { timestamps: [], values: [], buffer_start: 0 , buffer_end: 0 },
                                      };
                           DP_attribute.push(attr);
                           attrpos = DP_attribute.findIndex( obj => obj.id === dp_id );
                        }
                        for (var k = 1; k < text2.length; k++) {
                          if (text2[k].substr(0,1) === 'A') {
                             DP_attribute[attrpos].aggr = text2[k];
                          } else if (text2[k].substr(0,1) === 'Y') {
                             DP_attribute[attrpos].yaxis = text2[k];
                          } else if (text2[k].substr(0,1) === 'T') {
                             DP_attribute[attrpos].atime = text2[k];
                          } else if (text2[k].substr(0,1) === 'F') {
                             DP_attribute[attrpos].color = text2[k];
                          } else if (text2[k].substr(0,1) === 'C') {
                             DP_attribute[attrpos].comp = text2[k];
                          } else if (text2[k].substr(0,1) === 'L') {
                             DP_attribute[attrpos].line = text2[k];
                          } else if (text2[k].substr(0,1) === 'M') {
                             DP_attribute[attrpos].mark = text2[k];
                          } else if (text2[k].substr(0,1) === 'D') {
                             DP_attribute[attrpos].dash = text2[k];
                          } else if (text2[k].substr(0,1) === 'W') {
                             DP_attribute[attrpos].width = text2[k];
                          } else if (text2[k].substr(0,1) === 'V') {
                             DP_attribute[attrpos].visible = parseInt(text2[k].substr(1,1));
                          } else if (text2[k].substr(0,1) === 'U') {
                             DP_attribute[attrpos].unit = decodeURIComponent(nv[1]).split(',')[j].split('|')[k].substr(1,20);
                          } else if (text2[k].substr(0,1) === 'X') {
                             DP_attribute[attrpos].factor = parseFloat(text2[k].substr(1,10));
                          } else if (text2[k].substr(0,1) === 'O') {
                             DP_attribute[attrpos].offset = parseFloat(text2[k].substr(1,10));
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
                    };
                };

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
            }
        }
    }

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

    // ajust height of content to screen height
    document.getElementById("container").setAttribute("style", "height:" + ($(document).height() - ((DP_ShowFilter===0)?0:160)) + "px");

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

    // aggregation options
    var select = document.getElementById("Select-Aggregation");
    for (var i = 0; i < 6; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.highcharts['aggrtxt'+i];
        option.value = 'A'+i;
        select.add(option); 
    }

    // aggrtime options
    var select = document.getElementById("Select-AggrTime");
    for (var i = 0; i < 8; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['atimetxt'+i];
        option.value = 'T'+i;
        select.add(option); 
    }

    // Yaxis options
    var select = document.getElementById("Select-Yaxis");
    for (var i = 0; i < 13; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['yaxis'+i];
        option.value = 'Y'+i;
        select.add(option); 
    }

    // CompareType options
    var select = document.getElementById("Select-Compare");
    for (var i = 0; i < 14; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['comptype'+i];
        option.value = 'C'+i;
        select.add(option); 
    }

    // LineType options
    var select = document.getElementById("Select-Line");
    for (var i = 0; i < 12; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['linetype'+i];
        option.value = 'L'+i;
        select.add(option); 
    }

    // DashType options
    var select = document.getElementById("Select-DashType");
    for (var i = 0; i < DP_DashType.length; i++) {
        var option = document.createElement("option");
        if (ChhLanguage.default.historian['dashtype'+i]) {
           option.text = ChhLanguage.default.historian['dashtype'+i];
        } else {
           option.text = DP_DashType[i];
        }
        option.value = 'D'+i;
        select.add(option); 
    }

    // LineType options
    var select = document.getElementById("Select-LineWidth");
    for (var i = 0; i < 11; i++) {
        var option = document.createElement("option");
        option.text = ChhLanguage.default.historian['linewidth'+i];
        option.value = 'W'+i;
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

    HCDefaults = Highcharts.getOptions();


    // check parameter from get-link
    if (location.search) {
        var parts = location.search.substring(1).split('&');
        for (var i = 0; i < parts.length; i++) {
            var nv = parts[i].split('=');
            if (!nv[0])
                continue;

            if (nv[0].toLowerCase() === 'dp') {
               DP_Limit = true;

            // parameter Periode (Stunden)
            } else if (nv[0].toLowerCase() === 'periode') {
                Zeitraum_Start = new Date(Zeitraum_Ende - (new Date(3600 * 1000 * parseInt(nv[1]))));
                // parameter Data Point
            } else if (nv[0].toLowerCase() === 'filterkey') {
                filter_feld = decodeURIComponent(nv[1].toLowerCase());
            } else if (nv[0].toLowerCase() === 'legend') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') { DP_Legend = false; }
            } else if (nv[0].toLowerCase() === 'navigator') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') { DP_Navigator = false; }
            } else if (nv[0].toLowerCase() === 'theme') {
                DP_Theme = decodeURIComponent(nv[1].toLowerCase());
            } else if (nv[0].toLowerCase() === 'labels') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'true') { DP_Labels = true; }
            } else if (nv[0].toLowerCase() === 'daylight') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') { DP_DayLight = 0; }
                if (decodeURIComponent(nv[1].toLowerCase()) === '0') { DP_DayLight = 0; }
                if (decodeURIComponent(nv[1].toLowerCase()) === '1') { DP_DayLight = 1; }
                if (decodeURIComponent(nv[1].toLowerCase()) === '2') { DP_DayLight = 2; }
                if (decodeURIComponent(nv[1].toLowerCase()) === '3') { DP_DayLight = 3; }
            } else if (nv[0].toLowerCase() === 'aggregation') {
                if (decodeURIComponent(nv[1].toLowerCase()) === '1') { DP_Grouping = 1; }
                if (decodeURIComponent(nv[1].toLowerCase()) === '2') { DP_Grouping = 2; }
                if (decodeURIComponent(nv[1].toLowerCase()) === '3') { DP_Grouping = 3; }
            } else if (nv[0].toLowerCase() === 'refresh') {
                if (parseInt(decodeURIComponent(nv[1])) > 0 ) { 
                    H2_refreshSec = parseInt(decodeURIComponent(nv[1]));
                    DP_AutoRefresh = H2_refreshSec;
                    AutoRefreshCount = DP_AutoRefresh;
                    setTimeout(AutoRefresh, 1000);
                } else if (decodeURIComponent(nv[1].toLowerCase()) === 'true') { 
                    DP_AutoRefresh = H2_refreshSec;
                    AutoRefreshCount = DP_AutoRefresh;
                    setTimeout(AutoRefresh, 1000);
                }
            } else if (nv[0].toLowerCase() === 'filterline') {
                if (decodeURIComponent(nv[1].toLowerCase()) === 'false') { 
                   DP_ShowFilter = 0; 
                   document.getElementById("filter").style.display = "none";
                   $('nav.navbar.navbar-default')[0].style.display = "none";
                   document.getElementById("container").setAttribute("style", "height:" + ($(document).height() -50 ) + "px");

                }
            }
        }
    }

//    if (DP_attribute.length >0) DP_Limit = true;

    createChart();

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

            addSerie(DP_point[i],'');
            series = chart.get(DP_point[i].idx.toString());

            // check if should be visible
            var attr = DP_attribute.findIndex( obj => obj.id === DP_point[i].idx.toString() );
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
                  attr2 = DP_attribute.findIndex( obj => obj.id === compType +'_'+DP_point[i].idx.toString() );
                  if (attr2 === -1) {
                     DP_attribute.push( {id:     compType +'_'+DP_point[i].idx.toString(),
                                        aggr:    DP_attribute[attr].aggr,
                                        atime:   DP_attribute[attr].atime,
                                        yaxis:   DP_attribute[attr].yaxis,
                                        comp:    'C0',
                                        line:    DP_attribute[attr].line,
                                        mark:    DP_attribute[attr].mark,
                                        color:   DP_attribute[attr].color,
                                        visible: DP_attribute[attr].visible,
                                        dash:    'D1',
                                        width:   DP_attribute[attr].width,
                                        factor:  DP_attribute[attr].factor,
                                        offset:  DP_attribute[attr].offset,
                                        unit:    DP_attribute[attr].unit,
                                        buffer_data: { timestamps: [], values: [], buffer_start: 0 , buffer_end: 0 },

                                     });
                     // Pointer setzen
                     attr2 = DP_attribute.length-1;
                  }

                  addSerie(DP_point[i], compType );

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
    } else {
       loadNewPlotBand();
       chart.redraw();
    }
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

    // only marked series are needed ?
    if (DP_Limit) {
       var attr = DP_attribute.findIndex( obj => obj.id === p_dp.idx.toString() );
       if (attr === -1)  return false;
       if( DP_attribute[attr].visible === 0) return false; 
    }

    return true;
}

//********************
function loadNewSerienData() {
    for (var serie = 0; serie < chart.series.length; serie++) {
        if (chart.series[serie].visible && chart.series[serie].options.group != "nav") {
            SetData(chart.series[serie]);
            
            chart.series[serie].yAxis.update({
                          lineColor: chart.series[serie].color,
                          labels:    { style: {"color": chart.series[serie].color } }, 
                          title:     { style: {"color": chart.series[serie].color } }, 
                                             }, false);
        }
    };
    chart.xAxis[0].setExtremes(Zeitraum_Start.getTime(), Zeitraum_Ende.getTime(), true);
    loadNewPlotBand()
    chart.redraw();
}

//********************
function loadNewPlotBand() {
// add plotband for every day 00-06 and 20-24 gray, 06-20 yellow mean day

  // remove all PlotBands from xAxis[0]
  for (var band = chart.xAxis[0].plotLinesAndBands.length-1; band >= 0  ; band--) {
      var band_id = chart.xAxis[0].plotLinesAndBands[band].id;
      if (chart.xAxis[0].plotLinesAndBands[band].options.to) {
         chart.xAxis[0].removePlotBand( band_id );
      } else {
         chart.xAxis[0].removePlotLine( band_id );
      }
  }

  // gray in night, day yellow
  if (DP_DayLight === 1) {
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
            color: '#fbfce3',
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
    if (DP_Limit) {
      for (var serie = 0; serie < chart.series.length; serie++) {
        if (chart.series[serie].options.group != "nav" && chart.series[serie].options.name != 'MinMax' ) {
            // add Attribute if exist
            attr = DP_attribute.findIndex( obj => obj.id === chart.series[serie].options.id.toString() );
            if (attr != -1) {
               if (attr.visible != 0) {
                  url2 += chart.series[serie].options.id;
                  url2 += (DP_attribute[attr].aggr  === 'A0')?'':'|'+ DP_attribute[attr].aggr;
                  url2 += (DP_attribute[attr].atime === 'T1')?'':'|'+ DP_attribute[attr].atime;
                  url2 += (DP_attribute[attr].yaxis === 'Y0')?'':'|'+ DP_attribute[attr].yaxis;
                  url2 += (DP_attribute[attr].line  === 'L0')?'':'|'+ DP_attribute[attr].line;
                  url2 += (DP_attribute[attr].color === 'F0')?'':'|'+ DP_attribute[attr].color;
                  url2 += (DP_attribute[attr].comp  === 'C0')?'':'|'+ DP_attribute[attr].comp;
                  url2 += (DP_attribute[attr].mark  === 'M0')?'':'|'+ DP_attribute[attr].mark;
                  url2 += (DP_attribute[attr].dash  === 'D0')?'':'|'+ DP_attribute[attr].dash;
                  url2 += (DP_attribute[attr].width === 'W2')?'':'|'+ DP_attribute[attr].width;
                  url2 += (DP_attribute[attr].factor === 1  )?'':'|X'+ DP_attribute[attr].factor;
                  url2 += (DP_attribute[attr].offset === 0  )?'':'|O'+ DP_attribute[attr].offset;

                  // check if still default unit, otherwise add to url
                  if (chart.series[serie].options.id.substr(0,1) === 'C') {
                     DP_pos = DP_point.findIndex( obj => obj.idx.toString() === chart.series[serie].options.id.split('_')[1].toString() );
                  } else {
                     DP_pos = DP_point.findIndex( obj => obj.idx.toString() === chart.series[serie].options.id.toString() );
                  }
                  if (DP_pos === -1 ||  DP_point[DP_pos].attributes.unit != DP_attribute[attr].unit) {
                     url2 += (DP_attribute[attr].unit  === 'xx')?'':'|U'+ DP_attribute[attr].unit;
                  }

                  url2 += (DP_attribute[attr].visible  === 2)?'':'|V'+ DP_attribute[attr].visible;
                  url2 += ',';
               }
            }
        }
      };
    } else {
      for (var serie = 0; serie < chart.series.length; serie++) {
        if (chart.series[serie].visible && chart.series[serie].options.group != "nav" && chart.series[serie].options.name != 'MinMax' ) {
            url2 += chart.series[serie].options.id;
            // add Attribute if exist
            attr = DP_attribute.findIndex( obj => obj.id === chart.series[serie].options.id.toString() );
            if (attr != -1) {
               url2 += (DP_attribute[attr].aggr  === 'A0')?'':'|'+ DP_attribute[attr].aggr;
               url2 += (DP_attribute[attr].atime === 'T1')?'':'|'+ DP_attribute[attr].atime;
               url2 += (DP_attribute[attr].yaxis === 'Y0')?'':'|'+ DP_attribute[attr].yaxis;
               url2 += (DP_attribute[attr].line  === 'L0')?'':'|'+ DP_attribute[attr].line;
               url2 += (DP_attribute[attr].color === 'F0')?'':'|'+ DP_attribute[attr].color;
               url2 += (DP_attribute[attr].comp  === 'C0')?'':'|'+ DP_attribute[attr].comp;
               url2 += (DP_attribute[attr].mark  === 'M0')?'':'|'+ DP_attribute[attr].mark;
               url2 += (DP_attribute[attr].dash  === 'D0')?'':'|'+ DP_attribute[attr].dash;
               url2 += (DP_attribute[attr].width === 'W2')?'':'|'+ DP_attribute[attr].width;
               url2 += (DP_attribute[attr].factor === 1  )?'':'|X'+ DP_attribute[attr].factor;
               url2 += (DP_attribute[attr].offset === 0  )?'':'|O'+ DP_attribute[attr].offset;

               // check if still default unit, otherwise add to url
               if (chart.series[serie].options.id.substr(0,1) === 'C') {
                  DP_pos = DP_point.findIndex( obj => obj.idx.toString() === chart.series[serie].options.id.split('_')[1].toString() );
               } else {
                  DP_pos = DP_point.findIndex( obj => obj.idx.toString() === chart.series[serie].options.id.toString() );
               }
               if (DP_pos === -1 ||  DP_point[DP_pos].attributes.unit != DP_attribute[attr].unit) {
                  url2 += (DP_attribute[attr].unit  === 'xx')?'':'|U'+ DP_attribute[attr].unit;
               }

               url2 += (DP_attribute[attr].visible  === 2)?'':'|V'+ DP_attribute[attr].visible;
            }
            url2 += ',';
        }
      };
    }
    
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
    if (!DP_Legend) {
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
    if (DP_DayLight != 1) {
        url += '&daylight='+DP_DayLight;
    }

	 // Grouping show    
    if (DP_Grouping != 0) {
        url += '&aggregation='+DP_Grouping;
    }

	 // AutoRefresh    
    if (DP_AutoRefresh != 0) {
        url += '&refresh='+(DP_AutoRefresh===60?true:DP_AutoRefresh);
    }

	 // ShowFilterLine    
    if (DP_ShowFilter === 0) {
        url += '&filterline=false';
    }

	 // Theme    
    if (DP_Theme != '') {
        url += '&theme='+DP_Theme;
    }

    window.open(url, '_blank');
    window.focus();
}

//********************
function AutoRefresh() {
   if (DP_AutoRefresh > 0) {
      setTimeout(AutoRefresh, 1000);
      document.getElementById('autorefresh').innerHTML = ' - ' + ChhLanguage.default.highcharts.autorefreshText + ':' + AutoRefreshCount +' Sek.' ;
      AutoRefreshCount--;
      if (AutoRefreshCount <= 0) {
         AutoRefreshCount=DP_AutoRefresh;
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
                valueSuffix:   serieObj.userOptions.tooltip.valueSuffix,
            },
        })
}

// Show Dialog
function ShowDialog(serieObj) {

// Set Dialog Values
  if (serieObj.options.id) {
     DP_PopupID = serieObj.options.id.toString();

     var attr = DP_attribute.findIndex( obj => obj.id === serieObj.options.id.toString() );
     if (attr === -1) {
        var ArrAttr = {id: serieObj.options.id.toString(),
                         aggr:  'A0',
                         atime: 'T1',
                         yaxis: 'Y'+serieObj.options.yAxis,
                         comp:  'C0',
                         line:  'L0',
                         mark:  'M0',
                         color: 'F'+serieObj.colorIndex,
                         visible: 2,
                         dash:  'D0',
                         width: 'W2',
                         factor: 1,
                         offset: 0,
                         unit:   '',
                         buffer_data: { timestamps: [], values: [], buffer_start: 0 , buffer_end: 0 },
                        };
       DP_attribute.push(ArrAttr);
       attr = DP_attribute.length-1;
     }
     if ('C' === serieObj.options.id.toString().substr(0,1)) {
        document.getElementById("compare").style.display = 'none';
     } else {
        document.getElementById("compare").style.display = '';
     }

     // set value on Popup
     document.getElementsByClassName("modal-title")[0].innerHTML = serieObj.name;
     document.getElementById("Select-Aggregation").value = DP_attribute[attr].aggr;
     document.getElementById("Select-AggrTime").value    = DP_attribute[attr].atime;
     document.getElementById("Select-Yaxis").value       = DP_attribute[attr].yaxis;
     document.getElementById("Select-Compare").value     = DP_attribute[attr].comp;
     document.getElementById("Select-Line").value        = DP_attribute[attr].line;
     document.getElementById("Select-Color").value       = DP_attribute[attr].color;
     document.getElementById("Select-Marker").value      = DP_attribute[attr].mark;
     document.getElementById("Select-DashType").value    = DP_attribute[attr].dash;
     document.getElementById("Select-LineWidth").value   = DP_attribute[attr].width;
     document.getElementById("Line-Factor").value        = DP_attribute[attr].factor;
     document.getElementById("Line-OffSet").value        = DP_attribute[attr].offset;
     document.getElementById("Line-Unit").value          = DP_attribute[attr].unit;

     document.getElementById("Select-Color").style.color = chart.options.colors[parseInt(document.getElementById("Select-Color").value.substr(1,1))];

     $("#LinePopup").modal();
  }
}


// Close Dialog
$("#DialogBtnOK").click(function(){

    var attr = DP_attribute.findIndex( obj => obj.id === DP_PopupID );

    if (DP_attribute[attr].comp != document.getElementById("Select-Compare").value && document.getElementById("Select-Compare").value != 'C0' && DP_attribute[attr].comp != 'C0') {
       // change comparisation ID on old one, search any old one to update ID
       var attrC = DP_attribute.findIndex( obj => obj.id.substr(0,1) === 'C' && obj.id.split('_')[1] === DP_PopupID );
       if (attrC != -1) {
          DP_attribute[attrC].id = document.getElementById("Select-Compare").value +'_'+ DP_PopupID;
       }
    }

    // get value on Popup
    DP_attribute[attr].aggr   = document.getElementById("Select-Aggregation").value;
    DP_attribute[attr].atime  = document.getElementById("Select-AggrTime").value;
    DP_attribute[attr].yaxis  = document.getElementById("Select-Yaxis").value;
    DP_attribute[attr].comp   = document.getElementById("Select-Compare").value;
    DP_attribute[attr].line   = document.getElementById("Select-Line").value;
    DP_attribute[attr].color  = document.getElementById("Select-Color").value;
    DP_attribute[attr].mark   = document.getElementById("Select-Marker").value;
    DP_attribute[attr].dash   = document.getElementById("Select-DashType").value;
    DP_attribute[attr].width  = document.getElementById("Select-LineWidth").value;
    DP_attribute[attr].factor = parseFloat(document.getElementById("Line-Factor").value);
    DP_attribute[attr].offset = parseFloat(document.getElementById("Line-OffSet").value);
    DP_attribute[attr].unit   = document.getElementById("Line-Unit").value;

    // ignor 0 values for faktor
    if (isNaN(DP_attribute[attr].factor) || DP_attribute[attr].factor === 0.0) DP_attribute[attr].factor = 1;

    $("#LinePopup").modal('hide');

    ChangeEventRaumFilter();
});

// Close Dialog
$("#DialogBtnClose").click(function(){
    $("#LinePopup").modal('hide');
});


function ResetOptions() {
    var defaultOptions = Highcharts.getOptions();
    for (var prop in defaultOptions) {
        if (typeof defaultOptions[prop] !== 'function') delete defaultOptions[prop];
    }
    // Fall back to the defaults that we captured initially, this resets the theme
    Highcharts.setOptions(HCDefaults);
}


// *** set function for Filter Room
$("#Select-Color").on("change", function() {
   document.getElementById("Select-Color").style.color = chart.options.colors[parseInt(document.getElementById("Select-Color").value.substr(1,1))];
});

// define Comparisation days back
function getComparisionBackDay(str_compType) {
    if (str_compType === 'C1')  return -1;
    if (str_compType === 'C2')  return -2;
    if (str_compType === 'C3')  return -3;
    if (str_compType === 'C4')  return -4;
    if (str_compType === 'C5')  return -1*7;
    if (str_compType === 'C6')  return -2*7;
    if (str_compType === 'C7')  return -3*7;
    if (str_compType === 'C8')  return -4*7;
    if (str_compType === 'C9')  return -1*7*4;
    if (str_compType === 'C10') return -2*7*4;
    if (str_compType === 'C11') return -3*7*4;
    if (str_compType === 'C12') return -4*7*4;
    if (str_compType === 'C13') return -1*7*52;
    return 0
}


function ChartSetOptions() {

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
                  if (DP_Legend) {
                     $('.highcharts-contextmenu')[0].children[0].children[0].innerHTML = ChhLanguage.default.highcharts.legendactive;
                     this.legend.update( { enabled: true, 
                                           layout: 'horizontal',
                                           align: 'center',
                                           verticalAlign: 'top',
                                           floating: true,
                                           y: 25,
                                         } );
                     DP_Legend = false;
                     $('.highcharts-contextmenu')[0].children[0].children[4].innerHTML = ChhLanguage.default.highcharts.limitactive;
                     DP_Limit = true;
                     ChangeEventRaumFilter();
                  } else {
                     $('.highcharts-contextmenu')[0].children[0].children[0].innerHTML = ChhLanguage.default.highcharts.legenddeactive;
                     this.legend.update( { enabled: true,
                                           layout: 'vertical',
                                           align: 'left',
                                           verticalAlign: 'top',
                                           floating: false,
                                           y: 0,
                                         } );
                     DP_Legend = true;
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
                text: (DP_DayLight===3) ? ChhLanguage.default.highcharts.daylight0: ((DP_DayLight===0)? ChhLanguage.default.highcharts.daylight1 : ((DP_DayLight===1) ? ChhLanguage.default.highcharts.daylight2 : ChhLanguage.default.highcharts.daylight3)),
                onclick: function() {
                  if (DP_DayLight===0) {
                    $('.highcharts-contextmenu')[0].children[0].children[3].innerHTML = ChhLanguage.default.highcharts.daylight2;
                    DP_DayLight = 1;
                  } else if (DP_DayLight===1) {
                    $('.highcharts-contextmenu')[0].children[0].children[3].innerHTML = ChhLanguage.default.highcharts.daylight3;
                    DP_DayLight = 2;
                  } else if (DP_DayLight===2) {
                    $('.highcharts-contextmenu')[0].children[0].children[3].innerHTML = ChhLanguage.default.highcharts.daylight0;
                    DP_DayLight = 3;
                  } else {
                    $('.highcharts-contextmenu')[0].children[0].children[3].innerHTML = ChhLanguage.default.highcharts.daylight1;
                    DP_DayLight = 0;
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
                text: (DP_Grouping===0) ? ChhLanguage.default.highcharts.aggractive1: ((DP_Grouping===1) ? ChhLanguage.default.highcharts.aggractive2: ((DP_Grouping===2) ? ChhLanguage.default.highcharts.aggractive3: ChhLanguage.default.highcharts.aggrdeactive)),
                onclick: function() {
                  if (DP_Grouping === 0) {
                    $('.highcharts-contextmenu')[0].children[0].children[5].innerHTML = ChhLanguage.default.highcharts.aggractive2;
                    DP_Grouping = 1;
                  } else if (DP_Grouping === 1) {
                    $('.highcharts-contextmenu')[0].children[0].children[5].innerHTML = ChhLanguage.default.highcharts.aggractive3;
                    DP_Grouping = 2;
                  } else if (DP_Grouping === 2) {
                    $('.highcharts-contextmenu')[0].children[0].children[5].innerHTML = ChhLanguage.default.highcharts.aggractive4;
                    DP_Grouping = 3;
                  } else if (DP_Grouping === 3) {
                    $('.highcharts-contextmenu')[0].children[0].children[5].innerHTML = ChhLanguage.default.highcharts.aggractive5;
                    DP_Grouping = 4;
                  } else if (DP_Grouping === 4) {
                    $('.highcharts-contextmenu')[0].children[0].children[5].innerHTML = ChhLanguage.default.highcharts.aggrdeactive;
                    DP_Grouping = 5;
                  } else {
                    $('.highcharts-contextmenu')[0].children[0].children[5].innerHTML = ChhLanguage.default.highcharts.aggractive1;
                    DP_Grouping = 0;
                  }
                  ChangeEventRaumFilter();
                },
              },{
                text: (DP_AutoRefresh===0) ? ChhLanguage.default.highcharts.autorefresh1 + H2_refreshSec + ' Sek.': ChhLanguage.default.highcharts.autorefresh0,
                onclick: function() {
                  if (DP_AutoRefresh === 0) {
                    $('.highcharts-contextmenu')[0].children[0].children[6].innerHTML = ChhLanguage.default.highcharts.autorefresh0;
                    DP_AutoRefresh = H2_refreshSec;
                    AutoRefreshCount = DP_AutoRefresh;
                    setTimeout(AutoRefresh, 1000);
                  } else {
                    $('.highcharts-contextmenu')[0].children[0].children[6].innerHTML = ChhLanguage.default.highcharts.autorefresh1 + H2_refreshSec + ' Sek.';
                    DP_AutoRefresh = 0;
                  }
                },
              },{
                text: (DP_ShowFilter===0) ? ChhLanguage.default.highcharts.showfilter1 : ChhLanguage.default.highcharts.showfilter0,
                onclick: function() {
                  if (DP_ShowFilter=== 0) {
                    $('.highcharts-contextmenu')[0].children[0].children[7].innerHTML = ChhLanguage.default.highcharts.showfilter0;
                    document.getElementById("filter").style.display = "";
                    $('nav.navbar.navbar-default')[0].style.display = "";
                    document.getElementById("container").setAttribute("style", "height:" + ($(document).height() -250 ) + "px");
                    DP_ShowFilter = 1;
                  } else {
                    $('.highcharts-contextmenu')[0].children[0].children[7].innerHTML = ChhLanguage.default.highcharts.showfilter1;
                    document.getElementById("filter").style.display = "none";
                    $('nav.navbar.navbar-default')[0].style.display = "none";
                    document.getElementById("container").setAttribute("style", "height:" + ($(document).height() -50 ) + "px");
                    DP_ShowFilter = 0;
                  }
                  chart.setSize(null ,null, false);
                },
              },{
                text: ChhLanguage.default.historian.buttonLink,
                onclick: function() {
                    createUrl();
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
                   var attr;
                   var aggrType;
  					    for (var serie = 0; serie < this.series.length; serie++) {
                   if (this.series[serie].visible && this.series[serie].options.group != "nav") {
                      var grouping = this.series[serie].currentDataGrouping;
                      if (grouping) {
                         var text = grouping.unitName;
                         if(ChhLanguage.default.highcharts['aggr'+text]){
                            text = ChhLanguage.default.highcharts['aggr'+text];
                         }
                         if (this.series[serie].options.id) {
                            attr = DP_attribute.findIndex( obj => obj.id === this.series[serie].options.id.toString() );
                            aggrType = DP_Grouping;
                            if (attr != -1) {
                               aggrType = parseInt(DP_attribute[attr].aggr.substr(1,2))
                            }
                         }

                         if (aggrType === 1) {
                            document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.highcharts.aggrtxt1 + ': ' + grouping.count + '/' + text;
                         } else if (aggrType === 2) {
                            document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.highcharts.aggrtxt2 + ': ' + grouping.count + '/' + text;
                         } else if (aggrType === 3) {
                            document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.highcharts.aggrtxt3 + ': ' + grouping.count + '/' + text;
                         } else if (aggrType === 4) {
                            document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.highcharts.aggrtxt4 + ': ' + grouping.count + '/' + text;
                         } else if (aggrType === 5) {
                            document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.highcharts.aggrtxt5 + ': ' + grouping.count + '/' + text;
                         } else {
                            document.getElementById('aggr_text').innerHTML = ' - ' + ChhLanguage.default.highcharts.aggrtxt1 + ': ' + grouping.count + '/' + text;
                         }  
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
            title: {
                text: ChhLanguage.default.highcharts.yaxis0,
            },
            lineWidth: 2,
            opposite: false,
            showEmpty: false,
        }, {
            id: "AXISY1",
            title: {
                text: ChhLanguage.default.highcharts.yaxis1,
            },
            softMin: 10,
            softMax: 30,
            lineWidth: 2,
            opposite: false,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11,
        }, {
            id: "AXISY2",
            title: {
                text: ChhLanguage.default.highcharts.yaxis2,
            },
            softMin: -20,
            softMax: 50,
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11,
        }, {
            id: "AXISY3",
            title: {
                text: ChhLanguage.default.highcharts.yaxis3,
            },
            softMin: 90,
            softMax: 20,
            lineWidth: 2,
            opposite: false,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11,
        }, {
            id: "AXISY4",
            title: {
                text: ChhLanguage.default.highcharts.yaxis4,
            },
            softMin: 0,
            softMax: 100,
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11,
        }, {
            id: "AXISY5",
            title: {
                text: ChhLanguage.default.highcharts.yaxis5,
            },
            softMin: 0,
            softMax: 1,
            maxPadding: 0.1,
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            tickAmount: 7,
        }, {
            id: "AXISY6",
            title: {
                text: ChhLanguage.default.highcharts.yaxis6,
            },
            softMin: 20,
            softMax: 100,
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11,
        }, {
            id: "AXISY7",
            title: {
                text: ChhLanguage.default.highcharts.yaxis7,
            },
            softMin: 900,
            softMax: 1000,
            lineWidth: 2,
            opposite: false,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11, 
        }, {
            id: "AXISY8",
            title: {
                text: ChhLanguage.default.highcharts.yaxis8,
            },
            softMin: 0,
            softMax: 5000,
            lineWidth: 2,
            opposite: false,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11, 
        }, {
            id: "AXISY9",
            title: {
                text: ChhLanguage.default.highcharts.yaxis9,
            },
            softMin: 300,
            softMax: 3000,
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11, 
        }, {
            id: "AXISY10",
            title: {
                text: ChhLanguage.default.highcharts.yaxis10,
            },
            softMin: 3,
            softMax: 15,
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11, 
        }, {
            id: "AXISY11",
            title: {
                text: ChhLanguage.default.highcharts.yaxis11,
            },
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11, 
        }, {
            id: "AXISY12",
            title: {
                text: ChhLanguage.default.highcharts.yaxis12,
            },
            lineWidth: 2,
            opposite: true,
            showEmpty: false,
            allowDecimals: false,
            tickAmount: 11, 
        }],

        legend: ((DP_Legend)?({
            enabled: true,
            layout: 'vertical',
            backgroundColor: '#FFFFFF',
            align: 'left',
            verticalAlign: 'top',
            floating: false,
            x: 0,
            y: 0,
            navigation: {
                arrowSize: 20,
            }
        }):({
            enabled: true,
            layout: 'horizontal',
            backgroundColor: '#FFFFFF',
            align: 'center',
            verticalAlign: 'top',
            floating: true,
            x: 0,
            y: 25,
            navigation: {
                arrowSize: 20,
            }
        })),

        plotOptions: {
            series: {
                events: {
                    legendItemClick: function(event) {
                        if (event.browserEvent.shiftKey) {
                           ShowDialog(this);
                           return false;
                        } else {
                          var visibility = this.visible ? 'visible' : 'hidden';
                          if (!this.visible) {
                              SetData(this);
                          }
                          if (this.visible) {
                           var attr = DP_attribute.findIndex( obj => obj.id === this.options.id.toString() );
                           if (attr != -1) {
                              DP_attribute[attr].visible = (DP_Limit)?1:0;
                              if (DP_attribute[attr].aggr === 'A3') {
                                 for (var i = chart.series.length - 1; i >= 0; i--) {
                                   if (this.options.id === chart.series[i].options.linkedTo && chart.series[i].options.name === 'MinMax') { 
                                      chart.series[i].remove(false);
                                   }
                                 }
                              }
                           }
                          } else {
							        var attr = DP_attribute.findIndex( obj => obj.id === this.options.id.toString() );
                             if (attr != -1) DP_attribute[attr].visible = 2;
                          }
                          return true;
                        }
                      },
                    click:function(){
                         ShowDialog(this);
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


function chartSetElements() {

    chart = $('#container').highcharts();

    // dark themes need black borders, update to like chart background
    if ((typeof chart.options.chart.backgroundColor) === 'string') {
       $('body').css('background-color', chart.options.chart.backgroundColor );
    } else if ((typeof chart.options.background2) === 'string') {
       $('body').css('background-color', chart.options.background2 );
    } else if ((typeof chart.options.chart.borderColor) === 'string') {
       $('body').css('background-color', chart.options.chart.borderColor );
    }
    $('#message').css('color', chart.options.labels.style.color );

    // Color options
    var select = document.getElementById("Select-Color");
    for (i = 0; i < chart.options.colors.length; i++) {
        var option = document.createElement("option");
        option.text = 'Color '+i;
        option.value = 'F'+i;
        option.style.color = chart.options.colors[i];
        select.add(option); 
    }

    // Marker options
    var select = document.getElementById("Select-Marker");
        var option = document.createElement("option");
        option.text = 'none';
        option.value = 'M0';
        select.add(option); 

    for (i = 0; i < chart.options.symbols.length; i++) {
        var option = document.createElement("option");
        option.text = chart.options.symbols[i]
        option.value = 'M'+(i+1);
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

}
