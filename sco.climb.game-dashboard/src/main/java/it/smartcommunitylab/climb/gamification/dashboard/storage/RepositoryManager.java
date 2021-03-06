package it.smartcommunitylab.climb.gamification.dashboard.storage;

import it.smartcommunitylab.climb.gamification.dashboard.common.Const;
import it.smartcommunitylab.climb.gamification.dashboard.common.GEngineUtils;
import it.smartcommunitylab.climb.gamification.dashboard.exception.StorageException;
import it.smartcommunitylab.climb.gamification.dashboard.model.*;
import it.smartcommunitylab.climb.gamification.dashboard.model.events.WsnEvent;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PointConcept;
import it.smartcommunitylab.climb.gamification.dashboard.security.DataSetInfo;
import it.smartcommunitylab.climb.gamification.dashboard.security.Token;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class RepositoryManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RepositoryManager.class);

	@Autowired
	private GEngineUtils gengineUtils;
	
	private MongoTemplate mongoTemplate;
	private String defaultLang;

	private static final SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");


	public RepositoryManager(MongoTemplate template, String defaultLang) {
		this.mongoTemplate = template;
		this.defaultLang = defaultLang;
	}
	
	public String getDefaultLang() {
		return defaultLang;
	}

	public Token findTokenByToken(String token) {
		Query query = new Query(new Criteria("token").is(token));
		Token result = mongoTemplate.findOne(query, Token.class);
		return result;
	}
	
	public List<DataSetInfo> getDataSetInfo() {
		List<DataSetInfo> result = mongoTemplate.findAll(DataSetInfo.class);
		return result;
	}

	public List<PedibusGame> getPedibusGames() {
		return mongoTemplate.findAll(PedibusGame.class);		
	}		
	
	public PedibusGame getPedibusGame(String ownerId, String gameId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId));
		return mongoTemplate.findOne(query, PedibusGame.class);		
	}		
	
	public List<PedibusGame> getPedibusGames(String ownerId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId));
		return mongoTemplate.find(query, PedibusGame.class);		
	}	
	
	public PedibusItineraryLeg getPedibusItineraryLeg(String ownerId, String legId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("legId").is(legId));
		return mongoTemplate.findOne(query, PedibusItineraryLeg.class);		
	}		
	
	public List<PedibusItineraryLeg> getPedibusItineraryLegs(String ownerId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId)).with(new Sort(Sort.Direction.ASC, "position"));
		return mongoTemplate.find(query, PedibusItineraryLeg.class);
	}		
	
	public List<PedibusItineraryLeg> getPedibusItineraryLegsByGameId(String ownerId, String gameId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId))
		.with(new Sort(Sort.Direction.ASC, "position"));
		return mongoTemplate.find(query, PedibusItineraryLeg.class);		
	}		
	
	public List<PedibusPlayer> getPedibusPlayers(String ownerId, String gameId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId));
		return mongoTemplate.find(query, PedibusPlayer.class);		
	}
	
	public List<PedibusPlayer> getPedibusPlayersByClassRoom(String ownerId, String gameId, String classRoom) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId).and("classRoom").is(classRoom))
		.with(new Sort(Sort.Direction.ASC, "surname", "name"));
		return mongoTemplate.find(query, PedibusPlayer.class);		
	}	

	
//	public PedibusPlayer getPedibusPlayerByWsnId(String ownerId, String gameId, int wsnId) {
//		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId).and("wsnId").is(wsnId));
//		return mongoTemplate.findOne(query, PedibusPlayer.class);		
//	}
	
	public PedibusPlayer getPedibusPlayerByChildId(String ownerId, String gameId, String id) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId).and("childId").is(id));
		return mongoTemplate.findOne(query, PedibusPlayer.class);		
	}		
	
	public List<PedibusTeam> getPedibusTeams(String ownerId, String gameId) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId));
		return mongoTemplate.find(query, PedibusTeam.class);		
	}
	
	public CalendarDay getCalendarDay(String ownerId, String gameId, String classRoom,
			Date day) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom).and("day").is(day));
		CalendarDay calendarDayDB = mongoTemplate.findOne(query, CalendarDay.class);
		return calendarDayDB;
	}

	public CalendarDay getCalendarDay(String ownerId, String gameId, String classRoom,
									  Integer index) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom).and("index").is(index));
		CalendarDay calendarDayDB = mongoTemplate.findOne(query, CalendarDay.class);
		return calendarDayDB;
	}
	
	public List<Excursion> getExcursions(String ownerId, String gameId, String classRoom,
			Date from, Date to) {
		Criteria criteria = new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom);
		Criteria timeCriteria = new Criteria().andOperator(
				Criteria.where("day").gte(from),
				Criteria.where("day").lte(to));
		criteria = criteria.andOperator(timeCriteria);
		Query query = new Query(criteria);
		query.with(new Sort(Sort.Direction.DESC, "day"));
		List<Excursion> result = mongoTemplate.find(query, Excursion.class);
		return result;
	}



	
	public List<CalendarDay> getCalendarDays(String ownerId, String gameId, String classRoom,
			Integer from, Integer to) {
		Criteria criteria = new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom);
		Criteria timeCriteria = new Criteria().andOperator(
				Criteria.where("index").gte(from),
				Criteria.where("index").lte(to));
		criteria = criteria.andOperator(timeCriteria);
		Query query = new Query(criteria);
		query.with(new Sort(Sort.Direction.ASC, "index"));
		List<CalendarDay> result = mongoTemplate.find(query, CalendarDay.class);
		result.add(getCalendarDay(ownerId, gameId, classRoom, Integer.MIN_VALUE));
		return result;
	}



	public List<CalendarDay> getWeatherDays(String ownerId, String gameId, String classRoom,
											 String weather) {
		Criteria criteria = new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom).and("meteo").is(weather);
		Query query = new Query(criteria);
		query.with(new Sort(Sort.Direction.ASC, "index"));
		List<CalendarDay> result = mongoTemplate.find(query, CalendarDay.class);
		result.add(getCalendarDay(ownerId, gameId, classRoom, Integer.MIN_VALUE));
		return result;
	}
	
	public Map<String, Boolean> saveCalendarDay(String ownerId, String gameId, String classRoom, Integer index,
			CalendarDay calendarDay) {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		if(index == null){
			index = getIndex("123");
		}
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom).and("index").is(calendarDay.getIndex()));
		CalendarDay calendarDayDB = mongoTemplate.findOne(query, CalendarDay.class);
		Date now = new Date();
		Boolean merged = Boolean.FALSE; 
		Boolean closed = Boolean.FALSE;
		if(calendarDayDB == null) {
			Iterator it = calendarDay.getModeMap().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				if(pair.getValue().equals("zeroImpact_solo")){
					int EActive = calendarDay.getEActive()+1;
					calendarDay.setEActive(EActive);
				}
				if(pair.getValue().equals("zeroImpact_wAdult")){
					int VActive = calendarDay.getVActive()+1;
					calendarDay.setVActive(VActive);
				}
				if(pair.getValue().equals("bus")){
					int FActive = calendarDay.getFActive()+1;
					calendarDay.setFActive(FActive);
				}
				if(pair.getValue().equals("pandr")){
					int IActive = calendarDay.getIActive()+1;
					calendarDay.setIActive(IActive);
				}

			}
			calendarDay.setCreationDate(now);
			calendarDay.setLastUpdate(now);
			calendarDay.setOwnerId(ownerId);
			calendarDay.setObjectId(generateObjectId());
			calendarDay.setGameId(gameId);
			calendarDay.setClassRoom(classRoom);
			calendarDay.setClosed(true);
			calendarDay.setIndex(index);
			mongoTemplate.save(calendarDay);
		} else {
			if(calendarDayDB.isClosed()) {
				closed = Boolean.TRUE;
			} else {
				//merge pedibus data with calendar data
				Map<String, String> oldModeMap = calendarDayDB.getModeMap();
				for(String childId : calendarDay.getModeMap().keySet()) {
					String mode = calendarDay.getModeMap().get(childId);
					String oldMode = oldModeMap.get(childId);
					if(oldMode == null) {
						continue;
					} else if(mode.equals(Const.MODE_PEDIBUS) && oldMode.equals(Const.MODE_PEDIBUS)) {
						continue;
					}	else if(mode.equals(Const.MODE_PIEDI_ADULTO) && oldMode.equals(Const.MODE_PEDIBUS)) {
						calendarDay.getModeMap().put(childId, Const.MODE_PEDIBUS);
					} else if(oldMode.equals(Const.MODE_PEDIBUS)){
						calendarDay.getModeMap().put(childId, Const.MODE_PEDIBUS); // does this change anything???
						merged = Boolean.TRUE;
					}
				}
				Update update = new Update();
				update.set("meteo", calendarDay.getMeteo());
				update.set("modeMap", calendarDay.getModeMap());
				update.set("closed", Boolean.TRUE);
				update.set("lastUpdate", now);
				mongoTemplate.updateFirst(query, update, CalendarDay.class);				
			}
		}
		int updateIndex = index;
		updateIndex++;
		Index indexClass = new Index();
		indexClass.index = updateIndex;
		indexClass.ownerId = "123";
		saveIndex(indexClass);
		result.put(Const.MERGED, merged);
		result.put(Const.CLOSED, closed);
		return result;
	}

	public void saveIndex(Index index){
		Query query = new Query(new Criteria("ownerId").is(index.ownerId));
		Index indexTry = mongoTemplate.findOne(query, Index.class);
		if(indexTry == null) {
			mongoTemplate.save(index);
		} else {
			Update update = new Update();
			update.set("index", index.index);
			mongoTemplate.updateFirst(query, update, Index.class);
		}
	}
	public Integer getIndex(String ownderId){
		Query query = new Query(new Criteria("ownerId").is(ownderId));
		Index index = mongoTemplate.findOne(query, Index.class);
		if(index == null){
			return 0;
		}
		return index.index;
	}
	public void savePlayerDTO(ExecutionDataDTO executionDataDTO){
			mongoTemplate.save(executionDataDTO);
	}
	public void saveTeamDTO(PlayerStateDTO playerStateDTO){
		mongoTemplate.save(playerStateDTO);
	}
	public void updateTeamDTO(PlayerStateDTO teamDTO, String gameId, String playerId){
		Query query = new Query(new Criteria("playerId").is(playerId).and("gameId").is(gameId));
		PlayerStateDTO prevDTO = mongoTemplate.findOne(query, PlayerStateDTO.class);
		if(prevDTO == null){
			saveTeamDTO(teamDTO);
		} else if(teamDTO.getState().isEmpty()) {
				Update update = new Update();
				update.set("state", prevDTO.getState());
				mongoTemplate.updateFirst(query, update, PlayerStateDTO.class);
			}
			else{
			Update update = new Update();
			update.set("state", teamDTO.getState());
			mongoTemplate.updateFirst(query, update, PlayerStateDTO.class);
		}


	}
	public PlayerStateDTO getTeamDTO(String gameId, String playerId){
			Query query = new Query(new Criteria("gameId").is(gameId).and("playerId").is(playerId));
			PlayerStateDTO teamDTO = mongoTemplate.findOne(query, PlayerStateDTO.class);
			return teamDTO;
	}
	public void updateCalendarDayFromPedibus(String ownerId, String gameId, String classRoom, 
			Date day, Map<String, String> modeMap) {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom).and("day").is(day));
		CalendarDay calendarDayDB = mongoTemplate.findOne(query, CalendarDay.class);
		Date now = new Date();
		if(calendarDayDB == null) {
			CalendarDay calendarDay = new CalendarDay();
			calendarDay.setCreationDate(now);
			calendarDay.setLastUpdate(now);
			calendarDay.setOwnerId(ownerId);
			calendarDay.setObjectId(generateObjectId());
			calendarDay.setGameId(gameId);
			calendarDay.setClassRoom(classRoom);
			calendarDay.setDay(day);
			calendarDay.setModeMap(modeMap);
			mongoTemplate.save(calendarDay);
		} else {
			if(calendarDayDB.isClosed()) {
				return;
			} else {
				calendarDayDB.getModeMap().putAll(modeMap);
				Update update = new Update();
				update.set("modeMap", calendarDayDB.getModeMap());
				update.set("lastUpdate", now);
				mongoTemplate.updateFirst(query, update, CalendarDay.class);
			}
		}
	}

	public void createBabySwipes(String ownerId, String gameId, String classRoom) {

		Date now = new Date();
		CalendarDay calendarDay = new CalendarDay();
		calendarDay.setCreationDate(now);
		calendarDay.setLastUpdate(now);

		Calendar day = Calendar.getInstance();
		day.set(Calendar.HOUR, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		calendarDay.setDay(day.getTime());

		calendarDay.setOwnerId(ownerId);
		calendarDay.setObjectId(generateObjectId());
		calendarDay.setGameId(gameId);
		calendarDay.setClassRoom(classRoom);
		Map<String, String> modeMap = new HashMap<String, String>();
		calendarDay.setModeMap(modeMap);
		calendarDay.setIndex(Integer.MIN_VALUE);
		mongoTemplate.save(calendarDay);
	}

	// TODO may not be sending fully populated modeMap if not every student swipes
	public void submitBabySwipe(String ownerId, String gameId, String classRoom, String studentId, String activityLevel){
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom).and("index").is(Integer.MIN_VALUE));
		CalendarDay calendarDayDB = mongoTemplate.findOne(query, CalendarDay.class);
		Date now = new Date();
		if(calendarDayDB == null) {
			logger.error("Baby Swipe CalendarDay should already be initialized in repository");
		} else {
			Map<String, String> modeMap = calendarDayDB.getModeMap();
			modeMap.put(studentId, activityLevel);
			Update update = new Update();
			update.set("modeMap", calendarDayDB.getModeMap());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, CalendarDay.class);
		}
	}

	public void clearBabySwipes(String ownerId, String gameId, String classRoom){
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom).and("index").is(Integer.MIN_VALUE));
		CalendarDay calendarDayDB = mongoTemplate.findOne(query, CalendarDay.class);
		if(calendarDayDB != null) {
			Map<String, String> modeMap = calendarDayDB.getModeMap();
			modeMap.clear();
			Update update = new Update();
			update.set("modeMap", calendarDayDB.getModeMap());
			update.set("lastUpdate", new Date());
			mongoTemplate.updateFirst(query, update, CalendarDay.class);
		}
	}
	
	public void saveDataSetInfo(DataSetInfo dataSetInfo) {
		Query query = new Query(new Criteria("ownerId").is(dataSetInfo.getOwnerId()));
		DataSetInfo appInfoDB = mongoTemplate.findOne(query, DataSetInfo.class);
		if (appInfoDB == null) {
			mongoTemplate.save(dataSetInfo);
		} else {
			Update update = new Update();
			update.set("password", dataSetInfo.getPassword());
			update.set("token", dataSetInfo.getToken());
			mongoTemplate.updateFirst(query, update, DataSetInfo.class);
		}
	}
	
	public void saveAppToken(String name, String token) {
		Query query = new Query(new Criteria("name").is(name));
		Token tokenDB = mongoTemplate.findOne(query, Token.class);
		if(tokenDB == null) {
			Token newToken = new Token();
			newToken.setToken(token);
			newToken.setName(name);
			newToken.getPaths().add("/api");
			mongoTemplate.save(newToken);
		} else {
			Update update = new Update();
			update.set("token", token);
			mongoTemplate.updateFirst(query, update, Token.class);
		}
	}
	
	public void saveAdminToken(String name, String token) {
		Query query = new Query(new Criteria("name").is(name));
		Token tokenDB = mongoTemplate.findOne(query, Token.class);
		if(tokenDB == null) {
			Token newToken = new Token();
			newToken.setToken(token);
			newToken.setName(name);
			newToken.getPaths().add("*");
			mongoTemplate.save(newToken);
		} else {
			Update update = new Update();
			update.set("token", token);
			mongoTemplate.updateFirst(query, update, Token.class);
		}
	}
	
	public void savePedibusGame(PedibusGame game, String ownerId, boolean canUpdate) throws StorageException {
		Query query = new Query(new Criteria("gameId").is(game.getGameId()).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB == null) {
			game.setCreationDate(now);
			game.setLastUpdate(now);
			game.setObjectId(generateObjectId());
			game.setOwnerId(ownerId);
			mongoTemplate.save(game);
		} else if (canUpdate) {
			Update update = new Update();
			update.set("schoolId", game.getSchoolId());
			update.set("schoolName", game.getSchoolName());
			update.set("classRooms", game.getClassRooms());
			update.set("gameName", game.getGameName());
			update.set("gameDescription", game.getGameDescription());
			update.set("gameOwner", game.getGameOwner());
			update.set("from", game.getFrom());
			update.set("to", game.getTo());
			update.set("lastDaySeen", game.getLastDaySeen());
			update.set("fromHour", game.getFromHour());
			update.set("toHour", game.getToHour());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		} else {
			logger.warn("Cannot update existing PedibusGame with gameId " + game.getGameId());
		}
	}	
	
	public void updatePedibusGameLastDaySeen(String ownerId, String gameId, String lastDaySeen) {
		Query query = new Query(new Criteria("gameId").is(gameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB != null) {
			Update update = new Update();
			update.set("lastDaySeen", lastDaySeen);
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}
	
	public void resetPollingFlag(String ownerId, String gameId) {
		Query query = new Query(new Criteria("gameId").is(gameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB != null) {
			Update update = new Update();
			update.set("pollingFlagMap", new HashMap<String, Boolean>());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}
	
	public void updatePollingFlag(String ownerId, String gameId, String routeId, boolean flag) {
		Query query = new Query(new Criteria("gameId").is(gameId).and("ownerId").is(ownerId));
		PedibusGame gameDB = mongoTemplate.findOne(query, PedibusGame.class);
		Date now = new Date();
		if (gameDB != null) {
			gameDB.getPollingFlagMap().put(routeId, flag);
			Update update = new Update();
			update.set("pollingFlagMap", gameDB.getPollingFlagMap());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		}
	}

	
	public void saveExcursion(String ownerId, String gameId, String classRoom, String name, Integer children,
			Double distance, Date day, String meteo) {
		Excursion excursion = new Excursion();
		Date now = new Date();
		excursion.setOwnerId(ownerId);
		excursion.setObjectId(generateObjectId());
		excursion.setCreationDate(now);
		excursion.setLastUpdate(now);
		excursion.setGameId(gameId);
		excursion.setDay(day);
		excursion.setClassRoom(classRoom);
		excursion.setChildren(children);
		excursion.setDistance(distance);
		excursion.setMeteo(meteo);
		excursion.setName(name);
		mongoTemplate.save(excursion);
	}
	
	public void savePedibusItineraryLeg(PedibusItineraryLeg leg, String ownerId, boolean canUpdate) throws StorageException {
		Query query = new Query(new Criteria("gameId").is(leg.getGameId()).and("legId").is(leg.getLegId()).and("ownerId").is(ownerId));
		PedibusItineraryLeg legDB = mongoTemplate.findOne(query, PedibusItineraryLeg.class);
		Date now = new Date();
		if (legDB == null) {
			leg.setCreationDate(now);
			leg.setLastUpdate(now);
			leg.setObjectId(generateObjectId());
			leg.setOwnerId(ownerId);
			mongoTemplate.save(leg);
		} else if (canUpdate) {
			Update update = new Update();
			update.set("waypoint", leg.isWaypoint());
			update.set("badgeId", leg.getBadgeId());
			update.set("name", leg.getName());
			update.set("description", leg.getDescription());
			update.set("position", leg.getPosition());
			update.set("geocoding", leg.getGeocoding());
			update.set("externalUrls", leg.getExternalUrls());
			update.set("imageUrl", leg.getImageUrl());
			update.set("polyline", leg.getPolyline());
			update.set("score", leg.getScore());
			update.set("transport", leg.getTransport());
			update.set("activities", leg.getActivities());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusItineraryLeg.class);
		} else {
			logger.warn("Cannot update existing PedibusItineraryLeg with gameId " + leg.getGameId() + " and legId " + leg.getLegId());
		}
	}	
	
	public boolean savePedibusPlayer(PedibusPlayer player, String ownerId, boolean canUpdate) throws StorageException {
		Query query = new Query(new Criteria("childId").is(player.getChildId()).and("ownerId").is(ownerId));
		PedibusPlayer playerDB = mongoTemplate.findOne(query, PedibusPlayer.class);
		Date now = new Date();
		if (playerDB == null) {
			player.setCreationDate(now);
			player.setLastUpdate(now);
			player.setObjectId(generateObjectId());
			player.setOwnerId(ownerId);
			mongoTemplate.save(player);
			return false;
		} else if (canUpdate) {
			Update update = new Update();
			update.set("childId", player.getChildId());
			update.set("wsnId", player.getWsnId());
			update.set("gameId", player.getGameId());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusPlayer.class);
			return true;
		} else {
			logger.warn("Cannot update existing PedibusPlayer with childId " + player.getChildId());
			return false;
		}
	}	
	
	public boolean savePedibusTeam(PedibusTeam team, String ownerId, boolean canUpdate) throws StorageException {
		Query query = new Query(new Criteria("classRoom").is(team.getClassRoom()).and("ownerId").is(ownerId));
		PedibusTeam teamDB = mongoTemplate.findOne(query, PedibusTeam.class);
		Date now = new Date();
		if (teamDB == null) {
			team.setCreationDate(now);
			team.setLastUpdate(now);
			team.setObjectId(generateObjectId());
			team.setOwnerId(ownerId);
			mongoTemplate.save(team);
			return false;
		} else if (canUpdate) {
			Update update = new Update();
			update.set("classRoom", team.getClassRoom());
			update.set("gameId", team.getGameId());
			update.set("childrenId", team.getChildrenId());
			update.set("lastUpdate", now);
			//test code Charlie
			update.set("score", team.getScore());
			mongoTemplate.updateFirst(query, update, PedibusTeam.class);
			return true;
		} else {
			logger.warn("Cannot update existing savePedibusTeam with id " + team.getClassRoom());
			return false;
		}
	}	
	
	public void saveLastEvent(WsnEvent event) throws StorageException {
		Query query = new Query(new Criteria("ownerId").is(event.getOwnerId()).and("routeId").is(event.getRouteId()));
		WsnEvent eventDB = mongoTemplate.findOne(query, WsnEvent.class);
		Date now = new Date();
		if (eventDB == null) {
			event.setCreationDate(now);
			event.setLastUpdate(now);
			mongoTemplate.save(event);
		} else {
			Update update = new Update();
			update.set("timestamp", event.getTimestamp());
			update.set("eventType", event.getEventType());
			update.set("wsnNodeId", event.getWsnNodeId());
			update.set("payload", event.getPayload());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, WsnEvent.class);
		}
	}		

	public WsnEvent getLastEvent(String ownerId, String routeId) throws StorageException {
		Query query = new Query(new Criteria("ownerId").is(ownerId).and("routeId").is(routeId)); // .with(new Sort(Sort.Direction.ASC, "timestamp"));
		WsnEvent event = mongoTemplate.findOne(query, WsnEvent.class);
		return event;
	}	
	
	public List<?> findData(Class<?> entityClass, Criteria criteria, Sort sort, String ownerId)
			throws ClassNotFoundException {
		Query query = null;
		if (criteria != null) {
			query = new Query(new Criteria("ownerId").is(ownerId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("ownerId").is(ownerId));
		}
		if (sort != null) {
			query.with(sort);
		}
		query.limit(5000);
		List<?> result = mongoTemplate.find(query, entityClass);
		return result;
	}

	public <T> T findOneData(Class<T> entityClass, Criteria criteria, String ownerId)
			throws ClassNotFoundException {
		Query query = null;
		if (criteria != null) {
			query = new Query(new Criteria("ownerId").is(ownerId).andOperator(criteria));
		} else {
			query = new Query(new Criteria("ownerId").is(ownerId));
		}
		T result = mongoTemplate.findOne(query, entityClass);
		return result;
	}

	private String generateObjectId() {
		return UUID.randomUUID().toString();
	}

	public void saveActivity(Activity activity, String ownerId, boolean canUpdate) throws StorageException {
		Query query = new Query(new Criteria("gameId").is(activity.getGameId()).and("activityId").is(activity.getActivityId()).and("ownerId").is(ownerId));
		Activity activityDB = mongoTemplate.findOne(query, Activity.class);
		Date now = new Date();
		if (activityDB == null) {
			activity.setCreationDate(now);
			activity.setLastUpdate(now);
			activity.setObjectId(generateObjectId());
			activity.setOwnerId(ownerId);
			mongoTemplate.save(activity);
		} else if (canUpdate) {
			Update update = new Update();
			update.set("active", activity.isActive());
			update.set("gradeLevel", activity.getGradeLevel());
			update.set("teks", activity.getTeks());
			update.set("materials", activity.getMaterials());
			update.set("description", activity.getDescription());
			update.set("lastUpdate", now);
			mongoTemplate.updateFirst(query, update, PedibusGame.class);
		} else {
			logger.warn("Cannot update existing Activity with gameId " + activity.getGameId() + " and legId " + activity.getActivityId());
		}
	}

	public Settings getSettings(String ownerId, String gameId, String classRoom) {
		Criteria criteria = new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom);
		Query query = new Query(criteria);
		Settings settings = mongoTemplate.findOne(query, Settings.class);

		return settings;
	}

	public boolean saveSettings(Settings settings, String ownerId, String gameId, String classRoom, boolean canUpdate) {
		Criteria criteria = new Criteria("ownerId").is(ownerId).and("gameId").is(gameId)
				.and("classRoom").is(classRoom);
		Query query = new Query(criteria);
		Settings settingsTry = mongoTemplate.findOne(query, Settings.class);

		Date now = new Date();

		if(settingsTry == null) {
			settings.setCreationDate(now);
			settings.setLastUpdate(now);
			settings.setObjectId(generateObjectId());
			settings.setOwnerId(ownerId);
			settings.setClassRoom(classRoom);
			settings.setGameId(gameId);
			mongoTemplate.save(settings);
		} else if(canUpdate){
			Update update = new Update();
			update.set("lastUpdate", now);
			update.set("gradeLevels", settings.getGradeLevels());
			update.set("subjects", settings.getSubjects());
			update.set("teks", settings.getTeks());
//			update.set("imperial", settings.getImperial());
//			update.set("rounding", settings.getRounding());
			mongoTemplate.updateFirst(query, update, Settings.class);
		}
		else {
			logger.warn("Cannot update these settings");
			return false;
		}
		return true;
	}

}
