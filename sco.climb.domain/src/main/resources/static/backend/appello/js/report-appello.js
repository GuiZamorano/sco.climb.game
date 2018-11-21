var searchTableApp = angular.module('report-appello', ['DataService', 'ngclipboard', 'ngFileSaver']);

var searchTableCtrl = searchTableApp.controller('userCtrl', function($scope, $http, $location, $window, DataService, FileSaver, Blob) {
	$scope.selectedMenu = "report";
	$scope.selectedTab = "menu-report-appello";
	$scope.language = "it";
	$scope.defaultLang = "it";
	$scope.itemToDelete = "";

	$scope.edit = false;
	$scope.create = false;
	$scope.view = false;
	$scope.search = "";
	$scope.incomplete = true;

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
	var baseAppUrl = $location.$$absUrl.replace($location.$$path,'');
	var logoutUrl = baseUrl + '/logout?target=' + baseAppUrl;
    $window.location.href = logoutUrl;
  }
  
	DataService.getProfile().then(
		function(p) {
			$scope.initData(p);
		},
		function(e) {
			console.log(e);
			$scope.error = true;
			$scope.errorMsg = e.errorMsg;
		}
	);
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		$scope.ownerList = $scope.profile.ownerIds;
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
		+ "/" + $scope.selectedSchool.objectId;
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

	$scope.downloadReport = function() {
		if($scope.incomplete) {
			return;
		}
		var dateFrom = $scope.fDateFrom;
		if($scope.fHourFrom) {
			dateFrom = dateFrom + "T" + $scope.fHourFrom; 
		}
		var dateTo = $scope.fDateTo;
		if($scope.fHourTo) {
			dateTo = dateTo + "T" + $scope.fHourTo;
		}

		DataService.getDownload($scope.selectedOwner, $scope.selectedInstitute.objectId, $scope.selectedSchool.objectId, $scope.selectedRoute.objectId, dateFrom, dateTo).then(
			function(response) {
       			var blob = new Blob([response.data], { type: response.headers('Content-Type') });
				var contentDispositionHeader = response.headers('Content-Disposition');
				var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
				var matches = filenameRegex.exec(contentDispositionHeader);
				if (matches != null && matches[1]) { 
					filename = matches[1].replace(/['"]/g, '');
				}

				FileSaver.saveAs(blob, filename);				
			},
			function(e) {
				console.log(e);
				$scope.error = true;
				$scope.errorMsg = e.errorMsg;
			}
		);		
	};
	
	$scope.getRouteName = function(item) {
		var dateFrom = moment(item.from);
		var dateTo = moment(item.to);
		return item.name + " [" + dateFrom.format('DD/MM/YYYY') + " - " + dateTo.format('DD/MM/YYYY') + "]";
	};
	
	$scope.getEventTimestamp = function(item) {
		var day = moment(item.timestamp);
		var result = day.format('DD/MM/YYYY, hh:mm:ss'); 
		return result;
	};
	
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
