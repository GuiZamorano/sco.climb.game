package sco.climb.domain.test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.smartcommunitylab.climb.domain.common.GEngineUtils;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;
import it.smartcommunitylab.climb.domain.model.gamification.IncrementalClassificationDTO;
import it.smartcommunitylab.climb.domain.model.gamification.RuleValidateDTO;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource("classpath:application.properties")
public class TestGE {
	
	@Autowired
	private GEngineUtils gengineUtils;
	
	private String gameId = "5b4c5d33e4b0b12fd6fe03cf";
	
	@Test
	public void testValidationClassday() throws Exception {
		Map<String, String> params = new HashMap<>();
		List<PedibusItineraryLeg> legs = new ArrayList<>();

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/classday_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}
	
	@Test
	public void testValidationWeeklyTask() throws Exception {
		Map<String, String> params = new HashMap<>();
		List<PedibusItineraryLeg> legs = new ArrayList<>();

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/weeklytasks_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}

	@Test
	public void testValidationViaggiGiornalieri() throws Exception {
		Map<String, String> params = new HashMap<>();
		List<PedibusItineraryLeg> legs = new ArrayList<>();

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/challenge_ViaggiGiornalieri_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}

	@Test
	public void testValidationPedibus() throws Exception {
		Map<String, String> params = new HashMap<>();
		List<PedibusItineraryLeg> legs = new ArrayList<>();

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/pedibus_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}

	@Test
	public void testValidationScuolaSenzAuto() throws Exception {
		Map<String, String> params = new HashMap<>();
		List<PedibusItineraryLeg> legs = new ArrayList<>();

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/challenge_ScuolaSenzAuto_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}

	@Test
	public void testValidationClassTrips() throws Exception {
		Map<String, String> params = new HashMap<>();
		List<PedibusItineraryLeg> legs = new ArrayList<>();

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/classtrips_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}

	@Test
	public void testValidationCalendarTrips() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("const_pandr_distance", "100");
		params.put("const_bus_distance", "100");
		params.put("const_zeroimpact_distance", "100");
		params.put("const_cloudy_bonus", "100");
		params.put("const_rain_bonus", "100");
		params.put("const_snow_bonus", "100");
		
		List<PedibusItineraryLeg> legs = new ArrayList<>();

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/calendartrips_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}

	@Test
	public void testValidationLegsBadges() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("final_destination", "Trento e dintorni");
		
		List<PedibusItineraryLeg> legs = new ArrayList<>();
		PedibusItineraryLeg leg1 = new PedibusItineraryLeg();
		leg1.setName("Parco Adamello Brenta");
		leg1.setScore(70000);
		legs.add(leg1);
		PedibusItineraryLeg leg2 = new PedibusItineraryLeg();
		leg2.setName("Trento e dintorni");
		leg2.setScore(3700000);
		legs.add(leg2);

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		Template t = velocityEngine.getTemplate("game-template/legsbadges_v1.vm");
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
		ruleValidateDTO.setRule(writer.toString());
		gengineUtils.validateRule(gameId, ruleValidateDTO);
	}
	
//	@Test
//	public void testValidationConstants() throws Exception {
//		Map<String, String> params = new HashMap<>();
//		params.put("const_zi_solo_bonus", "100");
//		params.put("const_school_name", "Scuola Schmid 2A-B-C");
//		params.put("const_number_of_teams", "4");
//		params.put("const_NoCarDayClass_bonus", "100");
//		params.put("const_ZeroImpactDayClass_bonus", "100");
//		params.put("const_weekly_nominal_distance", "100");
//		params.put("final_destination", "Trento");
//		
//		List<PedibusItineraryLeg> legs = new ArrayList<>();
//		PedibusItineraryLeg leg1 = new PedibusItineraryLeg();
//		leg1.setName("Parco Adamello Brenta");
//		leg1.setScore(70000);
//		legs.add(leg1);
//
//		PedibusItineraryLeg leg2 = new PedibusItineraryLeg();
//		leg2.setName("Trento");
//		leg2.setScore(3700000);
//		legs.add(leg2);
//
//		VelocityEngine velocityEngine = new VelocityEngine();
//		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
//		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
//		velocityEngine.init();
//		
//		VelocityContext context = new VelocityContext();
//		context.put("params", params);
//		context.put("legList", legs);
//		context.put("Utils", Utils.class);
//		
//		Template t = velocityEngine.getTemplate("game-template/constants_v1.vm");
//		
//		StringWriter writer = new StringWriter();
//		t.merge(context, writer);
//
//		RuleValidateDTO ruleValidateDTO = new RuleValidateDTO();
//		ruleValidateDTO.setRule(writer.toString());
//		gengineUtils.validateRule(gameId, ruleValidateDTO);
//	}
	
	@Test
	public void testClassification() throws Exception {
		IncrementalClassificationDTO classificationDTO = new IncrementalClassificationDTO();
		classificationDTO.setClassificationName("team classification weekly");
		classificationDTO.setName("team classification weekly");
		classificationDTO.setItemType("total_distance");
		classificationDTO.setPeriodName("weekly");
		classificationDTO.setItemsToNotificate(10);
		classificationDTO.setType("incremental");
		gengineUtils.createTask(gameId, classificationDTO);
	}

}

