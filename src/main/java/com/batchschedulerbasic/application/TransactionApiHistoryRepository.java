package com.batchschedulerbasic.application;

import com.batchschedulerbasic.entity.TransactionApiHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionApiHistoryRepository extends JpaRepository<TransactionApiHistory, Long> {
}
