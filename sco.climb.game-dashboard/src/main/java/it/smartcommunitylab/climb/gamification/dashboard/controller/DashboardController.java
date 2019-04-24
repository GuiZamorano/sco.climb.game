package it.smartcommunitylab.climb.gamification.dashboard.controller;

import it.smartcommunitylab.climb.gamification.dashboard.common.Const;
import it.smartcommunitylab.climb.gamification.dashboard.common.GEngineUtils;
import it.smartcommunitylab.climb.gamification.dashboard.common.Utils;
import it.smartcommunitylab.climb.gamification.dashboard.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.gamification.dashboard.exception.UnauthorizedException;
import it.smartcommunitylab.climb.gamification.dashboard.model.CalendarDay;
import it.smartcommunitylab.climb.gamification.dashboard.model.Excursion;
import it.smartcommunitylab.climb.gamification.dashboard.model.PedibusGame;
import it.smartcommunitylab.climb.gamification.dashboard.model.PedibusPlayer;
import it.smartcommunitylab.climb.gamification.dashboard.model.PedibusTeam;
import it.smartcommunitylab.climb.gamification.dashboard.model.Stats;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.Challenge;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.Notification;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PointConcept;
import it.smartcommunitylab.climb.gamification.dashboard.storage.DataSetSetup;
import it.smartcommunitylab.climb.gamification.dashboard.storage.RepositoryManager;

import java.util.*;

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
public class DashboardController {
	private static final transient Logger logger = LoggerFactory.getLogger(DashboardController.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private RepositoryManager storage;

	@Autowired
	private DataSetSetup dataSetSetup;
	
	@Autowired
	private GEngineUtils gengineUtils;

	@Autowired
	@Value("${action.calendar}")	
	private String actionCalendar;	

	@Autowired
	@Value("${action.trip}")	
	private String actionTrip;
	
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

	private final Map<Integer,String> piActivity = new HashMap<Integer,String>() {{
		put(0, "pandr");
		put(1, "bus");
		put(2, "zeroImpact_wAdult");
		put(3, "zeroImpact_solo");
	}};
	
	@RequestMapping(value = "/api/player/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusPlayer> getPlayersByClassRoom1(@PathVariable String ownerId,
			@PathVariable String gameId, @PathVariable String classRoom, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusPlayer> players = storage.getPedibusPlayersByClassRoom(ownerId, gameId, classRoom);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPlayersByClassRoom[%s]: %s - %s", ownerId, gameId, players.size()));
		}
		return players; 
	}
	@RequestMapping(value = "/api/Babies", method = RequestMethod.GET)
	public @ResponseBody List<PedibusPlayer> getPlayersByClassRoom(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		List<PedibusPlayer> players = new ArrayList<PedibusPlayer>();
//		for(int i =0; i<10; i++){
//			PedibusPlayer pedibusPlayer = new PedibusPlayer();
//			pedibusPlayer.setChildId(Integer.toString(i));
//			pedibusPlayer.setGameId("1");
//			pedibusPlayer.setWsnId("1");
//			pedibusPlayer.setClassRoom("1");
//			pedibusPlayer.setName("Student: "+i);
//			pedibusPlayer.setSurname("Zamo");
//			players.add(pedibusPlayer);
//		}
		List<PedibusPlayer> players = storage.getPedibusPlayersByClassRoom("123", "1", "PROJECT SMART");
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getPlayersByClassRoom[%s]: %s - %s", "123", "1", players.size()));
		}
		return players;
	}
	
	@RequestMapping(value = "/api/team/{ownerId}/{gameId}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusTeam> getTeams(@PathVariable String ownerId, 
			@PathVariable String gameId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<PedibusTeam> teams = storage.getPedibusTeams(ownerId, gameId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getTeams[%s]: %s - %s", ownerId, gameId, teams.size()));
		}
		return teams; 
	}

	@RequestMapping(value = "/api/calendar/index", method = RequestMethod.GET)
	public @ResponseBody Integer getIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Integer index = storage.getIndex("123");

		return index;
	}

	@RequestMapping(value = "/api/calendar/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.POST)
	public @ResponseBody Boolean saveCalendarDay(@PathVariable String ownerId, 
			@PathVariable String gameId, @PathVariable String classRoom,
			@RequestBody CalendarDay calendarDay,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Boolean> result = storage.saveCalendarDay(ownerId, gameId, classRoom, calendarDay.getIndex(), calendarDay);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveCalendarDay[%s]: %s - %s - %s", ownerId, gameId, classRoom, result.toString()));
		}
		PlayerStateDTO teamDTO = storage.getTeamDTO(gameId, "Class");
		double totalScore = 0;
		if(teamDTO.getState().isEmpty()) { //Populate class with possible stats set to zero.
			Map<String, Set<Object>> state = new HashMap<>();
			Set<Object> pointConcepts = new HashSet<>();
			pointConcepts.add(new PointConcept("total_distance", 0.0));
			pointConcepts.add(new PointConcept("zeroImpact_solo_distance", 0.0));
			pointConcepts.add(new PointConcept("zeroImpact_wAdult_distance", 0.0));
			pointConcepts.add(new PointConcept("pedibus_distance", 0.0));
			pointConcepts.add(new PointConcept("bus_distance", 0.0));
			pointConcepts.add(new PointConcept("pandr_distance", 0.0));
			pointConcepts.add(new PointConcept("car_distance", 0.0));
			pointConcepts.add(new PointConcept("bonus_distance", 0.0));
			state.put("PointConcept", pointConcepts);
			teamDTO.setState(state);
		}

		if(!result.get(Const.CLOSED)) {
			for(String childId : calendarDay.getModeMap().keySet()) {
				ExecutionDataDTO ed = new ExecutionDataDTO();
				ed.setGameId(gameId);
				ed.setPlayerId(childId);
				ed.setActionId(actionCalendar);
				
				Map<String, Object> data = Maps.newTreeMap();
				data.put(paramMode, calendarDay.getModeMap().get(childId));
				data.put(paramDate, System.currentTimeMillis());
				data.put(paramMeteo, calendarDay.getMeteo());
				ed.setData(data);

				//Add stats to transport mode. Values are currently big so it shows up on statistics page.
				if(calendarDay.getModeMap().get(childId).equals("zeroImpact_solo")){
					PointConcept pc = gengineUtils.getPointConcept(teamDTO, "zeroImpact_solo_distance");
					pc.setScore(pc.getScore()+300000);
					totalScore+=300000;
				}
				else if(calendarDay.getModeMap().get(childId).equals("zeroImpact_wAdult")){
					PointConcept pc = gengineUtils.getPointConcept(teamDTO, "zeroImpact_wAdult_distance");
					pc.setScore(pc.getScore()+100000);
					totalScore+=300000;
				}
				else if(calendarDay.getModeMap().get(childId).equals("pedibus")){
					PointConcept pc = gengineUtils.getPointConcept(teamDTO, "pedibus_distance");
					pc.setScore(pc.getScore()+100000);
					totalScore+=100000;
				}
				else if(calendarDay.getModeMap().get(childId).equals("bus")){
					PointConcept pc = gengineUtils.getPointConcept(teamDTO, "bus_distance");
					pc.setScore(pc.getScore()+50000);
					totalScore+=50000;
				}
				else if(calendarDay.getModeMap().get(childId).equals("pandr")){
					PointConcept pc = gengineUtils.getPointConcept(teamDTO, "pandr_distance");
					pc.setScore(pc.getScore()+0);
					totalScore+=0;
				}
				else if(calendarDay.getModeMap().get(childId).equals("car")){
					PointConcept pc = gengineUtils.getPointConcept(teamDTO, "car_distance");
					pc.setScore(pc.getScore()+10000);
					totalScore+=10000;
				}
				else if(calendarDay.getModeMap().get(childId).equals("bonus")){
					PointConcept pc = gengineUtils.getPointConcept(teamDTO, "bonus_distance");
					pc.setScore(pc.getScore()+5);
					totalScore+=5;
				}
				try {
					//gengineUtils.executeAction(ed);
					storage.savePlayerDTO(ed);
				} catch (Exception e) {
					logger.warn(String.format("saveCalendarDay[%s]: error in GE excecute action %s - %s",
							ownerId, gameId, classRoom));
				}
			}
			PointConcept pc = gengineUtils.getPointConcept(teamDTO, "total_distance");
			pc.setScore(pc.getScore()+totalScore);
			List<PedibusTeam> pedibusTeams = storage.getPedibusTeams(ownerId, gameId);
			//need to come back and format, should add to team score
			PedibusTeam pedibusTeam = pedibusTeams.get(0) ;
			pedibusTeam.setScore(pedibusTeam.getScore()+totalScore);
			storage.savePedibusTeam(pedibusTeam, ownerId, true);
			storage.updateTeamDTO(teamDTO, gameId, "Class");

		}
		return result.get(Const.MERGED);
	}
	
	@RequestMapping(value = "/api/calendar/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody List<CalendarDay> getCalendarDays(@PathVariable String ownerId, 
			@PathVariable String gameId, @PathVariable String classRoom,
			@RequestParam Integer from, @RequestParam Integer to,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<CalendarDay> result = storage.getCalendarDays(ownerId, gameId, classRoom, from, to);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getCalendarDays[%s]: %s - %s - %s", ownerId, gameId, classRoom, result.size()));
		}
		return result;
	}

	@RequestMapping(value = "/api/weather/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody List<CalendarDay> getWeatherDays(@PathVariable String ownerId,
														   @PathVariable String gameId, @PathVariable String classRoom,
														   @RequestParam String weather,
														   HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<CalendarDay> result = storage.getWeatherDays(ownerId, gameId, classRoom, weather);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getWeatherDays[%s]: %s - %s - %s", ownerId, gameId, classRoom, result.size()));
		}
		return result;
	}

	@RequestMapping(value = "/api/calendar/getSwipes/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody CalendarDay getBabySwipes(@PathVariable String ownerId,
												 @PathVariable String gameId, @PathVariable String classRoom,
												 HttpServletRequest request, HttpServletResponse response) throws Exception {

		return storage.getCalendarDay(ownerId, gameId, classRoom, Integer.MIN_VALUE);
	}

	// This makes more sense as a post but costs less on aws as a get
	@RequestMapping(value = "/api/calendar/submitSwipes/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody boolean submitBabySwipe(@PathVariable String ownerId,
			@PathVariable String gameId, @PathVariable String classRoom,
			@RequestParam Long rfid, @RequestParam Integer activityLevel,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		storage.submitBabySwipe(ownerId, gameId, classRoom, rfid.toString(), piActivity.get(activityLevel));
		return true;
	}

	@RequestMapping(value = "/api/calendar/swipes/clear/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody void clearBabySwipes(@PathVariable String ownerId,
												 @PathVariable String gameId, @PathVariable String classRoom,
												 HttpServletRequest request, HttpServletResponse response) throws Exception {

		storage.clearBabySwipes(ownerId, gameId, classRoom);
	}

	@RequestMapping(value = "/api/excursion/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.POST)
	public @ResponseBody void saveExcursion(@PathVariable String ownerId, 
			@PathVariable String gameId, @PathVariable String classRoom,
			@RequestParam String name, @RequestParam String meteo, @RequestParam Long date, 
			@RequestParam Integer children, @RequestParam Double distance, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date day = new Date(date);
		storage.saveExcursion(ownerId, gameId, classRoom, name, children, distance, day, meteo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("saveExcursion[%s]: %s - %s - %s - %s", ownerId, gameId, classRoom, children, distance));
		}
		ExecutionDataDTO ed = new ExecutionDataDTO();
		ed.setGameId(gameId);
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
					ownerId, gameId, classRoom));
		}
	}	
	
	@RequestMapping(value = "/api/excursion/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody List<Excursion> getExcursions(@PathVariable String ownerId, 
			@PathVariable String gameId, @PathVariable String classRoom,
			@RequestParam Long from, @RequestParam Long to,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date dateFrom = new Date(from);
		Date dateTo = new Date(to);
		List<Excursion> result = storage.getExcursions(ownerId, gameId, classRoom, dateFrom, dateTo);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getExcursions[%s]: %s - %s - %s", ownerId, gameId, classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/notification/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody List<Notification> getNotifications(@PathVariable String ownerId, 
			@PathVariable String gameId, @PathVariable String classRoom, @RequestParam Long timestamp,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Notification> result = new ArrayList<Notification>();
		PedibusGame game = storage.getPedibusGame(ownerId, gameId);
		if(game != null) {
			List<Notification> classNotifications = gengineUtils.getNotification(gameId, classRoom, timestamp);
			List<Notification> schoolNotifications = gengineUtils.getNotification(gameId, game.getGlobalTeam(), timestamp);
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
			logger.info(String.format("getNotifications[%s]: %s - %s - %s", ownerId, gameId, classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/challenge/{ownerId}/{gameId}/{classRoom}", method = RequestMethod.GET)
	public @ResponseBody List<Challenge> getChallenge(@PathVariable String ownerId, 
			@PathVariable String gameId, @PathVariable String classRoom, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Challenge> result = new ArrayList<Challenge>();
		PedibusGame game = storage.getPedibusGame(ownerId, gameId);
		if(game != null) {
			PlayerStateDTO playerStatus = gengineUtils.getPlayerStatus(gameId, classRoom);
			Challenge classChallenge = new Challenge();
			classChallenge.setGameId(gameId);
			classChallenge.setPlayerId(classRoom);
			classChallenge.setState(playerStatus.getState().get(GEngineUtils.challengeConcept));
			result.add(classChallenge);
			
			playerStatus = gengineUtils.getPlayerStatus(gameId, game.getGlobalTeam());
			Challenge schoolChallenge = new Challenge();
			schoolChallenge.setGameId(gameId);
			schoolChallenge.setPlayerId(game.getGlobalTeam());
			schoolChallenge.setState(playerStatus.getState().get(GEngineUtils.challengeConcept));
			result.add(schoolChallenge);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getChallenge[%s]: %s - %s - %s", ownerId, gameId, classRoom, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/stat/{ownerId}/{gameId}", method = RequestMethod.GET)
	public @ResponseBody Stats getStats(@PathVariable String ownerId, @PathVariable String gameId,  
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		Stats result = new Stats();
		PedibusGame game = storage.getPedibusGame(ownerId, gameId);
		if(game != null) {
			PlayerStateDTO playerStatus = storage.getTeamDTO(gameId, "Class");
			PointConcept pointConcept = gengineUtils.getPointConcept(playerStatus, env.getProperty("score.name"));
			if(pointConcept != null) {
				result.setGameScore(pointConcept.getScore());
			}
			result.setMaxGameScore(Double.valueOf(env.getProperty("score.final")));
			
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
			logger.info(String.format("getStats[%s]: %s", ownerId, gameId));
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
