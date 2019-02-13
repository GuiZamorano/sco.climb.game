/* global angular */
angular.module('climbGame.controllers.newStats', [])
  .controller('statsNewCtrl', function ($scope, $filter, $window, dataService, statsService) {
    var KMS_PER_FOOT = 10

    $scope.stats = null
    $scope.currentScore = 0
    $scope.index = ''
    $scope.totalScore = 5000
    $scope.excursions = null

    //need data structure with info about all trips
    //testing ssh
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
           function (excursions) {
             $scope.excursions = excursions
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

      // $scope.getNumberOfStudentsByActivityLevel(activityLevel){
      //   TODO: return number of students with activity level
      // }

      $scope.init = function () {
          statsService.getIndex();
          statsService.getMathStats();
      };
  })
