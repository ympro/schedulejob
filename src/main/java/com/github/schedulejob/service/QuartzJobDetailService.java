package com.github.schedulejob.service;

import com.google.common.collect.Lists;

import com.github.schedulejob.config.quartz.QuartzConfig;
import com.github.schedulejob.domain.job.JobDetailDO;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 定时任务操作
 *
 * @author: lvhao
 * @since: 2016-6-23 19:58
 */
@Service
@Transactional
public class QuartzJobDetailService extends BaseService {
    private static final Logger log = LoggerFactory.getLogger(QuartzConfig.class);

    // SchedulerFactoryBean 创建
    @Autowired
    private Scheduler scheduler;

    // 任务列表
    @Transactional(readOnly = true)
    public List<JobDetailDO> queryJobList() {
        List<JobDetailDO> jobDetailDOs = Lists.newArrayList();

        // 数据处理
        try {
            Function<Set<JobKey>, List<JobDetailDO>> copyPropFun = dataFunc();
            Set<JobKey> jobSet = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
            jobDetailDOs = copyPropFun.apply(jobSet);
        } catch (SchedulerException e) {
            log.error("queryJobList error!", e);
        }
        return jobDetailDOs;
    }

    private Function<Set<JobKey>, List<JobDetailDO>> dataFunc() {
        return jbst -> {
            List<JobDetailDO> jddList;
            jddList = jbst.stream().map(jk -> {
                List<Trigger> trList = this.getTriggerByKey(jk);
                JobDetail jd = this.getJobDetailByKey(jk);

                // jobDetail
                JobDetailDO jobDetailDO = new JobDetailDO();
                jobDetailDO.fillWithQuartzJobDetail.accept(jd);
                jobDetailDO.fillWithQuartzTriggers.accept(trList);
                return jobDetailDO;
            }).collect(Collectors.toList());
            return jddList;
        };
    }

    /**
     * 查询指定jobkey jobDetail
     */
    @Transactional(readOnly = true)
    public JobDetailDO queryByKey(JobKey jobKey) {
        JobDetailDO jobDetailDO = new JobDetailDO();
        JobDetail jobDetail = this.getJobDetailByKey(jobKey);
        if (Objects.nonNull(jobDetail)) {
            List<Trigger> triggerList = this.getTriggerByKey(jobKey);
            jobDetailDO.fillWithQuartzJobDetail.accept(jobDetail);
            jobDetailDO.fillWithQuartzTriggers.accept(triggerList);
        }
        return jobDetailDO;
    }

    /**
     * 添加任务
     */
    public boolean add(JobDetailDO jobDetailDO) {
        JobDetail jobDetail = jobDetailDO.getJobDO().convert2QuartzJobDetail();
        Set<Trigger> triggerSet = jobDetailDO.getTriggerDOs().stream().map(jtd ->
                jtd.convert2QuartzTrigger(jobDetail)
        ).collect(Collectors.toSet());

        // 如果已经存在 则替换
        try {
            scheduler.scheduleJob(jobDetail, triggerSet, true);
            return true;
        } catch (SchedulerException e) {
            log.error("add error!", e);
        }
        return false;
    }

    /**
     * 删除任务
     */
    public boolean remove(List<JobKey> jobKeyList) {
        try {
            return scheduler.deleteJobs(jobKeyList);
        } catch (SchedulerException e) {
            log.error("remove error!", e);
        }
        return false;
    }

    // 停用任务
    public boolean disable(GroupMatcher<JobKey> matcher) {
        try {
            scheduler.pauseJobs(matcher);
            return true;
        } catch (SchedulerException e) {
            log.error("disable error!", e);
        }
        return false;
    }

    // 停用所有任务
    public boolean disableAll() {
        try {
            scheduler.pauseAll();
            return true;
        } catch (SchedulerException e) {
            log.error("disableAll error!", e);
        }
        return false;
    }

    // 启用任务
    public boolean enable(GroupMatcher<JobKey> matcher) {
        try {
            scheduler.resumeJobs(matcher);
            return true;
        } catch (SchedulerException e) {
            log.error("enable error!", e);
        }
        return false;
    }

    // 启用所有任务
    public boolean enableAll() {
        try {
            scheduler.resumeAll();
            return true;
        } catch (SchedulerException e) {
            log.error("enableAll erroe!", e);
        }
        return false;
    }

    // 立即触发任务
    public boolean triggerNow(JobKey jobKey, JobDataMap jobDataMap) {
        try {
            scheduler.triggerJob(jobKey, jobDataMap);
            return true;
        } catch (SchedulerException e) {
            log.error("triggerNow error!", e);
        }
        return false;
    }

    /**
     * 根据key 获取jobDetail
     */
    @Transactional(readOnly = true)
    public JobDetail getJobDetailByKey(JobKey jobKey) {
        JobDetail jd = null;
        try {
            jd = scheduler.getJobDetail(jobKey);
        } catch (SchedulerException e) {
            log.error("getJobDetailByKey error!", e);
        }
        return jd;
    }

    /**
     * 根据key 获取job trigger
     */
    public List<Trigger> getTriggerByKey(JobKey jobKey) {
        List<Trigger> triggerList = Lists.newArrayList();
        try {
            triggerList = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
        } catch (SchedulerException e) {
            log.error("getTriggerByKey error!", e);
        }
        return triggerList;
    }
}
