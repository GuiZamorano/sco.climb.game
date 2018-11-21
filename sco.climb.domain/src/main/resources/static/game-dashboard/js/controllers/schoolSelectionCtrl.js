/* global angular */
angular.module('climbGame.controllers.schoolSelection', [])
  .controller('schoolSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, loginService, CacheSrv, dataService) {
  		$rootScope.isLoading = true;
      $scope.schools = []; 
      if(loginService.getSchoolId()) {
      	$state.go('gameSelection')
      } else {
        dataService.getSchool().then(
          	function(data) {
              $scope.schools = data;              
							loginService.setSingleSchool($scope.schools.length == 1);
          		if($scope.schools.length == 1) {
          			loginService.setSchoolId($scope.schools[0].objectId)
          			$state.go('gameSelection')
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
        if ($scope.selectedSchool) {
          loginService.setSchoolId($scope.selectedSchool.objectId);
          $state.go('gameSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_school')))
        }
      }
  })
