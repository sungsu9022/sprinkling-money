package com.kakaopay.sprinklingmoney.config;

import com.kakaopay.sprinklingmoney.ApplicationPackageRoot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by seongsuPark on 2018. 9. 8..
 */
@Configuration
@ComponentScan(basePackageClasses = ApplicationPackageRoot.class)
@EnableAspectJAutoProxy
public class RootConfig {
}
