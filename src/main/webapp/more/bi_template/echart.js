// data中数据个数和labels个数不一定相等，以labels为准，labels有data里没有的默认等于0
function echartLine2D(canvasName, title, data, unitName, func) {
	// 单条折线图
	if(isArray(data)) {
		var _data = {};
		_data[title] = data;
		data = _data;
	}

	var legends = [],
		series = [],
		labels = [];
 
	for(var legend in data) {
		legends.push(legend);
		var legendData = data[legend];
 
		var serieData = [];
		var tempMap = {};
		for(var i = 0; i < legendData.length; i++) {
			var item = legendData[i];
			tempMap[item.name] = item.value;
			
			!labels.contains(item.name) && labels.push(item.name);
		}

		for(var i = 0; i < labels.length; i++) {
			serieData[i] = tempMap[labels[i]] || 0;
		}

		series.push({
			name: legend,
			type: 'line',
			itemStyle : { normal: {label : {show: true, position: 'top'}}},
			data: serieData,
			smooth: true,
            //symbol: 'none', 
            symbolSize: 3,
            markLine: {
                data: [
                    {type : 'average', name: '平均值'}
                ]
            }

		});
	}	
	
	var chartObj = echarts.init($1(canvasName));
	var option = {
	    title : {
	        text: title
	    },
	    tooltip : {
	        trigger: 'axis'
	    },
	    legend: {
	        data:legends
	    },
	    toolbox: {
	        show : true,
	        feature : {
	            saveAsImage : {show: true}
	        }
	    },
	    xAxis : [
	        {
	            type : 'category',
	            boundaryGap : false,
	            data : labels
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value',
	            axisLabel : {
	                formatter: '{value} ' + (unitName ? unitName : '')
	            }
	        }
	    ],
	    series : series
	};  

	chartObj.on( 'click', function (param) { func(param.name); } );
	chartObj.setOption(option);                  
}

function echartPie2D(canvasName, title, data) {
	var labels = [];
	for(var i = 0; i < data.length; i++) {
		var item = data[i];
		labels[i] = item.name;
	}

	var chartObj = echarts.init($1(canvasName));
	var option = {
	    title : {
	        text: title,
	        x:'center'
	    },
	    tooltip : {
	        trigger: 'item',
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    legend: {
	        orient : 'vertical',
	        x : 'left',
	        data: labels
	    },
	    toolbox: {
	        show : true,
	        feature : {
	            saveAsImage : {show: true}
	        }
	    },
	    series : [
	        {
	            name: title,
	            type:'pie',
	            radius : '75%',
	            center: ['50%', '50%'],
	            data:data
	        }
	    ]
	};
    chartObj.setOption(option);         
}

function echartColumn2D(canvasName, title, data, func) {
	var labels = [], _data = [];
	for(var i = 0; i < data.length; i++) {
		var item = data[i];
		labels[i] = item.name;
		_data[i] = item.value;
	}

	var chartObj = echarts.init($$(canvasName));
	var option = {
	    title : {
	        text: title,
	    },
	    tooltip : {
	        trigger: 'axis'
	    },
	    toolbox: {
	        show : true,
	        feature : {
	            saveAsImage : {show: true}
	        }
	    },
	    xAxis : [
	        {
	            type : 'category',
	            data : labels,
	            axisLabel : {	 
	                rotate: 15, // 倾斜度
	                textStyle: {
	                    fontSize: 14,
	                }
	            }
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value'
	        }
	    ],
	    series : [
	        {
	            name:title,
	            type:'bar',
	            itemStyle : { normal: {label : {show: true, position: 'top'}}},
	            data: _data
	        }
	    ]
	};

	chartObj.on( 'click', function (param) { func(param.name); } );
    chartObj.setOption(option);         
}