package com.bridge.herofincorp.controller;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.model.request.SurveyResponseDetails;
import com.bridge.herofincorp.model.response.DealerInfoResponse;
import com.bridge.herofincorp.model.response.QuestionResponse;
import com.bridge.herofincorp.model.response.SurveyResponse;
import com.bridge.herofincorp.service.CommonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/bridge-app/common")
public class CommonController {
    @Autowired
    private CommonService commonService;

    @GetMapping("/survey/questions")
    public QuestionResponse getQuestionsByProductCode(@RequestAttribute("logger") APILogger logger, @RequestParam String productCode) {
        QuestionResponse questionResponse;
        try {
            logger.add("CommonController.getQuestionsByProductCode-startTime", LocalDateTime.now().toString());
            questionResponse = commonService.getQuestionsByProductCode(logger,productCode);
            logger.add("CommonController.getQuestionsByProductCode-endTime", LocalDateTime.now().toString());
            logger.logSuccess(200);
        } catch (DataAccessException e) {
            logger.logError(e.getMessage(), HttpStatus.FORBIDDEN.value());
            throw e;
        }catch (Exception e) {
            logger.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw e;
        }
        return questionResponse;
    }
    @GetMapping("/survey/dealer")
    public DealerInfoResponse getDealerInfoForSurvey(@RequestAttribute("logger") APILogger logger,
                                                        @RequestParam String dealerCode,
                                                        @RequestParam String seId,
                                                        @RequestParam String productCode,
                                                        @RequestParam String surveyMonth) {
        DealerInfoResponse questionResponse;
        try {
            logger.add("CommonController.getDealerInfoForSurvey-startTime", LocalDateTime.now().toString());
            questionResponse = commonService.getDealerInfoForSurvey(logger,dealerCode,seId,productCode,surveyMonth);
            logger.add("CommonController.getDealerInfoForSurvey-endTime", LocalDateTime.now().toString());
            logger.logSuccess(200);
        } catch (DataAccessException e) {
            logger.logError(e.getMessage(), HttpStatus.FORBIDDEN.value());
            throw e;
        }catch (Exception e) {
            logger.logError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw e;
        }
        return questionResponse;
    }


    @PostMapping("/survey/response")
    public SurveyResponse createIngestionJourney(@RequestAttribute("logger")APILogger logger, HttpServletRequest httpRequest, @RequestBody SurveyResponseDetails request) throws ParseException {
        String token = httpRequest.getHeader("Authorization").substring(7);
        return commonService.saveResponse(token, request,logger);
    }

}