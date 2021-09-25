package org.prms.kdt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {


    // CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods("POST","GET")
                .allowedOrigins("*");
    }

    // 기존 JSON + XML도 추가 + Time에 대한 부분을 LocalDate로 JSON으로 변경 뒤 추가
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //XML
        var messageConverter=new MarshallingHttpMessageConverter();
        var xStreamMarshaller=new XStreamMarshaller();
        messageConverter.setMarshaller(xStreamMarshaller);
        messageConverter.setUnmarshaller(xStreamMarshaller);
        converters.add(0,messageConverter);

    }
}
