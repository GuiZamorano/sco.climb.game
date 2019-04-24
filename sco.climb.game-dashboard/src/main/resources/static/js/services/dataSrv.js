/* global angular */
angular.module('climbGame.services.data', [])
  .factory('dataService', function ($q, $http, configService, loginService) {
    var dataService = {}

    // get status of the game
    dataService.getStatus = function () {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: 'api/game/status/123/1',
        headers: {
          'Accept': 'application/json'
          //'x-access-token': loginService.getUserToken()
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

    // get calendar's days
    dataService.getCalendar = function (from, to) {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: 'api/calendar/123/1/EE 364D',
        params: {
          from: from,
          to: to
        },
        headers: {
          'Accept': 'application/json'
          //'x-access-token': loginService.getUserToken()
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

      dataService.getWeather = function (weather) {
          var deferred = $q.defer()
          $http({
              method: 'GET',
              url: 'api/weather/123/1/EE 364D',
              params: {
                  weather: weather
              },
              headers: {
                  'Accept': 'application/json'
                  //'x-access-token': loginService.getUserToken()
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

        dataService.getIndex = function () {
          var deferred = $q.defer()
          $http({
            method: 'GET',
            url: 'api/calendar/index',
            headers: {
              'Accept': 'application/json'
              //'x-access-token': loginService.getUserToken()
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

    // get components of the class
    dataService.getClassPlayers = function () {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        //url: configService.getPlayersURL() + loginService.getOwnerId() + '/' + loginService.getGameId() + '/' + loginService.getClassRoom(),
        url: 'api/Babies',
        headers: {
          'Accept': 'application/json'
         // 'x-access-token': loginService.getUserToken()
        },
        //timeout: configService.httpTimout()
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    dataService.sendData = function (data) {
      var deferred = $q.defer()
      $http({
        method: 'POST',
        url: 'api/calendar/123/1/EE 364D',
        headers: {
          'Accept': 'application/json'
          //'x-access-token': loginService.getUserToken()
        },
        data: {
          'day': data.day,
          'meteo': data.meteo,
          'modeMap': data.modeMap,
          'index': data.index,
          'name' : data.name,
          'duration' : data.duration,
          'eadistance' : data.eadistance,
          'distance' : data.distance,
          'vadistance' : data.vadistance,
          'fadistance' : data.fadistance
        },
        //timeout: configService.httpTimout()
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    // get game statistics
    dataService.getStats = function () {
      var deferred = $q.defer()
      $http({
        method: 'GET',
        url: 'api/stat/123/1',
        headers: {
          'Accept': 'application/json',
          'x-access-token': loginService.getUserToken()
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

    // get excursions
    dataService.getExcursions = function (from, to) {
      var deferred = $q.defer()

      if (!from) {
        from = new Date(2016, 9, 1, 0, 0, 0, 0).getTime()
      }

      if (!to) {
        to = new Date(2017, 6, 30, 0, 0, 0, 0).getTime()
      }

      $http({
        method: 'GET',
        url: configService.getExcursionsURL() + loginService.getOwnerId() + '/' + loginService.getGameId() + '/' + loginService.getClassRoom(),
        headers: {
          'Accept': 'application/json',
          'x-access-token': loginService.getUserToken()
        },
        timeout: configService.httpTimout(),
        params: {
          'from': from,
          'to': to
        }
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    // post new excursions
    dataService.postExcursion = function (params) {
      var deferred = $q.defer()

      if (!params || !params.name || !params.date || !params.children || !params.distance || !params.meteo) {
        deferred.reject('Invalid or missing data')
        return deferred.promise
      }

      $http({
        method: 'POST',
        url: 'api/calendar/123/1/EE 364D',
        headers: {
          'Accept': 'application/json',

        },
        timeout: configService.httpTimout(),
        params: {
          'name': params.name,
          'day': params.day,
          //children: params.children,
          //distance: params.distance,
          'meteo': params.meteo,
          'modeMap': data.modeMap
        }
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    // get notifications
    dataService.getNotifications = function (timestamp) {
      var deferred = $q.defer()

      if (!timestamp) {
        // January 1, 2017
        timestamp = new Date(2017, 1, 1, 0, 0, 0, 0).getTime()
      }

      $http({
        method: 'GET',
        url: configService.getNotificationsURL() + loginService.getOwnerId() + '/' + loginService.getGameId() + '/' + loginService.getClassRoom(),
        headers: {
          'Accept': 'application/json',
          'x-access-token': loginService.getUserToken()
        },
        timeout: configService.httpTimout(),
        params: {
          timestamp: timestamp
        }
      }).then(function (response) {
        deferred.resolve(response.data)
      }, function (reason) {
        console.log(reason)
        deferred.reject(reason)
      })
      return deferred.promise
    }

    // get challenges
    dataService.getChallenges = function () {
      var deferred = $q.defer()

      $http({
        method: 'GET',
        url: configService.getChallengesURL() + loginService.getOwnerId() + '/' + loginService.getGameId() + '/' + loginService.getClassRoom(),
        headers: {
          'Accept': 'application/json',
          'x-access-token': loginService.getUserToken()
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

    dataService.getMathStats = function(from, to) {
        var deferred = $q.defer()
        $http({
            method: 'GET',
            url: 'api/calendar/123/1/EE 364D',
            params: {
                from: from,
                to: to
            },
            headers: {
                'Accept': 'application/json'
                //'x-access-token': loginService.getUserToken()
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

      dataService.getActivitySubjects = function () {
          var deferred = $q.defer()
          $http({
              method: 'GET',
              url: '/api/settings/getSubjectOptions/123/1/EE 364D',
              headers: {
                  'Accept': 'application/json',
                  //'x-access-token': loginService.getUserToken()
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

      dataService.getActivityTeks = function () {
          var deferred = $q.defer()
          $http({
              method: 'GET',
              url: '/api/settings/getTeksOptions/123/1/EE 364D',
              headers: {
                  'Accept': 'application/json',
                  //'x-access-token': loginService.getUserToken()
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

      dataService.getActivityGrades = function () {
          var deferred = $q.defer()
          $http({
              method: 'GET',
              url: '/api/settings/getGradeOptions/123/1/EE 364D',
              headers: {
                  'Accept': 'application/json',
                  //'x-access-token': loginService.getUserToken()
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

      dataService.saveSettings = function(settings) {
      var deferred = $q.defer()
      $http({
          method: 'POST',
          url: 'api/settings/selectModulesAndSaveSettings/123/1/EE 364D',
          headers: {
              'Accept': 'application/json'
              //'x-access-token': loginService.getUserToken()
          },
          params: {
              'subjects': settings.subjects,
              'gradeLevels': settings.gradeLevels,
              'teks': settings.teks,
          },
          //timeout: configService.httpTimout()
      }).then(function (response) {
          deferred.resolve(response.data)
      }, function (reason) {
          console.log(reason)
          deferred.reject(reason)
      })
      return deferred.promise
  }

    dataService.clearSwipes = function () {
      var deferred = $q.defer()
        $http({
          method: 'GET',
          url: 'api/calendar/swipes/clear/123/1/EE 364D',
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
