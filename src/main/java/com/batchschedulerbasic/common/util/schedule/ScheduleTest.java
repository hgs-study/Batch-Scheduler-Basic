package com.batchschedulerbasic.common.util.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class ScheduleTest {

    @Scheduled(cron = "0 30 20 * * *")
    public void alert1(){
        log.info("==========현재시간 :=========== "+ new Date());
    }

    @Scheduled(fixedDelay = 1000)
    public void alert2(){
        log.info("==========현재시간 :=========== "+ new Date());
    }

}
