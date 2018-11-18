package it.smartcommunitylab.climb.gamification.dashboard.common;

import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.ExecutionDataDTO;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.Notification;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PlayerStateDTO;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PointConcept;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.TeamDTO;
import it.smartcommunitylab.climb.gamification.dashboard.utils.HTTPUtils;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GEngineUtils {
	public static String challengeConcept = "ChallengeConcept"; 
	
	@Autowired
	@Value("${gamification.url}")
	private String gamificationURL;
	
	@Autowired
	@Value("${gamification.user}")
	private String gamificationUser;

	@Autowired
	@Value("${gamification.password}")
	private String gamificationPassword;
	
	private ObjectMapper mapper = new ObjectMapper();
	
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

	public void executeAction(ExecutionDataDTO executionData) throws Exception {
		String address = gamificationURL + "/gengine/execute";
		HTTPUtils.post(address, executionData, null, gamificationUser, gamificationPassword);
	}
	
	public List<Notification> getNotification(String gameId, String playerId, long timestamp) 
			throws Exception {
		String address = gamificationURL + "/gengine/notification/" + gameId + "/" 
			+ URLEncoder.encode(playerId, "UTF-8") + "?timestamp=" + timestamp;
		String json = HTTPUtils.get(address, null, gamificationUser, gamificationPassword);
		Notification[] notifications = mapper.readValue(json, Notification[].class);
		List<Notification> result = Arrays.asList(notifications);
		return result;
	}
	
	public void createPlayer(String gameId, PlayerStateDTO player) throws Exception {
		String address = gamificationURL + "/console/game/" + gameId + "/player";
		HTTPUtils.post(address, player, null, gamificationUser, gamificationPassword);
	}
	
	public void createTeam(String gameId, TeamDTO team) throws Exception {
		String address = gamificationURL + "/console/game/" + gameId + "/team";
		HTTPUtils.post(address, team, null, gamificationUser, gamificationPassword);
	}
	
	public void createTeamMembers(String gameId, String teamId, List<String> memeberList) throws Exception {
		String address = gamificationURL + "/console/game/" + gameId + "/team/" 
				+ URLEncoder.encode(teamId, "UTF-8") + "/members";
		HTTPUtils.post(address, memeberList, null, gamificationUser, gamificationPassword);
	}
	
	public PlayerStateDTO getPlayerStatus(String gameId, String playerId) throws Exception {
		String address = gamificationURL + "/gengine/state/" + gameId + "/" + URLEncoder.encode(playerId, "UTF-8");
		String json = HTTPUtils.get(address, null, gamificationUser, gamificationPassword);
		PlayerStateDTO result = mapper.readValue(json, PlayerStateDTO.class);
		return result;
	}
	
}
