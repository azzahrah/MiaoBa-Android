package cn.nodemedia.library.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间转换
 * Created by Bining.
 */
public class TimeUtils {

    private static Date getTimeMillis(String timeMillis) {
        try {
            return getTimeMillis(Long.parseLong(timeMillis));
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static Date getTimeMillis(long timeMillis) {
        try {
            if (timeMillis != 0L) {
                return new Date(timeMillis);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        return null;
    }

    private static String Format(String format, String timeMillis) {
        return Format(format, getTimeMillis(timeMillis));
    }

    private static String Format(String format, long timeMillis) {
        return Format(format, getTimeMillis(timeMillis));
    }

    @SuppressLint({"SimpleDateFormat"})
    private static String Format(String format, Date date) {
        return format != null && date != null ? (new SimpleDateFormat(format)).format(date) : "Unknow";
    }

    public static String ymdhms(String timeMillis) {
        return Format("yyyy-MM-dd HH:mm:ss", timeMillis);
    }

    public static String ymdhms(long timeMillis) {
        return Format("yyyy-MM-dd HH:mm:ss", timeMillis);
    }

    public static String ymdhm(String timeMillis) {
        return Format("yyyy-MM-dd HH:mm", timeMillis);
    }

    public static String ymdhm(long timeMillis) {
        return Format("yyyy-MM-dd HH:mm", timeMillis);
    }

    public static String ymde(String timeMillis) {
        return Format("yyyy-MM-dd E", timeMillis);
    }

    public static String ymde(long timeMillis) {
        return Format("yyyy-MM-dd E", timeMillis);
    }

    public static String ymd(String timeMillis) {
        return Format("yyyy-MM-dd", timeMillis);
    }

    public static String ymd(long timeMillis) {
        return Format("yyyy-MM-dd", timeMillis);
    }

    public static String e(String timeMillis) {
        return Format("E", timeMillis);
    }

    public static String e(long timeMillis) {
        return Format("E", timeMillis);
    }

    public static String hms(String timeMillis) {
        return Format("HH:mm:ss", timeMillis);
    }

    public static String hms(long timeMillis) {
        return Format("HH:mm:ss", timeMillis);
    }

    public static String hm(String timeMillis) {
        return Format("HH:mm", timeMillis);
    }

    public static String hm(long timeMillis) {
        return Format("HH:mm", timeMillis);
    }

    public static String transformDate(Object timeMillis) {
        try {
            if (timeMillis != null) {
                long time;
                if (timeMillis instanceof String) {
                    time = Long.parseLong(timeMillis.toString());
                } else {
                    time = (long) timeMillis;
                }

                if (time == 0L) {
                    return "UnKnow";
                }

                Calendar currentCalendar = Calendar.getInstance();
                currentCalendar.setTimeInMillis(System.currentTimeMillis());
                int currentYear = currentCalendar.get(Calendar.YEAR);
                int currentMonth = currentCalendar.get(Calendar.MONTH);
                int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
                Calendar targetCalendar = Calendar.getInstance();
                targetCalendar.setTimeInMillis(time);
                int targetYear = targetCalendar.get(Calendar.YEAR);
                int targetMonth = targetCalendar.get(Calendar.MONTH);
                int targetDay = targetCalendar.get(Calendar.DAY_OF_MONTH);
                String format;
                if (currentDay < 3 && currentMonth - targetMonth == 1 && targetDay > 29) {
                    format = "E HH:mm";
                } else if (currentYear == targetYear && currentMonth == targetMonth) {
                    switch (currentDay - targetDay) {
                        case 0:
                            format = "HH:mm";
                            break;
                        case 1:
                            format = "E HH:mm";
                            break;
                        case 2:
                            format = "E HH:mm";
                            break;
                        default:
                            format = "dd日 HH:mm";
                    }
                } else {
                    format = "yyyy年MM月dd日 HH:mm";
                }
                return Format(format, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public static String getDateDesc(Date time) {
        if (time == null)
            return "";
        String timeContent;
        Long ss = System.currentTimeMillis() - time.getTime();
        Long minute = ss / 60000;
        if (minute < 1)
            minute = 1L;
        if (minute >= 60) {
            Long hour = minute / 60;
            if (hour >= 24) {
                if (hour > 720)
                    timeContent = "1月前";
                else if (hour > 168 && hour <= 720)
                    timeContent = (hour / 168) + "周前";
                else
                    timeContent = (hour / 24) + "天前";
            } else {
                timeContent = hour + "小时前";
            }
        } else {
            timeContent = minute + "分钟前";
        }
        return timeContent;
    }
}
