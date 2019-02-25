angular.module('climbGame.controllers.calendarStats', [])
    .controller('calendarStatsCtrl', ['$scope', '$http', 'uiCalendarConfig', 'dataService',
    function ($scope, $http, uiCalendarConfig, dataService) {

    $scope.index = ''
    $scope.calendarView = 0
    $scope.activityLevel = ["Extremely Active", "Very Active", "Fairly Active", "Inactive"]
    $scope.colorLevel = ['#99FF99', '#FFFF99', '#FF6666', '#CCCCCC']

$scope.switchCalendar = function () {
       if($scope.calendarView == 0){
        $scope.calendarView = 1;
        $scope.calendarDisplaySwitch()
        } else if ($scope.calendarView == 1){
        $scope.calendarView = 0
        $scope.calendarDisplaySwitch()
        }
        }

 $scope.calendarDisplaySwitch = function(){
       if($scope.calendarView == 0){
       dataService.getIndex().then(
          function(index) {
          $scope.index = index
          dataService.getCalendar(0, $scope.index).then(
              function (events) {
              $scope.events.splice(0,$scope.events.length)
                  for (var i = 0; i<events.length; i++){
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
                  }
                  });
          })


              }
          else if($scope.calendarView == 1){
                 dataService.getIndex().then(
                    function(index) {
                    $scope.index = index
                    dataService.getCalendar(0, $scope.index).then(
                        function (events) {
                        var dates = []
                        for (var i = 0; i< events.length-1; i++){
                        if(!dates.includes(events[i].day))
                            dates.push(events[i].day)
                        }

                        $scope.events.splice(0,$scope.events.length)
                            for (var i = 0; i<dates.length; i++){
                                var EA=0; FA=0; VA=0; IA =0;
                                var meteoDay = ''
                                for(var j =0; j<events.length-1; j++){
                                  if(events[j].day == dates[i]){
                                    EA+=events[j].eactive
                                    VA+=events[j].vactive
                                    FA+=events[j].factive
                                    IA+=events[j].iactive
                                    meteoDay = events[j].meteo
                                  }
                                }
                                var aLevel = [EA, VA, FA, IA]
                                for(var k = 0; k < 4; k++){
                                $scope.events.push({
                                    title: $scope.activityLevel[k]+": " + aLevel[k],
                                    meteo: meteoDay,
                                    eactive: EA,
                                    vactive: VA,
                                    factive: FA,
                                    iactive: IA,
                                    start: dates[i],
                                    id: k,
                                    textColor: 'black',
                                    color: $scope.colorLevel[k],
                                    stick: true
                                    });
                            }
                            }
                            });
                    })

                        }
                        }

$scope.calendarDisplaySwitch()



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