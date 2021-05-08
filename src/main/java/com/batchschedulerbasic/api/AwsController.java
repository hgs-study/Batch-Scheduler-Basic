package com.batchschedulerbasic.api;

import com.batchschedulerbasic.common.AWS.TransactionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AwsController {

    private final TransactionUtil transactionUtil;

    @Value("${klaytn.wallet.tx.from.address}")
    private String AdminAddress;

    @GetMapping("/walletAddress")
    public String getWalletAddress(){
        return transactionUtil.getBalance(AdminAddress);
    }
}
