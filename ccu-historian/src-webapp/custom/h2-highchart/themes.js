var DP_Themes = {

// ********
  'standard-light': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    chart: {
      backgroundColor: "#ffffff",
      borderColor: "#335cad",
      borderRadius: 0,
      borderWidth: null,
      colorCount: 10,
      ignoreHiddenSeries: true,
      spacing: [0, 10, 15, 10],
      styledMode: false,

      style: null,
      width: null,
      height: null,
      className: null,
      plotBorderColor: "#cccccc",

      plotBorderWidth: null,
      plotBackgroundColor: null,
      plotBackgroundImage: null,
      plotShadow: false,

      resetZoomButton: {
        position: {
          align: "right",
          x: -10,
          y: 10
        }
      },
      theme: {
        zIndex: 6
      },
    },
    title: {
      align: "center",
      margin: 15,
      text: "",
      widthAdjust: -44,
      y: 25,
      style: { "fontSize": "16px", "color": "#666666" },
    },
    subtitle: {
      align: "center",
      margin: null,
      text: "",
      widthAdjust: -44,
      y: 50,
      style: { "fontSize": "14px", "color": "#666666" },
    },
    xAxis: {
      lineColor: "#ccd6eb",
      gridLineColor: "#e6e6e6",
      alternateGridColor: null,
      minorGridLineColor: "#f2f2f2",
      minorTickColor: "#999999",
      tickColor: "#ccd6eb",
      labels: { style: { "color": "#666666", "cursor": "default", "fontSize": "11px" } },
      title: { style: { "color": "#666666", "fontSize": "14px" } },
    },
    yAxis: {
      lineColor: "#ccd6eb",
      gridLineColor: "#e6e6e6",
      alternateGridColor: null,
      minorGridLineColor: "#f2f2f2",
      minorTickColor: "#999999",
      tickColor: "#ccd6eb",
      labels: { style: { "color": "#666666", "cursor": "default", "fontSize": "11px" } },
      title: { style: { "color": "#666666", "fontSize": "14px" } },
    },
    tooltip: {
      animation: true,
      backgroundColor: "rgba(247,247,247,0.85)",
      borderRadius: 3,
      borderWidth: 1,
      dateTimeLabelFormats: {
        day: "%A, %b %e, %Y",
        hour: "%A, %b %e, %H:%M",
        millisecond: "%A, %b %e, %H:%M:%S.%L",
        minute: "%A, %b %e, %H:%M",
        month: "%B %Y",
        second: "%A, %b %e, %H:%M:%S",
        week: "Week from %A, %b %e, %Y",
        year: "%Y",
      },
      enabled: true,
      footerFormat: "",
      headerFormat: "<span style=\"font-size: 10px\">{point.key}</span><br/>",
      padding: 8,
      pointFormat: "<span style=\"color:{point.color}\">●</span> {series.name}: <b>{point.y}</b><br/>",
      shadow: true,
      snap: 10,
      style: {
        color: "#333333",
        cursor: "default",
        fontSize: "12px",
        pointerEvents: "none",
        whiteSpace: "nowrap"
      }
    },
    toolbar: null,
    plotOptions: {
      series: null,
      line: {
        dataLabels: {
          color: null
        },
        marker: {
          lineColor: "#ffffff"
        }
      },
      spline: {
        marker: {
          lineColor: "#ffffff"
        }
      },
      scatter: {
        marker: {
          lineColor: "#ffffff"
        }
      },
      candlestick: {
        lineColor: "#000000"
      }
    },
    legend: {
      align: "center",
      alignColumns: true,
      backgroundColor: 'rgba(0,0,0,0.03)',
      borderColor: "#999999",
      borderRadius: 0,
      enabled: true,
      itemCheckboxStyle: {
        height: "13px",
        position: "absolute",
        width: "13px"
      },
      itemHiddenStyle: {
        color: "#cccccc",
      },
      itemHoverStyle: {
        color: "#000000"
      },
      itemStyle: {
        color: "#333333",
        cursor: "pointer",
        font: null,
        fontSize: "12px",
        fontWeight: "bold",
        textOverflow: "ellipsis"
      },
      navigation: {
        activeColor: '#003399',
        inactiveColor: '#cccccc',
        animation: true,
        arrowSize: 12,
        style: {
          fontWeight: 'bold',
          color: '#333',
          fontSize: '12px'
        }
      },
      layout: "horizontal",
      padding: 5,
      shadow: false,
      squareSymbol: true,
      symbolPadding: 5,
      title: {
        style: {
          fontSize: '12px',
          fontWeight: "bold"
        }
      },
      verticalAlign: "bottom",
      x: 0,
      y: 0,
    },
    credits: {
      enabled: true,
      href: "https://www.highcharts.com?credits",
      position: {
        align: "right",
        verticalAlign: "bottom",
        x: -10,
        y: -5
      },
      style: {
        color: "#999999",
        cursor: "pointer",
        fontSize: "9px",
      },
      text: "Highcharts.com"
    },
    labels: {
      style: {
        fontSize: "11px",
        color: "#333333",
        position: "absolute"
      }
    },
    lang: {},
    navigation: {
      buttonOptions: {
        symbolStroke: "#666666",
        hoverSymbolStroke: null,
        theme: {
          padding: 5,
          fill: null,
          stroke: null
        }
      },
      menuItemHoverStyle: {
        background: "#335cad",
        color: "#000000",
      },
      menuItemStyle: {
        color: "#666666"
      },
      menuStyle: {
        background: "#ffffff",
        border: "#999999",
      }
    },
    rangeSelector: {
      buttonTheme: { // styles for the buttons
        fill: 'none',
        stroke: 'none', 'stroke-width': 0,
        r: 5,
        width: null,
        style: {
          fontSize: "14px",
          color: '#666666',
          fontWeight: 'bold'
        }
      },
      buttonSpacing: 5,
      inputBoxBorderColor: "none",
      inputStyle: null,
      labelStyle: {
        color: "#666666",
        display: 'none'
      }
    },
    navigator: {
      handles: {
        backgroundColor: "#f2f2f2",
        borderColor: "#999999"
      },
      height: 40,
      outlineColor: "#cccccc",
      maskFill: "rgba(102,133,194,0.3)",
      series: {
        color: null,
        lineColor: null
      },
      xAxis: {
        gridLineColor: "#e6e6e6"
      }
    },
    scrollbar: {
      barBackgroundColor: "#cccccc",
      barBorderColor: "#cccccc",
      barBorderRadius: 0,
      barBorderWidth: 1,
      buttonArrowColor: "#333333",
      buttonBackgroundColor: "#e6e6e6",
      buttonBorderColor: "#cccccc",
      buttonBorderRadius: 0,
      buttonBorderWidth: 1,
      height: 14,
      margin: 10,
      minWidth: 6,
      rifleColor: "#333333",
      step: 0.2,
      trackBackgroundColor: "#f2f2f2",
      trackBorderColor: "#f2f2f2",
      trackBorderWidth: 1,
    },
    drilldown: null,
    colorAxis: null,
    legendBackgroundColor: null,
    background2: "#ffffff",
    dataLabelsColor: null,
    textColor: "#777",
    maskColor: null
  },

// ********
  'standard-dark': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#ffffff", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    chart: {
      backgroundColor: "#000000",
      borderColor: "#335cad",
      plotBorderColor: "#000000",
    },
    title: {
      style: { "color": "#C0C0C0" },
    },
    subtitle: {
      style: { "color": "#666666" },
    },
    xAxis: {
      lineColor: "#A0A0A0",
      gridLineColor: "#333333",
      minorGridLineColor: "#666666",
      minorTickColor: "#666666",
      tickColor: "#A0A0A0",
      labels: { style: { "color": "#A0A0A0" } },
      title: { style: { "color": "#A0A0A0" } },
    },
    yAxis: {
      lineColor: "#A0A0A0",
      gridLineColor: "#333333",
      minorGridLineColor: "#666666",
      minorTickColor: "#666666",
      tickColor: "#A0A0A0",
      labels: { style: { "color": "#A0A0A0" } },
      title: { style: { "color": "#A0A0A0" } },
    },
    tooltip: {
      backgroundColor: "rgba(0, 0, 0, 0.75)",
      style: { color: "#333333" }
    },
    toolbar: { itemStyle: { color: 'silver' } }, 
    plotOptions: {
      line: { 
        dataLabels: { color: '#CCC' },
        marker: { lineColor: "#333" } 
      },
      spline: { marker: { lineColor: "#333" } },
      scatter: { marker: { lineColor: "#333" } },
      candlestick: { lineColor: "#000000" }
    },
    legend: {
      backgroundColor: 'rgba(0,0,0,0.03)',
      borderColor: "#999999",
      itemHiddenStyle: { color: "#333333" },
      itemHoverStyle: { color: "#A0A0A0" },
      itemStyle: { color: "#cccccc" },
      navigation: {
        activeColor: '#003399',
        inactiveColor: '#cccccc',
        style: { color: '#333' }
      },
    },
    credits: { style: { color: "#666" } },
    labels: { style: { color: "#CCC" } },
    navigation: {
      annotationsOptions: {
        labelOptions: {
          backgroundColor: 'rgba(200,200,200,0.75)',
          borderColor: 'rgba(255, 255, 255, 0.75)'
        },
        shapeOptions: {
          fill: 'rgba(255, 255, 255, 0.75)',
          stroke: 'rgba(255, 255, 255, 0.75)'
        },
      },
      buttonOptions: {
        symbolStroke: "#DDDDDD",
        hoverSymbolStroke: '#FFFFFF',
        theme: {
          fill: {
            linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
            stops: [
              [0.4, '#606060'],
              [0.6, '#333333']
            ]
          },
          stroke: '#000000'
        }
      },
      menuItemHoverStyle: {
        background: "#335cad",
        color: "#333333",
      },
      menuItemStyle: {
        color: "#C0C0C0"
      },
      menuStyle: {
        background: "#000000",
        border: "#999999",
      }
    },
    rangeSelector: {
      buttonTheme: {
        fill: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0.4, '#888'],
            [0.6, '#555']
          ]
        },
        stroke: '#000000',
        style: {
          color: '#CCC',
          fontWeight: 'bold'
        }
      },
      inputStyle: {
        backgroundColor: '#333',
        color: 'silver'
      },
      labelStyle: { color: "silver" }
    },
    navigator: {
      handles: {
        backgroundColor: "#000000",
        borderColor: "#999999"
      },
      outlineColor: "#cccccc",
      maskFill: "rgba(102,133,194,0.3)",
      xAxis: { gridLineColor: "#e6e6e6" }
    },
    scrollbar: {
      barBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      barBorderColor: '#CCC',
      buttonArrowColor: '#CCC',
      buttonBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      buttonBorderColor: '#CCC',
      rifleColor: '#FFF',
      trackBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0, '#000'],
          [1, '#333']
        ]
      },
      trackBorderColor: '#666',
    },
    legendBackgroundColor: 'rgb(255,255,255)',
    background2: 'rgb(0,0,0)',
    dataLabelsColor: '#444',
    textColor: '#C0C0C0',
    maskColor: 'rgba(255,255,255,0.3)'
  },

// ********
  'dark-green': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    chart: {
      backgroundColor: {
        linearGradient: [0, 0, 250, 500],
        stops: [
          [0, 'rgb(48, 96, 48)'],
          [1, 'rgb(0, 0, 0)']
        ]
      },
      borderColor: '#000000',
      borderWidth: 2,
      className: 'dark-container',
      plotBackgroundColor: 'rgba(255, 255, 255, .1)',
      plotBorderColor: '#CCCCCC',
      plotBorderWidth: 1
    },
    title: {
      style: {
        color: '#C0C0C0',
        font: 'bold 16px "Trebuchet MS", Verdana, sans-serif'
      }
    },
    subtitle: {
      style: {
        color: '#666666',
        font: 'bold 12px "Trebuchet MS", Verdana, sans-serif'
      }
    },
    xAxis: {
      gridLineColor: '#333333',
      labels: {
        style: {
          color: '#A0A0A0'
        }
      },
      lineColor: '#A0A0A0',
      tickColor: '#A0A0A0',
      title: {
        style: {
          color: '#CCC',
          fontWeight: 'bold',
          fontSize: '12px',
          fontFamily: 'Trebuchet MS, Verdana, sans-serif'

        }
      }
    },
    yAxis: {
      gridLineColor: '#333333',
      labels: {
        style: {
          color: '#A0A0A0'
        }
      },
      lineColor: '#A0A0A0',
      tickColor: '#A0A0A0',
      tickWidth: 1,
      title: {
        style: {
          color: '#CCC',
          fontWeight: 'bold',
          fontSize: '12px',
          fontFamily: 'Trebuchet MS, Verdana, sans-serif'
        }
      }
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.75)',
      style: {
        color: '#F0F0F0'
      }
    },
    toolbar: {
      itemStyle: {
        color: 'silver'
      }
    },
    plotOptions: {
      line: {
        dataLabels: {
          color: '#CCC'
        },
        marker: {
          lineColor: '#333'
        }
      },
      spline: {
        marker: {
          lineColor: '#333'
        }
      },
      scatter: {
        marker: {
          lineColor: '#333'
        }
      },
      candlestick: {
        lineColor: 'white'
      }
    },
    legend: {
      backgroundColor: 'rgba(0,0,0,0.3)',
      itemStyle: {
        font: '9pt Trebuchet MS, Verdana, sans-serif',
        color: '#FFFFFF'
      },
      itemHoverStyle: {
        color: '#000000',
        fontWeight: 'bold'
      },
      itemHiddenStyle: {
        color: '#A0A0A0'
      }
    },
    credits: {
      style: {
        color: '#666'
      }
    },
    labels: {
      style: {
        color: '#CCC'
      }
    },


    navigation: {
      buttonOptions: {
        symbolStroke: '#DDDDDD',
        hoverSymbolStroke: '#FFFFFF',
        theme: {
          fill: {
            linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
            stops: [
              [0.4, '#606060'],
              [0.6, '#333333']
            ]
          },
          stroke: '#000000'
        }
      },
      menuItemHoverStyle: {
        background: "#335cad",
        color: "#333333",
      },
      menuItemStyle: {
        color: "#C0C0C0"
      },
      menuStyle: {
        background: "#000000",
        border: "#999999",
      }
    },

    // scroll charts
    rangeSelector: {
      buttonTheme: {
        fill: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0.4, '#888'],
            [0.6, '#555']
          ]
        },
        stroke: '#000000',
        style: {
          color: '#CCC',
          fontWeight: 'bold'
        }
      },
      inputStyle: {
        backgroundColor: '#333',
        color: 'silver'
      },
      labelStyle: {
        color: 'silver'
      }
    },

    navigator: {
      handles: {
        backgroundColor: '#666',
        borderColor: '#AAA'
      },
      outlineColor: '#CCC',
      maskFill: 'rgba(16, 16, 16, 0.5)',
      series: {
        color: '#7798BF',
        lineColor: '#A6C7ED'
      }
    },

    scrollbar: {
      barBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      barBorderColor: '#CCC',
      buttonArrowColor: '#CCC',
      buttonBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      buttonBorderColor: '#CCC',
      rifleColor: '#FFF',
      trackBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0, '#000'],
          [1, '#333']
        ]
      },
      trackBorderColor: '#666'
    },

    // special colors for some of the
    legendBackgroundColor: 'rgb(255,255,255)',
    background2: 'rgb(18, 32, 16)',
    dataLabelsColor: '#444',
    textColor: '#C0C0C0',
    maskColor: 'rgba(255,255,255,0.3)'
  },

// ********
  'avocado': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    colorAxis: {
      maxColor: '#05426E',
      minColor: '#F3E796'
    },

    navigator: {
      maskFill: 'rgba(170, 205, 170, 0.5)',
      series: {
        color: '#95C471',
        lineColor: '#35729E'
      }
    }
  },

// ********
  'dark-blue': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    chart: {
      backgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
        stops: [
          [0, 'rgb(48, 48, 96)'],
          [1, 'rgb(0, 0, 0)']
        ]
      },
      borderColor: '#000000',
      borderWidth: 2,
      className: 'dark-container',
      plotBackgroundColor: 'rgba(255, 255, 255, .1)',
      plotBorderColor: '#CCCCCC',
      plotBorderWidth: 1
    },
    title: {
      style: {
        color: '#C0C0C0',
        font: 'bold 16px "Trebuchet MS", Verdana, sans-serif'
      }
    },
    subtitle: {
      style: {
        color: '#666666',
        font: 'bold 12px "Trebuchet MS", Verdana, sans-serif'
      }
    },
    xAxis: {
      gridLineColor: '#333333',
      labels: {
        style: {
          color: '#A0A0A0'
        }
      },
      lineColor: '#A0A0A0',
      tickColor: '#A0A0A0',
      title: {
        style: {
          color: '#CCC',
          fontWeight: 'bold',
          fontSize: '12px',
          fontFamily: 'Trebuchet MS, Verdana, sans-serif'

        }
      }
    },
    yAxis: {
      gridLineColor: '#333333',
      labels: {
        style: {
          color: '#A0A0A0'
        }
      },
      lineColor: '#A0A0A0',
      tickColor: '#A0A0A0',
      tickWidth: 1,
      title: {
        style: {
          color: '#CCC',
          fontWeight: 'bold',
          fontSize: '12px',
          fontFamily: 'Trebuchet MS, Verdana, sans-serif'
        }
      }
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.75)',
      style: {
        color: '#F0F0F0'
      }
    },
    toolbar: {
      itemStyle: {
        color: 'silver'
      }
    },
    plotOptions: {
      line: {
        dataLabels: {
          color: '#CCC'
        },
        marker: {
          lineColor: '#333'
        }
      },
      spline: {
        marker: {
          lineColor: '#333'
        }
      },
      scatter: {
        marker: {
          lineColor: '#333'
        }
      },
      candlestick: {
        lineColor: 'white'
      }
    },
    legend: {
      backgroundColor: 'rgba(0,0,0,0.3)',
      itemStyle: {
        font: '9pt Trebuchet MS, Verdana, sans-serif',
        color: '#FFF'
      },
      itemHoverStyle: {
        color: '#000000',
        fontWeight: 'bold'
      },
      itemHiddenStyle: {
        color: '#A0A0A0'
      }
    },
    credits: {
      style: {
        color: '#666'
      }
    },
    labels: {
      style: {
        color: '#CCC'
      }
    },
    navigation: {
      buttonOptions: {
        symbolStroke: '#DDDDDD',
        hoverSymbolStroke: '#FFFFFF',
        theme: {
          fill: {
            linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
            stops: [
              [0.4, '#606060'],
              [0.6, '#333333']
            ]
          },
          stroke: '#000000'
        }
      },
      menuItemHoverStyle: {
        background: "#335cad",
        color: "#333333",
      },
      menuItemStyle: {
        color: "#C0C0C0"
      },
      menuStyle: {
        background: "#000000",
        border: "#999999",
      }
    },
    rangeSelector: {
      buttonTheme: {
        fill: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0.4, '#888'],
            [0.6, '#555']
          ]
        },
        stroke: '#000000',
        style: {
          color: '#CCC',
          fontWeight: 'bold'
        }
      },
      inputStyle: {
        backgroundColor: '#333',
        color: 'silver'
      },
      labelStyle: {
        color: 'silver'
      },
      y: 100
    },
    navigator: {
      handles: {
        backgroundColor: '#666',
        borderColor: '#AAA'
      },
      outlineColor: '#CCC',
      maskFill: 'rgba(16, 16, 16, 0.5)',
      series: {
        color: '#7798BF',
        lineColor: '#A6C7ED'
      }
    },
    scrollbar: {
      barBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      barBorderColor: '#CCC',
      buttonArrowColor: '#CCC',
      buttonBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      buttonBorderColor: '#CCC',
      rifleColor: '#FFF',
      trackBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0, '#000'],
          [1, '#333']
        ]
      },
      trackBorderColor: '#666'
    },

    // special colors for some of the
    legendBackgroundColor: 'rgba(0, 0, 0, 0.5)',
    background2: 'rgb(35, 35, 70)',
    dataLabelsColor: '#444',
    textColor: '#C0C0C0',
    maskColor: 'rgba(255,255,255,0.3)'
  },

// ********
  'dark-unica': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    chart: {
      backgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
        stops: [
          [0, '#2a2a2b'],
          [1, '#3e3e40']
        ]
      },
      style: {
        fontFamily: '\'Unica One\', sans-serif'
      },
      plotBorderColor: '#606063'
    },
    title: {
      style: {
        color: '#E0E0E3',
        textTransform: 'uppercase',
        fontSize: '20px'
      }
    },
    subtitle: {
      style: {
        color: '#E0E0E3',
        textTransform: 'uppercase'
      }
    },
    xAxis: {
      gridLineColor: '#707073',
      labels: {
        style: {
          color: '#E0E0E3'
        }
      },
      lineColor: '#707073',
      minorGridLineColor: '#505053',
      tickColor: '#707073',
      title: {
        style: {
          color: '#A0A0A3'

        }
      }
    },
    yAxis: {
      gridLineColor: '#707073',
      labels: {
        style: {
          color: '#E0E0E3'
        }
      },
      lineColor: '#707073',
      minorGridLineColor: '#505053',
      tickColor: '#707073',
      tickWidth: 1,
      title: {
        style: {
          color: '#A0A0A3'
        }
      }
    },
    tooltip: {
      backgroundColor: 'rgba(0, 0, 0, 0.85)',
      style: {
        color: '#F0F0F0'
      }
    },
    plotOptions: {
      series: {
        dataLabels: {
          color: '#B0B0B3'
        },
        marker: {
          lineColor: '#333'
        }
      }
    },
    legend: {
      backgroundColor: 'rgba(0,0,0,0.3)',
      itemStyle: {
        color: '#E0E0E3'
      },
      itemHoverStyle: {
        color: '#A0A0A0'
      },
      itemHiddenStyle: {
        color: '#606063'
      }
    },
    credits: {
      style: {
        color: '#666'
      }
    },
    labels: {
      style: {
        color: '#707073'
      }
    },

    drilldown: {
      activeAxisLabelStyle: {
        color: '#F0F0F3'
      },
      activeDataLabelStyle: {
        color: '#F0F0F3'
      }
    },

    navigation: {
      buttonOptions: {
        symbolStroke: '#DDDDDD',
        theme: {
          fill: '#505053'
        }
      },
      menuItemHoverStyle: {
        background: "#335cad",
        color: "#333333",
      },
      menuItemStyle: {
        color: "#C0C0C0"
      },
      menuStyle: {
        background: "#000000",
        border: "#999999",
      }
    },

    // scroll charts
    rangeSelector: {
      buttonTheme: {
        fill: '#505053',
        stroke: '#000000',
        style: {
          color: '#CCC'
        }
      },
      inputBoxBorderColor: '#505053',
      inputStyle: {
        backgroundColor: '#333',
        color: 'silver'
      },
      labelStyle: {
        color: 'silver'
      }
    },

    navigator: {
      handles: {
        backgroundColor: '#666',
        borderColor: '#AAA'
      },
      outlineColor: '#CCC',
      maskFill: 'rgba(255,255,255,0.1)',
      series: {
        color: '#7798BF',
        lineColor: '#A6C7ED'
      },
      xAxis: {
        gridLineColor: '#505053'
      }
    },

    scrollbar: {
      barBackgroundColor: '#808083',
      barBorderColor: '#808083',
      buttonArrowColor: '#CCC',
      buttonBackgroundColor: '#606063',
      buttonBorderColor: '#606063',
      rifleColor: '#FFF',
      trackBackgroundColor: '#404043',
      trackBorderColor: '#404043'
    },

    // special colors for some of the
    legendBackgroundColor: 'rgba(0, 0, 0, 0.5)',
    background2: '#505053',
    dataLabelsColor: '#B0B0B3',
    textColor: '#C0C0C0',
    contrastTextColor: '#F0F0F3',
    maskColor: 'rgba(255,255,255,0.3)'
  },

// ********
  'gray': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    chart: {
      backgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0, 'rgb(96, 96, 96)'],
          [1, 'rgb(16, 16, 16)']
        ]
      },
      borderWidth: 0,
      borderRadius: 0,
      plotBackgroundColor: null,
      plotShadow: false,
      plotBorderWidth: 0
    },
    title: {
      style: {
        color: '#FFF',
        font: '16px Lucida Grande, Lucida Sans Unicode,' +
          ' Verdana, Arial, Helvetica, sans-serif'
      }
    },
    subtitle: {
      style: {
        color: '#DDD',
        font: '12px Lucida Grande, Lucida Sans Unicode,' +
          ' Verdana, Arial, Helvetica, sans-serif'
      }
    },
    xAxis: {
      lineColor: '#999',
      tickColor: '#999',
      labels: {
        style: {
          color: '#999',
          fontWeight: 'bold'
        }
      },
      title: {
        style: {
          color: '#AAA',
          font: 'bold 12px Lucida Grande, Lucida Sans Unicode,' +
            ' Verdana, Arial, Helvetica, sans-serif'
        }
      }
    },
    yAxis: {
      gridLineColor: 'rgba(255, 255, 255, .1)',
      minorGridLineColor: 'rgba(255,255,255,0.07)',
      lineWidth: 0,
      tickWidth: 0,
      labels: {
        style: {
          color: '#999',
          fontWeight: 'bold'
        }
      },
      title: {
        style: {
          color: '#AAA',
          font: 'bold 12px Lucida Grande, Lucida Sans Unicode,' +
            ' Verdana, Arial, Helvetica, sans-serif'
        }
      }
    },
    legend: {
      itemStyle: {
        color: '#808080'
      },
      itemHoverStyle: {
        color: '#000000',
      },
      itemHiddenStyle: {
        color: '#A0A0A0'
      }
    },
    labels: {
      style: {
        color: '#CCC'
      }
    },
    tooltip: {
      backgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0, 'rgba(96, 96, 96, .8)'],
          [1, 'rgba(16, 16, 16, .8)']
        ]
      },
      borderWidth: 0,
      style: {
        color: '#FFF'
      }
    },


    plotOptions: {
      series: {
        nullColor: '#444444'
      },
      line: {
        dataLabels: {
          color: '#CCC'
        },
        marker: {
          lineColor: '#333'
        }
      },
      spline: {
        marker: {
          lineColor: '#333'
        }
      },
      scatter: {
        marker: {
          lineColor: '#333'
        }
      },
      candlestick: {
        lineColor: 'white'
      }
    },

    toolbar: {
      itemStyle: {
        color: '#CCC'
      }
    },

    navigation: {
      buttonOptions: {
        symbolStroke: '#DDDDDD',
        hoverSymbolStroke: '#FFFFFF',
        theme: {
          fill: {
            linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
            stops: [
              [0.4, '#606060'],
              [0.6, '#333333']
            ]
          },
          stroke: '#000000'
        }
      },
      menuItemHoverStyle: {
        background: "#335cad",
        color: "#333333",
      },
      menuItemStyle: {
        color: "#C0C0C0"
      },
      menuStyle: {
        background: "#000000",
        border: "#999999",
      }
    },

    // scroll charts
    rangeSelector: {
      buttonTheme: {
        fill: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0.4, '#888'],
            [0.6, '#555']
          ]
        },
        stroke: '#000000',
        style: {
          color: '#CCC',
          fontWeight: 'bold'
        }
      },
      inputStyle: {
        backgroundColor: '#333',
        color: 'silver'
      },
      labelStyle: {
        color: 'silver'
      }
    },

    navigator: {
      handles: {
        backgroundColor: '#666',
        borderColor: '#AAA'
      },
      outlineColor: '#CCC',
      maskFill: 'rgba(16, 16, 16, 0.5)',
      series: {
        color: '#7798BF',
        lineColor: '#A6C7ED'
      }
    },

    scrollbar: {
      barBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      barBorderColor: '#CCC',
      buttonArrowColor: '#CCC',
      buttonBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      buttonBorderColor: '#CCC',
      rifleColor: '#FFF',
      trackBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0, '#000'],
          [1, '#333']
        ]
      },
      trackBorderColor: '#666'
    },

    // special colors for some of the demo examples
    legendBackgroundColor: 'rgba(48, 48, 48, 0.8)',
    background2: 'rgb(70, 70, 70)',
    dataLabelsColor: '#444',
    textColor: '#E0E0E0',
    maskColor: 'rgba(255,255,255,0.3)'
  },

// ********
  'grid-light': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    chart: {
      backgroundColor: null,
      style: {
        fontFamily: 'Dosis, sans-serif'
      }
    },
    title: {
      style: {
        fontSize: '16px',
        fontWeight: 'bold',
        textTransform: 'uppercase'
      }
    },
    tooltip: {
      borderWidth: 0,
      backgroundColor: 'rgba(219,219,216,0.8)',
      shadow: false
    },
    legend: {
      itemStyle: {
        fontWeight: 'bold',
        fontSize: '13px'
      }
    },
    xAxis: {
      labels: {
        style: {
          fontSize: '12px'
        }
      }
    },
    yAxis: {
      title: {
        style: {
          textTransform: 'uppercase'
        }
      },
      labels: {
        style: {
          fontSize: '12px'
        }
      }
    },

    // General
    background2: '#F0F0EA'

  },

// ********
  'grid': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    chart: {
      backgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
        stops: [
          [0, 'rgb(255, 255, 255)'],
          [1, 'rgb(240, 240, 255)']
        ]
      },
      borderWidth: 2,
      plotBackgroundColor: 'rgba(255, 255, 255, .9)',
      plotShadow: true,
      plotBorderWidth: 1
    },
    title: {
      style: {
        color: '#000',
        font: 'bold 16px "Trebuchet MS", Verdana, sans-serif'
      }
    },
    subtitle: {
      style: {
        color: '#666666',
        font: 'bold 12px "Trebuchet MS", Verdana, sans-serif'
      }
    },
    xAxis: {
      lineColor: '#000',
      tickColor: '#000',
      labels: {
        style: {
          color: '#000',
          font: '11px Trebuchet MS, Verdana, sans-serif'
        }
      },
      title: {
        style: {
          color: '#333',
          fontWeight: 'bold',
          fontSize: '12px',
          fontFamily: 'Trebuchet MS, Verdana, sans-serif'

        }
      }
    },
    yAxis: {
      lineColor: '#000',
      lineWidth: 1,
      tickWidth: 1,
      tickColor: '#000',
      labels: {
        style: {
          color: '#000',
          font: '11px Trebuchet MS, Verdana, sans-serif'
        }
      },
      title: {
        style: {
          color: '#333',
          fontWeight: 'bold',
          fontSize: '12px',
          fontFamily: 'Trebuchet MS, Verdana, sans-serif'
        }
      }
    },
    legend: {
      itemStyle: {
        font: '9pt Trebuchet MS, Verdana, sans-serif',
        color: 'black'

      },
      itemHoverStyle: {
        color: '#039'
      },
      itemHiddenStyle: {
        color: 'gray'
      }
    },
    labels: {
      style: {
        color: '#99b'
      }
    },

    navigation: {
      buttonOptions: {
        theme: {
          stroke: '#CCCCCC'
        }
      }
    },
    background2: '#F0F0EA'
  },

// ********
  'skies': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],

    chart: {
      className: 'skies',
      borderWidth: 0,
      plotShadow: false,
      plotBackgroundImage: 'skies.jpg',
      plotBorderWidth: 1
    },
    title: {
      style: {
        color: '#3E576F',
        font: '16px Lucida Grande, Lucida Sans Unicode,' +
          ' Verdana, Arial, Helvetica, sans-serif'
      }
    },
    subtitle: {
      style: {
        color: '#6D869F',
        font: '12px Lucida Grande, Lucida Sans Unicode,' +
          ' Verdana, Arial, Helvetica, sans-serif'
      }
    },
    xAxis: {
      lineColor: '#C0D0E0',
      tickColor: '#C0D0E0',
      labels: {
        style: {
          color: '#666',
          fontWeight: 'bold'
        }
      },
      title: {
        style: {
          color: '#666',
          font: '12px Lucida Grande, Lucida Sans Unicode,' +
            ' Verdana, Arial, Helvetica, sans-serif'
        }
      }
    },
    yAxis: {
      lineColor: '#C0D0E0',
      tickColor: '#C0D0E0',
      tickWidth: 1,
      labels: {
        style: {
          color: '#666',
          fontWeight: 'bold'
        }
      },
      title: {
        style: {
          color: '#666',
          font: '12px Lucida Grande, Lucida Sans Unicode,' +
            ' Verdana, Arial, Helvetica, sans-serif'
        }
      }
    },
    legend: {
      itemStyle: {
        font: '9pt Trebuchet MS, Verdana, sans-serif',
        color: '#3E576F'
      },
      itemHoverStyle: {
        color: 'black'
      },
      itemHiddenStyle: {
        color: 'silver'
      }
    },
    labels: {
      style: {
        color: '#3E576F'
      }
    },
    background2: '#F0F0EA'
  },

// ********
  'sunset': {
    colors: ["#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1",
      "#000000", "#663300", "#000066", "#4B0082", "#ff0000", "#8B008B", "#ff4000", "#ff8000", "#ffbf00", "#ffff00",
      "#bfff00", "#80ff00", "#40ff00", "#00ff00", "#00ff40", "#00ff80", "#00ffbf", "#00ffff", "#00bfff", "#0080ff",
      "#0040ff", "#0000ff", "#4000ff", "#8000ff", "#bf00ff", "#ff00ff", "#ff00bf", "#ff0080", "#ff0040", "#ff0000"],
    colorAxis: {
      maxColor: '#60042E',
      minColor: '#FDD089'
    },

    navigator: {
      series: {
        color: '#FF7F79',
        lineColor: '#A0446E'
      }
    }
  },

// ********
  'transparent': {
    chart: {
      backgroundColor: 'rgba(255, 255, 255, 0.0)',
      plotBackgroundColor: 'rgba(255,255,255, .1)',
      plotShadow: false,
      style: {
        fontFamily: 'Dosis, sans-serif'
      }
    },
    tooltip: {
      backgroundColor: 'rgba(247,247,247,0.7)'
    },
    legend: {
      borderColor: null,
      borderRadius: 0,
      borderWidth: 0,
      // backgroundColor not working,
      backgroundColor2: 'rgba(255, 255, 255, 0.0)'
    },
  },
  'dark-transp': {
    chart: {
      backgroundColor: 'rgba(255, 255, 255, 0.0)',
      plotBackgroundColor: 'rgba(255,255,255, .1)',
      plotShadow: false,
      style: {
        fontFamily: 'Dosis, sans-serif'
      }
    },
    title: {
      style: {
        color: '#C0C0C0',
      }
    },
    subtitle: {
      style: {
        color: '#666666',
      }
    },
    xAxis: {
      gridLineColor: '#333333',
      labels: {
        style: {
          color: '#A0A0A0'
        }
      },
      lineColor: '#A0A0A0',
      tickColor: '#A0A0A0',
      title: {
        style: {
          color: '#CCC',
          fontWeight: 'bold',
          fontSize: '12px',
          fontFamily: 'Trebuchet MS, Verdana, sans-serif'
        }
      }
    },
    yAxis: {
      gridLineColor: '#333333',
      labels: {
        style: {
          color: '#A0A0A0'
        }
      },
      lineColor: '#A0A0A0',
      tickColor: '#A0A0A0',
      tickWidth: 1,
      title: {
        style: {
          color: '#CCC',
          fontWeight: 'bold',
          fontSize: '12px',
          fontFamily: 'Trebuchet MS, Verdana, sans-serif'
        }
      }
    },
    tooltip: {
      backgroundColor: 'rgba(247,247,247,0.7)',
      style: {
        color: '#F0F0F0'
      }
    },
    toolbar: {
      itemStyle: {
        color: 'silver'
      }
    },
    plotOptions: {
      line: {
        dataLabels: {
          color: '#CCC'
        },
        marker: {
          lineColor: '#333'
        }
      },
      spline: {
        marker: {
          lineColor: '#333'
        }
      },
      scatter: {
        marker: {
          lineColor: '#333'
        }
      },
      candlestick: {
        lineColor: 'white'
      }
    },
    legend: {
      borderColor: null,
      borderRadius: 0,
      borderWidth: 0,
      // backgroundColor not working,
      backgroundColor2: 'rgba(255, 255, 255, 0.0)',
      itemStyle: {
        color: '#A0A0A0'
      },
      itemHoverStyle: {
        color: '#FFFFFF',
        fontWeight: 'bold'
      },
      itemHiddenStyle: {
        color: '#606060'
      }
    },
    credits: {
      style: {
        color: '#666'
      }
    },
    labels: {
      style: {
        color: '#CCC'
      }
    },
    navigation: {
      buttonOptions: {
        symbolStroke: '#DDDDDD',
        hoverSymbolStroke: '#FFFFFF',
        theme: {
          fill: {
            linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
            stops: [
              [0.4, '#606060'],
              [0.6, '#333333']
            ]
          },
          stroke: '#000000'
        }
      },
      menuItemHoverStyle: {
        background: "#335cad",
        color: "#333333",
      },
      menuItemStyle: {
        color: "#C0C0C0"
      },
      menuStyle: {
        background: "#000000",
        border: "#999999",
      }
    },

    // scroll charts
    rangeSelector: {
      buttonTheme: {
        fill: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0.4, '#888'],
            [0.6, '#555']
          ]
        },
        stroke: '#000000',
        style: {
          color: '#CCC',
          fontWeight: 'bold'
        }
      },
      inputStyle: {
        backgroundColor: '#333',
        color: 'silver'
      },
      labelStyle: {
        color: 'silver'
      }
    },

    navigator: {
      handles: {
        backgroundColor: '#666',
        borderColor: '#AAA'
      },
      outlineColor: '#CCC',
      maskFill: 'rgba(16, 16, 16, 0.5)',
      series: {
        color: '#7798BF',
        lineColor: '#A6C7ED'
      }
    },

    scrollbar: {
      barBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      barBorderColor: '#CCC',
      buttonArrowColor: '#CCC',
      buttonBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0.4, '#888'],
          [0.6, '#555']
        ]
      },
      buttonBorderColor: '#CCC',
      rifleColor: '#FFF',
      trackBackgroundColor: {
        linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
        stops: [
          [0, '#000'],
          [1, '#333']
        ]
      },
      trackBorderColor: '#666'
    },
    // special colors for some of the
    dataLabelsColor: '#444',
    textColor: '#C0C0C0',
    maskColor: 'rgba(255,255,255,0.3)'
  },
};