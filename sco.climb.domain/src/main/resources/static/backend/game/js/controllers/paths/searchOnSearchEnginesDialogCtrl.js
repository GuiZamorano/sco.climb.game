angular.module('consoleControllers.leg')

.controller('SearchOnSearchEnginesDialogCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, addElementsFunction, saveFunction) {
    

    $scope.searchtype = 'image';
    $scope.totalCounter = 0;

    $scope.searchOnEngine = function() {
        if (!$scope.searchtext) return;
        $scope.resetResults();
        if ($scope.searchtype == 'wikipedia' && !$scope.wikiResults) {            
            $scope.changePage(undefined);
        } else if ($scope.searchtype == 'video' && !$scope.ytResults) {
            $scope.changePage(undefined);
        } else if ($scope.searchtype == 'image' && !$scope.imageResults) {            
            $scope.changePage(undefined);
        }
    }

    $scope.changePage = function(pageToken) {
        if ($scope.searchtype == 'wikipedia') {
            //in this case pageToken is an offset of search results
            DataService.searchOnWikipedia($scope.searchtext, pageToken).then(
                function(response) {
                		$scope.resetResults();
                    $scope.wikiResults = response.data.query.pages;
                    for(var page in $scope.wikiResults){
                        $scope.wikiResults[page].link = 'https://it.wikipedia.org/wiki/' + $scope.wikiResults[page].title; 
                    }
                    $scope.prevPageToken = response.data['query-continue'].search.gsroffset - 20;
                    if ($scope.prevPageToken < 0) $scope.prevPageToken = -1;
                    $scope.nextPageToken = response.data['query-continue'].search.gsroffset;
                    console.log(response);
                		var el = document.getElementById('wikiContentList');
                		el.scrollTop = 0;
                }, function() {
                }
            );
        } else if ($scope.searchtype == 'video') {
            DataService.searchOnYoutube($scope.searchtext, pageToken).then(
                function(response) {
                    $scope.resetResults();
                    $scope.ytResults = response.data.items;
                    $scope.prevPageToken = response.data.prevPageToken;
                    if (!$scope.prevPageToken) $scope.prevPageToken = -1; //used to generalize pagination controls
                    $scope.nextPageToken = response.data.nextPageToken;
                    console.log(response);
              			var el = document.getElementById('videoContentList');
              			el.scrollTop = 0;
                }, function() {
                }
            );
        } else if ($scope.searchtype == 'image') {
            //in this case pageToken is an offset of search results
            DataService.searchOnImages($scope.searchtext, pageToken).then(
                function(response) {
                    $scope.resetResults();
                    $scope.imageResults = response.data.items;
                    if (response.data.queries.previousPage) {
                        $scope.prevPageToken = response.data.queries.previousPage[0].startIndex;
                    } else {
                        $scope.prevPageToken = -1; //used to generalize pagination controls
                    }
                    if (response.data.queries.nextPage) {
                        $scope.nextPageToken = response.data.queries.nextPage[0].startIndex;
                    }
                    console.log(response);
                		var el = document.getElementById('imageContentList');
                		el.scrollTop = 0;
                }, function() {
                }
            );
        }
    }

    $scope.$modalSuccess = function() {
        if ($scope.searchtype == 'wikipedia') {
            for(key in $scope.wikiResults){
                var element = $scope.wikiResults[key];
                if (element.selectedToAdd) {
                    addElementsFunction(element.title, element.link, 'link');
                }
            }
        } else if ($scope.searchtype == 'video') {
            $scope.ytResults.forEach(element => {
                if (element.selectedToAdd) {
                    addElementsFunction(element.snippet.title, 'https://www.youtube.com/watch?v=' + element.id.videoId, 'video');
                }
            });
        } else if ($scope.searchtype == 'image') {            
            $scope.imageResults.forEach(element => {
                if (element.selectedToAdd) {
                    addElementsFunction(element.title, element.link, 'image');
                }
            });
        }
        $scope.$modalClose();
        saveFunction();
    }
    $scope.resetResults = function() {
        $scope.wikiResults = undefined;
        $scope.ytResults = undefined;
        $scope.imageResults = undefined;
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
    $scope.checkImage = function(image) {
        image.selectedToAdd = !image.selectedToAdd;
        $scope.updateTotalCounter(image.selectedToAdd);
    }
});