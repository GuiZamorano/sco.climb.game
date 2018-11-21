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

package it.smartcommunitylab.climb.domain.model.gamification;

import java.util.ArrayList;
import java.util.List;

public class BadgeCollectionConcept extends GameConcept {

	private List<String> badgeEarned;

	public BadgeCollectionConcept() {
		badgeEarned = new ArrayList<String>();
	}

	public BadgeCollectionConcept(String name) {
		super(name);
		badgeEarned = new ArrayList<String>();
	}

	public List<String> getBadgeEarned() {
		return badgeEarned;
	}

	public void setBadgeEarned(List<String> badgeEarned) {
		this.badgeEarned = badgeEarned;
	}
}
