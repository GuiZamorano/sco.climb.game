angular.module('climbGame.services.calendar', [])
  .factory('calendarService', function ($q, $filter, dataService, loginService) {
    var calendarService = {};
    calendarService.getCalendar = function (from, to) {
      var deferr = $q.defer();
      dataService.getCalendar(from, to).then(function (data) {
        //return only the path and the legs
        deferr.resolve(data);
      }, function (err) {
        deferr.reject();
      });
      return deferr.promise;
    }
    calendarService.getClassPlayers = function () {
      var deferr = $q.defer();
      dataService.getClassPlayers().then(function (data) {
        //return only the path and the legs
        deferr.resolve(data);
      }, function (err) {
        deferr.reject();
      });
      return deferr.promise;
    }
    calendarService.sendData = function (todayData) {
      var deferr = $q.defer();
      dataService.sendData(todayData).then(function (data) {
        //return only the path and the legs
        deferr.resolve(data);
      }, function (err) {
        deferr.reject();
      });
      return deferr.promise;
    }

    calendarService.setTitle = function () {
      var deferr = $q.defer();
      dataService.getStatus().then(function (data) {
        //getStatus
        loginService.setTitle(data.game.globalTeam + " - " + $filter('translate')('title_class') + " " + loginService.getClassRoom());
        deferr.resolve();
      }, function (err) {
        deferr.reject();
      });
      return deferr.promise;
    }
    return calendarService;
  });
