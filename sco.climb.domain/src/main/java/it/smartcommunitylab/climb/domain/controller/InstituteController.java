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

import it.smartcommunitylab.climb.contextstore.model.Institute;
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
public class InstituteController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(InstituteController.class);
			
	@Autowired
	private RepositoryManager storage;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/institute/{ownerId}", method = RequestMethod.GET)
	public @ResponseBody List<Institute> searchInstitute(
			@PathVariable String ownerId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		User user = getUserByEmail(request);
		List<Institute> result = new ArrayList<>();
		List<Institute> list = (List<Institute>) storage.findData(Institute.class, null, null, ownerId);
		for(Institute institute : list) {
			if(validateAuthorization(ownerId, institute.getObjectId(), null, null, null,
				Const.AUTH_RES_Institute, Const.AUTH_ACTION_READ, user)) {
				result.add(institute);
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchInstitute[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/institute/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Institute addInstitute(
			@RequestBody Institute institute, 
			@PathVariable String ownerId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, null, null, null, null,
				Const.AUTH_RES_Institute, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		institute.setOwnerId(ownerId);
		institute.setObjectId(Utils.getUUID());
		storage.addInstitute(institute);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addInstitute[%s]:%s", ownerId, institute.getName()));
		}
		return institute;
	}

	@RequestMapping(value = "/api/institute/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Institute updateInstitute(
			@RequestBody Institute institute, 
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, null, null, null, null,
				Const.AUTH_RES_Institute, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		institute.setOwnerId(ownerId);
		institute.setObjectId(objectId);
		storage.updateInstitute(institute);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateSchool[%s]:%s", ownerId, institute.getName()));
		}
		return institute;
	}
	
	@RequestMapping(value = "/api/institute/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteInstitute(
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, null, null, null, null,
				Const.AUTH_RES_Institute, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeInstitute(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteInstitute[%s]:%s", ownerId, objectId));
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
