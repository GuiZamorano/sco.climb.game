package it.smartcommunitylab.climb.domain.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DataSetDetails implements UserDetails {
	private static final long serialVersionUID = 1970015369860723085L;

	private DataSetInfo dataset;
	
	public DataSetDetails() {
		super();
	}

	public DataSetDetails(DataSetInfo app) {
		super();
		this.dataset = app;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
		for(String ownerId : dataset.getOwnerIds()) {
			result.add(new SimpleGrantedAuthority(ownerId));
		}
		return result;
	}

	@Override
	public String getPassword() {
		return dataset.getSubject();
	}

	@Override
	public String getUsername() {
		return dataset.getSubject();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public DataSetInfo getApp() {
		return dataset;
	}

	public void setApp(DataSetInfo app) {
		this.dataset = app;
	}
}
