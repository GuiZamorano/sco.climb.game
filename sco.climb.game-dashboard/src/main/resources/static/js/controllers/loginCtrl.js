/* global angular */
angular.module('climbGame.controllers.login', [])
  .controller('loginCtrl', ['$scope', '$state', '$mdToast', '$filter', 'loginService',
    function ($scope, $state, $mdToast, $filter, loginService) {
      $scope.isAuth = false

      $scope.user = {
        username: '',
        password: ''
      }

      $scope.auth = function () {
        $scope.isAuth = true
      }

      $scope.login = function () {
        loginService.login($scope.user).then(function () {
          $state.go('classSelection')
        }, function (err) {
          console.log(err)
          // Toast the Problem
          $mdToast.show($mdToast.simple().content($filter('translate')('toast_uname_not_valid')))
        })
      }
    }
  ])
