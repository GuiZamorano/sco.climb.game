package it.smartcommunitylab.climb.domain.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Controller
public class RoleController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	private RepositoryManager storage;

	@RequestMapping(value = "/api/role/{ownerId}/owner", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addOwner(
			@PathVariable String ownerId,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<Authorization> auths = new ArrayList<Authorization>();
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.getActions().add(Const.AUTH_ACTION_DELETE);
		auth.setRole(Const.ROLE_OWNER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId("*");
		auth.setSchoolId("*");
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add("*");
		auths.add(auth);
		
		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_OWNER), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addOwner: %s - %s", ownerId, email));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/school", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addSchoolOwner(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<Authorization> auths = new ArrayList<Authorization>();
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.getActions().add(Const.AUTH_ACTION_DELETE);
		auth.setRole(Const.ROLE_SCHOOL_OWNER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(instituteId);
		auth.setSchoolId(schoolId);
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add("*");
		auths.add(auth);
		
		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_SCHOOL_OWNER, instituteId, schoolId), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addSchoolOwner: %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/volunteer", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addVolunteer(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_VOLUNTEER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(instituteId);
		auth.setSchoolId(schoolId);
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add(Const.AUTH_RES_Institute);
		auth.getResources().add(Const.AUTH_RES_School);
		auth.getResources().add(Const.AUTH_RES_Child);
		auth.getResources().add(Const.AUTH_RES_Image);
		auth.getResources().add(Const.AUTH_RES_Volunteer);
		auth.getResources().add(Const.AUTH_RES_Stop);
		auth.getResources().add(Const.AUTH_RES_Route);
		auth.getResources().add(Const.AUTH_RES_Attendance);
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_ADD);
		auth.setRole(Const.ROLE_VOLUNTEER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(instituteId);
		auth.setSchoolId(schoolId);
		auth.setRouteId("*");
		auth.setGameId("*");
		auth.getResources().add(Const.AUTH_RES_WsnEvent);
		auth.getResources().add(Const.AUTH_RES_EventLogFile);
		auths.add(auth);
		
		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_VOLUNTEER, instituteId, schoolId), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addVolunteer: %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/editor", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addGameEditor(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			@RequestParam String pedibusGameId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_GAME_EDITOR);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(instituteId);
		auth.setSchoolId(schoolId);
		auth.setRouteId("*");
		auth.setGameId(pedibusGameId);
		auth.getResources().add(Const.AUTH_RES_Institute);
		auth.getResources().add(Const.AUTH_RES_School);
		auth.getResources().add(Const.AUTH_RES_Child);
		auth.getResources().add(Const.AUTH_RES_Image);
		auth.getResources().add(Const.AUTH_RES_Volunteer);
		auth.getResources().add(Const.AUTH_RES_Stop);
		auth.getResources().add(Const.AUTH_RES_Route);
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Calendar);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Excursion);
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.setRole(Const.ROLE_GAME_EDITOR);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(instituteId);
		auth.setSchoolId(schoolId);
		auth.setRouteId("*");
		auth.setGameId(pedibusGameId);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Link);
		auths.add(auth);

		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_GAME_EDITOR, instituteId, schoolId, pedibusGameId), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addGameEditor: %s - %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId, pedibusGameId));
		}
		return auths;
	}

	@RequestMapping(value = "/api/role/{ownerId}/teacher", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addTeacher(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			@RequestParam String pedibusGameId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<Authorization> auths = new ArrayList<Authorization>();
		
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(instituteId);
		auth.setSchoolId(schoolId);
		auth.setRouteId("*");
		auth.setGameId(pedibusGameId);
		auth.getResources().add(Const.AUTH_RES_Institute);
		auth.getResources().add(Const.AUTH_RES_School);
		auth.getResources().add(Const.AUTH_RES_Child);
		auth.getResources().add(Const.AUTH_RES_Image);
		auth.getResources().add(Const.AUTH_RES_Volunteer);
		auth.getResources().add(Const.AUTH_RES_Stop);
		auth.getResources().add(Const.AUTH_RES_Route);
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auths.add(auth);
		
		auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
		auth.setRole(Const.ROLE_TEACHER);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(instituteId);
		auth.setSchoolId(schoolId);
		auth.setRouteId("*");
		auth.setGameId(pedibusGameId);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Calendar);
		auth.getResources().add(Const.AUTH_RES_PedibusGame_Excursion);
		auths.add(auth);

		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_TEACHER, instituteId, schoolId, pedibusGameId), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addTeacher: %s - %s - %s - %s - %s", ownerId, email, 
					instituteId, schoolId, pedibusGameId));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/parent", method = RequestMethod.POST)
	public @ResponseBody List<Authorization> addParent(
			@PathVariable String ownerId,
			@RequestParam String email,
			@RequestParam String instituteId,
			@RequestParam String schoolId,
			@RequestParam String pedibusGameId,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<Authorization> auths = new ArrayList<Authorization>();
		Authorization auth = new Authorization();
		auth.getActions().add(Const.AUTH_ACTION_READ);
		auth.setRole(Const.ROLE_PARENT);
		auth.setOwnerId(ownerId);
		auth.setInstituteId(instituteId);
		auth.setSchoolId(schoolId);
		auth.setRouteId("*");
		auth.setGameId(pedibusGameId);
		auth.getResources().add(Const.AUTH_RES_PedibusGame);
		auths.add(auth);

		storage.addUserRole(email, 
				Utils.getAuthKey(ownerId, Const.ROLE_PARENT, pedibusGameId), auths);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addParent: %s - %s - %s", ownerId, email, pedibusGameId));
		}
		return auths;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/auth/{authKey}", method = RequestMethod.DELETE)
	public @ResponseBody void removeAuthKey(
			@PathVariable String ownerId,
			@PathVariable String authKey,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request) && 
				!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User user = storage.getUserByEmail(email);
		if(user == null) {
			throw new EntityNotFoundException(String.format("user %s not found", email));
		}
		List<String> roles = Utils.getUserRoles(user);
		if(roles.contains(Const.ROLE_ADMIN)) {
			throw new UnauthorizedException("Unauthorized Exception: unable to delete admin role");
		}
		if(!Utils.checkOwnerId(ownerId, user, authKey)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		storage.removeUserAuthKey(email, authKey);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeAuthKey: %s - %s - %s", ownerId, email, authKey));
		}
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/users", method = RequestMethod.GET)
	public @ResponseBody List<User> getUsersByRole(
			@PathVariable String ownerId,
			@RequestParam(required=false) String role,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<User> result = storage.getUsersByOwnerIdAndRole(ownerId, role);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUsersByRole: %s - %s", ownerId, role));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/user", method = RequestMethod.GET)
	public @ResponseBody User getUserByEmail(
			@PathVariable String ownerId,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User user = storage.getUserByEmail(email);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getUserByEmail: %s - %s", ownerId, email));
		}
		if(user != null) {
			if(Utils.checkOwnerId(ownerId, user)) {
				return user;
			} else {
				throw new UnauthorizedException("Unauthorized Exception: ownerId not allowed");
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/user", method = RequestMethod.POST)
	public @ResponseBody User saveUser(
			@PathVariable String ownerId,
			@RequestBody User user,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User userDb = null;
		if(Utils.isNotEmpty(user.getEmail())) {
			userDb = storage.getUserByEmail(user.getEmail());
		} else {
			throw new EntityNotFoundException("email must be present");
		}
		if(userDb == null) {
			User newUser = new User();
			newUser.setObjectId(Utils.getUUID());
			newUser.setName(user.getName());
			newUser.setSurname(user.getSurname());
			newUser.setEmail(user.getEmail());
			newUser.setCf(user.getCf());
    	storage.addUser(newUser);
    	
  		List<Authorization> auths = new ArrayList<Authorization>();
  		Authorization auth = new Authorization();
  		auth.setRole(Const.ROLE_USER);
  		auth.setOwnerId(ownerId);
  		auths.add(auth);
  		newUser = storage.addUserRole(user.getEmail(), 
  				Utils.getAuthKey(ownerId, Const.ROLE_USER), auths);
  		if(logger.isInfoEnabled()) {
  			logger.info(String.format("saveUser[new]: %s - %s", ownerId, user.getEmail()));
  		}
    	return newUser;
    } else {
    	user.setObjectId(userDb.getObjectId());
    	storage.updateUser(user);
    	if(!Utils.checkOwnerIdAndRole(ownerId, Const.ROLE_USER, userDb)) {
    		List<Authorization> auths = new ArrayList<Authorization>();
    		Authorization auth = new Authorization();
    		auth.setRole(Const.ROLE_USER);
    		auth.setOwnerId(ownerId);
    		auths.add(auth);
    		user = storage.addUserRole(user.getEmail(), 
    				Utils.getAuthKey(ownerId, Const.ROLE_USER), auths);
    	}
  		if(logger.isInfoEnabled()) {
  			logger.info(String.format("saveUser[update]: %s - %s", ownerId, user.getEmail()));
  		}
    	return user;
    }
	}
	
	@RequestMapping(value = "/api/role/{ownerId}/user", method = RequestMethod.DELETE)
	public @ResponseBody void removeUser(
			@PathVariable String ownerId,
			@RequestParam String email,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		User user = storage.getUserByEmail(email);
		if(user == null) {
			throw new EntityNotFoundException(String.format("user %s not found", email));
		}
		List<String> userRoles = Utils.getUserRoles(user);
		List<String> userOwnerIds = Utils.getUserOwnerIds(user);
  	if(!userOwnerIds.contains(ownerId)) {
  		throw new UnauthorizedException("Unauthorized Exception: dataset not allowed");
  	}
  	if(userRoles.contains(Const.ROLE_ADMIN)) {
  		throw new UnauthorizedException("Unauthorized Exception: unable to delete admin user");
  	}
  	userRoles.remove(Const.ROLE_USER);
  	if((userOwnerIds.size() > 1) || (userRoles.size() > 1)) {
  		throw new UnauthorizedException("Unauthorized Exception: user has other roles and dataset");
  	}
		storage.removeUser(user.getObjectId());
		if(logger.isInfoEnabled()) {
			logger.info(String.format("removeUser: %s - %s", ownerId, email));
		}
	}

	@ExceptionHandler({EntityNotFoundException.class})
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
