package com.luying.weixinSystem.util;

 
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.TimeUtil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间 戳
 **/
public class TimeUtils {

	public static void main(String [] asg) throws Exception{
		
	 
//	System.out.println( date2TimeStamp("2019-02-04 00:00" ));
//	System.out.println( date2TimeStamp("2019-03-04 23:59" ));
//	compare_date("2019-04-04 00:00","2019-03-04 23:59");
//	System.out.println(timeStamp2Date("1541261100"));

      //  System.out.println(date2String(new Date()));

//        System.out.println(beforeDefinedTime("09:00:00",new Date()));
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//
//		System.out.println(simpleDateFormat.parse("08:00:11").getTime());
//        System.out.println(simpleDateFormat.parse("09:00:01").getTime());
//		System.out.println(simpleDateFormat.parse("09:01:03").getTime());
//		System.out.println(simpleDateFormat.parse("21:07:33").getTime());
//		System.out.println(simpleDateFormat.parse("20:03:15").getTime());
//		System.out.println(((simpleDateFormat.parse("21:07:33").getTime() - simpleDateFormat.parse("20:03:15").getTime()) <= 3600000));
//        isweekend(2019,05,8);



	}
	/** 日期格式 **/
	public interface DATE_PATTERN {
		String HHMMSS = "HHmmss";
        String HH_MM_SS = "HH:mm:ss";
		String YYYYMMDD = "yyyyMMdd";
		String YYYY_MM_DD = "yyyy-MM-dd";
		String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
		String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
		String YYYYMMDDHHMM  = "yyyyMMddHHmm";
		String YYYY_MM_DD_HH_MM  = "yyyy-MM-dd HH:mm";
	}
	 public static int compare_date(String DATE1, String DATE2) {
	        DateFormat df = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM);
	        try {
	            Date dt1 = df.parse(DATE1);
	            Date dt2 = df.parse(DATE2);
	            if (dt1.getTime() > dt2.getTime()) {
	                System.out.println("dt1 大");
	                return 1;
	            } else if (dt1.getTime() < dt2.getTime()) {
	                System.out.println("dt1 小");
	                return -1;
	            } else {
	                return 0;
	            }
	        } catch (Exception exception) {
	            exception.printStackTrace();
	        }
	        return 0;
	    }
	 /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param
     *
     * @return
     */
    public static long date2TimeStamp(String dateStr ) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD);
            return Long.parseLong(String.valueOf(sdf.parse(dateStr).getTime() / 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    } 
    public static String timeStamp2Date(Object timestampString){  
        Long timestamp = Long.parseLong(timestampString.toString())*1000;  
        String date = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS).format(new Date(timestamp));
        return date;  
    }
 
	public static String date2Stringymd(Date date ) {
	 
		return new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD ).format(date);
	}
    public static String date2String(Date date ) {

        return new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS ).format(date);
    }

    public static boolean beforeDefinedTime(String definedtime, Date date ) {

        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN.HH_MM_SS);
        Date ten = null;
        Date now = null;
        try {
            ten = format.parse(definedtime);
            now = format.parse(format.format(date)) ;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return ten.before(now);
    }
	public static Date string2Date(String date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD );
		try {
			return format.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
        /**
         *  某年某月 有几天
         * */
        public static int daysofmonth(int year,int month){
            Calendar c = Calendar.getInstance();
            c.set(year, month, 0); //输入类型为int类型
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
           // System.out.println(year + "年" + month + "月有" + dayOfMonth+"天" );
             return  dayOfMonth;
        }
        /**
         * 是否是 周 6,7
         * */
    public static boolean isweekend(int year,int month,int day){
        Calendar c = Calendar.getInstance();
        c.setTime(string2Date(year+ "-"+ month + "-" +day));
        //System.out.println("today ==" + c.get(Calendar.DAY_OF_WEEK) + "===Calendar.SUNDAY==" + Calendar.SUNDAY + "===Calendar.SATURDAY==" + Calendar.SATURDAY);
          if(  c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                    c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
              return true;
          }
            return false;
        }

    public static String formatDateString(int year ,int month,int day){
       return date2Stringymd(string2Date(year + "-" + month + "-" + day));
    }

    public static String formatyyyyMMdd(int year ,int month,int day){
        SimpleDateFormat dft2 = new SimpleDateFormat(DATE_PATTERN.YYYYMMDD);
        return dft2.format(string2Date(year + "-" + month + "-" + day));
    }
}
