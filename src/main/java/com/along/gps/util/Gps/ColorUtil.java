package com.along.gps.util.Gps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.along.gps.util.DataUtil.StringToLong;

public class ColorUtil {
	public static Map<Integer,String> COLORMAP = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		/*new  ColorUtil();
		String equipCard="114",color="";
		if(equipCard.length()>2) {
			int i = Integer.parseInt(equipCard.substring(equipCard.length() - 1, equipCard.length())) % 21;
			color = ColorUtil.COLORMAP.get(i);
		}else{
			color = ColorUtil.COLORMAP.get(Integer.parseInt(equipCard)%21);
		}
		System.out.println(color);*/
		System.out.println((StringToLong("2019-11-21 18:03:53", "yyyy-MM-dd HH:mm:ss")));
		System.out.println((StringToLong("2019-11-21 18:03:53", "yyyy-MM-dd HH:mm:ss") + 1000 * 60 * 5));
	}
	public ColorUtil() {
		COLORMAP.put(1,"#FFDAB9");//桃色
		COLORMAP.put(2,"#FFD700");//
		COLORMAP.put(3,"#FFC0CB");//
		COLORMAP.put(4,"#FFA500");//
		COLORMAP.put(5,"#FF69B4");//
		COLORMAP.put(6,"#FF4500");//
		COLORMAP.put(7,"#FF00FF");//
		COLORMAP.put(8,"#B0E0E6");//
		COLORMAP.put(9,"#B0C4DE");//
		COLORMAP.put(10,"#ADFF2F");//
		COLORMAP.put(11,"#A9A9A9");//
		COLORMAP.put(12,"#708090");//
		COLORMAP.put(13,"#2F4F4F");//
		COLORMAP.put(14,"#1E90FF");//
		COLORMAP.put(15,"#00FFFF");//
		COLORMAP.put(16,"#00FF7F");//
		COLORMAP.put(17,"#0000FF");//
		COLORMAP.put(18,"#00008B");//
		COLORMAP.put(19,"#000000");//
		COLORMAP.put(20,"#800000");//
		COLORMAP.put(21,"#E9967A");//
	}
}
