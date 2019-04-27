/* global angular */
angular.module('climbGame.controllers.settings', [])
    .controller('settingsCtrl', function ($scope, $filter, $window, dataService, settingsService) {

        $scope.subjectsList = null;
        $scope.subjectsValueList = null;
        $scope.teksList = null;
        $scope.teksValueList = null;
        $scope.gradeLevelsList = null;
        $scope.gradeLevelsValueList = null;
        $scope.selectedSubjects = [];
        $scope.selectedGradeLevels = [];
        $scope.selectedTeks = [];

        $scope.subjects = null;
        $scope.teks = null;
        $scope.gradeLevels = null;

        settingsService.getSettingsSubjects().then(
            function(subjects) {
                //Split up the former map into two actually usable lists
                $scope.subjectsList = Object.getOwnPropertyNames(subjects);
                $scope.subjectsValueList = Object.values(subjects);

                //Create structure that plays nice in front-end with checkboxes
                for(var i = 0; i < $scope.subjectsList.length; i++) {
                        $scope.selectedSubjects.push({name: $scope.subjectsList[i], val: $scope.subjectsValueList[i]});
                }
            })

        settingsService.getSettingsTeks().then(
            function(teks) {
                $scope.teksList = Object.getOwnPropertyNames(teks);
                $scope.teksValueList = Object.values(teks);

                for(var i = 0; i < $scope.teksList.length; i++) {
                    $scope.selectedTeks.push({name: $scope.teksList[i], val: $scope.teksValueList[i]});
                }
            })

        settingsService.getSettingsGradeLevels().then(
            function(gradeLevels) {
                $scope.gradeLevelsList = Object.getOwnPropertyNames(gradeLevels);
                $scope.gradeLevelsValueList = Object.values(gradeLevels);

                for(var i = 0; i < $scope.gradeLevelsList.length; i++) {
                    $scope.selectedGradeLevels.push({name: $scope.gradeLevelsList[i], val: $scope.gradeLevelsValueList[i]});
                }
            })

        $scope.saveSettings = function() {
            //Create list of updated selection values to be sent to the backend
            for(var i = 0; i < $scope.selectedSubjects.length; i++) {
               $scope.subjectsValueList[i] = $scope.selectedSubjects[i].val;
            }

            for(var i = 0; i < $scope.selectedGradeLevels.length; i++) {
                $scope.gradeLevelsValueList[i] = $scope.selectedGradeLevels[i].val;
            }

            for(var i = 0; i < $scope.selectedTeks.length; i++) {
                $scope.teksValueList[i] = $scope.selectedTeks[i].val;
            }

            var settings = {
                subjectNames: $scope.subjectsList,
                subjectValues: $scope.subjectsValueList,
                teksOptions: $scope.teksList,
                teksValues: $scope.teksValueList,
                gradeLevelOptions: $scope.gradeLevelsList,
                gradeLevelValues: $scope.gradeLevelsValueList
            }

            settingsService.saveSettings(settings)
          }
  });
