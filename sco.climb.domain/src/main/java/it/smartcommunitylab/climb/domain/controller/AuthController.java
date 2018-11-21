package it.smartcommunitylab.climb.domain.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import it.smartcommunitylab.aac.AACException;
import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.aac.authorization.beans.AccountAttributeDTO;
import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.aac.model.TokenData;
import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

public class AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	@Value("${oauth.serverUrl}")	
	private String oauthServerUrl;
	
	@Autowired
	@Value("${security.oauth2.client.clientId}")	
	private String clientId;

	@Autowired
	@Value("${security.oauth2.client.clientSecret}")	
	private String clientSecret;
	
	@Autowired
	@Value("${profile.serverUrl}")
	private String profileServerUrl;

	private AACService aacService;
	
	private AACProfileService profileConnector;
	
	@Autowired
	private RepositoryManager storage;

	@PostConstruct
	public void init() throws Exception {
		aacService = new AACService(oauthServerUrl, clientId, clientSecret);
		profileConnector = new AACProfileService(profileServerUrl);
	}
	
	protected TokenData refreshToken(String refreshToken) throws SecurityException, AACException {
		return aacService.refreshToken(refreshToken);
	}
	
	protected AccountAttributeDTO getAccountByEmail(AccountProfile accountProfile) {
		String email = null;
		if(accountProfile == null) {
			return null;
		}
		if(Utils.isNotEmpty(
				accountProfile.getAttribute("adc", "pat_attribute_email"))) {
			email = accountProfile.getAttribute("adc", "pat_attribute_email");
		} else if(Utils.isNotEmpty(
				accountProfile.getAttribute("google", "email"))) {
			email = accountProfile.getAttribute("google", "email");
		} else if(Utils.isNotEmpty(
				accountProfile.getAttribute("facebook", "email"))) {
			email = accountProfile.getAttribute("facebook", "email");
		} else if(Utils.isNotEmpty(
				accountProfile.getAttribute("internal", "email"))) {
			email = accountProfile.getAttribute("internal", "email");
		}
		AccountAttributeDTO account = new AccountAttributeDTO();
		account.setAccountName(Const.AUTH_ACCOUNT_NAME);
		account.setAttributeName(Const.AUTH_ATTRIBUTE_NAME);
		account.setAttributeValue(email);
		//TODO TEST
		//account.setAttributeValue("gino.rivieccio@gmail.com");
		return account;
	}

	protected String getSubject(AccountProfile accountProfile) {
		String result = null;
		if(accountProfile != null) {
			result = accountProfile.getUserId();
		}
		return result;
	}
	
	protected AccountProfile getAccoutProfile(HttpServletRequest request) {
		AccountProfile result = null;
		String token = request.getHeader("Authorization");
		if (Utils.isNotEmpty(token)) {
			token = token.replace("Bearer ", "");
			try {
				result = profileConnector.findAccountProfile(token);
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(String.format("getAccoutProfile[%s]: %s", token, e.getMessage()));
				}
			} 
		}
		return result;
	}
	
	public User getUserByEmail(HttpServletRequest request) throws Exception {
		AccountAttributeDTO account = getAccountByEmail(getAccoutProfile(request));
		if(account == null) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		User user = storage.getUserByEmail(account.getAttributeValue());
		if(user == null) {
			throw new UnauthorizedException("Unauthorized Exception: user email not found");
		}
		return user;
	}
	
	public boolean validateAuthorization(String ownerId, String instituteId, 
			String schoolId, String routeId, String gameId, String resource, String action,	
			HttpServletRequest request) throws Exception {
		AccountAttributeDTO account = getAccountByEmail(getAccoutProfile(request));
		if(account == null) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		User user = storage.getUserByEmail(account.getAttributeValue());
		if(!validateAuthorization(ownerId, instituteId, schoolId, routeId, gameId,
				resource, action, user)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		return true;
	}
	
	public boolean validateAuthorization(String ownerId, String instituteId, 
			String schoolId, String routeId, String gameId, String resource, String action,	
			User user) throws Exception {
		return validateAuthorization(ownerId, instituteId, schoolId, routeId, gameId,
				resource, action, user, true);
	}
	
	private boolean validateAuthorization(String ownerId, String instituteId, String schoolId, String routeId, 
			String gameId, String resource, String action, User user, boolean nullable) {
		if(user != null) {
			for(String authKey : user.getRoles().keySet()) {
				List<Authorization> authList = user.getRoles().get(authKey);
				for(Authorization auth : authList) {
					if(auth.getOwnerId().equals(ownerId)) {
						if(auth.getResources().contains("*") || auth.getResources().contains(resource)) {
							if(auth.getActions().contains(action)) {
								if(!Utils.isEmpty(instituteId) || !nullable) {
									if(Utils.isEmpty(instituteId) && !nullable) {
										instituteId = "*";
									}
									if(!auth.getInstituteId().equals(instituteId) && !auth.getInstituteId().equals("*")) {
										continue;
									}
								}
								if(!Utils.isEmpty(schoolId) || !nullable) {
									if(Utils.isEmpty(schoolId) && !nullable) {
										schoolId = "*";
									}
									if(!auth.getSchoolId().equals(schoolId) && !auth.getSchoolId().equals("*")) {
										continue;
									}
								}
								if(!Utils.isEmpty(routeId) || !nullable) {
									if(Utils.isEmpty(routeId) && !nullable) {
										routeId = "*";
									}
									if(!auth.getRouteId().equals(routeId) && !auth.getRouteId().equals("*")) {
										continue;
									}
								}
								if(!Utils.isEmpty(gameId) || !nullable) {
									if(Utils.isEmpty(gameId) && !nullable) {
										gameId = "*";
									}
									if(!auth.getGameId().equals(gameId) && !auth.getGameId().equals("*")) {
										continue;
									}
								}
								return true;
							}
						}						
					}
				}
			}
		}
		return false;
	}
	
	public boolean validateRole(String role, String ownerId, HttpServletRequest request) throws Exception {
		AccountAttributeDTO accountByEmail = getAccountByEmail(getAccoutProfile(request));
		if(accountByEmail == null) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		String email = accountByEmail.getAttributeValue();
		User user = storage.getUserByEmail(email);
		if(user != null) {
			for(String authKey : user.getRoles().keySet()) {
				List<Authorization> authList = user.getRoles().get(authKey);
				for(Authorization auth : authList) {
					if(auth.getRole().equals(role) && auth.getOwnerId().equals(ownerId)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean validateRole(String role, HttpServletRequest request) throws Exception {
		AccountAttributeDTO accountByEmail = getAccountByEmail(getAccoutProfile(request));
		if(accountByEmail == null) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid or call not authorized");
		}
		String email = accountByEmail.getAttributeValue();
		User user = storage.getUserByEmail(email);
		if(user != null) {
			for(String authKey : user.getRoles().keySet()) {
				List<Authorization> authList = user.getRoles().get(authKey);
				for(Authorization auth : authList) {
					if(auth.getRole().equals(role)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
