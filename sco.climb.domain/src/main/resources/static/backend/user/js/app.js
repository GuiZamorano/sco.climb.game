//  'use strict';
/* global angular */
angular.module('climbGameUser', [
  'ngAnimate',
  'ui.router',
  'ngMaterial',
  'ngAria',
  'ngMessages',
  'pascalprecht.translate',
  'climbGameUser.controllers.home',
  'climbGameUser.controllers.login',
  'climbGameUser.controllers.users.lists',
  'climbGameUser.controllers.users.lists.list',
  'climbGameUser.controllers.users.edit',
  'climbGameUser.controllers.users.editRole',
  'climbGameUser.services.data',
  'climbGameUser.services.conf',
  'climbGameUser.services.login',
  'climbGameUser.services.profile',
])

.config(function ($mdDateLocaleProvider) {

  // Can change week display to start on Monday.
  $mdDateLocaleProvider.firstDayOfWeek = 1;
  // Optional.

})
.config(['$translateProvider', function ($translateProvider) {
  // $translateProvider.translations('it', {});
  $translateProvider.preferredLanguage('it')
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
      .state('home', {
        url: '/:currentDomain/',
        views: {
          '@': {
            templateUrl: 'templates/home.html',
            controller: 'HomeCtrl'
          }
        }
      })
      .state('home.users-lists', {
        url: 'users-list/',
        views: {
          'content@home': {
            templateUrl: 'templates/users_lists.html',
            controller: 'usersListsParentCtrl'
          }
        },
        abstract: true        
      })
      .state('home.users-lists.list', {
        url: ':role',
        views: {
          'user-list@home.users-lists': {
            templateUrl: 'templates/users_list.html',
            controller: 'usersListCtrl'
          }
        }
      })
      .state('home.user-edit', {
        url: 'user-edit/:userEmail',
        views: {
          'content@home': {
            templateUrl: 'templates/user_edit.html',
            controller: 'userEditCtrl'
          }
        }
      })
      .state('home.user-role-edit', {
        url: 'user-role-edit/:userEmail',
        views: {
          'content@home': {
            templateUrl: 'templates/user_role_edit.html',
            controller: 'userRoleEditCtrl'
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

.directive('inputClear', function() {
  return {
    restrict: 'A',
    compile: function (element, attrs) {
        var color = attrs.inputClear;
        var style = color ? "color:" + color + ";" : "";
        var action = attrs.ngModel + " = ''";
        element.after(
            '<md-button class="animate-show md-icon-button md-accent"' +
            'ng-show="' + attrs.ngModel + '" ng-click="' + action + '"' +
            'style="position: absolute; top: -16px; right: -6px; margin: 13px 0px;">' +
            '<div style="' + style + '">x</div>' +
            '</md-button>');
    }
  };
})