angular.module('consoleControllers.leg')

.controller('SearchOnContentRepositoryDialogCtrl', function ($scope, $state, DataService, schoolId, addElementsFunction, saveFunction, position, getYoutubeImageFromLink) {
    
    $scope.totalCounter = 0;
    $scope.searchtype = 'all';
    $scope.searchdistance = null;
    $scope.searchlocalschool = false;

    $scope.searchOnContentRepository = function() {
        //if (!$scope.searchtext) return;
        $scope.resetResults();
        var searchposition = undefined;
        var searchdistance = undefined;
        if($scope.searchdistance) {
        	searchposition = position;
        	searchdistance = $scope.searchdistance;
        }
        DataService.searchOnContentRepository($scope.searchtext, searchposition, searchdistance,
        		$scope.searchlocalschool ? schoolId : undefined, 
        		$scope.searchtype != 'all' ? $scope.searchtype : '').then(
                function(response) {
                    response.data.forEach(element => {
                        switch (element.type) {
                            case 'image':
                                element.referenceImg = element.link;
                                break;
                            case 'video':
                                var youtubeThumbnail = getYoutubeImageFromLink(element.link);
                                if (youtubeThumbnail) {
                                    element.referenceImg = youtubeThumbnail;
                                    element.isYoutubeVideo = true;
                                } else {
                                    element.referenceImg = "img/video.png";
                                }                                
                                break;
                            case 'link':
                                element.referenceImg = "img/link.png";
                                break;
                            case 'file':
                              element.referenceImg = "img/file.png";
                              break;
                        }
                    });          
                    $scope.contentResults = response.data;
                    $scope.noResults = response.data.length == 0
                    console.log(response); 
                }, function() {
                }
        );
    }
    $scope.$modalSuccess = function() {
        $scope.contentResults.forEach(element => {
            if (element.selectedToAdd) {
                addElementsFunction(element.info[0].name, element.link, element.type);
            }
        });
        $scope.$modalClose();
        saveFunction();
    }
    $scope.resetResults = function() {
        $scope.contentResults = undefined;
        $scope.noResults = false;
        $scope.totalCounter = 0;
        $scope.$modalSuccessLabel = "Aggiungi " + $scope.totalCounter + " elementi";
    }
    $scope.updateTotalCounter = function(state) {
        if (state) {
            $scope.totalCounter++;
        } else {
            $scope.totalCounter--;
        }
        $scope.$modalSuccessLabel = "Aggiungi " + $scope.totalCounter + " elementi";
    }
    $scope.changePage = function(newPageNumber, oldPageNumber) {
    	var el = document.getElementById('contentList');
    	el.scrollTop = 0;
    }

});