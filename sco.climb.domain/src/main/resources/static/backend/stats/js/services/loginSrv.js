angular.module('climbGameUser.services.login', [])
  .factory('loginService', function ($http, $q, $rootScope, configService) {
    var loginService = {};
    var OWNERID = "USERNAME";
    var USERTOKEN = "USERTOKEN";

    loginService.login = function (user) {
      var deferred = $q.defer();
      $http({
        method: 'POST',
        url: configService.getTokenURL(),
        params: {
          username: user.username,
          password: user.password
        }
      }).
      success(function (data, status, headers, config) {
        console.log(data);
        //store owner id and token
        loginService.setOwnerId(data.name);
        loginService.setUserToken(data.token);        
      }).
      error(function (data, status, headers, config) {
        if (status == 403) {
          deferred.reject(1);
        }
        deferred.reject(2);
        //console.log(data);
      });
      return deferred.promise;
    }

    loginService.initProfileAfterLogin = function () {
      var deferred = $q.defer();
      var getUrl = window.location;
      var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
      $http({
        method: 'GET',
        url: baseUrl + '/console/data',
      }).
      success(function (data, status, headers, config) {
	    	loginService.setUserToken(data.token);
	    	loginService.setAllOwners(data.ownerIds);
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


    loginService.getOwnerId = function () {
      if (loginService.ownId) {
        return loginService.ownId;
      } else {
        loginService.ownId = localStorage.getItem(OWNERID);
        return loginService.ownId;
      }
    }
    loginService.getUserToken = function () {
      if (loginService.userToken) {
        return loginService.userToken;
      } else {
        loginService.userToken = localStorage.getItem(USERTOKEN);
        return loginService.userToken;
      }
    }
    loginService.getAllOwners = function() {
    	return loginService.ownerIds;
    }
    loginService.setOwnerId = function (id) {
      localStorage.setItem(OWNERID, id);
      loginService.ownId = id;
    }
    loginService.setUserToken = function (token) {
      localStorage.setItem(USERTOKEN, token);
      loginService.userToken = token;
    }
    loginService.setAllOwners = function (ownerIds) {
      loginService.ownerIds = ownerIds;
    }

    loginService.logout = function () {
      loginService.ownId = null;
      loginService.ownerIds = null;
      loginService.userToken = null;
      localStorage.removeItem(OWNERID);
      localStorage.removeItem(USERTOKEN);
      return configService.getURL() + '/logout'
    }
    return loginService;
  });
