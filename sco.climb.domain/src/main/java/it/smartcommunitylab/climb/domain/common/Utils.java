package it.smartcommunitylab.climb.domain.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.User;

public class Utils {
	private static ObjectMapper fullMapper = new ObjectMapper();
	static {
		Utils.fullMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Utils.fullMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Utils.fullMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}

	public static boolean isNotEmpty(String value) {
		boolean result = false;
		if ((value != null) && (!value.isEmpty())) {
			result = true;
		}
		return result;
	}
	
	public static boolean isEmpty(String value) {
		boolean result = true;
		if ((value != null) && (!value.isEmpty())) {
			result = false;
		}
		return result;
	}

	public static String getString(Map<String, String> data, String lang, String defaultLang) {
		String result = null;
		if(data.containsKey(lang)) {
			result = data.get(lang);
		} else {
			result = data.get(defaultLang);
		}
		return result;
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static JsonNode readJsonFromString(String json) throws JsonParseException, JsonMappingException, IOException {
		return Utils.fullMapper.readValue(json, JsonNode.class);
	}
	
	public static JsonNode readJsonFromReader(Reader reader) throws JsonProcessingException, IOException {
		return Utils.fullMapper.readTree(reader);
	}
	
	public static <T> List<T> readJSONListFromInputStream(InputStream in, Class<T> cls)
			throws IOException {
		List<Object> list = Utils.fullMapper.readValue(in, new TypeReference<List<?>>() {
		});
		List<T> result = new ArrayList<T>();
		for (Object o : list) {
			result.add(Utils.fullMapper.convertValue(o, cls));
		}
		return result;
	}
	
	public static <T> T toObject(Object in, Class<T> cls) {
		return Utils.fullMapper.convertValue(in, cls);
	}

	public static <T> T toObject(JsonNode in, Class<T> cls) throws JsonProcessingException {
		return Utils.fullMapper.treeToValue(in, cls);
	}

	public static Map<String,String> handleError(Exception exception) {
		Map<String,String> errorMap = new HashMap<String,String>();
		errorMap.put(Const.ERRORTYPE, exception.getClass().toString());
		errorMap.put(Const.ERRORMSG, exception.getMessage());
		return errorMap;
	}
	
	public static String getAuthKey(String ownerId, String role) {
		return ownerId + "__" + role;
	}

	public static String getAuthKey(String ownerId, String role, String... attributes) {
		String result = ownerId + "__" + role;
		for(String attribute : attributes) {
			result = result + "__" + attribute; 
		}
		return result;
	}
	
//	public static String getOwnerIdFromAuthKey(String authKey) {
//		String[] strings = authKey.split("__");
//		if(strings.length >= 1) {
//			return strings[0];
//		}
//		return null;
//	}
	
//	public static String getRoleFromAuthKey(String authKey) {
//		String[] strings = authKey.split("__");
//		if(strings.length >= 2) {
//			return strings[1];
//		}
//		return null;
//	}
	
//	public static String getBaseFromAuthKey(String authKey) {
//		String[] strings = authKey.split("__");
//		if(strings.length >= 2) {
//			return strings[0] + "__" + strings[1];
//		}
//		return null;
//	}
	
	public static boolean checkOwnerId(String ownerId, User user) {
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(auth.getOwnerId().equals(ownerId)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean checkOwnerId(String ownerId, User user, String authKey) {
		List<Authorization> authList = user.getRoles().get(authKey);
		if((authList != null) && (authList.size() > 0)) {
			Authorization auth = authList.get(0);
			if(auth.getOwnerId().equals(ownerId)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkRole(String role, User user) {
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(auth.getRole().equals(role)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean checkOwnerIdAndRole(String ownerId, String role, User user) {
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(auth.getOwnerId().equals(ownerId)) {
					if(auth.getRole().equals(role)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static List<String> getUserRoles(User user) {
		List<String> result = new ArrayList<>();
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(!result.contains(auth.getRole())) {
					result.add(auth.getRole());
				}
			}
		}
		return result;
	}
	
	public static List<String> getUserOwnerIds(User user) {
		List<String> result = new ArrayList<>();
		for(String authKey : user.getRoles().keySet()) {
			List<Authorization> authList = user.getRoles().get(authKey);
			for(Authorization auth : authList) {
				if(!result.contains(auth.getOwnerId())) {
					result.add(auth.getOwnerId());
				}
			}
		}
		return result;
	}
	
	public static String getNormalizeLegName(String name) {
		//TODO capire come normalizzare il nome della tappa
		return name.trim().replace(" ", "");
	}
	
}
