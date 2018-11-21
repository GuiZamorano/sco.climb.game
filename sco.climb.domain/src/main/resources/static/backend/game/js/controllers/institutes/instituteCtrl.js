institutesModule.controller('InstituteCtrl', function ($scope, $rootScope, $state, $stateParams, DataService, MainDataService, createDialog, PermissionsService) {    
    $scope.$parent.$parent.mainView = 'institute'; 
    $scope.$parent.selectedTab = 'info';
    
    $scope.initController = function() {
        if ($scope.currentInstitute) { //edit school
            $scope.saveData = DataService.editData;
        } else {
            $scope.currentInstitute = {
                name: '',
                address: '',
                ownerId: $stateParams.idDomain,
            }
            $scope.saveData = DataService.saveData;
        }
    }

    if ($stateParams.idInstitute) {
        MainDataService.getDomains().then(function (response) {
            MainDataService.getInstitutes($stateParams.idDomain).then(function (response) {
                $scope.institutesList = response.data;
                for (var i = 0; i < $scope.institutesList.length && !$scope.currentInstitute; i++) {
                    if ($scope.institutesList[i].objectId == $stateParams.idInstitute) $scope.currentInstitute = angular.copy($scope.institutesList[i]);
                }
                $scope.initController();
            });
        });
    } else { //new institute
        $scope.initController();
    }

    $scope.isNewInstitute = function() {
    	return ($stateParams.idInstitute == null || $stateParams.idInstitute == '');
    };
    
    $scope.save = function () {
        if (checkFields())
        {
            $scope.saveData('institute', $scope.currentInstitute).then(
                function(response) {
                    console.log('Salvataggio dati a buon fine.');
                    if ($scope.currentInstitute.objectId) { //edited
                        for (var i = 0; i < $scope.institutesList.length; i++) {
                            if ($scope.institutesList[i].objectId == $scope.currentInstitute.objectId) $scope.institutesList[i] = $scope.currentInstitute;
                        }
                    } else {
                        if ($scope.institutesList) $scope.institutesList.push(response.data);
                    }
                    $state.go('root.institutes-list');
                }, function() {
                    alert('Errore nella richiesta.');
                }
            );
        }
        else
        {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco.";
            $timeout(function () {
                $rootScope.modelErrors = '';
            }, 5000);
        }
    };

    function checkFields() {
        var isValidate = true;
        var invalidFields = $('.ng-invalid');

        if (invalidFields.length > 0)
            isValidate = false;

        return isValidate;
    }

    $scope.back = function() {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$state.go('root.institutes-list');} }
        });
    };

    $scope.$parent.getInstituteName = function() {
        if (!$scope.currentInstitute) return ''; 
    	if(!$scope.isNewInstitute()) {
            return $scope.currentInstitute.name;
    	} else {
    		return "";
    	} 
    }
})
