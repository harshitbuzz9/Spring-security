package com.bridge.herofincorp.configs;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Slf4j
public class APILogger {
    private Map<String, String> logger = new HashMap<>();

    public APILogger(String path, String requestId) {
        logger.put("path", path);
        logger.put("requestId", requestId);

    }

    public APILogger(String path) {
        logger.put("path", path);
        logger.put("requestId", UUID.randomUUID().toString());
    }

    public void add(String key, String value) {
        logger.put(key, value);
    }

    public void logSuccess(int statusCode) {
        logger.put("reason", "success");
        logger.put("status", String.valueOf(statusCode));
        log.info(logger.toString());
    }

    public void logError(String error, int statusCode, Throwable e) {
        logger.put("reason", error);
        logger.put("status", String.valueOf(statusCode));
        if (e != null) {
            logger.put("exception", ExceptionUtils.getStackTrace(e));
        }
        log.error(logger.toString());
    }

    public void logError(String error, int statusCode) {
        logError(error, statusCode, null);
    }
}
