package sco.climb.domain.test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Test;

import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.model.PedibusItineraryLeg;

public class TestVelocity {

	@Test
	public void testVelocityConstants() {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		    
		Template t = velocityEngine.getTemplate("game-template/constants_v1.vm");
		
		Map<String, String> params = new HashMap<>();
		params.put("const_zi_solo_bonus", "1000.0");
		params.put("final_destination", "FineViaggio");
		
		List<PedibusItineraryLeg> legs = new ArrayList<>();
		PedibusItineraryLeg leg = new PedibusItineraryLeg();
		leg.setName("Tappa 1");
		leg.setScore(1000);
		legs.add(leg);
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		System.out.println(writer.toString());
	}
	
	@Test
	public void testVelocityCalendarTrips() {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		    
		Template t = velocityEngine.getTemplate("game-template/calendartrips_v1.vm");
		
		Map<String, String> params = new HashMap<>();
		params.put("const_pandr_distance", "100");
		params.put("const_bus_distance", "100");
		params.put("const_zeroimpact_distance", "100");
		params.put("const_cloudy_bonus", "100");
		params.put("const_rain_bonus", "100");
		params.put("const_snow_bonus", "100");
		
		List<PedibusItineraryLeg> legs = new ArrayList<>();
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("Utils", Utils.class);
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		System.out.println(writer.toString());
	}
	
	@Test
	public void testVelocityLegsBadges() {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		    
		Template t = velocityEngine.getTemplate("game-template/legsbadges_v1.vm");
		
		Map<String, String> params = new HashMap<>();
		params.put("const_zi_solo_bonus", "1000.0");
		params.put("final_destination", "Fine Viaggio");
		
		List<PedibusItineraryLeg> legs = new ArrayList<>();
		PedibusItineraryLeg leg1 = new PedibusItineraryLeg();
		leg1.setObjectId(UUID.randomUUID().toString());
		leg1.setName("Tappa 1");
		leg1.setScore(1000);
		legs.add(leg1);

		PedibusItineraryLeg leg2 = new PedibusItineraryLeg();
		leg2.setObjectId(UUID.randomUUID().toString());
		leg2.setName("Tappa 2");
		leg2.setScore(2000);
		legs.add(leg2);

		PedibusItineraryLeg leg3 = new PedibusItineraryLeg();
		leg3.setObjectId(UUID.randomUUID().toString());
		leg3.setName("Tappa 3");
		leg3.setScore(4000);
		legs.add(leg3);

		Map<String, Boolean> almostReachedMap = new HashMap<>();
		almostReachedMap.put(leg1.getObjectId(), Boolean.FALSE);
		almostReachedMap.put(leg2.getObjectId(), Boolean.FALSE);
		almostReachedMap.put(leg3.getObjectId(), Boolean.TRUE);
		
		VelocityContext context = new VelocityContext();
		context.put("params", params);
		context.put("legList", legs);
		context.put("almostReachedMap", almostReachedMap);
		context.put("Utils", Utils.class);
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		System.out.println(writer.toString());
	}
	
	@Test
	public void testVelocityTempletes() {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		VelocityContext context = new VelocityContext();
		Template t = null;
		StringWriter writer = new StringWriter();
		
		t = velocityEngine.getTemplate("game-template/classtrips_v1.vm");
		t.merge(context, writer);
		System.out.println(writer.toString());

		t = velocityEngine.getTemplate("game-template/challenge_ScuolaSenzAuto_v1.vm");
		t.merge(context, writer);
		System.out.println(writer.toString());

		t = velocityEngine.getTemplate("game-template/pedibus_v1.vm");
		t.merge(context, writer);
		System.out.println(writer.toString());

		t = velocityEngine.getTemplate("game-template/challenge_ViaggiGiornalieri_v1.vm");
		t.merge(context, writer);
		System.out.println(writer.toString());

		t = velocityEngine.getTemplate("game-template/weeklytasks_v1.vm");
		t.merge(context, writer);
		System.out.println(writer.toString());

		t = velocityEngine.getTemplate("game-template/classday_v1.vm");
		t.merge(context, writer);
		System.out.println(writer.toString());
	}
	
}
