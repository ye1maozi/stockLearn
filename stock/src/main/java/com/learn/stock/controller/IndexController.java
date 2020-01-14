package com.learn.stock.controller;


import com.learn.stock.Config.IpConfiguration;
import com.learn.stock.pojo.Index;
import com.learn.stock.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @Autowired
    IndexService indexService;
    @Autowired
    IpConfiguration ipConfiguration;
    @GetMapping("codes")
    @CrossOrigin
    public List<Index> get() {
        System.out.println("connect to get codes");
        return indexService.get();
    }


    @GetMapping("/freshCodes")
    public List<Index> fresh() throws Exception {
        return indexService.fresh();
    }
    @GetMapping("/getCodes")
    public List<Index> get2() throws Exception {
        return indexService.get();
    }
    @GetMapping("/removeCodes")
    public String remove() throws Exception {
        indexService.remove();
        return "remove codes successfully";
    }
}
