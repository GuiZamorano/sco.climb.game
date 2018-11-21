angular.module('climbGame.services.profile', [])
  .factory('profileService', function ($http, $q, $rootScope, configService) {
    var profileService = {};

    profileService.getProfile = function () {
      var deferred = $q.defer();
      var getUrl = window.location;
      var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
      $http({
        method: 'GET',
        url: baseUrl + '/console/data',
      }).
      success(function (data, status, headers, config) {
        //store owner id and token
      	profileService.profile = data;
      	deferred.resolve(data);
      }).
      error(function (data, status, headers, config) {
        if (status == 403) {
          deferred.reject(1);
        }
        deferred.reject(2);
      });
      return deferred.promise;
    }
    return profileService;
  });
