/* global angular */
angular.module('climbGame.controllers.notifications', [])
  .controller('notificationsCtrl', function ($scope, $window, $translate, dataService, CacheSrv) {
    $scope.notifications = []
    $scope.loading = true
    $scope.scroll = function (direction) {
      if (direction === 'up') {
        $window.document.getElementById('notifications-list').scrollTop -= 50
      } else if (direction === 'down') {
        $window.document.getElementById('notifications-list').scrollTop += 50
      }
    }

    $scope.lastcheck = CacheSrv.getLastCheck('notifications')

    dataService.getNotifications().then(
      function (data) {
        $scope.loading = false
        angular.forEach(data, function (notif) {
          notif.data = $scope.convertFields(notif.data)
        })
        $scope.notifications = data
        CacheSrv.updateLastCheck('notifications')
      },
      function (reason) {
        // console.log(reason)
        $scope.loading = false
      }
    )
  })
