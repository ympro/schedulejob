package com.github.schedulejob.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * Thrift job封装
 *
 * @author: lvhao
 * @since: 2016-7-22 10:20
 */
@Component
public class ThriftJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
//
//        System.out.println(context);
//
//        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
//        System.out.println(mergedJobDataMap);
//
//        for (String key : mergedJobDataMap.keySet()) {
//            System.out.println(key + " : " + mergedJobDataMap.get(key));
//        }

        System.out.println("test ThriftJob running --------------------");
    }
}
