/* global angular */
angular.module('climbGame.controllers.calendar', [])
  .controller('calendarCtrl', ['$scope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', 'CacheSrv', 'dataService', 'calendarService', 'configService', 'loginService', 'profileService',
    function ($scope, $filter, $window, $interval, $mdDialog, $mdToast, CacheSrv, dataService, calendarService, configService, loginService, profileService) {
      $scope.week = []
      $scope.selectedWeather = ''
      $scope.selectedMean = ''
      $scope.selectedMeanColor = 'cal-menu-col'
      $scope.labelWeek = ''
      $scope.sendingData = false
      $scope.cal = {
        meanOpen: false
      }
      $scope.classMap = {}
      $scope.weekData = []

      $scope.todayData = {
        babies: [],
        means: {}
      }
      $scope.ENABLE_PAST_DAYS_EDIT = configService.ENABLE_PAST_DAYS_EDIT;

      setTodayIndex()
      setClassSize()
      for (var i = 0; i < 5; i++) {
        $scope.week.push(new Date(getMonday(new Date()).getTime() + (i * 24 * 60 * 60 * 1000)))
      }

      setLabelWeek($scope.week)

      calendarService.setTitle().then(
        function () {},
        function () {
          // default value
        }
      )

      profileService.getProfile().then(function(profile) {
	    	loginService.setUserToken(profile.token)
	    	loginService.setAllOwners(profile.ownerIds)
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
	
	          calendarService.getCalendar($scope.week[0].getTime(), $scope.week[$scope.week.length - 1].getTime()).then(
	            function (calendar) {
	              createWeekData(calendar)
	              updateTodayData(calendar)
	            },
	            function () {}
	          )
	        },
	        function () {}
	      )	    	
	    }, function (err) {
	      console.log(err)
	      // Toast the Problem
	      $mdToast.show($mdToast.simple().content($filter('translate')('toast_uname_not_valid')))
	    });

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
        case 'car':
          color = 'cal-car-school-col'
          break
        case 'absent':
          color = 'cal-away-col'
          break
        case 'pedibus':
          color = 'cal-pedibus-col'
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
          $mdToast.show($mdToast.simple().content('Selezionare un mezzo di trasporto'))
          return
        }

        if ($scope.todayData.babies[index].mean === 'pedibus') {
          $mdToast.show($mdToast.simple().content('Non e\' possibile sovrascrivere PEDIBUS'))
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
        return index === $scope.todayIndex
      }

      $scope.sendData = function (dayIndex) {
        if (dataAreComplete()) {
          $mdDialog.show({
            // targetEvent: $event,
            scope: $scope, // use parent scope in template
            preserveScope: true, // do not forget this if use parent scope
            template: '<md-dialog>' +
              '  <div class="cal-dialog-title">{{\'cal_save_popup_title\'|translate}}</div><md-divider></md-divider>' +
              '  <div class="cal-dialog-text">{{\'cal_save_popup_content\'|translate}}</div>' +
              '    <div layout="row"  layout-align="start center" ><div layout"column" flex="50" ><md-button ng-click="closeDialog()" class=" send-dialog-delete">' +
              '      Annulla' +
              '   </div> </md-button>' +
              '<div layout"column" flex="50" ><md-button ng-click="confirmSend()" class="send-dialog-confirm" ng-disabled="sendingData"> ' +
              '    <span ng-show="!sendingData">Invia</span>' +
              '    <md-progress-circular class="send-dialog-progress" style="margin:auto;border-color:white;" md-mode="indeterminate" md-diameter="20" ng-show="sendingData"></md-progress-circular></md-button></div>' +
              '</div></md-dialog>',
            controller: function DialogController($scope, $mdDialog) {
              $scope.closeDialog = function () {
                $mdDialog.hide()
                $scope.sendingData = false
              }

              $scope.confirmSend = function () {
                if (!$scope.sendingData) {
                  $scope.sendingData = true
                  $scope.todayData.weather = $scope.selectedWeather
                  $scope.todayData.day = $scope.week[dayIndex].setHours(0, 0, 0, 0);
                  var babiesMap = {}
                  for (var i = 0; i < $scope.todayData.babies.length; i++) {
                    if ($scope.todayData.babies[i].mean) {
                      babiesMap[$scope.todayData.babies[i].childId] = $scope.todayData.babies[i].mean
                    }
                  }

                  $scope.todayData.modeMap = babiesMap

                  calendarService.sendData($scope.todayData).then(function (returnValue) {
                    // change weekdata to closed
                    $scope.weekData[dayIndex].closed = true
                      // check if merged or not
                    if (returnValue) {
                      // popup dati backend cambiati
                      $mdDialog.show({
                        // targetEvent: $event,
                        scope: $scope, // use parent scope in template
                        preserveScope: true, // do not forget this if use parent scope
                        template: '<md-dialog>' +
                          '  <div class="cal-dialog-title"> Dati cambiati </div><md-divider></md-divider>' +
                          '  <div class="cal-dialog-text">I dati presenti sono cambiati. </div>' +
                          '    <div layout="row"  layout-align="start center" ><div layout"column" flex="100" ><md-button ng-click="closeDialogChanged()" class=" send-dialog-delete">' +
                          '      OK' +
                          '   </div> </md-button>' +
                          '</div></md-dialog>',
                        controller: function DialogController($scope, $mdDialog) {
                          // reload and show
                          calendarService.getCalendar($scope.week[0].getTime(), $scope.week[$scope.week.length - 1].getTime()).then(
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
                      $scope.isDevEditMode = undefined;
                      // sent data
                      $mdToast.show($mdToast.simple().content('Dati inviati'))
                        // reload and show
                      calendarService.getCalendar($scope.week[0].getTime(), $scope.week[$scope.week.length - 1].getTime()).then(
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
                    }
                    for (var i=0; i < $scope.todayData.babies.length; i++) {
                      $scope.todayData.babies[i].color = '';
                      $scope.todayData.babies[i].mean = '';
                    }
                    $scope.todayData.means = [];
                    $scope.closeDialog();
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
              '  <div class="cal-dialog-title"> Dati incompleti  </div><md-divider></md-divider>' +
              '  <div class="cal-dialog-text">{{"cal_data_missing"|translate}}</div>' +
              '    <div layout="row"  layout-align="start center" ><div layout"column" flex="100" ><md-button ng-click="closeDialog()" class=" send-dialog-delete">' +
              '      OK' +
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

      $scope.switchDevEditMode = function(dayIndex) {
        if (!$scope.ENABLE_PAST_DAYS_EDIT) return;
        if ($scope.isCurrentEditDay(dayIndex)) {
          $scope.sendData(dayIndex);
        } else {
          //reset: todayData is also for past days in ENABLE_PAST_DAYS_EDIT mode
          for (var i=0; i < $scope.todayData.babies.length; i++) {
            $scope.todayData.babies[i].color = '';
            $scope.todayData.babies[i].mean = '';
          }
          $scope.todayData.means = [];

          $scope.isDevEditMode = {};
          $scope.isDevEditMode.dayIndex = dayIndex;
        }
      }

      $scope.prevWeek = function () {
        changeWeek(-1)
        $scope.isDevEditMode = undefined;
      }
      $scope.nextWeek = function () {
        changeWeek(1)
        $scope.isDevEditMode = undefined;
      }

      $scope.scrollUp = function () {
        document.getElementById('table').scrollTop -= 50
      }
      $scope.scrollDown = function () {
        document.getElementById('table').scrollTop += 50
      }

      $scope.isFuture = function (dayIndex) {
        return (new Date().setHours(0, 0, 0, 0) < $scope.week[dayIndex].setHours(0, 0, 0, 0))
      }

      $scope.isPast = function (dayIndex) {
        return (new Date().setHours(0, 0, 0, 0) > $scope.week[dayIndex].setHours(0, 0, 0, 0))
      }

      $scope.isCurrentEditDay = function (dayIndex) {
        return $scope.isDevEditMode && $scope.isDevEditMode.dayIndex == dayIndex;
      }

      function dataAreComplete() {
        // weather and means must  be chosen
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
        if (dayFromData.day === $scope.week[indexOfWeek].getTime()) {
          return true
        }
        return false
      }

      function setTodayIndex() {
        /* set the day of week */
        var day = new Date().getDay()
        day = day - (day === 0 ? -6 : 1)
        $scope.todayIndex = day
      }

      function changeWeek(skipWeek) {
        $scope.isLoadingCalendar = true; 
        // take date of week[0] and go 1 week before or after
        var monday = $scope.week[0]
        monday.setDate(monday.getDate() + 7 * skipWeek)
        $scope.week = []
        for (var i = 0; i < 5; i++) {
          $scope.week.push(new Date(monday.getTime() + (i * 24 * 60 * 60 * 1000)))
        }

        calendarService.getCalendar($scope.week[0].getTime(), $scope.week[$scope.week.length - 1].getTime()).then(
          function (calendar) {
            createWeekData(calendar)
          },
          function () {}
        )

        // if the new week is the actual week
        var now = new Date()
        now.setHours(0, 0, 0, 0)
        if (now.getTime() >= $scope.week[0].getTime() && now.getTime() <= $scope.week[$scope.week.length - 1].getTime()) {
          setTodayIndex()
        } else {
          $scope.todayIndex = -1
        }

        setLabelWeek($scope.week)
          // $scope.labelWeek = $filter('date')($scope.week[0], 'dd') + " - "
          // $filter('date')($scope.week[$scope.week.length - 1], 'dd MMM yyyy');
      }

      function setLabelWeek(weekArray) {
        $scope.labelWeek = $filter('date')(weekArray[weekArray.length - 1], 'MMM') + ' ' + $filter('date')(weekArray[0], 'dd') + ' - ' +
          $filter('date')(weekArray[weekArray.length - 1], 'dd yyyy')
      }

      function updateTodayData(calendar) {
        // reset the number of means
        $scope.todayData.means = {}
          // if there is today data merge it with $scope.todayData
        var today = new Date().setHours(0, 0, 0, 0)
        for (var i = 0; i < calendar.length; i++) {
          if (calendar[i].day === today) {
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
              if (calendar[k].weather) {
                $scope.weekData[i].weather = calendar[k].weather
              }
              // if (calendar[i].closed) {
              $scope.weekData[i].closed = calendar[k].closed
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
        $scope.isLoadingCalendar = false; 
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
          var d = new Date();
          var now = d.getTime();
          //first get all the not completed
          for (var i = 0; i < arrayOfChallenges.length; i++) {
            if (!arrayOfChallenges[i].completed && !arrayOfChallenges[i].fields.prizeWon) {
            	if(arrayOfChallenges[i].start > now) {
            		continue;
            	}
            	if(arrayOfChallenges[i].hasOwnProperty('end')) {
            		if(arrayOfChallenges[i].end > now) {
            			challengesNotCompleted.push(arrayOfChallenges[i]);
            		}
            	} else {
            		challengesNotCompleted.push(arrayOfChallenges[i]);
            	}
            }  
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
            	 $scope.lastChallenge = {state:[]}
              if (data && data.length) {
                console.log('[Calendar] Challenges: ' + data.length)
                for (var i = 0; i < data.length; i++) {
                  if (data[i].state) {
                    angular.forEach(data[i].state, function (state) {
                    	state.fields = $scope.convertFields(state.fields)
                      $scope.lastChallenge.state.push(state)
                    })
                  }
                }
                cleanStatesChallenges($scope.lastChallenge.state)
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
