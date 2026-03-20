package com.example.internalbooks.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${app.image.storage.local.directory:src/main/resources/static/images/}")
	private String imageDirectory;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String absolutePath = Paths.get(System.getProperty("user.dir"))
				.resolve(imageDirectory)
				.normalize()
				.toUri()
				.toString();

		registry.addResourceHandler("/images/**")
				.addResourceLocations(absolutePath);
	}
}
