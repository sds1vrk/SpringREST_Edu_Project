package org.prms.kdt;

import org.apache.logging.log4j.message.Message;
import org.prms.kdt.order.OrderItem;
import org.prms.kdt.order.OrderService;
import org.prms.kdt.voucher.FixedAmountVoucher;
import org.prms.kdt.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

public class OrderTester {

    public static void main(String[] args) {

        // AppConfiguration을 사용하기 위해
        var applicationContext=new AnnotationConfigApplicationContext(AppConfiguration.class);

        var customerId= UUID.randomUUID();


        // 할인 적용
        var voucherRepository=BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(),VoucherRepository.class,"memory");
        var voucherRepository2=BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(),VoucherRepository.class,"memory");
//        var voucherRepository=applicationContext.getBean(voucherRepository.class);  // qualifier를 안쓸경우 이거 사용
        var voucher=voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(),10L));

        // 만들어진 객체가 실제 같은지 확인
        System.out.println(MessageFormat.format("voucherRepository {0}",voucherRepository));
        System.out.println(MessageFormat.format("voucherRepository {0}",voucherRepository2));
        System.out.println(MessageFormat.format("voucherRepository ==voucherRepository2 ==> {0}",voucherRepository==voucherRepository2));



        var orderService=applicationContext.getBean(OrderService.class);
        var order=orderService.createOrder(customerId,new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(),100L,1));
        }},voucher.getVoucherId());

        Assert.isTrue(order.totalAmount()==90L, MessageFormat.format("totalAMount {0} is not 90L",order.totalAmount()));


        // 컨테이너에 등록된 모든 빈이 소멸
        applicationContext.close();


    }
}
