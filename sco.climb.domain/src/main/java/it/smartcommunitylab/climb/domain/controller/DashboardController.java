package it.smartcommunitylab.climb.domain.controller;

import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.GEngineUtils;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.model.CalendarDay;
import it.smartcommunitylab.climb.domain.model.Excursion;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.PedibusItinerary;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.model.PedibusTeam;
import it.smartcommunitylab.climb.domain.model.Stats;
import it.smartcommunitylab.climb.domain.model.gamification.Challenge;
import it.smartcommunitylab.climb.domain.model.gamification.ChallengeConcept;
import it.smartcommunitylab.climb.domain.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.domain.model.gamification.Notification;
import it.smartcommunitylab.climb.domain.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PointConcept;
import it.smartcommunitylab.climb.domain.model.monitoring.MonitoringChallenge;
import it.smartcommunitylab.climb.domain.model.monitoring.MonitoringItinerary;
import it.smartcommunitylab.climb.domain.model.monitoring.MonitoringPlay;
import it.smartcommunitylab.climb.domain.model.monitoring.MonitoringStats;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Maps;

@Controller
public class DashboardController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(DashboardController.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private GEngineUtils gengineUtils;

	@Autowired
	@Value("${action.calendar}")	
	private String actionCalendar;	

	@Autowired
	@Value("${action.trip}")	
	private String actionTrip;
	
	@Autowired
	@Value("${action.filled}")	
	private String actionCalendarDayFilled;
	
	@Autowired
	@Value("${param.date}")	
	private String paramDate;	
	
	@Autowired
	@Value("${param.meteo}")	
	private String paramMeteo;
	
	@Autowired
	@Value("${param.mode}")	
	private String paramMode;
	
	@Autowired
	@Value("${param.participants}")	
	private String paramParticipants;
	
	@Autowired
	@Value("${param.class.distance}")	
	private String paramClassDistance;
	
	@Autowired
	@Value("${challenge.concept}")	
	private String challengeConcept;

	@RequestMapping(value = "/api/game/player/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.GET)
	public @ResponseBody List<PedibusPlayer> getPlayersByClassRoom(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusPlayer> players = storage.getPedibusPlayersByClassRoom(ownerId, pedibusGameId, classRoom);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPlayersByClassRoom[%s]: %s - %s", ownerId, 
					pedibusGameId, players.size()));
		}
		return players; 
	}
	
	@RequestMapping(value = "/api/game/team/{ownerId}/{pedibusGameId}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusTeam> getTeams(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusTeam> teams = storage.getPedibusTeams(ownerId, pedibusGameId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getTeams[%s]: %s - %s", ownerId, pedibusGameId, teams.size()));
		}
		return teams; 
	}
	
	@RequestMapping(value = "/api/game/calendar/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.POST)
	public @ResponseBody Boolean saveCalendarDay(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom,
			@RequestBody CalendarDay calendarDay,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame_Calendar, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Map<String, Boolean> result = storage.saveCalendarDay(ownerId, pedibusGameId, 
				classRoom, calendarDay);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveCalendarDay[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.toString()));
		}
		if(!result.get(Const.CLOSED)) {
			for(String childId : calendarDay.getModeMap().keySet()) {
				ExecutionDataDTO ed = new ExecutionDataDTO();
				ed.setGameId(game.getGameId());
				ed.setPlayerId(childId);
				ed.setActionId(actionCalendar);
				ed.setExecutionMoment(calendarDay.getDay());
				
				Map<String, Object> data = Maps.newTreeMap();
				data.put(paramMode, calendarDay.getModeMap().get(childId));
				data.put(paramDate, System.currentTimeMillis());
				data.put(paramMeteo, calendarDay.getMeteo());
				ed.setData(data);
				
				try {
					gengineUtils.executeAction(ed);
				} catch (Exception e) {
					logger.warn(String.format("saveCalendarDay[%s]: error in GE excecute action %s - %s",
							ownerId, game.getGameId(), classRoom));
				}
			}			
			ExecutionDataDTO ed = new ExecutionDataDTO();
			ed.setGameId(game.getGameId());
			ed.setPlayerId(classRoom);
			ed.setActionId(actionCalendarDayFilled);
			//ed.setExecutionMoment(calendarDay.getDay());
			try {
				gengineUtils.executeAction(ed);
			} catch (Exception e) {
				logger.warn(String.format("saveCalendarDay[%s]: error in GE excecute action %s - %s",
						ownerId, game.getGameId(), classRoom));
			}		
		}
		return result.get(Const.MERGED);
	}
	
	@RequestMapping(value = "/api/game/calendar/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.GET)
	public @ResponseBody List<CalendarDay> getCalendarDays(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom,
			@RequestParam Long from, 
			@RequestParam Long to, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date dateFrom = new Date(from);
		Date dateTo = new Date(to);
		List<CalendarDay> result = storage.getCalendarDays(ownerId, pedibusGameId, classRoom, 
				dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCalendarDays[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/excursion/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.POST)
	public @ResponseBody void saveExcursion(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom,
			@RequestParam String name, 
			@RequestParam String meteo, 
			@RequestParam Long date, 
			@RequestParam Integer children, 
			@RequestParam Double distance, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Excursion, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date day = new Date(date);
		storage.saveExcursion(ownerId, pedibusGameId, classRoom, name, children, 
				distance, day, meteo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveExcursion[%s]: %s - %s - %s - %s", ownerId, 
					pedibusGameId, classRoom, children, distance));
		}
		ExecutionDataDTO ed = new ExecutionDataDTO();
		ed.setGameId(game.getGameId());
		ed.setPlayerId(classRoom);
		ed.setActionId(actionTrip);
		
		Map<String, Object> data = Maps.newTreeMap();
		data.put(paramParticipants, Double.valueOf(children.toString()));
		data.put(paramClassDistance, distance);
		data.put(paramDate, date);
		data.put(paramMeteo, meteo);
		ed.setData(data);
		
		try {
			gengineUtils.executeAction(ed);
		} catch (Exception e) {
			logger.warn(String.format("saveExcursion[%s]: error in GE excecute action %s - %s",
					ownerId, game.getGameId(), classRoom));
		}
	}	
	
	@RequestMapping(value = "/api/game/excursion/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.GET)
	public @ResponseBody List<Excursion> getExcursions(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom,
			@RequestParam Long from, 
			@RequestParam Long to,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date dateFrom = new Date(from);
		Date dateTo = new Date(to);
		List<Excursion> result = storage.getExcursions(ownerId, pedibusGameId, classRoom, 
				dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExcursions[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/notification/{ownerId}/{pedibusGameId}/{classRoom}", 
			method = RequestMethod.GET)
	public @ResponseBody List<Notification> getNotifications(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom, 
			@RequestParam Long timestamp,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Notification> result = new ArrayList<Notification>();
		if(game != null) {
			List<Notification> classNotifications = gengineUtils.getNotification(game.getGameId(), 
					classRoom, timestamp);
			List<Notification> schoolNotifications = gengineUtils.getNotification(game.getGameId(), 
					game.getGlobalTeam(), timestamp);
			result.addAll(classNotifications);
			result.addAll(schoolNotifications);
			Collections.sort(result, new Comparator<Notification>() {
				@Override
				public int compare(Notification o1, Notification o2) {
					if(o1.getTimestamp() > o2.getTimestamp()) {
						return -1;
					} else if(o1.getTimestamp() < o2.getTimestamp()) {
						return 1;
					} else { 
						return 0; 
					}
				}
			});
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getNotifications[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/challenge/{ownerId}/{pedibusGameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody List<Challenge> getChallenge(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String classRoom, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Challenge> result = new ArrayList<Challenge>();
		if(game != null) {
			PlayerStateDTO playerStatus = gengineUtils.getPlayerStatus(game.getGameId(), classRoom);
			Challenge classChallenge = new Challenge();
			classChallenge.setGameId(game.getGameId());
			classChallenge.setPlayerId(classRoom);
			classChallenge.setState(playerStatus.getState().get(challengeConcept));
			result.add(classChallenge);
			
			playerStatus = gengineUtils.getPlayerStatus(game.getGameId(), game.getGlobalTeam());
			Challenge schoolChallenge = new Challenge();
			schoolChallenge.setGameId(game.getGameId());
			schoolChallenge.setPlayerId(game.getGlobalTeam());
			schoolChallenge.setState(playerStatus.getState().get(challengeConcept));
			result.add(schoolChallenge);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getChallenge[%s]: %s - %s - %s", ownerId, pedibusGameId, 
					classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/stat/{ownerId}/{pedibusGameId}", method = RequestMethod.GET)
	public @ResponseBody Stats getStats(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,  
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Stats result = new Stats();
		if(game != null) {
			PlayerStateDTO playerStatus = gengineUtils.getPlayerStatus(game.getGameId(), game.getGlobalTeam());
			PointConcept pointConcept = gengineUtils.getPointConcept(playerStatus, env.getProperty("score.name"));
			if(pointConcept != null) {
				result.setGameScore(pointConcept.getScore());
			}
			result.setMaxGameScore(getMaxGameScore(ownerId, pedibusGameId));
			
			String key = env.getProperty("stat." + Const.MODE_PIEDI_SOLO);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_PIEDI_SOLO, pointConcept.getScore());
			}
			
			key = env.getProperty("stat." + Const.MODE_PIEDI_ADULTO);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_PIEDI_ADULTO, pointConcept.getScore());
			}
			key = env.getProperty("stat." + Const.MODE_PEDIBUS);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				Double score = result.getScoreModeMap().get(Const.MODE_PIEDI_ADULTO);
				if(score != null) {
					score = score + pointConcept.getScore();
				} else {
					score = pointConcept.getScore();
				}
				result.getScoreModeMap().put(Const.MODE_PIEDI_ADULTO, score);
			}
			
			key = env.getProperty("stat." + Const.MODE_SCUOLABUS);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_SCUOLABUS, pointConcept.getScore());
			}
			
			key = env.getProperty("stat." + Const.MODE_PARK_RIDE);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_PARK_RIDE, pointConcept.getScore());
			}
			
			key = env.getProperty("stat." + Const.MODE_AUTO);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_AUTO, pointConcept.getScore());
			}
			
			key = env.getProperty("stat." + Const.MODE_BONUS);
			pointConcept = gengineUtils.getPointConcept(playerStatus, key);
			if(pointConcept != null) {
				result.getScoreModeMap().put(Const.MODE_BONUS, pointConcept.getScore());
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getStats[%s]: %s", ownerId, pedibusGameId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/monitoring/{ownerId}/{pedibusGameId}", method = RequestMethod.GET)
	public @ResponseBody MonitoringStats getMonitoringStats(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,  
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		MonitoringStats result = new MonitoringStats();

		//stats from global team
		PlayerStateDTO playerStatus = gengineUtils.getPlayerStatus(game.getGameId(), game.getGlobalTeam());
		PointConcept pointConcept = gengineUtils.getPointConcept(playerStatus, env.getProperty("score.name"));
		if(pointConcept != null) {
			result.setGameScore(pointConcept.getScore());
		}
		result.setMaxGameScore(getMaxGameScore(ownerId, pedibusGameId));
		
		String key = env.getProperty("stat." + Const.MODE_PIEDI_SOLO);
		pointConcept = gengineUtils.getPointConcept(playerStatus, key);
		if(pointConcept != null) {
			result.getScoreModeMap().put(Const.MODE_PIEDI_SOLO, pointConcept.getScore());
		}
		
		key = env.getProperty("stat." + Const.MODE_PIEDI_ADULTO);
		pointConcept = gengineUtils.getPointConcept(playerStatus, key);
		if(pointConcept != null) {
			result.getScoreModeMap().put(Const.MODE_PIEDI_ADULTO, pointConcept.getScore());
		}
		key = env.getProperty("stat." + Const.MODE_PEDIBUS);
		pointConcept = gengineUtils.getPointConcept(playerStatus, key);
		if(pointConcept != null) {
			Double score = result.getScoreModeMap().get(Const.MODE_PIEDI_ADULTO);
			if(score != null) {
				score = score + pointConcept.getScore();
			} else {
				score = pointConcept.getScore();
			}
			result.getScoreModeMap().put(Const.MODE_PIEDI_ADULTO, score);
		}
		
		key = env.getProperty("stat." + Const.MODE_SCUOLABUS);
		pointConcept = gengineUtils.getPointConcept(playerStatus, key);
		if(pointConcept != null) {
			result.getScoreModeMap().put(Const.MODE_SCUOLABUS, pointConcept.getScore());
		}
		
		key = env.getProperty("stat." + Const.MODE_PARK_RIDE);
		pointConcept = gengineUtils.getPointConcept(playerStatus, key);
		if(pointConcept != null) {
			result.getScoreModeMap().put(Const.MODE_PARK_RIDE, pointConcept.getScore());
		}
		
		key = env.getProperty("stat." + Const.MODE_AUTO);
		pointConcept = gengineUtils.getPointConcept(playerStatus, key);
		if(pointConcept != null) {
			result.getScoreModeMap().put(Const.MODE_AUTO, pointConcept.getScore());
		}
		
		key = env.getProperty("stat." + Const.MODE_BONUS);
		pointConcept = gengineUtils.getPointConcept(playerStatus, key);
		if(pointConcept != null) {
			result.getScoreModeMap().put(Const.MODE_BONUS, pointConcept.getScore());
		}
		
		List<ChallengeConcept> challengeConcept = gengineUtils.getChallengeConcept(playerStatus);
		for(ChallengeConcept challenge : challengeConcept) {
			MonitoringChallenge monitoringChallenge = new MonitoringChallenge();
			monitoringChallenge.setPlayer(game.getGlobalTeam());
			monitoringChallenge.setName(challenge.getModelName());
			monitoringChallenge.setFrom(getLongDate(challenge.getStart()));
			monitoringChallenge.setTo(getLongDate(challenge.getEnd()));
			monitoringChallenge.setStatus(getChallangeState(challenge));
			monitoringChallenge.setVirtualPrize(getChallangeVirtualPrice(challenge));
			result.getChallenges().add(monitoringChallenge);
		}
		
		//stats from classrooms
		Map<String, PlayerStateDTO> classroomMap = new HashMap<String, PlayerStateDTO>();
		for(String classRoom : game.getClassRooms()) {
			playerStatus = gengineUtils.getPlayerStatus(game.getGameId(), classRoom);
			classroomMap.put(classRoom, playerStatus);
			challengeConcept = gengineUtils.getChallengeConcept(playerStatus);
			for(ChallengeConcept challenge : challengeConcept) {
				MonitoringChallenge monitoringChallenge = new MonitoringChallenge();
				monitoringChallenge.setPlayer(classRoom);
				monitoringChallenge.setName(challenge.getModelName());
				monitoringChallenge.setFrom(getLongDate(challenge.getStart()));
				monitoringChallenge.setTo(getLongDate(challenge.getEnd()));
				monitoringChallenge.setStatus(getChallangeState(challenge));
				monitoringChallenge.setVirtualPrize(getChallangeVirtualPrice(challenge));
				result.getChallenges().add(monitoringChallenge);
			}
		}
		
		//stats from itineraies
		List<PedibusItinerary> itineraryList = storage.getPedibusItineraryByGameId(ownerId, pedibusGameId);
		for(PedibusItinerary itinerary : itineraryList) {
			MonitoringItinerary monitoringItinerary = new MonitoringItinerary();
			monitoringItinerary.setObjectId(itinerary.getObjectId());
			monitoringItinerary.setName(itinerary.getName());
			List<PedibusItineraryLeg> legs = storage.getPedibusItineraryLegsByGameId(ownerId, 
					pedibusGameId, itinerary.getObjectId());
			for(PedibusItineraryLeg leg : legs) {
				if(leg.getScore() <= result.getGameScore()) {
					monitoringItinerary.getReachedLegs().add(leg.getName());
				}
				monitoringItinerary.getLegs().add(leg.getName());
			}
			result.getItineraries().add(monitoringItinerary);
		}
		
		Map<String, MonitoringPlay> monitoringPlay = storage.getMonitoringPlayByGameId(ownerId, pedibusGameId);
		result.getPlays().putAll(monitoringPlay);
		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getMonitoringStats[%s]: %s", ownerId, pedibusGameId));
		}
		return result;
	}
	
	
	
	private String getChallangeVirtualPrice(ChallengeConcept challenge) {
		String virtualPrize = (String) challenge.getFields().get("virtualPrize");
		if(Utils.isNotEmpty(virtualPrize)) {
			return virtualPrize;
		}
		return null;
	}

	private String getChallangeState(ChallengeConcept challenge) {
		if(challenge.isCompleted()) {
			return Const.CHALLANGE_COMPLETED;
		} else {
			if(challenge.isActive(new Date())) {
				return Const.CHALLANGE_ACTIVE;
			}
		}
		return Const.CHALLANGE_FAILD;
	}

	private long getLongDate(Date date) {
		if(date != null) {
			return date.getTime();
		} else {
			return -1;
		}
	}
	
	private Double getMaxGameScore(String ownerId, String pedibusGameId) {
		double result = 0.0;
		List<PedibusItinerary> list = storage.getPedibusItineraryByGameId(ownerId, pedibusGameId);
		if(list.size() > 0) {
			PedibusItinerary pedibusItinerary = list.get(0);
			List<PedibusItineraryLeg> legs = storage.getPedibusItineraryLegsByGameId(ownerId, 
					pedibusGameId, pedibusItinerary.getObjectId());
			PedibusItineraryLeg lastLeg = Collections.max(legs);
			result = lastLeg.getScore();
		}
		return result;
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String,String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String,String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
}
