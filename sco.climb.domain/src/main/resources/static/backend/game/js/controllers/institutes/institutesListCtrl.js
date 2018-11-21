var institutesModule = angular.module('consoleControllers.institutes', ['ngSanitize'])

.controller('InstitutesListCtrl', function ($scope, $rootScope, DataService, createDialog, PermissionsService) {
    $scope.$parent.mainView = 'institute';
    $scope.PermissionsService = PermissionsService;

    $scope.delete = function (institute) {
        createDialog('templates/modals/delete-institute.html',{
            id : 'delete-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData('institute', institute).then(
                    function() {
                        console.log("Rimozione effettuata con successo.");
                        $scope.institutesList.splice($scope.institutesList.indexOf(institute), 1);
                    }, function() {
                        alert("Errore nella richiesta.");
                    });
            } }
        });
    };
})