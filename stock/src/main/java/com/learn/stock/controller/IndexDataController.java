package com.learn.stock.controller;


import com.learn.stock.pojo.IndexData;
import com.learn.stock.service.IndexDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexDataController {

    @Autowired
    IndexDataService indexDataService;

    @GetMapping("/data/{code}")
    public List<IndexData> get(@PathVariable("code") String code) throws Exception {
        return indexDataService.get(code);
    }



    @GetMapping("/freshIndex/{code}")
    public String fresh(@PathVariable("code") String code) throws Exception {
        indexDataService.fresh(code);
        return "fresh success " + code;
    }

    @GetMapping("/getIndexData/{code}")
    public List<IndexData> getIndexData(@PathVariable("code") String code) throws Exception {
        return indexDataService.get(code);
    }

    @GetMapping("/removeIndexData/{code}")
    public String remove(@PathVariable("code") String code) throws Exception {
        indexDataService.remove(code);
        return "remove index data successfully";
    }

}
