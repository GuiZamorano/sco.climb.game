angular.module('PermissionsService', []).factory('PermissionsService', function ($location, $state) {
    var permissionsService = {};

    var showInstitutes, editInstitute;
    var showSchools, editSchool;
    var showGames, editGame;
    var showPaths, editPath;
    var showLegs, editLegs, editLegsMultimedia;

    permissionsService.setProfilePermissions = function(roles) {
        //roles = ['teacher']; //uncomment to debug UI as a teacher!
        roles.forEach(role => {
            if (role == 'owner' || role == 'admin' || role == 'school-owner') {
                showInstitutes = true;
                editInstitute = true;
                showSchools = true;
                editSchool = true;
                showGames = true;
                editGame = true;
                showPaths = true;
                editPath = true;
                showLegs = true;
                editLegs = true;
                editLegsMultimedia = true;
            } else if (role == 'teacher') {
                showPaths = true;
                showLegs = true;
            } else if (role == 'childrenedit') {
                showInstitutes = true;
                editInstitute = true;
                showSchools = true;
                editSchool = true;
            } else if (role == 'game-editor') {
              showPaths = true;
              showLegs = true;
              editLegsMultimedia = true;
            }
        });
        
        permissionsService.redirectForPermissions();
        
    }

    permissionsService.redirectForPermissions = function() {
        //redirect to specific page in case permissions are not sufficient for default page
        //usefull when first logging! 
        var stateToRedirect = $state.current.name;
        if (stateToRedirect == 'root.institutes-list' && !showInstitutes) {
            stateToRedirect = 'root.schools-list';
        }
        if (stateToRedirect == 'root.schools-list' && !showSchools) {
            stateToRedirect = 'root.games-list';
        }
        if (stateToRedirect == 'root.games-list' && !showGames) {
            stateToRedirect = 'root.paths-list';
        }

        if (stateToRedirect != $state.current.name) {
            $state.go(stateToRedirect);
        }
    }

    permissionsService.permissionEnabledShowInstitutes = function() {
        return showInstitutes;
    }
    permissionsService.permissionEnabledEditInstitute = function() {
        return editInstitute;
    }
    permissionsService.permissionEnabledShowSchools = function() {
        return showSchools;
    }
    permissionsService.permissionEnabledEditSchool = function() {
        return editSchool;
    }
    permissionsService.permissionEnabledShowGames = function() {
        return showGames;
    }
    permissionsService.permissionEnabledEditGame = function() {
        return editGame;
    }
    permissionsService.permissionEnabledShowPaths = function() {
        return showPaths;
    }
    permissionsService.permissionEnabledEditPath = function() {
        return editPath;
    }
    permissionsService.permissionEnabledShowLegs = function() {
        return showLegs;
    }
    permissionsService.permissionEnabledEditLegs = function() {
        return editLegs;
    }
    permissionsService.permissionEnabledEditLegsMultimedia = function() {
        return editLegsMultimedia;
    }

    return permissionsService;
});