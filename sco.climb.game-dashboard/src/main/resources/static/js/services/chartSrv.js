angular.module('climbGame.services.chart', [])
  .service('chartService', function () {
    var color = Chart.helpers.color;
    var max = 0;
    var barChart = 0;
    var barChartData = {
      labels : ['', '', '', '', ''],
      datasets : [
      {
          type: 'bar',
          label: 'Inactive',
          backgroundColor: color('#F2F2F2').alpha(0.8).rgbString(),
          borderColor: color('#F2F2F2'),
          data : []
      },  {
          type: 'bar',
          label: 'Fairly Active',
          backgroundColor: color('#EF5350').alpha(0.8).rgbString(),
          borderColor: color('#EF5350'),
          data : []
      },  {
          type: 'bar',
          label: 'Very Active',
          backgroundColor: color('#FFEE58').alpha(0.8).rgbString(),
          borderColor: color('#FFEE58'),
          data : []
      },  {
          type: 'bar',
          label: 'Extremely Active',
          backgroundColor: color('#66BB6A').alpha(0.8).rgbString(),
          borderColor: color('#66BB6A'),
          data : []
      }]
    };

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

    this.loadChart = function() {
        var ctx = document.getElementById('canvas').getContext('2d');
        barChart = new Chart(ctx, {
            type: 'bar',
            data: barChartData,
            options: {
                responsive: true,
                legend: {
                    display: true
                },
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: true,
                            steps: max + 1,
                            stepValue: 1,
                            max: max + 1,
                            callback: function (value) {if (Number.isInteger(value)) { return value; }}
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
         if(barChart)
             barChart.destroy()
        barChartData.datasets.forEach(function(dataset) {
            dataset.data = []
        });
        max = 0;
        barChartData.labels = ['', '', '', '', '']
     };

    this.setName = function(name, index) {
        barChartData.labels[index] = name;
    };
  });
