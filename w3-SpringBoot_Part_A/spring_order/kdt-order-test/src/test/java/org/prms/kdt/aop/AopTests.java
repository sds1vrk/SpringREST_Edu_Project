package org.prms.kdt.aop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.prms.kdt.configuration.AppConfiguration;
import org.prms.kdt.order.OrderItem;
import org.prms.kdt.order.OrderService;
import org.prms.kdt.order.OrderStatus;
import org.prms.kdt.voucher.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

// JUNIT 5에서 이용되어야 할 extend
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes={AppConfiguration.class})

@SpringJUnitConfig // @ExtendWith(SpringExtension.class)와 @ContextConfiguration를 합친것
@ActiveProfiles("test") //Profile 적용
public class AopTests {


    // @ContextConfiguration(classes={AppConfiguration.class})가 잘못되었을때 임시로 사용하는 Config
    @Configuration
    @ComponentScan(basePackages = {"org.prms.kdt.voucher","org.prms.kdt.aop"})

    @EnableAspectJAutoProxy
    static class Config {
    }

    @Autowired
    ApplicationContext context;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherService voucherService;


    @Test
    @DisplayName("AOP 테스트")
    public void testOrderService() {
        //GIVEN
        var fixedAmountVoucher=new FixedAmountVoucher(UUID.randomUUID(),100);
        voucherRepository.insert(fixedAmountVoucher);

//        voucherService.getVoucher(fixedAmountVoucher.getVoucherId());
//        VoucherService voucherService=new VoucherService(voucherRepository);
//        voucherService.getVoucher(fixedAmountVoucher.getVoucherId());



    }

}
