/* global angular */
angular.module('climbGame.controllers.stats', [])
  .controller('statsNewCtrl', function ($scope, $filter, $window, dataService) {
    var KMS_PER_FOOT = 10

    $scope.stats

    //need data structure with info about all trips

    var data2stats = function (data) {
      return {
          'ownerId': data.ownerId,
          'gameId': data.gameId,
          'name': data.name,
          'index': data.index,
          'classRoom': data.classRoom,
          'weather': data.weather,
          'modeMap': {
              '1': data['modeMap']['1'],
              '2': data['modeMap']['2'],
              '3': data['modeMap']['3'],
              '4': data['modeMap']['4'],
              '5': data['modeMap']['5'],
              '6': data['modeMap']['6'],
              '7': data['modeMap']['7'],
          },
          'closed': data.closed
      }
    }

    dataService.getMathStats().then(
      function (stats) {
        $scope.stats = data2stats(stats)
      },
      function (reason) {
        console.log(reason)
      }
    )

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

      $scope.getNumberofStudents
  })
