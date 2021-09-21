package org.prms.kdt.servlet;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.thoughtworks.xstream.XStream;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.tomcat.jni.Local;
import org.prms.kdt.configuration.AppConfiguration;
import org.prms.kdt.customer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {
    private static final Logger logger= LoggerFactory.getLogger(KdtWebApplicationInitializer.class);




    @EnableWebMvc
    @Configuration
    @ComponentScan(basePackages ="org.prms.kdt.customer",
    includeFilters = @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE,value= CustomerController.class),
    useDefaultFilters = false
    )
    static class ServletConfig implements WebMvcConfigurer,ApplicationContextAware {

        ApplicationContext applicationContext;

        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
//            registry.jsp().viewNames("jsp/*");


            // thymeleaf
            var springResourceTemplateResolver=new SpringResourceTemplateResolver();
            springResourceTemplateResolver.setApplicationContext(applicationContext);
            springResourceTemplateResolver.setPrefix("/WEB-INF/");
            springResourceTemplateResolver.setSuffix(".html");
            var springTemplateEngine=new SpringTemplateEngine();
            springTemplateEngine.setTemplateResolver(springResourceTemplateResolver);
            var thymeleafViewResolver=new ThymeleafViewResolver();
            thymeleafViewResolver.setTemplateEngine(springTemplateEngine);
            // jsp와 동시에 사용할떄 사용
            thymeleafViewResolver.setOrder(1);
            thymeleafViewResolver.setViewNames(new String[]{"views/*"});
            //
            registry.viewResolver(thymeleafViewResolver);
        }


        // 정적
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resource/**")
                    .addResourceLocations("/resource/")
                    .setCachePeriod(60) // 60초마다 갱신
                    .resourceChain(true)
                    .addResolver(new EncodedResourceResolver());
        }


        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext=applicationContext;
        }



        // message Converter -> XML로 변경
//        @Override
//        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//            var messageConverter=new MarshallingHttpMessageConverter();
//            var xStreamMarshaller=new XStreamMarshaller();
//            messageConverter.setMarshaller(xStreamMarshaller);
//            messageConverter.setUnmarshaller(xStreamMarshaller);
//
//            converters.add(messageConverter);
//        }

        // 기존 JSON + XML도 추가 + Time에 대한 부분을 LocalDate로 JSON으로 변경 뒤 추가
        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            //XML
            var messageConverter=new MarshallingHttpMessageConverter();
            var xStreamMarshaller=new XStreamMarshaller();
            messageConverter.setMarshaller(xStreamMarshaller);
            messageConverter.setUnmarshaller(xStreamMarshaller);
            converters.add(0,messageConverter);


            //JSON에 Time에 대한 부분을 -> LocalDate로 표현
            var javaTimeModule=new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
            var modules=Jackson2ObjectMapperBuilder.json().modules(javaTimeModule);
            converters.add(1,new MappingJackson2HttpMessageConverter(modules.build()));
        }

        // CORS
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                    .allowedMethods("POST","GET")
                    .allowedOrigins("*");
        }
    }

    @Configuration
    @EnableTransactionManagement
    @ComponentScan(basePackages ="org.prms.kdt.customer",
            excludeFilters = @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE,value= CustomerController.class)
    )
    static class RootConfig {


        @Bean
        public DataSource dataSource() {


//          * DataSource
            var dataSource= DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt")
                    .username("root")
                    .password("root1234!")
                    .type(HikariDataSource.class)
                    .build();


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




    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("Init Servlet");

        var rootApplicationContext=new AnnotationConfigWebApplicationContext();
        rootApplicationContext.register(RootConfig.class);
        var loaderListener=new ContextLoaderListener(rootApplicationContext);
        servletContext.addListener(loaderListener);


        var applicationContext=new AnnotationConfigWebApplicationContext();
        applicationContext.register(ServletConfig.class);
        var dispatcherServlet=new DispatcherServlet(applicationContext);
        var servletRegistration=servletContext.addServlet("test",dispatcherServlet);
        servletRegistration.addMapping("/");
        //-1이 defuault이고 root만 실행 됨, API 요청 (Request)가 와야지만 ApplicationContext가 실행
        // 0이나 1로 주면 applcationContext가 실행됨
        servletRegistration.setLoadOnStartup(-1);

    }

}
