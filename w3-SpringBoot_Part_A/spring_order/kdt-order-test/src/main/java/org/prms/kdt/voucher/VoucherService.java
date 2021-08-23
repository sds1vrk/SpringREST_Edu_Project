package org.prms.kdt.voucher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
public class VoucherService {


    private VoucherRepository voucherRepository;


//    @Autowired
    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

//    @Autowired
//    public void setVoucherRepository(VoucherRepository voucherRepository) {
//        this.voucherRepository = voucherRepository;
//    }



//    public VoucherService(VoucherRepository voucherRepository) {
//        this.voucherRepository = voucherRepository;
//    }



    public Voucher getVoucher(UUID voucherID) {
        // voucherID를 찾지 못하는 경우에는 Throw로 날려서 Exception 처리
        return voucherRepository
                .findById(voucherID)
                .orElseThrow(()->new RuntimeException(MessageFormat.format("Can not find a Voucher for {0}",voucherID)));

    }

    public void useVoucher(Voucher voucher) {

    }




}
