package org.prms.kdt.voucher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
public class HamcrestAssertionTests {

    @Test
    @DisplayName("여러 hamcrest matcher test")
    void hamcrestTest() {
        assertEquals(2,1+1);
        // hamcrest 기능 assertThat(실제값, 기대값)
        assertThat(1+1,equalTo(2));
        assertThat(1+1,is(2));
        assertThat(1+1,anyOf(is(1),is(2))); // 기대값이 1 or 2 ==> anyOf


        //NOT 테스트
        assertNotEquals(1,1+1);
        assertThat(1+1,not(equalTo(1)));
    }
    @Test
    @DisplayName("컬렉션에 대한 matcher 테스트")
    void hamcrestListMatcherTest() {
        var prices= List.of(2,3,4);
        assertThat(prices,hasSize(3));
        assertThat(prices,everyItem(greaterThan(1))); // Item을 돌면서 원소가 1보다 크냐?
        assertThat(prices,containsInAnyOrder(3,4,2)); // 3,4,2를 포함하는지
        assertThat(prices,hasItem(greaterThanOrEqualTo(2))); // 2가 존재하는지
    }

}
