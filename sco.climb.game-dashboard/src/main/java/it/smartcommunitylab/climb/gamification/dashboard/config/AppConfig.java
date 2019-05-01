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

import java.beans.Transient;
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
import org.springframework.context.annotation.DependsOn;
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

	@Autowired
	@Value("${spring.data.mongodb.host}")
	private String mongodbHost;


	public AppConfig() {
	}

	@Bean
	public MongoTemplate getMongo() throws UnknownHostException, MongoException {
		return new MongoTemplate(new MongoClient(new MongoClientURI(mongodbHost)), dbName);
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
			add("PROJECT SMART");
			add("EE 360C"); }}, "1", "Game 1", "Trial Game", "Gui", dateObjectFrom, dateObjectTo);
		pedibusGame.setGlobalTeam("PROJECT SMART");
		PedibusTeam pedibusTeam = new PedibusTeam("PROJECT SMART", "1",
				new ArrayList<String>(){{
					for(int i=1; i<=32; i++)
						add(Integer.toString(i));
				}},
				0.0);

		repositoryManager.savePedibusGame(pedibusGame, "123", true);
		repositoryManager.savePedibusTeam(pedibusTeam, "123", true);
		PedibusPlayer player1 = new PedibusPlayer("550074872781", "Student", "1", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player1, "123", true);
		PedibusPlayer player2 = new PedibusPlayer("5500072EE69A", "Student", "2", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player2, "123", true);
		PedibusPlayer player3 = new PedibusPlayer("5500074F3528", "Student", "3", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player3, "123", true);
		PedibusPlayer player4 = new PedibusPlayer("55000767596C", "Student", "4", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player4, "123", true);
		PedibusPlayer player5 = new PedibusPlayer("5500076DC2FD", "Student", "5", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player5, "123", true);
		PedibusPlayer player6 = new PedibusPlayer("5500078C7BA5", "Student", "6", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player6, "123", true);
		PedibusPlayer player7 = new PedibusPlayer("5500079DD916", "Student", "7", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player7, "123", true);
		PedibusPlayer player8 = new PedibusPlayer("550007CC0E90", "Student", "8", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player8, "123", true);
		PedibusPlayer player9 = new PedibusPlayer("550007D9F279", "Student", "9", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player9, "123", true);
		PedibusPlayer player10 = new PedibusPlayer("550007DF169B", "Student", "10", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player10, "123", true);
		PedibusPlayer player11 = new PedibusPlayer("550007EF9A27", "Student", "11", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player11, "123", true);
		PedibusPlayer player12 = new PedibusPlayer("550007F57ADD", "Student", "12", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player12, "123", true);
		PedibusPlayer player13 = new PedibusPlayer("550007F715B0", "Student", "13", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player13, "123", true);
		PedibusPlayer player14 = new PedibusPlayer("550007FA8A22", "Student", "14", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player14, "123", true);
		PedibusPlayer player15 = new PedibusPlayer("550008005B06", "Student", "15", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player15, "123", true);
		PedibusPlayer player16 = new PedibusPlayer("550008017A26", "Student", "16", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player16, "123", true);
		PedibusPlayer player17 = new PedibusPlayer("5500081B0345", "Student", "17", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player17, "123", true);
		PedibusPlayer player18 = new PedibusPlayer("55000820007D", "Student", "18", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player18, "123", true);
		PedibusPlayer player19 = new PedibusPlayer("55000827AED4", "Student", "19", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player19, "123", true);
		PedibusPlayer player20 = new PedibusPlayer("5500082F2557", "Student", "20", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player20, "123", true);
		PedibusPlayer player21 = new PedibusPlayer("550008325F30", "Student", "21", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player21, "123", true);
		PedibusPlayer player22 = new PedibusPlayer("55000838A0C5", "Student", "22", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player22, "123", true);
		PedibusPlayer player23 = new PedibusPlayer("55000850131E", "Student", "23", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player23, "123", true);
		PedibusPlayer player24 = new PedibusPlayer("55000864477E", "Student", "24", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player24, "123", true);
		PedibusPlayer player25 = new PedibusPlayer("55000873DFF1", "Student", "25", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player25, "123", true);
		PedibusPlayer player26 = new PedibusPlayer("550008FA10B7", "Student", "26", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player26, "123", true);
		PedibusPlayer player27 = new PedibusPlayer("5500090EDE8C", "Student", "27", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player27, "123", true);
		PedibusPlayer player28 = new PedibusPlayer("55000934E68E", "Student", "28", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player28, "123", true);
		PedibusPlayer player29 = new PedibusPlayer("550009F8BC18", "Student", "29", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player29, "123", true);
		PedibusPlayer player30 = new PedibusPlayer("55000A04B4EF", "Student", "30", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player30, "123", true);
		PedibusPlayer player31 = new PedibusPlayer("55000A149CD7", "Student", "31", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player31, "123", true);
		PedibusPlayer player32 = new PedibusPlayer("55007486EF48", "Student", "32", "PROJECT SMART", "1");
		repositoryManager.savePedibusPlayer(player32, "123", true);

		repositoryManager.createBabySwipes("123", "1", "PROJECT SMART");

		//links come from modules provided
		//image urls stored on imgur.com
		double [] go1 = {-106.4850, 31.7619};
		PedibusItineraryLeg leg1 = newLeg(go1, "1", "1", 0, "El Paso", "1", "", 0);

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

		//proof of concept for waypoint
		double [] waypointCoord = {-102.5469, 30.5349};
		PedibusItineraryLeg waypoint1 = newLeg(waypointCoord, "2", "2", 1000000, "Reading Time", "1", "wlz`Ejx|hSgAnFiA~@c@^wEsBkJwOeLmRoVmc@kMsUaWge@_C}ZnBurAZmtBm@yc@wNg\\gEgJ{A{RDw`@oA}f@`Mw_@jb@skA~Rgj@jk@}mAbt@}tAhxAgoCtx@c~@deAsgArtAgxAndBsqBfqB_`CfWkZr[iXhaAor@lxAmdA|xAwdAbqDaiC`~HavFpjFuuDxOeRbWwi@zg@qiA|qAivCjj@amAv`@mh@|aEeoEhnA{gCv`AktBtmDsvHzZ{l@z\\s_@beF{bEt~@qv@`U__@nlAexBjiCkvEtiAqrBv`@ia@nTmI~m@aLphDkk@xWc[tcAixAxdDayEvbBqbChHgTnGo|@tSc{CpIgoAw@eWggA{~E__@aj@cWqc@y^ajBb@woAbHajDtF_|@`G_l@wB}W{mAqjCa_AqnBmF}RhAsRj]enApoAe`Brf@e}BjOkx@bk@o~DzZ{nBjX}aAnFmb@jNcwAnVi}AvuAadHjdCiaMjhAawFht@arDr\\csBbcAesGnd@ovCxJgj@hf@_eBtbBk`G|WcbAvEk\\lB{x@n@sU|FiTtWo_@pEoPjDmi@bL_v@fh@uiC~Iq_@~Tel@`O_~AS{eCjFyq@aC}aCaEieAoOut@eG{YgPoeEoC}t@x@cl@`Ke|BmA}s@ae@anMkf@mbNqf@khNq}@mdMcK}bCz\\iiItYafGmA_v@d@ax@nOao@|@i_@}QqdDuJk_EmB}w@jJycAvMcnDrMgiATcgAyGig@sz@a|B{P{y@s_@{z@iTux@aHi`@XgWtGg}@R_t@yFwdA_Gka@eMuZcHmY\\oY|Fek@xFcWnQe`@lW{h@bKg_@fSu`A`Qwb@jr@yqA~`A_bAbVi_@bKqk@jGmtChDse@jWmx@n_BcvEbyA{gEdZs}@hBwUsMqgC_VopEdGemB`MwkDdJkiApj@cpFvbBs{Onh@w~E~Hot@tAqc@nE{|DpIu}@dSwvBjUwqAdy@_zDfCoe@nGu~AxEwkAvLcyCrq@ezIt{AckRjh@esGtE}zApZkzDfLotAbTssAf_@amExU}uClEkx@dLaxFhKehF`K{aFvC}aCkA{|@dAgeCCk{@pAy^pBuf@zAyBz]mDlv@iWdtBmr@hl@mVduA_pAlP_Nz`@qO``DghApVgPn_D}fD|}C}cD~xB{qBdoCciCvOk~@~KiTdrBajBlQsh@rFiKj[_Vfk@u^tn@eR|bAcTh\\oQtMoM`\\oMlp@}OjYs`@xt@oaB|}@y{Bv_EavH`YkUloBafAfa@}RlZkEzz@X", 1);
		waypoint1.setWaypoint(true);
		List<Link> wayURL1 = new ArrayList<>();
		Link way1Link = newLink("History", "https://www.history.com/topics/us-states/texas");
		wayURL1.add(way1Link);
		waypoint1.setExternalUrls(wayURL1);
		waypoint1.setTransport("foot");
		waypoint1.setImageUrl("https://i.imgur.com/LDMvsrP.jpg");
		repositoryManager.savePedibusItineraryLeg(waypoint1, "123", true);

		double [] go2 = {-98.4936, 29.4241};
		PedibusItineraryLeg leg2 = newLeg(go2, "3", "3", 2000000, "San Antonio", "1", "{zjyDti}pRktAvCuiCptAmb@zZchAxvBgt@jsAiHUcQu@_OgRch@ulCwTqiAaYwWarAmqBewDqtFou@}v@oI{HwGhFgZxMgy@`Vqj@fg@uiBh\\{j@rTek@iEyo@rKwjA`tAisB~zCcu@l`AotBplAe`AvK}c@kDmjCqSyx@}Fk]nIesAdh@}ZjLeCoPnDmkDuR{vFed@}yEqj@ulBif@gbBkk@kcHi`@o_GlDy|Bnw@mwDr@iiD{h@y|BsDwlEhK}eG}Bk|Cx^kmBvhAoaFrl@yuBvhAm~BxgCurGhs@arBnq@gg@vf@ck@v`Am~Bvb@ev@pmA{o@fx@qq@hw@ehBhgAklDpc@{dBvOemB~WscDvQal@pjAg}Abh@_eA~Zu`Cse@y~CcfAi|DcRwx@i{@ksA}SmcBhX}cBvx@sjInUukHl\\ioFjY}o@ltAksAdb@uhBz}@saJtXyzKv`@gsB~`@_jE{CsaA_d@cpBgu@u_DoO}{BuTeiCkg@{kCg\\eoFY_iAzVymBlcAieHjjDqkUt`A_bGfbBidHllBo_H|Xq|AiJydFs@y}Ap\\ydDh`@ubBviAwpCfx@}_CtFyhBzeAusDl[gaHbx@_zLhaCwyKlcBepE~`CijGjlBodG`yBaoG|dB}fF`o@mxDjN}bDwb@w~Ck_AkaFsyBggKgVkqD}Fe}@gf@oaBwh@_uBxK{bCqr@e_FtScqBuF}r@giAypB{]in@sOikAkm@kfFzXwkC|lAstHl{AwpCro@uxAt\\mUjx@qM~uB{gAfnB_kAdh@_pA`g@{uBls@ibAfeAugAxj@}bEdgA_lAhqC}eBrnC}rA|w@ye@t{AsaA|`BchCxsAmbDxjBq}C|nA}pDj~A_tDv`AgnAxpAmnBzaBgcEh}A_bIhb@cwDbgAmwEzg@}gB|eAoi@jwAu_@|b@kMt[a^db@uhAlg@oiAdg@as@jEim@nWcq@fl@q`@nw@ywBhxAqfBtSwyC~EgfD`ImdC|Dwj@pn@_m@rb@i|AjC}{BbkAkoCtu@wwA~eA_o@lzBq~C|~@wpAd^{fAno@ctBtp@uc@p_B{i@vn@wu@zdC{fCz}A{|Dd{Aau@vrAkhA~iBg_BpeAeUp`BkeA|cB_nBfeEkf@vfC{tDnfGaiH~m@yr@tpCicBj}E_fC~dAg\\lsBadAv{Cw}@brFy{@pnBcr@p{AaxB~j@ak@n~Ak^joAoXph@aIrf@of@nB}s@tSee@`r@gx@d}@yBrsByDfWuXdH_^pn@_]ff@fAtEml@SkC", 2);

		List<Link> urls2 = new ArrayList<>();
		Link link2 = new Link();
		link2.setLink("https://en.wikipedia.org/wiki/San_Antonio");
		link2.setName("San Antonio");
		urls2.add(link2);
		leg2.setExternalUrls(urls2);
		leg2.setImageUrl("https://i.imgur.com/7ssVorZ.jpg");
		leg2.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(leg2, "123", true);


		double [] go3 = {-97.7431, 30.2672};
		PedibusItineraryLeg leg3 = newLeg(go3, "4", "4", 4000000, "Austin", "1", "k|rrDtcdxQBdA[Di@HcBN}Cn@eM`MKTcBkDkDcHeAeCGoCm@i@WBcAXwAu@iHuO{L_WaAuD[eFB{B^cDzB}IvBeOxAgOVgINqZC{k@IqUWoGsAqSa@qJCuL?q@?}PUyI_AkKuCuO{AeFkD{IiFqKcTyc@iRwb@yKs\\mFsOeEwH}D_FeCaCgFkD{DqBsi@kTch@wS{MiFcFiAsE]eQGaFSsJcAsPqBcC]mw@wJugBsTca@_FyGwAwDoAmIcCqE}@}GoAaGkA_UsEeGyAoFmBeGwCcLaIaGeG}DiFeHyMoQ}_@ed@_aAu[ar@{EsOyEqV}BuJuCoI_G{MqBmDqKiNoRuUyDqE_c@sh@wl@ct@_HwJuByDwEmLqD{MiNuj@wCmIaDwGsGqK_RsZwC{FcDcIg[{v@eb@odAaEoHsCgEkFiGgLuLyPiQyLgMaZo^uHaKuGkM}CwHc`@c`Ao}@iyBkx@ioBsbAqlB_IgOsHqMyJePmBwE{Jg[gL{^yDeLmB_EkNkTwd@cr@cb@up@_Yed@_DgHoBmHmCiTqIs{@sAcMsAkIaEwOmUaw@oZmcAcGaQ_H_McFmGkCoCiHyFiK{FeIwCuHgBoc@cFaGo@eQaC}KwCiGeCcKuFgIiGmJ{JoRcVwQ_UifAcsA{G{HiGgGqWiQke@kZag@o[ue@qYon@oa@uh@q]yWkQiPiPqHyH{MyLmRoN__AueAooCk_DqfCouCsgAypAqn@iu@q[oe@cQ}WeMwQyG_IyQ{P{RaQ_j@ef@{VwUmO}MoKqH_QiJoi@yTm}Ba`AajAse@gOcGsOoEcFeAgNsBoc@uFgm@qHmOwAmYoAcb@iBkMw@eWsCsOeBkZeEgPcEuKyDak@iXoh@aW{NmHc[yRcn@y`@m`@mVeHmDyKoE_E{AoeAya@sbEi_BiFuB}QoHgPcIaVuLuLqFaNaG_TeJqPgGi[iLaWgJ{R}Gei@oPwj@oP_wAsa@i|@iUgc@aJaXgFwHcBaP_FcJwDwJcFqI{E}f@gYu[}Q{|@sg@gf@s^gm@wd@_TyOoNsIoXsNaO{H}\\qPmf@gUgVwLqi@eXae@sUuDiAkEe@aC?_KbAwC\\qMfBsTnCeMlAgEHeIk@wKaCed@qLk[oI}PyDo@MyD}@qJcD_FoBaEoByPeJaWiNit@sa@qJmF}K}F{D_CaCiC}A{Bs@y@UUIPi@t@MNg@d@eDvBwDbABbCYvBsRth@]xBApA{Rhh@iFfMyKpUq@bBYx@`@VB@", 3);

		List<Link> urls3 = new ArrayList<>();
		Link link3 = new Link();
		link3.setLink("https://en.wikipedia.org/wiki/Austin,_Texas");
		link3.setName("Austin");
		urls3.add(link3);
		leg3.setExternalUrls(urls3);
		leg3.setImageUrl("https://i.imgur.com/QiPsH7d.jpg");
		leg3.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(leg3, "123", true);

		double [] go4 = {-95.3698,29.7604};
		PedibusItineraryLeg leg4 = newLeg(go4, "5", "5",5000000, "Houston", "1", "as_xDbppsQjDoL|JoSnRsg@bQ}i@rFsS~As@nJh@zD`@vJ|E|_@dS`p@t^~VfOnWrK`e@fLjn@nPz_@pJjl@}Ebh@uErOrFtd@jU~qAfq@p\\dPpDKdEmD|@oJEu]tFiaAtNsfCkFk\\ya@udBaP{f@_CaNDoPvBeN|HqSxVcm@dJqRt[kv@~p@_~AtWqq@rTs^vc@oh@|HwJfDmKa@}h@_NaeCgD{z@nDcg@hPulBtFof@dLe]f_AwwBrWal@xd@ix@tf@c{@vt@opAdQm\\nQ}f@n`@{hAlp@kfB|vA}yCdSki@bBuh@mA}xE_@mlCpAoOnMgi@fSmz@z@_f@Ue{@c@ydB`Feg@nQ_bApMad@lHgRjF{GdNoQdG_U~Nwv@fUwc@bPmXzM_J~e@oWpIgLpGeXfRwcAhIcUxNqQ~y@ocAzPia@xk@gzBhSi\\zj@kt@na@ic@lkBajBri@_i@za@e_@lUeLtk@}XrQw[l[ic@p`@kc@hcBkjBvp@yc@rLcQf^ibAbO_n@zGcy@Us{@kCw_AeCo~@p@uq@|IuaAhSuaBbHa^nb@yp@dGcSxAyXLy\\`JmXhj@woAx[}dApRyn@l\\koAvCkZo@sJmFsQkZen@wWyq@}Z}j@}Nq]oCmVv@up@`Kcf@r`@g~@~Ruc@dZym@|_@uhAhQiq@fDq^Qsx@kCmzBxBiUrD_Mtd@wz@lWqc@tReNr[uJxRsObKaPjU}Rft@qn@~KgUfJkLn_AmbAhlBwzB~r@kx@zY}g@bTqd@x`@gd@v_@c`@~Xa_@fw@wcAtm@ix@vpBohCziAabBx_AmsAlg@ot@xRkRpReKjUwFzl@aDvn@kC~XoE`w@}Q~d@eM|CuEx@_HeC_OqAwMr@qv@~G{hAG}QgBaL{Jk_@m[ag@kVw}@yGu\\oHePoVme@mO}~@{TusCaNkwB}Cep@aFif@i`@ecDsY_bCamBc~O{UesB}JoaBiYk}Egd@acFaEcg@h@_P`S_dAhBySEmUaPa`As[m}AkGkTkHkLqP{UyFePoGkj@iAoo@cBe{AdBwt@nOurBpJ}nB~H_wAgFqf@ci@ukDeWeiBH_v@bKqq@nCee@OigA[u`CpFyy@lBs|@Wu|ECwwGs@}vB}DsQcG_KwXm[cFcOo@{S@c|BZmiG`BkyQTidGlAqvERoP~CwF~r@wBtd@mAfRyIdFkEtL}G~O}@nbCaApeA[pn@a@nEmBpA_EyEupAkFekBc@s\\IuBlCUfB?hJEhMAhHjArKlDhOLxTGbD[AyJGuI~EkAlFD", 4);

		List<Link> urls4 = new ArrayList<>();
		Link link4 = new Link();
		link4.setLink("https://en.wikipedia.org/wiki/Houston");
		link4.setName("Houston");
		urls4.add(link4);
		leg4.setExternalUrls(urls4);
		leg4.setImageUrl("https://i.imgur.com/zqgU1W0.jpg");
		leg4.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(leg4, "123", true);

		double [] go5 = {-96.7970,32.7767};
		PedibusItineraryLeg leg5 = newLeg(go5, "6","6", 6000000, "Dallas", "1", "sihtD|u~eQiMaBvAiNlAyEaOqTah@}l@qQkRin@{t@_IiUSai@sFkaBoKaXerBgYs`@mEck@LmbAxAsr@iDmLoIcM{Gi_@B_t@sHs^aHkZqU_Umj@ePaq@e@gdCeEi~Aq@waBB_N_DwBw[pOi_AhV_nDxzByxAtn@ac@lNex@fBieAh@kjB`AaiBkCiv@bMg{@fTg|ApHqcHjv@yjAfFsoA`@qzAjNwtBlUkdA`Imj@kHsq@Toy@Ngl@~Kgm@hZi}@hKu}Cp_@anArIucBtH{zBtTc|ChMwdA?wk@qGm_Dc@ea@~CiT~I{y@l]qjD|w@wiBdf@{~@pVy\\jCesAxD{iAjCweD`G}wBfc@mq@`Moc@xA_~DpE_pFq]igAsBef@pFcjGr~@oqPfq@}]nHmrAzn@sYdc@mUdj@ki@zuAg]|l@}uDn~BqdAlsAkqBhlD{zFz~JwbEh~DidAzbAycAz{AcoAdgBc`DlaC_mCptB{i@reAwcEtuI_jAv`CsnB|mB}bApz@_lAvw@s{DriCgo@pb@ym@n[idFjmAosB~f@kp@vUks@f_@u|@lYu{C|h@eyFzw@qeBjVcZhAw~@mG{tAaNy`AkVgvB}Kca@@sm@tRyhAzd@cp@~Pe~Bva@__GxdAcrBd_@scA`j@o{Dr{BieFzmEk_@|Uc]bI_pA`Y_lBdb@a}@j[ug@|YiiBheAqhB``AkjEz`Cup@tS}aBd_@ycB`FahD~LciG~k@efA~^gkAzYysB|f@}sBtkBqv@zs@mkAlsAkr@j{@ap@zgAahBdvBoj@lo@eu@xq@odBfy@}eCn_C{Tb]w_@nyAiV`w@exBfxBu~BbgFooAh~Buz@xfAiN~AqZsD{lBoUim@PyrBpSqrAz]koGplBcjC~c@_XkDgi@m_@qc@w\\oZyKoc@cA}v@bKmxBb[}cBl`AkiAtn@et@|Xaj@bFuwDlVwu@rF}_@hNgtB|x@ogCxcAcoA`m@}z@|^uSvQkv@rpB_v@`uBqsArvDgp@xhBiYl^w`BliA}zBvnAyq@x_@idAtz@ik@bd@}ZpKuzAf_@ciBff@q[|B}h@eHkm@tBypAb^}[|Bu|BuTka@]q[bIy_@xGwt@c@mrBmCw`@m@m\\aI_s@eM_uAeAagBfa@afB`n@ytE|iBcfF|nBeSdJwNzReg@vjAycCnyBiTrPk_B|d@spAxWegAxb@esCjl@u`@bIim@nTw\\zIix@jNcUnN}@fK~NpS~DvWzJxl@tSl^R`S_JhU{TnEvAoEj@eCcDW{@P", 5);

		List<Link> urls5 = new ArrayList<>();
		Link link5 = new Link();
		link5.setLink("https://en.wikipedia.org/wiki/Dallas");
		link5.setName("Dallas");
		urls5.add(link5);
		leg5.setExternalUrls(urls5);
		leg5.setImageUrl("https://i.imgur.com/CQZsLnq.jpg");
		leg5.setTransport("foot");
		repositoryManager.savePedibusItineraryLeg(leg5, "123", true);

		double [] go6 = {-101.8313,35.2220};
		PedibusItineraryLeg leg6 = newLeg(go6, "7","7", 7000000, "Amarillo", "1", "in`gEf}zmQoAfH|CnFgNcCzCr@gg@zCylAxS}d@diAyr@x}BwTtoBcoAxtB{aA`aCCn{Ca@nwFn@dkGsGbzFrFbiBeHjeA`E|oBpCtlG_S~jAlAdoBxE~pBp]ty@xDnaAy]x_BgNhdBjC|hCe@ppGiJ`Xi}@nBsoCzYuy@Ks`@hRa|EzkFqsEtbHcm@dfAo{@pk@{~Ex}Ac_AdXexAr`Ago@`v@qVhqAua@zWiqCud@qiCmJmk@vNwxBpaBcrBv_Akl@by@yuAnbCkcA`sAwaEjfDejAjpBquB|{@yh@`h@auAnc@efE|fDu`FlzDosBbyAk[`nAiXfr@ig@~O}fAzFkcAfo@ys@paAuqAvaAklBnmAkkBp_BkvBvcBwaCznAiiFt_D{eB~eAss@hsAc_BleD{aBdiCgeAvqA}sBvqFk}DdkKsbBvjEiqCrgDsuCdbColBdy@ypEz}B_cBp|@ipBnnDwuAxvDai@pvBkh@`sA}SzGin@zUodBr}HqgDbnJka@nzA_JtnAmcAfmHrD~y@e^prB}@d{EmTdsA{z@`s@o|AnnAiZl`@_w@bQkmAbb@yZ~Yy^`hDsdAhpNca@v~EMvhE_FbgDcYp|AQt|Dma@rjCorAv`E{bAzjAmeEdpIudAtlBiC~dDuA`{AiSf|@gnBj{FmkCztHueF`{N{n@tq@sApx@lRrmAwXjx@a}B~sOq_@xfD{DloA}EziBqcB`eAyi@zh@}iAv`DotCpjGo|@rmEgy@zhBuv@thCqa@lpDoLjkBfJhlBpUr_B{RheB}iAzyHyNrdEwq@|cEuk@lfC{Cry@oQfmCua@r|FkjBv_Hk`@z^og@l}Ag|@nvDoNz_CstAfkHas@tyDyx@pmDyl@hzDm_AfbH{uBjlKe|E|tRwaA`aEqr@drBq`CtsF}]tsAooBnuC_Of{AaaA~uBkg@fe@sqAnmC}m@tnA_aAvEev@tL_o@_K{aAkXweBrsCwXt`@inAd\\sjEvu@gxDhg@gnBrnAklB~bBeO~o@a_Dj`DwrBfkBqhDlgBqhBzKgsAfx@qbE|hHqbAtt@aq@vp@}\\`fAaBjmAoKvcCov@`qEwlBn`E}d@vcAil@l{B{NvdCy`@b}Bg~B|fIwbBjnDqn@zpBke@niCmZ|uBc[j}@coChuGitDtuJk^lhScw@jlDebBjnB}z@fbAiaDlzK_dBjtGq_BtlFg}Ch`PwtBluKm~@l~GiiBtmWiy@jvLH`{AwKb\\wKjAeZxTib@{AsW?cW@@b\\Fnh@B~LBrD", 6);

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

		double[] go7 = {-102.0779, 31.9973};
		PedibusItineraryLeg leg7 = newLeg(go7, "8","8", 8000000, "Midland-Odessa", "1", "_v_vEraplRB~CtKBnHFvUIlr@vHns@tIf\\xBtGkCx^|C`o@jGjPFvUtJzPtOdBvHNzf@^fLjM~`@bQpd@rMfRz_Avw@dkDnsCrc@tZfk@dZn[vHtqElk@vo@lHzq@LraCF|{AD|^yBbO}GrVoUxjBmfBzfEggDdrBonBvScMtSeF`b@y@~qHH`pE`AlbAs@dlGDb`ECh_@iCzv@eA|iC@n_AGt_@wF|vBm`@hpGckA`o@yOrqB_s@rqEm~Ade@qQfSgOlSqN|K_DfR_@bS|Bdj@|BvsABd}@H`r@dBbbADfj@qAdvAkm@|{Awq@hb@eM`kBe@hjFFdiGGvZsFt^eUpfBirArhB}sAng@gSjwKomDfZuJ`i@yPxKqAzpBqAv[fB|S`LfcArmAvqBp_ChXbZpLtEvaARz~@rC~k@sDlyAuEh|Bs@jWjIfMzLbn@||@|c@vs@d}@zpAz_@nz@fbCbvFpc@jr@vPzLnOxAtl@P~NzF`Yh\\xU|QbS|DhgI\\j{IWpyMD|p@GfQcDpNiIvMgOla@mg@hNyF|`BEf\\kEhYeJz_@qAbaDOzxEKf_AtXrSl@bs@gAz^kMdP]bi@zB`m@`@pc@sIjc@eJ~~@u@xyA?z]dBlt@Mzl@uCllBu@dPhCfZtRxU`SjXbRpJr@xt@{H~ZcHvUaNzSoFzi@EtW|@t]nMli@zQjb@ZtbBDhaAkBffBcBz{@vCbhBDhkBOhaAkAfh@B|_@iEz|@{P`]mHhUkKjoAgn@`nCyrAvu@{^bRmDrtBgUjdGG|cLc@~mDVbj@Kxw@eR`vB_h@nLkCriAQ~U{@~LuDxOmErVMn\\z@fl@FpS~Dx~@rj@`e@hXxKlBntCTpsDNb|MR|V~Ept@`l@v}CfnChj@lh@bUzI~q@fFjTdCzUjKbeCloAdw@ha@te@nOphBzb@naDvv@h}BddAn}KvbFvm@bYfTnTrOlM|d@zS|w@p\\~l@~ErZcD`YuFjRwDvKwB`Ew@p@nFr@zFt@tF|K{BnMe@`m@|InYlDv{@oQzJWlM~Kv`@da@fZnMpyBtx@laFnfBnrB`n@l|E|xBlvA~[ng@rJzPx@pa@|EfnAdk@jeAff@pq@lc@zhCdeBjhAju@`z@np@~pDdyBfwN~vId_DjoB~k@p^pJpDnM|@rOmBrl@mM|qEqaAvrI{iBjd@eL|VyOnYgOvn@_NxmBaa@||Bch@ppGqyAtnIslBjzAs\\xRwBx[dBvi@rL`P|BjOo@lbBm^vc@gKzS_BpZrB~ZqDfc@c@", 7);

		List<Link> urls7 = new ArrayList<>();
		Link linkM = newLink("Big Bend Ranch State Park", "https://tpwd.texas.gov/state-parks/big-bend-ranch");
		urls7.add(linkM);
		leg7.setExternalUrls(urls7);
		leg7.setImageUrl("https://i.imgur.com/V7k6COY.jpg");

		Activity stemActMid = newActivity(true, 4, "teks4", null, Activity.Subject.STEM);
		List<Link> stemUrlsMid = stemActMid.getMaterials();
		Link stemMid1 = newLink("Inspire Aspiring Toolkit", "https://www.scholastic.com/teachers/blog-posts/scholasticcom-editors/2018-2019/inspire-budding-scientists-with-this-interactive-teaching-tool/");
		stemUrlsMid.add(stemMid1);
		selAct.setMaterials(stemUrlsMid);

		List<Activity> leg7Activities = leg7.getActivities();
		leg7Activities.add(stemActMid);
		leg7.setActivities(leg7Activities);

		repositoryManager.savePedibusItineraryLeg(leg7, "123", true);

		List<PedibusItineraryLeg> legs = repositoryManager.getPedibusItineraryLegs("123");
		Settings settings = new Settings();
		settings.setup(legs);
		repositoryManager.saveSettings(settings, "123", "1", "PROJECT SMART", true);

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
