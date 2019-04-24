/* global angular */
angular.module('climbGame.controllers.settings', [])
  .controller('settingsCtrl', function ($scope, $filter, $window, dataService, settingsService) {

    $scope.subjectsList = null;
    $scope.teksList = null;
    $scope.gradeLevelsList = null;
    $scope.selectedSubjects = [];
    $scope.selectedGradeLevels = [];
    $scope.selectedTeks = [];


    settingsService.getActivitySubjects().then(
        function(subjects) {
            $scope.subjectsList = subjects;

            for(var i = 0; i < subjects.length; i++) {
                $scope.selectedSubjects.push({name:subjects[i], val:false});
            }
        })

    settingsService.getActivityTeks().then(
      function(teks) {
          $scope.teksList = teks;

          for(var i = 0; i < teks.length; i++) {
              $scope.selectedTeks.push({name:teks[i], val:false});
          }
      })

    settingsService.getActivityGrades().then(
      function(gradeLevels) {
          $scope.gradeLevelsList = gradeLevels;

          for(var i = 0; i < gradeLevels.length; i++) {
              $scope.selectedGradeLevels.push({name:gradeLevels[i], val:false});
          }
      })


      $scope.saveSettings = function() {
          var selectedTrueSubjects = []
           for(var i = 0; i < $scope.selectedSubjects.length; i++) {
               if($scope.selectedSubjects[i].val == true) {
                   selectedTrueSubjects.push($scope.selectedSubjects[i].name);
               }
           }

          var selectedTrueGradeLevels = []
          for(var i = 0; i < $scope.selectedGradeLevels.length; i++) {
              if($scope.selectedGradeLevels[i].val == true) {
                  selectedTrueGradeLevels.push($scope.selectedGradeLevels[i].name);
              }
          }

          var selectedTrueTeks = []
          for(var i = 0; i < $scope.selectedTeks.length; i++) {
              if($scope.selectedTeks[i].val == true) {
                  selectedTrueTeks.push($scope.selectedTeks[i].name);
              }
          }

        var settings = {
            subjects: selectedTrueSubjects,
            teks: selectedTrueTeks,
            gradeLevels: selectedTrueGradeLevels
        }

          settingsService.saveSettings(settings)
      }

      //TODO after get the rest to work
      // settingsService.getSelectedSubjects().then(
      //     function(subjects) {
      //         $scope.selectedSubjects = subjects;
      //     }
      // )
      //
      // settingsService.getSelectedSubjects().then(
      //     function(subjects) {
      //         $scope.selectedSubjects = subjects;
      //     }
      // )
      //
      // settingsService.getSelectedSubjects().then(
      //     function(subjects) {
      //         $scope.selectedSubjects = subjects;
      //     }
      // )
  });
