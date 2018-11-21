/* global angular */
angular.module('climbGameUser.controllers.users.editRole', [])
.controller('userRoleEditCtrl', ['$scope', '$rootScope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $rootScope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
    console.log("User email: " + $stateParams.userEmail);
    initParentNavigation();
    
    if (!$stateParams.userEmail) {    
      $state.go($rootScope.backStateToGo);
    }
    $scope.saving = false;
    $scope.actualRole;
    dataService.getUserByEmail($stateParams.userEmail).then(
      function (data) {
        $scope.user = data;
        $scope.loadInstitutesList(true);
      }
    );

    $rootScope.saveUserRole = function() {
      $scope['role_edit'].$setSubmitted();
      if ($scope['role_edit'].$invalid) {
        $mdToast.show(
          $mdToast.simple()
            .textContent($filter('translate')('validation_error_msg'))
            .position("bottom")
            .hideDelay(3000)
        );
        return;
      }   
      //single role only, if multiple role can be assigned we have to edit this method!
      $scope.saving = true;
      dataService.addRole($scope.actualRole, $scope.user).then(
          function (data) {
            $state.go($rootScope.backStateToGo);
          },
          function () {
            $scope.saving = false;
            $mdToast.show(
              $mdToast.simple()
                .textContent($filter('translate')('role_add_error_msg'))
                .position("bottom")
                .hideDelay(3000)
            );
          }
        );      
    }

    $scope.loadInstitutesList = function(recoverOtherStates) {
      dataService.getInstitutesList().then(
        function (data) {
          $scope.institutesList = data;
          if (recoverOtherStates) $scope.loadSchoolsList(true);
        }
      );
    }
    $scope.loadSchoolsList = function(recoverOtherStates) {
      if (!recoverOtherStates) {
        $scope.user.schoolId = undefined;
        $scope.user.gameId = undefined;
        $scope.schoolsList = undefined;
        $scope.gamesList = undefined;
      }
      dataService.getSchoolsList($scope.user.instituteId).then(
        function (data) {
          $scope.schoolsList = data;
          if (recoverOtherStates) $scope.loadGamesList(true);
        }
      );
    }
    $scope.loadGamesList = function(recoverOtherStates) {
      if (!recoverOtherStates) {
        $scope.user.gameId = undefined;
        $scope.gamesList = undefined;
      }
      dataService.getGamesList($scope.user.instituteId, $scope.user.schoolId).then(
        function (data) {
          $scope.gamesList = data;
        }
      );
    }

    function initParentNavigation() {
      $rootScope.title = "title_user_role_edit";
      $rootScope.backStateToGo = "home.users-lists.list";
    }

  }
])
