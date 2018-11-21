package it.smartcommunitylab.climb.domain.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import it.smartcommunitylab.climb.contextstore.model.Authorization;
import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.Stop;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.converter.ExcelConverter;
import it.smartcommunitylab.climb.domain.converter.ExcelError;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

@Controller
public class AdminController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private RepositoryManager storage;

	@RequestMapping(value = "/admin/user/csv", method = RequestMethod.POST)
	public @ResponseBody void uploadOwnerUserCsv(
			@RequestParam("file") MultipartFile file,
			@RequestParam(name="update", required=false) Boolean update,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_ADMIN, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		if(update == null) {
			update = Boolean.FALSE;
		}
		if (!file.isEmpty()) {
			Path tempFile = Files.createTempFile("climb-user", ".csv");
			tempFile.toFile().deleteOnExit();
			File outputFileCSV = tempFile.toFile();
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(outputFileCSV));
			FileCopyUtils.copy(file.getInputStream(), stream);
			stream.close();
			
			Reader in = new FileReader(outputFileCSV);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
			for (CSVRecord record : records) {
		    String ownerId = record.get("ownerId");
		    String subject = record.get("subject");
		    String name = record.get("name");
		    String surname = record.get("surname");
		    String email = record.get("email");
		    String cf = record.get("cf");
	    	
		    User user = new User();
	    	user.setCf(cf);
	    	user.setEmail(email);
	    	user.setName(name);
	    	user.setSurname(surname);
	    	user.setSubject(subject);
	    	
		    User userDb = storage.getUserByCf(cf);
		    if(userDb == null) {
		    	user.setObjectId(Utils.getUUID());
		    	storage.addUser(user);
		    } else if(update) {
		    	user.setObjectId(userDb.getObjectId());
		    	storage.updateUser(user);
		    }
		    
	  		List<Authorization> auths = new ArrayList<Authorization>();
	  		Authorization auth = new Authorization();
	  		auth.getActions().add(Const.AUTH_ACTION_READ);
	  		auth.getActions().add(Const.AUTH_ACTION_ADD);
	  		auth.getActions().add(Const.AUTH_ACTION_UPDATE);
	  		auth.getActions().add(Const.AUTH_ACTION_DELETE);
	  		auth.setOwnerId(ownerId);
	  		auth.setInstituteId("*");
	  		auth.setSchoolId("*");
	  		auth.setRouteId("*");
	  		auth.setGameId("*");
	  		auth.getResources().add("*");
	  		auths.add(auth);
	  		
	  		storage.addUserRole(email, 
	  				Utils.getAuthKey(ownerId, Const.ROLE_OWNER), auths);
			}
		}
	}
	
	@RequestMapping(value = "/admin/import/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.POST)
	public @ResponseBody List<ExcelError> uploadData(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@RequestParam(name="onlychilds", required=false) Boolean onlyChild,
			@RequestParam("file") MultipartFile file,
			HttpServletRequest request) throws Exception {
		if(!validateRole(Const.ROLE_OWNER, ownerId, request)) {
			throw new UnauthorizedException("Unauthorized Exception: role not valid");
		}
		List<ExcelError> errors = new ArrayList<ExcelError>();
		if(onlyChild) {
			Map<String, Stop> stopsMap = new HashMap<String, Stop>();
			Map<String, Child> childrenMap = ExcelConverter.readChildren(file.getInputStream(), 
					ownerId, instituteId, schoolId, stopsMap, errors);
			if(errors.size() == 0) {
				for(Child child : childrenMap.values()) {
					storeChild(child);
				}
				if(logger.isInfoEnabled()) {
					logger.info(String.format("uploadData: %s %s %s %s", ownerId, instituteId, schoolId, onlyChild));
				}
			}
		} else {
			Map<String, Route> routesMap = ExcelConverter.readRoutes(file.getInputStream(), 
					ownerId, instituteId, schoolId, errors);
			Map<String, Stop> stopsMap = ExcelConverter.readStops(file.getInputStream(), 
					ownerId, instituteId, schoolId, routesMap, errors);
			Map<String, Child> childrenMap = ExcelConverter.readChildren(file.getInputStream(), 
					ownerId, instituteId, schoolId, stopsMap, errors);
			Map<String, Volunteer> volunteersMap = ExcelConverter.readVolunteers(file.getInputStream(), 
					ownerId, instituteId, schoolId, errors);
			if(errors.size() == 0) {
				for(Route route : routesMap.values()) {
					storage.addRoute(route);
				}
				for(Stop stop : stopsMap.values()) {
					storage.addStop(stop);
				}
				for(Child child : childrenMap.values()) {
					storeChild(child);
				}
				for(Volunteer volunteer : volunteersMap.values()) {
					storage.addVolunteer(volunteer);
				}			
				if(logger.isInfoEnabled()) {
					logger.info(String.format("uploadData: %s %s %s %s", ownerId, instituteId, schoolId, onlyChild));
				}
			}
		}
		return errors;
	}
	
	private void storeChild(Child child) throws ClassNotFoundException {
		Criteria criteriaBase = Criteria.where("schoolId").is(child.getSchoolId())
				.and("instituteId").is(child.getInstituteId());
		Child childDb;
		if(Utils.isNotEmpty(child.getCf())) {
			Criteria criteriaCf = criteriaBase.and("cf").is(child.getCf());
			childDb = storage.findOneData(Child.class, criteriaCf, child.getOwnerId());
			if(childDb != null) {
				if(logger.isInfoEnabled()) {
					logger.info(String.format("Child already exists: %s %s", 
							child.getName(), child.getSurname()));
				}
				return;
			}
		}
		Criteria criteriaName = criteriaBase.and("name").is(child.getName())
				.and("surname").is(child.getSurname());
		childDb = storage.findOneData(Child.class, criteriaName, child.getOwnerId());
		if(childDb != null) {
			if(logger.isInfoEnabled()) {
				logger.info(String.format("Child already exists: %s %s", 
						child.getName(), child.getSurname()));
			}
			return;
		}
		storage.addChild(child);
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
