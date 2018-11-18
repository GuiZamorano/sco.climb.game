/* global angular */
angular.module('climbGame.controllers.stats', [])
  .controller('statsCtrl', function ($scope, $filter, $window, dataService) {
    var KMS_PER_FOOT = 10

    $scope.stats = {
      'gameScore': 0,
      'maxGameScore': 0
    }

    var data2stats = function (data) {
      return {
        'gameScore': Math.round(data.gameScore / 1000, 0),
        'maxGameScore': Math.round(data.maxGameScore / 1000, 0),
        'scoreModeMap': {
          'zeroImpact_wAdult': Math.floor(data['scoreModeMap']['zeroImpact_wAdult'] / (1000 * KMS_PER_FOOT)),
          'bus': Math.floor(data['scoreModeMap']['bus'] / (1000 * KMS_PER_FOOT)),
          'pandr': Math.floor(data['scoreModeMap']['pandr'] / (1000 * KMS_PER_FOOT)),
          'bonus': Math.floor(data['scoreModeMap']['bonus'] / (1000 * KMS_PER_FOOT)),
          'zeroImpact_solo': Math.floor(data['scoreModeMap']['zeroImpact_solo'] / (1000 * KMS_PER_FOOT))
        }
      }
    }

    dataService.getStats().then(
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
  })
