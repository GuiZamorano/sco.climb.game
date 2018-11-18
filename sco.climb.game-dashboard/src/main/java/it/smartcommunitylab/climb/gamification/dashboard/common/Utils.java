package it.smartcommunitylab.climb.gamification.dashboard.common;

import it.smartcommunitylab.climb.gamification.dashboard.security.DataSetInfo;
import it.smartcommunitylab.climb.gamification.dashboard.security.Token;
import it.smartcommunitylab.climb.gamification.dashboard.storage.DataSetSetup;
import it.smartcommunitylab.climb.gamification.dashboard.storage.RepositoryManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Utils {
	private static ObjectMapper fullMapper = new ObjectMapper();
	static {
		Utils.fullMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Utils.fullMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Utils.fullMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
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
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
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

	public static boolean validateAPIRequest(ServletRequest req, DataSetSetup dataSetSetup, 
			RepositoryManager storage) {
		boolean result = false;
		HttpServletRequest request = (HttpServletRequest) req;
		String uriPath = request.getRequestURI();
		if (uriPath != null && !uriPath.isEmpty()) {
			String tokenArrived = request.getHeader("X-ACCESS-TOKEN");
			if (tokenArrived != null && !tokenArrived.isEmpty()) {
				Token matchedToken = storage.findTokenByToken(tokenArrived);
				if (matchedToken != null) {
					if (matchedToken.getExpiration() > 0) {
						//token exired
						if(matchedToken.getExpiration() < System.currentTimeMillis()) {
							return false;
						}
					}
					String ownerId = matchedToken.getName();
					DataSetInfo dataSetInfo = dataSetSetup.findDataSetById(ownerId);
					//dataset config not found
					if(dataSetInfo == null) {
						return false;
					}
					//wrong API path
					if(!uriPath.contains(ownerId)) {
						return false;
					}
					// delegate resources to controller via request attribute map.
					if (matchedToken.getResources() != null	&& !matchedToken.getResources().isEmpty()) {
						req.setAttribute("resources",	matchedToken.getResources());
					}
					// check ( resources *)
					if (matchedToken.getPaths().contains("*")) {
						result = true;
					} else {
						for(String resourcePath : matchedToken.getPaths()) {
							if(uriPath.contains(resourcePath)) {
								result = true;
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}

	public static Map<String,String> handleError(Exception exception) {
		Map<String,String> errorMap = new HashMap<String,String>();
		errorMap.put(Const.ERRORTYPE, exception.getClass().toString());
		errorMap.put(Const.ERRORMSG, exception.getMessage());
		return errorMap;
	}
}
