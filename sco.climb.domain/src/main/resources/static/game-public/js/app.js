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
  'climbGame.controllers.map',
  'climbGame.services.map',
  'climbGame.services.data',
  'climbGame.services.conf'
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
  $translateProvider.preferredLanguage('it')
  $translateProvider.useStaticFilesLoader({
    prefix: '../game-dashboard/i18n/',
    suffix: '.json'
  })

  // $translateProvider.useSanitizeValueStrategy('sanitize');
  // $translateProvider.useSanitizeValueStrategy('sanitizeParameters');
  $translateProvider.useSanitizeValueStrategy('escapeParameters')
}])
.config(['$stateProvider', '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/');

    $stateProvider
      .state('home', {
        url: '/:idDomain/:idGame/:idItinerary/',
        views: {
          '@': {
            templateUrl: 'templates/home.html'
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
