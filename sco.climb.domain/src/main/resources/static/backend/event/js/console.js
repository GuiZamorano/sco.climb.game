angular.module('console', ['DataService']).controller('userCtrl', function($scope, $location, $window, DataService) {
	DataService.getProfile().then(function(p) {
  	$scope.initData(p);
  });

	$scope.selectedTab = "";
	$scope.language = "it";

	$scope.initData = function(profile) {
		$scope.profile = profile;
	}
	
  $scope.logout = function() {
  	var getUrl = window.location;
  	var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
    var logoutUrl = baseUrl + '/logout?target=' + $location.path('/').absUrl();
    $window.location.href = logoutUrl;
  }
	
});
