package com.sandy.capitalyst.server ;

import org.apache.log4j.Logger ;
import org.springframework.beans.BeansException ;
import org.springframework.boot.SpringApplication ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.context.ApplicationContext ;
import org.springframework.context.ApplicationContextAware ;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry ;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer ;

import com.sandy.capitalyst.server.core.CapitalystConfig ;
import com.sandy.common.bus.EventBus ;

@SpringBootApplication
public class CapitalystServer 
    implements ApplicationContextAware, WebMvcConfigurer {

    private static final Logger log = Logger.getLogger( CapitalystServer.class ) ;
    
    private static ApplicationContext APP_CTX   = null ;
    private static CapitalystServer   APP       = null ;
    
    public static EventBus GLOBAL_EVENT_BUS = new EventBus() ;
    
    public static ApplicationContext getAppContext() {
        return APP_CTX ;
    }

    public static CapitalystConfig getConfig() {
        return (CapitalystConfig) APP_CTX.getBean( "config" ) ;
    }

    public static CapitalystServer getApp() {
        return APP ;
    }

    // ---------------- Instance methods start ---------------------------------

    public CapitalystServer() {
        APP = this ;
    }

    public void initialize() {
    }
    
    @Override
    public void setApplicationContext( ApplicationContext applicationContext )
            throws BeansException {
        APP_CTX = applicationContext ;
    }

    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry ) {
    }

    // --------------------- Main method ---------------------------------------

    public static void main( String[] args ) {
        log.debug( "Starting Spring Booot..." ) ;
        SpringApplication.run( CapitalystServer.class, args ) ;

        log.debug( "Starting Capitalyst Server.." ) ;
        CapitalystServer app = CapitalystServer.getAppContext().getBean( CapitalystServer.class ) ;
        app.initialize() ;
    }
}