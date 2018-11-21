angular.module('MainDataService', []).factory('MainDataService', function ($rootScope, $http, $q, DataService) {
    var mainDataService = {};

    var domains, institutes, schools, games, itineraries, gamesConfigs;
    var currentDomain, currentInstitute, currentSchool, currentGame;



    mainDataService.getDomains = function () {
        var deferred = $q.defer();
        
        if (domains == undefined) {
            DataService.getProfile().then(function (profile) {
                domains = profile;
                DataService.setProfileToken(profile.token);
                deferred.resolve(domains);
            }, function() {
                $rootScope.networkProblemDetected("Errore caricamento domini!");
            });
        } else {
            deferred.resolve(domains);
        }

        return deferred.promise;
    }

    mainDataService.getInstitutes = function (ownerID) {
        var deferred = $q.defer();
        
        if (institutes == undefined || ownerID != currentDomain) {
            DataService.getInstitutesList(ownerID).then(function (data) {
                institutes = data;
                currentDomain = ownerID;
                deferred.resolve(institutes);                
            }, function() {
                $rootScope.networkProblemDetected("Errore caricamento istituti!");
            });
        } else {
            deferred.resolve(institutes);
        }

        return deferred.promise;
    }

    mainDataService.getSchools = function (instituteID) {
        var deferred = $q.defer();
        
        if (schools == undefined || instituteID != currentInstitute) {
            DataService.getData('school', currentDomain, instituteID).then(function (data) {
                schools = data;
                currentInstitute = instituteID;
                gamesConfigs = undefined;
                deferred.resolve(schools);
            }, function() {
                $rootScope.networkProblemDetected("Errore caricamento scuole!");
            });
        } else {
            deferred.resolve(schools);
        }

        return deferred.promise;
    }

    mainDataService.getGames = function (schoolID) {
        var deferred = $q.defer();
        
        if (games == undefined || schoolID != currentSchool) {
            DataService.getData('game', currentDomain, currentInstitute, schoolID).then(function (data) {
                games = data;
                currentSchool = schoolID;
                deferred.resolve(games);
            }, function() {
                $rootScope.networkProblemDetected("Errore caricamento giochi!");
            });
        } else {
            deferred.resolve(games);
        }

        return deferred.promise;
    }

    mainDataService.getItineraries = function (gameID) {
        var deferred = $q.defer();
        
        if (itineraries == undefined || gameID != currentGame) {
            DataService.getData('itinerary', currentDomain, currentInstitute, currentSchool, null, gameID).then(function (data) {
                itineraries = data;
                currentGame = gameID;
                deferred.resolve(itineraries);
            }, function() {
                $rootScope.networkProblemDetected("Errore caricamento itinerari!");
            });
        } else {
            deferred.resolve(itineraries);
        }

        return deferred.promise;
    }

    mainDataService.getGamesConfigs = function (schoolID, invalidate) {
        var deferred = $q.defer();
        
        if (invalidate || gamesConfigs == undefined || schoolID != currentSchool) {
            DataService.getGameConfData('gameconfigsummary', {"ownerId": currentDomain, "instituteId": currentInstitute, "schoolId": schoolID}).then(function (data) {
                gamesConfigs = data;
                deferred.resolve(gamesConfigs);
            }, function() {
                $rootScope.networkProblemDetected("Errore caricamento configurazione giochi!");
            });
        } else {
            deferred.resolve(gamesConfigs);
        }

        return deferred.promise;
    }

    mainDataService.setSelectedDomain = function(domain) {
        currentDomain = domain;
    }
    mainDataService.setSelectedGame = function(game) {
        currentGame = game;
    }

    mainDataService.getSelectedDomain = function() {
        return currentDomain;
    }


    return mainDataService;
});