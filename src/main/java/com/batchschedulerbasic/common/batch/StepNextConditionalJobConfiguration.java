package com.batchschedulerbasic.common.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepNextConditionalJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextConditionalJob(){
        return jobBuilderFactory.get("stepNextConditionalJob")
                                .start(conditionalJobStep1())
                                    .on("FAILED")//FAILED 일 경우
                                    .to(conditionalJobStep3())//STEP 3으로 이동한다.
                                    .on("*")//STEP의 결과와 관계 없이
                                    .end()//STEP3으로 이동하면 FLOW가 종료된다.
                                .from(conditionalJobStep1())//STEP1로부터
                                    .on("*")//FAILED 외에 모든 경우
                                    .to(conditionalJobStep2())//STEP2로 이동한다.
                                    .next(conditionalJobStep3())//STEP2가 정상 종료되면 STEP3으로 이동한다.
                                    .on("*")//STEP3의 결과와 관계 없이
                                    .end()//STEP3으로 이동하면 FLOW가 종료한다.
                                .end()//JOB 종료료
                                .build();
    }

    @Bean
    public Step conditionalJobStep1(){
        return stepBuilderFactory.get("step1")
                                 .tasklet((contribution, chunkContext) -> {
                                     log.info(">>>> this is stepNextConditionalJob step1");
                                     //ExitStatus를 FAILED로 지정, 해당 status를 보고 flow가 진행된다.
                                     //contribution.setExitStatus(ExitStatus.FAILED);
                                     return RepeatStatus.FINISHED;
                                 })
                                 .build();
    }

    @Bean
    public Step conditionalJobStep2(){
        return stepBuilderFactory.get("conditionalJobStep2")
                                 .tasklet((contribution, chunkContext) -> {
                                     log.info(">>>>> this is stepNextConditionalJob Step2");
                                     return RepeatStatus.FINISHED;
                                 })
                                 .build();
    }

    @Bean
    public Step conditionalJobStep3(){
        return stepBuilderFactory.get("conditionalJobStep3")
                                 .tasklet((contribution, chunkContext) -> {
                                     log.info(">>>>> this is stepNextConditionalJob Step3");
                                     return RepeatStatus.FINISHED;
                                 })
                                 .build();
    }



}
