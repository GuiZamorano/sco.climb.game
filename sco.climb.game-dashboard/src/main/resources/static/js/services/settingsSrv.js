angular.module('climbGame.services.settings', [])
    .factory('settingsService', function ($q, $filter, dataService) {
        var settingsService = {}

        settingsService.getAlreadySelectedSubjects = function() {
            var deferr = $q.defer();
            dataService.getAlreadySelectedSubjects().then(function (data) {
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        settingsService.getSettingsSubjects = function () {
            var deferr = $q.defer();
            dataService.getSettingsSubjects().then(function (data) {
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        settingsService.getSettingsTeks = function () {
            var deferr = $q.defer();
            dataService.getSettingsTeks().then(function (data) {
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        settingsService.getSettingsGradeLevels = function () {
            var deferr = $q.defer();
            dataService.getSettingsGradeLevels().then(function (data) {
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        settingsService.saveSettings = function(settings) {
            var deferr = $q.defer();
            dataService.saveSettings(settings).then(function (data) {
                deferr.resolve(data);
            }, function (err) {
                deferr.reject();
            });
            return deferr.promise;
        }

        return settingsService;
    })
