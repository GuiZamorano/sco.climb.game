angular.module('consoleControllers.line', [])

// Edit the line for the selected school
.controller('LineCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, MainDataService,
		uploadImageOnImgur, drawMapLine, createDialog) {
    $scope.$parent.selectedTab = 'lines-list';
    var currentSchool = $scope.$parent.currentSchool;
    $scope.enableOrder = false;
    
    $scope.dateFormat = 'dd/MM/yyyy';
    $scope.startDate = new Date();
    $scope.endDate = new Date();
    $scope.isCalendarOpen = [false, false];
    $scope.minDate = new Date(1970, 1, 1);
		
		$scope.initController = function() {
			if ($stateParams.idLine)     // se sto modificando una linea già esistente
      {
				for (var i = 0; i < $scope.currentSchool.lines.length && !$scope.line; i++) {
						if ($scope.currentSchool.lines[i].objectId == $stateParams.idLine) $scope.line = angular.copy($scope.currentSchool.lines[i]);
				}					
				$scope.startDate.setTime($scope.line.from);
				$scope.endDate.setTime($scope.line.to);
				$scope.saveData = DataService.editData;
				DataService.getData('stops',
										$stateParams.idDomain, 
										$stateParams.idInstitute, 
										$stateParams.idSchool,
										$scope.line.objectId).then(
						function(response) {
							$scope.line['stops'] = response.data; 
							console.log('Caricamento delle linee a buon fine.');
							drawMapLine.createMap('map-line', $scope.line.stops);
						}, function() {
							alert('Errore nel caricamento delle linee.');
						}
				);
      }
      else
      {
        $scope.line = {
            "name": '',
            "instituteId": $stateParams.idInstitute,
            "ownerId": $stateParams.idDomain,
            "schoolId": $stateParams.idSchool,
            "from": '',             
            "to": '',
            "distance": '',         
            "stops": []
        };
        $scope.saveData = DataService.saveData;
        drawMapLine.createMap('map-line', $scope.line.stops);
      }
		}


		if ($scope.currentSchool) {
				$scope.initController();
		} else {
				$scope.$on('schoolLoaded', function(e) {  
						$scope.initController();        
				});
		}
    
    $scope.isNewLine = function() {
    	return ($stateParams.idLine == null || $stateParams.idLine == '');
    };


    $scope.sortableOptions = {
        handle: ' .handle',
        axis: 'y',
        stop: function(){$scope.updatePath()}
    };

    $scope.$on('stopMarkerPosChanged', function(event, stopNumber, newLat, newLng) {     // listener del broadcast che indica il cambiamento della posizione del marker
        $scope.line.stops[stopNumber].geocoding[1] = newLat;
        $scope.line.stops[stopNumber].geocoding[0] = newLng;
        if(!$scope.$$phase)
            $scope.$apply();        // forzo il controllo per l'aggiornamento dei campi
        drawMapLine.updateMarkers($scope.line.stops);
    });

    // Update the marker position when the user change coordinates
    $scope.updatePath = function () {
        drawMapLine.updateMarkers($scope.line.stops);
    };

    $scope.save = function () {    
        if (checkFields()) {
        	$scope.line.from = $scope.startDate.getTime();
        	$scope.line.to = $scope.endDate.getTime();
          	$scope.saveData('route', $scope.line).then(
                function(response) {
                    console.log('Linea salvata.');
                    $scope.line['objectId'] = response.data.objectId;
                    //create stops
                  	for (i = 0; i < $scope.line.stops.length; i++) { 
                  		$scope.line.stops[i].position = i;
                  	}
                    var routeModel = {
                    		"routeId": '',
                    		"ownerId": '',
                    		"stops": []
                    }
                    routeModel['ownerId'] = $scope.line.ownerId;
                    routeModel['routeId'] = $scope.line.objectId;
                    routeModel['stops'] = $scope.line.stops;
                    $scope.saveData('stops', routeModel).then(
                  		function(response) {
                        // riaggiorno la lista delle linee
                        DataService.getData('route', 
                        	$scope.currentSchool.ownerId, 
                        	$scope.currentSchool.instituteId, 
                        	$scope.currentSchool.objectId).then(       
                            function(response) {
                                $scope.currentSchool.lines = response.data;
                                console.log('Caricamento delle linee a buon fine.');
                                $state.go('root.school.lines-list');
                            }, function() {
                                alert('Errore nel caricamento delle linee.');
                            }
                        );
                  		},
                  		function(error) {
                  			alert('Errore nel salvataggio delle fermate.');
                  		}
                  	);
                }, function() {
                    alert('Errore nel salvataggio della linea.');
                }
          );
        }
    };
    
    function checkFields() {
        var allCompiled = true;
        var invalidFields = $('.ng-invalid');
        // Get all inputs
        if (invalidFields.length > 0) {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco.";
            $timeout(function () {
                $rootScope.modelErrors = '';
            }, 5000);
            allCompiled = false;
        }
        return allCompiled;
    }

    $scope.addStop = function()
    {
        var stopsArray = $scope.line.stops;
        var center = drawMapLine.getMapCenter();
        stopsArray.push(
        	{
        		geocoding: [center.lng(), center.lat()]
        	}
        );
        $scope.updatePath();
    };

    $scope.deleteStop = function(index)
    {
        $scope.line.stops.splice(index, 1);
        $scope.updatePath();
    };

    // Exit without saving changes
    $scope.back = function () {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$window.history.back();} }
        });
    };
    
    
    
    $scope.assignPassengers = function(index) {
      DataService.getData('children',
      		$scope.currentSchool.ownerId, 
      		$scope.currentSchool.instituteId, 
      		$scope.currentSchool.objectId).then(
          function(response) {
          	$scope.children = response.data; 
            console.log('Caricamento degli scolari a buon fine.');
          	createDialog('templates/modals/assign-passengers.html',
          		{
              	id : 'assign-passengers-dialog',
              	title: 'Assegna i passeggeri alla fermata',
              	controller: 'AssignPassegersModalController'
          		},
          		{
          			stop: $scope.line.stops[index],
          			children: $scope.children
          		}
          	);
          }, function() {
            alert('Errore nel caricamento degli scolari.');
          }
      );
	};
	
    $scope.saveOrder = function() {
        if (!$scope.enableOrder) {
            $scope.enableOrder = true;
        } else {
			for (i = 0; i < $scope.line.stops.length; i++) { 
				$scope.line.stops[i].position = i;
			}
			var routeModel = {
				"routeId": '',
				"ownerId": '',
				"stops": []
			}
			routeModel['ownerId'] = $scope.line.ownerId;
			routeModel['routeId'] = $scope.line.objectId;
			routeModel['stops'] = $scope.line.stops;
			$scope.saveData('stops', routeModel).then(
				function(response) {
					$scope.enableOrder = false;
				}
			);
        }
    }
})

.controller('AssignPassegersModalController', function($scope, stop, children) {
	$scope.stop = stop;
	$scope.children = children;
	$scope.data = {
		childrenSelected: [],
		stopSelected: []
	};
	$scope.childrenMap = {};
	$scope.passengers = [];
	
	$scope.children.forEach(function(entry) {
		$scope.childrenMap[entry.objectId] = entry;
	});
	
	if($scope.stop.passengerList !=  null) {
		$scope.stop.passengerList.forEach(function(entry) {
			var child = $scope.childrenMap[entry];
			if(child != null) {
				$scope.passengers.push(child);
			}
			var index = findIndexById($scope.children, entry);
			if(index > -1) {
				$scope.children.splice(index, 1);
			}			
		});
		$scope.passengers.sort(compareChild);
	}
	$scope.children.sort(compareChild);
	
	$scope.getChildName = function(child) {
		return child.name + " " + child.surname;
	}
	
	$scope.getChildNameById = function(id) {
		var child = $scope.childrenMap[id];
		if(child != null) {
			return child.name + " " + child.surname;
		} else {
			return "";
		}
	}
	
	function findById(source, id) {
	  for (var i = 0; i < source.length; i++) {
	    if (source[i].objectId === id) {
	      return source[i];
	    }
	  }
	  return null;
	}
	
	function findIndexById(source, id) {
	  for (var i = 0; i < source.length; i++) {
	    if (source[i].objectId === id) {
	      return i;
	    }
	  }
	  return -1;
	}
	
	function compareChild(a,b) {
		var aName = a.name + " " + a.surname;
		var bName = b.name + " " + b.surname;
	  if (aName < bName)
	    return -1;
	  if (aName > bName)
	    return 1;
	  return 0;
	}
	
	$scope.fillPassengers = function() {
		if($scope.stop.passengerList ==  null) {
			$scope.stop.passengerList = [];
		}
		$scope.stop.passengerList.splice(0, $scope.stop.passengerList.length);
		$scope.passengers.forEach(function(entry) {
			$scope.stop.passengerList.push(entry.objectId);
		});
	}
	
	$scope.moveToStop = function() {
		$scope.data.childrenSelected.forEach(function(entry) {
			$scope.passengers.push(entry);
			var index = findIndexById($scope.children, entry.objectId);
			if(index > -1) {
				$scope.children.splice(index, 1);
			}
		});
		$scope.passengers.sort(compareChild);
		$scope.fillPassengers();
		$scope.data.childrenSelected.splice(0, $scope.data.childrenSelected.length);
	}
	
	$scope.moveFromStop = function() {
		$scope.data.stopSelected.forEach(function(entry) {
			$scope.children.push(entry);
			var index = findIndexById($scope.passengers, entry.objectId);
			if(index > -1) {
				$scope.passengers.splice(index, 1);
			}
		});
		$scope.fillPassengers();
		$scope.children.sort(compareChild);
		$scope.data.stopSelected.splice(0, $scope.data.stopSelected.length);
	}
	
});