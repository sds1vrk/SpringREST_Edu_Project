package org.prms.kdt.voucher;

import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository {

    //Optional --> Null값 방지
    Optional<Voucher> findById(UUID voucherId);


    Voucher insert(Voucher voucher);



}
