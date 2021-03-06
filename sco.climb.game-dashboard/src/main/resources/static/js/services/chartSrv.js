angular.module('climbGame.services.chart', [])
  .service('chartService', function () {
    var color = Chart.helpers.color;
    var max = 0;
    var barChart = null;
    var ySize = -1;
    var barChartData = {};

    Chart.defaults.global.defaultFontStyle = 'Bold';
    Chart.defaults.global.defaultFontSize = 16;
    // Define a plugin to provide data labels
    Chart.plugins.register({
        afterDatasetsDraw: function(chart) {
            var ctx = chart.ctx;

            chart.data.datasets.forEach(function(dataset, i) {
                var meta = chart.getDatasetMeta(i);
                if (!meta.hidden) {
                    meta.data.forEach(function(element, index) {
                        // Draw the text in black, with the specified font
                        ctx.fillStyle = 'rgb(0, 0, 0)';

                        var fontSize = 16;
                        var fontStyle = 'bold';
                        var fontFamily = 'Helvetica Neue';
                        ctx.font = Chart.helpers.fontString(fontSize, fontStyle, fontFamily);

                        // Just naively convert to string for now
                        var dataString = dataset.data[index].toString();

                        // Make sure alignment settings are correct
                        ctx.textAlign = 'center';
                        ctx.textBaseline = 'middle';

                        var padding = 5;
                        var position = element.tooltipPosition();
                        ctx.fillText(dataString, position.x, position.y - (fontSize / 2) - padding);
                    });
                }
            });
        }
    });

    this.init = function(colors, categories) {
        barChartData.datasets = [];
        for(var i=0; i<colors.length; i++) {
            barChartData.datasets[i] = {};
            barChartData.datasets[i].type = 'bar';
            barChartData.datasets[i].backgroundColor = color(colors[i]).alpha(0.8).rgbString()
            barChartData.datasets[i].label = categories[i];
        }
    }

    var findNewMax = function() {
        max = 0;
        for(var i=0; i<barChart.data.datasets.length; i++){
            if(!barChart.chart.getDatasetMeta(i).hidden) {
                barChart.data.datasets[i].data.forEach(function(element) {
                    if(element > max)
                        max = element;
                })
            }
        }
    }

    this.loadChart = function(elementId) {
        if(!document.getElementById(elementId).className.includes("render")) {
            if(barChart)
                barChart.destroy();
            createNewBarChart(elementId);
        }
        else if(barChart) {
            findNewMax();
            barChart.options.scales.yAxes[0].ticks.max = Math.ceil(max*.2) + max;
            barChart.update();
        }
    };

    var createNewBarChart = function(elementId) {
        var ctx = document.getElementById(elementId).getContext('2d');
        barChart = new Chart(ctx, {
            type: 'bar',
            data: barChartData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                legend: {
                    display: true,
                    position: "bottom",
                    onClick: function(event, legendItem) {
                        var meta = this.chart.getDatasetMeta(legendItem.datasetIndex);
                        meta.hidden = meta.hidden === null ? !this.chart.data.datasets[legendItem.datasetIndex].hidden : null;
                        if(meta.hidden){
                            for(var i=0; i<barChart.data.datasets[legendItem.datasetIndex].data.length; i++) {
                                if(barChart.data.datasets[legendItem.datasetIndex].data[i] == max) {
                                    findNewMax();
                                    break;
                                }
                            }
                        }
                        else {
                            barChart.data.datasets[legendItem.datasetIndex].data.forEach(function(element) {
                                if(element > max) {
                                    max = element;
                                }
                            })
                        }

                        barChart.options.scales.yAxes[0].ticks.max = Math.ceil(max*.2) + max;
                        this.chart.update();
                    }
                },
                scales: {
                    xAxes: [{
                        display: false,
                        gridLines: {
                            drawOnChartArea: false
                        }
                    }],
                    yAxes: [{
                        display: false,
                        ticks: {
                            beginAtZero: true,
                            steps: Math.ceil(max*.2) + max,
                            stepValue: 1,
                            max: Math.ceil(max*.2) + max,
                            callback: function (value) {if (Number.isInteger(value)) { return value; }},
                        },
                        gridLines: {
                            drawOnChartArea: false
                        }
                    }]
                }
            }
        });
     };

    this.setData = function(data, datasetIndex, dataIndex) {
       barChartData.datasets[datasetIndex].data[dataIndex] = data;
       if(data > max)
           max = data
    };

    this.clearData = function() {
        barChartData.datasets.forEach(function(dataset) {
            dataset.data = [];
        });

        for(var i=0; i<ySize; i++)
            barChartData.labels[i] = '';

       max = 0;
    };

    this.setName = function(name, index) {
        barChartData.labels[index] = name;
    };

    this.setY = function(num) {
        ySize = num;
        barChartData.labels = []
        for(var i=0; i<num; i++)
            barChartData.labels[i] = '';
    };
  });
