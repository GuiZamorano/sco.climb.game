var searchTableApp = angular.module('search-table', ['DataService', 'ngclipboard']);

var searchTableCtrl = searchTableApp.controller('userCtrl', function($scope, $location, $http, $window, DataService) {
	DataService.getProfile().then(
	function(p) {
		$scope.initData(p);
	},
	function(e) {
		console.log(e);
		$scope.error = true;
		$scope.errorMsg = e.errorMsg;
	});

	$scope.selectedMenu = "ricerca";
	$scope.selectedTab = "menu-search-map";
	$scope.language = "it";
	$scope.defaultLang = "it";
	$scope.itemToDelete = "";

	$scope.edit = false;
	$scope.create = false;
	$scope.view = false;
	$scope.search = "";
	$scope.incomplete = false;

	$scope.error = false;
	$scope.errorMsg = "";

	$scope.ok = false;
	$scope.okMsg = "";

	$scope.data = null;
	$scope.status = 200;
	
	$scope.fEventType = -1;
	$scope.fCopyText = "";
	var today = moment();
	$scope.fDateFrom = today.format('YYYY-MM-DD');
	$scope.fDateTo = today.format('YYYY-MM-DD');
	$scope.fHourFrom = "07:30:00";
	$scope.fHourTo = "08:30:00";
	$scope.eventTypeList = [];
	$scope.events = null;
	$scope.routeList = null;
	$scope.schoolList = null;
	$scope.instituteList = null;
	$scope.ownerList = null;
	$scope.selectedOwner = null;
	$scope.selectedInstitute = null;
	$scope.selectedSchool = null;
	$scope.selectedRoute = null;

	var getUrl = window.location;
	var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];

  $scope.logout = function() {
    var logoutUrl = baseUrl + '/logout?target=' + $location.path('/').absUrl();
    $window.location.href = logoutUrl;
  }

	$scope.initData = function(profile) {
		$scope.profile = profile;
		$scope.ownerList = $scope.profile.ownerIds;
		$scope.eventTypeList = [
 		 {
 			 'name' : 'TUTTI',
 			 'value' : -1
 		 },
 		 {
 			 	'name' : 'NODE_IN_RANGE',
      		'value' : 101
      	},
      	{
      		'name' : 'NODE_CHECKIN',
      		'value' : 102
      	},
      	{
      		'name' : 'NODE_CHECKOUT',
      		'value' : 103
      	},
      	{
      		'name' : 'NODE_AT_DESTINATION',
      		'value' : 104
      	},
      	{
      		'name' : 'NODE_OUT_OF_RANGE',
      		'value' : 105
      	},
  		 {
  			 'name' : 'ANCHOR_IN_RANGE',
  			 'value' : 201
  		 },
 		 {
 			 'name' : 'STOP_LEAVED',
 			 'value' : 202
 		 },
 		 {
 			 'name' : 'SET_DRIVER',
 			 'value' : 301
 		 },
 		 {
 			 'name' : 'SET_HELPER',
 			 'value' : 302
 		 },
 		 {
 			 'name' : 'DRIVER_POSITION',
 			 'value' : 303
 		 },
 		 {
 			 'name' : 'START_ROUTE',
 			 'value' : 401
 		 },
 		 {
 			 'name' : 'END_ROUTE',
 			 'value' : 402
 		 },
 		 {
 			 'name' : 'TEST_STATE',
 			 'value' : 901
 		 } 		 
 		];
	};
	
	$scope.changeOwner = function() {
		var urlInstituteList = baseUrl + "/api/institute/" + $scope.selectedOwner;
		$http.get(urlInstituteList, {headers: {'Authorization': 'Bearer ' + $scope.profile.token}}).then(
			function (response) {
				$scope.instituteList = response.data;
			},
			function(response) {
				console.log(response.data);
				$scope.error = true;
				$scope.errorMsg = response.data.errorMsg;
			}
		);
	}
	
	$scope.changeInstitute = function() {
		var urlSchoolList = baseUrl + "/api/school/" + $scope.selectedOwner
		+ "/" + $scope.selectedInstitute.objectId;
		$http.get(urlSchoolList, {headers: {'Authorization': 'Bearer ' + $scope.profile.token}}).then(
			function (response) {
				$scope.schoolList = response.data;
			},
			function(response) {
				console.log(response.data);
				$scope.error = true;
				$scope.errorMsg = response.data.errorMsg;
			}
		);		
	}
	
	$scope.changeSchool = function() {
		var urlRouteList = baseUrl + "/api/route/" + $scope.selectedOwner 
		+ "/" + $scope.selectedInstitute.objectId
		+ "/" + $scope.selectedSchool.objectId
		$http.get(urlRouteList, {headers: {'Authorization': 'Bearer ' + $scope.profile.token}}).then(
		function (response) {
			$scope.routeList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});
	}
	
	$scope.doSearch = function(item) {
		var q = $scope.search.toLowerCase();
		var text;
		
		text = $scope.getEventName(item).toLowerCase();
		if(text.indexOf(q) != -1) {
			return true;
		}
		
		text = $scope.getEventTimestamp(item);
		if(text.indexOf(q) != -1) {
			return true;
		}
		
		if(item.wsnNodeId) {
			text = item.wsnNodeId.toLowerCase();
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		
		if(item.payload) {
			text = JSON.stringify(item.payload);
			if(text.indexOf(q) != -1) {
				return true;
			}
		}
		
		return false;
	};
	
	$scope.setNameMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].nome;
			map[key] = name;
		}
		return map;
	};

	$scope.setLocalNameMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].nome[$scope.language];
			map[key] = name;
		}
		return map;
	};

	$scope.resetError = function() {
		$scope.error = false;
		$scope.errorMsg = "";
	};

	$scope.resetOk = function() {
		$scope.ok = false;
		$scope.okMsg = "";
	};

	$scope.getModalHeaderClass = function() {
		if($scope.view) {
			return "view";
		}
		if($scope.edit) {
			return "edit";
		}
		if($scope.create) {
			return "create";
		}
	};

	$scope.setItemToDelete = function(id) {
		$scope.itemToDelete = id;
	};

	$scope.changeLanguage = function(language) {
		$scope.language = language;
	};

	$scope.resetUI = function() {
		$scope.search = "";
		$scope.incomplete = false;
		$('html,body').animate({scrollTop:0},0);
	};

	$scope.resetForm = function() {
		$sopne.fEventType = -1;
		$scope.fCopyText = "";
		$scope.fDateFrom = "";
		$scope.fDateTo = "";
		$scope.fHourFrom = "";
		$scope.fHourTo = "";
		$scope.selectedSchool = null;
		$scope.selectedRoute = null;
	};

	$scope.searchItem = function() {
		$window.spinner.spin($window.spinTarget);
		//var dateFrom = "2016-03-11T09:00:00";
		//var dateTo = "2016-03-11T12:00:00";
		var dateFrom = $scope.fDateFrom;
		if($scope.fHourFrom) {
			dateFrom = dateFrom + "T" + $scope.fHourFrom; 
		}
		var dateTo = $scope.fDateTo;
		if($scope.fHourTo) {
			dateTo = dateTo + "T" + $scope.fHourTo;
		}
		
		var urlSearch = baseUrl + "/api/event/" + $scope.selectedOwner
		+ "/" + $scope.selectedRoute.objectId
		+ "?dateFrom=" + dateFrom + "&dateTo=" + dateTo;
		
		if($scope.fEventType > 0) {
			urlSearch = urlSearch + "&eventType[]=" + $scope.fEventType;
		}
		
		//console.log("urlSearch:" + urlSearch);
		$http.get(urlSearch, {headers: {'Authorization': 'Bearer ' + $scope.profile.token}}).then(
		function (response) {
			$scope.events = response.data;
			$window.spinner.stop();
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
			$window.spinner.stop();
		});
	}

	$scope.getEventName = function(item) {
		var result = "";
		switch (item.eventType) {
		case 101:
			result = "NODE_IN_RANGE";
			break;
		case 102:
			result = "NODE_CHECKIN";
			break;
		case 103:
			result = "NODE_CHECKOUT";
			break;
		case 104:
			result = "NODE_AT_DESTINATION";
			break;
		case 105:
			result = "NODE_OUT_OF_RANGE";
			break;
		case 201:
			result = "ANCHOR_IN_RANGE";
			break;
		case 202:
			result = "STOP_LEAVED";
			break;
		case 301:
			result = "SET_DRIVER";
			break;
		case 302:
			result = "SET_HELPER";
			break;
		case 303:
			result = "DRIVER_POSITION";
			break;
		case 401:
			result = "START_ROUTE";
			break;
		case 402:
			result = "END_ROUTE";
			break;
		}
		return result;
	};
	
	$scope.getRouteName = function(item) {
		var dateFrom = moment(item.from);
		var dateTo = moment(item.to);
		return item.name + " [" + dateFrom.format('DD/MM/YYYY') + " - " + dateTo.format('DD/MM/YYYY') + "]";
	};
	
	$scope.getEventTimestamp = function(item) {
		var day = moment(item.timestamp);
		var result = day.format('DD/MM/YYYY, HH:mm:ss'); 
		return result;
	};
	
	$scope.getResponseSize = function() {
		if($scope.events) {
			return $scope.events.length;
		} else {
			return "";
		}
	}
	
	$scope.copyItem = function(item) {
		return JSON.stringify(item);
	};

	$scope.$watch('selectedSchool',function() {$scope.test();}, true);
	$scope.$watch('selectedRoute',function() {$scope.test();}, true);
	$scope.$watch('fDateFrom',function() {$scope.test();}, true);
	$scope.$watch('fDateTo',function() {$scope.test();}, true);

	$scope.test = function() {
		if(($scope.selectedSchool == null) ||
		($scope.selectedRoute == null) ||		
		(!$scope.fDateFrom) || 
		(!$scope.fDateTo)) {
			$scope.incomplete = true;
		} else {
			$scope.incomplete = false;
		}
	};

	$scope.findByObjectId = function(array, id) {
    for (var d = 0, len = array.length; d < len; d += 1) {
      if (array[d].objectId === id) {
          return array[d];
      }
    }
    return null;
	};

	$scope.findIndex = function(array, id) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if (array[d].objectId === id) {
				return d;
			}
		}
		return -1;
	};

});

searchTableApp.directive('datepicker', function() {
  return {
    restrict: 'A',
    require : 'ngModel',
    link : function (scope, element, attrs, ngModelCtrl) {
    	$(function(){
    		element.datepicker("option", $.datepicker.regional['it']);
    		element.datepicker({
    			showOn: attrs['showon'],
          buttonImage: "lib/jqueryui/images/ic_calendar.png",
          buttonImageOnly: false,
          buttonText: "Calendario",
          dateFormat: attrs['dateformat'],
          minDate: "-1Y",
          maxDate: "+2Y",
          onSelect:function (date) {
          	scope.$apply(function () {
          		ngModelCtrl.$setViewValue(date);
            });
          }
        });
      });
    }
  }
});

searchTableApp.directive('myMap', function() {
  // directive link function
  var link = function($scope, element, attrs) {
      var map, infoWindow;
      var markers = [];
      
      // map config
      var mapOptions = {
      		disableDoubleClickZoom: true,
      		scrollwheel: false,
      		streetViewControl: false,
      		rotateControl: false,
      		mapTypeControlOptions: {
      			style:google.maps.MapTypeControlStyle.DROPDOWN_MENU,
      			position: google.maps.ControlPosition.LEFT_TOP
      		},
      		tilt: 0,
          zoom: 15,
          center: new google.maps.LatLng(46.122666, 11.116963),
          mapTypeId: google.maps.MapTypeId.ROADMAP // ROADMAP | SATELLITE | HYBRID | TERRAIN
      };
      
      // init the map
      function initMap() {
          if (map === void 0) {
              map = new google.maps.Map(element[0], mapOptions);
          }
      }    
      
      // place a marker
      function setMarker(map, position, title, content) {
          var marker;
          var markerOptions = {
              position: position,
              map: map,
              title: title,
              icon: 'https://maps.google.com/mapfiles/ms/icons/green-dot.png'
          };

          marker = new google.maps.Marker(markerOptions);
          markers.push(marker); // add marker to array
          
          google.maps.event.addListener(marker, 'click', function () {
              // close window if not undefined
              if (infoWindow !== void 0) {
                  infoWindow.close();
              }
              // create new window
              var infoWindowOptions = {
                  content: content
              };
              infoWindow = new google.maps.InfoWindow(infoWindowOptions);
              infoWindow.open(map, marker);
          });
      }
      
      // show the map and place some markers
      initMap();
      
      $scope.$watch('events', function(newVal, oldVal) {
        if($scope.events) {
        	for (var i = 0; i < markers.length; i++ ) {
            markers[i].setMap(null);
          }
        	markers = [];
        	for (var d = 0, len = $scope.events.length; d < len; d += 1) {
        		var item = $scope.events[d];
        		var latitude = item.payload.latitude;
        		var longitude = item.payload.longitude;
        		var accuracy = item.payload.accuracy;
        		var title = $scope.getEventName(item) + " - " + $scope.getEventTimestamp(item) 
        		+ " [" + accuracy + "]";
        		setMarker(map, new google.maps.LatLng(latitude, longitude), title, title);
        	}
        }
      });
  };
  
  return {
      restrict: 'A',
      template: '<div id="gmaps"></div>',
      replace: true,
      link: link
  };	
});