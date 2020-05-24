package com.example.countstep;

import java.util.Calendar;
import java.util.TimeZone;

public class DataString {
        private static String mYear;
        private static String mMonth;
        private static String mDay;
        private static String mWay;

        public static String StringData(){
            final Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
            mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
            mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
            int i = c.get(Calendar.DAY_OF_WEEK);
            switch (i) {
                case 1:
                    mWay = "天";
                    break;
                case 2:
                    mWay = "一";
                    break;
                case 3:
                    mWay = "二";
                    break;
                case 4:
                    mWay = "三";
                    break;
                case 5:
                    mWay = "四";
                    break;
                case 6:
                    mWay = "五";
                    break;
                case 7:
                    mWay = "六";
                    break;
            }
//            return "星期"+mWay;
            return mYear + "年" + mMonth + "月" + mDay+"日" + "   星期"+mWay;
        }

        public static String WeekData(){
            final Calendar c = Calendar.getInstance();
            int i = c.get(Calendar.DAY_OF_WEEK);
            switch (i) {
                case 1:
                    mWay = "天";
                    break;
                case 2:
                    mWay = "一";
                    break;
                case 3:
                    mWay = "二";
                    break;
                case 4:
                    mWay = "三";
                    break;
                case 5:
                    mWay = "四";
                    break;
                case 6:
                    mWay = "五";
                    break;
                case 7:
                    mWay = "六";
                    break;
            }
            return "星期"+mWay;
        }

    }
