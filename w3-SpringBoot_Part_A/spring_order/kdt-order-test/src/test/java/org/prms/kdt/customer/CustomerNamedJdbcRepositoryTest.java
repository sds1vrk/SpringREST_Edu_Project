package org.prms.kdt.customer;

import com.wix.mysql.EmbeddedMysql;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.prms.kdt.OrderTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;



@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 클래스단위로 Instance 실행됨 -> 객체 유지
class CustomerNamedJdbcRepositoryTest {

    private static final Logger logger= LoggerFactory.getLogger(CustomerNamedJdbcRepositoryTest.class);

    @Configuration
    @ComponentScan(basePackages = {"org.prms.kdt.customer"})
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

    }


//    @Autowired
//    CustomerJdbcRepository customerJdbcRepository;

    // 만들어진 빈 연결
    @Autowired
    CustomerNamedJdbcRepository customerNamedJdbcRepository;

    // 만들어진 빈 연결
    @Autowired
    DataSource dataSource;

    Customer newCustomer;

//    EmbeddedMysql embeddedMysql;

    // Before, 테스트하기 전에 테이블 다 지우고 시작
    @BeforeAll
    void setup() {

        newCustomer=new Customer(UUID.randomUUID(),"test-user","test-user@naver.com",LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        customerNamedJdbcRepository.deleteAll();



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


    @Test
    @Order(1)
    public void testHikariConnectionPool() {
        assertThat(dataSource.getClass().getName(),is("com.zaxxer.hikari.HikariDataSource"));
    }

    @Test
    @Order(2)
    @DisplayName("고객 추가")
    public void testInsert()  {

        try {
            customerNamedJdbcRepository.insert(newCustomer);
        }catch (BadSqlGrammarException e) {
            logger.error("Got BadSqlGrammarException -> {}",e.getSQLException().getErrorCode(),e);

        }


//        customerNamedJdbcRepository.insert(newCustomer);
        System.out.println("newCustomer==>"+newCustomer.getCustomerId());
        System.out.println("newCustomer==>"+newCustomer.getCreatedAt());
        var retrieveCustomer=customerNamedJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrieveCustomer.isEmpty(),is(false));
        assertThat(retrieveCustomer.get(),samePropertyValuesAs(newCustomer));
    }


    @Test
    @Order(3)
    @DisplayName("전체 고객 조회")
    public void testFindAll()  {
        var customers=customerNamedJdbcRepository.findAll();
        assertThat(customers.isEmpty(),is(false));
//        Thread.sleep(10000);
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 고객을 조회")
    public void testFindByName()   {
        var customers=customerNamedJdbcRepository.findByName(newCustomer.getName());
        assertThat(customers.isEmpty(),is(false));
//        Thread.sleep(10000);

        var unknown=customerNamedJdbcRepository.findByName("unknown-user");
        assertThat(unknown.isEmpty(),is(true));
//        Thread.sleep(10000);
    }



    @Test
    @Order(5)
    @DisplayName("이메일로 고객을 조회")
    public void testFindByEmail(){
        var customers=customerNamedJdbcRepository.findByEmail(newCustomer.getEmail());
        assertThat(customers.isEmpty(),is(false));
//        Thread.sleep(10000);

        var unknown=customerNamedJdbcRepository.findByEmail("unkonwnr@gmail.com");
        assertThat(unknown.isEmpty(),is(true));
//        Thread.sleep(10000);
    }


    @Test
    @Order(6)
    @DisplayName("아이디로 고객을 수정")
    public void testUpdate(){

        newCustomer.changeName("updated-user");
        customerNamedJdbcRepository.update(newCustomer);

        var all=customerNamedJdbcRepository.findAll();
        assertThat(all,hasSize(1));
        assertThat(all,everyItem(samePropertyValuesAs(newCustomer)));

        var retrieveCustomer=customerNamedJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrieveCustomer.isEmpty(),is(false));
        assertThat(retrieveCustomer.get(),samePropertyValuesAs(newCustomer));

    }







}