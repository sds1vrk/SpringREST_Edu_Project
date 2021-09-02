package org.prms.kdt.voucher;

import java.util.UUID;

public class FixedAmountVoucher implements Voucher {
    private static final long MAX_VOUCHER_AMOUNT=10000;

    private final UUID voucherId;
    private final long amount;
    private final String type="FIXED";

    public FixedAmountVoucher(UUID voucherId, long amount) {
        if (amount==0) throw new IllegalArgumentException("Amount should be positive");
        if (amount<0) throw new IllegalArgumentException("Amount should not be zero");
        if (amount>MAX_VOUCHER_AMOUNT) throw new IllegalArgumentException("Amount should be less than %d".formatted(MAX_VOUCHER_AMOUNT));

        this.voucherId = voucherId;
        this.amount = amount;
    }


    @Override
    public UUID getVoucherId() {
        return voucherId;
    }

    public long discount(long beforeDiscount) {
        var discountAmount=beforeDiscount-amount;

        return (discountAmount<0)?0:discountAmount;

    }

    @Override
    public String toString() {
        return "FixedAmountVoucher{" +
                "voucherId=" + voucherId +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                '}';
    }
}
