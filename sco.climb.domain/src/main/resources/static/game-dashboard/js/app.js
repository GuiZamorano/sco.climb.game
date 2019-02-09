//  'use strict';
/* global angular */
angular.module('climbGame', [
  'ngAnimate',
  'ui.router',
  'ngMaterial',
  'ngAria',
  'ngMessages',
  'leaflet-directive',
  'ng-drag-scroll',
  'pascalprecht.translate',
  'climbGame.controllers.home',
  'climbGame.controllers.map',
  'climbGame.controllers.calendar',
  'climbGame.controllers.stats',
  'climbGame.controllers.excursions',
  'climbGame.controllers.notifications',
  'climbGame.controllers.login',
  'climbGame.controllers.ownerSelection',
  'climbGame.controllers.instituteSelection',
  'climbGame.controllers.schoolSelection',
  'climbGame.controllers.gameSelection',
  'climbGame.controllers.itinerarySelection',
  'climbGame.controllers.classSelection',
  'climbGame.services.cache',
  'climbGame.services.data',
  'climbGame.services.conf',
  'climbGame.services.map',
  'climbGame.services.login',
  'climbGame.services.profile',
  'climbGame.services.map',
  'climbGame.services.calendar',
  'climbGame.services.classSelection',
  'climbGame.services.home'
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
      $state.go('login')
      return $location.path()
    })

    $stateProvider
      .state('login', {
        url: '/login',
        views: {
          '@': {
            templateUrl: 'templates/login.html',
            controller: 'loginCtrl'
          }
        }
      })
      .state('ownerSelection', {
        url: '/owner',
        views: {
          '@': {
            templateUrl: 'templates/owner-selection.html',
            controller: 'ownerSelectionCtrl'
          }
        }
      })
      .state('instituteSelection', {
        url: '/institute',
        views: {
          '@': {
            templateUrl: 'templates/institute-selection.html',
            controller: 'instituteSelectionCtrl'
          }
        }
      })
      .state('schoolSelection', {
        url: '/school',
        views: {
          '@': {
            templateUrl: 'templates/school-selection.html',
            controller: 'schoolSelectionCtrl'
          }
        }
      })
      .state('gameSelection', {
        url: '/game',
        views: {
          '@': {
            templateUrl: 'templates/game-selection.html',
            controller: 'gameSelectionCtrl'
          }
        }
      })
      .state('itinerarySelection', {
        url: '/itinerary',
        views: {
          '@': {
            templateUrl: 'templates/itinerary-selection.html',
            controller: 'itinerarySelectionCtrl'
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
      .state('home.stats', {
        url: 'stats',
        views: {
          'content@home': {
            templateUrl: 'templates/stats.html',
            controller: 'statsCtrl'
          }
        }
      })
      .state('home.newStats', {
        url: 'newStats',
        views: {
          'content@home': {
            templateUrl: 'templates/statsNew.html',
            controller: 'statsNewCtrl'
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
