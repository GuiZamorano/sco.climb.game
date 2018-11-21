angular.module("climbGame.controllers.map", [])
  .controller("mapCtrl", ["$scope", "$window", "$timeout", "$sce", '$location', "leafletData", "mapService", "configService", function ($scope, $window, $timeout, $sce, $location, leafletData, mapService, configService) {
    $scope.IMAGES_PREFIX_URL = configService.IMAGES_PREFIX_URL;
    $scope.demoUpdateTimeout = $location.search().demoupdatetimeout; 
    $scope.demoCenterLat = $location.search().demolat; 
    $scope.demoCenterLng = $location.search().demolng;
    $scope.demoZoom = $location.search().demozoom;  
    $scope.isDemoDisplayer = false;
    $scope.firstInteraction = false;
    if ($scope.demoUpdateTimeout && $scope.demoCenterLat && $scope.demoCenterLng && $scope.demoZoom) {
      $scope.isDemoDisplayer = true;
      console.log("Aggiornamento dati ogni " + $scope.demoUpdateTimeout + " secondi");
    }

    var init = function () {
      angular.extend($scope, {
        defaults: {
          zoomControl: false,
          worldCopyJump: true,
          attributionControl: true
        },
        center: {
          lat: configService.getDefaultMapCenterConstant()[0],
          lng: configService.getDefaultMapCenterConstant()[1],
          zoom: configService.getDefaultZoomMapConstant()
        },
        pathLine: {},
        pathMarkers: [],
        layers: {
          baselayers: {
            altro: {
              name: 'Watercolor',
              url: 'https://stamen-tiles-{s}.a.ssl.fastly.net/watercolor/{z}/{x}/{y}.png',
              type: 'xyz',
              layerOptions: {
                attribution: "Map tiles by Stamen Design, under CC BY 3.0. Data by OpenStreetMap, under CC-BY-SA",
              }
            },
            osm: {
              name: 'OpenStreetMap',
              url: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
              type: 'xyz'
            }

          }
        },
        events: {
            map: {
                enable: ['click', 'popupclose'],
                logic: 'emit'
            }
        }
      });
      var controlsStyle = {
        leftarrow: {
          backgroundColor: '#f39c12',
          backgroundImage: 'url("'+configService.IMAGES_PREFIX_URL+'img/arrow_left.png")',
          color: 'white',
          backgroundSize: "30px 30px",
          width: '30px',
          height: '30px',
          position: 'absolute',
          top: '80px',
          left: '60px',
          padding: '1px',
          border: '1px solid white'
        },
        rightarrow: {
          backgroundColor: '#f39c12',
          backgroundImage: 'url("'+configService.IMAGES_PREFIX_URL+'img/arrow_right.png")',
          backgroundSize: "30px 30px",
          width: '30px',
          height: '30px',
          position: 'absolute',
          top: '80px',
          left: '140px',
          border: '1px solid white'
        },
        uparrow: {
          backgroundColor: '#f39c12',
          backgroundImage: 'url("'+configService.IMAGES_PREFIX_URL+'img/arrow_up.png")',

          backgroundSize: "30px 30px",
          width: '30px',
          height: '30px',
          position: 'absolute',
          top: '40px',
          left: '100px',
          border: '1px solid white'
        },
        downarrow: {
          backgroundColor: '#f39c12',
          backgroundImage: 'url("'+configService.IMAGES_PREFIX_URL+'img/arrow_down.png")',

          backgroundSize: "30px 30px",
          width: '30px',
          height: '30px',
          position: 'absolute',
          top: '120px',
          left: '100px',
          border: '1px solid white'
        },
        zoomin: {
          backgroundColor: '#f39c12',
          backgroundImage: 'url("'+configService.IMAGES_PREFIX_URL+'img/zoom-in.png")',

          backgroundSize: "30px 30px",
          width: '30px',
          height: '30px',
          position: 'absolute',
          top: '40px',
          left: '20px',
          border: '1px solid white'
        },
        zoomout: {
          backgroundColor: '#f39c12',
          backgroundImage: 'url("'+configService.IMAGES_PREFIX_URL+'img/zoom-out.png")',
          backgroundSize: "30px 30px",
          width: '30px',
          height: '30px',
          position: 'absolute',
          top: '120px',
          left: '20px',
          border: '1px solid white'
        },
        home: {
          backgroundColor: '#f39c12',
          backgroundImage: 'url("'+configService.IMAGES_PREFIX_URL+'img/home.png")',
          backgroundSize: "30px 30px",
          width: '30px',
          height: '30px',
          position: 'absolute',
          top: '80px',
          left: '20px',
          border: '1px solid white'
        }
      };
      var assignStyle = function (containerStyle, styleValues) {
        containerStyle.backgroundColor = styleValues.backgroundColor;
        containerStyle.backgroundImage = styleValues.backgroundImage;
        containerStyle.backgroundSize = styleValues.backgroundSize;
        containerStyle.width = styleValues.width;
        containerStyle.height = styleValues.height;
        containerStyle.position = styleValues.position;
        containerStyle.top = styleValues.top;
        containerStyle.left = styleValues.left;
      }
      leafletData.getMap('map').then(function (map) {
          var leftarrow = L.Control.extend({
            options: {
              position: 'topleft'
            },
            onAdd: function (map) {
              var container = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-custom');
              assignStyle(container.style, controlsStyle['leftarrow']);
              container.onclick = function () {

                // Calculate the offset
                var offset = map.getSize().x * 0.14;
                // Then move the map
                map.panBy(new L.Point(-offset, 0), {
                  animate: true
                })
              }
              L.DomEvent.disableClickPropagation(container);
              return container;
            }
          });
          var rightarrow = L.Control.extend({
            options: {
              position: 'topleft'
            },
            onAdd: function (map) {
              var container = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-custom');
              assignStyle(container.style, controlsStyle['rightarrow']);
              container.onclick = function () {

                // Calculate the offset
                var offset = map.getSize().x * 0.14;
                // Then move the map
                map.panBy(new L.Point(offset, 0), {
                  animate: true
                })
              }
              L.DomEvent.disableClickPropagation(container);
              return container;
            }
          });
          var uparrow = L.Control.extend({
            options: {
              position: 'topleft'
            },
            onAdd: function (map) {
              var container = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-custom');
              assignStyle(container.style, controlsStyle['uparrow']);
              container.onclick = function () {

                // Calculate the offset
                var offset = map.getSize().x * 0.14;
                // Then move the map
                map.panBy(new L.Point(0, -offset), {
                  animate: true
                })
              }
              L.DomEvent.disableClickPropagation(container);
              return container;
            }
          });
          var downarrow = L.Control.extend({
            options: {
              position: 'topleft'
            },
            onAdd: function (map) {
              var container = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-custom');
              assignStyle(container.style, controlsStyle['downarrow'])
              container.onclick = function () {

                // Calculate the offset
                var offset = map.getSize().x * 0.14;
                // Then move the map
                map.panBy(new L.Point(0, offset), {
                  animate: true
                })
              }
              L.DomEvent.disableClickPropagation(container);
              return container;
            }
          });

          var zoomin = L.Control.extend({
            options: {
              position: 'topleft'
            },
            onAdd: function (map) {
              var container = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-custom');
              assignStyle(container.style, controlsStyle['zoomin'])
              container.onclick = function () {

                // Calculate the offset
                var offset = map.getSize().x * 0.14;
                // Then move the map
                map.zoomIn();
              }
              L.DomEvent.disableClickPropagation(container);
              return container;
            }
          });
          var zoomout = L.Control.extend({
            options: {
              position: 'topleft'
            },
            onAdd: function (map) {
              var container = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-custom');
              assignStyle(container.style, controlsStyle['zoomout'])
              container.onclick = function () {

                // Calculate the offset
                var offset = map.getSize().x * 0.14;
                // Then move the map
                map.zoomOut();
              }
              L.DomEvent.disableClickPropagation(container);
              return container;
            }
          });
          var home = L.Control.extend({
            options: {
              position: 'topleft'
            },
            onAdd: function (map) {
              var container = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-custom');
              assignStyle(container.style, controlsStyle['home'])
              container.onclick = function () {
                //              $scope.center = {
                //                lat: 37.973378,
                //                lng: 23.730957,
                //                zoom: 4
                //              }
                //              map.setZoom(4);
                //              map.panTo([37.973378, 23.730957]);

                map.fitBounds($scope.myInitialBounds);
                //map.setView([configService.getDefaultMapCenterConstant()[0], configService.getDefaultMapCenterConstant()[1]], configService.getDefaultZoomMapConstant());
                // map.invalidateSize();

              }
              L.DomEvent.disableClickPropagation(container);
              return container;
            }
          });
          map.addControl(new leftarrow());
          map.addControl(new rightarrow());
          map.addControl(new uparrow());
          map.addControl(new downarrow());

          map.addControl(new zoomin());
          map.addControl(new zoomout());
          map.addControl(new home());
        },
        function (error) {
          console.log('error creation');
        });

    }

    var loadData = function() {
      console.log("Loading data");
      if($scope.isDemoDisplayer && $scope.firstInteraction) {
      	console.log("skip refresh data");
      	return;
      }
      mapService.getStatus().then(function (data) {
        //visualize the status trought path
        $scope.status = data;
        $scope.legs = data.legs;
        $scope.globalTeam = data.game.globalTeam;
        $scope.myInitialBounds = new L.latLngBounds();

        if ($scope.$parent) {
          $scope.$parent.gamePublicTitle = data.itinerary.name;
          $scope.$parent.gamePublicDescription = $scope.sanitizeHtmlString(data.itinerary.description);
        }

        // get actual situation
        for (var i = 0; i < data.teams.length; i++) {
          if (data.teams[i].classRoom == $scope.globalTeam) {
            $scope.globalScore = data.teams[i].score;
            if (data.teams[i].currentLeg) {
              $scope.currentLeg = data.teams[i].currentLeg;
            } else {
              $scope.currentLeg = data.legs[data.legs.length - 1];
              $scope.endReached = true;

            }
            $scope.lastReachedLeg = data.teams[i].previousLeg;
            $scope.globalStatus = data.teams[i];
            //            $timeout($scope.scrollToPoint($scope.currentLeg.position - 1), 500);


            break;
          }
        }
        $scope.selectedLeg = $scope.lastReachedLeg;
        $scope.pathMarkers = [];
        for (var i = 0; i < data.legs.length; i++) {
          $scope.pathLine[i] = {
              color: '#3f51b5',
              weight: 5,
              latlngs: mapService.decode(data.legs[i].polyline)
            }
            //create div of external url
          var externalUrl = '<div class="external-urls-viewer" id="external-urls-viewer">';
          for (var k = 0; k < data.legs[i].externalUrls.length; k++) {
            switch (data.legs[i].externalUrls[k].type) {
              case 'image':
                externalUrl = externalUrl + '<div class="url-view-col url-view-col-image"> ' + ' <a href="' + data.legs[i].externalUrls[k].link + '" target="_blank"><img class="map-gallery" src="' + data.legs[i].externalUrls[k].link + '"/><p>' + data.legs[i].externalUrls[k].name + '</p></a></div>';
                break;
              case 'video':
                //try to find thumbnail from youtube
                var regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
                var match = data.legs[i].externalUrls[k].link.match(regExp);
                if (match && match[2].length == 11) {
                  externalUrl = externalUrl + '<div class="url-view-col url-view-col-video-yt"> ' + ' <a href="' + data.legs[i].externalUrls[k].link + '" target="_blank"><img class="map-gallery" src="https://img.youtube.com/vi/' + match[2] + '/0.jpg"/><img class="url-view-play" src="'+configService.IMAGES_PREFIX_URL+'img/ic_play.png"/><p>' + data.legs[i].externalUrls[k].name + '</p></a></div>';
                } else {
                  externalUrl = externalUrl + '<div class="url-view-col url-view-col-video"> ' + ' <a href="' + data.legs[i].externalUrls[k].link + '" target="_blank"><img class="map-gallery" src="'+configService.IMAGES_PREFIX_URL+'img/ic_video.png"/><p>' + data.legs[i].externalUrls[k].name + '</p></a></div>';
                }
                break;
              case 'link':
                externalUrl = externalUrl + '<div class="url-view-col url-view-col-link"> ' + ' <a href="' + data.legs[i].externalUrls[k].link + '" target="_blank"><img class="map-gallery" src="'+configService.IMAGES_PREFIX_URL+'img/ic_link.png"/><p>' + data.legs[i].externalUrls[k].name + '</p></a></div>';
                break;
              case 'file':
                externalUrl = externalUrl + '<div class="url-view-col url-view-col-link"> ' + ' <a href="' + data.legs[i].externalUrls[k].link + '" target="_blank"><img class="map-gallery" src="'+configService.IMAGES_PREFIX_URL+'img/ic_file.png"/><p>' + data.legs[i].externalUrls[k].name + '</p></a></div>';
                break;
            }
          }
          externalUrl += '</div>'
          if (data.legs[i].externalUrls.length > 2) {
            externalUrl += '<div class="controls">'
              +'<md-button class="md-icon-button" ng-click="scroll(\'up\')">'
              +'<md-icon class="icon-arrow_up"></md-icon>'
              +'</md-button>'
              +'<md-button class="md-icon-button" ng-click="scroll(\'down\')">'
              +'<md-icon class="icon-arrow_down"></md-icon>'
              +'</md-button>'
              +'</div>';
          }
          var icon = getMarkerIcon(data.legs[i]);
          if ((data.legs[i].position < $scope.currentLeg.position) || $scope.endReached) {
            $scope.pathMarkers.push(getMarker(data.legs[i], externalUrl, icon, i));
            //marker with message
          } else {
            //marker without message
            $scope.pathMarkers.push(getMarker(data.legs[i], null, icon, i));
          }

          if (data.legs[i].position - $scope.currentLeg.position <= 1 && data.legs[i].position - $scope.currentLeg.position >= -2) {       
              $scope.myInitialBounds.extend(L.latLng(data.legs[i].geocoding[1], data.legs[i].geocoding[0]));
          }

        }

        addPlayerPosition();
        //                  $timeout($scope.scrollToPoint($scope.currentLeg.position - 1), 3000);
        setGallerySize();
        setMapSize();
        leafletData.getMap('map').then(function (map) {
          if ($scope.isDemoDisplayer) {
            map.setView([$scope.demoCenterLat, $scope.demoCenterLng], $scope.demoZoom);
          } else {
            map.fitBounds($scope.myInitialBounds);         
          }
        }, function (err) {

        });
      },
      function (err) {
        //error with status
      });

      
    }

    init();
    setMapSize();

    loadData();
    if ($scope.isDemoDisplayer) {
      setInterval(loadData, $scope.demoUpdateTimeout*1000);
    }
    //function that put the position on map using the actual points of the user

    function addPlayerPosition() {
      //var poly = {};
      var poligons = {};
      //var tmpPolys = [];
      var actualMarkerPosition = [];
      if ($scope.status) {
        var polylines = $scope.status.legs;
        var myLeg = null;
        var isCurrLeg = true;
        if ($scope.globalStatus.currentLeg) {
          myLeg = $scope.globalStatus.currentLeg;
        }
        if ($scope.globalStatus.previousLeg) {
          myLeg = $scope.globalStatus.previousLeg;
          isCurrLeg = false;
        }
        var myScores = $scope.globalStatus.score; //2800;
        var totalRange = ($scope.globalStatus.currentLeg) ? ($scope.globalStatus.currentLeg.score - myLeg.score) : ($scope.globalStatus.score + $scope.globalStatus.scoreToEnd); //1000;
        var doneRange = ($scope.globalStatus.currentLeg) ? (myScores - myLeg.score) : myLeg.score; //800;
        //        if (doneRange > totalRange)
        //          (doneRange = totalRange);
        if (totalRange == 0) {
          totalRange = myLeg.score;
          doneRange = myScores;
        }
        if (polylines) {
          for (var i = 0; i < polylines.length; i++) {
            var pointArr = polyline.decode(polylines[i].polyline);
            var middlePoint = Math.floor(pointArr.length / 2);
            if ((polylines[i].position == myLeg.position + 1) || $scope.endReached) {
              // Actual leg. I have to split them in 2 part considering the percentage
              var actual_completing = doneRange / totalRange; //0,8
              var lengthInMeters = $scope.sumAllDistances(pointArr) * 1000;
              if (actual_completing > 0) {
                if ((actual_completing < 1)) { // case completing between 1% and 99%
                  var proportionalLength = lengthInMeters * actual_completing;
                  var splittedSubPolys = $scope.retrievePercentagePoly(pointArr, actual_completing, proportionalLength);
                  for (var y = 0; y < splittedSubPolys.length; y++) {
                    if (y == 0) { // I initialize the actual position for the specific marker with the last coord of the firt splitted polyline
                      actualMarkerPosition = splittedSubPolys[y][splittedSubPolys[y].length - 1];
                    }
                  }
                } else {
                  //end of the leg
                  actualMarkerPosition = pointArr[pointArr.length - 1];

                }
              } else {
                //beginning of the leg
                actualMarkerPosition = pointArr[0];
              }
            }
          }
        }
      }
      $scope.pathMarkers.push({
        getMessageScope: function () {
          return $scope;
        },
        lat: actualMarkerPosition[0],
        lng: actualMarkerPosition[1],
        message: '<div class="map-balloon">' +
          '<h4 class="text-pop-up"> Voi siete qui</h4>' +
          '</div>',
        icon: {
          iconUrl: ''+configService.IMAGES_PREFIX_URL+'img/POI_here.png',
          iconSize: [50, 50],
          iconAnchor: [25, 50],
          popupAnchor: [0, -50]
        }
      });
    }

    function getMarkerIcon(leg) {
      //check leg and give me icon based on my status and type of mean
      if (leg.position == 0) {
        return configService.IMAGES_PREFIX_URL+'img/POI_start.png'
      }
      if (leg.position == $scope.legs.length - 1) {
        return configService.IMAGES_PREFIX_URL+'img/POI_destination.png'
      }
      return configService.getIconImg(leg.icon, leg.position < $scope.currentLeg.position); 
    }

    function getMarker(data, url, icon, i) {
      var returnMarker = {};
      if (url) {
        returnMarker = {
          getMessageScope: function () {
            return $scope;
          },
          lat: data.geocoding[1],
          lng: data.geocoding[0],
          message: '<div class="map-balloon">' +
            '<h4 class="text-pop-up">' + (i + 1) + '. ' + data.name + '</h4>' + url + '</div>', 
            //'<div class="row">' + url +
            //'</div>' +
            //'</div>',
          icon: {
            iconUrl: icon,
            iconSize: [50, 50],
            iconAnchor: [25, 25],
            popupAnchor: [0, -25]
          }
        }
      } else returnMarker = {
        getMessageScope: function () {
          return $scope;
        },
        lat: data.geocoding[1],
        lng: data.geocoding[0],

        icon: {
          iconUrl: icon,
          iconSize: [50, 50],
          iconAnchor: [25, 25],
          popupAnchor: [0, -25]
        }
      }
      return returnMarker;
    }

    // Method used to split a polyline in two polylines considering a percentage value.
    // Now the percentage is related with the array elements number but we can consider the real distance in meters
    $scope.retrievePercentagePoly = function (pointArr, percentage, proportionalLength) {
      var findSplitPoint = false;
      var count = 1;
      do {
        var partialPoly = pointArr.slice(0, count);
        var lengthInMeters = $scope.sumAllDistances(partialPoly) * 1000;
        if (lengthInMeters > proportionalLength) {
          findSplitPoint = true;
        } else {
          count++;
        }
      } while (!findSplitPoint);
      if (count == pointArr.length) {
        count--;
      }
      var previousPoint = pointArr[count - 1];
      var nextPoint = pointArr[count];
      var deltaY = nextPoint[0] - previousPoint[0];
      var deltaX = nextPoint[1] - previousPoint[1];
      var newX = previousPoint[1] + (deltaX * percentage);
      var newY = previousPoint[0] + (deltaY * percentage);
      var newPoint = [newY, newX];

      var partialPoly1 = pointArr.slice(0, count);
      partialPoly1.push(newPoint);
      var partialPoly2 = pointArr.slice(count, pointArr.length);
      partialPoly2.unshift(newPoint);

      var splittedPolys = [];
      splittedPolys.push(partialPoly1);
      splittedPolys.push(partialPoly2);
      return splittedPolys;
    };

    // Method used to calculate a polyline length (in meters) with the sum of the distances between each point
    $scope.sumAllDistances = function (arrOfPoints) {
      var partialDist = 0;
      for (var i = 1; i < arrOfPoints.length; i++) {
        var lat1 = arrOfPoints[i - 1][0];
        var lon1 = arrOfPoints[i - 1][1];
        var lat2 = arrOfPoints[i][0];
        var lon2 = arrOfPoints[i][1];
        partialDist += $scope.getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2);
      }
      return partialDist;
    };

    // Method used to calculate the distance between two points
    $scope.getDistanceFromLatLonInKm = function (lat1, lon1, lat2, lon2) {
      var R = 6371; // Radius of the earth in km
      var dLat = deg2rad(lat2 - lat1); // deg2rad below
      var dLon = deg2rad(lon2 - lon1);
      var a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);
      var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      var d = R * c; // Distance in km
      return d;
    };

    var deg2rad = function (deg) {
      return deg * (Math.PI / 180)
    };

    $scope.scrollLeft = function () {
      document.getElementById('gallery').scrollLeft -= 50;

    }
    $scope.scrollRight = function () {
      document.getElementById('gallery').scrollLeft += 50;
    }
    $scope.scrollToPoint = function (i) {
      //get the bar
      var imagesBar = document.getElementById('gallery');
      var widhtImages = 100;
      imagesBar.scrollLeft = widhtImages * (i+1) - imagesBar.offsetWidth / 2 + widhtImages / 2;
    }
    $scope.goToPoi = function (leg) {
    	$scope.selectedLeg = leg;
      if (leg.position <= ($scope.currentLeg.position)) {
      	$scope.firstInteraction = true;
        if ($scope.selectedPosition !== undefined) {
          $scope.pathMarkers[$scope.selectedPosition].focus = false;
        }
        setTimeout(function() {          
          leafletData.getMap('map').then(function (map) {
            map.popupClose
            $scope.pathMarkers[leg.position].focus = true;
            map.setView([leg.geocoding[1] + configService.DEFAULT_POI_POPUP_OFFSET, leg.geocoding[0]], configService.getDefaultZoomPoiConstant());
            $scope.selectedPosition = leg.position;
          }, function (err) {});
          $scope.scrollToPoint(leg.position);
        }, 100); //workaround for leaflet issue! Without delay the popup is opened and immediatly closed
        
      }
    }
    $scope.mapGalleryDragging = function (mousedown, event, leg) { //used to prevent bug of dragging library that doesn't stop click propagation when dragging
      if (mousedown) {
        $scope.mapGalleryX = event.screenX;
        $scope.mapGalleryY = event.screenY;
      } else { //click filtered by initial and final position of mouse
        if (Math.abs($scope.mapGalleryX - event.screenX) < 10 && Math.abs($scope.mapGalleryY - event.screenY) < 10) {
          $scope.goToPoi(leg);
        }
      }
    }
    $scope.getSelected = function (index) {
      return index == $scope.selectedPosition;
    }
    $scope.scrollMap = function (index) {
      if (index == $scope.legs.length - 1) {
        if (!$scope.endReached) {
          $scope.scrollToPoint($scope.currentLeg.position);
        } else {
          $scope.scrollToPoint($scope.currentLeg.position + 1);
        }
      }
    }

    function setMapSize() {
      var w = window,
        d = document,
        e = d.documentElement,
        g = d.getElementsByTagName('body')[0],
        x = w.innerWidth || e.clientWidth || g.clientWidth,
        y = w.innerHeight || e.clientHeight || g.clientHeight;
      
      var tmpHeight = y;
      tmpHeight -= document.getElementById('map-footer').clientHeight;
      var tmpCreditsBanner = document.getElementsByClassName("credits-banner");
      if (tmpCreditsBanner && tmpCreditsBanner.length) {
        tmpHeight -= tmpCreditsBanner[0].clientHeight;
      }
      tmpHeight -= 64; //toolbar

      document.getElementById('map-container').setAttribute("style", "height:" + tmpHeight + "px");
      leafletData.getMap('map').then(function (map) {
        map.invalidateSize();
      });
    }

    function setGallerySize() {
      //
      document.getElementById('pic-container').setAttribute("style", "width:" + ($scope.legs.length * 100) + "px");
    }
    $scope.$on('$destroy', function () {
      window.angular.element($window).off('resize', onResize);
    })

    function onResize() {
      setMapSize();
    }
    $scope.$on('$destroy', function () {

      window.angular.element($window).off('resize', onResize);
    })

    var appWindow = angular.element($window);
    appWindow.bind('resize', onResize);

    $scope.$on('leafletDirectiveMarker.map.click', function (e, args) {
      console.log("click");
      $scope.selectedPosition = Number(args.modelName);
      if($scope.selectedPosition) {
      	$scope.selectedLeg = $scope.legs[$scope.selectedPosition];
      	$scope.firstInteraction = true;
      }
      // Args will contain the marker name and other relevant information
      //console.log(args);
      var markerName = args.leafletEvent.target.options.name; //has to be set above
      //marker is clickable and already reached
      if (args.model.message) {
        
        leafletData.getMap('map').then(function (map) {
          map.setView([args.model.lat + configService.DEFAULT_POI_POPUP_OFFSET, args.model.lng], configService.getDefaultZoomPoiConstant());
          
          $scope.selectedPosition = Number(args.modelName);
          $scope.scrollToPoint($scope.legs[$scope.selectedPosition].position);
        }, function (err) {});        
      }
    });
    

    $scope.scroll = function (direction) {
      var parent = $window.document.getElementById('external-urls-viewer'); 
      if (direction === 'up') {
        parent.scrollTop -= parent.firstElementChild.offsetHeight;
      } else if (direction === 'down') {
        parent.scrollTop += parent.firstElementChild.offsetHeight;
      }
    }

    $scope.sanitizeHtmlString = function(string) {
      return $sce.trustAsHtml(string);
    }
    $scope.floor = function(number) {
      return Math.floor(number);
    }

  }]);
