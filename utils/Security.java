package com.bridge.herofincorp.utils;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class Security {
    private static final Logger log = LoggerFactory.getLogger(Security.class);

    public static Long generateOtp(){
        return RandomUtils.nextLong(1000, 9999);
    }

    public static String getCurrentUser(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}