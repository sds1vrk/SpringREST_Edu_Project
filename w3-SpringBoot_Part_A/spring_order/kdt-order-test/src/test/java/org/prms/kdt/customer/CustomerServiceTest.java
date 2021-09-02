package org.prms.kdt.customer;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerServiceTest {

    private static final Logger logger= LoggerFactory.getLogger(CustomerServiceTest.class);

    @Configuration
    @EnableTransactionManagement // transaction 을 사용하기 위해 사용
    static class Config {

        @Bean
        public DataSource dataSource() {

//            * EmbeddedData
//            return new EmbeddedDatabaseBuilder()
//                    .generateUniqueName(true)
//                    .setType(EmbeddedDatabaseType.H2)
//                    .setScriptEncoding("UTF-8")
//                    .ignoreFailedDrops(true)
//                    .addScript("schema.sql")
//                    .build();
//            * EmbeddedData

//          * DataSource
            var dataSource=DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt")
                    .username("root")
                    .password("root1234!")
                    .type(HikariDataSource.class)
                    .build();

//           * wix 사용 Mysql 버전 5
//            var dataSource=DataSourceBuilder.create()
//                    .url("jdbc:mysql://localhost:2215/test-order_mgmt")
//                    .username("test")
//                    .password("root1234")
//                    .type(HikariDataSource.class)
//                    .build();



            dataSource.setMaximumPoolSize(1000);
            dataSource.setMinimumIdle(100);
            return dataSource;


        }

        //JDBC Template Bean 등록
        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }


        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }

        @Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
            return new TransactionTemplate(platformTransactionManager);
        }

        @Bean
        public CustomerRepository customerRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
            return new CustomerNamedJdbcRepository(namedParameterJdbcTemplate);
        }

        @Bean
        public CustomerService customerService(CustomerRepository customerRepository) {
            return new CustomerServiceImpl(customerRepository);
        }


    }


//    @Autowired
    Customer newCustomer;


//    EmbeddedMysql embeddedMysql;

    // Before, 테스트하기 전에 테이블 다 지우고 시작
    @BeforeAll
    static void setup() {

//        customerNamedJdbcRepository.deleteAll();



        //wix
//        newCustomer=new Customer(UUID.randomUUID(),"test-user","test-user@naver.com",LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
//        var mysqlConfig=aMysqldConfig(v8_0_11)
//                .withCharset(UTF8)
//                .withPort(2215)
//                .withUser("test","test1234")
//                .withTimeZone("Asia/Seoul")
//                .build();
//
//        embeddedMysql=anEmbeddedMysql(mysqlConfig)
//                .addSchema("test-order_mgmt",classPathScript("schema.sql"))
//                .start();

    }

//    @AfterAll
//    void cleanUp() {
//        embeddedMysql.stop();
//    }

//    @AfterEach
//    void dataCleanup() {
//        customerRepository.deleteAll();
//    }

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @DisplayName("다건 추가 테스트")
    void multiInsertTest() {

//        customerRepository.deleteAll();


        var customers=List.of(
                new Customer(UUID.randomUUID(),"a","a@gmail.com",LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)),
                new Customer(UUID.randomUUID(),"b","b@gmail.com",LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
        );
        customerService.createCustomers(customers);
        var allCustomersRetrieved=customerRepository.findAll();
        logger.info("All {}",allCustomersRetrieved.toString());
        logger.info("get 0 {}",customers.get(0).toString());
        logger.info("get 1 {}",customers.get(1).toString());

        assertThat(allCustomersRetrieved.size(),is(2));
        assertThat(allCustomersRetrieved,containsInAnyOrder(samePropertyValuesAs(customers.get(0)),samePropertyValuesAs(customers.get(1))));
    }

    @Test
    @DisplayName("다건 추가 실패시 전체 트랜잭션이 롤백되어야 한다")
    void multiInsertRollbackTest() {

        customerRepository.deleteAll();


        var customers=List.of(
                new Customer(UUID.randomUUID(),"c","c@gmail.com",LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)),
                new Customer(UUID.randomUUID(),"d","c@gmail.com",LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
        );

        try {
            customerService.createCustomers(customers);

        }catch (DataAccessException e) {
            logger.error("while creating Error",e.getStackTrace());

        }


        var allCustomersRetrieved=customerRepository.findAll();
        logger.info("All {}",allCustomersRetrieved.toString());
//        logger.info("get 0 {}",customers.get(0).toString());
//        logger.info("get 1 {}",customers.get(1).toString());

        assertThat(allCustomersRetrieved.size(),is(0));
        assertThat(allCustomersRetrieved.isEmpty(),is(true));
        assertThat(allCustomersRetrieved,not(containsInAnyOrder(samePropertyValuesAs(customers.get(0)),samePropertyValuesAs(customers.get(1)))));
    }











}