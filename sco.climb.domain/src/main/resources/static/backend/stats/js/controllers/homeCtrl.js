/* global angular */
angular.module('climbGameUser.controllers.home', [])
  .controller('HomeCtrl', function ($rootScope, $scope, $log, $state, $stateParams, $mdToast, $filter, $mdSidenav, 
  		$timeout, $location, $window, dataService, loginService) {
        $scope.$state = $state;
        $scope.hideBack = true;
        console.log("HomeCtrl");
        
        $scope.initController = function() {
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
              }
            }
          );
          
          $scope.title = 'school_select';
          $scope.selectedDomain = $stateParams.currentDomain;
          dataService.setCurrentDomain($stateParams.currentDomain);
          if ($scope.selectedDomain && $scope.selectedDomain != 'selectDomain') {
            $scope.loadInstitutes();
          }
          
          $scope.selectedInstitute = $stateParams.currentInstitute;
          dataService.setCurrentInstitute($stateParams.currentInstitute);
          if ($scope.selectedInstitute && $scope.selectedInstitute != 'selectInstitute') {
            $scope.loadSchools();
          }

          $scope.selectedSchool = $stateParams.currentSchool;
          dataService.setCurrentSchool($stateParams.currentSchool);

          $scope.showSelection = function() {
            return $state.current.name == 'home';
            return $scope.selectedDomain == 'selectDomain' || $scope.selectedInstitute == 'selectInstitute' || $scope.selectedSchool == 'selectSchool'
              || $scope.selectedDomain == '' || $scope.selectedInstitute == '' || $scope.selectedSchool == '';
          }
        }

        $scope.loadInstitutes = function() {
          $scope.institutes = [];
          $scope.schools = [];
          dataService.getInstitutesList($scope.selectedDomain).then(
            function (data) {
              $scope.institutes = data;
              if ($scope.institutes.length == 1)  {
                $scope.changeInstitute($scope.institutes[0]);
              }
            }
          );
        }
        $scope.loadSchools = function() {
          $scope.schools = [];
          dataService.getSchoolsList($scope.selectedInstitute).then(
            function (data) {
              $scope.schools = data;
              if ($scope.schools.length == 1)  {
                $scope.changeSchool($scope.schools[0]);
              }
            }
          );
        }

        $scope.changeDomain = function(domain) {
          $scope.selectedDomain = domain;
          dataService.setCurrentDomain(domain);
          $scope.loadInstitutes();
        }
        $scope.changeInstitute = function(institute) {
          $scope.selectedInstitute = institute.objectId;
          dataService.setCurrentInstitute(institute.objectId);
          $scope.loadSchools();
        }
        $scope.changeSchool = function(school) {
          $scope.selectedSchool = school.objectId;
          dataService.setCurrentSchool(school.objectId);
          if ($state.current.name == 'home' || $state.current.name == 'home.games-list') { //this filters wrong redirect if home.games-list.game-stat page is reloaded
            $state.go('home.games-list', {currentDomain: $scope.selectedDomain, currentInstitute: $scope.selectedInstitute, currentSchool: $scope.selectedSchool})
          }
        }
        

        $scope.goBack = function() {
          $state.go($scope.backStateToGo);
        }
        $scope.backToHome = function() {
          $state.go('home');
        }

        $scope.logout = function () {
          var logoutUrl = loginService.logout();
          var baseAppUrl = $location.$$absUrl.replace($location.$$path,'');
          logoutUrl += '?target=' + baseAppUrl;
          $window.location.href = logoutUrl;
        }
        $scope.backToDomainSelection = function() {
          $state.go('home');
        }

        $scope.initController();
    }
  )
