package com.learn.stock.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GetWebData {

    private static String url163 = "http://quotes.money.163.com/service/chddata.html?code=%s&start=20020101&end=%s&fields=TCLOSE;";

    private static String[] fieldsData = new String[]{"date","","","closePoint"};
    public static void main(String[] args){

        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String strDate = format.format(now);
        String urlGo = String.format(url163,"1399300",strDate);

//        String str =  getWebData(urlGo);
    }

    public static <T> List<T> getWebData(Class<T> clazz, String httpurl){
        List<T> list = new ArrayList<>();
//        System.out.println(httpurl);
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
//        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpurl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                // 存放数据
//                StringBuffer sbf = new StringBuffer();
                String temp = null;
                boolean isFir = true;
                while ((temp = br.readLine()) != null) {
                    if(isFir){
                        isFir = false;
//                        sbf.append("\r\n");
                    }else{
//                        sbf.append(temp);
                        T indexData = getCSVBean(clazz,temp,fieldsData);
                        list.add(indexData);
//                        sbf.append("\r\n");
//                        System.out.println(indexData.toString());
                    }

                }
//                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }

        return list;
    }


    public GetWebData(){

    }

    public static <T> T getCSVBean( Class<T> clazz, String line,String[] fields){
        String[] row = line.split("\\,", -1); // 分隔字符串（这里用到转义），存储到List<Object>里


        T infos = null; // 创建运行时类的对象
        try {
            infos = clazz.getDeclaredConstructor().newInstance();
            for (int i=0;i<fields.length;i++) {
                String field = fields[i];
                if(field!=""){
                    Field f = clazz.getDeclaredField(field);
                    f.setAccessible(true);
                    Type p = (f.getGenericType());
                    if(p.toString().equals("class java.lang.String")){
                        f.set(infos,row[i]);
                    }else if(p.toString().equals("float")){
                        f.set(infos,Float.valueOf(row[i]));
                    }


                }
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return infos;
    }
}
