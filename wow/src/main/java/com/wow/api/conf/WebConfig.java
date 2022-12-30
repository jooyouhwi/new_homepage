package com.wow.api.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	 @Bean(name="JSONView")
	 public MappingJackson2JsonView JSONView(){
	        return new MappingJackson2JsonView();
	    }

	 
}
