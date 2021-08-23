package org.prms.kdt.order;

import org.prms.kdt.configuration.VersionProvider;
import org.prms.kdt.voucher.VoucherService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;



@Service
public class OrderService {
    // DI - 외부에 의해 주입받음 (생성자로부터)
    private final VoucherService voucherService;
    private final OrderRepository orderRepository;

    // Property에서 version을 받아 주입
    private final VersionProvider versionProvider;


    public OrderService(VoucherService voucherService, OrderRepository orderRepository, VersionProvider versionProvider) {
        this.voucherService = voucherService;
        this.orderRepository = orderRepository;
        this.versionProvider=versionProvider;
    }

    // Voucher가 없는 경우
    public Order createOrder(UUID customerId, List<OrderItem> orderItems) {

        var order= new Order(UUID.randomUUID(),customerId,orderItems);
        // order 정보 저장
        return orderRepository.insert(order);


    }

    // Voucher가 잇는 경우
    public Order createOrder(UUID customerId, List<OrderItem> orderItems, UUID voucherId) {

        versionProvider.getVersion();

        var voucher=voucherService.getVoucher(voucherId);
        var order= new Order(UUID.randomUUID(),customerId,orderItems,voucher);
        // order 정보 저장
        orderRepository.insert(order);

        // voucher를 재사용못하게 하는 useVoucher를 만듦
        voucherService.useVoucher(voucher);
        return order;
    }

}
