package com.bridge.herofincorp.utils;

import java.time.format.DateTimeFormatter;

public class LoginConstant {
    public static final String OTP_KEY_PREFIX = "OTP:";
    public static final String OTP_VERIFIED = "OTP_VERIFIED";
    public static final String OTP_INVALID = "OTP_INVALID";
    public static final String OTP_EXPIRED = "OTP_EXPIRED";
    public static final String APP = "TEMP";
    public static final String OTP_KEY_PREFIX_TIME = "TIME";
    public static final String OTP_KEY_PREFIX_REATTEMPT = "REATTEMPT";
    public static final String REATTEMPT_COUNT = "3";
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

}
