/**
 *    Copyright 2015 Fondazione Bruno Kessler - Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package it.smartcommunitylab.climb.domain.controller;

import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
public class VolunteerController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(VolunteerController.class);
	
	@Autowired
	private RepositoryManager storage;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/volunteer/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<Volunteer> searchVolunteer(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, 
				null,	null, Const.AUTH_RES_Volunteer, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		Criteria criteria = Criteria.where("instituteId").is(instituteId).and("schoolId").is(schoolId);
		List<Volunteer> result = (List<Volunteer>) storage.findData(Volunteer.class, criteria, null, ownerId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchVolunteer[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/volunteer/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Volunteer addVolunteer(
			@PathVariable String ownerId, 
			@RequestBody Volunteer volunteer, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(volunteer == null) {
			throw new EntityNotFoundException("volunteer not found");
		}
		if(!validateAuthorization(ownerId, volunteer.getInstituteId(), volunteer.getSchoolId(), 
				null,	null, Const.AUTH_RES_Volunteer, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		volunteer.setOwnerId(ownerId);
		volunteer.setObjectId(Utils.getUUID());
		storage.addVolunteer(volunteer);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addVolunteer[%s]:%s", ownerId, volunteer.getName()));
		}
		return volunteer;
	}

	@RequestMapping(value = "/api/volunteer/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Volunteer updateVolunteer(
			@RequestBody Volunteer volunteer, 
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(volunteer == null) {
			throw new EntityNotFoundException("volunteer not found");
		}
		if(!validateAuthorization(ownerId, volunteer.getInstituteId(), volunteer.getSchoolId(), 
				null,	null, Const.AUTH_RES_Volunteer, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		volunteer.setOwnerId(ownerId);
		volunteer.setObjectId(objectId);
		storage.updateVolunteer(volunteer);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateVolunteer[%s]:%s", ownerId, volunteer.getName()));
		}
		return volunteer;
	}
	
	@RequestMapping(value = "/api/volunteer/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteVolunteer(
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId);
		Volunteer volunteer = storage.findOneData(Volunteer.class, criteria, ownerId);
		if(volunteer == null) {
			throw new EntityNotFoundException("volunteer not found");
		}
		if(!validateAuthorization(ownerId, volunteer.getInstituteId(), volunteer.getSchoolId(), 
				null,	null, Const.AUTH_RES_Volunteer, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeVolunteer(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteVolunteer[%s]:%s", ownerId, objectId));
		}
		return "{\"status\":\"OK\"}";
	}
	
	@ExceptionHandler({EntityNotFoundException.class, StorageException.class})
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
