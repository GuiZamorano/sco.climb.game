angular.module('climbGame.controllers.calendarStats', [])
    .controller('calendarStatsCtrl', ['$scope', '$http', 'uiCalendarConfig', 'dataService',
    function ($scope, $http, uiCalendarConfig, dataService) {

    $scope.index = ''


    dataService.getIndex().then(
          function(index) {
          $scope.index = index
          dataService.getCalendar(0, $scope.index).then(
              function (events) {
                  for (var i = 0; i<events.length; i++)
                      $scope.events.push({
                          title: events[i].name,
                          meteo: events[i].meteo,
                          eactive: events[i].eactive,
                          vactive: events[i].vactive,
                          factive: events[i].factive,
                          iactive: events[i].iactive,
                          start: events[i].day,
                          stick: true
                      });
                  });
          })



    $scope.SelectedEvent = null;
    var isFirstTime = true;

    $scope.events = [];
    $scope.eventSources = [$scope.events];


    //Load events from server
    //$scope.getEvents = function() {

   // };

    //configure calendar
    $scope.uiConfig = {
        calendar: {
            height: 450,
            editable: true,
            displayEventTime: false,
            header: {
                left: 'month basicWeek basicDay agendaWeek agendaDay',
                center: 'title',
                right:'today prev,next'
            },
            eventClick: function (event) {
                $scope.SelectedEvent = event;
            },
            eventAfterAllRender: function () {
                if ($scope.events.length > 0 && isFirstTime) {
                    //Focus first event
                    uiCalendarConfig.calendars.myCalendar.fullCalendar('gotoDate', $scope.events[0].start);
                    isFirstTime = false;
                }
            }
        }
    };

}])