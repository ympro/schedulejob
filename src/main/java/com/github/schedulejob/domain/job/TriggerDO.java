package com.github.schedulejob.domain.job;

import com.google.common.base.Strings;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 触发器域
 *
 * @author: lvhao
 * @since: 2016-6-23 20:59
 */
public class TriggerDO {
    private static final Logger log = LoggerFactory.getLogger(TriggerDO.class);

    // trigger info
    private String name;
    private String group;
    private String cronExpression;
    private String type = "";
    private String description;

    /**
     * 任务转换
     *
     * @param jobDetail JobDetail
     */
    public Trigger convert2QuartzTrigger(JobDetail jobDetail) {
        try {
            return Objects.equals(this.type, "")
                    ? convert2CronTrigger(jobDetail)
                    : convert2SimpleTrigger(jobDetail);
        } catch (Exception e) {
            log.error("convert2QuartzTrigger error!", e);
        }
        return null;
    }

    /**
     * 转换定时任务 trigger
     *
     * @param jobDetail jobDetail
     * @return CronTrigger
     */
    public CronTrigger convert2CronTrigger(JobDetail jobDetail) throws ParseException {
        CronExpression ce;
        checkArgument(!Strings.isNullOrEmpty(cronExpression), "cronExpression参数非法");
        ce = new CronExpression(this.cronExpression);
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(ce))
                .withIdentity(this.name, this.group)
                .withDescription(this.description)
                .build();
    }

    /**
     * SimpleTrigger 限定只执行一次
     *
     * @param jobDetail JobDetail
     */
    public SimpleTrigger convert2SimpleTrigger(JobDetail jobDetail) {
        checkArgument(!Strings.isNullOrEmpty(type), "type 参数非法");
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .startAt(DateBuilder.futureDate(3, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(1)
                        .withRepeatCount(1))
                .build();
    }

    public boolean isSimpleTrigger() {
        return this.type != "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TriggerDO{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
