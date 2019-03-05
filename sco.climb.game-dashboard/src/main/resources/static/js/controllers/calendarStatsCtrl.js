angular.module('climbGame.controllers.calendarStats', [])
    .controller('calendarStatsCtrl', ['$scope', '$http', 'uiCalendarConfig', 'dataService',
    function ($scope, $http, uiCalendarConfig, dataService) {

    $scope.index = ''
    $scope.calendarView = 0
    $scope.activityLevel = ["Extremely Active", "Very Active", "Fairly Active", "Inactive"]
    $scope.colors = ["red", "blue", "orange", "green", "purple", "yellow", "brown", "white", "gray", "black"]
    $scope.colorLevel = ['#66BB6A', '#FFEE58', '#EF5350', '#F2F2F2']
    $scope.imperial = false

$scope.switchCalendar = function () {
       if($scope.calendarView == 0){
        $scope.calendarView = 1;
        $scope.calendarDisplaySwitch()
        } else if ($scope.calendarView == 1){
        $scope.calendarView = 0
        $scope.calendarDisplaySwitch()
        }
        }

 $scope.switchUnit = function () {
         $scope.imperial = !$scope.imperial
         $scope.calendarDisplaySwitch()
 }

 $scope.roundToPlaces = function(num, places){
         return +(Math.round(num + "e+" + places)  + "e-" + places)
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
                        if(events[i].index >= 0){
                          var displaydist = ""
                          if ($scope.imperial) displaydist += $scope.roundToPlaces(events[i].distance*1.61,2) + " Km"
                          else displaydist += events[i].distance + " Miles"

                          $scope.events.push({
                              title: events[i].name + ": " + displaydist,
                              activityName: events[i].name,
                              meteo: events[i].meteo,
                              eactive: events[i].eadistance,
                              vactive: events[i].vadistance,
                              factive: events[i].fadistance,
                              iactive: 0,
                              start: events[i].day,
                              stick: true
                          });
                        }
                      }
                  });
              }
           )


       } else if($scope.calendarView == 1){
             dataService.getIndex().then(
                function(index) {
                    $scope.index = index
                    dataService.getCalendar(0, $scope.index).then(
                        function (events) {
                            var dates = []
                            for (var i = 0; i< events.length-1; i++){
                                if(events[i].index >= 0 && !dates.includes(events[i].day))
                                if(!dates.includes(events[i].day))
                                    dates.push(events[i].day)
                            }

                            $scope.events.splice(0,$scope.events.length)
                            for (var i = 0; i<dates.length; i++){
                                var EA=0; FA=0; VA=0; IA =0; distance = 0;
                                var meteoDay = ''
                                var event = []
                                var eDistanceArr = []
                                var vDistanceArr = []
                                var fDistanceArr = []
                                for(var j =0; j<events.length-1; j++){
                                  if(events[j].index >= 0 && events[j].day == dates[i]){
                                    EA+=events[j].eadistance
                                    VA+=events[j].vadistance
                                    FA+=events[j].fadistance
                                    distance+=events[j].distance
                                    meteoDay = events[j].meteo
                                    event.push(events[j].name)
                                    eDistanceArr.push(events[j].eadistance)
                                    vDistanceArr.push(events[j].vadistance)
                                    fDistanceArr.push(events[j].fadistance)
                                  }
                                }
                                var distanceLevel = [eDistanceArr, vDistanceArr, fDistanceArr, 0]
                                var aLevel = [EA, VA, FA, IA]
                                for(var k = 0; k < 3; k++){

                                    var displayindividualdist = ""
                                    var displaytotdist = ""
                                    if ($scope.imperial) {
                                        displayindividualdist += $scope.roundToPlaces(aLevel[k]*1.61,2) + " Km"
                                        displaytotdist += $scope.roundToPlaces(distance*1.61,2) + " Km"
                                    }
                                    else {
                                        displayindividualdist += aLevel[k] + " Miles"
                                        displaytotdist += distance + " Miles"
                                    }

                                    $scope.events.push({
                                        title: $scope.activityLevel[k]+ ": " + displayindividualdist,
                                        activityName: $scope.activityLevel[k],
                                        meteo: meteoDay,
                                        eactive: EA,
                                        vactive: VA,
                                        factive: FA,
                                        iactive: IA,
                                        event: event,
                                        distanceLevel: distanceLevel[k],
                                        start: dates[i],
                                        id: k,
                                        textColor: 'black',
                                        color: $scope.colorLevel[k],
                                        stick: true,
                                        distanceEvent: false
                                    });
                                }
                                $scope.events.push({
                                    title: "Distance: " + displaytotdist,
                                    meteo: meteoDay,
                                    start: dates[i],
                                    textColor: 'black',
                                    color: '#7FFFD4',
                                    stick: true,
                                    distanceEvent: true
                                });
                            }
                        }
                    );
                }
             )
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

    $scope.setJsonOriginalView = function(SelectedEvent){
        $scope.myJson.title.text = SelectedEvent.activityName;
        $scope.myJson.scaleX.values.splice(0, $scope.myJson.scaleX.values.length);
        for(var i = 0; i<3; i++){
            $scope.myJson.scaleX.values.push($scope.activityLevel[i]);
        }

        $scope.myJson.series[0].values.splice(0, $scope.myJson.series[0].values.length);
        if ($scope.imperial){
            $scope.myJson.series[0].values.push($scope.roundToPlaces(SelectedEvent.eactive*1.61,2));
            $scope.myJson.series[0].values.push($scope.roundToPlaces(SelectedEvent.vactive*1.61,2));
            $scope.myJson.series[0].values.push($scope.roundToPlaces(SelectedEvent.factive*1.61,2));
            $scope.myJson.scaleY.label.text = "Kilometers"

        }
        else {
            $scope.myJson.series[0].values.push(SelectedEvent.eactive);
            $scope.myJson.series[0].values.push(SelectedEvent.vactive);
            $scope.myJson.series[0].values.push(SelectedEvent.factive);
            $scope.myJson.scaleY.label.text = "Miles"
        }

        $scope.myJson.plot.styles.splice(0, $scope.myJson.plot.styles.length);
        $scope.myJson.plot.styles.push("#99FF99");
        $scope.myJson.plot.styles.push("#FFFF99");
        $scope.myJson.plot.styles.push("#FF6666");
    }

    $scope.setJson = function(SelectedEvent){
        if(SelectedEvent.distanceEvent == false){
            $scope.myJson.title.text = SelectedEvent.activityName;
            $scope.myJson.scaleX.values.splice(0, $scope.myJson.scaleX.values.length);
            $scope.myJson.series[0].values.splice(0, $scope.myJson.series[0].values.length);
            $scope.myJson.plot.styles.splice(0, $scope.myJson.plot.styles.length);

            for(var i = 0; i<SelectedEvent.event.length; i++){
                $scope.myJson.scaleX.values.push(SelectedEvent.event[i])
                if ($scope.imperial) {
                    $scope.myJson.series[0].values.push($scope.roundToPlaces(SelectedEvent.distanceLevel[i]*1.61,2))
                    $scope.myJson.scaleY.label.text = "Kilometers"
                }
                else {
                    $scope.myJson.series[0].values.push(SelectedEvent.distanceLevel[i])
                    $scope.myJson.scaleY.label.text = "Miles"
                }
            }
            for(var i = 0; i<10; i++){
                $scope.myJson.plot.styles.push($scope.colors[i]);
            }
        }
        else {
            $scope.myJson.title.text = "Total Distance";
            $scope.myJson.scaleX.values.splice(0, $scope.myJson.scaleX.values.length);

            $scope.myJson.plot.styles.splice(0, $scope.myJson.plot.styles.length);
            for(var i = 0; i<3; i++){
                $scope.myJson.scaleX.values.push($scope.activityLevel[i]);
            }

            $scope.myJson.series[0].values.splice(0, $scope.myJson.series[0].values.length);

            if ($scope.imperial) {
                $scope.myJson.series[0].values.push($scope.roundToPlaces($scope.events[0].eactive*1.61,2));
                $scope.myJson.series[0].values.push($scope.roundToPlaces($scope.events[0].vactive*1.61,2));
                $scope.myJson.series[0].values.push($scope.roundToPlaces($scope.events[0].factive*1.61,2));
                $scope.myJson.scaleY.label.text = "Kilometers"

            }
            else {
                $scope.myJson.series[0].values.push($scope.events[0].eactive);
                $scope.myJson.series[0].values.push($scope.events[0].vactive);
                $scope.myJson.series[0].values.push($scope.events[0].factive);
                $scope.myJson.scaleY.label.text = "Miles"

            }
            $scope.myJson.plot.styles.splice(0, $scope.myJson.plot.styles.length);
            $scope.myJson.plot.styles.push("#99FF99");
            $scope.myJson.plot.styles.push("#FFFF99");
            $scope.myJson.plot.styles.push("#FF6666");
        }
    }





    $scope.myJson = {
         type : "bar",
         title: {
             backgroundColor : "transparent",
             fontColor :"black",
             text : ""
         },
         plot: {
            styles: []
         },
         plotArea: {
            margin:'dynamic'
         },
         backgroundColor : "white",
         scaleX: {
             values: [],
             item: {
                fontAngle: -45,
                fontSize: "9px"
             },
             label: {
                text:"Activity"
             }
         },
         scaleY: {
            label: {
                text:""
            }
         },
         series :[{
             values : []
         }]
    };
}])