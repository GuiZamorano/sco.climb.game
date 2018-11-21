package it.smartcommunitylab.climb.domain.common;

import it.smartcommunitylab.climb.domain.model.WsnEvent;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsLogger {
	private static final transient Logger logger = LoggerFactory.getLogger(StatsLogger.class);
	
	public static void logEvent(String ownerId, String instituteId, String schoolId, 
			String routeId, WsnEvent event) {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("type=WsnEvent ");
			sb.append("ownerId=" + addWordField(ownerId));
			sb.append("instituteId=" + addUUIDField(instituteId));
			sb.append("schoolId=" + addUUIDField(schoolId));
			sb.append("routeId=" + addUUIDField(routeId));
			sb.append("eventType=" + addNumberField(event.getEventType()));
			sb.append("timestamp=" + addNumberField(event.getTimestamp().getTime()));
			sb.append("wsnNodeId=" + addQuotedField(event.getWsnNodeId()));
			switch (event.getEventType()) {
			case Const.NODE_IN_RANGE:
				sb.append("passengerId=" + addUUIDField((String) event.getPayload().get("passengerId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.NODE_CHECKIN:
				sb.append("passengerId=" + addUUIDField((String) event.getPayload().get("passengerId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.NODE_CHECKOUT:
				sb.append("passengerId=" + addUUIDField((String) event.getPayload().get("passengerId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.NODE_AT_DESTINATION:
				sb.append("passengerId=" + addUUIDField((String) event.getPayload().get("passengerId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.NODE_OUT_OF_RANGE:
				sb.append("passengerId=" + addUUIDField((String) event.getPayload().get("passengerId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				sb.append("lastCheck=" + addNumberField((Long) event.getPayload().get("lastCheck")));
				break;
			case Const.STOP_REACHED:
				sb.append("stopId=" + addUUIDField((String) event.getPayload().get("stopId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.SET_DRIVER:
				sb.append("volunteerId=" + addUUIDField((String) event.getPayload().get("volunteerId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.SET_HELPER:
				sb.append("volunteerId=" + addUUIDField((String) event.getPayload().get("volunteerId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.DRIVER_POSITION:
				sb.append("volunteerId=" + addUUIDField((String) event.getPayload().get("volunteerId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.START_ROUTE:
				sb.append("stopId=" + addUUIDField((String) event.getPayload().get("stopId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.END_ROUTE:
				sb.append("stopId=" + addUUIDField((String) event.getPayload().get("stopId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				break;
			case Const.BATTERY_STATE:
				sb.append("passengerId=" + addUUIDField((String) event.getPayload().get("passengerId")));
				sb.append("latitude=" + addNumberField((Double) event.getPayload().get("latitude")));
				sb.append("longitude=" + addNumberField((Double) event.getPayload().get("longitude")));
				sb.append("accuracy=" + addNumberField((Number) event.getPayload().get("accuracy")));
				sb.append("batteryVoltage=" + addNumberField((Integer) event.getPayload().get("batteryVoltage")));
				sb.append("batteryLevel=" + addNumberField((Integer) event.getPayload().get("batteryLevel")));
				break;
			default:
				break;
			}
			logger.info(sb.toString());			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static String addQuotedField(String field) {
		return "\"" + StringEscapeUtils.escapeJson(field) + "\" "; 
	}
	
	public static String addUUIDField(String field) {
		return field + " ";
	}
	
	public static String addWordField(String field) {
		return field + " ";
	}
	
	public static String addNumberField(Number field) {
		return field + " ";
	}

}
