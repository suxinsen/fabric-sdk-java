package com.dapp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * @author: SuXinSen
 * @date: 2019/4/15
 * @time: 14:38
 * describe:
 */
public class JSONUtil {

    public static ArrayList<String> jsonToArrayCollection(JSONArray jsonArray) {
        List<String> strings = jsonArray.toJavaList(String.class);
        return (ArrayList)strings;
    }

    public static ArrayList<String> jsonToArrayCollection(JSONObject jsonObject) {
        Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        ArrayList<String> args = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            Object value = next.getValue();
            args.add(String.valueOf(value));
        }
        return args;
    }

    public static void main(String[] args) {
//        String slice = "[\"m\",\"n\",\"64\"]";
//        JSONArray objects = JSON.parseArray(slice);
//        Collection<String> strings = jsonToArrayCollection(objects);
//        Iterator<String> iterator = strings.iterator();
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }
        String mapstr = "{\"1\":\"2\",\"2\":\"m\"}";
        JSONObject jsonObject = JSON.parseObject(mapstr);
        ArrayList<String> strings = jsonToArrayCollection(jsonObject);
        for (String s:strings
             ) {
            System.out.println(s);
        }
    }

}
