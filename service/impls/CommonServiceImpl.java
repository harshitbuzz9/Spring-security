package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.configs.APILogger;
import com.bridge.herofincorp.exceptions.DataFetchException;
import com.bridge.herofincorp.exceptions.DatabaseAccessException;
import com.bridge.herofincorp.model.entities.SeSurveyResponse;
import com.bridge.herofincorp.model.request.SurveyResponseDetails;
import com.bridge.herofincorp.model.response.*;
import com.bridge.herofincorp.model.entities.Survey;
import com.bridge.herofincorp.repository.SeSurveyResponseRepository;
import com.bridge.herofincorp.repository.SurveyRepository;
import com.bridge.herofincorp.service.CommonService;
import com.bridge.herofincorp.service.UclLeadGenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private SurveyRepository repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UclLeadGenService uclService;
    @Autowired
    private SeSurveyResponseRepository surveyResponseRepository;

    public QuestionResponse getQuestionsByProductCode(APILogger logger,String productCode) {
        logger.add("CommonServiceImpl.getQuestionsByProductCode-startTime", LocalDateTime.now().toString());
        List<Survey> list;
        QuestionResponse response = new QuestionResponse();
        try {
            logger.add("calling surveyRepository for fetching Questions for product:"+productCode,"");
            list = repository.findAllByProductCode(productCode);
            logger.add("fetched info: "+list,"");
            response.setQuestions(list.stream()
                    .map(x -> mapper.map(x, QuestionDetail.class)).toList());
            logger.add("CommonServiceImpl.getQuestionsByProductCode-endTime", LocalDateTime.now().toString());
        } catch (DataAccessException e) {
            logger.logError(e.getMessage(), HttpStatus.FORBIDDEN.value());
            throw new DatabaseAccessException("Error accessing the database");
        }
        return response;
    }

    @Override
    public DealerInfoResponse getDealerInfoForSurvey(APILogger logger, String dealerCode, String seId, String productCode, String surveyMonth) {
        logger.add("CommonServiceImpl.getDealerInfoForSurvey-startTime", LocalDateTime.now().toString());
        Object obj = null;
        try{
            logger.add("fetching dealer info from uclService.getDealerInfo() for dealerCode: "+dealerCode,"");
            obj = uclService.getDealerInfo(dealerCode,productCode,logger);
            logger.add("info fetched: "+obj,"");
        }catch (DataFetchException e){
            logger.logError(e.getMessage(),HttpStatus.BAD_REQUEST.value());
            throw e;
        }
        DealerInfoResponse response = mapper.map(obj,DealerInfoResponse.class);
        List<DealerDetailResponse> rms = response.getResponse().getRM();
        List<DealerDetailResponse> li = rms.stream()
                .map(x->{
                    List<SeSurveyResponse> l = new ArrayList<>();
                    try{
                        logger.add("getting survey response detail from db for seId: "+x.getId(),"");
                        l = surveyResponseRepository.getSurvey(dealerCode, x.getId(), productCode, surveyMonth);
                        logger.add("fetched info: "+l,"");
                    }catch(Exception e){
                        logger.logError(e.getMessage(),HttpStatus.FORBIDDEN.value());
                        throw e;
                    }
                    if (!l.isEmpty())
                        x.setStatus("Completed");
                    else
                        x.setStatus("Pending");
                    return x;
                }).toList();
        response.getResponse().setRM(li);
        logger.add("CommonServiceImpl.getDealerInfoForSurvey-endTime", LocalDateTime.now().toString());
        return response;
    }

    @Override
    public SurveyResponse saveResponse(String token, SurveyResponseDetails request, APILogger logger) {
        return mapSurveyDetailsToSurveyResponse(
                surveyResponseRepository.saveAll(request.getQuestionRequests().stream().map(t->mapSurveyResponseDetailsToSurveyResponse(request,t.getQuestionId(),t.getResponse())).collect(Collectors.toList())).get(0)
        );
    }

    public SeSurveyResponse mapSurveyResponseDetailsToSurveyResponse(SurveyResponseDetails request,String questionId, int response){
        return SeSurveyResponse.builder()
                .id(UUID.randomUUID().toString())
                .surveyQuestionId(questionId)
                .dealerCode(request.getDealerCode())
                .dealerName(request.getDealerName())
                .productCode(request.getProductCode())
                .seRecordId(request.getSeRecordId())
                .seName(request.getSeName())
                .surveyMonth(LocalDate.now().minusMonths(1).withDayOfMonth(1))
                .surveyResponse(response)
                .surveyDatetime(Timestamp.valueOf(LocalDateTime.now()))
                .surveyStatus("Completed")
                .build();
    }

    public SurveyResponse mapSurveyDetailsToSurveyResponse(SeSurveyResponse response){
        return SurveyResponse.builder()
                .dealerCode(response.getDealerCode())
                .dealerName(response.getDealerName())
                .productCode(response.getProductCode())
                .seRecordId(response.getSeRecordId())
                .seName(response.getSeName())
                .surveyMonth(response.getSurveyMonth())
                .surveyDatetime(response.getSurveyDatetime())
                .surveyStatus(response.getSurveyStatus())
                .build();
    }

}
