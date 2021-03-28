package com.oktaice.scim;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

//@Configuration
//public class RequestLoggerFilter {
//
//    @Bean
//    public CommonsRequestLoggingFilter logFilter() {
//        System.out.println("\n\nSetting Filter?? \n\n");
//        CommonsRequestLoggingFilter filter
//                = new CommonsRequestLoggingFilter();
//        filter.setIncludeQueryString(true);
//        filter.setIncludePayload(true);
//        filter.setMaxPayloadLength(10000);
//        filter.setIncludeHeaders(true);
//        filter.setAfterMessagePrefix("REQUEST DATA : ");
//
//        return filter;
//    }
//}