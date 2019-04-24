//  'use strict';
/* global angular */
angular.module('climbGame', [
  'ngAnimate',
  'ui.router',
  'ngMaterial',
  'ngAria',
  'ngMessages',
  'leaflet-directive',
  'ui.calendar',
  'pascalprecht.translate',
  'climbGame.controllers.home',
  'climbGame.controllers.map',
  'climbGame.controllers.calendar',
  'climbGame.controllers.calendarStats',
  'climbGame.controllers.stats',
  'climbGame.controllers.newStats',
  'climbGame.controllers.excursions',
  'climbGame.controllers.notifications',
  'climbGame.controllers.login',
  'climbGame.controllers.classSelection',
  'climbGame.controllers.settings',
  'climbGame.services.cache',
  'climbGame.services.data',
  'climbGame.services.excursions',
  'climbGame.services.conf',
  'climbGame.services.map',
  'climbGame.services.login',
  'climbGame.services.map',
  'climbGame.services.calendar',
  'climbGame.services.chart',
  'climbGame.services.classSelection',
  'climbGame.services.stats',
  'climbGame.services.settings',
  'climbGame.services.home',
  'zingchart-angularjs'
])

.config(function ($mdThemingProvider) {
    $mdThemingProvider.theme('default')
      .primaryPalette('light-blue', {
        'default': '300'
      })
      .accentPalette('deep-orange', {
        'default': '500'
      })
  })
  .config(function ($mdDateLocaleProvider) {

    // Can change week display to start on Monday.
    $mdDateLocaleProvider.firstDayOfWeek = 1;
    // Optional.

  })
  .config(['$translateProvider', function ($translateProvider) {
    // $translateProvider.translations('it', {});
    $translateProvider.preferredLanguage('en')
    $translateProvider.useStaticFilesLoader({
      prefix: 'i18n/',
      suffix: '.json'
    })

    // $translateProvider.useSanitizeValueStrategy('sanitize');
    // $translateProvider.useSanitizeValueStrategy('sanitizeParameters');
    $translateProvider.useSanitizeValueStrategy('escapeParameters')
  }])

.config(['$stateProvider', '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
    //  $urlRouterProvider.otherwise('/')
    $urlRouterProvider.otherwise(function ($injector, $location) {
      var $state = $injector.get('$state')
      var loginService = $injector.get('loginService')
      if (loginService.getOwnerId() && loginService.getClassRoom()) {
        $state.go('home')
          // $state.go('home.stats')
      } else if (loginService.getOwnerId()) {
        // if only user go to class
        $state.go('classSelection')
      } else {
        $state.go('login')
      }
      // login default
      return $location.path()
    })

    $stateProvider
      .state('login', {
        url: '/login',
        views: {
          '@': {
            templateUrl: 'templates/home.html',
            controller: 'HomeCtrl'
          }
        }
      })
      .state('classSelection', {
        url: '/classSelection',
        views: {
          '@': {
            templateUrl: 'templates/class-selection.html',
            controller: 'classSelectionCtrl'
          }
        }
      })
      .state('home', {
        url: '/',
        views: {
          '@': {
            templateUrl: 'templates/home.html',
            controller: 'HomeCtrl'
          }
        }
      })
      .state('home.map', {
        url: 'map',
        views: {
          'content@home': {
            templateUrl: 'templates/map.html',
            controller: 'mapCtrl'
          }
        }
      })
      .state('home.class', {
        url: 'class',
        views: {
          'content@home': {
            templateUrl: 'templates/calendar.html',
            controller: 'calendarCtrl'
          }
        }
      })
      .state('home.calendarStats', {
        url: 'calendarStats',
        views: {
          'content@home': {
            templateUrl: 'templates/calendarStats.html',
            controller: 'calendarStatsCtrl'
          }
        }
       })
      .state('home.excursions', {
        url: 'excursions',
        views: {
          'content@home': {
            templateUrl: 'templates/excursions.html',
            controller: 'excursionsCtrl'
          }
        }
      })
      .state('home.notifications', {
        url: 'notifications',
        views: {
          'content@home': {
            templateUrl: 'templates/notifications.html',
            controller: 'notificationsCtrl'
          }
        }
      })
      .state('home.statsNew', {
              url: 'statsNew',
              views: {
                'content@home': {
                  templateUrl: 'templates/statsNew.html',
                  controller: 'statsNewCtrl'
                }
              }
            })
        .state('home.settings', {
          url: 'settings',
          views: {
            'content@home': {
              templateUrl: 'templates/settings.html',
              controller: 'settingsCtrl'
            }
          }
        })
      .state('home.stats', {
        url: 'stats',
        views: {
          'content@home': {
            templateUrl: 'templates/stats.html',
            controller: 'statsCtrl'
          }
        }
      })
    }
  ])

// take all whitespace out of string
.filter('nospace', function () {
  return function (value) {
    return (!value) ? '' : value.replace(/ /g, '')
  }
})

// replace uppercase to regular case
.filter('humanizeDoc', function () {
  return function (doc) {
    if (doc) {
      if (doc.type === 'directive') {
        return doc.name.replace(/([A-Z])/g, function ($1) {
          return '-' + $1.toLowerCase()
        })
      }
      return doc.label || doc.name
    }
  }
})
