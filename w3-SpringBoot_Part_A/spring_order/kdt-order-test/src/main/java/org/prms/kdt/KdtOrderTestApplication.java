package org.prms.kdt;

import org.prms.kdt.order.OrderProperties;
import org.prms.kdt.voucher.FixedAmountVoucher;
import org.prms.kdt.voucher.JdbcVoucherRepository;
import org.prms.kdt.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.text.MessageFormat;
import java.util.UUID;

@SpringBootApplication
@ComponentScan(basePackages = {"org.prms.kdt.order","org.prms.kdt.voucher","org.prms.kdt.configuration"})
public class KdtOrderTestApplication {
	private static final Logger logger= LoggerFactory.getLogger(OrderTester.class);

	public static void main(String[] args) {
//
		var applicationContext=SpringApplication.run(KdtOrderTestApplication.class,args);
		var orderProperties=applicationContext.getBean(OrderProperties.class);

		// Spring Boot는 기본적으로 application.properties와 application.yaml을 검색

		logger.error("logger name ==>{} {} {}",logger.getName(),2,3); // 괄호를 쓰면 자동으로 치환 됨
		logger.warn("version -> {}",orderProperties.getVersion());
		logger.warn("minimumAmount -> {}",orderProperties.getMinimumOrderAmount());
		logger.warn("supportVendors -> {}",orderProperties.getSupportVendors());
		logger.warn("description -> {}",orderProperties.getDescription());



	}

}
