angular.module('consoleControllers.gameclone', ['ngSanitize'])

    .controller('GameCloneCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, MainDataService, createDialog) {

        $scope.initController = function () {
            DataService.getData('gamereports').then(function (response) {
		            console.log('Caricamento report dei giochi andata a buon fine.');
		            $scope.allGames = response.data;
            });
        }
        
        $scope.clone = function (game) {
          createDialog('templates/modals/clone-game.html', {
              id: 'delete-dialog',
              title: 'Attenzione!',
              success: {
                  label: 'Conferma', fn: function () {
                      DataService.cloneGame($stateParams.idDomain, $stateParams.idInstitute,
                      		$stateParams.idSchool, game.objectId).then(
                          function (response) {
                              alert("clone gioco effettuato con successo.");
                              $scope.$parent.games.push(response.data);
                              $scope.back();
                          }, function (error) {
                              alert("Errore nella richiesta:" + error.data.errorMsg);
                          });
                  }
              }
          });
        };
        
        $scope.initController();
        
        // Exit without saving changes
        $scope.back = function () {
            $state.go('root.games-list'); //$window.history.back();
        };

    });