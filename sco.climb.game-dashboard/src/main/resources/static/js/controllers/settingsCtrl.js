/* global angular */
angular.module('climbGame.controllers.settings', [])
  .controller('settingsCtrl', function ($scope, $filter, $window, dataService, settingsService) {

    $scope.subjetcsList = null;
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

      $scope.fruitsList = [
          {id: 1, name: 'Apple'},
          {id: 2, name: 'Mango'},
          {id: 3, name: 'Banana'},
          {id: 4, name: 'Guava'},
          {id: 5, name: 'Orange'}
      ];
      $scope.selected = {
          fruits: []
      };

      $scope.selectedSubjects = {
          subjects: []
      };

      $scope.selectedTeks = {
          teks: []
      };

      $scope.selectedGradeLevels = {
          gradeLevels: []
      };

      $scope.checkAll = function() {
          $scope.selectedSubjects.subjects = angular.copy($scope.subjectsList);
      };
      $scope.uncheckAll = function() {
          $scope.selectedSubjects.subjects = [];
      };

  });
