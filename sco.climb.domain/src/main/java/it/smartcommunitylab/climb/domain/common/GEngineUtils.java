package it.smartcommunitylab.climb.domain.common;

import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.model.gamification.ChallengeConcept;
import it.smartcommunitylab.climb.domain.model.gamification.ChallengeModel;
import it.smartcommunitylab.climb.domain.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.domain.model.gamification.GameDTO;
import it.smartcommunitylab.climb.domain.model.gamification.IncrementalClassificationDTO;
import it.smartcommunitylab.climb.domain.model.gamification.Notification;
import it.smartcommunitylab.climb.domain.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.PointConcept;
import it.smartcommunitylab.climb.domain.model.gamification.RuleDTO;
import it.smartcommunitylab.climb.domain.model.gamification.RuleValidateDTO;
import it.smartcommunitylab.climb.domain.model.gamification.TeamDTO;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class GEngineUtils {
	private static final transient Logger logger = LoggerFactory.getLogger(GEngineUtils.class);
			
	@Autowired
	@Value("${gamification.url}")
	private String gamificationURL;
	
	@Autowired
	@Value("${gamification.user}")
	private String gamificationUser;

	@Autowired
	@Value("${gamification.password}")
	private String gamificationPassword;
	
	private ObjectMapper mapper = null;
	
	public GEngineUtils() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	@SuppressWarnings("rawtypes")
	public PointConcept getPointConcept(PlayerStateDTO state, String key) {
		PointConcept result = null;
		Set<?> pointConcept = (Set) state.getState().get("PointConcept");
		if(pointConcept != null) {
			Iterator<?> it = pointConcept.iterator();
			while(it.hasNext()) {
				PointConcept pc = mapper.convertValue(it.next(), PointConcept.class);
				if(pc.getName().equals(key)) {
					result = pc;
					break;
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	public List<ChallengeConcept> getChallengeConcept(PlayerStateDTO state) {
		List<ChallengeConcept> result = new ArrayList<ChallengeConcept>();
		Set<?> challengeConcept = (Set) state.getState().get("ChallengeConcept");
		if(challengeConcept != null) {
			Iterator<?> it = challengeConcept.iterator();
			while(it.hasNext()) {
				ChallengeConcept challange = mapper.convertValue(it.next(), ChallengeConcept.class);
				result.add(challange);
			}
		}
		return result;
	}

	public void executeAction(ExecutionDataDTO executionData) throws Exception {
		String address = gamificationURL + "/exec/game/" + executionData.getGameId() + "/action/" + executionData.getActionId();
		HTTPUtils.post(address, executionData, null, gamificationUser, gamificationPassword);
	}
	
	public List<Notification> getNotification(String gameId, String playerId, long timestamp) 
			throws Exception {
		String address = gamificationURL + "/notification/game/" + gameId + "/team/" 
			+ URLEncoder.encode(playerId, "UTF-8") + "?fromTs=" + timestamp + "&size=1000000";
		String json = HTTPUtils.get(address, null, gamificationUser, gamificationPassword);
		Notification[] notifications = mapper.readValue(json, Notification[].class);
		List<Notification> result = Arrays.asList(notifications);
		return result;
	}
	
	public void createPlayer(String gameId, PlayerStateDTO player) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createPlayer[%s]: %s", gameId, player.getPlayerId()));
		}	
		String address = gamificationURL + "/data/game/" + gameId + "/player/" + URLEncoder.encode(player.getPlayerId(), "UTF-8");
		HTTPUtils.post(address, player, null, gamificationUser, gamificationPassword);
	}
	
	public void createTeam(String gameId, TeamDTO team) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createTeam[%s]: %s %s", gameId, team.getPlayerId(), team.getMembers().size()));
		}	
		String address = gamificationURL + "/data/game/" + gameId + "/team/" + URLEncoder.encode(team.getPlayerId(), "UTF-8");
		HTTPUtils.post(address, team, null, gamificationUser, gamificationPassword);
	}
	
	public void addTeamMember(String gameId, String teamId, String playerId) throws Exception {
		String address = gamificationURL + "/data/game/" + gameId + "/team/" + URLEncoder.encode(teamId, "UTF-8")
				+ "/members/" + URLEncoder.encode(playerId, "UTF-8");
		List<String> list = new ArrayList<>();
		HTTPUtils.put(address, list, null, gamificationUser, gamificationPassword);
	}
	
	public void deleteTeamMember(String gameId, String teamId, String playerId) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("deleteTeamMember[%s]: %s %s", gameId, teamId, playerId));
		}	
		String address = gamificationURL + "/data/game/" + gameId + "/team/" + URLEncoder.encode(teamId, "UTF-8")
				+ "/members/" + URLEncoder.encode(playerId, "UTF-8");
		List<String> list = new ArrayList<>();
		String content = mapper.writeValueAsString(list);
		HTTPUtils.delete(address, content, null, gamificationUser, gamificationPassword);
	}
	
	public PlayerStateDTO getPlayerStatus(String gameId, String playerId) throws Exception {
		String address = gamificationURL + "/data/game/" + gameId + "/player/" + URLEncoder.encode(playerId, "UTF-8");
		String json = HTTPUtils.get(address, null, gamificationUser, gamificationPassword);
		PlayerStateDTO result = mapper.readValue(json, PlayerStateDTO.class);
		return result;
	}
	
	public void deletePlayerState(String gameId, String playerId) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("deletePlayerState[%s]: %s", gameId, playerId));
		}	
		String address = gamificationURL + "/data/game/" + gameId + "/player/" + URLEncoder.encode(playerId, "UTF-8");
		HTTPUtils.delete(address, null, null, gamificationUser, gamificationPassword);
	}
	
	public String createGame(GameDTO game) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createGame[%s]", game.getId()));
		}	
		String address = gamificationURL + "/model/game";
		String json = HTTPUtils.post(address, game, null, gamificationUser, gamificationPassword);
		GameDTO result = mapper.readValue(json, GameDTO.class);
		return result.getId();
	}
	
	public void deleteGame(String gameId) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("deleteGame[%s]", gameId));
		}	
		String address = gamificationURL + "/model/game/" + gameId;
		HTTPUtils.delete(address, null, null, gamificationUser, gamificationPassword);
	}
	
	public void createChallenge(String gameId, ChallengeModel challengeModel) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createChallenge[%s]: %s", gameId, challengeModel.getName()));
		}	
		String address = gamificationURL + "/model/game/" + gameId + "/challenge";
		HTTPUtils.post(address, challengeModel, null, gamificationUser, gamificationPassword);
	}
	
	public void deleteChallenges(String gameId) throws Exception {
		String address = gamificationURL + "/model/game/" + gameId + "/challenge";
		String json = HTTPUtils.get(address, null, gamificationUser, gamificationPassword);
		TypeReference<ArrayList<ChallengeModel>> typeRef = new TypeReference<ArrayList<ChallengeModel>>() {};
		ArrayList<ChallengeModel> challengeList = mapper.readValue(json, typeRef);
		for(ChallengeModel challenge : challengeList) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("deleteChallenges[%s]: %s", gameId, challenge.getName()));
			}	
			HTTPUtils.delete(address + "/" + challenge.getId(), null, null, gamificationUser, gamificationPassword); 
		}
	}
	
	public void createRule(String gameId, RuleDTO rule) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createRule[%s]: %s", gameId, rule.getName()));
		}	
		String address = gamificationURL + "/model/game/" + gameId + "/rule";
		HTTPUtils.post(address, rule, null, gamificationUser, gamificationPassword);
	}
	
	public void validateRule(String gameId, RuleValidateDTO rule) throws Exception {
		//String address = gamificationURL + "/model/game/" + gameId + "/rule/validate";
		String address = gamificationURL + "/console/rule/validate";
		String json = HTTPUtils.post(address, rule.getRule(), null, gamificationUser, gamificationPassword);
		TypeReference<ArrayList<String>> typeRef = new TypeReference<ArrayList<String>>() {};
		ArrayList<String> value = mapper.readValue(json, typeRef);
		if(value.size() > 0) {
			throw new StorageException(value.toString());
		}
	}
	
	public void deleteRules(String gameId) throws Exception {
		String address = gamificationURL + "/model/game/" + gameId + "/rule";
		String json = HTTPUtils.get(address, null, gamificationUser, gamificationPassword);
		TypeReference<ArrayList<RuleDTO>> typeRef = new TypeReference<ArrayList<RuleDTO>>() {};
		ArrayList<RuleDTO> ruleList = mapper.readValue(json, typeRef);
		for(RuleDTO rule : ruleList) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("deleteRules[%s]: %s", gameId, rule.getName()));
			}	
			HTTPUtils.delete(address + "/" + rule.getId(), null, null, gamificationUser, gamificationPassword); 
		}
	}
	
	public void createPointConcept(String gameId, PointConcept pointConcept) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createPointConcept[%s]: %s", gameId, pointConcept.getName()));
		}	
		String address = gamificationURL + "/model/game/" + gameId + "/point";
		HTTPUtils.post(address, pointConcept, null, gamificationUser, gamificationPassword);
	}
	
	public void createTask(String gameId, IncrementalClassificationDTO classification) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(String.format("createTask[%s]: %s", gameId, classification.getName()));
		}	
		String address = gamificationURL + "/model/game/" + gameId + "/incclassification";
		HTTPUtils.post(address, classification, null, gamificationUser, gamificationPassword);
	}
	
}
