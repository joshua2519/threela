//個股的chart圖
function GetStockPrice(stockid){

    $.getJSON('makeForPhp.php?enterId='+stockid, function (data) {

        //修改colum条的颜色
        // var originalDrawPoints = Highcharts.seriesTypes.column.prototype.drawPoints;  
        // Highcharts.seriesTypes.column.prototype.drawPoints = function () {  
        //     var merge  = Highcharts.merge,  
        //         series = this,  
        //         chart  = this.chart,  
        //         points = series.points,  
        //         i      = points.length;  
              
        //     while (i--) {  
        //         var candlePoint = chart.series[0].points[i];  
        //         if(candlePoint.open != undefined && candlePoint.close !=  undefined){  //如果是K线图 改变矩形条颜色，否则不变  
        //             var color = (candlePoint.open < candlePoint.close) ? '#DD2200' : '#33AA22';  
        //             var seriesPointAttr = merge(series.pointAttr);  
        //             seriesPointAttr[''].fill = color;  
        //             seriesPointAttr.hover.fill = Highcharts.Color(color).brighten(0.3).get();  
        //             seriesPointAttr.select.fill = color;  
        //         }else{  
        //             var seriesPointAttr = merge(series.pointAttr);  
        //         }  
                  
        //         points[i].pointAttr = seriesPointAttr;  
        //     }  
      
        //     originalDrawPoints.call(this);  
        // }           
        

        // split the data set into ohlc and volume
        var ohlc = [],
            volume = [],
            Ma5=[],Ma20=[],Ma60=[],UBand=[],LBand=[],
            dataLength = data.length,
            // set the allowed units for data grouping
            groupingUnits = [[
                'day',                         // unit name
                [1]                             // allowed multiples
            ]],

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

            Ma5.push([
                data[i][0],
                data[i][6]
            ]);
            Ma20.push([
                data[i][0],
                data[i][7]
            ]);
            Ma60.push([
                data[i][0],
                data[i][8]
            ]);
            UBand.push([
                data[i][0],
                data[i][9]
            ]);
            LBand.push([
                data[i][0],
                data[i][10]
            ]);
        }


        // create the chart
        $('#test_coco').highcharts('StockChart', {
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
                selected: 4
            },

            title: {
                text: 'Stock Historical'
            },

            yAxis: [{
                tickInterval:5,//寬度
                labels: {
                    align: 'right',
                    x: -3
                },
                title: {
                    text: '股價'
                },
                height: '70%',
                lineWidth: 2
            }, {
                labels: {
                    align: 'right',
                    x: -3
                },
                title: {
                    text: '成交量'
                },
                top: '75%',
                height: '25%',
                offset: 0,
                lineWidth: 2
            }],

            series: [{
                
                type: 'candlestick',
                upColor:'#FF0000',
                color:'#008800',
                name: 'STK',
                data: ohlc,
                dataGrouping: {
                    units: groupingUnits
                }
            },{
                type: 'column',
                negativeColor:'#008800',
                color:'#FF0000',
                name: 'BS20',
                data: volume,
                yAxis: 1,
                dataGrouping: {
                    units: groupingUnits
                }
            },{
                type: 'line',
                
                name: 'MA5',
                data: Ma5,
                color:'yellow',
                dataGrouping: {
                    units: groupingUnits
                }
            },{
                type: 'line',
                
                name: 'MA60',
                data: Ma60,
                color:'purple',
                dataGrouping: {
                    units: groupingUnits
                }
            },{
                type: 'line',
                
                name: '+STD',
                data: UBand,
                color:'cyan',
                dataGrouping: {
                    units: groupingUnits
                }
            },{
                type: 'line',
                
                name: '-STD',
                data: LBand,
                color:'cyan',
                dataGrouping: {
                    units: groupingUnits
                }
            }]
        });
    });
}