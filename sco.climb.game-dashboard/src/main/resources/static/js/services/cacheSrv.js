/* global angular, localStorage */
angular.module('climbGame.services.cache', [])
  .factory('CacheSrv', function () {
    var cacheService = {}

    cacheService.getLastCheck = function (page) {
      var timestamp = localStorage.getItem(page + '_page_last_check')
      if (timestamp) {
        return timestamp
      }
      return new Date(2017, 1, 1, 0, 0, 0, 0).getTime()
    }

    cacheService.updateLastCheck = function (page) {
      var now = new Date().getTime()
      localStorage.setItem(page + '_page_last_check', now)
      return now
    }

    cacheService.resetLastCheck = function (page) {
      localStorage.removeItem(page + '_page_last_check')
    }

    return cacheService
  })
