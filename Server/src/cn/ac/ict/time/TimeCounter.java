package cn.ac.ict.time;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class TimeCounter {
//	private static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	private static long startmili = System.currentTimeMillis();
	private static HashMap<Object,Long> timeStamp = new HashMap<Object,Long>();
	private TimeCounter(){}

    /*
     * 毫秒转化时分秒毫秒
     */
    private static String formatTime(Long ms) {
    		
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
        
        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }
        if(milliSecond >= 0) {
            sb.append(milliSecond+"毫秒");
        }
        return sb.toString();
    }
	
	public static long getCurrentTimeMillis(){
		return System.currentTimeMillis();
	}
	
	public static void updateTimeStamp(Object obj){
		timeStamp.put(obj, System.currentTimeMillis());
	}
	
	public static String getTimeDiff(Object obj){
		Long diff = timeStamp.get(obj);
		if( diff == null ) diff = startmili;
		
		diff = System.currentTimeMillis() - diff;
		return formatTime(diff);
	}
	
	public static void removeTimeStamp(Object obj){
		timeStamp.remove(obj);
	}
}
