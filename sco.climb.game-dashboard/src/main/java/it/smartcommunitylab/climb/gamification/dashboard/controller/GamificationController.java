package it.smartcommunitylab.climb.gamification.dashboard.controller;

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.gamification.dashboard.common.GEngineUtils;
import it.smartcommunitylab.climb.gamification.dashboard.common.Utils;
import it.smartcommunitylab.climb.gamification.dashboard.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.gamification.dashboard.exception.UnauthorizedException;
import it.smartcommunitylab.climb.gamification.dashboard.model.Gamified;
import it.smartcommunitylab.climb.gamification.dashboard.model.PedibusGame;
import it.smartcommunitylab.climb.gamification.dashboard.model.PedibusItineraryLeg;
import it.smartcommunitylab.climb.gamification.dashboard.model.PedibusPlayer;
import it.smartcommunitylab.climb.gamification.dashboard.model.PedibusTeam;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.CustomData;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PointConcept;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.TeamDTO;
import it.smartcommunitylab.climb.gamification.dashboard.scheduled.ChildStatus;
import it.smartcommunitylab.climb.gamification.dashboard.scheduled.EventsPoller;
import it.smartcommunitylab.climb.gamification.dashboard.storage.DataSetSetup;
import it.smartcommunitylab.climb.gamification.dashboard.storage.RepositoryManager;
import it.smartcommunitylab.climb.gamification.dashboard.utils.HTTPUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
public class GamificationController {

	private static final transient Logger logger = LoggerFactory.getLogger(GamificationController.class);

	@Autowired
	@Value("${contextstore.url}")
	private String contextstoreURL;
	
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
	private DataSetSetup dataSetSetup;

	@Autowired
	private EventsPoller eventsPoller;
	
	@Autowired
	private GEngineUtils gengineUtils;

	private ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = "/api/game/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody void createPedibusGame(@PathVariable String ownerId, @RequestBody PedibusGame game, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}

		List<String> allChildrenId = Lists.newArrayList();
		List<String> allTeamsId = Lists.newArrayList();
		
		try {
			String token = request.getHeader("X-ACCESS-TOKEN");

			game.setToken(token);

			storage.savePedibusGame(game, ownerId, false);

			for (String classRoom : game.getClassRooms()) {

				String address = contextstoreURL + "/api/child/" + ownerId + "/" + game.getSchoolId() + "/classroom?classRoom=" + classRoom;

				String result = HTTPUtils.get(address, token, null, null);

				List<?> children = mapper.readValue(result, List.class);
				List<String> childrenId = Lists.newArrayList();

				for (Object c : children) {
					Child child = mapper.convertValue(c, Child.class);
					childrenId.add(child.getObjectId());
					allChildrenId.add(child.getObjectId());

					PedibusPlayer pp = new PedibusPlayer();
					pp.setChildId(child.getObjectId());
					pp.setWsnId(child.getWsnId());
					pp.setGameId(game.getGameId());
					pp.setName(child.getName());
					pp.setSurname(child.getSurname());
					pp.setClassRoom(child.getClassRoom());
					storage.savePedibusPlayer(pp, ownerId, false);

					PlayerStateDTO player = new PlayerStateDTO();
					player.setPlayerId(child.getObjectId());
					player.setGameId(game.getGameId());
					CustomData cd = new CustomData();
					cd.put("name", child.getName());
					cd.put("surname", child.getSurname());
					player.setCustomData(cd);

					try {
						gengineUtils.createPlayer(game.getGameId(), player);
					} catch (Exception e) {
						logger.warn("Gamification engine player creation warning: " + e.getClass() + " " + e.getMessage());
					}
				}
				PedibusTeam pt = new PedibusTeam();
				pt.setChildrenId(childrenId);
				pt.setGameId(game.getGameId());
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
			
			if (game.getGlobalTeam() != null && !game.getGlobalTeam().isEmpty()) {
				PedibusTeam pt = new PedibusTeam();
				pt.setChildrenId(allChildrenId);
				pt.setGameId(game.getGameId());
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
				logger.info("add pedibusGame");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}

	@RequestMapping(value = "/api/game/{ownerId}", method = RequestMethod.PUT)
	public @ResponseBody void updatePedibusGame(@PathVariable String ownerId, @RequestBody PedibusGame game, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		
		List<String> allChildrenId = Lists.newArrayList();
		List<String> allTeamsId = Lists.newArrayList();

		try {
			String token = request.getHeader("X-ACCESS-TOKEN");

			game.setToken(token);

			storage.savePedibusGame(game, ownerId, true);

			for (String classRoom : game.getClassRooms()) {

				String address = contextstoreURL + "/api/child/" + ownerId + "/" + game.getSchoolId() + "/classroom?classRoom=" + classRoom;

				String result = HTTPUtils.get(address, token, null, null);

				List<?> children = mapper.readValue(result, List.class);
				List<String> childrenId = Lists.newArrayList();

				for (Object c : children) {
					Child child = mapper.convertValue(c, Child.class);
					childrenId.add(child.getObjectId());
					allChildrenId.add(child.getObjectId());

					PedibusPlayer pp = new PedibusPlayer();
					pp.setChildId(child.getObjectId());
					pp.setWsnId(child.getWsnId());
					pp.setGameId(game.getGameId());
					boolean updated = storage.savePedibusPlayer(pp, ownerId, true);

					if (!updated) {
						PlayerStateDTO player = new PlayerStateDTO();
						player.setPlayerId(child.getObjectId());
						player.setGameId(game.getGameId());
						CustomData cd = new CustomData();
						cd.put("name", child.getName());
						cd.put("surname", child.getSurname());
						player.setCustomData(cd);

						try {
							gengineUtils.createPlayer(game.getGameId(), player);
						} catch (Exception e) {
							logger.warn("Gamification engine player creation warning: " + e.getClass() + " " + e.getMessage());
						}						
					}
				}
				PedibusTeam pt = new PedibusTeam();
				pt.setChildrenId(childrenId);
				pt.setGameId(game.getGameId());
				pt.setClassRoom(classRoom);
				boolean updated = storage.savePedibusTeam(pt, ownerId, true);

				if (!updated) {
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
					
				} else {
					try {
						gengineUtils.createTeamMembers(game.getGameId(), classRoom, childrenId);
					} catch (Exception e) {
						logger.warn("Gamification engine team update warning: " + e.getClass() + " " + e.getMessage());
					}					
				}
			}
			
			if (game.getGlobalTeam() != null && !game.getGlobalTeam().isEmpty()) {
				PedibusTeam pt = new PedibusTeam();
				pt.setChildrenId(allChildrenId);
				pt.setGameId(game.getGameId());
				pt.setClassRoom(game.getGlobalTeam());
				boolean updated = storage.savePedibusTeam(pt, ownerId, true);

				if (!updated) {
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
				} else {
					try {
						gengineUtils.createTeamMembers(game.getGameId(), game.getGlobalTeam(), allChildrenId);
					} catch (Exception e) {
						logger.warn("Gamification engine global team update warning: " + e.getClass() + " " + e.getMessage());
					}					
				}
			}			

			if (logger.isInfoEnabled()) {
				logger.info("update pedibusGame");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}

	@RequestMapping(value = "/api/game/{ownerId}/{gameId}", method = RequestMethod.GET)
	public @ResponseBody PedibusGame getPedibusGame(@PathVariable String ownerId, @PathVariable String gameId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}

		try {
			PedibusGame result = storage.getPedibusGame(ownerId, gameId);

			if (logger.isInfoEnabled()) {
				logger.info(String.format("getPedibusGame[%s]: %s", ownerId, gameId));
			}
			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/{ownerId}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusGame> getPedibusGames(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}

		try {
			List<PedibusGame> result = storage.getPedibusGames(ownerId);

			if (logger.isInfoEnabled()) {
				logger.info(String.format("getPedibusGames[%s]: %s", ownerId, result.size()));
			}
			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/leg/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody void createPedibusItineraryLeg(@PathVariable String ownerId, 
			@RequestBody PedibusItineraryLeg leg, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}

		try {
			leg.setLegId(getUUID());
			storage.savePedibusItineraryLeg(leg, ownerId, false);

			if (logger.isInfoEnabled()) {
				logger.info("add pedibusItineraryLeg");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}
	
	@RequestMapping(value = "/api/legs/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody void createPedibusItineraryLegs(@PathVariable String ownerId, 
			@RequestBody List<PedibusItineraryLeg> legs, @RequestParam(required = false) Boolean sum, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}

		Collections.sort(legs);
		int sumValue = 0;
		try {
			for (PedibusItineraryLeg leg: legs) {
				leg.setLegId(getUUID());
				if (sum != null && sum) {
					sumValue += leg.getScore();
					leg.setScore(sumValue);
				}
				storage.savePedibusItineraryLeg(leg, ownerId, false);
			}

			if (logger.isInfoEnabled()) {
				logger.info("add pedibusItineraryLegs");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}	

	@RequestMapping(value = "/api/leg/{ownerId}/{legId}", method = RequestMethod.GET)
	public @ResponseBody PedibusItineraryLeg getPedibusItineraryLeg(@PathVariable String ownerId, 
			@PathVariable String legId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
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

	@RequestMapping(value = "/api/leg/{ownerId}", method = RequestMethod.GET)
	public @ResponseBody List<PedibusItineraryLeg> getPedibusItineraryLegs(@PathVariable String ownerId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}

		try {
			List<PedibusItineraryLeg> result = storage.getPedibusItineraryLegs(ownerId);

			if (logger.isInfoEnabled()) {
				logger.info(String.format("getPedibusItineraryLegs[%s]: %s", ownerId, result.size()));
			}
			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/status/{ownerId}/{gameId}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getGameStatus(@PathVariable String ownerId, @PathVariable String gameId, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}

		try {
			PedibusGame game = storage.getPedibusGame(ownerId, gameId);
			game.setToken("*****");
			List<PedibusItineraryLeg> legs = storage.getPedibusItineraryLegsByGameId(ownerId, gameId);
			PedibusItineraryLeg lastLeg = Collections.max(legs);
			
			// players score
			/**
			List<PedibusPlayer> players = storage.getPedibusPlayers(ownerId, gameId);

			for (PedibusPlayer player : players) {
				updateGamificationData(player, gameId, player.getChildId());
			}
			**/

			// teams score
			List<PedibusTeam> teams = storage.getPedibusTeams(ownerId, gameId);
			for (PedibusTeam team : teams) {
				updateGamificationData(team, gameId, team.getClassRoom());

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
			result.put("legs", legs);
			//result.put("players", players);
			result.put("teams", teams);

			if (logger.isInfoEnabled()) {
				logger.info(String.format("getGameStatus[%s]: %s", ownerId, gameId));
			}

			return result;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/game/events/{ownerId}/{gameId}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Collection<ChildStatus>> pollEvents(@PathVariable String ownerId, 
			@PathVariable String gameId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		try {
			PedibusGame game = storage.getPedibusGame(ownerId, gameId);
			Map<String, Collection<ChildStatus>> childrenStatusMap = eventsPoller.pollGameEvents(game, false);
			for(String routeId : childrenStatusMap.keySet()) {
				Collection<ChildStatus> childrenStatus = childrenStatusMap.get(routeId);
				if(!eventsPoller.isEmptyResponse(childrenStatus)) {
					eventsPoller.sendScores(childrenStatus, game);
					storage.updatePollingFlag(game.getOwnerId(), game.getGameId(), routeId, Boolean.FALSE);
					eventsPoller.updateCalendarDayFromPedibus(game.getOwnerId(), game.getGameId(), childrenStatus);
				}
			}
			return childrenStatusMap;
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
			return null;
		}
	}

	@RequestMapping(value = "/api/child/score/{ownerId}/{gameId}", method = RequestMethod.GET)
	public @ResponseBody void increaseChildScore(@PathVariable String ownerId, @PathVariable String gameId, 
			@RequestParam String playerId, @RequestParam Double score, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		
		ExecutionDataDTO ed = new ExecutionDataDTO();
		ed.setGameId(gameId);
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
					gameId, playerId, score));
		}			
	}
	
	@RequestMapping(value = "/api/game/reset/{ownerId}/{gameId}", method = RequestMethod.GET)
	public @ResponseBody void resetGame(@PathVariable String ownerId, @PathVariable String gameId, 
			HttpServletRequest request,	HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}

		try {
			List<PedibusPlayer> players = storage.getPedibusPlayers(ownerId, gameId);
			for (PedibusPlayer player: players) {
				resetChild(gameId, player.getChildId());
			}
			
			List<PedibusTeam> teams = storage.getPedibusTeams(ownerId, gameId);
			for (PedibusTeam team: teams) {
				resetChild(gameId, team.getClassRoom());
			}			
			
			if (logger.isInfoEnabled()) {
				logger.info("reset game");
			}			
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}	
	
	@RequestMapping(value = "/api/child/reset/{ownerId}/{gameId}", method = RequestMethod.GET)
	public @ResponseBody void resetChild(@PathVariable String ownerId, @PathVariable String gameId, 
			@RequestParam String playerId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!Utils.validateAPIRequest(request, dataSetSetup, storage)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}

		try {
			resetChild(gameId, playerId);
			
			if (logger.isInfoEnabled()) {
				logger.info("reset player");
			}			
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Throwables.getStackTraceAsString(e));
		}
	}		
	
	private void resetChild(String gameId, String playerId) throws Exception {
		ExecutionDataDTO ed = new ExecutionDataDTO();
		ed.setGameId(gameId);
		ed.setPlayerId(playerId);
		ed.setActionId(actionReset);

		Map<String, Object> data = Maps.newTreeMap();
		ed.setData(data);
		
		gengineUtils.executeAction(ed);
	}
	

	@SuppressWarnings("rawtypes")
	private void updateGamificationData(Gamified entity, String gameId, String id) throws Exception {
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
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
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
