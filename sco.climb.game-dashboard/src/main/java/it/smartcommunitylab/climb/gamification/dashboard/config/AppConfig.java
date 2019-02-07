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

import com.mongodb.MongoClientURI;
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
		return new MongoTemplate(new MongoClient(new MongoClientURI(
				"mongodb+srv://dbuser:dbuserpassword@testcluster-msoq5.mongodb.net/test?retryWrites=true")), dbName);
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
		PedibusItineraryLeg pedibusItineraryLeg = new PedibusItineraryLeg();
		pedibusItineraryLeg.setLegId("1");
		pedibusItineraryLeg.setScore(0);
		pedibusItineraryLeg.setBadgeId("1");
		pedibusItineraryLeg.setDescription("Test");
		pedibusItineraryLeg.setName("TestLeg");
		pedibusItineraryLeg.setGameId("1");
		double [] go = {11.100807499999974, 46.083009};
		pedibusItineraryLeg.setGeocoding(go);
		pedibusItineraryLeg.setPolyline("");
		pedibusItineraryLeg.setPosition(0);
		List<Link> urls = new ArrayList<>();
		Link link = new Link();
		link.setLink("https://github.com/GuiZamorano/sco.climb.game/settings/collaboration");
		link.setName("GitHub");
		urls.add(link);
		pedibusItineraryLeg.setExternalUrls(urls);
		pedibusItineraryLeg.setImageUrl("http://imgur.com/oFB6oyem.jpg");
		pedibusItineraryLeg.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(pedibusItineraryLeg, "123", true);
		PedibusItineraryLeg pedibusItineraryLeg1 = new PedibusItineraryLeg();
		pedibusItineraryLeg1.setLegId("2");
		pedibusItineraryLeg1.setScore(560000);
		pedibusItineraryLeg1.setBadgeId("2");
		pedibusItineraryLeg1.setDescription("Test1");
		pedibusItineraryLeg1.setName("TestLeg1");
		pedibusItineraryLeg1.setGameId("1");
		double [] go1 = {12.31551509999997, 45.4408474};
		pedibusItineraryLeg1.setGeocoding(go1);
		pedibusItineraryLeg1.setPolyline("yqgxGccwbAjEwL`@mVf[s[i@q[fG}e@~GeLpEeP|FyPrAeL}GwF}BeQiEcEaA{MYgXqA_OoOqh@_Gu\\kKiVT_NHiWuHi^aFyZoBkN}GHuIuB_BwKsCsThOwZ~Vq]dDcCbKuc@jWmf@n_@{{@ns@e_A|m@q_@ho@mNt]kAzJgLxFq[jPmI~f@uIhc@yB|e@a~@hk@wf@hSqTpIe[|TuvAsGwxAwBc~AaXi{@gViVm\\olAiFsw@{Jw_AoI__@cU{e@gX}c@Hc[kGuj@sOs]o@mJaE{ByJy]qEyHcFqNmCyl@kQwp@qZy}@ge@gbCsG{j@yMed@wAef@vFgp@p@sBxFaLpCmNlH{bAD[gCq_BmG}GxGoSuC_HnDw@d@CnFkD~Ec_@|C_Ut^k|Apr@abCpYeyA|XqbAjYis@nWanA|Tg`@pS{qB`Dk}@ng@ieA`VybAtn@gV|i@_f@j\\uTbXiIt\\qWz^{PvZqg@i@_n@dFua@z_@wa@dNwQ`CqPdPsj@fTwLrZiGlk@jI`g@aPfe@ErX}A`WhGfm@pYje@`|@vRvfBfCfTjJpLrZ_@hPzElZb[pf@va@nT`L~NlIzEtM|g@dz@jMbPhYzDtWyWrb@q`@h^ye@hg@w{Al\\_d@dI_W~\\oZpUi^xP}OvP_Qzm@kI|\\oChQoKfP_Vta@cTjXsa@fI}EHsQtWak@~HaUdWue@h_@a`@dLqZfJkLl@oKnCqHfMoE~U_Oba@iKdDwLjq@ca@|L_Cb@{HxLsIb]gKbb@_Hpz@mS~hAca@xR_I~Kyj@vNgIdDkRbDo_@~Mwi@nDsPfJaGnGeRkAuUvIk\\vNml@eJwmA}Fyw@zE_Uxq@mnBd{@amChVuTfAsSz@iRc@wFlIwBxFcFa@oOxEgBRG?C@CDCJBr[kb@r[gUrxB{|AvIyFj[wGvVsGtc@mhA`a@e_AgBwa@|AqY~ImRlCyLtNwhAdAsAhSmf@hNqt@vHuc@~Uq]|Hca@dFqNdXWpLyIhEgLjBul@bLm@~KgBjDcJh@_NzPkU~D}OrF_WjFaE~GuUvEaxAzEsbAnKoeA|DiM|QqObSyRzRqp@lHkUlC{WjL{Q|V}P~R}SnK_d@vWw`AxFgg@hIi[xO_SnGsVfWiVbh@cy@tO}QtKyVbFaJz^y_@dL}Zva@ku@n[aYpSuRjJoSpMee@pCmHx@qYfCh@b@D~]bBXEdf@bNvDgLrD{C`F_VbItBrGhC~AgIzEaa@bGuSdSyZpLuXpKyu@dTuq@|Jwa@p|AkeDn_@iq@~Q{B");
		pedibusItineraryLeg1.setPosition(1);
		List<Link> urls1 = new ArrayList<>();
		Link link1 = new Link();
		link1.setLink("https://github.com/GuiZamorano/sco.climb.game/settings/collaboration");
		link1.setName("GitHub");
		urls1.add(link1);
		pedibusItineraryLeg1.setExternalUrls(urls1);
		pedibusItineraryLeg1.setImageUrl("https://i.imgur.com/pwyMe8j.jpg");
		pedibusItineraryLeg1.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(pedibusItineraryLeg1, "123", true);
		PedibusItineraryLeg pedibusItineraryLeg2 = new PedibusItineraryLeg();
		pedibusItineraryLeg2.setLegId("3");
		pedibusItineraryLeg2.setScore(1475000);
		pedibusItineraryLeg2.setBadgeId("3");
		pedibusItineraryLeg2.setDescription("Test2");
		pedibusItineraryLeg2.setName("TestLeg2");
		pedibusItineraryLeg2.setGameId("1");
		double [] go2 = {12.496365500000024, 41.90278349999999};
		pedibusItineraryLeg2.setGeocoding(go2);
		pedibusItineraryLeg2.setPolyline("kdjtGekdjA|b@_aBjIie@Fec@j_@i_@b^oa@vNrk@nzBx}AleA|Fb~Bq_@`kDdzDxjAodA|hArJxbFlwA|}C|Q``AjOn~@pgAjhApOhS`Hx\\xJRF||Av@fn@_[dtCjl@nxCpo@v{ElwAv|@_Whx@x\\hyB`e@hbCwDdtD`tAb`EpiAfhJop@p~A{e@`eBto@nnC|qDv|ChdCbwBhTtvDq[hnKoyCfiEeuB|vCuHrcCkr@h`FmfAl~H}fAleFrcCl_GjeBl{Dhr@jrA~l@hUly@ngAcp@rd@zC~}Am^~~BoeApy@~PjpEeElFqtAgNguCjSeg@vjFifDliCudB`nBsoCz_GymFjjFokEpiEk|EnnBkqCrzDwpCvjEcdHfmA}hBptA{KpgAycBv^q}BlcBod@peDemBdgCkFvm@pGbk@uEzi@lm@vlClTnk@pAjb@yb@xC|F|k@_\\td@aX|Gch@fVrMxZ{NrbAmXzs@r{@h}ChdAtPha@lk@mXj~@z[Yj\\tLgYrv@Ipe@u`AdvAaxBrj@mMxhA{cBnoAuXn_@g^|gAds@d_Adi@bp@cBzaAjMt~Aut@jo@yItqBcm@n}Ag{@dy@_kA`aAmPzk@e@pp@kJtqBbFveB}RvvA|a@jgBfoA|xArk@jmAl[|aAbWhy@hLfjBfy@nc@la@vkA[tj@gKfvBc|DxbB}aDzk@s{Bl_AiYni@dZbZdoBtz@p`BrY}`@daAme@tZYz[q{@bhBqbCjnEisDpeAqcBpwBlQtg@d[tdAe^hgB}p@pwGipCbjGfAdkCa~@dpAfP~uCpU~kA`}@beAp}A~`A`J`oB|h@jxBrHfeBmg@thBcs@dn@ds@ntArVnh@vu@~UltA~Ypt@hm@a@fWxYhvApXtnCedAtmDoAx`BeL`tBud@fb@b^zsEpIrsGnJ|bA`k@hzAkd@|iAsl@|x@hZzd@g`@fsArvAvhDuQr|BeA`s@sw@~uHzK`aAfq@aAfdAps@f]lpCt_HhhArcAzj@vp@ph@wQjqChjBxfB`r@vyCkfBlfCyVxoA|Gpb@}n@tzAgSpkBuUl}A}TjyBnj@vhBtEpx@yH|l@h[hi@bMd|BrHvdCkeAbwAbyAluBzK`}AfVf|@kvBxv@n@xv@ne@b`B}YfpAbzAdwAeB|l@a_A~u@tAjuDfl@`hA|_@nz@plAdlAlmAfhBtb@~uBr_Dfm@uS|w@iuAbYug@t_AvYjy@j`@jKvT~gBxlAdj@yMrDzJjx@toBz{@|s@xdA|YlgEhqCfmCfaD");
		pedibusItineraryLeg2.setPosition(2);
		List<Link> urls2 = new ArrayList<>();
		Link link2 = new Link();
		link2.setLink("https://github.com/GuiZamorano/sco.climb.game/settings/collaboration");
		link2.setName("GitHub");
		urls2.add(link2);
		pedibusItineraryLeg2.setExternalUrls(urls2);
		pedibusItineraryLeg2.setImageUrl("https://i.imgur.com/41Q7t91.jpg");
		pedibusItineraryLeg2.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(pedibusItineraryLeg2, "123", true);
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

}
