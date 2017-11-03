package com.github.schedulejob.util;

import com.github.schedulejob.schedule.ThriftJob;

import org.junit.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.core.jmx.JobDataMapSupport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

/**
 * Scheduler 测试
 * Created by Yan Meng on 2017/11/3.
 */
public class SchedulerTest extends BaseTest {

    @Resource
    private Scheduler scheduler;

    /**
     * 添加任务
     */
    @Test
    public void add() throws SchedulerException {
        Trigger trigger1 = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "simple")
                .startAt(DateBuilder.futureDate(3, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(5)
                        .withIntervalInSeconds(1)
                        .withRepeatCount(5))
                .build();
        Trigger trigger2 = TriggerBuilder.newTrigger()
                .withIdentity("cron trigger", "simple")
                .withSchedule(
                        //每5秒执行一次
                        CronScheduleBuilder.cronSchedule("0/1 * * ? * *")
                ).build();

        JobDetail jobDetail = JobBuilder.newJob()
                .ofType(ThriftJob.class)
                .withIdentity("simple test", "simple")
                .withDescription("simple")
                .setJobData(JobDataMapSupport.newJobDataMap(new HashMap<>()))
                .build();

        Set<Trigger> triggerSet = new HashSet<>();
        triggerSet.add(trigger1);
        triggerSet.add(trigger2);

        scheduler.scheduleJob(jobDetail, triggerSet, true);

        while (true) {
        }
    }
}
