/* global angular */
angular.module('climbGame.controllers.calendar', [])
  .controller('calendarCtrl', ['$scope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', 'CacheSrv', 'dataService', 'calendarService',
    function ($scope, $filter, $window, $interval, $mdDialog, $mdToast, CacheSrv, dataService, calendarService) {
      $scope.week = []
      $scope.weekName = []
      $scope.weekNumber = []
      $scope.selectedWeather = ''
      $scope.selectedMean = ''
      $scope.selectedMeanColor = 'cal-menu-col'
      $scope.labelWeek = ''
      $scope.sendingData = false
      $scope.inputVal = {}
      $scope.cal = {
        meanOpen: false
      }
      $scope.classMap = {}
      $scope.weekData = []

      $scope.todayData = {
        babies: [],
        means: {}
      }
      $scope.Index = ''
      calendarService.getIndex().then(
        function(index) {
            $scope.Index = index
            setTodayIndex()
            var moduloCheck = $scope.todayIndex
            var moduloAttempt = moduloCheck%10
            var counter = 0
                  while(moduloAttempt != 5 && moduloAttempt != 0){
                    moduloAttempt = --moduloCheck
                    moduloAttempt = moduloAttempt%10
                    counter++
                    }
                    var startPoint = $scope.todayIndex-counter
                  for (var i = startPoint; i < startPoint+5; i++) {
                    //$scope.week.push(new Date(getMonday(new Date()).getTime() + (i * 24 * 60 * 60 * 1000)))
                    $scope.weekNumber.push("Event " + i);
                    $scope.week.push(i);
                  }
                  setLabelWeek($scope.weekNumber)
            }
      )
      setClassSize()





      calendarService.setTitle().then(
        function () {},
        function () {
          // default value
        }
      )

      calendarService.getClassPlayers().then(
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

          calendarService.getCalendar($scope.week[0], $scope.week[$scope.week.length - 1]).then(
            function (calendar) {
              createWeekData(calendar)
              updateTodayData(calendar)
            },
            function () {}
          )
        },
        function () {}
      )

      $scope.returnColorByType = function (type) {
        var color = ''
        switch (type) {
        case 'zeroImpact_solo':
          color = 'cal-foot-friend-col'
          break
        case 'zeroImpact_wAdult':
          color = 'cal-foot-adult-col'
          break
        case 'bus':
          color = 'cal-bus-col'
          break
        case 'pandr':
          color = 'cal-car-square-col'
          break
        }
        return color
      }



      $scope.selectWather = function (weather) {
        $scope.selectedWeather = weather
      }

      /*
      $scope.openMeans = function () {
        $scope.cal.meanOpen = !$scope.cal.meanOpen
      }
      */

      $scope.selectGeneralMean = function (mean) {
        $scope.selectedMean = mean
        $scope.selectedMeanColor = $scope.returnColorByType($scope.selectedMean)
      }

      $scope.selectBabyMean = function (index) {
        if (!$scope.selectedMean) {
          $mdToast.show($mdToast.simple().content('Select a means of transport'))
          return
        }

        if ($scope.todayData.babies[index].mean === 'pedibus') {
          $mdToast.show($mdToast.simple().content('It is not possible to overwrite PEDIBUS'))
          return
        }

        // set baby[$index]= selected mean;
        // add mean to index and remove the other
        if ($scope.todayData.babies[index].mean) {
          $scope.todayData.means[$scope.todayData.babies[index].mean]--
        }
        $scope.todayData.babies[index].color = $scope.returnColorByType($scope.selectedMean)
        $scope.todayData.babies[index].mean = $scope.selectedMean
        if (!$scope.todayData.means[$scope.todayData.babies[index].mean]) {
          $scope.todayData.means[$scope.todayData.babies[index].mean] = 0
        }
        $scope.todayData.means[$scope.todayData.babies[index].mean]++
      }

      $scope.today = function (index) {
      var todayCheck = $scope.todayIndex

        return index === todayCheck
      }

      $scope.sendData = function () {
        if (dataAreComplete()) {
          $mdDialog.show({
            // targetEvent: $event,
            scope: $scope, // use parent scope in template
            preserveScope: true, // do not forget this if use parent scope
            template: '<md-dialog>' +
              '  <div class="cal-dialog-title"> Data sending  </div><md-divider></md-divider>' +
              '  <div class="cal-dialog-text">Send final data to the system You will no longer be able to modify it.</div>' +
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
                $scope.sendingData = false
              }

              $scope.confirmSend = function () {
                if (!$scope.sendingData) {
                  $scope.sendingData = true
                  $scope.todayData.meteo = $scope.selectedWeather
                  $scope.todayData.name = $scope.inputVal.name
                  $scope.todayData.day = new Date().setHours(0, 0, 0, 0)
                  var babiesMap = {}
                  for (var i = 0; i < $scope.todayData.babies.length; i++) {
                    if ($scope.todayData.babies[i].mean) {
                      babiesMap[$scope.todayData.babies[i].childId] = $scope.todayData.babies[i].mean
                    }
                  }

                  $scope.todayData.modeMap = babiesMap
                  $scope.todayData.index = $scope.Index
                  calendarService.sendData($scope.todayData).then(function (returnValue) {
                    // change weekdata to closed
                    $scope.weekData[$scope.todayIndex%5].closed = true
                      // check if merged or not

                    if (returnValue) {
                      // popup dati backend cambiati
                      $mdDialog.show({
                        // targetEvent: $event,
                        scope: $scope, // use parent scope in template
                        preserveScope: true, // do not forget this if use parent scope
                        template: '<md-dialog>' +
                          '  <div class="cal-dialog-title"> Data Changed </div><md-divider></md-divider>' +
                          '  <div class="cal-dialog-text">The current data has been changed. </div>' +
                          '    <div layout="row"  layout-align="start center" ><div layout"column" flex="100" ><md-button ng-click="closeDialogChanged()" class=" send-dialog-delete">' +
                          '      I understand' +
                          '   </div> </md-button>' +
                          '</div></md-dialog>',
                        controller: function DialogController($scope, $mdDialog) {
                          // reload and show


                          calendarService.getCalendar($scope.week[0], $scope.week[$scope.week.length - 1]).then(
                            function (calendar) {
                              createWeekData(calendar)
                              updateTodayData(calendar)
                              $scope.sendingData = false
                            },
                            function () {
                              // manage error
                              $scope.sendingData = false
                            }
                          )

                          $scope.closeDialogChanged = function () {
                            $mdDialog.hide()
                          }
                        }
                      })
                    } else {
                      // sent data
                      $mdToast.show($mdToast.simple().content('Data sending'))
                        // reload and show
                      calendarService.getCalendar($scope.week[0], $scope.week[$scope.week.length - 1]).then(
                        function (calendar) {
                          createWeekData(calendar)
                          updateTodayData(calendar)
                          $scope.sendingData = false
                        },
                        function () {
                          // manage error
                          $scope.sendingData = false
                        },
                      )
                      calendarService.getIndex().then(
                                      function(index) {
                                          $scope.Index = index
                                          setTodayIndex()
                                          $scope.todayData = {
                                                                                      babies: [],
                                                                                      means: {},
                                                                                      meteo : '',
                                                                                      name : ''
                                                                                    }
                                                                                    calendarService.getClassPlayers().then(
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

                                                                                              calendarService.getCalendar($scope.week[0], $scope.week[$scope.week.length - 1]).then(
                                                                                                function (calendar) {
                                                                                                  createWeekData(calendar)
                                                                                                  updateTodayData(calendar)
                                                                                                },
                                                                                                function () {}
                                                                                              )
                                                                                            },
                                                                                            function () {}
                                                                                          )
                                          }
                                    )

                    }
                    $scope.closeDialog()
                  }, function () {
                    // TODO get error
                    $scope.sendingData = false
                  })
                }
              }
            }
          })
        } else {
          $mdDialog.show({
            // targetEvent: $event,
            scope: $scope, // use parent scope in template
            preserveScope: true, // do not forget this if use parent scope
            template: '<md-dialog>' +
              '  <div class="cal-dialog-title"> Incomplete Data  </div><md-divider></md-divider>' +
              '  <div class="cal-dialog-text">{{"cal_data_missing"|translate}}</div>' +
              '    <div layout="row"  layout-align="start center" ><div layout"column" flex="100" ><md-button ng-click="closeDialog()" class=" send-dialog-delete">' +
              '      I understand' +
              '   </div> </md-button>' +
              '</div></md-dialog>',
            controller: function DialogController($scope, $mdDialog) {
              $scope.closeDialog = function () {
                $mdDialog.hide()
                $scope.sendingData = false
              }
            }
          })
        }
      }

      $scope.prevWeek = function () {
        changeWeek(-1)
      }
      $scope.nextWeek = function () {
        changeWeek(1)
      }

      $scope.newEvent = function () {
          $scope.week.push("Event" + $scope.Index++)
          calendarService.getCalendar($scope.week[0], $scope.week[$scope.week.length - 1]).then(
            function (calendar) {
                createWeekData(calendar)
            },
            function () {}
          )
          setLabelWeek($scope.weekNumber)
      }

      $scope.scrollUp = function () {
        document.getElementById('table').scrollTop -= 50
      }
      $scope.scrollDown = function () {
        document.getElementById('table').scrollTop += 50
      }

      $scope.isFuture = function (dayIndex) {
        return ($scope.Index < $scope.week[dayIndex])
      }

      $scope.isPast = function (dayIndex) {
        return ($scope.Index > $scope.week[dayIndex])
      }

      $scope.getEventName = function (day) {
            day = day%5
            return $scope.weekName[day]
            }

      function dataAreComplete() {
        // meteo and means must  be chosen
        if (!$scope.selectedWeather) {
          return false
        }
        for (var i = 0; i < $scope.todayData.babies.length; i++) {
          if (!$scope.todayData.babies[i].mean) {
            return false
          }
        }
        // all babies  have a mean
        return true
      }


      function getMonday(d) {
        d = new Date(d)
        d.setHours(0, 0, 0, 0)
        var day = d.getDay()
        var diff = d.getDate() - day + (day === 0 ? -6 : 1) // adjust when day is sunday
        return new Date(d.setDate(diff))
      }

      function checkDayOfTheWeek(dayFromData, indexOfWeek) {
        // compare timestamp dayFromData.day with timestamp of the $scope.week[indexOfWeek]
        // return true if it is the same day and false otherwise
        if (dayFromData.index === $scope.week[indexOfWeek]) {
          return true
        }
        return false
      }

      function setTodayIndex() {
        /* set the day of week */
        //var day = new Date().getDay()
        //day = day - (day === 0 ? -6 : 1)
        $scope.todayIndex = $scope.Index
      }

      function changeWeek(skipWeek) {
        // take date of week[0] and go 1 week before or after
        //var monday = $scope.week[0]
        //monday.setDate(monday.getDate() + 7 * skipWeek)

        var skip = skipWeek*5 + $scope.week[0]
        $scope.week = []
        $scope.weekNumber = []
         for (var i = skip; i < skip+5; i++) {
                //$scope.week.push(new Date(getMonday(new Date()).getTime() + (i * 24 * 60 * 60 * 1000)))
                $scope.weekNumber.push("Event " + i);
                $scope.week.push(i);
              }
              setLabelWeek($scope.weekNumber)

        calendarService.getCalendar($scope.week[0], $scope.week[$scope.week.length - 1]).then(
          function (calendar) {
            createWeekData(calendar)
          },
          function () {}
        )

        // if the new week is the actual week
        //var now = new Date()
        //now.setHours(0, 0, 0, 0)
        //if (now.getTime() >= $scope.week[0].getTime() && now.getTime() <= $scope.week[$scope.week.length - 1].getTime()) {
          //setTodayIndex()
        //} else {
          //$scope.todayIndex = -1
        //}


           //$scope.labelWeek = $filter('date')($scope.week[0], 'dd') + " - "
           //$filter('date')($scope.week[$scope.week.length - 1], 'dd MMM yyyy');
      }

      function setLabelWeek(weekArray) {
        $scope.labelWeek = weekArray[0]+" - "+weekArray[4]
      }

      function updateTodayData(calendar) {
        // reset the number of means
        $scope.todayData.means = {}
          // if there is today data merge it with $scope.todayData
        var today = new Date().setHours(0, 0, 0, 0)
        for (var i = 0; i < 5; i++) {
          if (calendar[i].index === $scope.todayIndex) {
            // merge it
            for (var k = 0; k < $scope.todayData.babies.length; k++) {
              if (calendar[i].modeMap[$scope.todayData.babies[k].childId]) {
                $scope.todayData.babies[k].color = $scope.returnColorByType(calendar[i].modeMap[$scope.todayData.babies[k].childId])
                $scope.todayData.babies[k].mean = calendar[i].modeMap[$scope.todayData.babies[k].childId]
                if (!$scope.todayData.means[$scope.todayData.babies[k].mean]) {
                  $scope.todayData.means[$scope.todayData.babies[k].mean] = 0
                }
                $scope.todayData.means[$scope.todayData.babies[k].mean]++
              }
            }
            break
          }
        }
      }

      function setClassSize() {
        var w = window
        var d = document
        var e = d.documentElement
        var g = d.getElementsByTagName('body')[0]
          // x = w.innerWidth || e.clientWidth || g.clientWidth,
        var y = w.innerHeight || e.clientHeight || g.clientHeight
        if (document.getElementById('table')) {
          document.getElementById('table').setAttribute('style', 'height:' + (y - 64 - 100 - 130 - 50) + 'px')
        }
      }

      function createWeekData(calendar) {
        $scope.weekData = []
        var k = 0
        for (var i = 0; i < 5; i++) {
          // get i-th day data and put baby with that object id with that setted mean
          $scope.weekData.push({})

            // if calendar[i] esiste vado avanti
          if (calendar[k]) {
            // se giorno della settimana coincide con calendar.day vado avanti altrimenti skip
            if (checkDayOfTheWeek(calendar[k], i)) {
              for (var property in calendar[k].modeMap) {
                $scope.weekData[i][property] = {
                  mean: calendar[k].modeMap[property]
                }
                $scope.weekData[i][property].color = $scope.returnColorByType(calendar[k].modeMap[property])
                if (!$scope.weekData[i][calendar[k].modeMap[property]]) {
                  $scope.weekData[i][calendar[k].modeMap[property]] = 0
                }
                $scope.weekData[i][calendar[k].modeMap[property]] = $scope.weekData[i][calendar[k].modeMap[property]] + 1
              }
              if (calendar[k].meteo) {
                $scope.weekData[i].meteo = calendar[k].meteo
              }
              // if (calendar[i].closed) {
              $scope.weekData[i].closed = calendar[k].closed

              $scope.weekName[i] = calendar[k].name

              k++
            } else {
              // add entire day of null data
              for (var prop in calendar[k].modeMap) {
                $scope.weekData[i][prop] = {}
              }
            }
          } else {
            // add entire day of null data
          }

        }
      }

      /*
       * Notifications and Challenges stuff
       */
      $scope.lastNotification = null
      $scope.notificationsPoller = null

      /*
      {
        "gameId": "588889c0e4b0464e16ac40a0",
        "playerId": "5^",
        "state": [{
          "name": "test_KmSettimanali_classe_5",
          "modelName": "KmSettimanali",
          "fields": {
            "TargetTeam": "classe",
            "VirtualPrize": "biglietto aereo di test",
            "bonusPointType": "bonus_distance",
            "bonusScore": 3000,
            "counterName": "total_distance",
            "periodName": "weekly",
            "target": 10000
          },
          "start": 1486512000000,
          "completed": true,
          "dateCompleted": 1486569750531
        }]
      }
      */

      var startPoller = function () {
        /* comment this if you don't want always the last notification available */
        CacheSrv.resetLastCheck('calendar')

        var getNotifications = function () {
          dataService.getNotifications(CacheSrv.getLastCheck('calendar')).then(
            function (data) {
              if (data && data.length) {
                console.log('[Calendar] New notifications: ' + data.length)
                data[0].data = $scope.convertFields(data[0].data)
                $scope.lastNotification = data[0]
                CacheSrv.updateLastCheck('calendar')
              }
            },
            function (reason) {
              console.log('[Calendar]' + reason)
            }
          )
        }
        var cleanStatesChallenges = function (arrayOfChallenges) {
          $scope.openChallenge = false;
          var challengesNotCompleted = [];
          //first get all the not completed
          for (var i = 0; i < arrayOfChallenges.length; i++) {
            if (!arrayOfChallenges[i].completed && !arrayOfChallenges[i].fields.prizeWon)
              challengesNotCompleted.push(arrayOfChallenges[i]);
          }
          if (challengesNotCompleted[0]) {
            $scope.lastChallenge.state = [challengesNotCompleted[0]];
            $scope.openChallenge = true;

          }
          for (var j = 1; j < challengesNotCompleted.length; j++) {
            if (challengesNotCompleted[j] && challengesNotCompleted[j].start > $scope.lastChallenge.state[0].start) {
              $scope.lastChallenge.state = [challengesNotCompleted[j]]
            }
          }
        }
        var getChallenges = function () {
          dataService.getChallenges().then(
            function (data) {
              if (data && data.length) {
                console.log('[Calendar] Challenges: ' + data.length)
                for (var i = 0; i < data.length; i++) {
                  if (data[i].state) {
                    angular.forEach(data[i].state, function (state) {
                      state.fields = $scope.convertFields(state.fields)
                    })
                    $scope.lastChallenge = data[i]
                    i = data.length
                    cleanStatesChallenges($scope.lastChallenge.state)
                  }
                }
              }
            },
            function (reason) {
              console.log('[Calendar]' + reason)
            }
          )
        }

        getNotifications()
        getChallenges()
          // poll every 10 seconds
        $scope.poller = $interval(function () {
          getNotifications()
          getChallenges()
        }, (1000 * 10))
      }

      startPoller()

      function onResize() {
        setClassSize()
      }

      $scope.$on('$destroy', function () {
        if ($scope.poller) {
          $interval.cancel($scope.poller)
          console.log('[Calendar] poller cancelled')
        }
        window.angular.element($window).off('resize', onResize)
      })

      var appWindow = angular.element($window)
      appWindow.bind('resize', onResize)
    }
  ])
