package com.learn.stock.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.learn.stock.pojo.IndexData;
import com.learn.stock.util.GetWebData;
import com.learn.stock.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@CacheConfig(cacheNames = "index_datas")
public class IndexDataService
{


    @Cacheable(key="'indexData-code-'+ #p0")
    public List<IndexData> get(String code){
        return CollUtil.toList();
    }




    private Map<String, List<IndexData>> indexDatas = new HashMap<>();
    //    private String url = "http://127.0.0.1:8090/indexes/";
    private String url163 = "http://quotes.money.163.com/service/chddata.html?code=%s&start=20020101&end=%s&fields=TCLOSE";//TOPEN";

    @Autowired
    RestTemplate restTemplate;

    public List<IndexData> fresh(String code){
        List<IndexData> idatas = fetch_indexes_from_thrid_part(code);

        indexDatas.put(code,idatas);

        IndexDataService indexDataService = SpringContextUtil.getBean(IndexDataService.class);
        indexDataService.remove(code);
        return indexDataService.store(code);
    }

    @CachePut(key="'indexData-code-'+ #p0")
    public List<IndexData> store(String code) {
        return indexDatas.get(code);
    }

    @CacheEvict(key="'indexData-code-'+ #p0")
    public void remove(String code) {

    }


    private List<IndexData> fetch_indexes_from_thrid_part(String code) {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String strDate = format.format(now);
        String urlGo = String.format(url163,code,strDate);

        List<IndexData> list = GetWebData.getWebData(IndexData.class,urlGo);
//        List<Map> temp = restTemplate.getForObject(url + code + ".json" ,List.class);
//        return map2IndexData(temp);
        if(list.size() == 0){
            System.out.println("error size " + urlGo);
        }
        return list;
    }


    private List<IndexData> map2IndexData(List<Map> temp) {
        List<IndexData> indexDatas = new ArrayList<>();
        for (Map map : temp) {
            String date = map.get("date").toString();
            float closePoint = Convert.toFloat(map.get("closePoint"));
            IndexData indexData = new IndexData();

            indexData.setDate(date);
            indexData.setClosePoint(closePoint);
            indexDatas.add(indexData);
        }

        return indexDatas;
    }
}
