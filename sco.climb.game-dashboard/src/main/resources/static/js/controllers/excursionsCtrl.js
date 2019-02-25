/* global angular */
angular.module('climbGame.controllers.excursions', [])
  .controller('excursionsCtrl', ['$scope', '$q', '$http', '$window', '$mdDialog', 'dataService', 'excursionsService',
    function ($scope, $q, $http, $window, $mdDialog, dataService, excursionsService) {
      $scope.showHints = false
      $scope.datepickerisOpen = false
      $scope.excursions = null
      $scope.sendingData = false
      $scope.index = ''
      $scope.classMap = {}
      $scope.meansNumber = []
      $scope.distance = {}
      $scope.todayData = {
              babies: [],
              means: {}
            }

      /* excursion example
      {
        children: 12,
        classRoom: '1^',
        creationDate: 1486042491236,
        day: 1485385200000,
        distance: 750,
        gameId: '588889c0e4b0464e16ac40a0',
        lastUpdate: 1486042491236,
        meteo: 'cloudy',
        name: 'test1',
        objectId: 'c219c822-35af-4e34-ad81-b39591dd36a2',
        ownerId: 'VELA'
      }
      */
dataService.getIndex().then(
      function(index) {
      $scope.index = index
      for(var i = 0; i<4; i++){
      $scope.meansNumber.push(0)
      }
      $scope.refreshExcursions()
      })



excursionsService.getClassPlayers().then(
                      function (players) {
                        $scope.class = players
                        for (var i = 0; i < players.length; i++) {
                          $scope.todayData.babies.push({
                            name: players[i].name,
                            surname: players[i].surname,
                            childId: players[i].childId,
                            color: ''
                          })
                          $scope.classMap[players[i].childId] = players[i]
                        }


                      },
                      function () {}
                    )
      $scope.countMeans = function (excursion) {

      for(var i = 1; i<Object.keys($scope.excursion.modeMap).length+1; i++){
      if(excursion.modeMap[i] == 'zeroImpact_solo'){
      $scope.meansNumber[0]++
      }
      if(excursion.modeMap[i] == 'zeroImpact_wAdult'){
            $scope.meansNumber[1]++
            }
      if(excursion.modeMap[i] == 'bus'){
                  $scope.meansNumber[2]++
                  }
      if(excursion.modeMap[i] == 'pandr'){
                        $scope.meansNumber[3]++
                        }

      }
      return $scope.meansNumber
      }




      $scope.refreshExcursions = function () {
        dataService.getCalendar(0, $scope.index).then(
          function (excursions) {
            length = excursions.length
            if(length == 0){
            length++
            } else{
            length = length - 1
            }
            $scope.excursions = excursions.slice(0, length)
          },
          function (reason) {
            // console.log(reason)
          }
        )
      }



      $scope.scroll = function (direction) {
        if (direction === 'up') {
          $window.document.getElementById('excursions-list').scrollTop -= 50
        } else if (direction === 'down') {
          $window.document.getElementById('excursions-list').scrollTop += 50
        }
      }
       $scope.scrollUp = function () {
              document.getElementById('table').scrollTop -= 50
            }
            $scope.scrollDown = function () {
              document.getElementById('table').scrollTop += 50
            }

      /* Form */
      var emptyExcursion = {
        name: null,
        date: null,
        time: null,
        childrenEA: null,
        childrenVA: null,
        childrenFA: null,
        childrenIA: null,
        meteo: 'sunny'
      }

      $scope.newExcursion = angular.copy(emptyExcursion)

      $scope.now = new Date()

      $scope.createExcursion = function () {



        var params = {
          name: $scope.newExcursion.name,
          date: $scope.newExcursion.date.getTime(),
          time: $scope.newExcursion.time,
          childrenEA: $scope.newExcursion.childrenEA,
          childrenVA: $scope.newExcursion.childrenVA,
          childrenFA: $scope.newExcursion.childrenFA,
          childrenIA: $scope.newExcursion.childrenIA,
          meteo: $scope.newExcursion.meteo
        }

        if (!params.name || !params.date || !params.meteo) {
        return
        }
        if (!(params.childrenEA + params.childrenVA + params.childrenFA + params.childrenIA == $scope.todayData.babies.length) ){
                    $mdDialog.show({
                      // targetEvent: $event,
                      scope: $scope, // use parent scope in template
                      preserveScope: true, // do not forget this if use parent scope
                      template: '<md-dialog>' +
                        '  <div class="cal-dialog-title"> Number of Children Incorrect. If children are absent mark as inactive. </div><md-divider></md-divider>' +
                        '    <div layout="row"  layout-align="start center" ><div layout"column" flex="100" ><md-button ng-click="closeDialog()" class=" send-dialog-delete">' +
                        '      I understand' +
                        '   </div> </md-button>' +
                        '</div></md-dialog>',
                      controller: function DialogController($scope, $mdDialog) {
                        $scope.closeDialog = function () {
                          $mdDialog.hide()
                        }
                      }
                    })
                    return
        }
        $scope.todayData.day = params.date
        $scope.todayData.meteo = params.meteo
        $scope.todayData.name = params.name
        $scope.todayData.time = params.time
        $scope.todayData.childrenEA = params.childrenEA
        $scope.todayData.childrenVA = params.childrenVA
        $scope.todayData.childrenFA = params.childrenFA
        $scope.todayData.childrenIA = params.childrenIA
        var count = 0;
        var babiesMap = {}
        var activityMap = {}
        for (var i =0; i < params.childrenEA; i++){
            $scope.todayData.babies[count].mean = 'zeroImpact_solo'
            count++
        }
        for (var i =0; i < params.childrenVA; i++){
            $scope.todayData.babies[count].mean = 'zeroImpact_wAdult'
            count++
        }
        for (var i =0; i < params.childrenFA; i++){
            $scope.todayData.babies[count].mean = 'bus'
            count++
        }
        for (var i =0; i < params.childrenIA; i++){
            $scope.todayData.babies[count].mean = 'pandr'
            count++
        }
        var total = params.childrenEA+params.childrenVA+params.childrenFA+params.childrenIA

        //time input
        $scope.distance.slow = 1 //red bus
        $scope.distance.med = 2 //yellow zero with adult
        $scope.distance.fast = 3 //green zero solo
        //divide duration by 60 for fraction of hour and make sure its not a long decimal
        $scope.distance.duration = Number($scope.todayData.time)/60
        $scope.todayData.duration = $scope.todayData.time

        //calculate distances travelled by each group and aggregate
        $scope.distance.means_bus = $scope.todayData.childrenFA
        $scope.distance.means_zeroImpact_wAdult = $scope.todayData.childrenVA
        $scope.distance.means_zeroImpact_solo = $scope.todayData.childrenEA
        if (typeof $scope.distance.means_bus == "undefined")
          $scope.distance.means_bus = 0
        if (typeof $scope.distance.means_zeroImpact_wAdult == "undefined")
          $scope.distance.means_zeroImpact_wAdult = 0
        if (typeof $scope.distance.means_zeroImpact_solo == "undefined")
          $scope.distance.means_zeroImpact_solo = 0
        $scope.distance.slowDistance = Number($scope.distance.means_bus) * $scope.distance.slow * Number($scope.distance.duration)
        $scope.distance.medDistance = Number($scope.distance.means_zeroImpact_wAdult) * $scope.distance.med * Number($scope.distance.duration)
        $scope.distance.fastDistance = Number($scope.distance.means_zeroImpact_solo) * $scope.distance.fast * Number($scope.distance.duration)
        //add group distances to get total
        $scope.todayData.distance = Number($scope.distance.slowDistance) + Number($scope.distance.medDistance) + Number($scope.distance.fastDistance)
        $scope.distance.popup_distance = Number($scope.todayData.distance)

        for (var i = 0; i < total; i++) {
            babiesMap[$scope.todayData.babies[i].childId] = $scope.todayData.babies[i].mean
            if($scope.todayData.babies[i].mean == "zeroImpact_solo") // green
                activityMap[$scope.todayData.babies[i].childId] = $scope.distance.fast * Number($scope.distance.duration)
            else if($scope.todayData.babies[i].mean == "zeroImpact_wAdult") // yellow
                activityMap[$scope.todayData.babies[i].childId] = $scope.distance.med * Number($scope.distance.duration)
            else if($scope.todayData.babies[i].mean == "bus") // red
                activityMap[$scope.todayData.babies[i].childId] = $scope.distance.slow * Number($scope.distance.duration)
            else // grey
                activityMap[$scope.todayData.babies[i].childId] = 0
        }

        $scope.todayData.modeMap = babiesMap
        $scope.todayData.activityType = "miles" // TODO un-hardcode string
        $scope.todayData.activityMap = activityMap

        $mdDialog.show({
          // targetEvent: $event,
          scope: $scope, // use parent scope in template
          preserveScope: true, // do not forget this if use parent scope
          template: '<md-dialog>' +
            '  <div class="cal-dialog-title"> Data sending  </div><md-divider></md-divider>' +
            '  <div class="cal-dialog-text">Send final data to the system? You will no longer be able to modify it.</div>' +
            '    <div layout="row"  layout-align="start center" ><div layout"column" flex="50" ><md-button ng-click="closeDialog()" class=" send-dialog-delete">' +
            '      Cancel' +
            '   </div> </md-button>' +
            '<div layout"column" flex="50" ><md-button ng-click = "confirmSend()" class = "send-dialog-confirm" > ' +
            '      Submit' +
            '    </md-button></div>' +
            '</div></md-dialog>',
          controller: function DialogController($scope, $mdDialog) {
            $scope.closeDialog = function () {
              $mdDialog.hide()
            }

            $scope.confirmSend = function () {
              if (!$scope.sendingData) {
                $scope.sendingData = true;
                dataService.sendData($scope.todayData).then(
                  function (data) {
                    // reset form
                    $scope.resetForm()
                      // refresh
                    //$scope.refreshExcursions()
                      //close dialog
                    $mdDialog.hide();
                    $scope.sendingData = false;




                        $mdDialog.show({
                                    // targetEvent: $event,
                                    scope: $scope, // use parent scope in template
                                    preserveScope: true, // do not forget this if use parent scope
                                    template: '<md-dialog>' +

                                      '  <div class="cal-dialog-title"> {{distance.popup_distance}} miles added! </div><md-divider></md-divider>' +
                                      '  <div class="cal-dialog-text"># students x speed x time = distance</div>' +
                                      '  <div class="cal-dialog-text">{{distance.means_bus}} students x {{distance.slow}} mph x {{distance.duration}} hour(s) = {{distance.slowDistance}} miles</div>' +
                                      '  <div class="cal-dialog-text">{{distance.means_zeroImpact_wAdult}} students x {{distance.med}} mph x {{distance.duration}} hour(s) = {{distance.medDistance}} miles</div>' +
                                      '  <div class="cal-dialog-text">{{distance.means_zeroImpact_solo}} students x {{distance.fast}} mph x {{distance.duration}} hour(s) = {{distance.fastDistance}} miles</div>' +

                                      '    <div layout="row"  layout-align="start center" ><div layout"column" flex="100" ><md-button ng-click="closeDialog()" class=" send-dialog-delete">' +
                                      '      Cool!' +
                                      '   </div> </md-button>' +
                                      '</div></md-dialog>',
                                    controller: function DialogController($scope, $mdDialog) {
                                      $scope.closeDialog = function () {
                                        $mdDialog.hide()
                                        $scope.sendingData = false
                                      }
                                    }
                                  })
                         $scope.refreshExcursions()



                  },
                  function (reason) {
                    // console.log(reason)
                    $scope.sendingData = false;
                  }
                )
              }
            }
          }
        })
       }

      $scope.resetForm = function () {
        $scope.newExcursion = angular.copy(emptyExcursion)
        $scope.excursionForm.$setPristine()
        $scope.excursionForm.$setUntouched()
      }
    }])
