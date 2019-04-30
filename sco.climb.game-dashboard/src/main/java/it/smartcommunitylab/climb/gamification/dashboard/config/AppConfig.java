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
					add("PROJECT SMART");
				}}, "1", "Game 1", "Trial Game", "Gui", dateObjectFrom, dateObjectTo);
		pedibusGame.setGlobalTeam("PROJECT SMART");
		PedibusTeam pedibusTeam = new PedibusTeam("PROJECT SMART", "1", new ArrayList<String>(){{
			add("1");add("2");add("3");add("4");add("5");add("6");add("7");}}, 0.0);
		PedibusPlayer Dylan = new PedibusPlayer("1", "Dylan", "Bray", "PROJECT SMART", "1");
		PedibusPlayer Anjali = new PedibusPlayer("2", "Anjali", "Tewani", "PROJECT SMART", "1");
		PedibusPlayer Charlie = new PedibusPlayer("3", "Charlie", "Yeng", "PROJECT SMART", "1");
		PedibusPlayer Grace = new PedibusPlayer("4", "Grace", "Zhuang", "PROJECT SMART", "1");
		PedibusPlayer Grayson = new PedibusPlayer("5", "Grayson", "Barrett", "PROJECT SMART", "1");
		PedibusPlayer Kevin = new PedibusPlayer("6", "Kevin", "Brill", "PROJECT SMART", "1");
		PedibusPlayer Gui = new PedibusPlayer("7", "Gui", "Zamorano", "PROJECT SMART", "1");
		repositoryManager.savePedibusGame(pedibusGame, "123", true);
		repositoryManager.savePedibusTeam(pedibusTeam, "123", true);
		repositoryManager.savePedibusPlayer(Dylan, "123", true);
		repositoryManager.savePedibusPlayer(Anjali, "123", true);
		repositoryManager.savePedibusPlayer(Charlie, "123", true);
		repositoryManager.savePedibusPlayer(Grace, "123", true);
		repositoryManager.savePedibusPlayer(Grayson, "123", true);
		repositoryManager.savePedibusPlayer(Kevin, "123", true);
		repositoryManager.savePedibusPlayer(Gui, "123", true);

		repositoryManager.createBabySwipes("123", "1", "PROJECT SMART");


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

		Activity stemActEP = newActivity(true, 4, null, null, Activity.Subject.STEM);
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
		PedibusItineraryLeg leg2 = newLeg(go1, "2", "2", 2000000, "San Antonio", "1", "c}|`E~p_iSl_Ay{@@ySwnAu~BmBunCuWgmD~PsgCd|@_cCn`E{oHhcI{{IxvF{hFntCcsBxnIccGjkKcvHvzDsrInyFczGhzCetG`jDunHd`Is{GvaHi|L~nA}xAbhFqdA`{GesJrgAmdBrPq{BjSeyCiVo|Bcz@sdDa{@emBmM_dD|H_dDQ}bCuhC{pFrh@mwBzmAojBjf@yzB`{@seGfx@ilEbiC{yNvgH{i_@xyAikJjaDodLlMeqBfe@yfA|aBsmIfJcvLeo@e_J`A_pIsoAqz]w}Ay|Xd^ipOz\\wxH}QwjHnFgsH|VufFsiA}aEyhA{sD|Ce_Eqb@oaCxu@onCxlAuqD|oBogC`\\ghFjxCm|IzxA_eFub@k}HvPieG|tAytN~_BcoOru@q`NbbBmwKd~Aq}Vt_Ds`b@f}@kzIfh@m`OjTwpKeX_aDut@ylCjZgcD`}@cyBdJomI~Fi`J|aBadV|z@ufDdSstDoQujGsiAy}H_l@mkBuh@upGm_@{qKvk@yyCvN_eEcj@{}BwD{kE`KgfGcCexC~ZwjBtjAggFpxF{{N|y@sbCbn@uf@`iBydDpoBolBvz@sn@~v@kdBlhAaoDbv@}pEri@gwE|sBk~Cv]ycC}b@qxC{gAs|DomAisC{UqbBlTg|Ab{@cgIvVcyHj[{nFbjB_aCpb@{qAv|@yzIp]_yLzcAc~Hac@mjDchC{|Pi\\aoFbSiuDxcDuxTvjCa{PdjEuwPv`@gmBeHesErXyvGvcBshF|~@{gCtGakB`cAonDx[qlGfy@qmMd`Ce~Kj~C_kIfiH}}SjgB}cFlr@acEpOudD}`@q~CuwB}uKg_AelEuYywD}wAewG~KabCms@s}ErNifDktByeEyr@ajGlSibCtoAsbIznAyyBp{@woB|oAye@vfFuoClsA{dEhzBcmC~l@efElfAamAjaHizDtrC_fBfbH}hMhmDcdJlrCs}DjcBybEjaCuzNxcA{pE`h@snBzaA{k@t}CakAlkAyuCpo@q`BlMwh@nt@uk@hs@eqBv}A_mBpZ{|HrIyzBtCkw@`l@oj@lf@o_BjBuxBxgAulCbu@m{AvhA{p@zvBqyC|~A_tC`q@uyB`j@qe@jcBqj@lo@yr@xeCegCf~Ac_EjxCqgBh_CgvBdeAmVzfEssDvfE{g@vfKiuMxcEk~ChaFqhCjuDu`Bf}Cm~@zcJinBdzAcvBbl@am@|~A{_@|iA{W|vAqn@nWm|Axp@ex@dw@yCbaBwC~j@{IhNkm@ec@gMeFaY", 1);
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
		PedibusItineraryLeg leg3 = newLeg(go2, "3", "3", 4000000, "Austin", "1", "k|rrDtcdxQBdA[Di@HcBN}Cn@eM`MKTcBkDkDcHeAeCGoCm@i@WBcAXwAu@iHuO{L_WaAuD[eFB{B^cDzB}IvBeOxAgOVgINqZC{k@IqUWoGsAqSa@qJCuL?q@?}PUyI_AkKuCuO{AeFkD{IiFqKcTyc@iRwb@yKs\\mFsOeEwH}D_FeCaCgFkD{DqBsi@kTch@wS{MiFcFiAsE]eQGaFSsJcAsPqBcC]mw@wJugBsTca@_FyGwAwDoAmIcCqE}@}GoAaGkA_UsEeGyAoFmBeGwCcLaIaGeG}DiFeHyMoQ}_@ed@_aAu[ar@{EsOyEqV}BuJuCoI_G{MqBmDqKiNoRuUyDqE_c@sh@wl@ct@_HwJuByDwEmLqD{MiNuj@wCmIaDwGsGqK_RsZwC{FcDcIg[{v@eb@odAaEoHsCgEkFiGgLuLyPiQyLgMaZo^uHaKuGkM}CwHc`@c`Ao}@iyBkx@ioBsbAqlB_IgOsHqMyJePmBwE{Jg[gL{^yDeLmB_EkNkTwd@cr@cb@up@_Yed@_DgHoBmHmCiTqIs{@sAcMsAkIaEwOmUaw@oZmcAcGaQ_H_McFmGkCoCiHyFiK{FeIwCuHgBoc@cFaGo@eQaC}KwCiGeCcKuFgIiGmJ{JoRcVwQ_UifAcsA{G{HiGgGqWiQke@kZag@o[ue@qYon@oa@uh@q]yWkQiPiPqHyH{MyLmRoN__AueAooCk_DqfCouCsgAypAqn@iu@q[oe@cQ}WeMwQyG_IyQ{P{RaQ_j@ef@{VwUmO}MoKqH_QiJoi@yTm}Ba`AajAse@gOcGsOoEcFeAgNsBoc@uFgm@qHmOwAmYoAcb@iBkMw@eWsCsOeBkZeEgPcEuKyDak@iXoh@aW{NmHc[yRcn@y`@m`@mVeHmDyKoE_E{AoeAya@sbEi_BiFuB}QoHgPcIaVuLuLqFaNaG_TeJqPgGi[iLaWgJ{R}Gei@oPwj@oP_wAsa@i|@iUgc@aJaXgFwHcBaP_FcJwDwJcFqI{E}f@gYu[}Q{|@sg@gf@s^gm@wd@_TyOoNsIoXsNaO{H}\\qPmf@gUgVwLqi@eXae@sUuDiAkEe@aC?_KbAwC\\qMfBsTnCeMlAgEHeIk@wKaCed@qLk[oI}PyDo@MyD}@qJcD_FoBaEoByPeJaWiNit@sa@qJmF}K}F{D_CaCiC}A{Bs@y@UUIPi@t@MNg@d@eDvBwDbABbCYvBsRth@]xBApA{Rhh@iFfMyKpUq@bBYx@`@VB@", 2);
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
		PedibusItineraryLeg leg4 = newLeg(go3, "4", "4",5000000, "Houston", "1", "as_xDbppsQjDoL|JoSnRsg@bQ}i@rFsS~As@nJh@zD`@vJ|E|_@dS`p@t^~VfOnWrK`e@fLjn@nPz_@pJjl@}Ebh@uErOrFtd@jU~qAfq@p\\dPpDKdEmD|@oJEu]tFiaAtNsfCkFk\\ya@udBaP{f@_CaNDoPvBeN|HqSxVcm@dJqRt[kv@~p@_~AtWqq@rTs^vc@oh@|HwJfDmKa@}h@_NaeCgD{z@nDcg@hPulBtFof@dLe]f_AwwBrWal@xd@ix@tf@c{@vt@opAdQm\\nQ}f@n`@{hAlp@kfB|vA}yCdSki@bBuh@mA}xE_@mlCpAoOnMgi@fSmz@z@_f@Ue{@c@ydB`Feg@nQ_bApMad@lHgRjF{GdNoQdG_U~Nwv@fUwc@bPmXzM_J~e@oWpIgLpGeXfRwcAhIcUxNqQ~y@ocAzPia@xk@gzBhSi\\zj@kt@na@ic@lkBajBri@_i@za@e_@lUeLtk@}XrQw[l[ic@p`@kc@hcBkjBvp@yc@rLcQf^ibAbO_n@zGcy@Us{@kCw_AeCo~@p@uq@|IuaAhSuaBbHa^nb@yp@dGcSxAyXLy\\`JmXhj@woAx[}dApRyn@l\\koAvCkZo@sJmFsQkZen@wWyq@}Z}j@}Nq]oCmVv@up@`Kcf@r`@g~@~Ruc@dZym@|_@uhAhQiq@fDq^Qsx@kCmzBxBiUrD_Mtd@wz@lWqc@tReNr[uJxRsObKaPjU}Rft@qn@~KgUfJkLn_AmbAhlBwzB~r@kx@zY}g@bTqd@x`@gd@v_@c`@~Xa_@fw@wcAtm@ix@vpBohCziAabBx_AmsAlg@ot@xRkRpReKjUwFzl@aDvn@kC~XoE`w@}Q~d@eM|CuEx@_HeC_OqAwMr@qv@~G{hAG}QgBaL{Jk_@m[ag@kVw}@yGu\\oHePoVme@mO}~@{TusCaNkwB}Cep@aFif@i`@ecDsY_bCamBc~O{UesB}JoaBiYk}Egd@acFaEcg@h@_P`S_dAhBySEmUaPa`As[m}AkGkTkHkLqP{UyFePoGkj@iAoo@cBe{AdBwt@nOurBpJ}nB~H_wAgFqf@ci@ukDeWeiBH_v@bKqq@nCee@OigA[u`CpFyy@lBs|@Wu|ECwwGs@}vB}DsQcG_KwXm[cFcOo@{S@c|BZmiG`BkyQTidGlAqvERoP~CwF~r@wBtd@mAfRyIdFkEtL}G~O}@nbCaApeA[pn@a@nEmBpA_EyEupAkFekBc@s\\IuBlCUfB?hJEhMAhHjArKlDhOLxTGbD[AyJGuI~EkAlFD", 3);
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
		PedibusItineraryLeg leg5 = newLeg(go4, "5","5", 6000000, "Dallas", "1", "sihtD|u~eQiMaBvAiNlAyEaOqTah@}l@qQkRin@{t@_IiUSai@sFkaBoKaXerBgYs`@mEck@LmbAxAsr@iDmLoIcM{Gi_@B_t@sHs^aHkZqU_Umj@ePaq@e@gdCeEi~Aq@waBB_N_DwBw[pOi_AhV_nDxzByxAtn@ac@lNex@fBieAh@kjB`AaiBkCiv@bMg{@fTg|ApHqcHjv@yjAfFsoA`@qzAjNwtBlUkdA`Imj@kHsq@Toy@Ngl@~Kgm@hZi}@hKu}Cp_@anArIucBtH{zBtTc|ChMwdA?wk@qGm_Dc@ea@~CiT~I{y@l]qjD|w@wiBdf@{~@pVy\\jCesAxD{iAjCweD`G}wBfc@mq@`Moc@xA_~DpE_pFq]igAsBef@pFcjGr~@oqPfq@}]nHmrAzn@sYdc@mUdj@ki@zuAg]|l@}uDn~BqdAlsAkqBhlD{zFz~JwbEh~DidAzbAycAz{AcoAdgBc`DlaC_mCptB{i@reAwcEtuI_jAv`CsnB|mB}bApz@_lAvw@s{DriCgo@pb@ym@n[idFjmAosB~f@kp@vUks@f_@u|@lYu{C|h@eyFzw@qeBjVcZhAw~@mG{tAaNy`AkVgvB}Kca@@sm@tRyhAzd@cp@~Pe~Bva@__GxdAcrBd_@scA`j@o{Dr{BieFzmEk_@|Uc]bI_pA`Y_lBdb@a}@j[ug@|YiiBheAqhB``AkjEz`Cup@tS}aBd_@ycB`FahD~LciG~k@efA~^gkAzYysB|f@}sBtkBqv@zs@mkAlsAkr@j{@ap@zgAahBdvBoj@lo@eu@xq@odBfy@}eCn_C{Tb]w_@nyAiV`w@exBfxBu~BbgFooAh~Buz@xfAiN~AqZsD{lBoUim@PyrBpSqrAz]koGplBcjC~c@_XkDgi@m_@qc@w\\oZyKoc@cA}v@bKmxBb[}cBl`AkiAtn@et@|Xaj@bFuwDlVwu@rF}_@hNgtB|x@ogCxcAcoA`m@}z@|^uSvQkv@rpB_v@`uBqsArvDgp@xhBiYl^w`BliA}zBvnAyq@x_@idAtz@ik@bd@}ZpKuzAf_@ciBff@q[|B}h@eHkm@tBypAb^}[|Bu|BuTka@]q[bIy_@xGwt@c@mrBmCw`@m@m\\aI_s@eM_uAeAagBfa@afB`n@ytE|iBcfF|nBeSdJwNzReg@vjAycCnyBiTrPk_B|d@spAxWegAxb@esCjl@u`@bIim@nTw\\zIix@jNcUnN}@fK~NpS~DvWzJxl@tSl^R`S_JhU{TnEvAoEj@eCcDW{@P", 4);
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
		PedibusItineraryLeg leg6 = newLeg(go5, "6","6", 7000000, "Amarillo", "1", "in`gEf}zmQoAfH|CnFgNcCzCr@gg@zCylAxS}d@diAyr@x}BwTtoBcoAxtB{aA`aCCn{Ca@nwFn@dkGsGbzFrFbiBeHjeA`E|oBpCtlG_S~jAlAdoBxE~pBp]ty@xDnaAy]x_BgNhdBjC|hCe@ppGiJ`Xi}@nBsoCzYuy@Ks`@hRa|EzkFqsEtbHcm@dfAo{@pk@{~Ex}Ac_AdXexAr`Ago@`v@qVhqAua@zWiqCud@qiCmJmk@vNwxBpaBcrBv_Akl@by@yuAnbCkcA`sAwaEjfDejAjpBquB|{@yh@`h@auAnc@efE|fDu`FlzDosBbyAk[`nAiXfr@ig@~O}fAzFkcAfo@ys@paAuqAvaAklBnmAkkBp_BkvBvcBwaCznAiiFt_D{eB~eAss@hsAc_BleD{aBdiCgeAvqA}sBvqFk}DdkKsbBvjEiqCrgDsuCdbColBdy@ypEz}B_cBp|@ipBnnDwuAxvDai@pvBkh@`sA}SzGin@zUodBr}HqgDbnJka@nzA_JtnAmcAfmHrD~y@e^prB}@d{EmTdsA{z@`s@o|AnnAiZl`@_w@bQkmAbb@yZ~Yy^`hDsdAhpNca@v~EMvhE_FbgDcYp|AQt|Dma@rjCorAv`E{bAzjAmeEdpIudAtlBiC~dDuA`{AiSf|@gnBj{FmkCztHueF`{N{n@tq@sApx@lRrmAwXjx@a}B~sOq_@xfD{DloA}EziBqcB`eAyi@zh@}iAv`DotCpjGo|@rmEgy@zhBuv@thCqa@lpDoLjkBfJhlBpUr_B{RheB}iAzyHyNrdEwq@|cEuk@lfC{Cry@oQfmCua@r|FkjBv_Hk`@z^og@l}Ag|@nvDoNz_CstAfkHas@tyDyx@pmDyl@hzDm_AfbH{uBjlKe|E|tRwaA`aEqr@drBq`CtsF}]tsAooBnuC_Of{AaaA~uBkg@fe@sqAnmC}m@tnA_aAvEev@tL_o@_K{aAkXweBrsCwXt`@inAd\\sjEvu@gxDhg@gnBrnAklB~bBeO~o@a_Dj`DwrBfkBqhDlgBqhBzKgsAfx@qbE|hHqbAtt@aq@vp@}\\`fAaBjmAoKvcCov@`qEwlBn`E}d@vcAil@l{B{NvdCy`@b}Bg~B|fIwbBjnDqn@zpBke@niCmZ|uBc[j}@coChuGitDtuJk^lhScw@jlDebBjnB}z@fbAiaDlzK_dBjtGq_BtlFg}Ch`PwtBluKm~@l~GiiBtmWiy@jvLH`{AwKb\\wKjAeZxTib@{AsW?cW@@b\\Fnh@B~LBrD", 5);
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

		Activity stemAct = newActivity(true, 4, null, null, Activity.Subject.STEM);
		List<Link> urlsA = stemAct.getMaterials();
		Link linkX = newLink("Ecosystems", "http://www.teacherplanet.com/content/food-chainweb");
		urlsA.add(linkX);
		Link linkY = newLink("Human Impact on the Ecosystem", "https://www.youtube.com/watch?v=5eTCZ9L834s");
		urlsA.add(linkY);
		stemAct.setMaterials(urlsA);

		Activity csAct = newActivity(true, 4, null, "Read Sarah Plain and Tall", Activity.Subject.CS);
		List<Link> urls = csAct.getMaterials();
		Link linkA = newLink("Quizlet", "https://quizlet.com/2646281/sarah-plain-and-tall-flash-cards/");
		urls.add(linkA);
		Link linkB = newLink("YouTube - read the story", "https://www.youtube.com/watch?v=00LNxG8LAJg");
		urls.add(linkB);
		Link linkC = newLink("Scholastic", "https://www.scholastic.com/teachers/books/sarah-plain-and-tall-by-patricia-maclachlan/");
		urls.add(linkC);
		csAct.setMaterials(urls);

		Activity selAct = newActivity(true, 4, null, "Managing Worry", Activity.Subject.SEL);
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
		PedibusItineraryLeg leg7 = newLeg(go6, "7","7", 8000000, "Midland-Odessa", "1", "_v_vEraplRB~CtKBnHFvUIlr@vHns@tIf\\xBtGkCx^|C`o@jGjPFvUtJzPtOdBvHNzf@^fLjM~`@bQpd@rMfRz_Avw@dkDnsCrc@tZfk@dZn[vHtqElk@vo@lHzq@LraCF|{AD|^yBbO}GrVoUxjBmfBzfEggDdrBonBvScMtSeF`b@y@~qHH`pE`AlbAs@dlGDb`ECh_@iCzv@eA|iC@n_AGt_@wF|vBm`@hpGckA`o@yOrqB_s@rqEm~Ade@qQfSgOlSqN|K_DfR_@bS|Bdj@|BvsABd}@H`r@dBbbADfj@qAdvAkm@|{Awq@hb@eM`kBe@hjFFdiGGvZsFt^eUpfBirArhB}sAng@gSjwKomDfZuJ`i@yPxKqAzpBqAv[fB|S`LfcArmAvqBp_ChXbZpLtEvaARz~@rC~k@sDlyAuEh|Bs@jWjIfMzLbn@||@|c@vs@d}@zpAz_@nz@fbCbvFpc@jr@vPzLnOxAtl@P~NzF`Yh\\xU|QbS|DhgI\\j{IWpyMD|p@GfQcDpNiIvMgOla@mg@hNyF|`BEf\\kEhYeJz_@qAbaDOzxEKf_AtXrSl@bs@gAz^kMdP]bi@zB`m@`@pc@sIjc@eJ~~@u@xyA?z]dBlt@Mzl@uCllBu@dPhCfZtRxU`SjXbRpJr@xt@{H~ZcHvUaNzSoFzi@EtW|@t]nMli@zQjb@ZtbBDhaAkBffBcBz{@vCbhBDhkBOhaAkAfh@B|_@iEz|@{P`]mHhUkKjoAgn@`nCyrAvu@{^bRmDrtBgUjdGG|cLc@~mDVbj@Kxw@eR`vB_h@nLkCriAQ~U{@~LuDxOmErVMn\\z@fl@FpS~Dx~@rj@`e@hXxKlBntCTpsDNb|MR|V~Ept@`l@v}CfnChj@lh@bUzI~q@fFjTdCzUjKbeCloAdw@ha@te@nOphBzb@naDvv@h}BddAn}KvbFvm@bYfTnTrOlM|d@zS|w@p\\~l@~ErZcD`YuFjRwDvKwB`Ew@p@nFr@zFt@tF|K{BnMe@`m@|InYlDv{@oQzJWlM~Kv`@da@fZnMpyBtx@laFnfBnrB`n@l|E|xBlvA~[ng@rJzPx@pa@|EfnAdk@jeAff@pq@lc@zhCdeBjhAju@`z@np@~pDdyBfwN~vId_DjoB~k@p^pJpDnM|@rOmBrl@mM|qEqaAvrI{iBjd@eL|VyOnYgOvn@_NxmBaa@||Bch@ppGqyAtnIslBjzAs\\xRwBx[dBvi@rL`P|BjOo@lbBm^vc@gKzS_BpZrB~ZqDfc@c@", 6);
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
