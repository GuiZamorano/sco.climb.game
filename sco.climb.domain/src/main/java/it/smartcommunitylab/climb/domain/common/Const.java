package it.smartcommunitylab.climb.domain.common;

public class Const {
	public static final String ERRORTYPE = "errorType";
	public static final String ERRORMSG = "errorMsg";
	
	public static final int NODE_IN_RANGE = 101;
	public static final int NODE_CHECKIN = 102;
	public static final int NODE_CHECKOUT = 103;
	public static final int NODE_AT_DESTINATION = 104;
	public static final int NODE_OUT_OF_RANGE = 105;
	public static final int ANCHOR_IN_RANGE = 201;
	public static final int STOP_REACHED = 202;
	public static final int SET_DRIVER = 301;
	public static final int SET_HELPER = 302;
	public static final int DRIVER_POSITION = 303;
	public static final int START_ROUTE = 401;
	public static final int END_ROUTE = 402;
	public static final int BATTERY_STATE = 501;
	public static final int TEST_STATE = 901;
	
	public static final String SYSTEM_DOMAIN = "SYSTEM";
	
	public static final String ROLE_ADMIN = "admin";
	public static final String ROLE_USER = "user";
	public static final String ROLE_PARENT = "parent";
	public static final String ROLE_GAME_EDITOR = "game-editor";
	public static final String ROLE_VOLUNTEER = "volunteer";
	public static final String ROLE_TEACHER = "teacher";
	public static final String ROLE_OWNER = "owner";
	public static final String ROLE_SCHOOL_OWNER = "school-owner";
	
	public static final String AUTH_ACTION_READ = "READ";
	public static final String AUTH_ACTION_ADD = "ADD";
	public static final String AUTH_ACTION_UPDATE = "UPDATE";
	public static final String AUTH_ACTION_DELETE = "DELETE";
	
	public static final String AUTH_RES_Auth = "Auth";
	public static final String AUTH_RES_WsnEvent = "WsnEvent";
	public static final String AUTH_RES_EventLogFile = "EventLogFile";
	public static final String AUTH_RES_NodeState = "NodeState";
	public static final String AUTH_RES_Child = "Child";
	public static final String AUTH_RES_Image = "Image";
	public static final String AUTH_RES_Volunteer = "Volunteer";
	public static final String AUTH_RES_Stop = "Stop";
	public static final String AUTH_RES_School = "School";
	public static final String AUTH_RES_Route = "Route";
	public static final String AUTH_RES_Institute = "Institute";
	public static final String AUTH_RES_Attendance = "Attendance";
	public static final String AUTH_RES_PedibusGame = "PedibusGame";
	public static final String AUTH_RES_PedibusGame_Link = "PedibusGame-Link";
	public static final String AUTH_RES_PedibusGame_Calendar = "PedibusGame-Calendar";
	public static final String AUTH_RES_PedibusGame_Excursion = "PedibusGame-Excursion";
	
	public static final String AUTH_ACCOUNT_NAME = "climb";
	public static final String AUTH_ATTRIBUTE_NAME = "email";
	
	public static final String METEO_SOLE = "sunny";
	public static final String METEO_NUVOLE = "cloudy";
	public static final String METEO_PIOGGIA = "rain";
	public static final String METEO_NEVE = "snow";
	
	public static final String MODE_PEDIBUS = "pedibus";
	public static final String MODE_PIEDI_SOLO = "zeroImpact_solo";
	public static final String MODE_PIEDI_ADULTO = "zeroImpact_wAdult";
	public static final String MODE_SCUOLABUS = "bus";
	public static final String MODE_PARK_RIDE = "pandr";
	public static final String MODE_AUTO = "car";
	public static final String MODE_ASSENTE = "absent";
	public static final String MODE_BONUS = "bonus";
	
	public static final String TRANSPORT_FOOT = "foot";
	public static final String TRANSPORT_BOAT = "boat";
	public static final String TRANSPORT_AIRPLANE = "plane";
	
	public static final String MEDIA_LINK = "link";
	public static final String MEDIA_VIDEO = "video";
	public static final String MEDIA_IMAGE = "image";
	public static final String MEDIA_FILE = "file";
	
	public static final String MERGED = "MERGED";
	public static final String CLOSED = "CLOSED";
	
	public static final String CHALLANGE_ACTIVE = "ACTIVE";
	public static final String CHALLANGE_FAILD = "FAILD";
	public static final String CHALLANGE_COMPLETED = "COMPLETED";

}
