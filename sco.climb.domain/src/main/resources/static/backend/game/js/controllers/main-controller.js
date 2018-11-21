angular.module('consoleControllers.mainCtrl', [])

.controller('MainCtrl', function ($scope, $rootScope, $timeout, $location, DataService, $window, MainDataService, PermissionsService)
    {
        $rootScope.networkProblem = {
            status: false,
            msg: ''
        }
        $rootScope.networkProblemDetected = function(msg) {
            $rootScope.networkProblem.status = true;
            $rootScope.networkProblem.msg = msg;
            
            $timeout(function() {
                $rootScope.networkProblem.status = false;
            }, 10 * 1000);
        }

        $scope.PermissionsService = PermissionsService;

        $scope.getLocation = function() {
            return $location.url()
        };

        $scope.isDropdownAvailable = function(location, dropdownType) {
            if (dropdownType == 'institute') {
                return location != '/institutes-list';
            } else if (dropdownType == 'school') {
                return location != '/institutes-list' && location != '/schools-list';
            } else if (dropdownType == 'game') {
                return location != '/institutes-list' && location != '/schools-list' && location != '/games-list';
            }
            return false;
        }
        
        $scope.logout = function() {
          $scope.selectedOwner = '';
          $scope.selectedInstitute = '';
          $scope.selectedSchool = '';
          $scope.institutesList = [];
          $scope.selectedGame = '';
          $scope.profile = null;
          localStorage.clear();
          sessionStorage.clear();
          var logoutUrl = DataService.getBaseUrl();
          var baseAppUrl = $location.$$absUrl.replace($location.$$path,'');
          logoutUrl += '/logout?target=' + baseAppUrl;
          $window.location.href = logoutUrl;
        }

        MainDataService.getDomains().then(function (p) {
            $scope.profile = p;
            PermissionsService.setProfilePermissions($scope.profile.roles);

            if ($scope.profile.ownerIds.length == 1) {
                MainDataService.setSelectedDomain($scope.profile.ownerIds[0]);
                $scope.selectedOwner = $scope.profile.ownerIds[0];
                $scope.loadInstitutesList($scope.profile.ownerIds[0]);
            }
        });

        $scope.loadInstitutesList = function(owner) {
            if (!owner) return;
            MainDataService.getInstitutes(owner).then(function (response) {
                $scope.institutesList = response.data;
                if ($scope.institutesList.length == 1) {
                    $scope.selectedInstitute = $scope.institutesList[0];
                    $scope.loadSchoolsList($scope.institutesList[0]);
                }
            });
        };

        $scope.loadSchoolsList = function(institute) {
            if (!institute) return;          
            MainDataService.getSchools(institute.objectId).then(function (response) {
                $scope.schools = response.data;
                if ($scope.schools.length == 1) {
                    $scope.selectedSchool = $scope.schools[0];
                    $scope.loadGames($scope.schools[0]);
                }
            });  
        };
        $scope.loadGames = function(school) {
            if (!school) return;
            $scope.gamesConfigs = [];
            MainDataService.getGames(school.objectId).then(function (response) {
                $scope.games = response.data;
                if ($scope.games.length == 1) {
                    $scope.selectedGame = $scope.games[0];
                    $scope.loadItineraries($scope.games[0]);
                }
            });
            // $scope.reloadGamesConfig(school.objectId);
        };
        $scope.loadItineraries = function(game) {
            if (!game) return;          
            MainDataService.getItineraries(game.objectId).then(function (response) {
                $scope.paths = response.data;
            });
        };
        // $scope.reloadGamesConfig = function(schoolId, invalidate) {
        //     MainDataService.getGamesConfigs(schoolId, invalidate).then(function (response) {                
        //         response.data.forEach(config => {
        //             $scope.gamesConfigs[config.pedibusGameId] = config;
        //         });
        //     });
        // }

        $scope.uploadComplete = function (content) {
            if (content.id) {
                $rootScope.errorTexts = [];
                $rootScope.successText = 'Data successfully uploaded!';
                $scope.profile = content;
            }
            else {
                var txt = [];
                if (content.errorMessage) {
                    txt.push(content.errorMessage);
                } else {
                    txt.push("General server error");
                }
                $rootScope.successText = '';
                $rootScope.errorTexts = txt;
            }
        };

        $scope.exportPaths = function () {
            window.open('console/exportexcel', '_blank');
        };

    }
);
