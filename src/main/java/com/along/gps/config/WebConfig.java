package com.along.gps.config;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


/**
 * @author why
 * @date 2019-04-05 10:52
 */
@Configuration
public class WebConfig  implements WebMvcConfigurer {
    private final Logger logger = LoggerFactory.getLogger(WebMvcConfigurer.class);


    /**
     * 功能描述(打war包时需要注释掉，打成war或者传统方式发布到tomcat中， 相当于启动了两次 )
     * @author why
     * @date 2019/6/10
     * @param
     * @return org.springframework.web.socket.server.standard.ServerEndpointExporter
     * @description 配置ServerEndpointExporter，配置后会自动注册所有“@ServerEndpoint”注解声明的Websocket Endpoint
     */
   @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


}
