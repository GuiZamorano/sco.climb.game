angular.module('climbGame.services.excursions', [])
  .factory('excursionsService', function ($q, $filter, dataService) {
    var excursionsService = {};

    excursionsService.getClassPlayers = function () {
      var deferr = $q.defer();
      dataService.getClassPlayers().then(function (data) {
        //return only the path and the legs
        deferr.resolve(data);
      }, function (err) {
        deferr.reject();
      });
      return deferr.promise;
    }


    return excursionsService;
  });
