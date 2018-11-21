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

import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.util.ArrayList;
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
public class SchoolController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(SchoolController.class);
			
	@Autowired
	private RepositoryManager storage;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/school/{ownerId}/{instituteId}", method = RequestMethod.GET)
	public @ResponseBody List<School> searchSchool(
			@PathVariable String ownerId, 
			@PathVariable String instituteId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		User user = getUserByEmail(request);
		List<School> result = new ArrayList<>();
		Criteria criteria = Criteria.where("instituteId").is(instituteId);
		List<School> list = (List<School>) storage.findData(School.class, criteria, null, ownerId);
		for(School school : list) {
			if(validateAuthorization(ownerId, instituteId, school.getObjectId(), null, null,
				Const.AUTH_RES_School, Const.AUTH_ACTION_READ, user)) {
				result.add(school);
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchSchool[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/school/{ownerId}/{instituteId}", method = RequestMethod.POST)
	public @ResponseBody School addSchool(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@RequestBody School school, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, null, null, null,
				Const.AUTH_RES_School, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}		
		school.setOwnerId(ownerId);
		school.setInstituteId(instituteId);
		school.setObjectId(Utils.getUUID());
		storage.addSchool(school);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addSchool[%s]:%s", ownerId, school.getName()));
		}
		return school;
	}

	@RequestMapping(value = "/api/school/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody School updateSchool(
			@PathVariable String ownerId,
			@PathVariable String objectId,
			@RequestBody School school,  
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(school == null) {
			throw new EntityNotFoundException("route not found");
		}
		if(!validateAuthorization(ownerId, school.getInstituteId(), school.getObjectId(), 
				null, null, Const.AUTH_RES_School, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		school.setOwnerId(ownerId);
		school.setObjectId(objectId);
		storage.updateSchool(school);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateSchool[%s]:%s", ownerId, school.getName()));
		}
		return school;
	}
	
	@RequestMapping(value = "/api/school/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteSchool(
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("ownerId").is(ownerId).and("objectId").is(objectId);
		School school = storage.findOneData(School.class, criteria, ownerId);
		if(school == null) {
			throw new EntityNotFoundException("route not found");
		}
		if(!validateAuthorization(ownerId, school.getInstituteId(), school.getObjectId(), 
				null, null, Const.AUTH_RES_School, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeSchool(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteSchool[%s]:%s", ownerId, objectId));
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
