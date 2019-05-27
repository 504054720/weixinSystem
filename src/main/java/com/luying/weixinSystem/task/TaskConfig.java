package com.luying.weixinSystem.task;

import com.luying.weixinSystem.service.SignRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Configuration
@EnableScheduling
@Component
public class TaskConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskConfig.class);
    @Autowired
    private SignRecordService signRecordService;

    /**
     * 定时任务：凌晨半点执行，计算前一天所有考勤状态
     */
    @Scheduled(cron = "0 30 1 ? * *")
    public void statisticalSignState(){
        try {
            LOGGER.info("TaskConfig_statisticalSignState_start");
            signRecordService.signRecordCountTask();
            LOGGER.info("TaskConfig_statisticalSignState_end");
        } catch (Exception e) {
            LOGGER.info("TaskConfig_statisticalSignState_exception:" + e);
            e.printStackTrace();
        }
    }
}
