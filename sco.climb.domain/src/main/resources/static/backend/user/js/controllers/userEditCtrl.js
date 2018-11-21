/* global angular */
angular.module('climbGameUser.controllers.users.edit', [])
.controller('userEditCtrl', ['$scope', '$rootScope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $rootScope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
    console.log("User email: " + $stateParams.userEmail);
    initParentNavigation();
    
    if ($stateParams.userEmail) {      
      dataService.getUserByEmail($stateParams.userEmail).then(
        function (data) {
          $scope.user = data;
        }
      );
    } else {
      $scope.newUser = true;
      $scope.user = {};
    }
    $scope.saving = false;

    $rootScope.saveUser = function() {
      $scope['user_edit'].$setSubmitted();
      if ($scope['user_edit'].$invalid) {
        $mdToast.show(
          $mdToast.simple()
            .textContent($filter('translate')('validation_error_msg'))
            .position("bottom")
            .hideDelay(3000)
        );
        return;
      }   
      $scope.saving = true;
      dataService.saveUser($scope.user).then(
        function (data) {
          $scope.saving = false;
          $state.go('home.user-edit', {'userEmail':$scope.user.email, 'newCreated': true});
        },
        function (reason) {
          $scope.saving = false;
          $mdToast.show(
            $mdToast.simple()
              .textContent($filter('translate')('user_creation_saving_error_msg'))
              .position("bottom")
              .hideDelay(3000)
          );
        }
      );
    }

    function initParentNavigation() {
      if ($stateParams.userEmail) {
        $rootScope.title = "title_user_edit";
      } else {
        $rootScope.title = "title_user_creation";
      }
      $rootScope.backStateToGo = "home.users-lists.list";
    }

  }
])
