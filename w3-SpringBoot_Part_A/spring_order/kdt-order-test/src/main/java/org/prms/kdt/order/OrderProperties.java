package org.prms.kdt.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

// yaml을 등록하기 위해 필요 -> @confugration, @ConfigurationProperties -> 하위 폴더들을 전부 사용가능
@Configuration
@ConfigurationProperties(prefix = "kdt")
public class OrderProperties implements InitializingBean {

    private final static Logger logger= LoggerFactory.getLogger(OrderProperties.class);

    private String version;
    private Integer minimumOrderAmount;

    private List<String> supportVendors;
    private String description;

    @Value("${JAVA_HOME}")
    private String javaHome;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("[OrderProperties] version-> {}", version);
        logger.debug("[OrderProperties] minimumOrderAmount-> {}", minimumOrderAmount);
        logger.debug("[OrderProperties]  supportVendors -> {}", supportVendors);
        logger.debug("[OrderProperties]  javaHOME -> {}", javaHome);

    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(Integer minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public List<String> getSupportVendors() {
        return supportVendors;
    }

    public void setSupportVendors(List<String> supportVendors) {
        this.supportVendors = supportVendors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
