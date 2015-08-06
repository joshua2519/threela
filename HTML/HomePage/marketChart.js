

    $(function () {
        //大盤
        $.getJSON('marketChart.php', function (data) {

            //修改colum条的颜色
            var originalDrawPoints = Highcharts.seriesTypes.column.prototype.drawPoints;  
            Highcharts.seriesTypes.column.prototype.drawPoints = function () {  
                var merge  = Highcharts.merge,  
                    series = this,  
                    chart  = this.chart,  
                    points = series.points,  
                    i      = points.length;  
                  
                while (i--) {  
                    var candlePoint = chart.series[0].points[i];  
                    if(candlePoint.open != undefined && candlePoint.close !=  undefined){  //如果是K线图 改变矩形条颜色，否则不变  
                        var color = (candlePoint.open < candlePoint.close) ? '#DD2200' : '#33AA22';  
                        var seriesPointAttr = merge(series.pointAttr);  
                        seriesPointAttr[''].fill = color;  
                        seriesPointAttr.hover.fill = Highcharts.Color(color).brighten(0.3).get();  
                        seriesPointAttr.select.fill = color;  
                    }else{  
                        var seriesPointAttr = merge(series.pointAttr);  
                    }  
                      
                    points[i].pointAttr = seriesPointAttr;  
                }  
          
                originalDrawPoints.call(this);  
            }           
            

            // split the data set into ohlc and volume
            var ohlc = [],
                volume = [],
                dataLength = data.length,
                // set the allowed units for data grouping
                groupingUnits = [ ['day',[1]]
                ],

                i = 0;

            for (i; i < dataLength; i += 1) {
                ohlc.push([
                    data[i][0], // the date
                    data[i][1], // open
                    data[i][2], // high
                    data[i][3], // low
                    data[i][4] // close
                ]);

                volume.push([
                    data[i][0], // the date
                    data[i][5] // the volume
                ]);
            }


            // create the chart
            $('#marketChart').highcharts('StockChart', {
                chart:{backgroundColor:'#aaa'},
                 tooltip:{
                style: {
                color: '#333333',
                fontSize: '12px',
                padding: '18px',
                opacity:0.5,
                backgroundColor:'#aaa'
                },
                borderColor: '',
                borderWidth: 0

                },
                rangeSelector: {
                    selected: 2
                },

                title: {
                    text: 'Stock Historical'
                },

                yAxis: [{
                    labels: {
                        align: 'right',
                        x: -3
                    },
                    title: {
                        text: '指數'
                    },
                    height: '80%',
                    lineWidth: 2
                }, {
                    labels: {
                        align: 'right',
                        x: -3
                    },
                    title: {
                        text: '成交量'
                    },
                    top: '85%',
                    height: '15%',
                    offset: 0,
                    lineWidth: 2
                }],

                series: [{
                    
                    type: 'candlestick',
                    upColor:'#FF0000',
                    color:'#008800',
                    name: 'STD',
                    data: ohlc,
                    dataGrouping: {
                        units: groupingUnits
                    }
                },{
                    type: 'column',
                    
                    name: 'VOL',
                    data: volume,
                    yAxis: 1,
                    dataGrouping: {
                        units: groupingUnits
                    }
                }]
            });
        });
    });