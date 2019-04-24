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

package it.smartcommunitylab.climb.gamification.dashboard.config;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import it.smartcommunitylab.climb.contextstore.model.Pedibus;
import it.smartcommunitylab.climb.gamification.dashboard.exception.StorageException;
import it.smartcommunitylab.climb.gamification.dashboard.model.*;
import it.smartcommunitylab.climb.gamification.dashboard.model.gamification.PlayerStateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import it.smartcommunitylab.climb.gamification.dashboard.storage.RepositoryManager;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableAsync
@EnableScheduling
@EnableSwagger2
public class AppConfig extends WebMvcConfigurerAdapter {

	@Autowired
	@Value("${db.name}")
	private String dbName;
	
	@Autowired
	@Value("${defaultLang}")
	private String defaultLang;

	@Autowired
	@Value("${swagger.title}")
	private String swaggerTitle;
	
	@Autowired
	@Value("${swagger.desc}")
	private String swaggerDesc;

	@Autowired
	@Value("${swagger.version}")
	private String swaggerVersion;
	
	@Autowired
	@Value("${swagger.tos.url}")
	private String swaggerTosUrl;
	
	@Autowired
	@Value("${swagger.contact}")
	private String swaggerContact;

	@Autowired
	@Value("${swagger.license}")
	private String swaggerLicense;

	@Autowired
	@Value("${swagger.license.url}")
	private String swaggerLicenseUrl;

	public AppConfig() {
	}

	@Bean
	public MongoTemplate getMongo() throws UnknownHostException, MongoException {
		return new MongoTemplate(new MongoClient(), dbName);
	}

	@Bean
	RepositoryManager getRepositoryManager() throws UnknownHostException, MongoException, StorageException, ParseException {
		RepositoryManager repositoryManager =  new RepositoryManager(getMongo(), defaultLang);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStringFrom = "2018-11-08";
		String dateStringTo = "2018-12-25";
		Date dateObjectFrom = sdf.parse(dateStringFrom);
		Date dateObjectTo = sdf.parse(dateStringTo);
		PedibusGame pedibusGame = new PedibusGame("003658", "UT Austin", new ArrayList<String>(){{
			add("EE 364D");
			add("EE 360C"); }}, "1", "Game 1", "Trial Game", "Gui", dateObjectFrom, dateObjectTo);
		pedibusGame.setGlobalTeam("EE 364D");
		PedibusTeam pedibusTeam = new PedibusTeam("EE 364D", "1", new ArrayList<String>(){{
			add("1");add("2");add("3");add("4");add("5");add("6");add("7");}}, 0.0);
		PedibusPlayer Dylan = new PedibusPlayer("1", "Dylan", "Bray", "EE 364D", "1");
		PedibusPlayer Anjali = new PedibusPlayer("2", "Anjali", "Tewani", "EE 364D", "1");
		PedibusPlayer Charlie = new PedibusPlayer("3", "Charlie", "Yeng", "EE 364D", "1");
		PedibusPlayer Grace = new PedibusPlayer("4", "Grace", "Zhuang", "EE 364D", "1");
		PedibusPlayer Grayson = new PedibusPlayer("5", "Grayson", "Barrett", "EE 364D", "1");
		PedibusPlayer Kevin = new PedibusPlayer("6", "Kevin", "Brill", "EE 364D", "1");
		PedibusPlayer Gui = new PedibusPlayer("7", "Gui", "Zamorano", "EE 364D", "1");
		repositoryManager.savePedibusGame(pedibusGame, "123", true);
		repositoryManager.savePedibusTeam(pedibusTeam, "123", true);
		repositoryManager.savePedibusPlayer(Dylan, "123", true);
		repositoryManager.savePedibusPlayer(Anjali, "123", true);
		repositoryManager.savePedibusPlayer(Charlie, "123", true);
		repositoryManager.savePedibusPlayer(Grace, "123", true);
		repositoryManager.savePedibusPlayer(Grayson, "123", true);
		repositoryManager.savePedibusPlayer(Kevin, "123", true);
		repositoryManager.savePedibusPlayer(Gui, "123", true);

		repositoryManager.createBabySwipes("123", "1", "EE 364D");


		double [] go = {-106.4850, 31.7619};
		PedibusItineraryLeg leg1 = newLeg(go, "1", "1", 0, "El Paso", "1", "", 0);
		List<Link> urls1 = new ArrayList<>();
		Link linkEP1 = new Link();
		linkEP1.setLink("https://www.tripadvisor.com/Attraction_Review-g60768-d145533-Reviews-YDSP_Tigua_Indian_Cultural_Center_and_Museum-El_Paso_Texas.html");
		linkEP1.setName("El Paso Indian Cultural Center and Museum");

		Link linkEP2 = new Link();
		linkEP2.setLink("https://www.tripadvisor.com/Attraction_Review-g60768-d146904-Reviews-Hueco_Tanks_State_Historic_Site-El_Paso_Texas.html");
		linkEP2.setName("El Paso Hueco Tanks");


		urls1.add(linkEP1);
		urls1.add(linkEP2);
		leg1.setExternalUrls(urls1);
		leg1.setImageUrl("https://i.imgur.com/lsU13Qm.jpg");
		leg1.setTransport("foot");

		Activity stemActEP = newActivity(true, 4, "teks1", null, Activity.Subject.STEM);
		List<Link> stemUrlsEP = stemActEP.getMaterials();
		Link stemEP1 = newLink("Fab Lab","https://fablabelpaso.org/");
		Link stemEP2 = newLink("Makeblock Robotics", "https://www.makeblock.com/");
		Link stemEP3 = newLink("Make Virtual Reality Headset", "https://www.youtube.com/watch?v=8qNmRi-gNqE");
		stemUrlsEP.add(stemEP1);
		stemUrlsEP.add(stemEP2);
		stemUrlsEP.add(stemEP3);
		stemActEP.setMaterials(stemUrlsEP);

		List<Activity> leg1Activities = leg1.getActivities();
		leg1Activities.add(stemActEP);
		leg1.setActivities(leg1Activities);

		repositoryManager.savePedibusItineraryLeg(leg1, "123", true);

		double [] go1 = {-98.4936, 29.4241};
		PedibusItineraryLeg leg2 = newLeg(go1, "2", "2", 2000000, "San Antonio", "1", "{nz`Efz|hSfrgMgywo@", 1);
		List<Link> urls2 = new ArrayList<>();
		Link link2 = new Link();
		link2.setLink("https://en.wikipedia.org/wiki/San_Antonio");
		link2.setName("San Antonio");
		urls2.add(link2);
		leg2.setExternalUrls(urls2);
		leg2.setImageUrl("https://i.imgur.com/7ssVorZ.jpg");
		leg2.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(leg2, "123", true);


		double [] go2 = {-97.7431, 30.2672};
		PedibusItineraryLeg leg3 = newLeg(go2, "3", "3", 4000000, "Austin", "1", "s{qrD~_dxQktcDsqqC", 2);
		List<Link> urls3 = new ArrayList<>();
		Link link3 = new Link();
		link3.setLink("https://en.wikipedia.org/wiki/Austin,_Texas");
		link3.setName("Austin");
		urls3.add(link3);
		leg3.setExternalUrls(urls3);
		leg3.setImageUrl("https://i.imgur.com/QiPsH7d.jpg");
		leg3.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(leg3, "123", true);

		double [] go3 = {-95.3698,29.7604};
		PedibusItineraryLeg leg4 = newLeg(go3, "4", "4",5000000, "Houston", "1", "_qvwDjmqsQn~aBcpnM", 3);
		List<Link> urls4 = new ArrayList<>();
		Link link4 = new Link();
		link4.setLink("https://en.wikipedia.org/wiki/Houston");
		link4.setName("Houston");
		urls4.add(link4);
		leg4.setExternalUrls(urls4);
		leg4.setImageUrl("https://i.imgur.com/zqgU1W0.jpg");
		leg4.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(leg4, "123", true);

		double [] go4 = {-96.7970,32.7767};
		PedibusItineraryLeg leg5 = newLeg(go4, "5","5", 6000000, "Dallas", "1", "oqstDf|aeQ{blQ~vuG", 4);
		List<Link> urls5 = new ArrayList<>();
		Link link5 = new Link();
		link5.setLink("https://en.wikipedia.org/wiki/Dallas");
		link5.setName("Dallas");
		urls5.add(link5);
		leg5.setExternalUrls(urls5);
		leg5.setImageUrl("https://i.imgur.com/CQZsLnq.jpg");
		leg5.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(leg5, "123", true);

		double [] go5 = {-101.8313,35.2220};
		PedibusItineraryLeg leg6 = newLeg(go5, "6","6", 7000000, "Amarillo", "1", "ku`gEftxmQcr|Mjgv]", 5);
		List<Link> urls6 = new ArrayList<>();
		Link link6 = new Link();
		link6.setLink("https://www.tourtexas.com/attractions/panhandle-plains-historical-museum-canyon");
		link6.setName("Panhandle Plains Historical Museum");
		urls6.add(link6);
		Link link7 = new Link();
		link7.setLink("https://en.wikipedia.org/wiki/Panhandle%E2%80%93Plains_Historical_Museum");
		link7.setName("Panhandle Plains Historical Museum Wikipedia");
		urls6.add(link7);

		leg6.setExternalUrls(urls6);
		leg6.setImageUrl("https://i.imgur.com/lcdRGxi.jpg");
		leg6.setTransport("foot");


		List<Activity> leg6Activities = leg6.getActivities();

		Activity stemAct = newActivity(true, 4, "teks2", null, Activity.Subject.STEM);
		List<Link> urlsA = stemAct.getMaterials();
		Link linkX = newLink("Ecosystems", "http://www.teacherplanet.com/content/food-chainweb");
		urlsA.add(linkX);
		Link linkY = newLink("Human Impact on the Ecosystem", "https://www.youtube.com/watch?v=5eTCZ9L834s");
		urlsA.add(linkY);
		stemAct.setMaterials(urlsA);

		Activity csAct = newActivity(true, 4, "teks1", "Read Sarah Plain and Tall", Activity.Subject.CS);
		List<Link> urls = csAct.getMaterials();
		Link linkA = newLink("Quizlet", "https://quizlet.com/2646281/sarah-plain-and-tall-flash-cards/");
		urls.add(linkA);
		Link linkB = newLink("YouTube - read the story", "https://www.youtube.com/watch?v=00LNxG8LAJg");
		urls.add(linkB);
		Link linkC = newLink("Scholastic", "https://www.scholastic.com/teachers/books/sarah-plain-and-tall-by-patricia-maclachlan/");
		urls.add(linkC);
		csAct.setMaterials(urls);

		Activity selAct = newActivity(true, 4, "teks3", "Managing Worry", Activity.Subject.SEL);
		List<Link> selUrls = selAct.getMaterials();
		Link linkS = newLink("Managing Worry", "https://www.flocabulary.com/unit/managing-worry/");
		selUrls.add(linkS);
		selAct.setMaterials(selUrls);


		leg6Activities.add(stemAct);
		leg6Activities.add(csAct);
		leg6Activities.add(selAct);
		leg6.setActivities(leg6Activities);

		repositoryManager.savePedibusItineraryLeg(leg6, "123", true);

		double[] go6 = {-102.0779, 31.9973};
		PedibusItineraryLeg leg7 = newLeg(go6, "7","7", 8000000, "Midland-Odessa", "1", "oh~uEr|olRjytRfdo@", 6);
		List<Link> urls7 = new ArrayList<>();
		Link linkM = newLink("Big Bend Ranch State Park", "https://tpwd.texas.gov/state-parks/big-bend-ranch");
		urls7.add(linkM);
		leg7.setExternalUrls(urls7);
		leg7.setImageUrl("https://i.imgur.com/V7k6COY.jpg");

		Activity stemActMid = newActivity(true, 4, null, null, Activity.Subject.STEM);
		List<Link> stemUrlsMid = stemActMid.getMaterials();
		Link stemMid1 = newLink("Inspire Aspiring Toolkit", "https://www.scholastic.com/teachers/blog-posts/scholasticcom-editors/2018-2019/inspire-budding-scientists-with-this-interactive-teaching-tool/");
		stemUrlsMid.add(stemMid1);
		selAct.setMaterials(stemUrlsMid);

		List<Activity> leg7Activities = leg7.getActivities();
		leg7Activities.add(stemActMid);
		leg7.setActivities(leg7Activities);

		repositoryManager.savePedibusItineraryLeg(leg7, "123", true);








		PlayerStateDTO teamDTO = new PlayerStateDTO();// Set up class to hold statistics
		teamDTO.setGameId("1");
		teamDTO.setPlayerId("Class");
		repositoryManager.updateTeamDTO(teamDTO, "1", "Class");
		return repositoryManager;
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/").setViewName("redirect:/index.html");
	}
	
//
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//		return new PropertySourcesPlaceholderConfigurer();
//	}
//
//	@Bean
//	public ViewResolver getViewResolver() {
//		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//		resolver.setPrefix("/resources/");
//		resolver.setSuffix(".jsp");
//		return resolver;
//	}
//
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/*").addResourceLocations(
//				"/resources/");
//		registry.addResourceHandler("/resources/*").addResourceLocations(
//				"/resources/");
//		registry.addResourceHandler("/css/**").addResourceLocations(
//				"/resources/css/");
//		registry.addResourceHandler("/fonts/**").addResourceLocations(
//				"/resources/fonts/");
//		registry.addResourceHandler("/js/**").addResourceLocations(
//				"/resources/js/");
//		registry.addResourceHandler("/lib/**").addResourceLocations(
//				"/resources/lib/");
//		registry.addResourceHandler("/i18n/**").addResourceLocations(
//				"/resources/i18n/");
//		registry.addResourceHandler("/templates/**").addResourceLocations(
//				"/resources/templates/");
//		registry.addResourceHandler("/html/**").addResourceLocations(
//				"/resources/html/");
//		registry.addResourceHandler("/file/**").addResourceLocations(
//				"/resources/file/");
//		registry.addResourceHandler("/img/**").addResourceLocations(
//				"/resources/img/");
//		registry.addResourceHandler("/images/**").addResourceLocations(
//				"/resources/img/");
//		
//		registry.addResourceHandler("swagger-ui.html")
//    .addResourceLocations("classpath:/META-INF/resources/");
//
//		registry.addResourceHandler("/webjars/**")
//    .addResourceLocations("classpath:/META-INF/resources/webjars/");		
//	}
//
//	@Bean
//	public MultipartResolver multipartResolver() {
//		return new CommonsMultipartResolver();
//	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedMethods("PUT", "DELETE", "GET", "POST");
	}
	
	@SuppressWarnings("deprecation")
	@Bean
  public Docket swaggerSpringMvcPlugin() {
		ApiInfo apiInfo = new ApiInfo(swaggerTitle, swaggerDesc, swaggerVersion, swaggerTosUrl, swaggerContact, 
				swaggerLicense, swaggerLicenseUrl);
     return new Docket(DocumentationType.SWAGGER_2)
     	.groupName("api")
     	.select()
     		.paths(PathSelectors.regex("/api/.*"))
     		.build()
        .apiInfo(apiInfo)
        .produces(getContentTypes())
        .securitySchemes(getSecuritySchemes())
        .securityContexts(securityContexts());
  }
	
	private Set<String> getContentTypes() {
		Set<String> result = new HashSet<String>();
		result.add("application/json");
    return result;
  }
	
	private List<SecurityScheme> getSecuritySchemes() {
		List<SecurityScheme> result = new ArrayList<SecurityScheme>();
		ApiKey apiKey = new ApiKey("X-ACCESS-TOKEN", "X-ACCESS-TOKEN", "header");
		result.add(apiKey);
		return result;
	}
	
	private List<SecurityContext> securityContexts() {
		List<SecurityContext> result = new ArrayList<SecurityContext>();
		SecurityContext sc = SecurityContext.builder()
		.securityReferences(defaultAuth())
		.forPaths(PathSelectors.regex("/api/.*"))
		.build();
		result.add(sc);
		return result;
	}	
	
	private List<SecurityReference> defaultAuth() {
		List<SecurityReference> result = new ArrayList<SecurityReference>();
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
	  AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
	  authorizationScopes[0] = authorizationScope;
	  result.add(new SecurityReference("X-ACCESS-TOKEN", authorizationScopes));
	  return result;
	}

	private PedibusItineraryLeg newLeg(double [] go, String legID, String badgeID, int score, String name, String gameID, String polyline, int pos){
		PedibusItineraryLeg leg = new PedibusItineraryLeg();
		leg.setGeocoding(go);
		leg.setLegId(legID);
		leg.setBadgeId(badgeID);
		leg.setScore(score);
		leg.setName(name);
		leg.setGameId(gameID);
		leg.setPolyline(polyline);
		leg.setPosition(pos);
		return leg;
	}

	private Activity newActivity(boolean active, int gradeLevel, String teks, String description, Activity.Subject subject){
		Activity activity = new Activity();
		activity.setActive(active);
		activity.setGradeLevel(gradeLevel);
		activity.setTeks(teks);
		activity.setSubject(subject);
		activity.setDescription(description);
		return activity;
	}

	private Link newLink(String name, String url){
		Link link = new Link();
		link.setLink(url);
		link.setName(name);
		return link;
	}

}
