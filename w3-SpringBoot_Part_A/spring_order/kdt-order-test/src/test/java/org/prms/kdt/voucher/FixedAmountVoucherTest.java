package org.prms.kdt.voucher;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FixedAmountVoucherTest {

    private static final Logger logger= LoggerFactory.getLogger(FixedAmountVoucherTest.class);

    // Before All
    @BeforeAll
    static void setup() {
        logger.info("@BeforeAll - run once");
    }
    @BeforeEach
    void init() {
        logger.info("@BeforeEach - run before each test method");
    }



    @Test
    @DisplayName("기본적인 assertEqual 테스트 ")
    // 테스트 메소드는 void 이여야한다.
    void testAssertEquals() {
        // 기대되는 값, 실제 값
        assertEquals(2,1+1);
    }

    @Test
    @DisplayName("주어진 금액만큼 할인을 해야한다.")
    void testdiscount() {
        // 클래스 테스트
        var sut=new FixedAmountVoucher(UUID.randomUUID(),100);

        assertEquals(900,sut.discount(1000));

    }

    @Test
    @DisplayName("할인 금액은 마이너스가 될수 없다. ")
    //  @Disabled 테스트가 동자하지 않음
    void testWithMinus() {
        // 클래스 테스트
        assertThrows(IllegalArgumentException.class,()->new FixedAmountVoucher(UUID.randomUUID(),-100));
    }


    @Test
    @DisplayName("디스카운트 된 금액은 마이너스가 될 수 없다. ")
        //  @Disabled 테스트가 동자하지 않음
    void testMinDiscountedAMount() {
        // 클래스 테스트
        var sut=new FixedAmountVoucher(UUID.randomUUID(),1000);
        assertEquals(0,sut.discount(900));
    }



    @Test
    @DisplayName("유효한 할인 금액으로만 생성 할 수 있다.")
        //  @Disabled 테스트가 동자하지 않음
    void testVoucherCreateion() {

        // 다양한 변수를 줘서 테스트 할때 사용 asertAll
        assertAll("FixedAmountVoucher creation",
                ()-> assertThrows(IllegalArgumentException.class,()->new FixedAmountVoucher(UUID.randomUUID(),0)),
                ()-> assertThrows(IllegalArgumentException.class,()->new FixedAmountVoucher(UUID.randomUUID(),-100)),
                ()-> assertThrows(IllegalArgumentException.class,()->new FixedAmountVoucher(UUID.randomUUID(),10000000))
                );
    }




}