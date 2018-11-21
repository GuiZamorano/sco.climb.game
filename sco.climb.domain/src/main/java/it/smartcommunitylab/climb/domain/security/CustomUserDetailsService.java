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

import it.smartcommunitylab.climb.domain.storage.RepositoryManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private RepositoryManager storage;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DataSetInfo dataSetInfo = storage.getDataSetInfoBySubject(username);
		if(dataSetInfo == null) {
			throw new UsernameNotFoundException(String.format("user %s not found", username));
		}
		return new DataSetDetails(dataSetInfo);
	}


	
}
