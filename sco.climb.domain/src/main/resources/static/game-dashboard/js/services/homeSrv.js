/* global angular */
angular.module('climbGame.services.home', [])
  .factory('HomeSrv', function () {
    var homeService = {}

    var language = 'en'
    // get Language
    homeService.translatePage = function () {
     if(language == "en"){
        language = 'es'
     }
     else if(language == "es"){
        language = 'en'
     }
     return language
    }
    return homeService;
  })