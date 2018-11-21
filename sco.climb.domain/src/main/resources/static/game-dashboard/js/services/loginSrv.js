angular.module('climbGame.services.login', [])
  .factory('loginService', function ($http, $q, $rootScope, configService) {
    var loginService = {};
    var OWNERID = "USERNAME";
    var INSTITUTEID = "INSTITUTEID";
    var SCHOOLID = "SCHOOLID";
    var GAMEID = "GAMEID";
    var ITINERARYID = "ITINERARYID";
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
    loginService.getInstituteId = function () {
      if (loginService.instituteId) {
        return loginService.instituteId;
      } else {
        loginService.instituteId = localStorage.getItem(INSTITUTEID);
        return loginService.instituteId;
      }
    }
    loginService.getSchoolId = function () {
      if (loginService.schoolId) {
        return loginService.schoolId;
      } else {
        loginService.schoolId = localStorage.getItem(SCHOOLID);
        return loginService.schoolId;
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
    loginService.getItineraryId = function () {
      if (loginService.itineraryId) {
        return loginService.itineraryId;
      } else {
        loginService.itineraryId = localStorage.getItem(ITINERARYID);
        return loginService.itineraryId;
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
    loginService.getAllInstitutes = function() {
    	return loginService.instituteIds;
    }
    loginService.getAllSchools = function() {
    	return loginService.schoolIds;
    }    
    loginService.getAllItineraies = function() {
    	return loginService.itineraryIds;
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
    loginService.getSingleInstitute = function() {      
      return localStorage.getItem('singleInstitute') == 'true'; //terrible workaround, boolean saved as text
    }
    loginService.getSingleSchool = function() {
      return localStorage.getItem('singleSchool') == 'true';
    }
    loginService.getSingleClass = function() {
      return localStorage.getItem('singleClass') == 'true';
    }
    loginService.getSingleItinerary = function() {
      return localStorage.getItem('singleItinerary') == 'true';
    }
    loginService.getSingleGame = function() {
      return localStorage.getItem('singleGame') == 'true';
    }
    loginService.setOwnerId = function (id) {
      localStorage.setItem(OWNERID, id);
      loginService.ownId = id;
    }
    loginService.setInstituteId = function (id) {
      localStorage.setItem(INSTITUTEID, id);
      loginService.instituteId = id;
    }
    loginService.setSchoolId = function (id) {
      localStorage.setItem(SCHOOLID, id);
      loginService.schoolId = id;
    }
    loginService.setGameId = function (id) {
      localStorage.setItem(GAMEID, id);
      loginService.gameId = id;
    }
    loginService.setItineraryId = function (id) {
      localStorage.setItem(ITINERARYID, id);
      loginService.itineraryId = id;
    }
    loginService.setUserToken = function (token) {
      localStorage.setItem(USERTOKEN, token);
      loginService.userToken = token;
    }
    loginService.setAllOwners = function (ownerIds) {
      loginService.ownerIds = ownerIds;
    }
    loginService.setAllInstitutes = function (instituteIds) {
      loginService.instituteIds = instituteIds;
    }
    loginService.setAllSchools = function (schoolIds) {
      loginService.schoolIds = schoolIds;
    }
    loginService.setAllItineraies = function (itineraryIds) {
      loginService.itineraryIds = itineraryIds;
    }
    loginService.setAllClasses = function (classes) {
      localStorage.setItem(CLASSES, JSON.stringify(classes));
      loginService.classes = classes;
    }
    loginService.setSingleInstitute = function (single) {
      localStorage.setItem('singleInstitute', single);
    }
    loginService.setSingleSchool = function (single) {
      localStorage.setItem('singleSchool', single);
    }
    loginService.setSingleClass = function (single) {
      localStorage.setItem('singleClass', single);
    }
    loginService.setSingleItinerary = function (single) {
      localStorage.setItem('singleItinerary', single);
    }
    loginService.setSingleGame = function (single) {
      localStorage.setItem('singleGame', single);
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
    loginService.removeItinerary = function () {
      localStorage.removeItem(ITINERARYID);
      loginService.itineraryId = null;
    }
    loginService.removeGame = function () {
      localStorage.removeItem(GAMEID);
      loginService.gameId = null;
    }
    loginService.removeSchool = function () {
      localStorage.removeItem(SCHOOLID);
      loginService.schoolId = null;
    }
    loginService.removeInstitute = function () {
      localStorage.removeItem(INSTITUTEID);
      loginService.instituteId = null;
    }
    loginService.removeClasses = function () {
      localStorage.removeItem(CLASSES);
      loginService.classes = null;
    }
    loginService.logout = function () {
      loginService.ownId = null;
      loginService.userToken = null;
      loginService.instituteId = null;
      loginService.schoolId = null;
      loginService.gameId = null;
      loginService.itineraryId = null;
      loginService.classRoom = null;
      loginService.classes = null;
      localStorage.removeItem(OWNERID);
      localStorage.removeItem(INSTITUTEID);
      localStorage.removeItem(SCHOOLID);
      localStorage.removeItem(GAMEID);
      localStorage.removeItem(ITINERARYID);
      localStorage.removeItem(USERTOKEN);
      localStorage.removeItem(CLASS);
      localStorage.removeItem(CLASSES);
      return configService.getURL() + '/logout'
    }
    return loginService;
  });
