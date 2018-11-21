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

import it.smartcommunitylab.climb.contextstore.model.Child;
import it.smartcommunitylab.climb.contextstore.model.Volunteer;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.converter.ExcelConverter;
import it.smartcommunitylab.climb.domain.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.domain.exception.InvalidParametersException;
import it.smartcommunitylab.climb.domain.exception.StorageException;
import it.smartcommunitylab.climb.domain.exception.UnauthorizedException;
import it.smartcommunitylab.climb.domain.model.WsnEvent;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;


@Controller
public class ReportController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(ReportController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	@Autowired
	private RepositoryManager storage;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/report/attendance/{ownerId}/{instituteId}/{schoolId}/{routeId}", method = RequestMethod.GET)
	public void writeAttendance(
			@PathVariable String ownerId,
			@PathVariable String instituteId,
			@PathVariable String schoolId,
			@PathVariable String routeId,
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		if(!validateAuthorization(ownerId, instituteId, schoolId, routeId, null, 
				Const.AUTH_RES_Attendance, Const.AUTH_ACTION_READ, request)) {
			throw new UnauthorizedException("Unauthorized Exception: token not valid");
		}
		List<WsnEvent> eventList = Lists.newArrayList();
		List<Child> childList = Lists.newArrayList();
		List<Volunteer> volunteerList = Lists.newArrayList();
		List<String> nodeIdList = Lists.newArrayList();
		String dateFromString = request.getParameter("dateFrom");
		String dateToString = request.getParameter("dateTo");
		Date dateFrom = sdf.parse(dateFromString);
		Date dateTo = sdf.parse(dateToString);
		List<Integer> eventTypeList = Lists.newArrayList();
		eventTypeList.add(Const.NODE_AT_DESTINATION);
		eventTypeList.add(Const.SET_DRIVER);
		eventTypeList.add(Const.SET_HELPER);
 		try {
 			eventList = storage.searchEvents(ownerId, routeId, dateFrom, dateTo, eventTypeList, nodeIdList);
 			Criteria criteria = Criteria.where("instituteId").is(instituteId).and("schoolId").is(schoolId);
 			childList = (List<Child>) storage.findData(Child.class, criteria, null, ownerId);
 			volunteerList = (List<Volunteer>) storage.findData(Volunteer.class, criteria, null, ownerId);
 			ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
			ExcelConverter.writeAttendance(dateFrom, dateTo, eventList, childList, volunteerList, outputBuffer);
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=\"report-" + ownerId + ".xls\"");
			response.addHeader("Content-Transfer-Encoding", "binary");
			response.addHeader("Cache-control", "no-cache");
			response.getOutputStream().write(outputBuffer.toByteArray());
			response.getOutputStream().flush();
			outputBuffer.close();
			if(logger.isInfoEnabled()) {
				logger.info(String.format("writeAttendance[%s]:%s - %s - %s", ownerId, instituteId, schoolId, routeId));
			}
		} catch (Exception e) {
			logger.error("writeAttendance:" + e.getMessage());
			throw new InvalidParametersException("Invalid query parameters:" + e.getMessage());
		}
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
