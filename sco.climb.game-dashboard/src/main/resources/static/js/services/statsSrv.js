angular.module('climbGame.services.stats', [])
    .factory('statsService', function ($q, $filter, dataService) {
        var statsService = {};
        statsService.getMathStats = function (from, to) {
            var deferr = $q.defer();
            dataService.getCalendar(from, to).then(function (data) {
                //return only the path and the legs
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        statsService.getIndex = function () {
            var deferr = $q.defer();
            dataService.getIndex().then(function (data) {
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }
        return statsService;
    })
