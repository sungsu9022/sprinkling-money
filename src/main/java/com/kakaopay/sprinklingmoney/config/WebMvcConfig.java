package com.kakaopay.sprinklingmoney.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.kakaopay.sprinklingmoney.app.common.interceptor.SprinklingMoneyInterceptor;
import com.kakaopay.sprinklingmoney.app.common.resolver.MessageRoomIdArgumentResolver;
import com.kakaopay.sprinklingmoney.app.common.resolver.UserIdArgumentResolver;
import com.kakaopay.sprinklingmoney.app.messageroom.MessageRoomService;
import com.kakaopay.sprinklingmoney.app.user.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
	private final UserService userService;
	private final MessageRoomService messageRoomService;

	// resourceHandlers
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
			.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	// interceptors
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sprinklingMoneyInterceptor())
			.addPathPatterns("/money/sprinkling")
			.addPathPatterns("/money/sprinkling/**");
	}


	@Bean
	public SprinklingMoneyInterceptor sprinklingMoneyInterceptor() {
		SprinklingMoneyInterceptor interceptor = new SprinklingMoneyInterceptor(userService, messageRoomService);
		return interceptor;
	}

	// argumentResolvers
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolverList) {
		argumentResolverList.add(userIdArgumentResolver());
		argumentResolverList.add(messageRoomIdArgumentResolver());
	}

	@Bean
	public UserIdArgumentResolver userIdArgumentResolver() {
		return new UserIdArgumentResolver();
	}

	@Bean
	public MessageRoomIdArgumentResolver messageRoomIdArgumentResolver() {
		return new MessageRoomIdArgumentResolver();
	}

	// Converters
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(mappingJackson2HttpMessageConverter());
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		return new MappingJackson2HttpMessageConverter(objectMapper());
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new ParameterNamesModule());
		objectMapper.registerModule(new Jdk8Module());
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return objectMapper;
	}


}