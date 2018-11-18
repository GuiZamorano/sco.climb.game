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

package it.smartcommunitylab.climb.gamification.dashboard.controller;
import it.smartcommunitylab.climb.gamification.dashboard.common.Utils;
import it.smartcommunitylab.climb.gamification.dashboard.exception.EntityNotFoundException;
import it.smartcommunitylab.climb.gamification.dashboard.exception.UnauthorizedException;
import it.smartcommunitylab.climb.gamification.dashboard.security.DataSetInfo;
import it.smartcommunitylab.climb.gamification.dashboard.security.Token;
import it.smartcommunitylab.climb.gamification.dashboard.storage.RepositoryManager;

import java.security.Principal;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ConsoleController {

	@Autowired
	@Value("${climbdashboard.map.center}")
	private String mapCenter;
	
	@Autowired
	@Value("${climbdashboard.map.zoom}")
	private String mapZoom;
	
	@Autowired
	@Value("${dashboardws.url}")
	private String dashboardWsUrl;
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private RepositoryManager storage;
	
	private static final transient Logger logger = LoggerFactory.getLogger(ConsoleController.class);
		
	@RequestMapping(value = "/upload")
	public String upload() {
		return "upload";
	}

	@RequestMapping(value = "/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping(value = "/logout")
	public String logout() {
		return "logout";
	}
	
	@RequestMapping(value = "/console")
	public String console() {
		return "console";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/pedibus-game")
	public String pedibusGame(Model model, Principal principal) {
		String name = principal.getName();
		DataSetInfo dataSetInfo = null;
		try {
			dataSetInfo = storage.findOneData(DataSetInfo.class, null, name);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
//		if(logger.isDebugEnabled()) {
//			logger.debug("Principal Data " + dataSetInfo.getOwnerId());
//		}
		Token token = storage.findTokenByToken(dataSetInfo.getToken());
		model.addAttribute("token", token.getToken());
		model.addAttribute("map_center", mapCenter);
		model.addAttribute("map_zoom", mapZoom);
		//model.addAttribute("api", (token.getPaths() != null && token.getPaths().size() > 0) ? token.getPaths().get(0) : "");
		model.addAttribute("api", dashboardWsUrl);
		model.addAttribute("gname", name);
		return "pedibus-game";
	}
	
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public @ResponseBody Token getToken(@RequestParam String username, @RequestParam String password) throws Exception {
		DataSetInfo dataSetInfo = storage.findOneData(DataSetInfo.class, null, username);
		if(dataSetInfo == null) {
			logger.warn("getToken: owner not found " + username);
			throw new EntityNotFoundException("owner not found");
		}
		if (!dataSetInfo.getPassword().equals(password)) {
			logger.warn("getToken:username or password not valid " + username);
			throw new UnauthorizedException("username or password not valid");
		}
		Token token = storage.findTokenByToken(dataSetInfo.getToken());
		if(logger.isInfoEnabled()) {
			logger.info("getToken:" + username);
		}
		return token;
	}

	@ExceptionHandler(EntityNotFoundException.class)
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
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
	
}
