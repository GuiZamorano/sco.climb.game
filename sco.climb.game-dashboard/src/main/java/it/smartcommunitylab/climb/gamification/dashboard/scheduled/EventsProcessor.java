package it.smartcommunitylab.climb.gamification.dashboard.scheduled;

import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.gamification.dashboard.model.events.WsnEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class EventsProcessor {

	private static final transient Logger logger = LoggerFactory.getLogger(EventsProcessor.class);
	
	private Map<String, Stop> childStopsMap;

	public EventsProcessor(Map<String, Stop> stopsMap) {
		childStopsMap = new HashMap<String, Stop>();
		Collection<Stop> stops = stopsMap.values();
		for (Stop stop: stops) {
			for (String childId: stop.getPassengerList()) {
				childStopsMap.put(childId, stop);
			}
		}
	}

	public Collection<ChildStatus> process(List<WsnEvent> events) {
		Map<String, ChildStatus> childrenStatus = Maps.newTreeMap();

		boolean travelling = false;
		
		for (WsnEvent event : events) {

//			Date timestamp = event.getTimestamp();
			ChildStatus cs = null;

			switch (event.getEventType()) {
			case 101: // add child in range
				cs = getChildStatus(childrenStatus, (String)event.getPayload().get("passengerId"));
				cs.setInRange(true);
				break;
			case 102: // checkin child
				cs = getChildStatus(childrenStatus, (String)event.getPayload().get("passengerId"));
				cs.setInPedibus(true);
				break;
			case 103: // checkout child
				cs = getChildStatus(childrenStatus, (String)event.getPayload().get("passengerId"));
				cs.setInPedibus(false);
				break;
			case 104: // child at school
				cs = getChildStatus(childrenStatus, (String)event.getPayload().get("passengerId"));
				cs.setArrived(true);
				break;
			case 105: // child lost
				cs = getChildStatus(childrenStatus, (String)event.getPayload().get("passengerId"));
				cs.setInRange(false);
				break;
//			case 201: // anchor
//				anchors.put(timestamp, wsnId);
//				if (travelling) {
//					for (ChildStatus css : childrenStatus.values()) {
//						if (css.isInPedibus() && css.isInRange() && !css.isArrived()) {
//							css.getAnchors().add(wsnId);
//						}
//					}
//				}
//				break;				
			case 202: // stop reached
				if (travelling) {
					for (ChildStatus css : childrenStatus.values()) {
						if (css.isInPedibus() && !css.isArrived()) {
							css.getStops().add((String)event.getPayload().get("stopId"));
//							css.getAnchors().add(wsnId);
						}
					}
				}
				break;

			case 301: // driver
				break;
			case 302: // helper
				break;
			case 303: // driver position
				break;

			case 401: // start
				travelling = true;
				break;
			case 402: // end
				travelling = false;
				break;

			default:
				;
			}
		}

		computeScore(childrenStatus.values());
		
		return childrenStatus.values();
	}
	
	private ChildStatus getChildStatus(Map<String, ChildStatus> childrenStatus, String id) {
		if (!childrenStatus.containsKey(id)) {
			ChildStatus cs = new ChildStatus(id);
			childrenStatus.put(id, cs);
		}
		return childrenStatus.get(id);
	}

	private double computeScore(Collection<ChildStatus> childrenStatus) {
		double score = 0;
		for (ChildStatus cs : childrenStatus) {
			score += computeScore(cs);
		}
		return score;
	}
	
	private double computeScore(ChildStatus childStatus) {
		double score = 0;
		if (childStatus.isArrived()) {
			if (childStopsMap.containsKey(childStatus.getChildId())) {
				Stop stop = childStopsMap.get(childStatus.getChildId());
				score = stop.getDistance();
			} else {
				logger.warn("ChildId " + childStatus.getChildId() + " not associated to any stop.");
			}
		}
		childStatus.setScore(score);
		return score;
	}

}
