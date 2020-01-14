package com.learn.stock.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.learn.stock.pojo.AnnualProfit;
import com.learn.stock.pojo.IndexData;
import com.learn.stock.pojo.Profit;
import com.learn.stock.pojo.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BackTestService {

    @Autowired
    IndexDataService indexDataService;
    public List<IndexData> listIndexData(String code){
        List<IndexData> result = indexDataService.get(code);
        Collections.reverse(result);

        for (IndexData i:result
             ) {
            i.getDate();
        }
        return result;
    }

    public int getYear(String date){
        String strYear = StrUtil.subBefore(date,"-",false);
        return Convert.toInt(strYear);
    }

    public float getIndexIncome(int year,List<IndexData> indexDatas){
        IndexData first = null;
        IndexData last = null;

        for(IndexData indexData : indexDatas){
            String strDate = indexData.getDate();
            int currYear = getYear(strDate);
            if (currYear == year){
                if (first == null)
                    first = indexData;

                last = indexData;
            }
        }

        return (last.getClosePoint() - first.getClosePoint()) / first.getClosePoint();
    }
    public float getTrendIncome(int year,List<Profit> indexDatas){
        Profit first = null;
        Profit last = null;

        for(Profit indexData : indexDatas){
            String strDate = indexData.getDate();
            int currYear = getYear(strDate);
            if (currYear == year){
                if (first == null)
                    first = indexData;

                last = indexData;
            }else if(currYear > year){
                break;
            }
        }

        return (last.getValue() - first.getValue()) / first.getValue();
    }


    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits){
        List<AnnualProfit> annualProfits = new ArrayList<>();
        String strData = indexDatas.get(0).getDate();
        String strendDate = indexDatas.get(indexDatas.size()-1).getDate();
        Date startDate = DateUtil.parseDate(strData);
        Date endDate = DateUtil.parseDate(strendDate);
        int syear = DateUtil.year(startDate);
        int eyear = DateUtil.year(endDate);

        for (int i = syear; i <= eyear; i++) {
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(i);
            annualProfit.setIndexIncome(getIndexIncome(i,indexDatas));
            annualProfit.setTrendIncome(getTrendIncome(i,profits));
            annualProfits.add(annualProfit);
        }
        return annualProfits;
    }

    public Map<String , Object> simulate(int ma,float sellRate,float buyRate,float serviceCharge,List<IndexData> indexDatas){
        List<Profit> profits = new ArrayList<>();
        List<Trade> trades = new ArrayList<>();

        float initCash = 1000;
        float cash = initCash;
        float share = 0;
        float value = 0;


        int winCount = 0;
        float totalWinRate = 0;
        float avgWinRate = 0;
        float totalLossRate = 0;
        int lossCount = 0;
        float avgLossRate = 0;

        float init = 0;
        if (!indexDatas.isEmpty()){
            init = indexDatas.get(0).getClosePoint();
        }

        for (int i = 0; i < indexDatas.size(); i++) {
            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            float avg = getMA(i , ma,indexDatas);
            float max = getMax(i,ma,indexDatas);

            float increase_rate = closePoint/avg;
            float decrease_rate = closePoint/max;

            if(avg!=0){
                if (increase_rate > buyRate){
                    if (share == 0){
                        share = cash / closePoint;
                        cash = 0;

                        //buy
                        Trade trade = new Trade();
                        trade.setBuyDate(indexData.getDate());
                        trade.setBuyClosePoint(indexData.getClosePoint());
                        trade.setSellDate("n/a");
                        trade.setSellClosePoint(0);
                        trades.add(trade);
                    }
                }else if (decrease_rate < sellRate){
                    if (0 != share){
                        cash = share * closePoint * ( 1 - serviceCharge);
                        share = 0;

                        Trade trade = trades.get(trades.size()-1);
                        trade.setSellClosePoint(indexData.getClosePoint());
                        trade.setSellDate(indexData.getDate());

                        trade.setRate(cash/initCash);


                        if(trade.getSellClosePoint() > trade.getBuyClosePoint()){
                            winCount++;
                            totalWinRate += (trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                        }else{
                            lossCount++;
                            totalLossRate += (trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                        }
                    }
                }
            }

            avgWinRate = totalWinRate / winCount;
            avgLossRate = totalLossRate / lossCount;

            if(share != 0){
                value = closePoint * share;
            }else{
                value = cash;
            }

            float rate = value/initCash;

            Profit profit = new Profit();
            profit.setDate(indexData.getDate());
            profit.setValue(rate * init);

            profits.add(profit);
        }
        Map<String,Object> maps = new HashMap<>();
        maps.put("profits",profits);
        maps.put("trades", trades);

        maps.put("winCount", winCount);
        maps.put("lossCount", lossCount);
        maps.put("avgWinRate", avgWinRate);
        maps.put("avgLossRate", avgLossRate);

        List<AnnualProfit> annualProfits = caculateAnnualProfits(indexDatas, profits);
        maps.put("annualProfits", annualProfits);

        float years = getYear(indexDatas);
        float indexIncomeTotal = (indexDatas.get(indexDatas.size()-1).getClosePoint() - indexDatas.get(0).getClosePoint())/indexDatas.get(0).getClosePoint();
        float indexIncomeAnnual = (float) (Math.pow(1 + indexIncomeTotal , 1/years) - 1);
        float trendIncomeTotal = (profits.get(profits.size()-1).getValue() - profits.get(0).getValue()) / profits.get(0).getValue();
        float trendIncomeAnnual = (float) Math.pow(1+trendIncomeTotal, 1/years) - 1;
        maps.put("years", years);
        maps.put("indexIncomeTotal", indexIncomeTotal);
        maps.put("indexIncomeAnnual", indexIncomeAnnual);
        maps.put("trendIncomeTotal", trendIncomeTotal);
        maps.put("trendIncomeAnnual", trendIncomeAnnual);
        return  maps;
    }

    private float getMax(int i, int day, List<IndexData> indexDatas) {
        int start = i - day - 1;
        start = start< 0 ? 0:start;
        int now  = i-1;

        float max = 0;
        for (int j = start ; j < now ; j++) {
            IndexData indexData = indexDatas.get(j);
            if (indexData.getClosePoint() > max){
                max = indexData.getClosePoint();
            }
        }

        return max;
    }

    private float getMA(int i, int day, List<IndexData> indexDatas) {
        int start = i - day - 1;
        start = start< 0 ? 0:start;
        int now  = i-1;

        float avg = 0;
        float sum = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =indexDatas.get(j);
            sum += bean.getClosePoint();
        }
        avg = sum / (now - start);

        return avg;
    }

    private float getYear(List<IndexData> allIndexDatas){
        float years;
        String ssDate = allIndexDatas.get(0).getDate();
        String seDate = allIndexDatas.get(allIndexDatas.size()-1).getDate();
        Date sData = DateUtil.parseDate(ssDate);
        Date eData = DateUtil.parseDate(seDate);
        long days = DateUtil.between(sData,eData, DateUnit.DAY);
        years = days/365f;
        return years;
    }
}
