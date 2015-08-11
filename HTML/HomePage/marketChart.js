

    $(function () {
        //大盤
        $.getJSON('marketChart.php', function (data) {

            // split the data set into ohlc and volume
            var ohlc = [],
                volume = [],
                MA10 = [], MA60=[],UBand=[],LBand=[],
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

                MA10.push([
                    data[i][0],
                    data[i][6]
                ]);

                MA60.push([
                    data[i][0],
                    data[i][7]
                ]);

                UBand.push([
                    data[i][0],
                    data[i][8]
                ]);

                LBand.push([
                    data[i][0],
                    data[i][9]
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
                    selected: 4
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
                    negativeColor:'#008800',
                    color:'#FF0000',
                    name: 'VOL',
                    data: volume,
                    yAxis: 1,
                    dataGrouping: {
                        units: groupingUnits
                    }
                },{
                    type: 'line',
                    
                    name: 'MA10',
                    data: MA10,
                     color:'yellow',
                    dataGrouping: {
                        units: groupingUnits
                    }
                },{
                    type: 'line',
                    
                    name: 'MA60',
                    data: MA60,
                    color:'purple',
                    dataGrouping: {
                        units: groupingUnits
                    }
                },{
                    type: 'line',
                    
                    name: 'UBand',
                    data: UBand,
                    color:'cyan',
                    dataGrouping: {
                        units: groupingUnits
                    }
                },{
                    type: 'line',
                    
                    name: 'LBand',
                    data: LBand,
                    color:'cyan',
                    dataGrouping: {
                        units: groupingUnits
                    }
                }]
            });
        });
    });