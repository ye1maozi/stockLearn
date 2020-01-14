package com.learn.stock.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.learn.stock.pojo.Index;
import com.learn.stock.util.SpringContextUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames="indexes")
public class IndexService {
    private List<Index> indexes;

    @Cacheable(key="'all_codes'")
    public List<Index> get(){
        Index index = new Index();
        index.setName("无效指数代码");
        index.setCode("000000");
        return CollUtil.toList(index);
    }



    private String path = "static/codes.json";
    private List<Index> indexList;
    @Autowired
    RestTemplate restTemplate;

    //    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    @Cacheable(key="'all_codes'")
    public List<Index> fetch_indexes_from_third_part(){
//        List<Map> tmp = restTemplate.getForObject(url,List.class);
        List<Map> tmp = new ArrayList<>();
        try {
            //路径
            ClassPathResource classPathResource = new ClassPathResource(path);
            //读取文件信息
            String str = IOUtils.toString(new InputStreamReader(classPathResource.getInputStream(),"UTF-8"));
            //转换为Map对象
            tmp = JSONObject.parseObject(str, List.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return map2Index(tmp);
    }

    private List<Index> map2Index(List<Map> tmp) {
        List<Index> indices = new ArrayList<>();

        for (Map m : tmp){
            String code = m.get("code").toString();
            String name = m.get("name").toString();
            Index index = new Index();
            index.setCode(code);
            index.setName(name);
            indices.add(index);
        }

        return  indices;
    }
    public List<Index> third_part_not_connected(){
        System.out.println("third_part_not_connected()");
        Index index= new Index();
        index.setCode("000000");
        index.setName("无效指数代码");
        return CollectionUtil.toList(index);
    }

    @CacheEvict(allEntries = true)
    public void remove(){

    }

    @Cacheable(key="'all_codes'")
    public List<Index> store(){
        return indexList;
    }

    public List<Index> fresh(){
        indexList = fetch_indexes_from_third_part();
        IndexService indexService = SpringContextUtil.getBean(IndexService.class);
        indexService.remove();
        return indexService.store();
    }
}
