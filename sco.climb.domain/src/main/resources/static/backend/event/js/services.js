angular.module('DataService', []).factory('DataService',
		[ '$q', '$http', '$rootScope', function($q, $http, $rootScope) {
			var logout = function() {
				var data = $q.defer();
				$http.post('logout', {}).success(function() {
					data.resolve();
				}, function() {
					data.resolve();
				});
				return data.promise;
			};

			return {
				getProfile : function() {
					var deferred = $q.defer();
					var getUrl = window.location;
					var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
					$http.get(baseUrl + '/console/data').success(function(data) {
						deferred.resolve(data);
					}).error(function(e) {
						deferred.reject(e);
					});
					return deferred.promise;
				},

				logout : logout
			};
		} ]);
