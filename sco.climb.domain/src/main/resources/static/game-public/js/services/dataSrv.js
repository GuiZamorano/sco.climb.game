/* global angular */
angular.module('climbGame.services.data', [])
  .factory('dataService', function ($q, $http, $stateParams, configService) {
    configService.IMAGES_PREFIX_URL = '../game-dashboard/'; //Fix images base path

    var dataService = {}
    
    // get status of the game
    dataService.getStatus = function () {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/game/game/' 
        + $stateParams.idDomain 
        + '/' + $stateParams.idGame
        + '/itinerary/' + $stateParams.idItinerary
        + '/status',
        headers: {
          'Accept': 'application/json'
        },
        timeout: configService.httpTimout()
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    return dataService
  })
