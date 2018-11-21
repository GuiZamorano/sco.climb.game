/* global angular */
angular.module('climbGameUser.controllers.game.list', [])
  .controller('gamesListCtrl', ['$scope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
    function ($scope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
      
      console.log("GamesCtrl");
      $scope.$parent.title = "games_list_title";
      $scope.$parent.hideBack = true;


      dataService.getGamesList().then(
        function (data) {
          $scope.games = data;
        }
      );
      dataService.getInstitutesList().then(
        function (data) {
          $scope.institute = data.find(function (elem) {
            return elem.objectId == dataService.getCurrentInstitute();
          });
        }
      );
      dataService.getSchoolsList(dataService.getCurrentInstitute()).then(
        function (data) {
          $scope.school = data.find(function (elem) {
            return elem.objectId == dataService.getCurrentSchool();
          });
        }
      );

      $scope.openGame = function(event, game) {
        $state.go('home.games-list.game-stat', {gameId: game.objectId});
      }

      $scope.showStats = function() {
        if ($state.current.name == 'home.games-list.game-stat')
          return true;
        else {
          $scope.$parent.hideBack = true;
          return false;
        }
      }
    }
  ])
