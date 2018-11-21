/* global angular */
angular.module('climbGameUser.controllers.home', [])
  .controller('HomeCtrl', function ($rootScope, $scope, $log, $state, $stateParams, $mdToast, $filter, $mdSidenav, 
  		$timeout, $location, $window, dataService, loginService) {
        $scope.$state = $state;
        
        dataService.getProfile().then(
          function (data) {
            $scope.myProfile = data;
            $scope.myProfile.ownerIds = [];
            for(authKey in $scope.myProfile.roles) {
            	var auths = $scope.myProfile.roles[authKey];
            	auths.forEach(function(auth) {
            		if(auth.ownerId.startsWith("SYSTEM")) {
            			return true;
            		}
            		if(!$scope.myProfile.ownerIds.includes(auth.ownerId)) {
            			$scope.myProfile.ownerIds.push(auth.ownerId);
            		}
            	});
            }
            if ($scope.myProfile.ownerIds.length == 1)  { //if single domain available, select it 
              $scope.changeDomain($scope.myProfile.ownerIds[0]);
            } else { //check if domain inserted in the URL is present
              if ($stateParams.currentDomain != 'selectDomain' && !$scope.myProfile.ownerIds.includes($stateParams.currentDomain)) {
                $state.go('home', {currentDomain: 'selectDomain'});
              }
            }
          }
        );
        if ($stateParams.currentDomain == 'selectDomain') {
          $scope.title = 'Select domain';
        }
        $scope.selectedDomain = $stateParams.currentDomain;
        dataService.setCurrentDomain($stateParams.currentDomain);

        $scope.changeDomain = function(domain) {
          $scope.selectedDomain = domain;
          dataService.setCurrentDomain(domain);
          $state.go('home.users-lists.list', {currentDomain: domain});
        }

        $scope.goBack = function() {
          $state.go($scope.backStateToGo);
        }

        $scope.logout = function () {
          var logoutUrl = loginService.logout();
          var baseAppUrl = $location.$$absUrl.replace($location.$$path,'');
          logoutUrl += '?target=' + baseAppUrl;
          $window.location.href = logoutUrl;
        }
    }
  )
