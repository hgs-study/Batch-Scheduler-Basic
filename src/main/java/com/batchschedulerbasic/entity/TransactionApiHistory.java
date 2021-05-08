package com.batchschedulerbasic.entity;

import com.batchschedulerbasic.common.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "transaction_api_history")
public class TransactionApiHistory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="transaction_api_history_id")
    private Long id;

    @Column(name="address")
    private String address;

    @Column(name="api_name")
    private String apiName;

    @Column(name="balance")
    private String balance;

    @Builder
    private TransactionApiHistory(String apiName,String balance){

        this.apiName = apiName;
        this.balance = balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
