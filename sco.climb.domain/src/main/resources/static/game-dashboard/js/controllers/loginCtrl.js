/* global angular */
angular.module('climbGame.controllers.login', [])
  .controller('loginCtrl', function ($scope, $state, $mdToast, $filter, loginService, profileService) {
  		profileService.getProfile().then(function(profile) {
	    	loginService.setUserToken(profile.token)
	    	loginService.setAllOwners(profile.ownerIds)
	      $state.go('ownerSelection')
	    }, function (err) {
	      console.log(err)
	      // Toast the Problem
	      $mdToast.show($mdToast.simple().content($filter('translate')('toast_uname_not_valid')))
	    });
	  	
    }
  )
