/* global angular */
angular.module('climbGameUser.services.data', [])
  .factory('dataService', function ($q, $http, configService, loginService) {
    var dataService = {};
    var myProfile;
    var currentDomain, currentInstitute, currentSchool;
    
    dataService.getProfile = function () {
      var deferred = $q.defer()
      if (myProfile) deferred.resolve(myProfile);
      else {
        $http({
          method: 'GET',
          url: configService.getURL() + '/api/profile', 
          headers: {
            'Accept': 'application/json',
            'Authorization': 'Bearer ' + loginService.getUserToken()
          },
          timeout: configService.httpTimout(),
        }).then(function (response) {
          myProfile = response.data;
          deferred.resolve(response.data)
        }, function (reason) {
          console.log(reason)
          deferred.reject(reason)
        })
      }      
      return deferred.promise
    }
    dataService.getCurrentDomain = function() {
      return currentDomain;
    }
    dataService.setCurrentDomain = function(domain) {
      currentDomain = domain;
    }
    dataService.getCurrentInstitute = function() {
      return currentInstitute;
    }
    dataService.setCurrentInstitute = function(institute) {
      currentInstitute = institute;
    }
    dataService.getCurrentSchool = function() {
      return currentSchool;
    }
    dataService.setCurrentSchool = function(school) {
      currentSchool = school;
    }


    dataService.getInstitutesList = function () {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/institute/' + currentDomain, 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
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
    
    dataService.getSchoolsList = function (instituteId) {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/school/' + currentDomain + '/' + instituteId, 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
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

    dataService.getGamesList = function () {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/game/' + currentDomain + '/' + currentInstitute + '/' + currentSchool, 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
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

    dataService.getGame = function (gameId) {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/game/' + currentDomain + '/' + gameId, 
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
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

    dataService.getGameStats = function (gameId) {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: configService.getURL() + '/api/game/monitoring/' + currentDomain + '/' + gameId,
        //url: 'data/testGameStats.json',
        headers: {
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + loginService.getUserToken()
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
