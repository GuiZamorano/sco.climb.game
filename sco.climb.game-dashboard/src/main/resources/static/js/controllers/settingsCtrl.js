/* global angular */
angular.module('climbGame.controllers.settings', [])
  .controller('settingsCtrl', function ($scope, $filter, $window, dataService, settingsService) {

    $scope.subjectsList = null;
    $scope.teksList = null;
    $scope.gradeLevelsList = null;

    settingsService.getActivitySubjects().then(
        function(subjects) {
            $scope.subjectsList = subjects;
        })

    settingsService.getActivityTeks().then(
      function(teks) {
          $scope.teksList = teks;
      })

    settingsService.getActivityGrades().then(
      function(gradeLevels) {
          $scope.gradeLevelsList = gradeLevels;
      })

    settingsService.saveSettings().then(
      function(selectedSubjects) {

      })

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

      $scope.selectedSubjects = {
          subjects: []
      };

      $scope.selectedTeks = {
          teks: []
      };

      $scope.selectedGradeLevels = {
          gradeLevels: []
      };
  });
