package it.smartcommunitylab.climb.domain.scheduled;

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.HTTPUtils;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.model.WsnEvent;
import it.smartcommunitylab.climb.domain.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
public class EventsPoller {

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
//	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");

	
	@Scheduled(cron = "0 5,10,15 8 * * MON-FRI") // second, minute, hour, day, month, weekday
	//@Scheduled(cron = "0 */2 8-18 * * MON-FRI")
	public void earlyScheduledPollEvents() throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("scheduledPollEvents");
		}
		pollEvents(true, false);
	}
	
	@Scheduled(cron = "0 5,10,15 18 * * MON-FRI") // second, minute, hour, day, month, weekday
	//@Scheduled(cron = "0 */2 8-18 * * MON-FRI")
	public void lateScheduledPollEvents() throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("scheduledPollEvents");
		}
		pollEvents(true, true);
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
	
	public Map<String, Collection<ChildStatus>> pollEvents(boolean checkDate, 
			boolean lateSchedule) throws Exception {
		Map<String, Collection<ChildStatus>> results = Maps.newTreeMap();
		List<PedibusGame> games = storage.getPedibusGames();
		for (PedibusGame game : games) {
			logger.info("Reading game " + game.getGameId() + " events.");
			if(!game.isUsingPedibusData()) {
				logger.info("Game " + game.getGameId() + " skip is not using pedibus data.");
				continue;
			}
			if(game.isLateSchedule() ^ lateSchedule) {
				logger.info("Game " + game.getGameId() + " skip is not scheduled now.");
				continue;
			}
			Map<String, Collection<ChildStatus>> childrenStatusMap = pollGameEvents(game, checkDate);
			for(String routeId : childrenStatusMap.keySet()) {
				Collection<ChildStatus> childrenStatus = childrenStatusMap.get(routeId); 
				if(!isEmptyResponse(childrenStatus)) {
					Map<String, Boolean> updateClassScores = 
							updateCalendarDayFromPedibus(game.getOwnerId(), game.getObjectId(), childrenStatus);
					sendScores(childrenStatus, updateClassScores, game);
					storage.updatePollingFlag(game.getOwnerId(), game.getObjectId(), routeId, Boolean.FALSE);
				}
			}
			results.putAll(childrenStatusMap);
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
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
			
			Criteria criteria = Criteria.where("instituteId").is(game.getInstituteId())
					.and("schoolId").is(game.getSchoolId());
			List<Route> routesList = (List<Route>) storage.findData(Route.class, criteria, 
					null, game.getOwnerId());

			Calendar cal = new GregorianCalendar(TimeZone.getDefault());

//			String from, to;

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

//			from = sdf.format(cal.getTime());
			Date dateFrom = cal.getTime(); 

			// h = (game.getToHour() != null ? game.getToHour() :
			// "23:59").split(":");
			h = game.getToHour().split(":");
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(h[1]));

//			to = sdf.format(cal.getTime());
			Date dateTo = cal.getTime();

			for (Route route : routesList) {
				String routeId = route.getObjectId();
				logger.info("Reading route " + routeId + " events.");
				
				Boolean pollingFlag = game.getPollingFlagMap().get(routeId);
				if((pollingFlag != null) && (pollingFlag == Boolean.FALSE)) {
					logger.info("Events already managed for route " + routeId);
					continue;
				}
				
				List<Integer> eventTypeList = Lists.newArrayList();
				List<String> nodeIdList = Lists.newArrayList();
				List<WsnEvent> eventsList = storage.searchEvents(game.getOwnerId(), routeId, dateFrom, dateTo, 
						eventTypeList, nodeIdList);
				
				if (!eventsList.isEmpty()) {
					Criteria routeCriteria = Criteria.where("routeId").is(routeId);
					Sort sort = new Sort(Sort.Direction.ASC, "position");
					List<Stop> stopList = (List<Stop>) storage.findData(Stop.class, routeCriteria, sort, 
							game.getOwnerId());

					Map<String, Stop> stopsMap = Maps.newTreeMap();
					for (Stop stop : stopList) {
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
	
	public void sendScores(Collection<ChildStatus> childrenStatus, 
			Map<String, Boolean> updateClassScores, PedibusGame game) {
		String address = gamificationURL + "/gengine/execute";
		if(childrenStatus == null) { 
			return;
		}
		for (ChildStatus childStatus: childrenStatus) {
			if(childStatus.isArrived()) {
				//check if is a right classroom
				Criteria childCriteria = Criteria.where("objectId").is(childStatus.getChildId());
				try {
					Child child = storage.findOneData(Child.class, childCriteria, game.getOwnerId());
					if(!game.getClassRooms().contains(child.getClassRoom())) {
						continue;
					}
					Boolean updateClassScore = updateClassScores.get(child.getClassRoom());
					if((updateClassScore == null) || (!updateClassScore)) {
						continue;
					}
				} catch (ClassNotFoundException e) {
					logger.warn(e.getMessage());
					continue;
				}
				
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
					continue;
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
	
	public Map<String, Boolean> updateCalendarDayFromPedibus(String ownerId, String pedibusGameId, 
			Collection<ChildStatus> childrenStatus) {
		
		Map<String, Map<String, String>> classModeMap = new HashMap<String, Map<String,String>>();
		Map<String, Boolean> classUpdateScoreMap = new HashMap<String, Boolean>();
		
		if(childrenStatus == null) {
			return classUpdateScoreMap;
		}
		for(ChildStatus childStatus : childrenStatus) {
			if(childStatus.isArrived()) {
				PedibusPlayer player = storage.getPedibusPlayerByChildId(ownerId, pedibusGameId, childStatus.getChildId());
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
		
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game != null) {
			try {
				Date day = shortSdf.parse(game.getLastDaySeen());
				for(String classRoom : classModeMap.keySet()) {
					Map<String, String> modeMap = classModeMap.get(classRoom);
					Boolean update = storage.updateCalendarDayFromPedibus(ownerId, pedibusGameId, classRoom, day, modeMap);
					classUpdateScoreMap.put(classRoom, update);
				}
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		return classUpdateScoreMap;
	}
	
	public boolean isEmptyResponse(Collection<ChildStatus> childrenStatus) {
		boolean result = true;
		if((childrenStatus != null) && !childrenStatus.isEmpty()) {
			result = false;
		}
		return result;
	}
}
