/* global angular */
angular.module('climbGameUser.services.conf', [])
  .factory('configService', function () {
    var configService = {}
    configService.DEVELOPMENT = true
    var getUrl = window.location;
    var URL = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
  
    var httpTimeout = 10000

    configService.getURL = function () {
      return URL
    }

    configService.httpTimout = function () {
      return httpTimeout
    }

    return configService
  })
