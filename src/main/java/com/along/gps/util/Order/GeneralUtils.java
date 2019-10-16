package com.along.gps.util.Order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: why
 * @Data:2019/4/29
 * @Deacription:
 */
public class GeneralUtils {
	public static void main(String[] args) {



	}
    /**
     * 判断对象是否为null , 为null返回true,否则返回false
     * @param obj 被判断的对象
     * @return boolean
     */
    public static boolean isNull(Object obj){
        return (null == obj) ? true : false;
    }

    /**
     * 判断输入的字符串参数是否为空
     * @return boolean 空则返回true,非空则false
     */
    public static boolean isEmpty(String input) {
        return null==input || 0==input.length() || 0==input.replaceAll("\\s", "").length();
    }
   /**
    *首字母转小写
    */
    public static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    /**
     *首字母转大写
     */
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    /**
     * json字符串转 Json对象格式
     * */
    public static JSON getJson(String jsonStr){
       return JSON.parseObject(jsonStr);
    }
	/**
	 * json字符串转 Json数组格式
	 * */
	public static JSON getJsonArr(String jsonStr){
		return JSONObject.parseArray(jsonStr);
	}

    /**
     * 对象转json字符串
     * */
    public static String getJsonStr(Object obj){
        return  JSONObject.toJSONString(obj);
    }

	/**
	 * 将字符串切割成数组，每组param个字符
	 * @param str 字符串
	 * @param param 每组param个字符
	 * @return
	 */
	public static String[] getSplitStr(String str,int param){
		return  str.split("(?<=\\G.{"+param+"})");
	}

	/**
	 * 去除字符串中的空格、回车、换行符、制表符等
	 * @param str
	 * @return
	 */
	public static String replaceSpecialStr(String str) {
		String repl = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			repl = m.replaceAll("");
		}
		return repl;
	}

}
