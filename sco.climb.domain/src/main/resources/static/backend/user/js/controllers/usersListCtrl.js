/* global angular */
angular.module('climbGameUser.controllers.users.lists.list', [])
.controller('usersListCtrl', ['$scope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', '$translate', 'dataService', 'configService',
  function ($scope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, $translate, dataService, configService) {
      console.log("Selected user list: " + $stateParams.role);
      if (!$stateParams.role) {
        $state.go("home.users-lists.list", {'role':'all'});
        $scope.$parent.currentUsersListTab = "owner-list-tab";
      }
      $scope.$parent.currentUsersListTab = $stateParams.role + "-list-tab";

      $scope.users = [];
      $scope.authTextMap = {};
      $scope.showAuthsMap = {};
      $scope.loading = true;
      $scope.roleToShow = $stateParams.role;
      if ($scope.roleToShow == 'all' || $scope.roleToShow == 'norole') {
      	$scope.roleToShow = null;
      }
      dataService.getUsersByRole($scope.roleToShow).then(
        function (data) {
        	data.forEach(user => {
        		user.roleNames = [];
          	for(authKey in user.roles) {
          		var auths = user.roles[authKey];
          		auths.forEach(function(auth) {
          			if(auth.role == "user") {
          				return true;
          			}
          			if(!user.roleNames.includes(auth.role)) {
          				user.roleNames.push(auth.role);
          			}
          		});
          	}
          	if($stateParams.role == 'norole') {
          		if(user.roleNames.length == 0) {
          			$scope.users.push(user);
          		}
          	} else {
          		$scope.users.push(user);
          	}
          });
          //console.log('[UsersList] Users number: ' + $scope.users.length)
          $scope.users.forEach(function(user) {
          	$scope.showAuthsMap[user.objectId] = false;
          	$scope.authTextMap[user.objectId] = {};
          	if(!angular.equals(user.roles, {})) {
          		var properties = Object.getOwnPropertyNames(user.roles);
        			properties.forEach(function(key) {
        				$scope.authTextMap[user.objectId][key] = {
        					"data": "",
        					"loaded": false
        				};
              });
          	}
          });
          $scope.loading = false;
        },
        function (reason) {
          console.log('[UsersList]' + reason)
          $scope.loading = false;
        }
      );
      
      $scope.isAuthToDelete = function(user, authKey) {
      	if(!$scope.roleToShow) {
      		return false;
      	}
      	if(user.roleNames.includes("admin")) {
      		return false;
      	}
      	var auths = user.roles[authKey];
      	if(auths) {
        	for(var auth of auths) {
        		if((auth.role == "user") || (auth.role == "admin")) {
        			return false
        		}
        	}
        	return true;
      	}
      	return false;
      }
      
      $scope.openUser = function(event, user) {
        $mdDialog.show({
          controller: ShowUserControllerDialog,
          templateUrl: 'templates/user_details_dialog.users_list.html',
          parent: angular.element(document.body),
          targetEvent: event,
          clickOutsideToClose:true,
          locals: {
            currentUser: user
          }
        });

        function ShowUserControllerDialog($scope, $mdDialog, locals) {
          $scope.currentUser = locals.currentUser;
          $scope.cancel = function() {
            $mdDialog.cancel();
          };
        }

      };
      
      $scope.deleteAuth = function(event, user, authKey) {
        $mdDialog.show({
          controller: DeleteAuthControllerDialog,
          templateUrl: 'templates/auth_delete_confirm_dialog.users_list.html',
          parent: angular.element(document.body),
          targetEvent: event,
          clickOutsideToClose:true,
          locals: {
            currentUser: user,
            authKey: authKey,
            authText: $scope.authTextMap[user.objectId][authKey]['data']
          }
        })
        .then(function(answer) {
          if (answer == 'confirm') {
            $scope.loading = true;
            dataService.removeAuth(authKey, user).then(
              function (data) {
              	delete user.roles[authKey];
                $scope.loading = false;
                $state.go("home.users-lists.list", {'role':'all'});
              },
              function (reason) {
              	console.log('[deleteAuth]' + JSON.stringify(reason));
                $mdToast.show(
                  $mdToast.simple()
                    .textContent($translate.instant('user_delete_error_msg'))
                    .position("bottom")
                    .hideDelay(3000)
                );
                $scope.loading = false;
              }
            );
          }
        });

        function DeleteAuthControllerDialog($scope, $mdDialog, locals) {
          $scope.currentUser = locals.currentUser;
          $scope.authKey = locals.authKey;
          $scope.authText = locals.authText;
          $scope.cancel = function() {
            $mdDialog.cancel();
          };
          $scope.delete = function() {
            $mdDialog.hide('confirm');
          }
        }      	
      };
      
      $scope.deleteRole = function(event, user, role) {
        $mdDialog.show({
          controller: DeleteRoleControllerDialog,
          templateUrl: 'templates/role_delete_confirm_dialog.users_list.html',
          parent: angular.element(document.body),
          targetEvent: event,
          clickOutsideToClose:true,
          locals: {
            currentUser: user,
            currentRole: role
          }
        })
        .then(function(answer) {
          if (answer == 'confirm') {
            $scope.loading = true;
            dataService.removeRole(role, user).then(
              function (data) {
              	$scope.users.splice($scope.users.indexOf(user), 1);
                $scope.loading = false;
                $state.go("home.users-lists.list", {'role':'all'});
              },
              function (reason) {
              	console.log('[deleteRole]' + JSON.stringify(reason));
                $mdToast.show(
                  $mdToast.simple()
                    .textContent($translate.instant('user_delete_error_msg'))
                    .position("bottom")
                    .hideDelay(3000)
                );
                $scope.loading = false;
              }
            );
          }
        });

        function DeleteRoleControllerDialog($scope, $mdDialog, locals) {
          $scope.currentUser = locals.currentUser;
          $scope.currentRole = locals.currentRole;
          $scope.cancel = function() {
            $mdDialog.cancel();
          };
          $scope.delete = function() {
            $mdDialog.hide('confirm');
          }
        }      	
      };

      $scope.deleteUser = function(event, user) {
        $mdDialog.show({
          controller: DeleteUserControllerDialog,
          templateUrl: 'templates/user_delete_confirm_dialog.users_list.html',
          parent: angular.element(document.body),
          targetEvent: event,
          clickOutsideToClose:true,
          locals: {
            currentUser: user
          }
        })
        .then(function(answer) {
          if (answer == 'confirm') {
            $scope.loading = true;
            dataService.removeUser(user).then(
              function (data) {
                $scope.users.splice($scope.users.indexOf(user), 1);
                $scope.loading = false;
              },
              function (reason) {
              	console.log('[deleteUser]' + JSON.stringify(reason));
                $mdToast.show(
                  $mdToast.simple()
                    .textContent($translate.instant('user_delete_error_msg'))
                    .position("bottom")
                    .hideDelay(3000)
                );
                $scope.loading = false;
              }
            );
          }
        });

        function DeleteUserControllerDialog($scope, $mdDialog, locals) {
          $scope.currentUser = locals.currentUser;
          $scope.cancel = function() {
            $mdDialog.cancel();
          };
          $scope.delete = function() {
            $mdDialog.hide('confirm');
          }
        }
     }
    
    $scope.hasRoles = function(user) {
    	if(user.roles) {
    		if(user.roles.length > 0) {
    			return true;
    		}
    	}
    	return false;
    }
    
    $scope.getShowAuths = function(user) {
    	if($scope.showAuthsMap.hasOwnProperty(user.objectId)) {
    		return $scope.showAuthsMap[user.objectId];
    	} else {
    		return false;
    	}
    }
    
    $scope.toggleShowAuths = function(user) {
    	if(!$scope.showAuthsMap.hasOwnProperty(user.objectId)) {
    		$scope.showAuthsMap[user.objectId] = false;
    	}
    	$scope.showAuthsMap[user.objectId] = !$scope.showAuthsMap[user.objectId];
    	var showAuths = $scope.showAuthsMap[user.objectId];
    	if(showAuths) {
    		if(!angular.equals(user.roles, {})) {
    			var properties = Object.getOwnPropertyNames(user.roles);
    			properties.forEach(function(key) {
    				if(!$scope.authTextMap[user.objectId][key]['loaded']) {
    					getAuthText(user, key);
    				}
          });
    		}
    	}
    	
      function getAuthText(user, authKey) {
        dataService.getAuthText(user, authKey).then(
          function (data) {
            $scope.authTextMap[user.objectId][authKey]['data'] = data;
            $scope.authTextMap[user.objectId][authKey]['loaded'] = true;
          },
          function (reason) {
          	alert(reason);
          }
        );
      } 
    }  

  }
])
.filter('fromMap', function() {
  return function(input) {
    var out = {};
    if(!angular.equals(input, {})) {
      var properties = Object.getOwnPropertyNames(input);
      properties.forEach(function(key) {
      	out[key] = input[key];
      });
    }
    return out;
  };
})
