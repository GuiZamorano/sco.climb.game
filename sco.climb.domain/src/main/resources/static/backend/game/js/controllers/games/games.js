angular.module('consoleControllers.games', ['ngSanitize'])

    // Games controller
    .controller('GamesListCtrl', function ($scope, $rootScope, DataService, MainDataService, createDialog, PermissionsService) {
        $scope.$parent.mainView = 'game';
        $scope.PermissionsService = PermissionsService;

        $scope.delete = function (game) {
            createDialog('templates/modals/delete-game.html', {
                id: 'delete-dialog',
                title: 'Attenzione!',
                success: {
                    label: 'Conferma', fn: function () {
                        DataService.removeData('game', game).then(
                            function (result) {
                                console.log("Rimozione gioco effettuata con successo.");
                                $scope.games.splice($scope.games.indexOf(game), 1);
                            }, function (error) {
                            	alert("Errore nella richiesta:" + error.data.errorMsg);
                            });
                    }
                }
            });
        };
        
        $scope.reset = function (game) {
          createDialog('templates/modals/reset-game.html', {
              id: 'delete-dialog',
              title: 'Attenzione!',
              success: {
                  label: 'Conferma', fn: function () {
                      DataService.resetGame(MainDataService.getSelectedDomain(), game.objectId).then(
                          function (response) {
                              alert("reset gioco effettuato con successo.");
                          }, function (error) {
                              alert("Errore nella richiesta:" + error.data.errorMsg);
                          });
                  }
              }
          });
        };
                
        $scope.initGameOnServer = function (game) {
            createDialog('templates/modals/init-game-confirmation.html', {
                id: 'init-game-confirmation-dialog',
                title: 'Inizializzare gioco?',
                success: {
                    label: 'Conferma', fn: function () {
                        DataService.initGameCall(MainDataService.getSelectedDomain(), game.objectId).then(
                            function (response) {
                                alert("Init game riuscito!");
                            }, function (error) {
                            	alert("Errore nella richiesta:" + error.data.errorMsg);
                            }
                        );
                    }
                }
            });

        }
    })

    .controller('GameCtrl', function ($scope, $stateParams, $state, $rootScope, $timeout, DataService, MainDataService, createDialog, $filter) {
        $scope.$parent.mainView = 'game';

        // Variabili per date-picker
        $scope.dateFormat = 'dd/MM/yyyy';
        $scope.startDate = new Date();
        $scope.endDate = new Date();
        $scope.isCalendarOpen = [false, false];
        $scope.minDate = new Date(1970, 1, 1);

        // Variabili per time-picker
        $scope.collectFromHour = new Date();
        $scope.collectFromHour.setSeconds(0);           // in questo modo evito che nell'input field compaiano anche i secondi e i millisecondi
        $scope.collectFromHour.setMilliseconds(0);
        $scope.collectToHour = new Date();
        $scope.collectToHour.setSeconds(0);
        $scope.collectToHour.setMilliseconds(0);

        // Variabili per selezione scuola, classi e linee corrispondenti
        $scope.classes = [];


        $scope.initController = function () {
            if ($scope.currentGame) { //edit game
                $scope.saveData = DataService.editData;
                if($scope.currentGame.usingPedibusData) {
                  $scope.startDate.setTime($scope.currentGame.from);
                  $scope.endDate.setTime($scope.currentGame.to);
                  $scope.collectFromHour.setHours(Number($scope.currentGame.fromHour.slice(0, 2)), Number($scope.currentGame.fromHour.slice(3, 5)));
                  $scope.collectToHour.setHours(Number($scope.currentGame.toHour.slice(0, 2)), Number($scope.currentGame.toHour.slice(3, 5)));
                }
            } else {
                $scope.currentGame = {
                    gameName: '',
                    gameDescription: '',
                    from: '',
                    to: '',
                    fromHour: '',
                    toHour: '',
                    school: '',
                    classRooms: [],
                    ownerId: $stateParams.idDomain,
                    schoolId: $stateParams.idSchool,
                    instituteId: $stateParams.idInstitute,
                }
                $scope.saveData = DataService.saveData;
            }
            DataService.getData('classes',
                $stateParams.idDomain,
                $stateParams.idInstitute,
                $stateParams.idSchool).then(
                    function (response) {
                        var classes = response.data;
                        if (classes) {
                            classes.forEach(function (entry) {
                                var classEntry = {
                                    value: false,
                                    name: entry
                                };
                                if ($scope.currentGame.classRooms && $scope.currentGame.classRooms.includes(entry)) {
                                    classEntry.value = true;
                                }
                                $scope.classes.push(classEntry);
                            });
                        }
                        $scope.$broadcast('gameLoaded');
                    }, function (error) {
                        alert('Errore nel caricamento delle classi:' + error.data.errorMsg);
                    }
                );

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
        } else { //new game
            $scope.initController();
        }

        // Save the changes made to the path
        $scope.save = function () {

            if (checkFields()) {
                // Salvataggio date in timestamp Unix (ms)
            		$scope.startDate.setHours(0, 0, 0, 0);
            		$scope.endDate.setHours(23, 59, 59, 999);
                $scope.currentGame.from = $scope.startDate.getTime();
                $scope.currentGame.to = $scope.endDate.getTime();

                // Salvataggio orari di inizio e fine raccolta dati
                if (this.enablePiediBusParams) {
                    $scope.currentGame.fromHour = $scope.collectFromHour.toTimeString().slice(0, 5);
                    $scope.currentGame.toHour = $scope.collectToHour.toTimeString().slice(0, 5);
                }

                $scope.currentGame.classRooms = [];
                $scope.classes.forEach(function (entry) {
                    if (entry.value) {
                        $scope.currentGame.classRooms.push(entry.name);
                    }
                });

                $scope.saveData('game', $scope.currentGame).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                  function (response) {
                      console.log('Salvataggio dati a buon fine.');
                      if ($scope.currentGame.objectId) { //edited
                          for (var i = 0; i < $scope.games.length; i++) {
                              if ($scope.games[i].objectId == $scope.currentGame.objectId) $scope.games[i] = $scope.currentGame;
                          }
                      } else {
                      	$scope.currentGame.objectId = response.data.objectId;
                        if ($scope.games) $scope.games.push(response.data);
                      }
                      $state.go('root.games-list');
                  }, function (error) {
                  	alert("Errore nella richiesta:" + error.data.errorMsg);
                  }
                );
            }
            else {
                $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco e che le date siano valide.";
                $timeout(function () {
                    $rootScope.modelErrors = '';
                }, 5000);
            }
        };

        // Controlla se ci sono campi vuoti e se le ore e le date di fine sono precedenti o uguali a quelle di inizio comparando le stringhe
        function checkFields() {
            var isValidate = true;
            var invalidFields = $('.ng-invalid');


            for (var p in $scope.currentGame.params) {
                if ($scope.currentGame.params[p] < 0) {
                    isValidate = false;
                }
            }

            if (this.enablePiediBusParams) {
                if ($scope.collectToHour.toTimeString().localeCompare($scope.collectFromHour.toTimeString(), { numeric: true }) <= 0) {
                    isValidate = false;    
                }
            }

            if (invalidFields.length > 0 || $scope.endDate.toISOString().localeCompare($scope.startDate.toISOString(), { numeric: true }) <= 0)
                isValidate = false;

            return isValidate;
        }

        // Back without saving changes
        $scope.back = function () {
            createDialog('templates/modals/back.html', {
                id: 'back-dialog',
                title: 'Sei sicuro di voler uscire senza salvare?',
                success: { label: 'Conferma', fn: function () { $state.go('root.games-list'); } }
            });
        };
    })


    .controller('GameInfoCtrl', function ($scope, DataService) {
        $scope.$parent.selectedTab = 'info';
        $scope.$parent.enablePiediBusParams = false;
        $scope.nrOfStudenti = 0;

        $scope.$on('gameLoaded', function (e) {
        	// Variabili per date-picker(fix for refresh issue on parametri page)
          $scope.$parent.dateFormat = 'dd/MM/yyyy';
          $scope.$parent.startDate = new Date();
          $scope.$parent.endDate = new Date();
          $scope.$parent.isCalendarOpen = [false, false];
          $scope.$parent.minDate = new Date(1970, 1, 1);
          if($scope.currentGame.from) {
          	$scope.$parent.startDate.setTime($scope.currentGame.from);
          }
          if($scope.currentGame.to) {
          	$scope.$parent.endDate.setTime($scope.currentGame.to);
          }
          $scope.classToggled();
        });

        $scope.toggleSelectedClasses = function () {
            $scope.$parent.classes.forEach(function (currentClass) {
                currentClass.value = $scope.classesAllSelected;
            });
            $scope.classToggled();
        }

        $scope.classToggled = function () {
            $scope.selectedClasses = [];
            $scope.classesAllSelected = $scope.$parent.classes.every(function (cl) {
                return cl.value;
            })

            $scope.$parent.classes.forEach(function (cl) {
                if (cl.value) {
                    $scope.selectedClasses.push(cl.name);
                }
            })

            $scope.calculateStudenti($scope.selectedClasses);
        }

        $scope.calculateStudenti = function (selectedClasses) {
            //call api and calculate nr.of studenti.
            DataService.getNrOfStudents($scope.currentGame.ownerId, $scope.currentGame.instituteId, $scope.currentGame.schoolId, selectedClasses).then(
                function (response) {
                    $scope.nrOfStudenti = response.data;
                    $scope.$parent.nrOfStudenti = $scope.nrOfStudenti;
                });
        }

        // refresh of checkbox while switching between views
        if ($scope.currentGame) {
            $scope.classToggled();
        }


    })


    .controller('GameParamsCtrl', function ($scope, DataService, createDialog) {
        $scope.$parent.selectedTab = 'params';


        $scope.initParamController = function () {
        	if((!$scope.currentGame.params) || (Object.keys($scope.currentGame.params).length === 0)) {
            $scope.currentGame.params = {
                piedi_o_bici_in_autonomia_studenti: 0,
                piedi_o_bici_in_autonomia_distanza: 0,
                piedi_o_bici_con_adulti_studenti: 0,
                piedi_o_bici_con_adulti_distanza: 0,
                scuolabus_o_autobus_studenti: 0,
                scuolabus_o_autobus_distanza: 0,
                parcheggio_attestamento_studenti: 0,
                parcheggio_attestamento_distanza: 0,
                auto_fine_a_scuola_studenti: 0,
                auto_fine_a_scuola_distanza: 0,
                const_daily_nominal_distance: 0,
                const_zeroimpact_distance: 0,
                const_bus_distance: 0,
                const_pandr_distance: 0,
                const_zi_solo_bonus: 0,
                giorni_chiusi: 0,
                const_cloudy_bonus: 0,
                const_rain_bonus: 0,
                const_snow_bonus: 0,
                const_ZeroImpactDayClass_bonus: 0,
                const_NoCarDayClass_bonus: 0,
                km_bonus: 0
            }
        	} else {
            // typecast params.
            for (var p in $scope.currentGame.params) {
                $scope.currentGame.params[p] = parseFloat($scope.currentGame.params[p]);
            }
        	}
        }

        $scope.$on('gameLoaded', function (e) {
            // Variabili per date-picker(fix for refresh issue on parametri page)
            $scope.$parent.dateFormat = 'dd/MM/yyyy';
            $scope.$parent.startDate = new Date();
            $scope.$parent.endDate = new Date();
            $scope.$parent.isCalendarOpen = [false, false];
            $scope.$parent.minDate = new Date(1970, 1, 1);
            if($scope.currentGame.from) {
            	$scope.$parent.startDate.setTime($scope.currentGame.from);
            }
            if($scope.currentGame.to) {
            	$scope.$parent.endDate.setTime($scope.currentGame.to);
            }
            $scope.initParamController();
        });
        
        $scope.calculateBonusAutonomia = function() {
        	$scope.currentGame.params.const_pandr_distance = $scope.currentGame.params.parcheggio_attestamento_distanza;
        	$scope.currentGame.params.const_bus_distance = $scope.currentGame.params.scuolabus_o_autobus_distanza;
        	$scope.currentGame.params.const_zeroimpact_distance = $scope.currentGame.params.piedi_o_bici_con_adulti_distanza;
        	$scope.currentGame.params.const_zi_solo_bonus = $scope.currentGame.params.piedi_o_bici_in_autonomia_distanza - $scope.currentGame.params.piedi_o_bici_con_adulti_distanza;
        	return $scope.currentGame.params.const_zi_solo_bonus;
        }

        $scope.calculateCDND = function () {
            if ($scope.currentGame && $scope.currentGame.params) {
                $scope.currentGame.params.const_daily_nominal_distance = (
                    ($scope.currentGame.params.piedi_o_bici_in_autonomia_studenti * $scope.currentGame.params.piedi_o_bici_in_autonomia_distanza) +
                    ($scope.currentGame.params.piedi_o_bici_con_adulti_studenti * $scope.currentGame.params.piedi_o_bici_con_adulti_distanza) +
                    ($scope.currentGame.params.scuolabus_o_autobus_studenti * $scope.currentGame.params.scuolabus_o_autobus_distanza) +
                    ($scope.currentGame.params.parcheggio_attestamento_studenti * $scope.currentGame.params.parcheggio_attestamento_distanza) +
                    ($scope.currentGame.params.auto_fine_a_scuola_studenti * $scope.currentGame.params.auto_fine_a_scuola_distanza)
                );

                return $scope.currentGame.params.const_daily_nominal_distance;
            }
        };

        $scope.calculateSS = function () { 
            if ($scope.currentGame && $scope.currentGame.params) {
                return ($scope.currentGame.params.piedi_o_bici_in_autonomia_studenti +
                    $scope.currentGame.params.piedi_o_bici_con_adulti_studenti +
                    $scope.currentGame.params.scuolabus_o_autobus_studenti +
                    $scope.currentGame.params.parcheggio_attestamento_studenti +
                    $scope.currentGame.params.auto_fine_a_scuola_studenti);
            }            
        }

        $scope.calculateKMStimati = function () {
            if ($scope.currentGame && $scope.currentGame.params) {
                // calcuate actual days.
                actualDays = $scope.getNumWorkDays($scope.currentGame.from, $scope.currentGame.to);
                actualDays = actualDays - $scope.currentGame.params.giorni_chiusi;
                $scope.kmStimati = ($scope.currentGame.params.const_daily_nominal_distance/1000) * actualDays;

                return $scope.kmStimati;
            }

        }

        $scope.calculateKMTarget = function () {
            if ($scope.currentGame && $scope.currentGame.params) {
                return ($scope.kmStimati + $scope.currentGame.params.km_bonus);
            }
        }

        $scope.getNumWorkDays = function (startTS, endTS) {
            var numWorkDays = 0;
            var currentDate = new Date(startTS);
            var endDate = new Date(endTS)
            while (currentDate <= endDate) {
                // Skips Sunday and Saturday
                if (currentDate.getDay() !== 0 && currentDate.getDay() !== 6) {
                    numWorkDays++;
                }
                currentDate = new Date(currentDate.setTime(currentDate.getTime() + 1 * 86400000));
            }
            return numWorkDays;
        }

        if ($scope.currentGame) {
            $scope.initParamController();
        }



    });