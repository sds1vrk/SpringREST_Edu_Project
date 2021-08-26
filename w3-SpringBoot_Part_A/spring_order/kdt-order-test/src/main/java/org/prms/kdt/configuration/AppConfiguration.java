package org.prms.kdt.configuration;

import org.prms.kdt.order.Order;
import org.prms.kdt.voucher.Voucher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// Bean을 정의할 도면이다라고 스프링에게 알려줘야 함. @Configuration
// 각 메소드에 Bean 어노테이션 사용
@Configuration
@ComponentScan(basePackages = {"org.prms.kdt.order","org.prms.kdt.voucher","org.prms.kdt.configuration"})
//@ComponentScan(basePackageClasses = {Order.class, Voucher.class})

//@PropertySource("application.properties")
@PropertySource(value = "application.yaml",factory =YamlPropertiesFactory.class)
@EnableConfigurationProperties
public class AppConfiguration {


}

