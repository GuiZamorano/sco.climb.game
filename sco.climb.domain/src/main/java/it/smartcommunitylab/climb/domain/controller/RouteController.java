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

import it.smartcommunitylab.climb.contextstore.model.Route;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class RouteController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(RouteController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private RepositoryManager storage;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/route/{ownerId}/{instituteId}/{schoolId}", method = RequestMethod.GET)
	public @ResponseBody List<Route> searchRoute(
			@PathVariable String ownerId, 
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		User user = getUserByEmail(request);
		List<Route> result = new ArrayList<>();
		Criteria criteria = Criteria.where("instituteId").is(instituteId).and("schoolId").is(schoolId);
		String dateString = request.getParameter("date");
		if(Utils.isNotEmpty(dateString)) {
			Date date = sdf.parse(dateString);
			criteria = criteria.andOperator(
					Criteria.where("from").lte(date), 
					Criteria.where("to").gte(date));
		}
		List<Route> list = (List<Route>) storage.findData(Route.class, criteria, null, ownerId);
		for(Route route : list) {
			if(validateAuthorization(ownerId, instituteId, schoolId, route.getObjectId(), null,
				Const.AUTH_RES_Route, Const.AUTH_ACTION_READ, user)) {
				result.add(route);
			}
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchRoute[%s]:%d", ownerId, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/route/{ownerId}/{objectId}", method = RequestMethod.GET)
	public @ResponseBody Route searchRouteById(
			@PathVariable String ownerId,  
			@PathVariable String objectId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId);
		Route route = storage.findOneData(Route.class, criteria, ownerId);
		if(route == null) {
			throw new EntityNotFoundException("route not found");
		}
		if(!validateAuthorization(ownerId, route.getInstituteId(), route.getSchoolId(), 
				objectId, null, Const.AUTH_RES_Route, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("searchRouteById[%s]:%s", ownerId, objectId));
		}
		return route;
	}
	
	@RequestMapping(value = "/api/route/{ownerId}", method = RequestMethod.POST)
	public @ResponseBody Route addRoute(
			@RequestBody Route route, 
			@PathVariable String ownerId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(route == null) {
			throw new EntityNotFoundException("route not found");
		}
		if(!validateAuthorization(ownerId, route.getInstituteId(), route.getSchoolId(), 
				null, null, Const.AUTH_RES_Route, Const.AUTH_ACTION_ADD, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		route.setOwnerId(ownerId);
		route.setObjectId(Utils.getUUID());
		storage.addRoute(route);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("addRoute[%s]:%s", ownerId, route.getName()));
		}
		return route;
	}

	@RequestMapping(value = "/api/route/{ownerId}/{objectId}", method = RequestMethod.PUT)
	public @ResponseBody Route updateRoute(
			@RequestBody Route route, 
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(route == null) {
			throw new EntityNotFoundException("route not found");
		}
		if(!validateAuthorization(ownerId, route.getInstituteId(), route.getSchoolId(), 
				objectId, null, Const.AUTH_RES_Route, Const.AUTH_ACTION_UPDATE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		route.setOwnerId(ownerId);
		route.setObjectId(objectId);
		storage.updateRoute(route);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("updateRoute[%s]:%s", ownerId, route.getName()));
		}
		return route;
	}
	
	@RequestMapping(value = "/api/route/{ownerId}/{objectId}", method = RequestMethod.DELETE)
	public @ResponseBody String deleteRoute(
			@PathVariable String ownerId, 
			@PathVariable String objectId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		Criteria criteria = Criteria.where("objectId").is(objectId);
		Route route = storage.findOneData(Route.class, criteria, ownerId);
		if(route == null) {
			throw new EntityNotFoundException("route not found");
		}
		if(!validateAuthorization(ownerId, route.getInstituteId(), route.getSchoolId(), 
				objectId, null, Const.AUTH_RES_Route, Const.AUTH_ACTION_DELETE, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		storage.removeRoute(ownerId, objectId);
		storage.removeStopByRouteId(ownerId, objectId);
		if(logger.isInfoEnabled()) {
			logger.info(String.format("deleteRoute[%s]:%s", ownerId, objectId));
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
