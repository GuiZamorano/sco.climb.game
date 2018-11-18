angular.module('climbGame.services.login', [])
  .factory('loginService', function ($http, $q, $rootScope, configService) {
    var loginService = {};
    var OWNERID = "USERNAME";
    var GAMEID = "GAMEID";
    var USERTOKEN = "USERTOKEN";
    var CLASS = "CLASS";
    var CLASSES = "CLASSES";


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
        $http({
          method: 'GET',
          url: configService.getGameId() + data.name,
          headers: {
            'Accept': 'application/json',
            'x-access-token': data.token
          }

        }).
        success(function (data, status, headers, config) {
          console.log(data);
          // store gameid
          loginService.setGameId(data[0].gameId);
          //show class List
          loginService.setAllClasses(data[0].classRooms);
          deferred.resolve(data);

        }).
        error(function (data, status, headers, config) {
          //console.log(data);
          loginService.logout();
          deferred.reject(2);
        });
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
    loginService.getOwnerId = function () {
      if (loginService.ownId) {
        return loginService.ownId;
      } else {
        loginService.ownId = localStorage.getItem(OWNERID);
        return loginService.ownId;
      }
    }
    loginService.getGameId = function () {
      if (loginService.gameId) {
        return loginService.gameId;
      } else {
        loginService.gameId = localStorage.getItem(GAMEID);
        return loginService.gameId;
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
    loginService.getAllClasses = function () {
      if (loginService.classes) {
        return loginService.classes;
      } else {
        loginService.classes = JSON.parse(localStorage.getItem(CLASSES));
        return loginService.classes;
      }
    }
    loginService.getClassRoom = function () {
      if (loginService.classRoom) {
        return loginService.classRoom;
      } else {
        loginService.classRoom = localStorage.getItem(CLASS);
        return loginService.classRoom;
      }
    }
    loginService.setOwnerId = function (id) {
      localStorage.setItem(OWNERID, id);
      loginService.ownId = id;
    }
    loginService.setGameId = function (id) {
      localStorage.setItem(GAMEID, id);
      loginService.gameId = id;
    }
    loginService.setUserToken = function (token) {
      localStorage.setItem(USERTOKEN, token);
      loginService.userToken = token;
    }
    loginService.setAllClasses = function (classes) {
      localStorage.setItem(CLASSES, JSON.stringify(classes));
      loginService.classes = classes;
    }
    loginService.setClassRoom = function (classRoom) {
      localStorage.setItem(CLASS, classRoom);
      loginService.classRoom = classRoom;
    }
    loginService.setTitle = function (title) {
      $rootScope.title = title;
    }
    loginService.removeClass = function () {
      localStorage.removeItem(CLASS);
      loginService.classRoom = null;
    }
    loginService.logout = function () {
      loginService.ownId = null;
      loginService.gameId = null;
      loginService.userToken = null;
      loginService.classes = null;
      loginService.classRoom = null;
      localStorage.removeItem(OWNERID)
      localStorage.removeItem(GAMEID)
      localStorage.removeItem(USERTOKEN)
      localStorage.removeItem(CLASS)
      localStorage.removeItem(CLASSES)
    }
    return loginService;
  });
