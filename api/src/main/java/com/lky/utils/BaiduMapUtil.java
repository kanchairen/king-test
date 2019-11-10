package com.lky.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 两个经纬度坐标点，计算两点距离
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/21
 */
public class BaiduMapUtil {
    /**
     * 计算两点之间距离
     *
     * @param longitudeA A点经度
     * @param latitudeA  A点纬度
     * @param longitudeB B点经度
     * @param latitudeB  B点纬度
     * @return 千米数
     */
    public static double getDistance(String longitudeA, String latitudeA, String longitudeB, String latitudeB) {
        double lat1 = (Math.PI / 180) * Double.parseDouble(latitudeA);
        double lat2 = (Math.PI / 180) * Double.parseDouble(latitudeB);

        double lon1 = (Math.PI / 180) * Double.parseDouble(longitudeA);
        double lon2 = (Math.PI / 180) * Double.parseDouble(longitudeB);

        //地球半径
        double R = 6371;
        //两点间距离 km，如果想要米的话，结果*1000就可以了
        return Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
    }

    public static void main(String[] args) {
        double distance = BaiduMapUtil.getDistance("116.525736", "39.790888", "107.894467", "26.521027" );
        System.out.println(distance);
    }

    public static String[] findAreaByLngAndLat(String lng, String lat) {

        String add = getAdd(lng, lat);
        JSONObject jsonObject = JSONObject.fromObject(add);
        JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString("addrList"));
        JSONObject j_2 = JSONObject.fromObject(jsonArray.get(0));
        String allAdd = j_2.getString("admName");
        return allAdd.split(",");
    }

    public static String getAdd(String log, String lat) {
        //lat 小  log  大
        //参数解释: 纬度,经度 type 001 (100代表道路，010代表POI，001代表门址，111可以同时显示前三项)
        String urlString = "http://gc.ditu.aliyun.com/regeocoding?l=" + lat + "," + log + "&type=010";
        JSONObject salerJson = JSONObject.fromObject(getUrl(urlString));
        JSONObject jsonData = JSONObject.fromObject(salerJson);
        return jsonData.toString();
    }

    public static String getUrl(String url) {
        String resData;
        StringBuffer s = new StringBuffer();
        BufferedReader bReader;
        try {
            URL urlWeb = new URL(url);
            URLConnection connection = urlWeb.openConnection();
            bReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            while (null != (resData = bReader.readLine())) {
                s.append(resData);
            }
            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }

}
