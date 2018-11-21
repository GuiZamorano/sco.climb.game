angular.module('consoleControllers.leg', ['isteven-multi-select', 'angularUtils.directives.dirPagination'])

// Edit the leg for the selected path
.controller('LegCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, PermissionsService, uploadImageOnImgur, drawMapLeg, drawMapLine, createDialog) {
    $scope.$parent.selectedTab = 'legs';
    $scope.viewIconsModels = [
        { icon: "<img src=img/POI_foot_full.png />", name: "A piedi", value:"foot", ticked: true},
        { icon: "<img src=img/POI_airplane_full.png />", name: "Aereo", value:"plane"},
        { icon: "<img src=img/POI_boat_full.png />", name: "Nave/Traghetto", value:"boat"},
        { icon: "<img src=img/POI_baloon_full.png />", name: "Mongolfiera", value:"balloon"},
        { icon: "<img src=img/POI_zeppelin_full.png />", name: "Dirigibile", value:"zeppelin"},
        { icon: "<img src=img/POI_train_full.png />", name: "Treno", value:"train"},
        { icon: "<img src=img/POI_sleigh_full.png />", name: "Slitta", value:"sled"}
    ];
    $scope.editMode = false;

    $scope.getYoutubeImageFromLink = function(ytLink) {
        //try to find thumbnail from youtube
        var regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
        var match = ytLink.match(regExp);
        if (match && match[2].length == 11) {
            return "https://img.youtube.com/vi/" + match[2] + "/0.jpg";
        }
        return false;
    }

    $scope.initController = function() {
        if ($stateParams.idLeg) { //edit path
        		$scope.newLeg = false;
            $scope.leg = angular.copy($scope.legs.find(function (e) { return e.objectId == $stateParams.idLeg }));
            $scope.leg.coordinates = {lat: $scope.leg.geocoding[1], lng: $scope.leg.geocoding[0]};      // trasformo le coordinate in un formato gestibile da GMaps
            $scope.saveData = DataService.editData;
            
            $scope.leg.externalUrls.forEach(element => {
                if (element.type == 'video') {
                    element.youtubeThumbnail = $scope.getYoutubeImageFromLink(element.link);
                }
            });

            $scope.viewIconsModels.forEach(function(element) {
                element.ticked = (element.value == $scope.leg.icon); 
            }, this);

        } else {
        		$scope.newLeg = true;
            $scope.leg = {
                ownerId: $stateParams.idDomain,
                pedibusGameId: $stateParams.idGame,
                itineraryId: $stateParams.idPath,
                name: '',
                description: '',
                imageUrl: '',
                coordinates: {
                    lat: 45.8832637,
                    lng: 11.0014507
                },
                score: '',
                polyline: '',         // NEW: stringa contenente il percorso compreso tra la tappa e la sua precedente (nel 1° LEG sarà vuota, ovviamente)
                transport: 'foot',     // NEW: mezzo con cui si arriva alla tappa (foot [default], plane, boat)
                externalUrls: [],        // NEW: array di oggetti contenente gli elementi multimediali
                position: $scope.legs.length
            };
            $scope.saveData = DataService.saveData;
        }

        if($scope.leg.position === 0) {
            drawMapLeg.createMap('map-leg', 'geocodeHintInput', null, $scope.leg.coordinates, null, $scope.leg.transport);
        } else {
          if($scope.newLeg) {
          	drawMapLeg.createMap('map-leg', 'geocodeHintInput', 
          			{lat: $scope.legs[$scope.leg.position-1].geocoding[1], lng: $scope.legs[$scope.leg.position-1].geocoding[0]}, 
          			{lat: $scope.legs[$scope.leg.position-1].geocoding[1] + 0.05, lng: $scope.legs[$scope.leg.position-1].geocoding[0]}, 
                $scope.leg.additionalPoints, $scope.leg.transport);
          } else {
          	drawMapLeg.createMap('map-leg', 'geocodeHintInput', 
          			{lat: $scope.legs[$scope.leg.position-1].geocoding[1], lng: $scope.legs[$scope.leg.position-1].geocoding[0]}, 
                $scope.leg.coordinates, $scope.leg.additionalPoints, $scope.leg.transport);
          } 
        }

        // get previous leg scoreif exist.
        var currentLegIndex = 0;
        $scope.previousLegScore = 0;
        for (var i = 0; i < $scope.legs.length; i++) {
            if ($scope.legs[i].objectId == $stateParams.idLeg) {
                currentLegIndex = i;
                break;
            }
        }
        if($scope.newLeg) {
        	if($scope.leg.position > 0) {
        		$scope.previousLegScore = $scope.legs[$scope.legs.length - 1].score;
        	} else {
        		$scope.previousLegScore = 0;
        	}
        }
        if(currentLegIndex > 0) {
        	$scope.previousLegScore = $scope.legs[currentLegIndex - 1].score;
        }
    }


    if ($scope.legs) {
        $scope.initController();
    } else {
        $scope.$on('legsLoaded', function(e) {  
            $scope.initController();        
        });
    }

    $scope.$on('poiMarkerPosChanged', function(event, newLat, newLng, wipeAirDistance) {     // listener del broadcast che indica il cambiamento della posizione del marker
        $scope.leg.coordinates.lat = newLat;
        $scope.leg.coordinates.lng = newLng;
        /*if(wipeAirDistance)
            document.getElementById('airDistance').value = '';       // pulisci la textbox per il calcolo della lunghezza della linea*/
        if(!$scope.$$phase)
            $scope.$apply();        // forzo il controllo per l'aggiornamento dei campi
    });

    $scope.$on('poiMapTotalKmChanged', function(event, newDistance) {     //total km changed listener
        $scope.leg.totalDistance = newDistance;
        if(!$scope.$$phase)
            $scope.$apply();        // forzo il controllo per l'aggiornamento dei campi
    });

    $scope.updateTravelType = function (newTravelType) {
        drawMapLeg.setTravelType(newTravelType);
    };

    // Update the marker position when the user change coordinates
    $scope.updateMarkerPosition = function () {
        drawMapLeg.updateMarker($scope.leg.coordinates.lat, $scope.leg.coordinates.lng);
    };

    $scope.calculateNewMarkerPos = function(distance)
    {
        drawMapLeg.calculateMarkerPosFromDistance(Number(distance)*1000);
    };

    $scope.saveLeg = function () {
        $scope.leg.icon = undefined;
        for (var i = 0; i < $scope.viewIconsModels.length && !$scope.leg.icon; i++) { //bug in the library, no output-model present, so need to search selected item in the input-model
            if ($scope.viewIconsModels[i].ticked) $scope.leg.icon = $scope.viewIconsModels[i].value;
        }
        if ($scope.leg.position > 0) {
            $scope.leg.polyline = drawMapLeg.getPathPolyline();     // ottiene la polyline dal servizio
            $scope.leg.additionalPoints = drawMapLeg.getCustomWayPoint();
        }
        if (checkFields()) {
            if (PermissionsService.permissionEnabledEditLegs()) {
                $scope.leg.geocoding = [$scope.leg.coordinates.lng, $scope.leg.coordinates.lat];        // converto le coordinate in modo che possano essere "digerite dal server"
                var legBackup;
                if ($stateParams.idLeg) { //edited, have to update array
                    for (var i = 0; i < $scope.legs.length; i++) {
                        if ($scope.legs[i].objectId == $stateParams.idLeg) {
                            legBackup = {
                                value: $scope.legs[i],
                                positon: i
                            }
                            $scope.legs[i] = $scope.leg;
                            break;
                        }
                    }
                }
                $scope.saveData('leg', $scope.leg).then(
                    function(response) {
                        console.log('Salvataggio dati a buon fine.');
                        $scope.leg = response.data;
                        $scope.leg.coordinates = {};
                        $scope.leg.coordinates.lat = $scope.leg.geocoding[1];
                        $scope.leg.coordinates.lng = $scope.leg.geocoding[0];
                        if (!$stateParams.idLeg) {
                        	$scope.legs.push(response.data);
                        	$stateParams.idLeg = response.data.objectId;
                        }
                        $scope.currentPath.legs = $scope.legs;
                        $scope.saveData = DataService.editData;
                        $scope.newLeg = false;

                        // modify next leg if exist.
                        var modifiedLegIndex = 0;
                        for (var i = 0; i < $scope.legs.length; i++) {
                            if ($scope.legs[i].objectId == $stateParams.idLeg) {
                                modifiedLegIndex = i;
                                break;
                            }
                        }

                        var nextLeg = $scope.legs[modifiedLegIndex + 1];
                        
                        if (nextLeg) {
                            var modifiedLeg = $scope.legs[modifiedLegIndex];
                            // generate simple polyline in case of plane and navetta targetto.                           
                            if (nextLeg.transport.toLowerCase() == 'plane' || nextLeg.transport.toLowerCase() == 'boat') {
                                // define polyline
                                var points = new Array();
                                points[0] = [modifiedLeg.geocoding[1], modifiedLeg.geocoding[0]];
                                points[1] = [nextLeg.geocoding[1], nextLeg.geocoding[0]];
                                // encode polyline                        
                                drawMapLine.createEncodings(points).then(function (response) {
                                    nextLeg.polyline = response;
                                    var backUpLegNext = $scope.legs[modifiedLegIndex + 1];
                                    $scope.saveData('leg', nextLeg).then(
                                        function (response) {
                                            console.log('Salvataggio dati a buon fine.');
                                            $state.go('root.path.legs');
                                        }, function (error) {
                                            if (backUpLegNext) {
                                                $scope.legs[backUpLegNext.position] = backUpLegNext.value;
                                            } else {
                                                $scope.legs.splice($scope.legs.length - 1, 1);
                                            }
                                        });
                                });
        
                            } else {
                                var start = new google.maps.LatLng(modifiedLeg.geocoding[1], modifiedLeg.geocoding[0]);
                                var end = new google.maps.LatLng(nextLeg.geocoding[1], nextLeg.geocoding[0]);
                                var request = {
                                    origin: start,
                                    destination: end,
                                    travelMode: drawMapLine.selectMode(nextLeg.transport)
                                };
                                // calculate new route between 'new preious' -> 'next leg and update polyline.'
                                drawMapLine.route(request).then(function (response) {
                                    nextLeg.polyline = response.routes[0].overview_polyline;
                                    var backUpLegNext = $scope.legs[modifiedLegIndex + 1];
                                    $scope.saveData('leg', nextLeg).then(
                                        function (response) {
                                            console.log('Salvataggio dati a buon fine.');
                                            $state.go('root.path.legs');
                                        }, function (error) {
                                            if (backUpLegNext) {
                                                $scope.legs[backUpLegNext.position] = backUpLegNext.value;
                                            } else {
                                                $scope.legs.splice($scope.legs.length - 1, 1);
                                            }
                                            alert('Errore nel salvataggio delle tappe.');
                                        });
                                }, function (error) {
                                    if (backUpLegNext) {
                                        $scope.legs[backUpLegNext.position] = backUpLegNext.value;
                                    } else {
                                        $scope.legs.splice($scope.legs.length - 1, 1);
                                    }
                                    alert('Errore nel salvataggio delle tappe.');
                                });
                            }
                        } else {
                            $state.go('root.path.legs');
                        }
                    }, function (error) {
                        console.log(error);
                        if (legBackup) {
                            $scope.legs[legBackup.position] = legBackup.value;
                        } else {
                            $scope.legs.splice($scope.legs.length-1, 1);
                        }
                        alert('Errore nel salvataggio delle tappe.');
                    }
                );
            } else {
              $rootScope.modelErrors = "Errore! Non hai i permessi di modificare la tappa.";
              $timeout(function () {
                  $rootScope.modelErrors = '';
              }, 5000);
            }           
        } else {
          $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco, di avere inserito almeno una foto e un punto di interesse prima di salvare.";
          $timeout(function () {
              $rootScope.modelErrors = '';
          }, 5000);
        }
    };
    
    $scope.saveLegLinks = function () {
      var toSend = {
          ownerId: $stateParams.idDomain,
          pedibusGameId: $stateParams.idGame,
          itineraryId: $stateParams.idPath,
          legId: $stateParams.idLeg,
          externalUrls: $scope.leg.externalUrls
      };
      var legBackup;
      for (var i = 0; i < $scope.legs.length; i++) {
          if ($scope.legs[i].objectId == $stateParams.idLeg) {
              legBackup = {
                  value: $scope.legs[i],
                  positon: i
              }
              $scope.legs[i] = $scope.leg;
              break;
          }
      }
      $scope.saveData('leg_content', toSend).then(
          function(response) {
              console.log('Salvataggio dati a buon fine.');
              //$state.go('root.path.legs');
          }, function() {
              if (legBackup) {
                  $scope.legs[legBackup.position] = legBackup.value;
              } else {
                  $scope.legs.splice($scope.legs.length-1, 1);
              }
              alert('Errore nel salvataggio delle tappe.');
          }
      );    	
    }

    function checkFields() {
        var allCompiled = true;
        var invalidFields = $('.error','.ng-invalid');
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

    // Exit without saving changes
    $scope.back = function () {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$window.history.back();} }
        });
    };

    
    $scope.uploadFeaturedPic = function () {
    	var fileInput = document.getElementById('upload-featured-img');
    	if(fileInput.files.length == 0) {
    		alert('Scegliere un file da caricare');
    		return;
    	}
    	var file = fileInput.files[0];
    	var formData = new FormData();
    	formData.append('file', file);
    	var element = {
    			"ownerId": $scope.leg.ownerId,
    			"pedibusGameId": $scope.leg.pedibusGameId,
    			"itineraryId": $scope.leg.itineraryId,
    			"legId": $scope.leg.objectId,
          "formdata": formData,
    	};
    	DataService.uploadFileContent(element).then(function (response) {
    		$scope.leg.imageUrl = response.data.link;
        $scope.img = null;
      });
    };

    $scope.deleteLink = function (element) {
        createDialog('templates/modals/delete-media.html',{
            id : 'delete-media-dialog',
            title: 'Attenzione!',
            success: { 
            	label: 'Conferma', 
            	fn: function() {
            		$scope.leg.externalUrls.splice($scope.leg.externalUrls.indexOf(element), 1);
            		$scope.saveLegLinks();
            	} 
            }
        });
    };
    
    $scope.updateLink = function(index, newTitle, newUrl, newType) {
    	$scope.updateElementData(index, newTitle, newUrl, newType);
    	$scope.saveLegLinks();
    }

    $scope.updateElementData = function(index, newTitle, newUrl, newType) {
        $scope.leg.externalUrls[index].name = newTitle;
        $scope.leg.externalUrls[index].link = newUrl;
        $scope.leg.externalUrls[index].type = newType;
        if (newType == 'video') {
        	$scope.leg.externalUrls[index].youtubeThumbnail = $scope.getYoutubeImageFromLink(newUrl);
        }
    }

    $scope.saveOrder = function() {
        if ($scope.enableOrder) {            
            $scope.currentPath.legs = $scope.legs;
//            createDialog('templates/modals/leg-multimedia-order-save.html',{
//                id : 'multimedia-save-order',
//                title: 'Tappa da salvare!',
//                success: { label: 'OK', fn: function() {} },
//                noCancelBtn: true
//            });
            $scope.saveLegLinks();
            $scope.enableOrder = false;
        } else {
            $scope.enableOrder = true;
        }
    };

    var addMultimediaElement = function(name, link, type) {
        var element = {
            name: name,
            link: link,
            type: type            
        };
        if (type == 'video') {
            element.youtubeThumbnail = $scope.getYoutubeImageFromLink(element.link);
        }
        $scope.leg.externalUrls.push(element);
    };



    $scope.createNewMultimediaElement = function() {
        createDialog('templates/modals/multimedia-create-new.html',
            {
                id : 'create-new-multimedia-dialog',
                title: 'Crea nuovo elemento multimediale',
                controller: 'CreateNewMultimediaElementDialogCtrl',
                success: {
                    label: "Aggiungi elemento",
                    fn: null
                },
                cancel: {
                    label: "Chiudi",
                    fn: null
                } 
            },
            {
                addElementsFunction: addMultimediaElement,
                saveFunction: $scope.saveLegLinks,
                dataService: DataService,
                leg: $scope.leg
            }
        );
    }
    $scope.searchMultimediaOnSearchEngines = function() {
        createDialog('templates/modals/multimedia-on-search-engines.html',
            {
                id : 'search-on-search-engines-dialog',
                title: 'Cerca elemento multimediale',
                controller: 'SearchOnSearchEnginesDialogCtrl',
                success: {
                    label: "Aggiungi 0 elementi",
                    fn: null
                },
                cancel: {
                    label: "Chiudi",
                    fn: null
                } 
            },
            {
                addElementsFunction: addMultimediaElement,
                saveFunction: $scope.saveLegLinks
            }
        );
    }
    $scope.searchMultimediaOnRepository = function() {
        createDialog('templates/modals/multimedia-on-content-repository.html',
            {
                id : 'search-on-search-engines-dialog',
                title: 'Cerca elementi multimediali già utilizzati',
                controller: 'SearchOnContentRepositoryDialogCtrl',
                success: {
                    label: "Aggiungi 0 elementi",
                    fn: null
                },
                cancel: {
                    label: "Chiudi",
                    fn: null
                }
            },
            {
            		schoolId: $scope.$parent.currentGame.schoolId,
            		addElementsFunction: addMultimediaElement,
            		saveFunction: $scope.saveLegLinks,
                position: [$scope.leg.coordinates.lat, $scope.leg.coordinates.lng],
                getYoutubeImageFromLink: $scope.getYoutubeImageFromLink
            }
        );
    }

});