package org.prms.kdt.customer;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 클래스단위로 Instance 실행됨 -> 객체 유지
class CustomerJdbcRepositoryTest {

    @Configuration
    @ComponentScan(basePackages = {"org.prms.kdt.customer"})
    static class Config {

        @Bean
        public DataSource dataSource() {
            var dataSource=DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt")
                    .username("root")
                    .password("root1234!")
                    .type(HikariDataSource.class)
                    .build();
            dataSource.setMaximumPoolSize(1000);
            dataSource.setMinimumIdle(100);
            return dataSource;
        }
    }



    // 만들어진 빈 연결
    @Autowired
    CustomerJdbcRepository customerJdbcRepository;

    // 만들어진 빈 연결
    @Autowired
    DataSource dataSource;

    Customer newCustomer;

    // Before, 테스트하기 전에 테이블 다 지우고 시작
    @BeforeAll
    void setup() {
        newCustomer=new Customer(UUID.randomUUID(),"test-user","test-user@naver.com",LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        customerJdbcRepository.deleteAll();
    }


    @Test
    @Order(1)
    public void testHikariConnectionPool() {
        assertThat(dataSource.getClass().getName(),is("com.zaxxer.hikari.HikariDataSource"));
    }

    @Test
    @Order(2)
    @DisplayName("고객 추가")
    public void testInsert()  {
        customerJdbcRepository.insert(newCustomer);
        System.out.println("newCustomer==>"+newCustomer.getCustomerId());
        System.out.println("newCustomer==>"+newCustomer.getCreatedAt());
        var retrieveCustomer=customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrieveCustomer.isEmpty(),is(false));
        assertThat(retrieveCustomer.get(),samePropertyValuesAs(newCustomer));
    }


    @Test
    @Order(3)
    @DisplayName("전체 고객 조회")
    public void testFindAll()  {
        var customers=customerJdbcRepository.findAll();
        assertThat(customers.isEmpty(),is(false));
//        Thread.sleep(10000);
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 고객을 조회")
    public void testFindByName()   {
        var customers=customerJdbcRepository.findByName(newCustomer.getName());
        assertThat(customers.isEmpty(),is(false));
//        Thread.sleep(10000);

        var unknown=customerJdbcRepository.findByName("unknown-user");
        assertThat(unknown.isEmpty(),is(true));
//        Thread.sleep(10000);
    }



    @Test
    @Order(5)
    @DisplayName("이메일로 고객을 조회")
    public void testFindByEmail(){
        var customers=customerJdbcRepository.findByEmail(newCustomer.getEmail());
        assertThat(customers.isEmpty(),is(false));
//        Thread.sleep(10000);

        var unknown=customerJdbcRepository.findByEmail("unkonwnr@gmail.com");
        assertThat(unknown.isEmpty(),is(true));
//        Thread.sleep(10000);
    }


    @Test
    @Order(6)
    @DisplayName("아이디로 고객을 수정")
    public void testUpdate(){

        newCustomer.changeName("updated-user");
        customerJdbcRepository.update(newCustomer);

        var all=customerJdbcRepository.findAll();
        assertThat(all,hasSize(1));
        assertThat(all,everyItem(samePropertyValuesAs(newCustomer)));

        var retrieveCustomer=customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrieveCustomer.isEmpty(),is(false));
        assertThat(retrieveCustomer.get(),samePropertyValuesAs(newCustomer));

    }







}