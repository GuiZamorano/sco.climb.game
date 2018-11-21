angular.module('consoleControllers.leg')

.controller('CreateNewMultimediaElementDialogCtrl', function ($scope, addElementsFunction, saveFunction, 
		dataService, leg) {
 
    $scope.newMedia = {type: 'image'};

    $scope.$modalSuccess = function() {
        if(!$scope.newMedia.link) {     // controlla che sia stato inserito un URL
            alert("Non è stato inserito un indirizzo valido.");
        }
        else {
            if ($scope.newMedia.type) {
                addElementsFunction($scope.newMedia.name, $scope.newMedia.link, $scope.newMedia.type);
                $scope.$modalClose();
                saveFunction();
            }
            else {
                alert("Errore: il tipo dell'oggetto non è un tipo valido.");
            }
        }
    }
    
    $scope.uploadFile = function () {
    	var fileInput = document.getElementById('upload-content-file');
    	if(fileInput.files.length == 0) {
    		alert('Scegliere un file da caricare');
    		return;
    	}
    	var file = fileInput.files[0];
    	var formData = new FormData();
    	formData.append('file', file);
    	var element = {
    			"ownerId": leg.ownerId,
    			"pedibusGameId": leg.pedibusGameId,
    			"itineraryId": leg.itineraryId,
    			"legId": leg.objectId,
          "formdata": formData,
    	};
    	dataService.uploadFileContent(element).then(function (response) {
            $scope.newMedia.link = response.data.link;
            $scope.file = null;
        });
    };
});