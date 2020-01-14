package com.learn.stock.Config;

import com.learn.stock.job.IndexDataSyncJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Configuration
public class QuartzConfiguration {

    private static final int interval = 2;

    @Bean
    public JobDetail weatherDataSyncJobDetail(){
        return JobBuilder.newJob(IndexDataSyncJob.class).withIdentity("indexDataSyncJob")
                .storeDurably().build();
    }

    @Bean
    public Trigger weatherDataSyncTigger(){
        System.out.println("weatherDataSyncTigger");
//        SimpleScheduleBuilder schedBuilder = SimpleScheduleBuilder.simpleSchedule()
//                .withIntervalInMinutes(interval).repeatForever();

        CronTrigger trigger = newTrigger().forJob(weatherDataSyncJobDetail()).withIdentity("indexDataSyncTrigger", "group1").withSchedule(cronSchedule("0 5 0 1/1 * ? "))
                .build();

        return trigger;
//        return TriggerBuilder.newTrigger().forJob(weatherDataSyncJobDetail())
//                .withIdentity("indexDataSyncTrigger").withSchedule(schedBuilder).build();
    }

    @Bean
    public Trigger startDoJob(){
        System.out.println("startDoJob");
        SimpleScheduleBuilder schedBuilder = SimpleScheduleBuilder.simpleSchedule();
        Date startTime = DateBuilder.futureDate(10, DateBuilder.IntervalUnit.SECOND);
        SimpleTrigger trigger = TriggerBuilder.newTrigger().forJob(weatherDataSyncJobDetail())
                .withIdentity("indexDataSyncTrigger").startAt(startTime).withSchedule(schedBuilder).build();
        return trigger;
    }
}
