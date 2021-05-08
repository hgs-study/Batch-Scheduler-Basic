package com.batchschedulerbasic.common.AWS;

import com.batchschedulerbasic.common.util.BasicRestTemplate;
import com.batchschedulerbasic.common.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TransactionUtil {

    private final APIRequest apiRequest;

    private final BasicRestTemplate basicRestTemplate;

    private final JsonConverter jsonConverter;

    //밸런스 확인
    public String getBalance(String address){
        HttpEntity<?> entity = apiRequest.setEntity("klay_getBalance",address);

        RestTemplate restTemplate = basicRestTemplate.get();
        ResponseEntity<String> responseEntity = restTemplate.exchange("https://node-api.klaytnapi.com/v1/klaytn", HttpMethod.POST,entity,String.class);

        return ConvertBalance(responseEntity);
    }

    @SneakyThrows
    private String ConvertBalance(ResponseEntity<String> responseEntity){
        String hexBalance = jsonConverter.responseEntityToValue(responseEntity,"result");
        return hexBalance;
    }
}
