package com.github.schedulejob.schedule;

import com.github.schedulejob.domain.TicketDO;
import com.github.schedulejob.service.TicketService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 功能简单描述
 *
 * @author: lvhao
 * @since: 2016-6-2 21:14
 */
@Component
public class QuartzTestJob implements Job {

    @Autowired
    private TicketService ticketService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<TicketDO> ticketDOList = ticketService.queryList();
        System.out.println("ticketDOList = " + ticketDOList);
    }
}
