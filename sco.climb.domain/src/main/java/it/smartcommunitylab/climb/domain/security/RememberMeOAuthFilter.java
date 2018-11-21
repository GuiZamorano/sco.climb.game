/**
 * Copyright 2015 Smart Community Lab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.smartcommunitylab.climb.domain.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.filter.GenericFilterBean;

/**
 * 
 * @author nori
 *
 */
public class RememberMeOAuthFilter extends GenericFilterBean {

	private RememberMeServices rememberMeServices;
	
	public RememberMeOAuthFilter(RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, 
			final FilterChain chain) throws IOException, ServletException {
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
		if((existingAuth != null) && existingAuth.isAuthenticated()) {
			rememberMeServices.loginSuccess((HttpServletRequest)request, 
				(HttpServletResponse)response, existingAuth);
		}
		chain.doFilter(request, response);
	}

}