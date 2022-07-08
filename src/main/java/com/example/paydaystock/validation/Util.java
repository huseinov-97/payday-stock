package az.hesab.autopaymentservice.service.impl;

import az.goldenpay.pg.web.webservice.GpmBillingParam;
import az.goldenpay.pg.web.webservice.GpmReqGetBillingInfo;
import az.goldenpay.pg.web.webservice.GpmRespGetBillingInfo;
import az.goldenpay.pg.web.webservice.GpmStatus;
import az.goldenpay.pg.web.webservice.PaymentGatewayWebService;
import az.hesab.autopaymentservice.config.LimitedQueue;
import az.hesab.autopaymentservice.dto.AzerIshiqResponse;
import az.hesab.autopaymentservice.exception.AppException;
import az.hesab.autopaymentservice.exception.Error;
import az.hesab.autopaymentservice.model.AutopaymentTransactionFail;
import az.hesab.autopaymentservice.model.PgSavedPayment;
import az.hesab.autopaymentservice.model.PgSavedPaymentParameter;
import az.hesab.autopaymentservice.service.AutopaymentQueueExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Log4j2
public class AutopaymentQueueExecutorImpl implements AutopaymentQueueExecutor {

    @Value("${executor-config.shutdown.await.time}")
    private int executorShutdownAwaitTime;
    @Value("${executor-config.thread.count}")
    private int threadCount;
    @Value("${executor-config.blocking.queue.limit}")
    private int blockingQueueLimit;
    @Value("${executor-config.thread.sleep.time}")
    private long threadSleepTime;

    private final AutopaymentOperationServiceImpl autopaymentOperationService;
    private final PaymentGatewayWebService paymentGatewayWebService;
    private final ObjectMapper objectMapper;

    private final String EHALI_FILE_NAME = "D:\\JavaFiles\\autopayment-service\\src\\main\\resources\\ehali.txt";
    private final String NEW_EHALI_FILE_NAME = "D:\\JavaFiles\\autopayment-service\\src\\main\\resources\\newEhali.txt";

    private ExecutorService getPaymentKeyExecutorService;
    private ExecutorService payCSExecutorService;
    private ExecutorService afterPaymentExecutorService;
    private ExecutorService paymentResultExecutorService;
    private ExecutorService exportToFileEhaliParamValue;
    private ExecutorService convertToFileEhaliParamValue;

    @PostConstruct
    public void init() {
        getPaymentKeyExecutorService = new ThreadPoolExecutor(threadCount, threadCount,
                threadSleepTime, TimeUnit.MILLISECONDS, new LimitedQueue<>(blockingQueueLimit));

        payCSExecutorService = new ThreadPoolExecutor(threadCount, threadCount,
                threadSleepTime, TimeUnit.MILLISECONDS, new LimitedQueue<>(blockingQueueLimit));

        afterPaymentExecutorService = new ThreadPoolExecutor(threadCount, threadCount,
                threadSleepTime, TimeUnit.MILLISECONDS, new LimitedQueue<>(blockingQueueLimit));

        paymentResultExecutorService = new ThreadPoolExecutor(threadCount, threadCount,
                threadSleepTime, TimeUnit.MILLISECONDS, new LimitedQueue<>(blockingQueueLimit));

        exportToFileEhaliParamValue = new ThreadPoolExecutor(threadCount, threadCount,
                threadSleepTime, TimeUnit.MILLISECONDS, new LimitedQueue<>(blockingQueueLimit));

        convertToFileEhaliParamValue = new ThreadPoolExecutor(threadCount, threadCount,
                threadSleepTime, TimeUnit.MILLISECONDS, new LimitedQueue<>(blockingQueueLimit));

        autopaymentOperationService.setQueueExecutor(this);
    }

    @PreDestroy
    public void destroy() {
        shutdown(getPaymentKeyExecutorService);

        shutdown(payCSExecutorService);

        shutdown(afterPaymentExecutorService);

        shutdown(paymentResultExecutorService);

        shutdown(exportToFileEhaliParamValue);

        shutdown(convertToFileEhaliParamValue);
    }

    @Override
    public void submitAutopaymentToGetPaymentKey(int idAutopayment) {
        getPaymentKeyExecutorService.submit(() -> autopaymentOperationService.getPaymentKey(idAutopayment));
    }

    @Override
    public void submitAutopaymentToPayCs(int idPgTransaction) {
        payCSExecutorService.submit(() -> autopaymentOperationService.payCs(idPgTransaction));
    }

    @Override
    public void submitAutopaymentToAfterPayment(int idPgTransaction) {
        afterPaymentExecutorService.submit(() -> autopaymentOperationService.afterPayment(idPgTransaction));
    }

    @Override
    public void submitCheckPaymentResult(AutopaymentTransactionFail autopaymentTransactionFail) {
        paymentResultExecutorService.submit(() -> autopaymentOperationService.checkPaymentResult(autopaymentTransactionFail));
    }

    @Override
    public void converter(String code) {
        convertToFileEhaliParamValue.submit(() -> {
            String url = "https://www.azerishiq.az/api/v1/debt/info";
            List<AzerIshiqResponse> azerIshiqResponseList = new ArrayList<>();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept-Language", Locale.getDefault().getLanguage());
            headers.set("X-CSRF-TOKEN", "SJgV0eJRSvQ3oqBqHQPhkNetxtwJlhMLLNYRfIdF");
            headers.set("Cookie", "XSRF-TOKEN=eyJpdiI6InhiU0svRFAyZDhJTCtiVUFtNFI4QkE9PSIsInZhbHVlIjoiNndWMnlESlFOWGhlYjBUM296WEExY2E0WVVDOUs3TmVtMFAzdEV5VHVSZGhjenB4dkxvTUJjWisxZVdrRStIYmlXdzN6R3NZblY0WjQzVnRuenJTNnVtQXR5RE1UTUxoME95L3RVM3FYR0hqaU5BRkVzMjhsWjJsalMzY0lVNW0iLCJtYWMiOiI0Yjc0OTFhNTE1N2I0NTlmMzk4YWJlZGUwNTYzNzU3ZGFmZTMzOGQxNjRlMmQ3YjA3YTIzMWU0NjEwMzI2ZmQ0In0%3D; azerishiqaz_session=eyJpdiI6ImJhc3VwV1pic1hUZk5JRWR0blVqRmc9PSIsInZhbHVlIjoicm5RaDFoTlFCdlArUVdpZll6dmlxWmkxakU5eVRpN3MrQ2M0K3JPeGYwaUY3M2lSR1c2TDNhaFdoV2Q0dWh1VGlINkl6RkFTWW5YemVUelAwWkI4RytIdVc3bFU1WkVkWnlFdTdQU3ZQWmpGdHFZaWd5NG92QmpDSzRVTkdXRTQiLCJtYWMiOiJmYzFiMzJkNTBhNTAwNWQ0ZWVhMThkZTVkYTVjYTJiMjI3MThlMzM1YWIxMTIzNzg5ZDgwZjlmYjM2ZmY5ZGI2In0%3D");
            headers.set("Referer", "https://www.azerishiq.az/");
            headers.set("Origin", "https://www.azerishiq.az");
            headers.set("Sec-Fetch-Mode", "cors");


            MultiValueMap<String, String> bodyPair = new LinkedMultiValueMap();
            bodyPair.add("debt", code);
            bodyPair.add("people_type", "ahali");

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(getMappingJackson2HttpMessageConverter());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(bodyPair, headers);
            ResponseEntity<AzerIshiqResponse> azerIshiqResponseResponseEntity = restTemplate
                    .exchange(url, HttpMethod.POST, request, AzerIshiqResponse.class);

            azerIshiqResponseList.add(azerIshiqResponseResponseEntity.getBody());

            azerIshiqResponseList.forEach(azerIshiqResponse -> {
                String subId = azerIshiqResponse.getSubId();
                String oldSubId = azerIshiqResponse.getOldSubId();
                String row = subId + " - " + oldSubId + "\n";
                try (FileOutputStream fileOutputStream = new FileOutputStream(NEW_EHALI_FILE_NAME, true)) {
                    byte[] strToBytes = row.getBytes();
                    fileOutputStream.write(strToBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
        return mappingJackson2HttpMessageConverter;
    }


//    @Override
//    public void exportParamValuesToFile(PgSavedPayment pgSavedPayment) {
//        exportToFileEhaliParamValue.submit(() -> {
//            List<PgSavedPaymentParameter> pgSavedPaymentParameters = pgSavedPayment.getPgSavedPaymentParameters();
//            GpmReqGetBillingInfo.Parameters parameters = new GpmReqGetBillingInfo.Parameters();
//            pgSavedPaymentParameters.forEach(pgSavedPaymentParameter -> {
//                GpmBillingParam gpmBillingParam = new GpmBillingParam();
//                gpmBillingParam.setParamName(pgSavedPaymentParameter.getParamName());
//                gpmBillingParam.setParamValue(pgSavedPaymentParameter.getParamValue());
//                parameters.getParameter().add(gpmBillingParam);
//            });
//            GpmReqGetBillingInfo gpmReqGetBillingInfo = new GpmReqGetBillingInfo();
//            gpmReqGetBillingInfo.setLang("en");
//            gpmReqGetBillingInfo.setMerchantName(pgSavedPayment.getMerchant().getPgMerchantName());
//            gpmReqGetBillingInfo.setParameters(parameters);
//
//            log.info("GET_BILLING_INFO request: [{}]", writeValueAsString(gpmReqGetBillingInfo));
//            GpmRespGetBillingInfo gpmRespGetBillingInfo;
//            try (FileOutputStream fileOutputStream = new FileOutputStream(EHALI_FILE_NAME, true)) {
//                gpmRespGetBillingInfo = paymentGatewayWebService.getBillingInformation(gpmReqGetBillingInfo);
//                GpmStatus status = gpmRespGetBillingInfo.getStatus();
//                if (status.getCode() != 1) {
//                    List<GpmBillingParam> parameter = parameters.getParameter();
//                    String paramValue = parameter.get(0).getParamValue();
//                    if (paramValue.equals("f")) {
//                        paramValue = parameter.get(1).getParamValue();
//                    }
//                    if (paramValue.length() <= 14) {
//                        paramValue = paramValue + "\n";
//                        byte[] strToBytes = paramValue.getBytes();
//                        fileOutputStream.write(strToBytes);
//                    }
//                }
//
//            } catch (Throwable throwable) {
//                throw new AppException(Error.INTERNAL_SERVER_ERROR, throwable);
//            }
//        });
//    }

    private void shutdown(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(executorShutdownAwaitTime, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

//    @SneakyThrows
//    private String writeValueAsString(Object object) {
//        return objectMapper.writeValueAsString(object);
//    }
}
