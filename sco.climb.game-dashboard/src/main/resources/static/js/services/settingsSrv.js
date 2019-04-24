angular.module('climbGame.services.settings', [])
    .factory('settingsService', function ($q, $filter, dataService) {
        var settingsService = {}

        settingsService.getActivitySubjects = function () {
            var deferr = $q.defer();
            dataService.getActivitySubjects().then(function (data) {
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        settingsService.getActivityTeks = function () {
            var deferr = $q.defer();
            dataService.getActivityTeks().then(function (data) {
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        settingsService.getActivityGrades = function () {
            var deferr = $q.defer();
            dataService.getActivityGrades().then(function (data) {
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        settingsService.saveSettings = function(settings) {
            var deferr = $q.defer();
            dataService.saveSettings(settings).then(function (data) {       //idk if this is supposed to match settings
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        return settingsService;
    })
