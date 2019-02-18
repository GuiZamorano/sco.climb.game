/* global angular */
angular.module('climbGame.controllers.newStats', [])
  .controller('statsNewCtrl', function ($scope, $filter, $window, dataService, statsService) {
    var KMS_PER_FOOT = 10

    $scope.stats = null
    $scope.currentScore = 0
    $scope.index = ''
    $scope.totalScore = 5000
    $scope.weatherDays = null
    $scope.mean = 0;
    $scope.mode = [0,0];
    $scope.median = 0;
    $scope.scores = {
          'gameScore': 0,
          'maxGameScore': 0
    }





    dataService.getIndex().then(
        function(index) {
        $scope.index = index;
        $scope.refreshExcursions()
     })

    statsService.getMathStats(0, $scope.index).then(
      function (stats) {
        $scope.stats = data2stats(stats)
      },
      function (reason) {
        console.log(reason)
      }
    )


      var data2stats = function (data) {
          var ret = []
          for (i = 0; i < data.length; ++i) {
              ret.push(data[i]);
          }
          return ret;
      }


    $scope.refreshExcursions = function () {
         dataService.getCalendar(0, $scope.index).then(
           function (stats) {
             $scope.stats = stats;
             $scope.mean = $scope.getMeanDistance();
             $scope.mode = $scope.getModeDistance();
             $scope.median = $scope.getMedianDistance();
           },
           function (reason) {
                // console.log(reason)
           }
         )
       }

    $scope.getCurrentScore = function(){
        //iterate through each event
        for(i=0; i<$scope.stats; i++){
            //iterate through the modeMap of each event
            for (var property in stats[i].modeMap) {
                switch(property){
                    case 'zeroImpact_solo':
                        $scope.currentScore +=  3
                        break
                    case 'zeroImpact_wAdult':
                        $scope.currentScore +=  2
                        break
                    case 'bus':
                        $scope.currentScore +=  1
                        break
                    case 'pandr':
                        $scope.currentScore +=  0
                        break
                }
            }
        }
        return $scope.currentScore;
    }


    $scope.scroll = function (id, direction) {
      if (direction === 'up') {
        $window.document.getElementById(id).scrollTop -= 50
      } else if (direction === 'down') {
        $window.document.getElementById(id).scrollTop += 50
      }
    }

    $scope.getGameScorePercentage = function () {
      if ($scope.stats) {
        return ($scope.stats.gameScore * 100) / $scope.stats.maxGameScore
      }
    }

    $scope.getCount = function (count) {
      return !count ? 0 : new Array(count)
    }

    $scope.MathFloor = function (n) {
      return Math.floor(n)
    }

    // $scope.getAverageMilesPerStudent = function(){
    //     TODO: return average
    // }


      $scope.getNumberOfStudentsByActivityLevel = function(activityLevel){
        var retVal = 0;
        for(i=0; i<$scope.stats.length; i++){
            switch(activityLevel){
                case 'vactive':
                    retVal+=$scope.stats[i].vactive;
                    break;
                case 'eactive':
                    retVal+=$scope.stats[i].eactive;
                    break;
                case 'factive':
                    retVal+=$scope.stats[i].factive;
                    break;
                case 'iactive':
                    retVal+=$scope.stats[i].eactive;
                    break;
            }
        }
        return retVal;
      }

      $scope.getWeatherDays = function(weather){
          var weatherDays = statsService.getWeather(weather);
          $scope.weatherDays = weatherDays;
      }

      $scope.getMeanDistance = function(){
        var sum=0;
        var count=0;
        for(var i =0; i<$scope.stats.length-1; i++){
            sum += $scope.stats[i].distance;
            count++;
        }
        return sum/count;
      }

      $scope.getModeDistance = function(){
          var map = {};
          var retVal = [0, 0];
          for(var i =0; i<$scope.stats.length-1; i++ ){
              if(typeof map[$scope.stats[i].distance] == "undefined"){
                  map[$scope.stats[i].distance] = 1;
              }
              else{
                  map[$scope.stats[i].distance]+=1;
              }
          }

          for(key in map){
              if(map[key] > retVal[1]){
                  retVal[1] = map[key];
                  retVal[0] = key;
              }
          }
          return retVal; // [distance, number of occurrences]
      }

      $scope.getMedianDistance = function() {
          var median = 0;
          var sort = [];
          for(var i =0; i<$scope.stats.length-1; i++){
              sort.push($scope.stats[i].distance)
          }
          sort = sort.sort(function(a, b){return a-b});
          var length = sort.length;

          if (length % 2 == 0)
              median = (sort[length / 2 - 1] + sort[length / 2] / 2)
          else
              median = sort[(length-1)/2]

          return median;

      }


      $scope.init = function () {
          statsService.getIndex();
          statsService.getMathStats();
          statsService.getStats();
      };
  })
