/* global angular */
angular.module('climbGame.services.conf', [])
  .factory('configService', function () {
    var configService = {}
    configService.DEVELOPMENT = true
    configService.ENABLE_PAST_DAYS_EDIT = true
    configService.IMAGES_PREFIX_URL = './'; //changed in game-public to load right images
    var URL = 'https://' + (configService.DEVELOPMENT ? 'climbdev' : 'climb') + '.smartcommunitylab.it/v2'
  
    configService.FOOT_CONSTANT = 'foot'
    configService.PLANE_CONSTANT = 'plane'
    configService.BOAT_CONSTANT = 'boat'
    configService.BALLOON_CONSTANT = 'balloon'
    configService.ZEPPELIN_CONSTANT = 'zeppelin'
    configService.TRAIN_CONSTANT = 'train'
    configService.SLED_CONSTANT = 'sled'

    var httpTimeout = 10000
    var DEFAULT_CENTER_MAP = [37.973378, 23.730957]
    var DEFAULT_ZOOM_MAP = 4
    var DEFAULT_ZOOM_POI = 9
    configService.DEFAULT_POI_POPUP_OFFSET = 0.4
      // var APP_BUILD = ''

    configService.TRAVEL_ICONS_MAP = {};
      configService.TRAVEL_ICONS_MAP[configService.FOOT_CONSTANT] = 'img/POI_foot';
      configService.TRAVEL_ICONS_MAP[configService.PLANE_CONSTANT] = 'img/POI_airplane';
      configService.TRAVEL_ICONS_MAP[configService.BOAT_CONSTANT] = 'img/POI_boat';
      configService.TRAVEL_ICONS_MAP[configService.BALLOON_CONSTANT] = 'img/POI_baloon';
      configService.TRAVEL_ICONS_MAP[configService.ZEPPELIN_CONSTANT] = 'img/POI_zeppelin';
      configService.TRAVEL_ICONS_MAP[configService.TRAIN_CONSTANT] = 'img/POI_train';
      configService.TRAVEL_ICONS_MAP[configService.SLED_CONSTANT] = 'img/POI_sleigh';

    configService.TRAVEL_ICONS_STATE_MAP = {
      true: '_full.png',
      false: '_empty.png'
    }

    configService.getURL = function () {
      return URL
    }

    configService.httpTimout = function () {
      return httpTimeout
    }

    configService.getFootConstant = function () {
      return FOOT_CONSTANT
    }

    configService.getBoatConstant = function () {
      return BOAT_CONSTANT
    }

    configService.getPlaneConstant = function () {
      return PLANE_CONSTANT
    }
    configService.getDefaultMapCenterConstant = function () {
      return DEFAULT_CENTER_MAP
    }
    configService.getDefaultZoomMapConstant = function () {
      return DEFAULT_ZOOM_MAP
    }
    configService.getDefaultZoomPoiConstant = function () {
      return DEFAULT_ZOOM_POI
    }

    configService.getIconImg = function (iconName, state) {
      var iconImg = configService.TRAVEL_ICONS_MAP[iconName];
      if (!iconImg) iconImg = configService.TRAVEL_ICONS_MAP[configService.FOOT_CONSTANT];
      return configService.IMAGES_PREFIX_URL + iconImg + configService.TRAVEL_ICONS_STATE_MAP[state];
    }

    return configService
  })
