package org.prms.kdt.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.prms.kdt.voucher.FixedAmountVoucher;
import org.prms.kdt.voucher.MemoryVoucherRepository;
import org.prms.kdt.voucher.VoucherService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    // stub, 가짜 클래스
    class OrderRepositoryStub implements OrderRepository{
        @Override
        public Order insert(Order order) {
            return null;
        }
    }

    @Test
    @DisplayName("오더가 생성 (stub)")
    void createOrder() {
        // GIVEN
        var voucherRepository=new MemoryVoucherRepository();
        var fixedAmountVoucher=new FixedAmountVoucher(UUID.randomUUID(),100);
        voucherRepository.insert(fixedAmountVoucher);
        var sut=new OrderService(new VoucherService(voucherRepository),new OrderRepositoryStub());

        // WHEN
        var order=sut.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(),200,1)),fixedAmountVoucher.getVoucherId());


        // THEN
        assertThat(order.totalAmount(),is(100L));
        assertThat(order.getVoucher().isEmpty(),is(false));
        assertThat(order.getVoucher().get().getVoucherId(),is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(),is(OrderStatus.ACCEPTED));

    }


    @Test
    @DisplayName("오더가 생성 (mock)")
    void createOrderbyMock() {
        // GIVEN
        // Mocking
        var voucherServiceMock=mock(VoucherService.class);
        var orderRepositoryMock=mock(OrderRepository.class);

        var fixedAMountVoucher=new FixedAmountVoucher(UUID.randomUUID(),100);
        // 실제 when 부분만 동작
        when(voucherServiceMock.getVoucher(fixedAMountVoucher.getVoucherId())).thenReturn(fixedAMountVoucher);

        var sut=new OrderService(voucherServiceMock,orderRepositoryMock);


        // WHEN
        var order=sut.createOrder(
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(),200,1)),
                fixedAMountVoucher.getVoucherId()
        );

        // THEN
        assertThat(order.totalAmount(),is(100L));
        assertThat(order.getVoucher().isEmpty(),is(false));


        // 순서대로 동작되는지 확인 inOrder
        // createOrder메소드에서 순서 확인
        var inOrder=inOrder(voucherServiceMock,orderRepositoryMock);
        inOrder.verify(voucherServiceMock).getVoucher(fixedAMountVoucher.getVoucherId());
        inOrder.verify(orderRepositoryMock).insert(order);
        inOrder.verify(voucherServiceMock).useVoucher(fixedAMountVoucher);


    }

}