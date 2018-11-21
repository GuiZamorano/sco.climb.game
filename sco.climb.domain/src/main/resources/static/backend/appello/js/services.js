angular.module('DataService', []).factory('DataService',
		[ '$q', '$http', '$rootScope', function($q, $http, $rootScope) {
			var dataService = {};
			var getUrl = window.location;
			var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
			var profileToken;

			dataService.logout = function() {
				var data = $q.defer();
				$http.post('logout', {}).success(function() {
					data.resolve();
				}, function() {
					data.resolve();
				});
				return data.promise;
			};
			dataService.getProfile = function() {
				var deferred = $q.defer();
				$http.get(baseUrl + '/console/data').success(function(data) {
					profileToken = data.token;
					deferred.resolve(data);
				}).error(function(e) {
					deferred.reject(e);
				});
				return deferred.promise;
			}
			dataService.getDownload = function (domain, institute, school, route, dateFrom, dateTo) {
				var deferred = $q.defer()
				$http({
				  method: 'GET',
				  url: baseUrl + '/api/report/attendance/' + domain + '/' + institute + '/' + school + '/' + route, 
				  headers: {
					'Accept': 'application/json',
					'Authorization': 'Bearer ' + profileToken
				  },
				  responseType: "arraybuffer",
				  timeout: 10000,
				  params: {
					dateFrom: dateFrom,
					dateTo: dateTo
				  }
				}).then(function (response, status, headers) {
				  deferred.resolve(response)
				}, function (reason) {
				  console.log(reason)
				  deferred.reject(reason)
				})
				return deferred.promise
			  }

			return dataService;
		}
	]
);
