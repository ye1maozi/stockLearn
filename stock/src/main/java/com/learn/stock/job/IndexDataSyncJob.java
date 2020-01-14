package com.learn.stock.job;

import cn.hutool.core.date.DateUtil;
import com.learn.stock.pojo.Index;
import com.learn.stock.service.IndexDataService;
import com.learn.stock.service.IndexService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

public class IndexDataSyncJob extends QuartzJobBean {

    @Autowired
    IndexService indexService;
    @Autowired
    IndexDataService indexDataService;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("定时启动：" + DateUtil.now());
        List<Index> indexList = indexService.fresh();
        for (Index i: indexList
             ) {
            indexDataService.fresh(i.getCode());
        }
        System.out.println("定时结束：" + DateUtil.now());
    }
}
