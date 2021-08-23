package org.prms.kdt;

import org.prms.kdt.order.OrderProperties;
import org.prms.kdt.voucher.FixedAmountVoucher;
import org.prms.kdt.voucher.JdbcVoucherRepository;
import org.prms.kdt.voucher.VoucherRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.text.MessageFormat;
import java.util.UUID;

@SpringBootApplication
@ComponentScan(basePackages = {"org.prms.kdt.order","org.prms.kdt.voucher","org.prms.kdt.configuration"})
public class KdtOrderTestApplication {

	public static void main(String[] args) {
		var springApplication=new SpringApplication(KdtOrderTestApplication.class);
//		springApplication.setAdditionalProfiles("local");
		var applicationContext=springApplication.run(args);
//		var applicationContext =SpringApplication.run(KdtOrderTestApplication.class, args);


		var orderProperties=applicationContext.getBean(OrderProperties.class);
		System.out.println(MessageFormat.format("version -> {0}",orderProperties.getVersion()));
		System.out.println(MessageFormat.format("minimumAmount -> {0}",orderProperties.getMinimumOrderAmount()));
		System.out.println(MessageFormat.format("supportVendors -> {0}",orderProperties.getSupportVendors()));
		System.out.println(MessageFormat.format("description -> {0}",orderProperties.getDescription()));



		var customerId= UUID.randomUUID();
		var voucherRepository=applicationContext.getBean(VoucherRepository.class);  // qualifier를 안쓸경우 이거 사용
		var voucher=voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(),10L));

		System.out.println(MessageFormat.format("is JDBC repo -> {0}",voucherRepository instanceof JdbcVoucherRepository));
		System.out.println(MessageFormat.format("is JDBC repo -> {0}",voucherRepository.getClass().getCanonicalName()));

	}

}
