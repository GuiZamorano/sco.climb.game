var searchTableApp = angular.module('check-nodes', ['DataService', 'ngclipboard']);

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
	$scope.selectedTab = "menu-check-nodes";
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
	
	$scope.fCopyText = "";
	var today = moment();
	$scope.fDateFrom = today.format('YYYY-MM-DD');
	$scope.fDateTo = today.format('YYYY-MM-DD');
	$scope.fHourFrom = "07:30:00";
	$scope.fHourTo = "08:30:00";
	$scope.eventTypeList = [];
	$scope.eventTypeSelected = {};
	$scope.events = null;
	$scope.routeList = null;
	$scope.schoolList = null;
	$scope.instituteList = null;
	$scope.ownerList = null;
	$scope.selectedOwner = null;
	$scope.selectedInstitute = null;
	$scope.selectedSchool = null;
	$scope.selectedRoute = null;
	$scope.childMap = {};

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
		 }
		];
		
		for(var i = 0; i < $scope.eventTypeList.length; i++ ) {
			var item = $scope.eventTypeList[i];
			$scope.eventTypeSelected[item.value] = false;
		}		
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
			
			var urlChildList = baseUrl + "/api/child/" + $scope.selectedOwner
			+ "/" + $scope.selectedInstitute.objectId
			+ "/" + $scope.selectedSchool.objectId
			$http.get(urlChildList, {headers: {'Authorization': 'Bearer ' + $scope.profile.token}}).then(
				function (response) {
					var childList = response.data;
					for(var i = 0; i < childList.length; i++ ) {
						var child = childList[i];
						$scope.childMap[child.objectId] = child;
					}
				},
				function (response) {
					console.log(response.data);
					$scope.error = true;
					$scope.errorMsg = response.data.errorMsg;
				}
			);
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
		
		text = $scope.getChildName(item).toLowerCase();
		if(text.indexOf(q) != -1) {
			return true;
		}
		
		if(item.wsnNodeId) {
			text = item.wsnNodeId.toLowerCase();
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
		var dateTo = $scope.fDateFrom;
		if($scope.fHourTo) {
			dateTo = dateTo + "T" + $scope.fHourTo;
		}
		
		var urlSearch = baseUrl + "/api/event/check/" + $scope.selectedOwner 
		+ "/" + $scope.selectedRoute.objectId
		+ "?dateFrom=" + dateFrom + "&dateTo=" + dateTo;
		
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
		case -1:
			result = "TUTTI";
			break;
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
	
	$scope.getChildName = function(item) {
		var child = $scope.childMap[item.passengerId];
		if(child != null) {
			return child.name + " " + child.surname;
		} else {
			return "";
		}
	}
	
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
	
	$scope.getSelectedEventTypes = function() {
		var result = [];
		for(var i = 0; i < $scope.eventTypeList.length; i++ ) {
			var item = $scope.eventTypeList[i];
			if($scope.eventTypeSelected[item.value]) {
				result.push(item);
			}
		}
		return result;
	}
	
	$scope.copyItem = function(item) {
		return JSON.stringify(item);
	};

	$scope.$watch('selectedSchool',function() {$scope.test();}, true);
	$scope.$watch('selectedRoute',function() {$scope.test();}, true);
	$scope.$watch('fDateFrom',function() {$scope.test();}, true);

	$scope.test = function() {
		if(($scope.selectedSchool == null) ||
		($scope.selectedRoute == null) ||		
		(!$scope.fDateFrom)) {
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
