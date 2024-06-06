package com.bridge.herofincorp.service.impls;

import com.bridge.herofincorp.exceptions.DatabaseAccessException;
import com.bridge.herofincorp.exceptions.DisbursalDetailsNotFoundException;
import com.bridge.herofincorp.model.entities.DisbursalDetails;
import com.bridge.herofincorp.model.request.DisbursalRequest;
import com.bridge.herofincorp.model.response.*;
import com.bridge.herofincorp.repository.DisbursalDetailsRepository;
import com.bridge.herofincorp.service.DisbursalService;
import com.bridge.herofincorp.utils.ApplicationConstants;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

@Service
public class DisbursalServiceImpl implements DisbursalService {

    private static final Logger log = LoggerFactory.getLogger(DisbursalServiceImpl.class);
    @Autowired
    private DisbursalDetailsRepository repository;
    @Autowired
    private ModelMapper mapper;

    @Override
    public DisbursalResponse getDisbursal(String startDate, String endDate, String groupBy, DisbursalRequest request) {
        String dealerCode = request.getDealerCode();
        String product = request.getProduct();
        List<DisbursalDetails> disbursalDetails;
        try{
            log.info("getting disbursal data from {} to {} grouped by {} for dealer code: {} and product code: {}"
                    ,startDate,endDate,groupBy,dealerCode,product);
            disbursalDetails = repository.getDisbursalDetails(startDate, endDate, dealerCode, product);
        }catch(DataAccessException e){
            log.error("error in getting disbursal data from {} to {} grouped by {} for dealer code: {} and product code: {}"
                    ,startDate,endDate,groupBy,dealerCode,product);
            throw new DatabaseAccessException("Error accessing the database");
        }
        DisbursalResponse response = new DisbursalResponse();
        response.setDealerCode(dealerCode);
        response.setGroupBy(groupBy);

        if (groupBy.equalsIgnoreCase(ApplicationConstants.GROUPBY_WEEK)) {
            response.setMonths(this.getDataWeekly(disbursalDetails, LocalDate.parse(startDate), LocalDate.parse(endDate)));
        }
        if (groupBy.equalsIgnoreCase(ApplicationConstants.GROUPBY_MONTH)){
            try{
                response.setMonths(this.getDataMonthly(disbursalDetails, LocalDate.parse(startDate), LocalDate.parse(endDate)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (groupBy.equalsIgnoreCase(ApplicationConstants.GROUPBY_DAY)){
            response.setMonths(this.getDataDaily(disbursalDetails, LocalDate.parse(startDate), LocalDate.parse(endDate)));
        }
        if (groupBy.equalsIgnoreCase(ApplicationConstants.GROUPBY_QUARTERLY)){
            response.setMonths(this.getDataQuaterly(disbursalDetails, LocalDate.parse(startDate), LocalDate.parse(endDate)));
        }
        return response;
    }

    private List<DisbursalDetailsResponse> getDataDaily(List<DisbursalDetails> disbursalDetails, LocalDate startDate, LocalDate endDate) {
        List<DisbursalDetailsResponse> disbursalDetailsResponses = new ArrayList<>();
        long noOfDay = DAYS.between(startDate,endDate);
        String label;
        for (int i=0; i<noOfDay+1; i++){
            LocalDate finalStartDate = startDate;
            int day = startDate.getDayOfMonth();
            String month = finalStartDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String year = String.valueOf(finalStartDate.getYear()).substring(2);
            label = day+"-"+month+"'"+year;
            List<DisbursalDetails> filteredDisbursalDetails = disbursalDetails.stream()
                    .filter(x->x.getSfdcLoginDate().toLocalDateTime().toLocalDate().isEqual(finalStartDate))
                    .toList();
            disbursalDetailsResponses.add(buildDisbursalDetailResponse(filteredDisbursalDetails,finalStartDate,label));
            startDate = startDate.plusDays(1);
        }
        return disbursalDetailsResponses.stream()
                .filter(x-> !(x.getTotalLogin() ==0))
                .toList();
    }

    private List<DisbursalDetailsResponse> getDataWeekly(List<DisbursalDetails> details, LocalDate startDate, LocalDate endDate) {
        List<DisbursalDetailsResponse> disbursalDetailsResponses = new ArrayList<>();
        int dayOfWeek = startDate.getDayOfWeek().getValue();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = startDate.get(weekFields.weekOfMonth());
        if (startDate.getDayOfWeek()== DayOfWeek.SUNDAY)
            weekNumber=weekNumber-1;
        String month = startDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String year = String.valueOf(startDate.getYear()).substring(2);
        String label = "W"+weekNumber+"-"+month+"'"+year;
        int daysTillAWeek=(7-dayOfWeek);
        LocalDate firstWeekStart = startDate.minusDays(1);
        LocalDate firstWeekEnd  = startDate.plusDays(daysTillAWeek+1);
        List<DisbursalDetails> disbursalDetailsFirstWeek = details.stream()
                .filter(x->x.getSfdcLoginDate().toLocalDateTime().toLocalDate().isAfter(firstWeekStart) && x.getSfdcLoginDate().toLocalDateTime().toLocalDate().isBefore(firstWeekEnd))
                .toList();
        disbursalDetailsResponses.add(buildDisbursalDetailResponse(disbursalDetailsFirstWeek,firstWeekStart.plusDays(1),label));
        int endDateDayOfWeek = endDate.getDayOfWeek().getValue();
        LocalDate endWeekStart = endDate.minusDays(endDateDayOfWeek);
        LocalDate endWeekEnd = endDate.plusDays(1);
        int noOfDay = (int) DAYS.between(firstWeekEnd.minusDays(1), endWeekStart.minusDays(1));
        int diff = Math.abs(noOfDay)+1;
        int loop = diff /7;
        LocalDate day = firstWeekEnd;
        for (int i=1; i<=loop; i++){
            LocalDate date = day.plusWeeks(1);
            LocalDate after = day;
            month = after.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            year = String.valueOf(after.getYear()).substring(2);
            weekNumber = after.get(weekFields.weekOfMonth());
            if (startDate.getDayOfWeek()== DayOfWeek.SUNDAY)
                weekNumber=weekNumber-1;
            label = "W"+weekNumber+"-"+month+"'"+year;
            List<DisbursalDetails> disbursalDetails = details.stream()
                    .filter(x->x.getSfdcLoginDate().toLocalDateTime().toLocalDate().isAfter(after)
                            && x.getSfdcLoginDate().toLocalDateTime().toLocalDate().isBefore(date))
                    .toList();
            disbursalDetailsResponses.add(buildDisbursalDetailResponse(disbursalDetails,day,label));
            day = day.plusWeeks(1);
        }
        month = endWeekStart.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        year = String.valueOf(endWeekStart.getYear()).substring(2);
        weekNumber = endWeekStart.get(weekFields.weekOfMonth());
        if (startDate.getDayOfWeek()== DayOfWeek.SUNDAY)
            weekNumber=weekNumber-1;
        label = "W"+weekNumber+"-"+month+"'"+year;
        List<DisbursalDetails> disbursalDetailsEndWeek = details.stream()
                .filter(x->x.getSfdcLoginDate().toLocalDateTime().toLocalDate().isAfter(endWeekStart)
                        && x.getSfdcLoginDate().toLocalDateTime().toLocalDate().isBefore(endWeekEnd))
                .toList();
        disbursalDetailsResponses.add(buildDisbursalDetailResponse(disbursalDetailsEndWeek,endWeekStart.plusDays(1),label));
        return disbursalDetailsResponses.stream()
                .filter(x-> !(x.getTotalLogin() ==0))
                .toList();
    }

    @SuppressWarnings("deprecation")
    private List<DisbursalDetailsResponse> getDataMonthly(List<DisbursalDetails> disbursalDetails, LocalDate startDate, LocalDate endDate) {
        List<DisbursalDetailsResponse> disbursalDetailsResponses = new ArrayList<>();
        int noOfMonths = (int) MONTHS.between(startDate,endDate);
        String label = "";
        for (int i=noOfMonths; i>=0; i--){
            LocalDate beforeDate = endDate.minusMonths(i);
            int month = beforeDate.getMonth().getValue();
            LocalDate printDate = LocalDate.of(beforeDate.getYear(),beforeDate.getMonth(), 1);
            String labelMonth = printDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String year = String.valueOf(printDate.getYear()).substring(2);
            label = labelMonth+"'"+year;
            List<DisbursalDetails> l1 = disbursalDetails.stream()
                    .filter(x->x.getSfdcLoginDate().getMonth()+1==month)
                    .toList();
            disbursalDetailsResponses.add(buildDisbursalDetailResponse(l1,printDate,label));
        }
        return disbursalDetailsResponses.stream()
                .filter(x-> !(x.getTotalLogin() ==0))
                .toList();
    }
    private List<DisbursalDetailsResponse> getDataQuaterly(List<DisbursalDetails> disbursalDetails, LocalDate startDate, LocalDate endDate) {
        List<DisbursalDetailsResponse> disbursalDetailsResponses = new ArrayList<>();
        int noOfMonths = (int) MONTHS.between(startDate,endDate);
        String label = "";
        LocalDate startDateLocal = startDate;
        for (int i=0; i<=noOfMonths/3; i++){
            LocalDate beforeDate = startDateLocal.plusMonths(3);
            LocalDate finalStartDateLocal = startDateLocal;
            label = "Qtr "+(i+1);
            List<DisbursalDetails> l1 = disbursalDetails.stream()
                    .filter(x->x.getSfdcLoginDate().toLocalDateTime().isAfter(finalStartDateLocal.atStartOfDay())&&
                            x.getSfdcLoginDate().toLocalDateTime().isBefore(beforeDate.atStartOfDay()))
                    .toList();
            disbursalDetailsResponses.add(buildDisbursalDetailResponse(l1,startDateLocal,label));
            startDateLocal = beforeDate;
        }
        return disbursalDetailsResponses.stream()
                .filter(x-> !(x.getTotalLogin() ==0))
                .toList();
    }

    private DisbursalDetailsResponse buildDisbursalDetailResponse(List<DisbursalDetails> disbursalDetails, LocalDate date, String label) {
        return DisbursalDetailsResponse.builder()
                .day(date)
                .label(label)
                .amountDisbursed(disbursalDetails.stream().filter(y->y.getApplicationStatus().equalsIgnoreCase(ApplicationConstants.DISBURSAL_STATUS_DISBURSED)).mapToDouble(x-> x.getDisbursedAmount()!=null ? x.getDisbursedAmount() : 0).sum())
                .amountCancelled(disbursalDetails.stream().filter(y->y.getApplicationStatus().equalsIgnoreCase(ApplicationConstants.DISBURSAL_STATUS_CANCELLED)).mapToDouble(x->x.getDisbursedAmount()!=null ? x.getDisbursedAmount() : 0).sum())
                .totalLogin(disbursalDetails.size())
                .totalDisbursed((int)disbursalDetails.stream().filter(y->y.getApplicationStatus().equalsIgnoreCase(ApplicationConstants.DISBURSAL_STATUS_DISBURSED)).count())
                .totalCancelled((int)disbursalDetails.stream().filter(y->y.getApplicationStatus().equalsIgnoreCase(ApplicationConstants.DISBURSAL_STATUS_CANCELLED)).count())
                .totalApproved((int)disbursalDetails.stream().filter(y->y.getApplicationStatus().equalsIgnoreCase(ApplicationConstants.DISBURSAL_STATUS_APPROVED)).count())
                .build();
    }

    @Override
    public List<DisbursalResponseDatewise> getDisbursalDatewise(String date, DisbursalRequest request) {
        String dealerCode = request.getDealerCode();
        String product = request.getProduct();
        List<DisbursalDetails> disbursalDetails;
        try{
            log.info("getting disbursal data for date: {} for dealer: {} and product: {}",date,dealerCode,product);
            disbursalDetails = repository.getDisbursalDetailsDatewise(date, dealerCode, product);

        }catch(DataAccessException e){
            log.error("error in getting disbursal data for date: {} for dealer: {} and product: {}",date,dealerCode,product);
            throw new DatabaseAccessException("Error accessing the database");
        }
        List<DisbursalResponseDatewise> response = disbursalDetails.stream()
                .map(x->mapper.map(x,DisbursalResponseDatewise.class))
                .toList();
        try{
            response.forEach(x->x.setData(this.getDisbursalByApplicationId(x.getApplicationId())));
        }catch(Exception e){
            e.printStackTrace();
        }

        return response;

    }

    @Override
    public DisbursalDetailResponse getDisbursalByApplicationId(Long applicationId) {
        DisbursalDetails disbursalDetails;
        try{
            log.info("getting disbursal data for applicationId: {}",applicationId);
            disbursalDetails = repository.findById(applicationId)
                    .orElseThrow(()->new DisbursalDetailsNotFoundException("Disbursal Details doesn't exist for applicationId: "+applicationId));
        }catch(DataAccessException e){
            log.error("error in getting disbursal data for application id: {}"
                    ,applicationId);
            throw new DatabaseAccessException("Error accessing the database");
        }
        return DisbursalDetailResponse.builder()
                .dealerCode(disbursalDetails.getDealerCode())
                .lanId(disbursalDetails.getSfdcApplicationId())
                .appId(disbursalDetails.getApplicationId())
                .product(disbursalDetails.getProductCode())
                .status(disbursalDetails.getApplicationStatus())
                .customerName(disbursalDetails.getCustomerName())
                .utr(disbursalDetails.getUtrNumber())
                .disbursalDetails(DisbursalAmountDetails.builder()
                        .amount(disbursalDetails.getDisbursedAmount())
                        .date(disbursalDetails.getSfdcLoginDate().toLocalDateTime().toLocalDate())
                        .build())
                .loginDate(disbursalDetails.getSfdcLoginDate())
                .model(disbursalDetails.getModel())
                .scheme(disbursalDetails.getScheme())
                .build();
    }
}
