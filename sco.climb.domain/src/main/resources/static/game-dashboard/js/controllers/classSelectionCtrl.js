/* global angular */
angular.module('climbGame.controllers.classSelection', [])
  .controller('classSelectionCtrl', function ($scope, $rootScope, $state, $mdToast, $filter, loginService, CacheSrv) {
  		$rootScope.isLoading = false;	
      $scope.classes = loginService.getAllClasses()
      loginService.setSingleClass($scope.classes.length == 1);
      if(loginService.getClassRoom()) {
      	$state.go('home')
      }
      
      $scope.select = function () {
        if ($scope.selectedClass) {
          CacheSrv.resetLastCheck('calendar')
          CacheSrv.resetLastCheck('notifications')
          loginService.setClassRoom($scope.selectedClass)
          $state.go('home')
        } else {
          $mdToast.show($mdToast.simple().content($filter('translate')('choose_classroom')))
        }
      }
    }
  )
