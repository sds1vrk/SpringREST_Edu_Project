package org.prms.kdt;

import org.prms.kdt.configuration.AppConfiguration;
import org.prms.kdt.order.OrderItem;
import org.prms.kdt.order.OrderProperties;
import org.prms.kdt.order.OrderService;
import org.prms.kdt.voucher.FixedAmountVoucher;
import org.prms.kdt.voucher.JdbcVoucherRepository;
import org.prms.kdt.voucher.*;
import org.prms.kdt.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderTester {
    // 로거를 만들고 로거 명을 지정한다. 로거 명은 보통 클래스로 지정
    // 패키지명을 기준으로도 가능
    // org.prms.kdt.order => INFO로 지정
    // org.prms.kdt.voucher ==> WARN
    private static final Logger logger= LoggerFactory.getLogger(OrderTester.class);


    public static void main(String[] args) throws IOException {

        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);

        // AppConfiguration을 사용하기 위해
        var applicationContext=new AnnotationConfigApplicationContext();

        // Profile 사용
        applicationContext.register(AppConfiguration.class);
        var environmnet=applicationContext.getEnvironment();
//        environmnet.setActiveProfiles("dev");         // local -> memory /  dev -> jdbc
        applicationContext.refresh(); //Profile 적용될수 있게 refresh




//        var version=environmnet.getProperty("kdt.version");
//        var minimumOrderAmount=environmnet.getProperty("kdt.minimum-order-amount",Integer.class); // Integer로 받음
//        var supportVendors=environmnet.getProperty("kdt.support-vendors", List.class); // List로 받음
//        var description=environmnet.getProperty("kdt.description",List.class);


        // OrderProperties.class에서 Bean으로 등록된 것을 사용
        var orderProperties=applicationContext.getBean(OrderProperties.class);
        logger.error("logger name ==>{} {} {}",logger.getName(),2,3); // 괄호를 쓰면 자동으로 치환 됨
        logger.warn("version -> {}",orderProperties.getVersion());
        logger.warn("minimumAmount -> {}",orderProperties.getMinimumOrderAmount());
        logger.warn("supportVendors -> {}",orderProperties.getSupportVendors());
        logger.warn("description -> {}",orderProperties.getDescription());


        //Reource
//        var resource=applicationContext.getResource("application.yaml");
//        var resource=applicationContext.getResource("file:temp/sample.txt"); // 현재 폴더 위치에서 찾음 // 배포에서
        var resource3=applicationContext.getResource("https://stackoverflow.com"); // 현재 폴더 위치에서 찾음 // 배포에서
        var readableByteChannel=Channels.newChannel(resource3.getURL().openStream());
        var bufferedReader=new BufferedReader(Channels.newReader(readableByteChannel, StandardCharsets.UTF_8));
        var contents=bufferedReader.lines().collect(Collectors.joining("\n"));
//        System.out.println(contents);

//        System.out.println(MessageFormat.format("Resource->{0}", resource.getClass().getCanonicalName()));
//        var file=resource.getFile();
//        var strings=Files.readAllLines(file.toPath());
//        System.out.println(strings.stream().reduce("",(a,b)->a+"\n"+b));



        var customerId= UUID.randomUUID();

        // 할인 적용
        var voucherRepository=BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(),VoucherRepository.class,"memory");
//        var voucherRepository2=BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(),VoucherRepository.class,"memory");


//        var voucherRepository=applicationContext.getBean(VoucherRepository.class);  // qualifier를 안쓸경우 이거 사용
        var voucher=voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(),10L));
//
//        System.out.println(MessageFormat.format("is JDBC repo -> {0}",voucherRepository instanceof JdbcVoucherRepository));
//        System.out.println(MessageFormat.format("is JDBC repo -> {0}",voucherRepository.getClass().getCanonicalName()));

        // 만들어진 객체가 실제 같은지 확인
//        System.out.println(MessageFormat.format("voucherRepository {0}",voucherRepository));
//        System.out.println(MessageFormat.format("voucherRepository {0}",voucherRepository2));
//        System.out.println(MessageFormat.format("voucherRepository ==voucherRepository2 ==> {0}",voucherRepository==voucherRepository2));



        var orderService=applicationContext.getBean(OrderService.class);
        var order=orderService.createOrder(customerId,new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(),100L,1));
        }},voucher.getVoucherId());

        Assert.isTrue(order.totalAmount()==90L, MessageFormat.format("totalAMount {0} is not 90L",order.totalAmount()));


        // 컨테이너에 등록된 모든 빈이 소멸
        applicationContext.close();


    }
}
