angular.module('climbGame.services.calendar', [])
    .factory('statsService', function ($q, $filter, dataService, loginService) {
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
    })