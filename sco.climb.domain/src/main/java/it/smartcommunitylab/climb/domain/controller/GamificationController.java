package it.smartcommunitylab.climb.domain.controller;

import java.io.StringWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.GEngineUtils;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.model.Gamified;
import it.smartcommunitylab.climb.domain.model.Link;
import it.smartcommunitylab.climb.domain.model.PedibusGame;
import it.smartcommunitylab.climb.domain.model.PedibusGameReport;
import it.smartcommunitylab.climb.domain.model.PedibusItinerary;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;
import it.smartcommunitylab.climb.domain.model.PedibusPlayer;
import it.smartcommunitylab.climb.domain.model.PedibusTeam;
import it.smartcommunitylab.climb.domain.model.gamification.BadgeCollectionConcept;
import it.smartcommunitylab.climb.domain.model.gamification.ChallengeModel;
import it.smartcommunitylab.climb.domain.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.domain.model.gamification.GameDTO;
import it.smartcommunitylab.climb.domain.model.gamification.IncrementalClassificationDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PedibusGameConfTemplate;
import it.smartcommunitylab.climb.domain.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PointConcept;
import it.smartcommunitylab.climb.domain.model.gamification.PointConcept.PeriodInternal;
import it.smartcommunitylab.climb.domain.model.gamification.RuleDTO;
import it.smartcommunitylab.climb.domain.model.gamification.RuleValidateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.TeamDTO;
import it.smartcommunitylab.climb.domain.model.multimedia.MultimediaContent;
import it.smartcommunitylab.climb.domain.scheduled.ChildStatus;
import it.smartcommunitylab.climb.domain.scheduled.EventsPoller;
import it.smartcommunitylab.climb.domain.storage.DocumentManager;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Controller
public class GamificationController extends AuthController {

	private static final transient Logger logger = LoggerFactory.getLogger(GamificationController.class);

	@Autowired
	@Value("${action.reset}")
	private String actionReset;	

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
	@Value("${score.name}")	
	private String scoreName;	
	
	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private DocumentManager documentManager;

	@Autowired
	private EventsPoller eventsPoller;
	
	@Autowired
	private GEngineUtils gengineUtils;

	private ObjectMapper mapper = new ObjectMapper();

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/deploy", method = RequestMethod.GET)
	public @ResponseBody void deployGame(
		@PathVariable String ownerId, 
		@PathVariable String pedibusGameId, 
		HttpServletRequest request, 
		HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		if(game.isDeployed()) {
			throw new StorageException("game already deployed");
		}
		//create game
		if(Utils.isEmpty(game.getGameId())) {
			//read legs
			List<PedibusItineraryLeg> legs = new ArrayList<>();
			String finalDestination = "";
			List<PedibusItinerary> itineraryList = storage.getPedibusItineraryByGameId(ownerId, pedibusGameId);
			if(itineraryList.size() > 0) {
				List<PedibusItineraryLeg> legsByGameId = storage.getPedibusItineraryLegsByGameId(ownerId, 
						pedibusGameId, itineraryList.get(0).getObjectId());
				legs.addAll(legsByGameId);
				finalDestination = legsByGameId.get(legsByGameId.size() - 1).getName();	
			}
			//get game conf template
			PedibusGameConfTemplate gameConf = storage.getPedibusGameConfTemplate(game.getConfTemplateId());
			if(gameConf == null) {
				throw new EntityNotFoundException("game conf not found");
			}
			GameDTO gameDTO = new GameDTO();
			gameDTO.setName(game.getGameName());
			//set params
			game.getParams().put("const_school_name", game.getGlobalTeam());
			game.getParams().put("const_number_of_teams", String.valueOf(game.getClassRooms().size() + 1));
			long dailyNominalDistance = Long.valueOf(game.getParams().get("const_daily_nominal_distance"));
			long weeklyNominalDistance = dailyNominalDistance * 5;
			long minDistanceLegsAlmostReached = dailyNominalDistance * 15;
			long almostReachedNextLeg = dailyNominalDistance * 3;
			game.getParams().put("const_weekly_nominal_distance", String.valueOf(weeklyNominalDistance));
			game.getParams().put("const_almost_reached_next_leg", String.valueOf(almostReachedNextLeg));
			game.getParams().put("final_destination", finalDestination);
			//set almost reached rules
			Map<String, Boolean> almostReachedMap = new HashMap<>();
			for(int i = 0; i < legs.size(); i++) {
				PedibusItineraryLeg currentLeg = legs.get(i);
				if(i == 0) {
					almostReachedMap.put(currentLeg.getObjectId(), Boolean.FALSE);
				} else {
					PedibusItineraryLeg previousLeg = legs.get(i - 1);
					if(currentLeg.getScore() - previousLeg.getScore() >= minDistanceLegsAlmostReached) {
						almostReachedMap.put(currentLeg.getObjectId(), Boolean.TRUE);
					} else {
						almostReachedMap.put(currentLeg.getObjectId(), Boolean.FALSE);
					}
				}
			}
			//create actions
			gameDTO.getActions().addAll(gameConf.getActions());
			//create badgeCollections
			for(String badgeCollection : gameConf.getBadgeCollections()) {
				BadgeCollectionConcept badgeCollectionConcept = new BadgeCollectionConcept(badgeCollection);
				gameDTO.getBadgeCollectionConcept().add(badgeCollectionConcept);
			}
			//create point concepts
			for(String pointName : gameConf.getPoints().keySet()) {
				List<String> periods = gameConf.getPoints().get(pointName);
				PointConcept pointConcept = new PointConcept(pointName);
				Map<String, PeriodInternal> intervalMap = new HashMap<>();
				for(String period : periods) {
					if(period.equals("daily")) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(game.getFrom());
						LocalDate ld = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
						ld = ld.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
						Date start = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
						PeriodInternal periodInternal = pointConcept.new PeriodInternal(period, start, 86400000);
						intervalMap.put(period, periodInternal);
					} else if(period.equals("weekly")) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(game.getFrom());
						LocalDate ld = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
						ld = ld.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
						Date start = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
						PeriodInternal periodInternal = pointConcept.new PeriodInternal(period, start, 604800000);
						intervalMap.put(period, periodInternal);
					}
				}
				pointConcept.setPeriods(intervalMap);
				gameDTO.getPointConcept().add(pointConcept);
			}
			//create game
			String gameId = null;
			try {
				gameId = gengineUtils.createGame(gameDTO);
				game.setGameId(gameId);
				storage.updatePedibusGameGameId(ownerId, pedibusGameId, gameId);
				storage.updatePedibusGameDeployed(ownerId, pedibusGameId, true);
			} catch (Exception e) {
				logger.error("Gamification engine game creation error: " + e.getClass() + " " + e.getMessage());
				throw new StorageException("unable to create game:" + e.getMessage());
			}
			//create tasks
			try {
				for(String classificationName : gameConf.getTasks().keySet()) {
					Map<String, String> taskMap = gameConf.getTasks().get(classificationName);
					IncrementalClassificationDTO classificationDTO = new IncrementalClassificationDTO();
					classificationDTO.setClassificationName(classificationName);
					classificationDTO.setName(classificationName);
					classificationDTO.setItemType(taskMap.get("point"));
					classificationDTO.setPeriodName(taskMap.get("period"));
					classificationDTO.setItemsToNotificate(game.getClassRooms().size() + 1);
					classificationDTO.setType(taskMap.get("type"));
					gengineUtils.createTask(gameId, classificationDTO);
				}
			} catch (Exception e) {
				logger.error("Gamification engine task creation error: " + e.getClass() + " " + e.getMessage());
				throw new StorageException("unable to create task:" + e.getMessage());
			}
			//create rules
			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
			velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			velocityEngine.init();
			VelocityContext context = new VelocityContext();
			context.put("params", game.getParams());
			context.put("legList", legs);
			context.put("almostReachedMap", almostReachedMap);
			context.put("Utils", Utils.class); 
			for(String ruleFile : gameConf.getRuleFileTemplates()) {
				String ruleName = ruleFile.replace(".vm", ""); 
				try {
					Template t = velocityEngine.getTemplate("game-template/" + ruleFile);
					StringWriter writer = new StringWriter();
					t.merge(context, writer);
					String ruleContent = writer.toString();
					if(!ruleName.contains("constants")) {
						RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
						ruleValidateDTO.setRule(ruleContent);
						gengineUtils.validateRule(gameId, ruleValidateDTO);
					}
					RuleDTO ruleDTO = new RuleDTO();
					ruleDTO.setName(ruleName);
					ruleDTO.setContent(writer.toString());
					if(ruleName.contains("constants")) {
						int indexOf = ruleName.indexOf("_");
						if(indexOf > 0) {
							ruleDTO.setName(ruleName.substring(0, indexOf));
						}
					}
					gengineUtils.createRule(gameId, ruleDTO);
				} catch (Exception e) {
					logger.error("Gamification engine rule creation error: " + e.getClass() + " " + e.getMessage());
					throw new StorageException("unable to create rule:" + e.getMessage());
				}				
			}			
			//create challenges
			for(String model : gameConf.getChallengeModels().keySet()) {
				List<String> variables = gameConf.getChallengeModels().get(model);
				ChallengeModel challengeModel = new ChallengeModel();
				challengeModel.setName(model);
				challengeModel.setVariables(variables);
				try {
					gengineUtils.createChallenge(gameId, challengeModel);
				} catch (Exception e) {
					logger.error("Gamification engine challenge creation error: " + e.getClass() + " " + e.getMessage());
					throw new StorageException("unable to create challenge:" + e.getMessage());
				}
			}
		}		
		//create players and teams
		List<String> allChildrenId = Lists.newArrayList();
		List<String> allTeamsId = Lists.newArrayList();
		for (String classRoom : game.getClassRooms()) {
			Criteria criteria = Criteria.where("instituteId").is(game.getInstituteId())
					.and("schoolId").is(game.getSchoolId()).and("classRoom").is(classRoom);
			List<Child> childrenList = (List<Child>) storage.findData(Child.class, criteria, null, ownerId);
			List<String> childrenId = Lists.newArrayList();
			for (Child child : childrenList) {
				if(!child.isActiveForGame()) {
					continue;
				}
				childrenId.add(child.getObjectId());
				allChildrenId.add(child.getObjectId());

				PedibusPlayer pp = new PedibusPlayer();
				pp.setChildId(child.getObjectId());
				pp.setWsnId(child.getWsnId());
				pp.setPedibusGameId(pedibusGameId);
				pp.setName(child.getName());
				pp.setSurname(child.getSurname());
				pp.setClassRoom(child.getClassRoom());
				storage.savePedibusPlayer(pp, ownerId, false);

				PlayerStateDTO player = new PlayerStateDTO();
				player.setPlayerId(child.getObjectId());
				player.setGameId(game.getGameId());

				try {
					gengineUtils.createPlayer(game.getGameId(), player);
				} catch (Exception e) {
					logger.warn("Gamification engine player creation warning: " + e.getClass() + " " + e.getMessage());
				}
			}
			PedibusTeam pt = new PedibusTeam();
			pt.setChildrenId(childrenId);
			pt.setGameId(game.getGameId());
			pt.setPedibusGameId(pedibusGameId);
			pt.setClassRoom(classRoom);
			storage.savePedibusTeam(pt, ownerId, false);

			TeamDTO team = new TeamDTO();
			team.setName(classRoom);
			team.setMembers(childrenId);
			team.setPlayerId(classRoom);
			team.setGameId(game.getGameId());
			allTeamsId.add(classRoom);

			try {
				gengineUtils.createTeam(game.getGameId(), team);
			} catch (Exception e) {
				logger.warn("Gamification engine team creation warning: " + e.getClass() + " " + e.getMessage());
			}
		}	
		//create global team
		if (Utils.isNotEmpty(game.getGlobalTeam())) {
			PedibusTeam pt = new PedibusTeam();
			pt.setGameId(game.getGameId());
			pt.setPedibusGameId(pedibusGameId);
			pt.setClassRoom(game.getGlobalTeam());
			storage.savePedibusTeam(pt, ownerId, false);

			TeamDTO team = new TeamDTO();
			team.setName(game.getGlobalTeam());
			team.setMembers(allTeamsId);
			team.setPlayerId(game.getGlobalTeam());
			team.setGameId(game.getGameId());

			try {
				gengineUtils.createTeam(game.getGameId(), team);
			} catch (Exception e) {
				logger.warn("Gamification engine global team creation warning: " + e.getClass() + " " + e.getMessage());
			}				
		}		
		if (logger.isInfoEnabled()) {
			logger.info("deployGame");
		}
	}
	
	@RequestMapping(value = "/api/game/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody PedibusGame createPedibusGame(
			@PathVariable String ownerId, 
			@RequestBody PedibusGame game, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, null, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusGame result = storage.savePedibusGame(game, ownerId, false);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createPedibusGame[%s]: %s", ownerId, game.getObjectId()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}", method = RequestMethod.PUT)
	public @ResponseBody PedibusGame updatePedibusGame(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@RequestBody PedibusGame game, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, null, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		game.setOwnerId(ownerId);
		game.setObjectId(pedibusGameId);
		PedibusGame result = storage.savePedibusGame(game, ownerId, true);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibusGame[%s]: %s", ownerId, pedibusGameId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}", method = RequestMethod.DELETE)
	public @ResponseBody PedibusGame deletePedibusGame(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusGame result = storage.removePedibusGame(ownerId, pedibusGameId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("deletePedibusGame[%s]: %s", ownerId, pedibusGameId));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}", method = RequestMethod.GET)
	public @ResponseBody PedibusGame getPedibusGame(
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
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getPedibusGame[%s]: %s", ownerId, pedibusGameId));
		}
		return game;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}/classes", method = RequestMethod.GET)
	public @ResponseBody List<String> getClassRoomBySchool(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,			
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<Child> children = storage.getChildrenBySchool(ownerId, instituteId, schoolId);
		List<String> result = new ArrayList<String>();
		for(Child child : children) {
			if(!result.contains(child.getClassRoom())) {
				result.add(child.getClassRoom());
			}
		}
		Collections.sort(result);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getClassRoomBySchool[%s]: %s", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}/students", method = RequestMethod.GET)
	public @ResponseBody Integer getStudentByClasses(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@RequestParam List<String> classes,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		int result = 0;
		List<Child> children = storage.getChildrenBySchool(ownerId, instituteId, schoolId);
		for(Child child : children) {
			if(child.isActiveForGame()) {
				if(classes.contains(child.getClassRoom())) {
					result++;
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getStudentByClasses[%s]: %s - %s - %s", ownerId, instituteId, schoolId, result));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/report", method = RequestMethod.GET)
	public @ResponseBody List<PedibusGameReport> getPedibusGameReports(
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		List<PedibusGameReport> result = new ArrayList<>();
		List<PedibusGame> games = storage.getPedibusGames();
		for(PedibusGame game : games) {
			PedibusGameReport report = new PedibusGameReport(game);
			List<PedibusItinerary> itineraryList = storage.getPedibusItineraryByGameId(game.getOwnerId(), game.getObjectId());
			if(itineraryList.size() > 0) {
				PedibusItinerary pedibusItinerary = itineraryList.get(0);
				List<PedibusItineraryLeg> legs = storage.getPedibusItineraryLegsByGameId(game.getOwnerId(), game.getObjectId(), 
						pedibusItinerary.getObjectId());
				report.setLegs(legs.size());
				report.setFirstLeg(legs.get(0).getName());
				report.setFinalLeg(legs.get(legs.size() - 1).getName());
				report.setFinalScore(legs.get(legs.size() - 1).getScore());
				result.add(report);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getPedibusGameReports: %s", result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusGame> getPedibusGamesBySchool(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,			
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, null, null, 
				Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		User user = getUserByEmail(request);
		List<PedibusGame> result = new ArrayList<>();
		List<PedibusGame> list = storage.getPedibusGames(ownerId, instituteId, schoolId);
		for(PedibusGame game : list) {
			if(validateAuthorization(ownerId, instituteId, schoolId, null, game.getObjectId(),
				Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, user)) {
				result.add(game);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getPedibusGamesBySchool[%s]: %s", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary", method = RequestMethod.POST)
	public @ResponseBody PedibusItinerary createItinerary(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@RequestBody PedibusItinerary itinerary, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		itinerary.setPedibusGameId(pedibusGameId);
		itinerary.setOwnerId(ownerId);
		itinerary.setObjectId(Utils.getUUID());
		storage.savePedibusItinerary(itinerary);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createItinerary[%s]: %s", ownerId, pedibusGameId));
		}
		return itinerary; 
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}", method = RequestMethod.PUT)
	public @ResponseBody PedibusItinerary updateItinerary(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@RequestBody PedibusItinerary itinerary, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		itinerary.setPedibusGameId(pedibusGameId);
		itinerary.setOwnerId(ownerId);
		itinerary.setObjectId(itineraryId);
		storage.savePedibusItinerary(itinerary);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updateItinerary[%s]: %s - %s", ownerId, pedibusGameId, itineraryId));
		}
		return itinerary; 
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteItinerary(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removePedibusItinerary(ownerId, pedibusGameId, itineraryId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updateItinerary[%s]: %s - %s", ownerId, pedibusGameId, itineraryId));
		}
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary", method = RequestMethod.GET)
	public @ResponseBody List<PedibusItinerary> getItinerary(
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
		List<PedibusItinerary> result = storage.getPedibusItineraryByGameId(ownerId, pedibusGameId);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("getItinerary[%s]: %s - %s", ownerId, pedibusGameId, result.size()));
		}
		return result;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg", method = RequestMethod.POST)
	public @ResponseBody PedibusItineraryLeg createPedibusItineraryLeg(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@RequestBody PedibusItineraryLeg leg, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		leg.setPedibusGameId(pedibusGameId);
		leg.setItineraryId(itineraryId);
		leg.setOwnerId(ownerId);
		storage.savePedibusItineraryLeg(leg, ownerId, false);
		for (Link link : leg.getExternalUrls()) {
			MultimediaContent content = new MultimediaContent();
			content.setOwnerId(ownerId);
			content.setInstituteId(game.getInstituteId());
			content.setSchoolId(game.getSchoolId());
			content.setItineraryId(itineraryId);
			content.setName(link.getName());
			content.setLegName(leg.getName());
			content.setType(link.getType());
			content.setLink(link.getLink());
			content.setGeocoding(leg.getGeocoding());
			storage.saveMultimediaContent(content);
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createPedibusItineraryLeg[%s]: %s", ownerId, itineraryId));
		}
		return leg;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}", method = RequestMethod.PUT)
	public @ResponseBody PedibusItineraryLeg updatePedibusItineraryLeg(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			@RequestBody PedibusItineraryLeg leg, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		PedibusItineraryLeg legDb = storage.getPedibusItineraryLeg(ownerId, legId);
		if(legDb == null) {
			throw new EntityNotFoundException("pedibus itinerary leg not found");
		}		
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		leg.setPedibusGameId(pedibusGameId);
		leg.setItineraryId(itineraryId);
		leg.setOwnerId(ownerId);
		storage.savePedibusItineraryLeg(leg, ownerId, true);
		for (Link link : leg.getExternalUrls()) {
			MultimediaContent content = new MultimediaContent();
			content.setOwnerId(ownerId);
			content.setInstituteId(game.getInstituteId());
			content.setSchoolId(game.getSchoolId());
			content.setItineraryId(itineraryId);
			content.setName(link.getName());
			content.setLegName(leg.getName());
			content.setType(link.getType());
			content.setLink(link.getLink());
			content.setGeocoding(leg.getGeocoding());
			storage.saveMultimediaContent(content);
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibusItineraryLeg[%s]: %s", ownerId, itineraryId));
		}
		return leg;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/legs", method = RequestMethod.POST)
	public @ResponseBody void createPedibusItineraryLegs(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@RequestBody List<PedibusItineraryLeg> legs, 
			@RequestParam(required = false) Boolean sum, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Collections.sort(legs);
		int sumValue = 0;
		try {
			storage.removePedibusItineraryLegByItineraryId(ownerId, pedibusGameId, itineraryId);
			for (PedibusItineraryLeg leg: legs) {
				leg.setPedibusGameId(pedibusGameId);
				leg.setItineraryId(itineraryId);
				leg.setOwnerId(ownerId);
				if (sum != null && sum) {
					sumValue += leg.getScore();
					leg.setScore(sumValue);
				}
				storage.savePedibusItineraryLeg(leg, ownerId, false);
			}
			if (logger.isInfoEnabled()) {
				logger.info(String.format("createPedibusItineraryLegs[%s]: %s", ownerId, itineraryId));
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}	

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/legs", method = RequestMethod.PUT)
	public @ResponseBody List<PedibusItineraryLeg> updatePedibusItineraryLegs(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@RequestBody List<PedibusItineraryLeg> legs, 
			@RequestParam(required = false) Boolean sum, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Collections.sort(legs);
		int sumValue = 0;
		try {
			storage.removePedibusItineraryLegByItineraryId(ownerId, pedibusGameId, itineraryId);
//			storage.removeMultimediaContentByItineraryId(ownerId, game.getInstituteId(), 
//					game.getSchoolId(), itineraryId);
			for (PedibusItineraryLeg leg: legs) {
				leg.setPedibusGameId(pedibusGameId);
				leg.setItineraryId(itineraryId);
				leg.setOwnerId(ownerId);
				if (sum != null && sum) {
					sumValue += leg.getScore();
					leg.setScore(sumValue);
				}
				storage.savePedibusItineraryLeg(leg, ownerId, false);
				for (Link link : leg.getExternalUrls()) {
					MultimediaContent content = new MultimediaContent();
					content.setOwnerId(ownerId);
					content.setInstituteId(game.getInstituteId());
					content.setSchoolId(game.getSchoolId());
					content.setItineraryId(itineraryId);
					content.setName(link.getName());
					content.setLegName(leg.getName());
					content.setType(link.getType());
					content.setLink(link.getLink());
					content.setGeocoding(leg.getGeocoding());
					storage.saveMultimediaContent(content);
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info(String.format("updatePedibusItineraryLegs[%s]: %s", ownerId, itineraryId));
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
		return legs;
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}/links", method = RequestMethod.PUT)
	public @ResponseBody List<Link> updatePedibusItineraryLegLinks(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			@RequestBody List<Link> links, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		PedibusItineraryLeg leg = storage.getPedibusItineraryLeg(ownerId, legId);
		if(leg == null) {
			throw new EntityNotFoundException("pedibus itinerary leg not found");
		}		
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Link, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.updatePedibusItineraryLegLink(ownerId, legId, links);
		for (Link link : links) {
			MultimediaContent content = new MultimediaContent();
			content.setOwnerId(ownerId);
			content.setInstituteId(game.getInstituteId());
			content.setSchoolId(game.getSchoolId());
			content.setItineraryId(itineraryId);
			content.setName(link.getName());
			content.setLegName(leg.getName());
			content.setType(link.getType());
			content.setLink(link.getLink());
			content.setGeocoding(leg.getGeocoding());
			storage.saveMultimediaContent(content);
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("updatePedibusItineraryLegLinks[%s]: %s", ownerId, itineraryId));
		}
		return links;
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}/link/file", method = RequestMethod.POST)
	public @ResponseBody Link uploadPedibusItineraryLegLinkFile(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
			@RequestParam(required = false) MultipartFile file, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		PedibusItineraryLeg leg = storage.getPedibusItineraryLeg(ownerId, legId);
		if(leg == null) {
			throw new EntityNotFoundException("pedibus itinerary leg not found");
		}		
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame_Link, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		String url = documentManager.uploadFile(file);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("uploadPedibusItineraryLegLinkFile: %s - %s - %s", ownerId, legId, url));
		}
		Link link = new Link();
		link.setLink(url);
		return link;
	}

	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/leg/{legId}", method = RequestMethod.GET)
	public @ResponseBody PedibusItineraryLeg getPedibusItineraryLeg(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			@PathVariable String legId,
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
		try {
			PedibusItineraryLeg result = storage.getPedibusItineraryLeg(ownerId, legId);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("getPedibusItineraryLeg[%s]: %s", ownerId, legId));
			}
			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/legs", 
			method = RequestMethod.GET)
	public @ResponseBody List<PedibusItineraryLeg> getPedibusItineraryLegs(
			@PathVariable String ownerId,
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
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
		try {
			List<PedibusItineraryLeg> result = storage.getPedibusItineraryLegsByGameId(ownerId, 
					pedibusGameId, itineraryId);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("getPedibusItineraryLegs[%s]: %s", ownerId, result.size()));
			}
			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/game/{ownerId}/{pedibusGameId}/itinerary/{itineraryId}/status", 
			method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getGameStatus(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			@PathVariable String itineraryId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		PedibusItinerary itinerary = storage.getPedibusItinerary(ownerId, pedibusGameId, itineraryId);
		if(itinerary == null) {
			throw new EntityNotFoundException("itinerary not found");
		}
		/*if(!validateAuthorizationByExp(ownerId, game.getInstituteId(), game.getSchoolId(), null, 
				pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}*/
		try {
			List<PedibusItineraryLeg> legs = storage.getPedibusItineraryLegsByGameId(ownerId, 
					pedibusGameId, itineraryId);
			PedibusItineraryLeg lastLeg = Collections.max(legs);
			
			// players score
			/**
			List<PedibusPlayer> players = storage.getPedibusPlayers(ownerId, gameId);

			for (PedibusPlayer player : players) {
				updateGamificationData(player, gameId, player.getChildId());
			}
			**/

			// teams score
			List<PedibusTeam> teams = storage.getPedibusTeams(ownerId, pedibusGameId);
			for (PedibusTeam team : teams) {
				updateGamificationData(team, pedibusGameId, game.getGameId(), team.getClassRoom());

				// find "current" leg
				for (PedibusItineraryLeg leg : legs) {
					if (team.getScore() >= leg.getScore()) {
						team.setPreviousLeg(leg);
					} else {
						team.setCurrentLeg(leg);
						break;
					}
				}

				if (team.getCurrentLeg() != null) {
					team.setScoreToNext(team.getCurrentLeg().getScore() - team.getScore());
				}
				team.setScoreToEnd(lastLeg.getScore() - team.getScore());
			}

			Map<String, Object> result = Maps.newTreeMap();
			result.put("game", game);
			result.put("itinerary", itinerary);
			result.put("legs", legs);
			//result.put("players", players);
			result.put("teams", teams);

			if (logger.isInfoEnabled()) {
				logger.info(String.format("getGameStatus[%s]: %s", ownerId, pedibusGameId));
			}

			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/events/{ownerId}/{pedibusGameId}", method = RequestMethod.PATCH)
	public @ResponseBody Map<String, Collection<ChildStatus>> pollEvents(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		try {
			Map<String, Collection<ChildStatus>> childrenStatusMap = eventsPoller.pollGameEvents(game, false);
			for(String routeId : childrenStatusMap.keySet()) {
				Collection<ChildStatus> childrenStatus = childrenStatusMap.get(routeId);
				if(!eventsPoller.isEmptyResponse(childrenStatus)) {
					Map<String, Boolean> updateClassScores = 
							eventsPoller.updateCalendarDayFromPedibus(ownerId, pedibusGameId, childrenStatus); 
					eventsPoller.sendScores(childrenStatus, updateClassScores, game);
					storage.updatePollingFlag(ownerId, pedibusGameId, routeId, Boolean.FALSE);
				}
			}
			return childrenStatusMap;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/child/score/{ownerId}/{pedibusGameId}/{playerId}", method = RequestMethod.GET)
	public @ResponseBody void increaseChildScore(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			@PathVariable String playerId, 
			@RequestParam Double score, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		ExecutionDataDTO ed = new ExecutionDataDTO();
		ed.setGameId(game.getGameId());
		ed.setPlayerId(playerId);
		ed.setActionId(actionPedibus);

		Map<String, Object> data = Maps.newTreeMap();
		data.put(paramDistance, score);
		Date date = new Date();
		data.put(paramDate, date.getTime());
		ed.setData(data);
		
		gengineUtils.executeAction(ed);
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("increaseChildScore[%s]: increased game[%s] player[%s] score[%s]", ownerId,
					game.getGameId(), playerId, score));
		}			
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{pedibusGameId}/reset", method = RequestMethod.GET)
	public @ResponseBody void resetGame(
			@PathVariable String ownerId, 
			@PathVariable String pedibusGameId, 
			HttpServletRequest request,	
			HttpServletResponse response) throws Exception {
		PedibusGame game = storage.getPedibusGame(ownerId, pedibusGameId);
		if(game == null) {
			throw new EntityNotFoundException("game not found");
		}
		if(!validateAuthorization(ownerId, game.getInstituteId(), game.getSchoolId(), 
				null, pedibusGameId, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Date now = new Date();
		if(now.after(game.getFrom()) && now.before(game.getTo())) {
			//TODO for testing only
//			throw new StorageException("game is in progress");
		}
		
		if(Utils.isNotEmpty(game.getGameId())) {
			try {
				List<PedibusPlayer> players = storage.getPedibusPlayers(ownerId, pedibusGameId);
				for (PedibusPlayer player: players) {
					gengineUtils.deletePlayerState(game.getGameId(), player.getChildId());
				}
				
				List<PedibusTeam> teams = storage.getPedibusTeams(ownerId, pedibusGameId);
				for (PedibusTeam team: teams) {
					gengineUtils.deletePlayerState(game.getGameId(), team.getClassRoom());
				}
				
				gengineUtils.deleteChallenges(game.getGameId());
				
				gengineUtils.deleteRules(game.getGameId());
				
				gengineUtils.deleteGame(game.getGameId());
			} catch (Exception e) {
				throw e;
			}
		}
		
		storage.removePedibusPlayerByGameId(ownerId, pedibusGameId);
		
		storage.removePedibusTeamByGameId(ownerId, pedibusGameId);
		
		storage.removeExcursionByGameId(ownerId, pedibusGameId);
		
		storage.removeCalendarDayByGameId(ownerId, pedibusGameId);
		
		storage.updatePedibusGameGameId(ownerId, pedibusGameId, null);
		
		storage.updatePedibusGameDeployed(ownerId, pedibusGameId, false);
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("resetGame[%s]: %s", ownerId, pedibusGameId));
		}	
	}
	
	@RequestMapping(value = "/api/game/{ownerId}/{instituteId}/{schoolId}/clone/{pedibusGameId}", method = RequestMethod.GET)
	public @ResponseBody PedibusGame cloneGame(			
			@PathVariable String ownerId, 
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@PathVariable String pedibusGameId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, 
				null, null, Const.AUTH_RES_PedibusGame, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		PedibusGame gameToClone = storage.getPedibusGame(pedibusGameId);
		if(gameToClone == null) {
			throw new EntityNotFoundException("game to clone not found");
		}
		List<PedibusItineraryLeg> legsToClone = new ArrayList<>();
		PedibusItinerary itineraryToClone = null;
		List<PedibusItinerary> itineraryList = storage.getPedibusItineraryByGameId(ownerId, pedibusGameId);
		if(itineraryList.size() > 0) {
			itineraryToClone = itineraryList.get(0);
			legsToClone = storage.getPedibusItineraryLegsByGameId(ownerId, pedibusGameId, 
					itineraryToClone.getObjectId());
		}
		//create game
		PedibusGame game = new PedibusGame();
		game.setOwnerId(ownerId);
		game.setInstituteId(instituteId);
		game.setSchoolId(schoolId);
		game.setGameName(gameToClone.getGameName() + " - clone");
		game.setConfTemplateId(gameToClone.getConfTemplateId());
		storage.savePedibusGame(game, ownerId, false);
		if(itineraryToClone!= null) {
			//create itinerary
			PedibusItinerary itinerary = new PedibusItinerary();
			itinerary.setOwnerId(ownerId);
			itinerary.setPedibusGameId(game.getObjectId());
			itinerary.setObjectId(Utils.getUUID());
			itinerary.setName(itineraryToClone.getName());
			itinerary.setDescription(itineraryToClone.getDescription());
			storage.savePedibusItinerary(itinerary);
			//create legs
			for(PedibusItineraryLeg legToClone : legsToClone) {
				PedibusItineraryLeg leg = new PedibusItineraryLeg();
				leg.setOwnerId(ownerId);
				leg.setPedibusGameId(game.getObjectId());
				leg.setItineraryId(itinerary.getObjectId());
				leg.setName(legToClone.getName());
				leg.setDescription(legToClone.getDescription());
				leg.setBadgeId(legToClone.getBadgeId());
				leg.setPosition(legToClone.getPosition());
				leg.setGeocoding(legToClone.getGeocoding());
				leg.setExternalUrls(legToClone.getExternalUrls());
				leg.setImageUrl(legToClone.getImageUrl());
				leg.setPolyline(legToClone.getPolyline());
				leg.setScore(legToClone.getScore());
				leg.setTransport(legToClone.getTransport());
				leg.setIcon(legToClone.getIcon());
				leg.setAdditionalPoints(legToClone.getAdditionalPoints());
				storage.savePedibusItineraryLeg(leg, ownerId, false);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("cloneGame[%s]: %s", ownerId, pedibusGameId));
		}
		return game;
	}
	
	@RequestMapping(value = "/api/game/{shortName}", method = RequestMethod.GET)
	public void redirectShortName(
			@PathVariable String shortName,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		List<PedibusGame> games = storage.getPedibusGamesByShortName(shortName);
		if(games.size() != 1) {
			throw new EntityNotFoundException("short name is not unique or not present");
		}
		PedibusGame game = games.get(0);
		StringBuilder redirectUrl = new StringBuilder(request.getContextPath());
		List<PedibusItinerary> itineraryList = storage.getPedibusItineraryByGameId(game.getOwnerId(), game.getObjectId());
		if(itineraryList.size() == 0) {
			throw new EntityNotFoundException("game has no itinerary");
		}
		redirectUrl.append("/game-public/index.html#/" + game.getOwnerId());
		redirectUrl.append("/" + game.getObjectId());
		redirectUrl.append("/" + itineraryList.get(0).getObjectId());
		redirectUrl.append("/map");
		response.sendRedirect(redirectUrl.toString());
	}
	
	@SuppressWarnings("rawtypes")
	private void updateGamificationData(Gamified entity, String pedibusGameId, String gameId, String id) throws Exception {
		
		PlayerStateDTO gamePlayer = gengineUtils.getPlayerStatus(gameId, id);
		
		entity.setGameStatus(gamePlayer);
		
		Set<?> pointConcept = (Set) gamePlayer.getState().get("PointConcept");
		
		if (pointConcept != null) {
			Iterator<?> it = pointConcept.iterator();
			while (it.hasNext()) {
				PointConcept pc = mapper.convertValue(it.next(), PointConcept.class);
				if (scoreName.equals(pc.getName())) {
					entity.setScore(pc.getScore());
				}
			}
		}

		/**
		Set<?> badgeCollectionConcept = (Set) gamePlayer.getState().get("BadgeCollectionConcept");
		if (badgeCollectionConcept != null) {
			Map<String, Collection> badges = Maps.newTreeMap();
			Iterator<?> it = badgeCollectionConcept.iterator();
			while (it.hasNext()) {
				BadgeCollectionConcept bcc = mapper.convertValue(it.next(), BadgeCollectionConcept.class);
				badges.put(bcc.getName(), bcc.getBadgeEarned());
			}
			entity.setBadges(badges);
		}
		**/
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
