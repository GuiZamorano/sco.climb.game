package it.smartcommunitylab.climb.domain.config;

import it.smartcommunitylab.climb.domain.security.AacUserInfoTokenServices;
import it.smartcommunitylab.climb.domain.security.DataSetDetails;
import it.smartcommunitylab.climb.domain.security.DataSetInfo;

import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	@Value("${oauth.serverUrl}")
	private String oauthServerUrl;
	
	@Autowired
	@Value("${rememberMe.key}")
	private String rememberMeKey;
	
  @Autowired
  OAuth2ClientContext oauth2ClientContext;
  
  @Autowired
  CustomLogoutSuccessHandler customLogoutSuccessHandler;
  
  @Autowired
  private UserDetailsService userDetailsServiceImpl;
  
  @Bean
  @ConfigurationProperties("security.oauth2.client")
  public AuthorizationCodeResourceDetails aac() {
      return new AuthorizationCodeResourceDetails();
  }

  @Bean
  @ConfigurationProperties("security.oauth2.resource")
  public ResourceServerProperties aacResource() {
      return new ResourceServerProperties();
  }
  
  @Bean
  public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
      FilterRegistrationBean registration = new FilterRegistrationBean();
      registration.setFilter(filter);
      registration.setOrder(-100);
      return registration;
  }
  
  private Filter ssoFilter() {
    OAuth2ClientAuthenticationProcessingFilter aacFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/aac");
    OAuth2RestTemplate aacTemplate = new OAuth2RestTemplate(aac(), oauth2ClientContext);
    aacFilter.setRestTemplate(aacTemplate);
    AacUserInfoTokenServices tokenServices = new AacUserInfoTokenServices(aacResource().getUserInfoUri(), aac().getClientId());
    tokenServices.setRestTemplate(aacTemplate);
    tokenServices.setPrincipalExtractor(new PrincipalExtractor() {
			
			@SuppressWarnings("unchecked")
			@Override
			public Object extractPrincipal(Map<String, Object> map) {
				DataSetInfo dataSetInfo = new DataSetInfo();
				dataSetInfo.setSubject((String) map.get("userId"));
				dataSetInfo.setName((String) map.get("name"));
				dataSetInfo.setSurname((String) map.get("surname"));
				dataSetInfo.setToken((String) map.get("token"));
				dataSetInfo.setRefreshToken((String) map.get("refreshToken"));
				dataSetInfo.setExpiration((Long) map.get("expiration"));
				Map<String, Object> accounts = (Map<String, Object>) map.get("accounts");
				if(accounts.containsKey("adc")) {
					Map<String, Object> adc = (Map<String, Object>) accounts.get("adc");
					dataSetInfo.setEmail((String) adc.get("pat_attribute_codicefiscale"));
				}
				if(accounts.containsKey("google")) {
					Map<String, Object> google = (Map<String, Object>) accounts.get("google");
					dataSetInfo.setEmail((String) google.get("email"));
				}
				if(accounts.containsKey("facebook")) {
					Map<String, Object> facebook = (Map<String, Object>) accounts.get("facebook");
					dataSetInfo.setEmail((String) facebook.get("email"));
				}
				if(accounts.containsKey("internal")) {
					Map<String, Object> internal = (Map<String, Object>) accounts.get("internal");
					dataSetInfo.setEmail((String) internal.get("email"));
				}
				DataSetDetails details = new DataSetDetails(dataSetInfo);
				return details;
			}
		});
    aacFilter.setTokenServices(tokenServices);
    return aacFilter;
  }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.headers()
			.frameOptions().disable();
		http
			.logout()
				.clearAuthentication(true)
				.deleteCookies("rememberme", "JSESSIONID")
				.invalidateHttpSession(true)
				.logoutSuccessHandler(customLogoutSuccessHandler);
		http
			.csrf()
				.disable()
			.antMatcher("/**")
				.authorizeRequests()
			.antMatchers("/", "/index.html", "/login**", "/logout**", "/swagger-ui.html", "/v2/api-docs**")
				.permitAll();
		http
			.csrf()
				.disable()
			.authorizeRequests()
				.antMatchers("/console/**", "/upload/**", "/report/**", "/backend/**", "/game-dashboard/**/*.html")
					.authenticated()
			.and()
				.addFilterBefore(rememberMeAuthenticationFilter(), BasicAuthenticationFilter.class)
				.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
				.rememberMe();
					
		http
			.formLogin()
				.loginPage("/login/aac");
	}
	
  @Bean
  public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception {
      return new RememberMeAuthenticationFilter(authenticationManager(),
              tokenBasedRememberMeService());
  }
  
  @Bean
  public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
      return new RememberMeAuthenticationProvider(tokenBasedRememberMeService().getKey());
  }
  
  @Bean
  public TokenBasedRememberMeServices tokenBasedRememberMeService() {
      TokenBasedRememberMeServices service = new TokenBasedRememberMeServices(
              rememberMeKey, userDetailsServiceImpl);
      service.setAlwaysRemember(true);
      service.setCookieName("rememberme");
      service.setTokenValiditySeconds(3600 * 24 * 365 * 1);
      return service;
  }
}
