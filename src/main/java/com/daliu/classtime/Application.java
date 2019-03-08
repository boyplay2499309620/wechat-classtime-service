package com.daliu.classtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import springfox.documentation.swagger2.annotations.EnableSwagger2;



/**
 * Spring Boot应用启动类
 *
 * Created by daliu on 18/11/27.
 */
// Spring Boot 应用的标识
@SpringBootApplication
@EnableCaching
@EnableSwagger2             //启动swagger注解
public class Application {
	

    public static void main(String[] args) {
        // 程序启动入口
        // 启动嵌入式的Tomcat并初始化Spring环境及其各Spring组件
        SpringApplication.run(Application.class,args);
    	//new SpringApplicationBuilder(Application.class).web(true).run(args);
        System.out.println("******   springboot启动成功!   ****** ");
        
    }
    
    
    /**
     * it's for set http url auto change to https
     */
    
    /*
     *
     //暂时不转发
     
    @Bean
    public Connector connector(){
        Connector connector=new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(Connector connector){
        TomcatServletWebServerFactory tomcat=new TomcatServletWebServerFactory(){
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint=new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection=new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }*/
    
    /*
     * 也有用这两个类配置的，但一直有个包找不到
     *  这是该配置导入的包
     *  import org.apache.catalina.Context;
		import org.apache.catalina.connector.Connector;
		import org.apache.tomcat.util.descriptor.web.SecurityCollection;
		import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
		import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
		import org.springframework.context.annotation.Bean;


     *  @Bean
    public EmbeddedServletContainerFactory servletContainer(){
        TomcatEmbeddedServletContainerFactory tomcat=new TomcatEmbeddedServletContainerFactory(){
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint=new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");//confidential
                SecurityCollection collection=new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    @Bean
    public Connector httpConnector(){
        Connector connector=new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }

     * 
     */

}
