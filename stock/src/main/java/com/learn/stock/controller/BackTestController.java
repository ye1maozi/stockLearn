package com.learn.stock.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.learn.stock.pojo.IndexData;
import com.learn.stock.service.BackTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class BackTestController {

    @Autowired
    BackTestService backTestService;

    @GetMapping("/simulate/{code}/{ma}/{buyThreshold}/{sellThreshold}/{serviceCharge}/{startDate}/{endDate}")
    @CrossOrigin
    public Map<String,Object> backTest(@PathVariable("code") String code,
                                       @PathVariable("ma") int ma,
                                       @PathVariable("buyThreshold") float buyRate,
                                       @PathVariable("sellThreshold") float sellRate,
                                       @PathVariable("serviceCharge") float serviceCharge,
                                        @PathVariable("startDate") String startDate,
                                       @PathVariable("endDate") String endDate)throws Exception{

        List<IndexData> indexData = backTestService.listIndexData(code);
        String indexStartDate = indexData.get(0).getDate();
        String indexEndDate = indexData.get(indexData.size()-1).getDate();

        indexData = fiterByDateRange(indexData,startDate,endDate);
        Map<String,Object> allresult = new HashMap<>();
        allresult.put("indexDatas", indexData);
        allresult.put("indexStartDate", indexStartDate);
        allresult.put("indexEndDate", indexEndDate);


//        int ma = 20;
//        float sellRate = 0.95f;
//        float buyRate = 1.05f;
//        float serviceCharge = 0f;
        Map<String,Object> simulates = backTestService.simulate(ma,sellRate,buyRate,serviceCharge,indexData);
//        List<Profit> profits = (List<Profit>) simulates.get("profits");
//        allresult.put("profits",profits);
//        List<Trade> trades = (List<Trade>) simulates.get("trades");
//        allresult.put("trades", trades);

       for (Map.Entry<String,Object> entry: simulates.entrySet()){
           allresult.put(entry.getKey(),entry.getValue());
       }
        return allresult;
    }

    private List<IndexData> fiterByDateRange(List<IndexData> indexData, String startDate, String endDate) {
        if(StrUtil.isBlankOrUndefined((startDate)) || StrUtil.isBlankOrUndefined(endDate))
        {
            return indexData;
        }

        List<IndexData> list = new ArrayList<>();
        Date sDate = DateUtil.parseDate(startDate);
        Date eDate = DateUtil.parseDate(endDate);
        for (IndexData idata:indexData
             ) {
            Date d = DateUtil.parseDate(idata.getDate());
            if(d.getTime() >= sDate.getTime() && d.getTime() <= eDate.getTime()){
                list.add(idata);
            }
        }

        return list;
    }
}
