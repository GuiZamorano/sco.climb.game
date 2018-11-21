/* global angular */
angular.module('climbGame.controllers.instituteSelection', [])
  .controller('instituteSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, loginService, CacheSrv, dataService) {
  		$rootScope.isLoading = true;
  		$scope.institutes = []; 
      if(loginService.getInstituteId()) {
      	$state.go('schoolSelection')
      } else {
        dataService.getInstitute().then(
          	function(data) {
							$scope.institutes = data;
							loginService.setSingleInstitute($scope.institutes.length == 1);
          		if($scope.institutes.length == 1) {
          			loginService.setInstituteId($scope.institutes[0].objectId)
								$state.go('schoolSelection')
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
        if ($scope.selectedInstitute) {
          loginService.setInstituteId($scope.selectedInstitute.objectId);
          $state.go('schoolSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_institute')))
        }
      }
  })
