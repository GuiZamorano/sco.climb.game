/* global angular */
angular.module('climbGame.controllers.home', [])
  .controller('HomeCtrl', [
    '$rootScope',
    '$scope',
    '$log',
    '$state',
    '$mdSidenav',
    '$timeout',
    '$location',
    '$translate',
    'loginService',
    'CacheSrv',
    'HomeSrv',
    function ($rootScope, $scope, $log, $state, $mdSidenav, $timeout, $location, $translate, loginService, CacheSrv, HomeSrv) {
      $state.go('home.class')
      // $state.go('home.stats')

      $scope.go = function (path) {
        $scope.closeSideNavPanel()
        $state.go(path)
      }

      $scope.isCurrentState = function (state) {
        return $state.includes(state)
      }

      $scope.logout = function () {
        CacheSrv.resetLastCheck('calendar')
        CacheSrv.resetLastCheck('notifications')
        // delete storage
        loginService.logout()
        // go to login
        $state.go('login')
      }

      $scope.changeClass = function (path) {
        loginService.removeClass()
        $state.go('classSelection')
      }

      $scope.openSideNavPanel = function () {
        $mdSidenav('leftMenu').open()
      }

//      $scope.translate = function() {
//        $translateProvider.preferredLanguage(HomeSrv.translatePage())
//        $translateProvider.useStaticFilesLoader({
//         prefix: 'i18n/',
//         suffix: '.json'
//        })
//        $translateProvider.useSanitizeValueStrategy('escapeParameters')
//      }

      $scope.translatePage = function () {
        $translate.use(HomeSrv.translatePage())
      }

      $scope.closeSideNavPanel = function () {
        $mdSidenav('leftMenu').close()
      }

      $scope.convertFields = function (obj) {
        var convertibleFields = ['_bonus_', '_record_', '_performance_', 'target', '_totalKm_']
        angular.forEach(convertibleFields, function (field) {
          if (obj && obj[field]) {
            obj[field] = Math.floor(obj[field] / 1000)
          }
        })
        return obj
      }
    }
  ])
