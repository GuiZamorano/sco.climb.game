/* global angular */
angular.module('climbGame.controllers.ownerSelection', [])
  .controller('ownerSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, loginService, CacheSrv) {
  		$rootScope.isLoading = true;
      $scope.ownerIds = loginService.getAllOwners();
      if(loginService.getOwnerId()) {
      	$state.go('instituteSelection');
      }
      if($scope.ownerIds.length == 1) {
      	loginService.setOwnerId($scope.ownerIds[0]);
      	$state.go('instituteSelection');
      }
      $rootScope.isLoading = false;
      
      $scope.select = function () {
        if ($scope.selectedOwnerId) {
          loginService.setOwnerId($scope.selectedOwnerId);
          $state.go('instituteSelection')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_owner')))
        }
      }
    }
  )
