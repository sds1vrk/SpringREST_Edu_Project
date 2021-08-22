package org.prms.kdt;

import org.prms.kdt.order.Order;
import org.prms.kdt.voucher.Voucher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// Bean을 정의할 도면이다라고 스프링에게 알려줘야 함. @Configuration
// 각 메소드에 Bean 어노테이션 사용
@Configuration
@ComponentScan(basePackages = {"org.prms.kdt.order","org.prms.kdt.voucher","org.prms.kdt.configuration"})
//@ComponentScan(basePackageClasses = {Order.class, Voucher.class})
public class AppConfiguration {


    @Bean(initMethod = "init")
    public BeanOne beanOne() {
        return new BeanOne();
    }

}

class BeanOne implements InitializingBean {
    public void init() {
        System.out.println("[BeanOne] init called");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[BeanOne] afterPropertiseSet called");

    }
}
