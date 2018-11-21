package sco.climb.domain.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import it.smartcommunitylab.climb.domain.common.GEngineUtils;

@ComponentScan(basePackageClasses = {GEngineUtils.class})
@Configuration
public class TestConfig {

}
