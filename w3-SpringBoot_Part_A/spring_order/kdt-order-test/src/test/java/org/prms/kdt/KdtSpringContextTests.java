package org.prms.kdt;

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
public class KdtSpringContextTests {


    // @ContextConfiguration(classes={AppConfiguration.class})가 잘못되었을때 임시로 사용하는 Config
    @Configuration
    @ComponentScan(basePackages = {"org.prms.kdt.order","org.prms.kdt.voucher","org.prms.kdt.configuration"})
    static class Config {
    }

    @Autowired
    ApplicationContext context;

    @Autowired
    OrderService orderService;

    @Autowired
    VoucherRepository voucherRepository;


    @Test
    @DisplayName("applicationContext가 생성되어야 한다")

    public void testApplicationContext() {
        assertThat(context,notNullValue());

    }


    @Test
    @DisplayName("VoucherRepostiory가 빈으로 등록되어 있어야 한다")
    public void testVoucherRepositoryCreation() {
        var bean=context.getBean(VoucherRepository.class);
        assertThat(bean,notNullValue());

    }

    @Test
    @DisplayName("orderService를 사용해서 주문을 생성 할 수 있다.")
    public void testOrderService() {
        //GIVEN
        var fixedAmountVoucher=new FixedAmountVoucher(UUID.randomUUID(),100);
        voucherRepository.insert(fixedAmountVoucher);

        // WHEN
        var order=orderService.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(),200,1)),fixedAmountVoucher.getVoucherId());


        // THEN
        assertThat(order.totalAmount(),is(100L));
        assertThat(order.getVoucher().isEmpty(),is(false));
        assertThat(order.getVoucher().get().getVoucherId(),is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(),is(OrderStatus.ACCEPTED));

    }
}
