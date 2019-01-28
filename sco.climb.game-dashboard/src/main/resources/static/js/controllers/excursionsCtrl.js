/* global angular */
angular.module('climbGame.controllers.excursions', [])
  .controller('excursionsCtrl', ['$scope', '$q', '$http', '$window', '$mdDialog', 'dataService', 'excursionsService',
    function ($scope, $q, $http, $window, $mdDialog, dataService, excursionsService) {
      $scope.showHints = false
      $scope.datepickerisOpen = false
      $scope.excursions = null
      $scope.sendingData = false
      $scope.classMap = {}
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
//      $scope.refreshExcursions = function () {
//        dataService.getExcursions().then(
//          function (excursions) {
//            $scope.excursions = excursions
//          },
//          function (reason) {
//            // console.log(reason)
//          }
//        )
//      }

      //$scope.refreshExcursions()

      $scope.scroll = function (direction) {
        if (direction === 'up') {
          $window.document.getElementById('excursions-list').scrollTop -= 50
        } else if (direction === 'down') {
          $window.document.getElementById('excursions-list').scrollTop += 50
        }
      }

      /* Form */
      var emptyExcursion = {
        name: null,
        date: null,
        children: null,
        distance: null,
        meteo: 'sunny'
      }

      $scope.newExcursion = angular.copy(emptyExcursion)

      $scope.now = new Date()

      $scope.createExcursion = function () {



        var params = {
          name: $scope.newExcursion.name,
          date: $scope.newExcursion.date.getTime(),
          children: $scope.newExcursion.children,
          distance: $scope.newExcursion.distance * 1000,
          meteo: $scope.newExcursion.meteo
        }

        if (!params.name || !params.date || !params.children || !params.distance || !params.meteo) {
          return
        }
        $scope.todayData.day = params.date
        $scope.todayData.meteo = params.meteo
        $scope.todayData.name = params.name
        $scope.todayData.distance = params.distance
        $scope.todayData.children = params.children

        var babiesMap = {}
        for (var i = 0; i < params.children; i++) {
                babiesMap[$scope.todayData.babies[i].childId] = 'zeroImpact_solo'
        }

        $scope.todayData.modeMap = babiesMap
        $mdDialog.show({
          // targetEvent: $event,
          scope: $scope, // use parent scope in template
          preserveScope: true, // do not forget this if use parent scope
          template: '<md-dialog>' +
            '  <div class="cal-dialog-title"> Data sending  </div><md-divider></md-divider>' +
            '  <div class="cal-dialog-text">Send final data to the system \'You will no longer be able to modify it.</div>' +
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
