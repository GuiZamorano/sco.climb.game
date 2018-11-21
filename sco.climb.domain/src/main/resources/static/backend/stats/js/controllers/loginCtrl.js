/* global angular */
angular.module('climbGameUser.controllers.login', [])
  .controller('loginCtrl', function ($scope, $state, $mdToast, $filter, loginService) {
			loginService.initProfileAfterLogin().then(function(profile) {
				$state.go('home', {currentDomain: 'selectDomain', currentInstitute: 'selectInstitute', currentSchool: 'selectSchool'})
	    }, function (err) {
	      console.log(err)
	      // Toast the Problem
	      $mdToast.show($mdToast.simple().content($filter('translate')('toast_uname_not_valid')))
	    });
    }
  )
