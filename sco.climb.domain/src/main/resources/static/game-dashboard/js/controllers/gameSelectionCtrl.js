/* global angular */
angular.module('climbGame.controllers.gameSelection', [])
  .controller('gameSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, loginService, CacheSrv, dataService) {
  		$rootScope.isLoading = true;
      $scope.games = []; 
      if(loginService.getGameId()) {
      	$state.go('itinerarySelection')
      } else {
        dataService.getGame().then(
          	function(data) {
          		$scope.games = data;
              loginService.setSingleGame($scope.games.length == 1);
          		if($scope.games.length == 1) {
          			loginService.setGameId($scope.games[0].objectId)
          			$state.go('itinerarySelection')
          		}
          		$rootScope.isLoading = false;
          	}, 
          	function (err) {
          		console.log(err)
          		//Toast the Problem
          		$mdToast.show($mdToast.simple().content($filter('translate')('toast_api_error')))
          		setTimeout(function() {
          			$state.go('login')
          		}, 3000)
          	}
          );	
      }

      $scope.select = function () {
        if ($scope.selectedGame) {
          loginService.setGameId($scope.selectedGame.objectId);
          $state.go('itinerarySelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_game')))
        }
      }
  })
