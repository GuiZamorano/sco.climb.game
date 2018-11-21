angular.module('consoleControllers.gameconfig', ['ngSanitize'])

    .controller('GameConfigCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, MainDataService, createDialog) {

        $scope.initController = function () {
            $scope.isNewGameConf = true;
            if ($stateParams.idGameConfig) $scope.isNewGameConf = false;
            //load general templates
            DataService.getGameConfData('gameconfigtemplate').then(function (response) {
                  console.log('Caricamento delle config andata a buon fine.');
                  $scope.configs = response.data;
                  for (var i = 0; i < $scope.configs.length; i++) {
                    if ($scope.configs[i].objectId == $scope.currentGame.confTemplateId) {
                        $scope.configs[i].saved = true;
                        $scope.selectedConfig = $scope.configs[i];
                        $scope.$parent.selectedConfig = $scope.selectedConfig;
                        break;
                    }
                }
            });
        }

        if ($stateParams.idGame) {
          MainDataService.getDomains().then(function (response) {
              MainDataService.getInstitutes($stateParams.idDomain).then(function (response) {
                  MainDataService.getSchools($stateParams.idInstitute).then(function (response) {
                      MainDataService.getGames($stateParams.idSchool).then(function (response) {
                          $scope.games = response.data;
                          $scope.currentGame = angular.copy($scope.games.find(function (e) { return e.objectId == $stateParams.idGame }));
                          $scope.initController();
                      });
                  });
              });
          });
        }

        // Exit without saving changes
        $scope.back = function () {
            $state.go('root.games-list'); //$window.history.back();
        };

        // select template.
        $scope.setTemplate = function (i) {
            $scope.selectedConfig = $scope.configs[i];
            $scope.selectedConfig.pedibusGameId = $stateParams.idGame;
            $scope.selectedConfig.ownerId = $stateParams.idDomain;

            var titleMsg = 'Sei sicuro di voler utilizzare questo template?';
            // identify and select game object.
            if ($scope.currentGame.gameId) {
                titleMsg = 'Gioco già istanziato. Sei sicuro di voler cambiare il template?';
            }

            createDialog(null, {
                id: 'back-dialog',
                title: 'Attenzione!',
                success: {
                    label: 'Conferma',
                    fn: function () {
                        DataService.updateTemplateToGame($scope.selectedConfig).then(
                            function () {
                                console.log('Salvataggio template a buon fine.');
                                $scope.currentGame.confTemplateId = $scope.selectedConfig.objectId;
                                $scope.initController();
                            }, function (error) {
                                if (error.data.errorMsg) {
                                    alert(error.data.errorMsg);
                                } else {
                                    alert('Errore nel salvataggio delle template.');
                                }
                            }
                        );

                    }
                },
                template: '<p>' + titleMsg + '</p>',
            });
        }


    });