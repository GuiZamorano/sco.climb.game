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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import it.smartcommunitylab.aac.model.TokenData;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Const;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.security.DataSetDetails;
import it.smartcommunitylab.climb.domain.security.DataSetInfo;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;


@Controller
public class ConsoleController extends AuthController {

	@Autowired
	private RepositoryManager storage;
	
	@Autowired
	private RememberMeServices rememberMeServices; 
	
	@RequestMapping(value = "/")
	public View root() {
		return new RedirectView("login");
	}		
	
	@RequestMapping(value = "/upload")
	public String upload() {
		return "upload";
	}

	@RequestMapping(value = "/login")
	public String login() {
		return "login";
	}		
	
	@RequestMapping(value = "/console")
	public String console() {
		return "console";
	}
	
	@RequestMapping(value = "/console/data")
	public @ResponseBody DataSetInfo data(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return getDataSetInfo(request, response);
	}
	
	private DataSetInfo getDataSetInfo(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		DataSetDetails details = (DataSetDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		//check token expiration
		long now = System.currentTimeMillis();
		if(now > details.getApp().getExpiration()) {
			TokenData tokenData = refreshToken(details.getApp().getRefreshToken());
			details.getApp().setToken(tokenData.getAccess_token());
			details.getApp().setRefreshToken(tokenData.getRefresh_token());
			details.getApp().setExpiration(tokenData.getExpires_on());
		}
		//save info
		storage.saveDataSetInfo(details.getApp());
		//create response
		DataSetInfo dsInfo = new DataSetInfo();
		dsInfo.setCf(details.getApp().getCf());
		dsInfo.setEmail(details.getApp().getEmail());
		dsInfo.setName(details.getApp().getName());
		dsInfo.setSurname(details.getApp().getSurname());
		dsInfo.setSubject(details.getApp().getSubject());
		dsInfo.setToken(details.getApp().getToken());
		dsInfo.setExpiration(details.getApp().getExpiration());
		//TODO TEST
		//dsInfo.setEmail("smartcommunitytester@gmail.com");
		User user = storage.getUserByEmail(details.getApp().getEmail());
		if(user != null) {
			dsInfo.setOwnerIds(Utils.getUserOwnerIds(user));
			dsInfo.getOwnerIds().remove(Const.SYSTEM_DOMAIN);
			dsInfo.setRoles(Utils.getUserRoles(user));
		}
		//save rememeberme
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
		rememberMeServices.loginSuccess((HttpServletRequest)request, 
				(HttpServletResponse)response, existingAuth);
		return dsInfo;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleError(HttpServletRequest request, Exception exception) {
		return Utils.handleError(exception);
	}
	
}
