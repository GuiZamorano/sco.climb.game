/* global angular */
angular.module('climbGameUser.controllers.users.lists', [])
.controller('usersListsParentCtrl', ['$scope', '$rootScope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $rootScope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
    $rootScope.title = "title_users_list";
    
    $scope.newUser = function() {
      $state.go("home.user-edit");
    }
  }
])
