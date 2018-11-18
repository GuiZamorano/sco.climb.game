package it.smartcommunitylab.climb.gamification.dashboard.scheduled;

import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.gamification.dashboard.common.Const;
import it.smartcommunitylab.climb.gamification.dashboard.model.PedibusGame;
import it.smartcommunitylab.climb.gamification.dashboard.model.PedibusPlayer;
import it.smartcommunitylab.climb.gamification.dashboard.model.events.WsnEvent;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.gamification.dashboard.storage.RepositoryManager;
import it.smartcommunitylab.climb.gamification.dashboard.utils.HTTPUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
public class EventsPoller {

	@Autowired
	@Value("${contextstore.url}")
	private String contextstoreURL;

	@Autowired
	@Value("${eventstore.url}")
	private String eventstoreURL;
	
	@Autowired
	@Value("${gamification.url}")
	private String gamificationURL;			
	
	@Autowired
	@Value("${gamification.user}")
	private String gamificationUser;

	@Autowired
	@Value("${gamification.password}")
	private String gamificationPassword;
	
	@Autowired
	@Value("${action.pedibus}")	
	private String actionPedibus;	

	@Autowired
	@Value("${param.kid.distance}")	
	private String paramDistance;	

	@Autowired
	@Value("${param.date}")	
	private String paramDate;	
	
	@Autowired
	private RepositoryManager storage;

	private static final transient Logger logger = LoggerFactory.getLogger(EventsPoller.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");

	
	@Scheduled(cron = "0 5,10,15 8-20 * * MON-FRI") // second, minute, hour, day, month, weekday
	//@Scheduled(cron = "0 */2 8-18 * * MON-FRI")
	public void scheduledPollEvents() throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("scheduledPollEvents");
		}
		pollEvents(true);
	}
	
	@Scheduled(cron = "0 45,55 7 * * *") // second, minute, hour, day, month, weekday
	public void resetPollingFlag() {
		List<PedibusGame> games = storage.getPedibusGames();
		Calendar cal = new GregorianCalendar(TimeZone.getDefault());
		String date = shortSdf.format(cal.getTime());
		for (PedibusGame game : games) {
			storage.resetPollingFlag(game.getOwnerId(), game.getGameId());
			storage.updatePedibusGameLastDaySeen(game.getOwnerId(), game.getGameId(), date);
		}
	}
	
	public Map<String, Collection<ChildStatus>> pollEvents(boolean checkDate) throws Exception {
		Map<String, Collection<ChildStatus>> results = Maps.newTreeMap();
		List<PedibusGame> games = storage.getPedibusGames();
		for (PedibusGame game : games) {
			logger.info("Reading game " + game.getGameId() + " events.");
			Map<String, Collection<ChildStatus>> childrenStatusMap = pollGameEvents(game, checkDate);
			for(String routeId : childrenStatusMap.keySet()) {
				Collection<ChildStatus> childrenStatus = childrenStatusMap.get(routeId); 
				if(!isEmptyResponse(childrenStatus)) {
					sendScores(childrenStatus, game);
					storage.updatePollingFlag(game.getOwnerId(), game.getGameId(), routeId, Boolean.FALSE);
					updateCalendarDayFromPedibus(game.getOwnerId(), game.getGameId(), childrenStatus);
				}
			}
			results.putAll(childrenStatusMap);
		}
		return results;
	}
	
	public Map<String, Collection<ChildStatus>> pollGameEvents(PedibusGame game, 
			boolean checkDate) throws Exception {
		Map<String, Collection<ChildStatus>> results = Maps.newTreeMap();
		if(game != null) {
			Date date = new Date();
			if(checkDate) {
				if(game.getFrom().compareTo(date) > 0 || game.getTo().compareTo(date) < 0) {
					logger.info("Skipping game " + game.getGameId() + ", date out of range.");
					return results;
				}
			}
			List<String> routesList = getRoutes(game.getSchoolId(), game.getOwnerId(), game.getToken());

			Calendar cal = new GregorianCalendar(TimeZone.getDefault());

			String from, to;

			if (game.getLastDaySeen() != null) {
				cal.setTime(shortSdf.parse(game.getLastDaySeen()));
			} else {
				cal.setTime(cal.getTime());
			}

			String h[];

			// h = (game.getFromHour() != null ? game.getFromHour() :
			// "00:01").split(":");
			h = game.getFromHour().split(":");
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(h[1]));
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			from = sdf.format(cal.getTime());

			// h = (game.getToHour() != null ? game.getToHour() :
			// "23:59").split(":");
			h = game.getToHour().split(":");
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(h[1]));

			to = sdf.format(cal.getTime());

			ObjectMapper mapper = new ObjectMapper();
			
			for (String routeId : routesList) {
				logger.info("Reading route " + routeId + " events.");
				
				Boolean pollingFlag = game.getPollingFlagMap().get(routeId);
				if((pollingFlag != null) && (pollingFlag == Boolean.FALSE)) {
					logger.info("Events already managed for route " + routeId);
					continue;
				}

				String address = eventstoreURL + "/api/event/" + game.getOwnerId() + "?" + "routeId=" + routeId + "&dateFrom=" + from + "&dateTo=" + to;

				String routeEvents = HTTPUtils.get(address, game.getToken(), null, null);

				List<WsnEvent> eventsList = Lists.newArrayList();

				List<?> events = mapper.readValue(routeEvents, List.class);

				for (Object e : events) {
					WsnEvent event = mapper.convertValue(e, WsnEvent.class);
					eventsList.add(event);
				}
				if (!eventsList.isEmpty()) {
					address = contextstoreURL + "/api/stop/" + game.getOwnerId() + "/" + routeId;

					String routeStops = HTTPUtils.get(address, game.getToken(), null, null);

					Map<String, Stop> stopsMap = Maps.newTreeMap();

					List<?> stops = mapper.readValue(routeStops, List.class);
					for (Object e : stops) {
						Stop stop = mapper.convertValue(e, Stop.class);
						stopsMap.put(stop.getObjectId(), stop);
					}

					logger.info("Computing scores for route " + routeId);
					EventsProcessor ep = new EventsProcessor(stopsMap);
					Collection<ChildStatus> status = ep.process(eventsList);

					results.put(routeId, status);
					logger.info("Computed scores for route " + routeId + " = " + status);
				} else {
					results.put(routeId, null);
					logger.info("No recent events for route " + routeId);
				}
			}	
		}
		return results;
	}
	
	private List<String> getRoutes(String schoolId, String ownerId, String token) throws Exception {
		String address = contextstoreURL + "/api/route/" + ownerId + "/school/" + schoolId;

		String result = HTTPUtils.get(address, token, null, null);

		List<String> routesList = Lists.newArrayList();
		ObjectMapper mapper = new ObjectMapper();
		List<?> routes = mapper.readValue(result, List.class);
		for (Object e : routes) {
			Route route = mapper.convertValue(e, Route.class);
			routesList.add(route.getObjectId());
		}

		return routesList;
	}
	
	public void sendScores(Collection<ChildStatus> childrenStatus, PedibusGame game) {
		String address = gamificationURL + "/gengine/execute";
		if(childrenStatus == null) { 
			return;
		}
		for (ChildStatus childStatus: childrenStatus) {
			if(childStatus.isArrived()) {
				String playerId = childStatus.getChildId();
				Double score = childStatus.getScore();
						
				ExecutionDataDTO ed = new ExecutionDataDTO();
				ed.setGameId(game.getGameId());
				ed.setPlayerId(playerId);
				ed.setActionId(actionPedibus);
				
				Map<String, Object> data = Maps.newTreeMap();
				data.put(paramDistance, score);
				Date date = new Date();
				try {
					date = shortSdf.parse(game.getLastDaySeen());
				} catch (ParseException e) {
					logger.warn("sendScores error:" + e.getMessage());
				}
				data.put(paramDate, date.getTime());
				ed.setData(data);
				
				try {
					if(logger.isInfoEnabled()) {
						logger.info(String.format("increased game[%s] player[%s] score[%s]", game.getGameId(), playerId, score));
					}
					HTTPUtils.post(address, ed, null, gamificationUser, gamificationPassword);
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}				
			}
		}
	}
	
	public void updateCalendarDayFromPedibus(String ownerId, String gameId,
			Collection<ChildStatus> childrenStatus) {
		
		Map<String, Map<String, String>> classModeMap = new HashMap<String, Map<String,String>>();
		
		if(childrenStatus == null) {
			return;
		}
		for(ChildStatus childStatus : childrenStatus) {
			if(childStatus.isArrived()) {
				PedibusPlayer player = storage.getPedibusPlayerByChildId(ownerId, gameId, childStatus.getChildId());
				if(player != null) {
					String classRoom = player.getClassRoom();
					Map<String, String> modeMap = classModeMap.get(classRoom);
					if(modeMap == null) {
						modeMap = new HashMap<String, String>();
						classModeMap.put(classRoom, modeMap);
					}
					modeMap.put(player.getChildId(), Const.MODE_PEDIBUS);
				}
			}
		}
		
		PedibusGame game = storage.getPedibusGame(ownerId, gameId);
		if(game != null) {
			try {
				Date day = shortSdf.parse(game.getLastDaySeen());
				for(String classRoom : classModeMap.keySet()) {
					Map<String, String> modeMap = classModeMap.get(classRoom);
					storage.updateCalendarDayFromPedibus(ownerId, gameId, classRoom, day, modeMap);
				}
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
	}
	
	public boolean isEmptyResponse(Collection<ChildStatus> childrenStatus) {
		boolean result = true;
		if((childrenStatus != null) && !childrenStatus.isEmpty()) {
			result = false;
		}
		return result;
	}
}
